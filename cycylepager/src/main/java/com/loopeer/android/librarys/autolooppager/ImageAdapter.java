package com.loopeer.android.librarys.autolooppager;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter<T> extends PagerAdapter{

    private ILoopAdapter<T> mILoopAdapter;
    private List<T> mData;
    private List<View> mHolderViews = new ArrayList<>();

    public ImageAdapter() {
        mData = new ArrayList<>();
    }

    public void updateData(List<T> data) {
        setData(data);
        notifyDataSetChanged();
    }

    private void setData(List<T> data) {
        mData.clear();
        if (data != null) {
            mData.addAll(data);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        position = position % getRealCount();
        LayoutInflater inflater = LayoutInflater.from(collection.getContext());
        View view = getView(collection, inflater, position);
        mILoopAdapter.bindItem(view, position, mData.get(position));
        collection.addView(view);
        return view;
    }

    private View getView(ViewGroup collection, LayoutInflater inflater, int position) {
        View view;
        if (mHolderViews.size() < 4) {
            view = mILoopAdapter.createView(collection, inflater, collection.getContext());
            mHolderViews.add(view);
        } else {
            view = mHolderViews.get(position % 4);
            collection.removeView(view);
        }
        return view;
    }


    public void setILoopAdapter(ILoopAdapter loopAdapter) {
        mILoopAdapter = loopAdapter;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size() == 1 ? 1 : mData.size() * AutoLoopLayout.TMP_AMOUNT;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public int getRealCount() {
        return mData.size();
    }
}
