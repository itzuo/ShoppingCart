package com.zxj.shoppingcart.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.zxj.shoppingcart.R;

/**
 * @描述: 侧滑删除组件
 * @项目名: ShoppingCart
 * @包名: com.zxj.shoppingcart.widget
 * @类名: SlideView
 * @作者: zuojie
 * @创建时间: 16-8-22 下午2:14
 */
public class SlideView extends LinearLayout {
    public static final String TAG = "SlideView";

    private static final int TAN = 2;
    private int mHolderWidth = 90;
    private float mLastX = 0;
    private float mLastY = 0;
    private LinearLayout mViewContent;
    private Scroller mScroller;
    private Context mContext;
    private Resources mResources;

    public TextView getBack() {
        return back;
    }

    private TextView back;

    public SlideView(Context context, Resources resources, View content) {
        super(context);
        initView(context, resources, content);
    }


    public SlideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        initView(context, context.getResources(), null);
    }

    public SlideView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        initView(context, context.getResources(), null);
    }
    private void initView(Context context, Resources resources, View content) {
        // TODO Auto-generated method stub
        setOrientation(LinearLayout.HORIZONTAL);
        this.mContext = context;
        this.mResources = resources;
        Log.i(TAG, "---1---");
        mScroller = new Scroller(context);
        Log.i(TAG, "---2---");
        View view = LayoutInflater.from(context).inflate(resources.getLayout(R.layout.slide_view_merge), this);
        Log.i(TAG, "---3---");
//		view.findViewById(R.id.holder).setBackground(resources.getDrawable(R.drawable.selector_slider_holder));
        Log.i(TAG, "---4---");
//		shenhe = (TextView) view.findViewById(R.id.shenhe);
//		back = (TextView) findViewById(R.id.back);
//		back.setCompoundDrawablesWithIntrinsicBounds(null, resources.getDrawable(R.drawable.back), null, null);
//		back.setOnClickListener(this);
//		shenhe.setOnClickListener(this);
        mViewContent = (LinearLayout) view.findViewById(R.id.view_content);
//		mHolderWidth = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mHolderWidth, getResources().getDisplayMetrics()));
        mHolderWidth = getResources().getDimensionPixelSize(R.dimen.width_);
        if(content!=null){
            mViewContent.addView(content);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                float deltaX = x - mLastX;
                float deltaY = y - mLastY;
                mLastX = x;
                mLastY = y;
                if(Math.abs(deltaX)<Math.abs(deltaY)*TAN){
                    break;
                }
                if(deltaX != 0){
                    float newScrollX = getScrollX() - deltaX;
                    if(newScrollX<0){
                        newScrollX = 0;
                    }else if(newScrollX > mHolderWidth){
                        newScrollX = mHolderWidth;
                    }
                    this.scrollTo((int)newScrollX, 0);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void smoothScrollTo(int destX, int destY) {
        int scrollX = getScrollX();
        int delta = destX - scrollX;
        mScroller.startScroll(scrollX, 0, delta, 0, Math.abs(delta) * 3);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }
    /**
     * 获取view是需要重置缓存状态
     */
    public void shrink() {
        int offset = getScrollX();
        if (offset == 0) {
            return;
        }
        scrollTo(0, 0);
    }

    public void setContentView(View view) {
        if (mViewContent != null) {
            mViewContent.addView(view);
        }
    }

    public void reset() {
        int offset = getScrollX();
        if (offset == 0) {
            return;
        }
        smoothScrollTo(0, 0);
    }

    public void adjust(boolean left) {
        int offset = getScrollX();
        if (offset == 0) {
            return;
        }
        if (offset < 20) {
            this.smoothScrollTo(0, 0);
        } else if (offset < mHolderWidth - 20) {
            if (left) {
                this.smoothScrollTo(mHolderWidth, 0);
            } else {
                this.smoothScrollTo(0, 0);
            }
        } else {
            this.smoothScrollTo(mHolderWidth, 0);
        }
    }
}
