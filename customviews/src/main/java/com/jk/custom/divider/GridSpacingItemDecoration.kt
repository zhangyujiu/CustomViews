package com.jk.custom.divider

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class GridSpacingItemDecoration(private val mSpanCount: Int,
                                private val mSpacing: Int,
                                private val mIncludeEdge: Boolean) : RecyclerView.ItemDecoration() {

    private var mTopDecoration = 0

    constructor(spanCount: Int,
                spacing: Int,
                includeEdge: Boolean,
                topDecoration: Int) : this(spanCount, spacing, includeEdge) {
        this.mTopDecoration = topDecoration
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % mSpanCount

        if (mIncludeEdge) {
            outRect.left = mSpacing - column * mSpacing / mSpanCount

            outRect.right = (column + 1) * mSpacing / mSpanCount

            if (position < mSpanCount) {
                outRect.top = mSpacing
            }

            outRect.bottom = mSpacing
        } else {
            outRect.left = column * mSpacing / mSpanCount
            outRect.right = mSpacing - (column + 1) * mSpacing / mSpanCount
            if (position >= mSpanCount) {
                outRect.top = mSpacing
            }
        }

        if (mTopDecoration != 0 && position < mSpanCount) {
            outRect.top = outRect.top + mTopDecoration
        }
    }
}