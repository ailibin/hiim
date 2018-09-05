/*
 * Copyright 2014 Hieu Rocker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aiitec.imlibrary.emojicon;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.aiitec.imlibrary.R;
import com.aiitec.imlibrary.emojicon.emoji.Emojicon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * @author Hieu Rocker (rockerhieu@gmail.com).
 */
public class EmojiconsFragment extends Fragment implements ViewPager.OnPageChangeListener, EmojiconRecents {
    private OnEmojiconBackspaceClickedListener mOnEmojiconBackspaceClickedListener;
    private int mEmojiTabLastSelectedIndex = -1;
//    private View[] mEmojiTabs;
    private ViewPager mViewPager;
    private PagerAdapter mEmojisAdapter;
    private EmojiconRecentsManager mRecentsManager;
    private LinearLayout ll_emoji_circle;
    private List<View> circles = new ArrayList<>();

    private boolean mUseSystemDefault = false;

    private static final String USE_SYSTEM_DEFAULT_KEY = "useSystemDefaults";

    public static EmojiconsFragment newInstance(boolean useSystemDefault) {
        EmojiconsFragment fragment = new EmojiconsFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(USE_SYSTEM_DEFAULT_KEY, useSystemDefault);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.emojicons, container, false);
        ll_emoji_circle = (LinearLayout) view.findViewById(R.id.ll_emoji_circle);

        mViewPager = (ViewPager) view.findViewById(R.id.emojis_pager);
        mViewPager.setOnPageChangeListener(this);
        EmojiconRecents recents = this;
        //                EmojiconRecentsGridFragment.newInstance(mUseSystemDefault),
        //                EmojiconGridFragment.newInstance(Emojicon.TYPE_SYMBOLS, recents, mUseSystemDefault)
        List<EmojiconGridFragment> emojiFragments = Arrays.asList(
                EmojiconGridFragment.newInstance(Emojicon.TYPE_PEOPLE, recents, mUseSystemDefault),
                EmojiconGridFragment.newInstance(Emojicon.TYPE_PLACES, recents, mUseSystemDefault),
                EmojiconGridFragment.newInstance(Emojicon.TYPE_OBJECTS, recents, mUseSystemDefault),
                EmojiconGridFragment.newInstance(Emojicon.TYPE_NATURE, recents, mUseSystemDefault));
                mEmojisAdapter = new EmojiconGridFragmentPagerAdapter(getFragmentManager(), emojiFragments);
        mViewPager.setAdapter(mEmojisAdapter);

