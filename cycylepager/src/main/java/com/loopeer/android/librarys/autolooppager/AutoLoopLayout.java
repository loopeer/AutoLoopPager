package com.loopeer.android.librarys.autolooppager;

import android.content.Context;
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
import android.widget.LinearLayout;

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
        LayoutInflater.from(getContext()).inflate(R.layout.view_auto_loop_layout, this, true);
        mImageAdapter = new ImageAdapter();
        createIndicator();
    }

    private void createIndicator() {
        mPageIndicator = new PageIndicator(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        layoutParams.rightMargin = 16;
        layoutParams.bottomMargin = 16;
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
            mTimer.schedule(mTask, DEFAULT_START_DELAY, DEFAULT_PERIOD);
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

    public void setILoopImage(ILoopAdapter loopImager) {
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
            mViewPager.setCurrentItem(TMP_AMOUNT);
        }
        updateImageIndicator();
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
        return position % getAdapterRealCount();
    }

    public void setLoopPageChangeListener(LoopPageChangeListener listener) {
        mLoopPageChangeListener = listener;
    }

    public void setPageIndicator(PageIndicator pageIndicator) {
        if (mPageIndicator != null) {
            ((ViewGroup)mPageIndicator.getParent()).removeView(mPageIndicator);
            mPageIndicator = null;
        }
        mPageIndicator = pageIndicator;
        updateImageIndicator();
    }

    private void updateImageIndicator() {
        if (mPageIndicator == null) return;
        mPageIndicator.updateCount(mImageAdapter.getRealCount());
        updateIndicatorPosition(getRealPosition(mViewPager.getCurrentItem()));
    }

}
