package com.loopeer.android.librarys.autolooppager;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PageIndicator extends LinearLayout {
    private final static int DEFAUlT_MARGIN = 8;

    private int mCount;
    private Drawable mUnSelectDrawable;
    private Drawable mSelectDrawable;
    private int mPreSelectPosition;

    public PageIndicator(Context context) {
        this(context, null);
    }

    public PageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);

        mUnSelectDrawable = mUnSelectDrawable == null ? createUnSelectDefaultDrawable() : null;
        mSelectDrawable = mSelectDrawable == null ? createSelectDefaultDrawable() : null;
    }

    private Drawable createUnSelectDefaultDrawable() {
        return createDefaultDrawable(false);
    }

    private Drawable createSelectDefaultDrawable() {
        return createDefaultDrawable(true);
    }

    private Drawable createDefaultDrawable(boolean isSelected) {
        return ContextCompat.getDrawable(getContext(), isSelected
                ? R.drawable.shape_selected
                : R.drawable.shape_unselected);
    }

    public void updateCount(int realCount) {
        mCount = realCount;
        resetView();
    }

    private void resetView() {
        removeAllViews();
        for (int i = 0; i < mCount; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageDrawable(mUnSelectDrawable);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.rightMargin = DEFAUlT_MARGIN;
            if (i == mCount - 1) layoutParams.rightMargin = 0;
            addView(imageView, layoutParams);
        }
        updatePosition(mPreSelectPosition);
    }

    public void updatePosition(int realPosition) {
        if (getChildAt(mPreSelectPosition) == null) return;
        ImageView imageView = (ImageView) getChildAt(mPreSelectPosition);
        imageView.setImageDrawable(mUnSelectDrawable);

        ImageView imageViewSelect = (ImageView) getChildAt(realPosition);
        imageViewSelect.setImageDrawable(mSelectDrawable);
        mPreSelectPosition = realPosition;
    }
}
