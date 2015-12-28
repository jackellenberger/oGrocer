package quokka.jellenberger.ogrocer;

/**
 * Created by jellenberger on 12/17/15.
 */
/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ViewUtils {
    public static boolean hitTest(View v, int x, int y) {
        final int tx = (int) (ViewCompat.getTranslationX(v) + 0.5f);
        final int ty = (int) (ViewCompat.getTranslationY(v) + 0.5f);
        final int left = v.getLeft() + tx;
        final int right = v.getRight() + tx;
        final int top = v.getTop() + ty;
        final int bottom = v.getBottom() + ty;

        return (x >= left) && (x <= right) && (y >= top) && (y <= bottom);
    }

    public static void setTwoPane(RecyclerView mRecyclerView, int tabPosition) {
        final int tabPos[] = new int[1];
        tabPos[0] = tabPosition;
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            private Float x1, x2;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: //this never seems to happen?
                        Log.d("setTwoPane","ACTION DOWN");
                        x1 = event.getX();
                        if (x1 > 73) //leave room for drawer to be pulled
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (x1 == null){
                            Log.d("setTwoPane","ACTION MOVE - SET X1");
                            x1 = event.getX();
                        }
                        else {
                            Log.d("setTwoPane","ACTION DOWN - SET X2");
                            x2 = event.getX();
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            //if you sliding (in the allowed direction) allow for intercept to be handled by slidingTabLayout
                            if (tabPos[0] == 0)
                                if (x2 < x1){
                                    Log.d("setTwoPane","tab pos 0 allow tab change");
                                    v.getParent().requestDisallowInterceptTouchEvent(false);
                                }
                                else v.getParent().requestDisallowInterceptTouchEvent(true);
                            else
                                if (x1 < x2) {
                                    Log.d("setTwoPane","tab pos 1 allow tab change");
                                    v.getParent().requestDisallowInterceptTouchEvent(false);
                                }
                                else v.getParent().requestDisallowInterceptTouchEvent(true);
                        }
                        break;
                    case MotionEvent.ACTION_UP | MotionEvent.ACTION_CANCEL:
                        Log.d("setTwoPane","ACTION UP / ACTION CANCEL RESET");
                        x1 = x2 = null;
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                    default:
                        Log.d("SOMETHING ELSE HAPPENED",String.valueOf(event.getAction()));
                        break;
                }
                return false;
            }
        });
    }

}