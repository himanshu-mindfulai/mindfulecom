package com.mindfulai.Utils;

import android.content.Context;
import android.util.AttributeSet;
import android.content.Context;
import android.util.AttributeSet;

import androidx.core.widget.NestedScrollView;

import androidx.core.widget.NestedScrollView;

public class NestedScrollViewExt  extends NestedScrollView {
    private ScrollViewListener scrollViewListener = null;
    public NestedScrollViewExt(Context context) {
        super(context);
    }

    public NestedScrollViewExt(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public NestedScrollViewExt(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
        }
    }
}