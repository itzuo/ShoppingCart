package com.zxj.shoppingcart.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.zxj.shoppingcart.R;

/**
 * @描述: 自定义的ExpandableListView，处理滑动冲突
 * @项目名: ShoppingCart
 * @包名: com.zxj.shoppingcart.widget
 * @类名: SuperExpandableListView
 * @作者: zuojie
 * @创建时间: 16-8-22 下午2:32
 */
public class SuperExpandableListView extends ExpandableListView {

    private static final String TAG = SuperExpandableListView.class.getCanonicalName();
    /**
     * 用户滑动最小距离
     */
    private int touchSlop;
    /**
     * 是否相应滑动
     */
    private boolean isSliding;
    /**
     * 手指按下时x坐标
     */
    private int xDown;
    /**
     * 手指按下时的y坐标
     */
    private int yDown;
    /**
     * 手指移动时的x坐标
     */
    private int xMove;
    /**
     * 手指移动时的y坐标
     */
    private int yMove;

    boolean isChild;

    private LayoutInflater mInflater;

    private ViewGroup itemLayout;


    private SlideView mFocusedItemView;


    /**
     * 为删除按钮提供一个回调接口
     */
    private ButtonClickListener mListener;
    /**
     * 当前手指触摸的View
     */

    /**
     * 当前手指触摸的位置
     */
    private int mCurrentViewPos = -1;


    public SuperExpandableListView(Context context, AttributeSet attrs,
                                   int defStyle) {
        super(context, attrs, defStyle);
        initData(context);
        // TODO Auto-generated constructor stub
    }

    public SuperExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        initData(context);
    }

    public SuperExpandableListView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        initData(context);
    }

    private void initData(Context context) {
        mInflater = LayoutInflater.from(context);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
//			isSliding = false;
//			if(isSliding){
//				a
//			}
                xDown = x;
                yDown = y;
                // 获得当前手指按下时的item的位置
                int position = pointToPosition(xDown, yDown);
                if (mCurrentViewPos != position || isSliding) {
                    mCurrentViewPos = position;
                    isSliding = false;
                    if (mFocusedItemView != null) {
                        mFocusedItemView.reset();
                    }
                }
                // 获得当前手指按下时的item
                itemLayout = (ViewGroup) getChildAt(mCurrentViewPos - getFirstVisiblePosition());
                if (itemLayout != null) {
                    int id = itemLayout.getId();
                    if (id == R.id.SwipeMenuExpandableListView) {
                        isChild = false;
                    } else {
                        isChild = true;
                    }
                /*if(itemLayout instanceof RelativeLayout){
                    isChild = false;
                }else{
                    isChild = true;
                }*/
                }
                break;
            case MotionEvent.ACTION_MOVE:
                xMove = x;
                yMove = y;
                int dx = xMove - xDown;
                int dy = yMove - yDown;
                /**
                 * 判断是否是从右到左的滑动
                 */
                if (xMove < xDown && Math.abs(dx) > touchSlop && Math.abs(dy) < touchSlop && isChild) {
                    // Log.e(TAG, "touchslop = " + touchSlop + " , dx = " + dx + " , dy = " + dy);
                    isSliding = true;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        int x = (int) ev.getX();
        int y = (int) ev.getY();
        /**
         * 如果是从右到左的滑动才相应
         */
        if (isSliding) {
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    if (mCurrentViewPos != -1) {
                        if (Math.abs(yDown - y) < 30 && Math.abs(xDown - x) > 20) {
                            int first = this.getFirstVisiblePosition();
                            int index = mCurrentViewPos - first;
                            mFocusedItemView = (SlideView) getChildAt(index);
                            mFocusedItemView.onTouchEvent(ev);
                            return true;
                        }
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    isChild = false;
                    if (isSliding) {
//					isSliding = false;
                        if (mFocusedItemView != null) {
                            mFocusedItemView.adjust(xDown - x > 0);
                            return true;
                        }
                    }
            }
            // 相应滑动期间屏幕itemClick事件，避免发生冲突
            return true;
        }

        return super.onTouchEvent(ev);
    }


    public void setButtonClickListener(ButtonClickListener listener) {
        mListener = listener;
    }

    interface ButtonClickListener {
        public void clickHappend(int position);
    }
}
