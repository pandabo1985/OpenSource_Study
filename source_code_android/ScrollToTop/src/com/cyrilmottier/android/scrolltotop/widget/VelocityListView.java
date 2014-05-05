/*
 * Copyright (C) 2013 Cyril Mottier (http://www.cyrilmottier.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyrilmottier.android.scrolltotop.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * An extension of the framework's {@link ListView} that can determine an
 * approximate value of its current velocity on the Y-axis.
 * 
 * @author Cyril Mottier
 */
public class VelocityListView extends AutoScrollListView {

    /**
     * A callback to be notified the velocity has changed.
     * 
     * @author Cyril Mottier
     */
    public interface OnVelocityListViewListener {
        void onVelocityChanged(int velocity);
    }

    private static final long INVALID_TIME = -1;

    /**
     * This value is really necessary to avoid weird velocity values. Indeed, in
     * fly-wheel mode, onScroll is called twice per-frame which results in
     * having a delta divided by a value close to zero. onScroll is usually
     * being called 60 times per seconds (i.e. every 16ms) so 10ms is a good
     * threshold.
     */
    private static final long MINIMUM_TIME_DELTA = 10L;

    private final ForwardingOnScrollListener mForwardingOnScrollListener = new ForwardingOnScrollListener();

    private OnVelocityListViewListener mOnVelocityListViewListener;

    private long mTime = INVALID_TIME;
    private int mVelocity;

    private int mFirstVisiblePosition;
    private int mFirstVisibleViewTop;
    private int mLastVisiblePosition;
    private int mLastVisibleViewTop;

    public VelocityListView(Context context) {
        super(context);
        init();
    }

    public VelocityListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VelocityListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        super.setOnScrollListener(mForwardingOnScrollListener);
        mForwardingOnScrollListener.selfListener = mOnScrollListener;
    }

    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mForwardingOnScrollListener.clientListener = l;
    }

    public void setOnVelocityListener(OnVelocityListViewListener l) {
        mOnVelocityListViewListener = l;
    }

    /**
     * Return an approximative value of the ListView's current velocity on the
     * Y-axis. A negative value indicates the ListView is currently being
     * scrolled towards the bottom (i.e items are moving from bottom to top)
     * while a positive value indicates it is currently being scrolled towards
     * the top (i.e. items are moving from top to bottom).
     * 
     * @return An approximative value of the ListView's velocity on the Y-axis
     */
    public int getVelocity() {
        return mVelocity;
    }

    private void setVelocity(int velocity) {
        if (mVelocity != velocity) {
            mVelocity = velocity;
            if (mOnVelocityListViewListener != null) {
                mOnVelocityListViewListener.onVelocityChanged(velocity);
            }
        }
    }

    /**
     * @author Cyril Mottier
     */
    private static class ForwardingOnScrollListener implements OnScrollListener {

        private OnScrollListener selfListener;
        private OnScrollListener clientListener;

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (selfListener != null) {
                selfListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
            if (clientListener != null) {
                clientListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (selfListener != null) {
                selfListener.onScrollStateChanged(view, scrollState);
            }
            if (clientListener != null) {
                clientListener.onScrollStateChanged(view, scrollState);
            }
        }
    }

    private OnScrollListener mOnScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            switch (scrollState) {
                case SCROLL_STATE_IDLE:
                    mTime = INVALID_TIME;
                    setVelocity(0);
                    break;

                default:
                    break;
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisiblePosition, int visibleItemCount, int totalItemCount) {

            final long now = AnimationUtils.currentAnimationTimeMillis();
            final int lastVisiblePosition = firstVisiblePosition + visibleItemCount - 1;

            if (mTime != INVALID_TIME) {

                final long delta = now - mTime;
                if (now - mTime > MINIMUM_TIME_DELTA) {
                    int distance = 0;
                    //@formatter:off
                    if (mFirstVisiblePosition >= firstVisiblePosition 
                            && mFirstVisiblePosition <= lastVisiblePosition) {
                        distance = getChildAt(mFirstVisiblePosition - firstVisiblePosition).getTop() - mFirstVisibleViewTop;

                    } else if (mLastVisiblePosition >= firstVisiblePosition 
                            && mLastVisiblePosition <= lastVisiblePosition) {
                        distance = getChildAt(mLastVisiblePosition - firstVisiblePosition).getTop() - mLastVisibleViewTop;
                    //@formatter:on
                    } else {
                        // We're in a case were the item we were previously
                        // referencing has moved out of the visible window.
                        // Let's compute an approximative distance
                        int heightSum = 0;
                        for (int i = 0; i < visibleItemCount; i++) {
                            heightSum += getChildAt(i).getHeight();
                        }

                        distance = heightSum / visibleItemCount * (mFirstVisiblePosition - firstVisiblePosition);
                    }

                    setVelocity((int) (1000d * distance / delta));
                }
            }

            mFirstVisiblePosition = firstVisiblePosition;
            mFirstVisibleViewTop = getChildAt(0).getTop();
            mLastVisiblePosition = lastVisiblePosition;
            mLastVisibleViewTop = getChildAt(visibleItemCount - 1).getTop();

            mTime = now;
        }
    };

}
