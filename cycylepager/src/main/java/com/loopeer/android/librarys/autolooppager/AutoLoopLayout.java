package com.loopeer.android.librarys.autolooppager;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AutoLoopLayout<T> extends FrameLayout implements ViewPager.OnPageChangeListener {
    protected final static int TMP_AMOUNT = 1200;
    private final static int DEFAULT_PERIOD = 5000;
    private final static int DEFAULT_START_DELAY = 2000;
    private final static int MESSAGE_ON_PAGE_CHANGE = 1;

    private ViewPager mViewPager;
    private PageIndicator mPageIndicator;
    private LoopPageChangeListener mLoopPageChangeListener;
    private ImageAdapter<T> mImageAdapter;
    private Timer mTimer;
    private TimerTask mTask;
    private boolean mCanAutoLoop;
    private boolean mShowIndicator;
    private int mLoopPeriod;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == MESSAGE_ON_PAGE_CHANGE) {
                mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
            }
            return false;
        }
    });

    public AutoLoopLayout(Context context) {
        this(context, null);
    }

    public AutoLoopLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoLoopLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        if (attrs == null) return;
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoLoopLayout, defStyleAttr, 0);
        if (a == null) return;

        mCanAutoLoop = a.getBoolean(R.styleable.AutoLoopLayout_autoLoop, true);
        mShowIndicator = a.getBoolean(R.styleable.AutoLoopLayout_showIndicator, true);
        mLoopPeriod = a.getInteger(R.styleable.AutoLoopLayout_loopPeriod, DEFAULT_PERIOD);
        int indicatorMargin = a.getDimensionPixelSize(R.styleable.AutoLoopLayout_indicatorMargin,
                getResources().getDimensionPixelSize(R.dimen.cyclepager_inline_margin));
        Drawable selectDrawable = a.getDrawable(R.styleable.AutoLoopLayout_selectDrawable);
        Drawable unSelectDrawable = a.getDrawable(R.styleable.AutoLoopLayout_unSelectDrawable);
        a.recycle();

        LayoutInflater.from(getContext()).inflate(R.layout.view_auto_loop_layout, this, true);
        mImageAdapter = new ImageAdapter();
        if (mShowIndicator) createDefaultIndicator(unSelectDrawable, selectDrawable, indicatorMargin);
    }

    private void createDefaultIndicator(Drawable unSelectDrawable, Drawable selectDrawable, int indicatorMargin) {
        mPageIndicator = new PageIndicator(getContext());
        mPageIndicator.setIndicatorMargin(indicatorMargin);
        mPageIndicator.updateDrawable(unSelectDrawable, selectDrawable);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        int margin = getResources().getDimensionPixelSize(R.dimen.cyclepager_list_horizontal_margin);
        layoutParams.rightMargin = margin;
        layoutParams.bottomMargin = margin;
        addView(mPageIndicator, layoutParams);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mViewPager = (ViewPager) this.findViewById(R.id.pager_auto_loop_layout);
        mViewPager.setAdapter(mImageAdapter);
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        stopTimer();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case KeyEvent.ACTION_DOWN:
                stopTimer();
                break;
            case KeyEvent.ACTION_UP:
                startTimer();
                break;
        }

        return super.dispatchTouchEvent(ev);
    }

    private void startTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (mTask == null) {
            mTask = new TimerTask() {
                public void run() {
                    Message message = new Message();
                    message.what = MESSAGE_ON_PAGE_CHANGE;
                    mHandler.sendMessage(message);
                }
            };
            mTimer.schedule(mTask, DEFAULT_START_DELAY, mLoopPeriod);
        }
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
    }

    private void doNormalShow() {
        mViewPager.setCurrentItem(TMP_AMOUNT);
        if (mCanAutoLoop) startLoop();
    }

    private void doOneSimpleImageShow() {
        mViewPager.setCurrentItem(0);
        stopLoop();
    }

    private int getAdapterRealCount() {
        return mImageAdapter.getRealCount();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        doOnPageScrolled(getRealPosition(position), positionOffset, positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        doOnPageSelected(getRealPosition(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        doOnPageScrollStateChanged(state);
    }

    private void doOnPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mLoopPageChangeListener != null) {
            mLoopPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    private void doOnPageSelected(int position) {
        if (mLoopPageChangeListener != null) {
            mLoopPageChangeListener.onPageSelected(position);
        }
        updateIndicatorPosition(position);
    }

    private void updateIndicatorPosition(int position) {
        if (mPageIndicator == null) return;
        mPageIndicator.updatePosition(position);
    }

    private void doOnPageScrollStateChanged(int state) {
        if (mLoopPageChangeListener != null) {
            mLoopPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    private int getRealPosition(int position) {
        return position == 0 ? 0 : position % getAdapterRealCount();
    }

    public void setLoopPageChangeListener(LoopPageChangeListener listener) {
        mLoopPageChangeListener = listener;
    }

    public void setPageIndicator(PageIndicator pageIndicator) {
        mShowIndicator = true;
        if (mPageIndicator != null) {
            ((ViewGroup)mPageIndicator.getParent()).removeView(mPageIndicator);
            mPageIndicator = null;
        }
        mPageIndicator = pageIndicator;
        updateImageIndicator();
    }

    private void updateImageIndicator() {
        if (mPageIndicator == null) return;
        mPageIndicator.setVisibility(mImageAdapter.getRealCount() == 1 ? GONE : VISIBLE);
        mPageIndicator.updateCount(mImageAdapter.getRealCount());
        updateIndicatorPosition(getRealPosition(mViewPager.getCurrentItem()));
    }

    public void setILoopAdapter(ILoopAdapter loopImager) {
        mImageAdapter.setILoopAdapter(loopImager);
    }

    public void startLoop() {
        startTimer();
    }

    @SuppressWarnings("unused")
    public void stopLoop() {
        stopTimer();
    }

    public void updateData(List<T> data) {
        boolean isAddDataFromEmptyState = mImageAdapter.getCount() == 0;
        mImageAdapter.updateData(data);
        if (isAddDataFromEmptyState) {
            mViewPager.setCurrentItem(mImageAdapter.getRealCount() == 1 ? 0 : TMP_AMOUNT);
            if (mImageAdapter.getRealCount() == 1) {
                doOneSimpleImageShow();
            } else {
                doNormalShow();
            }
        }
        updateImageIndicator();
    }

}
