package com.senseluxury.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.senseluxury.common.Constants;
import com.senseluxury.ui.main.MainActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by Jay_zhao
 * on 2017/1/16.
 */

public class MyScrollerViewGroup extends ViewGroup {
    private float mXDown, mXMove, mXLast; //  记录 点击是坐标、移动时坐标、上一次移动坐标
    private int mTouchSlop; //   最小滑动距离 大于则认为是滑动
    private Scroller mScroller;
    private int count = 0;
    private int LEFT;
    private boolean mGone = false;

    public MyScrollerViewGroup(Context context) {
        super(context);
    }

    public MyScrollerViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        //初始化Scroller 并获取mTouchSlop的值
        mScroller = new Scroller(context);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //测量子view 的大小
        int childCount = getChildCount();
        View childView = null;
        for (int i = 0; i < childCount; i++) {
            childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (count == 0) {
            LEFT = l;
        }
        count++;
        int childCount = getChildCount();
        View childView = null;
        for (int i = 0; i < childCount; i++) {
            childView = getChildAt(i);
            childView.layout(0, i * childView.getMeasuredHeight(), childView.getMeasuredWidth(), (i + 1) * childView.getMeasuredHeight());
        }
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mXDown = ev.getRawX();
                mXLast = mXDown;
//                return true ;
                break;
            case MotionEvent.ACTION_MOVE:
                mXMove = ev.getRawX();
                float diff = Math.abs(mXMove - mXLast);
                mXLast = mXMove;
                // 当滑动大于mTouchSlop 判定为滑动 拦截事件
                return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        getParent().requestDisallowInterceptTouchEvent(true);
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mXMove = event.getRawX();
                int scrolledX = (int) (mXLast - mXMove);
                mXLast = mXMove;
                if (mXMove > mXDown) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    scrollBy(scrolledX, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
                int diff = (int) (mXMove - mXDown);
                if (diff > getMeasuredWidth() / 3) {
                    mScroller.startScroll(getScrollX(), 0, -getMeasuredWidth(), 0, 500);
                    mGone = true;
                    invalidate();
                } else {
                    mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0, 500);
                    invalidate();
                }
                break;
        }
        return true;
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }else if(mGone){
            this.setVisibility(GONE);
            MobclickAgent.onEvent(getContext(), Constants.UMENG_EVENT_MessageBoxRightSlide);
        }
    }
}