        int screenWidth = getActivity().getResources().getDisplayMetrics().widthPixels;
        int width = screenWidth>>5;
        ll_emoji_circle.removeAllViews();
        circles.clear();
        boolean isFirst = true;
        for (EmojiconGridFragment fregment : emojiFragments){
            View circleView = new View(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
            circleView.setLayoutParams(params);
            if(isFirst){
                isFirst = false;
                params.setMargins(width, width, width, width);
            } else {
                params.setMargins(0, width, width, width);
            }

            circleView.setBackgroundResource(R.drawable.circle_emoji_selector);
            ll_emoji_circle.addView(circleView);
            circles.add(circleView);
        }
        mEmojiTabLastSelectedIndex = 0;
        circles.get(0).setSelected(true);

//        view.findViewById(R.id.emojis_backspace).setOnTouchListener(new RepeatListener(1000, 50, new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mOnEmojiconBackspaceClickedListener != null) {
//                    mOnEmojiconBackspaceClickedListener.onEmojiconBackspaceClicked(v);
//                }
//            }
//        }));

        // get last selected page
        mRecentsManager = EmojiconRecentsManager.getInstance(view.getContext());
        int page = mRecentsManager.getRecentPage();
        // last page was recents, check if there are recents to use
        // if none was found, go to page 1
//        if (page == 0 && mRecentsManager.size() == 0) {
//            page = 1;
//        }

//        if (page == 0) {
//            onPageSelected(page);
//        } else {
//
//        }
        mViewPager.setCurrentItem(0, false);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getActivity() instanceof OnEmojiconBackspaceClickedListener) {
            mOnEmojiconBackspaceClickedListener = (OnEmojiconBackspaceClickedListener) getActivity();
        } else if (getParentFragment() instanceof OnEmojiconBackspaceClickedListener) {
            mOnEmojiconBackspaceClickedListener = (OnEmojiconBackspaceClickedListener) getParentFragment();
        } else {
            throw new IllegalArgumentException(context + " must implement interface " + OnEmojiconBackspaceClickedListener.class.getSimpleName());
        }
    }

    @Override
    public void onDetach() {
        mOnEmojiconBackspaceClickedListener = null;
        super.onDetach();
    }

    public static void input(EditText editText, Emojicon emojicon) {
        if (editText == null || emojicon == null) {
            return;
        }

        int start = editText.getSelectionStart();
        int end = editText.getSelectionEnd();
        if (start < 0) {
            editText.append(emojicon.getEmoji());
        } else {
            editText.getText().replace(Math.min(start, end), Math.max(start, end), emojicon.getEmoji(), 0, emojicon.getEmoji().length());
        }
    }

    @Override
    public void addRecentEmoji(Context context, Emojicon emojicon) {
//        EmojiconRecentsGridFragment fragment = (EmojiconRecentsGridFragment) mEmojisAdapter.instantiateItem(mViewPager, 0);
//        fragment.addRecentEmoji(context, emojicon);
    }

    public static void backspace(EditText editText) {
        KeyEvent event = new KeyEvent(0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
        editText.dispatchKeyEvent(event);
    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
    }

    @Override
    public void onPageSelected(int i) {
        if (mEmojiTabLastSelectedIndex == i) {
            return;
        }
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:

                if (mEmojiTabLastSelectedIndex >= 0 && mEmojiTabLastSelectedIndex < circles.size()) {
                    circles.get(mEmojiTabLastSelectedIndex).setSelected(false);
                }
                circles.get(i).setSelected(true);
                mEmojiTabLastSelectedIndex = i;
                mRecentsManager.setRecentPage(i);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    private static class EmojiconGridFragmentPagerAdapter extends FragmentStatePagerAdapter {
        private List<EmojiconGridFragment> fragments;

        public EmojiconGridFragmentPagerAdapter(FragmentManager fm, List<EmojiconGridFragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    /**
     * <p>
     * A class, that can be used as a TouchListener on any view (e.g. a Button).
     * It cyclically runs a clickListener, emulating keyboard-like behaviour. First
     * click is fired immediately, next before initialInterval, and subsequent before
     * normalInterval.
     * </p>
     * <p>
     * Interval is scheduled before the onClick completes, so it has to run fast.
     * If it runs slow, it does not generate skipped onClicks.
     * </p>
     */
    public static class RepeatListener implements View.OnTouchListener {

        private Handler handler = new Handler();

        private int initialInterval;
        private final int normalInterval;
        private final View.OnClickListener clickListener;

        private Runnable handlerRunnable = new Runnable() {
            @Override
            public void run() {
                if (downView == null) {
                    return;
                }
                handler.removeCallbacksAndMessages(downView);
                handler.postAtTime(this, downView, SystemClock.uptimeMillis() + normalInterval);
                clickListener.onClick(downView);
            }
        };

        private View downView;

        /**
         * @param initialInterval The interval before first click event
         * @param normalInterval  The interval before second and subsequent click
         *                        events
         * @param clickListener   The OnClickListener, that will be called
         *                        periodically
         */
        public RepeatListener(int initialInterval, int normalInterval, View.OnClickListener clickListener) {
            if (clickListener == null) {
                throw new IllegalArgumentException("null runnable");
            }
            if (initialInterval < 0 || normalInterval < 0) {
                throw new IllegalArgumentException("negative interval");
            }

            this.initialInterval = initialInterval;
            this.normalInterval = normalInterval;
            this.clickListener = clickListener;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downView = view;
                    handler.removeCallbacks(handlerRunnable);
                    handler.postAtTime(handlerRunnable, downView, SystemClock.uptimeMillis() + initialInterval);
                    clickListener.onClick(view);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                    handler.removeCallbacksAndMessages(downView);
                    downView = null;
                    return true;
                default:
                    break;
            }
            return false;
        }
    }

    public interface OnEmojiconBackspaceClickedListener {
        void onEmojiconBackspaceClicked(View v);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUseSystemDefault = getArguments().getBoolean(USE_SYSTEM_DEFAULT_KEY);
        } else {
            mUseSystemDefault = false;
        }
    }
}
