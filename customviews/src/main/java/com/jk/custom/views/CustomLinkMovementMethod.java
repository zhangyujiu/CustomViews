package com.jk.custom.views;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

public class CustomLinkMovementMethod extends LinkMovementMethod {

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        int action = event.getAction();
        boolean isTouchEventConsumed = false;

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] links = buffer.getSpans(off, off, ClickableSpan.class);

            if (links.length != 0) {
                ClickableSpan link = links[0];
                if (action == MotionEvent.ACTION_UP) {
                    link.onClick(widget);
                } else if (action == MotionEvent.ACTION_DOWN) {
                    // 下面这行代码会造成触摸 clickbleSpan 时 RecyclerView 的 item 高度变更  : (
                    // 看起来像是跟文字选择相关的东西，反正我们的 clickSpan 也不让选，直接屏蔽掉了
//                    Selection.setSelection(buffer,
//                            buffer.getSpanStart(link),
//                            buffer.getSpanEnd(link));
                }
                isTouchEventConsumed = true;
            } else {
                Selection.removeSelection(buffer);
            }
        }

        //解决点击事件冲突问题
        if (!isTouchEventConsumed && event.getAction() == MotionEvent.ACTION_UP) {
            ViewParent parent = iterateViewParentForClicking(widget.getParent());//处理widget的父控件点击事件
            if (parent instanceof ViewGroup) {
                return ((ViewGroup) parent).performClick();
            }
        }
        return false;
    }
    
    
    private ViewParent iterateViewParentForClicking(ViewParent parent) {
        if (parent instanceof ViewGroup) {
            if (((ViewGroup) parent).hasOnClickListeners()) {
                return parent;
            } else {
                return iterateViewParentForClicking(parent.getParent());
            }
        }
        return null;
    }

    public static CustomLinkMovementMethod getInstance() {
        if (sInstance == null) sInstance = new CustomLinkMovementMethod();

        return sInstance;
    }

    private static CustomLinkMovementMethod sInstance;

}