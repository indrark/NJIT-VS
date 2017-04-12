package com.njit.buddy.app.fragment;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.njit.buddy.app.R;
import com.njit.buddy.app.entity.Mood;
import com.njit.buddy.app.network.task.MoodListTask;
import com.njit.buddy.app.util.DateUtil;
import com.njit.buddy.app.widget.VSistersScrollListener;
import com.njit.buddy.app.widget.VSistersScrollView;

import java.util.ArrayList;

/**
 * @author Indraneel 4/11/2017
 */
public class MoodFragment extends Fragment {

    private SwipeRefreshLayout swipe_container;
    private int current_page;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mood, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        swipe_container = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_container_mood);
        swipe_container.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        tryRefreshMoodList();
                    }
                }
        );
        VSistersScrollView mood_scroll_view = (VSistersScrollView) getActivity().findViewById(R.id.scroll_view_mood);
        mood_scroll_view.setScrollListener(new VSistersScrollListener() {
            @Override
            public void onBottomReached() {
                tryReadMoreMoods();
            }
        });
    }

    public void tryRefreshMoodList() {
        current_page = 0;
        MoodListTask task = new MoodListTask() {
            @Override
            public void onSuccess(ArrayList<Mood> mood_list) {
                swipe_container.setRefreshing(false);
                setMoodList(mood_list);
            }

            @Override
            public void onFail(int error_code) {
                swipe_container.setRefreshing(false);
                Log.d("Attention", "Error code " + error_code);
            }
        };
        task.execute(current_page);
    }

    public void tryReadMoreMoods() {
        MoodListTask task = new MoodListTask() {
            @Override
            public void onSuccess(ArrayList<Mood> mood_list) {
                swipe_container.setRefreshing(false);
                addMoodList(mood_list);
            }

            @Override
            public void onFail(int error_code) {
                swipe_container.setRefreshing(false);
                Log.d("Attention", "Error code " + error_code);
            }
        };
        task.execute(++current_page);
    }

    public void setMoodList(ArrayList<Mood> mood_list) {
        LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.mood_layout);
        layout.removeAllViews();
        String[] moods = getResources().getStringArray(R.array.mood);
        for (Mood mood : mood_list) {
            layout.addView(createMoodView(mood, moods));
        }
    }

    public void addMoodList(ArrayList<Mood> mood_list) {
        LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.mood_layout);
        layout.removeAllViews();
        String[] moods = getResources().getStringArray(R.array.mood);
        for (Mood mood : mood_list) {
            layout.addView(createMoodView(mood, moods));
        }
    }

    private TextView createMoodView(Mood mood, String[] moods) {
        TextView text_mood = new TextView(getActivity());
        text_mood.setTextAppearance(getActivity(), R.style.TextAppearance_AppCompat_Large);
        text_mood.setTextColor(Color.BLACK);
        text_mood.setPadding(10, 0, 0, 0);
        text_mood.setText(String.format("[%s] %s", DateUtil.toDateString(mood.getTimestamp()), moods[mood.getMood()]));
        return text_mood;
    }

}
