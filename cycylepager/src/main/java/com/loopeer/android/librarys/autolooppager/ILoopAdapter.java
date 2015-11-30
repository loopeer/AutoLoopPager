package com.loopeer.android.librarys.autolooppager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public interface ILoopAdapter<T> {

    View createView(ViewGroup viewGroup, LayoutInflater inflater, Context context);

    void bindItem(View view, int position, T t);
}
