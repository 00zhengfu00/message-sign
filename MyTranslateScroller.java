package com.senseluxury.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * Created by Jay_zhao
 * on 2017/1/19.
 */

public class MyTranslateScroller extends LinearLayout {
    private Scroller mScroller;
    private int mCount = 0;

    public MyTranslateScroller(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
    }

    public void startTranslateChild(int count) {
        mCount = count;
        mScroller.startScroll(0, getScrollY(), 0, do2px(55), 1000);
        invalidate();
    }

    private int do2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        } else {
            for (int i = 1; i < mCount; i++) {
                mScroller.startScroll(0, getScrollY(), 0, do2px(55), 1000);
                invalidate();
                mCount--;
            }
        }
    }
}
