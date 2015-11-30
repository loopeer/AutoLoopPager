package com.loopeer.android.librarys.autolooppager;

public interface LoopPageChangeListener {

    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

    void onPageSelected(int position);

    void onPageScrollStateChanged(int state);

}
