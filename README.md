# ShoppingCart
一个仿京东、淘宝等购物车的效果，并支持侧滑删除效果

1、使用一个继承ExpandableListView的SuperExpandableListView类来实现购物车的分组的效果
### SuperExpandableListView.java
```java
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
```
2、使用一个继承LinearLayout的SlideView来实现左滑删除效果
### SlideView.java
```java
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
```
我的代码还有些需要完善的，还请多多指教！
如果你觉得我的代码对你有帮助，请麻烦你在右上角给我一个star.^_^

#效果图
![image](https://github.com/itzuo/ShoppingCart/blob/master/GIF.gif) 
