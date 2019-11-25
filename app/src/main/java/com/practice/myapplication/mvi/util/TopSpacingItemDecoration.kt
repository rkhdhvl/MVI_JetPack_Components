package com.practice.myapplication.mvi.util

import androidx.recyclerview.widget.RecyclerView
import android.graphics.Rect
import android.view.View


class TopSpacingItemDecoration(private val padding: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        // this will give some spacing to the upper bound of the card view
        outRect.top = padding
    }
}