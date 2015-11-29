package com.loopeer.android.librarys.autolooppager;

import android.view.View;
import android.view.ViewGroup;

public interface ILoopAdapter<T> {

    View createView(ViewGroup viewGroup);

    void bindItem(View view, int position, T t);
}
