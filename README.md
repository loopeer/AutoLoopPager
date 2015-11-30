# AutoLoopPager

Screeshot
====
![](/screenshot/screenshot.gif)

Usage
====

```xml
    <com.loopeer.android.librarys.autolooppager.AutoLoopLayout
        android:id="@+id/pager_main"
        android:layout_width="match_parent"
        android:layout_height="180dp"
        />
```
And java
```java
    mAutoLoopLayout = (AutoLoopLayout<Integer>) findViewById(R.id.pager_main);
        mAutoLoopLayout.setILoopAdapter(new ILoopAdapter<Integer>() {
            @Override
            public View createView(ViewGroup viewGroup, LayoutInflater inflater, Context context) {
                FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.view_pager_item, viewGroup, false);
                return layout;
            }

            @Override
            public void bindItem(View view, int position, Integer s) {
                view.setBackgroundColor(ContextCompat.getColor(view.getContext(), s));
                ((TextView) view.findViewById(R.id.text_pager_item_title)).setText(String.valueOf(position + 1));
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mAutoLoopLayout.updateData(Arrays.asList(COLOR));
                    }
                });
            }
        });
        mAutoLoopLayout.updateData(Arrays.asList(COLOR));
```
Also can set some values

```xml

    <com.loopeer.android.librarys.autolooppager.AutoLoopLayout
        android:id="@+id/pager_main"
        android:layout_width="match_parent"
        android:layout_height="180dp"
        custom:loopPeriod="5000"
        custom:indicatorMargin="6dp"
        custom:unSelectDrawable="@drawable/drawable_unselected"
        custom:selectDrawable="@drawable/drawable_selected"
        custom:showIndicator="false"
        custom:autoLoop="false"
        />
        
```

And you can 
 * **startLoop()** 
 * **stopLoop()**
 * **setPageIndicator()**
 * **setLoopPageChangeListener()**

  
License
====
<pre>
Copyright 2015 Loopeer

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>
