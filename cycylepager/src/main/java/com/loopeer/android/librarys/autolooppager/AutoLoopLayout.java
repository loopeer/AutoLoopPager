package com.loopeer.android.librarys.autolooppager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AutoLoopLayout<T> extends FrameLayout {
    protected final static int TMP_AMOUNT = 1200;

    private ViewPager mViewPager;
    private ImageAdapter<T> mImageAdapter;
    private Timer mTimer;
    private TimerTask mTask;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
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
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mViewPager = (ViewPager) this.findViewById(R.id.pager_auto_loop_layout);
        mViewPager.setAdapter(mImageAdapter);
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
                    message.what = 1;
                    mHandler.sendMessage(message);
                }
            };
            mTimer.schedule(mTask, 2000, 2000);
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
        mImageAdapter.updateDatas(data);
        if (isAddDataFromEmptyState) {
            mViewPager.setCurrentItem(TMP_AMOUNT);
        }

    }
}
