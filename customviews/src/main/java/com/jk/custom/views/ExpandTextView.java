package com.jk.custom.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.jk.custom.R;


public class ExpandTextView extends AppCompatTextView {

    private CharSequence mSnapshotText;

    private String mEndText = "...";
    private CharSequence mShrankText = "";
    private String mExpandText = mEndText + "全文";
    private CharSequence mExpandedText = "";
    private boolean mUseGradientAlphaEndText = false;
    private boolean mShowExpandTextRegardlessOfMaxLines = false; // 不论文字超过 maxLines 都显示"...展开"文字

    private int mMaxLines = 3; // 由于sdk版本限制(getMaxLines) 这里设置默认值

    private boolean mInitLayout = false;
    private boolean mIsExpanded = false; // 位于 recyclerView 时需要自行在外层管理是否已展开

    private ExpandCallback mExpandCallback;
    private SelfCalculateMaxLinesCallback mMaxLinesCalculatedCallback;

    private Rect mLastVisibleLineRect;
    private Rect mLastActualLineRect;

    private static int DEFAULT_ADDITIONAL_END_TEXT_COUNT = 2;

    public ExpandTextView(Context context) {
        super(context);
    }

    public ExpandTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mMaxLines = getMaxLines();
        }

        mLastVisibleLineRect = new Rect();
        mLastActualLineRect = new Rect();

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ExpandTextView);
        mUseGradientAlphaEndText = ta.getBoolean(R.styleable.ExpandTextView_useGradientAlphaEndText, false);
        mEndText = ta.getString(R.styleable.ExpandTextView_endText) == null ? mEndText : ta.getString(R.styleable.ExpandTextView_endText);
        mExpandText = ta.getString(R.styleable.ExpandTextView_expandText) == null ? mExpandText : ta.getString(R.styleable.ExpandTextView_expandText);
        ta.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mShowExpandTextRegardlessOfMaxLines && !mIsExpanded) {
            updateMaxLines();
        }
        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() - getExtraBottomPadding());
    }

    private void updateMaxLines() {
        mMaxLines = getLineCount() - 1;
        setMaxLines(mMaxLines);
        mMaxLinesCalculatedCallback.onMaxLinesCalculated(mMaxLines);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mMaxLines > 0
                && ((mShowExpandTextRegardlessOfMaxLines && !mIsExpanded) || (mInitLayout && !mIsExpanded && getLineCount() > mMaxLines))) {
            mSnapshotText = getText();
            mInitLayout = false;
            showExpandButton();
        }
    }

    public void setExpandText(String text) {
        this.mExpandText = text;
    }

    public void setExpandCallback(ExpandCallback callback) {
        this.mExpandCallback = callback;
    }

    /**
     * 适用于不使用 maxLines 而是整段收起时的文字来确定“...更多”的位置的样式
     * @param shrankText 收起时的文字 （“...更多”跟在 shrankText 后）
     * @param expandedText 展开时的文字
     */
    public void setShrankTextAndExpandedText(CharSequence shrankText, CharSequence expandedText) {
        mShrankText = shrankText;
        mExpandedText = expandedText;
        mShowExpandTextRegardlessOfMaxLines = !TextUtils.isEmpty(shrankText);

        if (!mIsExpanded && mShowExpandTextRegardlessOfMaxLines) {
            setText(mShrankText);
        } else {
            setText(mExpandedText);
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        mInitLayout = true;
        super.setText(text, type);
    }

    private void showExpandButton() {
        String finalEndText = "";
        TextPaint paint = getPaint();

        Layout layout = getLayout();
        int start = layout.getLineStart(0);
        int lastLineEnd = layout.getLineEnd(mMaxLines - 1);
        int lastLineStart = layout.getLineStart(mMaxLines - 1);
        float lastLineRight = layout.getLineRight(mMaxLines - 1);

        int viewWidth = getWidth() - getPaddingRight() - getPaddingLeft();
        int additionalEndTextCount = 0;

        float expandTextWidth;
        if (mUseGradientAlphaEndText) {
            additionalEndTextCount = DEFAULT_ADDITIONAL_END_TEXT_COUNT;
            expandTextWidth = paint.measureText(mEndText + mExpandText);
        } else {
            expandTextWidth = paint.measureText(mExpandText);
        }

        CharSequence content = mSnapshotText.subSequence(start, lastLineEnd);

        if (viewWidth - lastLineRight > expandTextWidth) {
            if (mUseGradientAlphaEndText) {
                finalEndText = content.toString()
                        .substring(content.length() - additionalEndTextCount, content.length()) + mEndText;
                finalEndText = finalEndText.replace("\n", "");

                content = content.subSequence(0, content.length() - additionalEndTextCount) + finalEndText + mExpandText;
            } else {
                content = content.toString().trim() + mExpandText;
            }
        } else {
            CharSequence lastLineText = mSnapshotText.subSequence(lastLineStart, lastLineEnd);
            CharSequence subSequence;
            float subSequenceWidth;
            for (int i = lastLineText.length() - 1; i > 0; i--) {
                if (mUseGradientAlphaEndText) {
                    subSequence = lastLineText.subSequence(0, i - additionalEndTextCount);
                    subSequenceWidth = paint.measureText(subSequence.toString());

                    finalEndText = lastLineText.subSequence(i - additionalEndTextCount, i) + mEndText;
                    expandTextWidth = paint.measureText(finalEndText + mExpandText);

                    if (viewWidth - subSequenceWidth > expandTextWidth) {
                        finalEndText = finalEndText.replace("\n", "");
                        content = mSnapshotText.subSequence(start, lastLineStart + i - additionalEndTextCount) + finalEndText + mExpandText;
                        break;
                    }
                } else {
                    subSequence = lastLineText.subSequence(0, i);
                    subSequenceWidth = paint.measureText(subSequence.toString());

                    if (viewWidth - subSequenceWidth > expandTextWidth) {
                        content = mSnapshotText.subSequence(start, lastLineStart + i) + mExpandText;
                        break;
                    }
                }
            }
        }

        SpannableStringBuilder msp = new SpannableStringBuilder(mSnapshotText);
        int length = msp.length();
        int expandTextStartPosition;
        expandTextStartPosition = content.length() - finalEndText.length() - mExpandText.length();
        expandTextStartPosition = Math.max(expandTextStartPosition, 0);

        // 避免越界
        if (expandTextStartPosition >= length) return;

        msp.replace(expandTextStartPosition, length, finalEndText + mExpandText);

        if (expandTextStartPosition + mEndText.length() >= msp.length()) return;

        msp.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(getContext(), R.color.theme_font));
                ds.setUnderlineText(false);
            }

            @Override
            public void onClick(@NonNull View widget) {
                mIsExpanded = true;
                setMaxLines(Integer.MAX_VALUE);
                if (mShowExpandTextRegardlessOfMaxLines) {
                    setText(mExpandedText);
                } else {
                    setText(mSnapshotText);
                }

                if (mExpandCallback != null) {
                    mExpandCallback.onExpand();
                }
            }
        }, expandTextStartPosition + mEndText.length(), msp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        int paintColor = 0;

        // 找到第一个位置与 endTextStartPosition 贴合的 ForegroundSpan / ClickableSpan ,
        // 获取颜色赋值给 GradientAlphaTextSpan
        Object[] objects = msp.getSpans(0, expandTextStartPosition, Object.class);
        if (objects.length != 0) {
            for (Object span : objects) {
                int startPosition = msp.getSpanStart(span);
                int endPosition = msp.getSpanEnd(span);
                if (expandTextStartPosition >= startPosition && expandTextStartPosition <= endPosition) {
                    if (span instanceof ForegroundColorSpan) {
                        paintColor = ((ForegroundColorSpan) span).getForegroundColor();
                        break;
                    } else if (span instanceof ClickableSpan) {
                        paintColor = getResources().getColor(R.color.theme_font);
                        break;
                    }
                }
            }
        }
        msp.setSpan(new GradientAlphaTextSpan(paintColor), expandTextStartPosition, expandTextStartPosition + finalEndText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        setText(msp);
        setMovementMethod(CustomLinkMovementMethod.getInstance());
    }

    /**
     * 获取 maxLines + lineSpacingExtra + movementMethod 一起使用时产生的大小与 lineSpacingExtra 一样的底部空间
     */
    private int getExtraBottomPadding() {
        int result = 0;
        // 界面上显示的最后一行的 index
        int lastVisibleLineIndex = Math.min(getMaxLines(), getLineCount()) - 1;
        // 获取实际文字的最后一行的 index
        int lastActualLineIndex = getLineCount() - 1;

        if (lastVisibleLineIndex >= 0) {
            Layout layout = getLayout();
            int lastVisibleLineBaseline = getLineBounds(lastVisibleLineIndex, mLastVisibleLineRect);
            getLineBounds(lastActualLineIndex, mLastActualLineRect);
            int heightBetweenLastVisibleLineRectAndLastActualLineRect = (mLastActualLineRect.bottom - mLastVisibleLineRect.bottom);

            if (getMeasuredHeight() == getLayout().getHeight() - heightBetweenLastVisibleLineRectAndLastActualLineRect) {
                result = mLastVisibleLineRect.bottom - (lastVisibleLineBaseline + layout.getPaint()
                        .getFontMetricsInt().descent + getPaddingBottom());
                if (getLineSpacingExtra() > result) {
                    result = 0;
                } else {
                    result = (int) getLineSpacingExtra();
                }
            }
        }
        return result;
    }

    /**
     * 此方法仅更改标记，不做实际展开的操作
     */
    public void setIsExpanded(boolean isExpanded) {
        mIsExpanded = isExpanded;
    }

    public void setExpandMaxLines(int maxLines) {
        mMaxLines = maxLines;
        setMaxLines(maxLines);
    }

    public void setSelfCalculateMaxLinesCallback(SelfCalculateMaxLinesCallback callback) {
        mMaxLinesCalculatedCallback = callback;
    }

    public interface ExpandCallback {
        void onExpand();
    }

    public interface SelfCalculateMaxLinesCallback {
        void onMaxLinesCalculated(int maxLines);
    }

}
