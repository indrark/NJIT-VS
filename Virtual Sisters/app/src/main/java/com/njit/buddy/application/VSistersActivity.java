package com.njit.buddy.application;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.njit.buddy.application.fragment.AttentionFragment;
import com.njit.buddy.application.fragment.MoodFragment;
import com.njit.buddy.application.fragment.MoreFragment;
import com.njit.buddy.application.fragment.NewsFragment;
import com.njit.buddy.application.network.task.MoodSubmitTask;
import com.njit.buddy.application.util.DateUtil;

/**
 * @author toyknight 8/16/2015.
 */
public class VSistersActivity extends VSistersResumeRecordingActivity implements View.OnClickListener {

    private final int TAB_NEWS = 0x001;
    private final int TAB_ATTENTION = 0x002;
    private final int TAB_MOOD = 0x003;
    private final int TAB_MORE = 0x004;

    private NewsFragment news_fragment;
    private AttentionFragment attention_fragment;
    private MoodFragment mood_fragment;
    private MoreFragment more_fragment;

    private View tab_news_layout;
    private View tab_attention_layout;
    private View tab_mood_layout;
    private View tab_more_layout;

    private Dialog dialog_mood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vsisters);

        initComponents();

        setTabSelection(TAB_NEWS);

        /*if(isFirstTimeUsing()) {
            gotoIntroductionActivity();
        }*/
        SharedPreferences preferences = getSharedPreferences("buddy", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(getString(R.string.key_last_time), -1);
        editor.apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        long last_use_time = getLastUsingTime();
        if (last_use_time > 0) {
            if (!DateUtil.isToday(last_use_time)) {
                showMoodDialog();
            }
        } else {
            showMoodDialog();
        }
    }

   /* private void gotoIntroductionActivity() {
        SharedPreferences preferences = getSharedPreferences("buddy", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(getResources().getString(R.string.key_first_time), false);
        editor.apply();
        Intent intent = new Intent(this, IntroductionActivity.class);
        startActivity(intent);
    } */

    private boolean isFirstTimeUsing() {
        SharedPreferences preferences = getSharedPreferences("buddy", Context.MODE_PRIVATE);
        return preferences.getBoolean(getResources().getString(R.string.key_first_time), true);
    }

    private long getLastUsingTime() {
        SharedPreferences preferences = getSharedPreferences("buddy", Context.MODE_PRIVATE);
        return preferences.getLong(getString(R.string.key_last_time), -1);
    }

    private void showMoodDialog() {
        SharedPreferences preferences = getSharedPreferences("buddy", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(getString(R.string.key_last_time), System.currentTimeMillis());
        editor.apply();

        dialog_mood.show();
    }

    private void submitMood(int mood) {
        MoodSubmitTask task = new MoodSubmitTask() {
            @Override
            public void onSuccess(Integer result) {
            }

            @Override
            public void onFail(int error_code) {
            }
        };
        task.execute(mood);
    }

    @SuppressWarnings("ResourceType")
    private void initComponents() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setCustomView(R.layout.abs_vsisters);
        }

        //initialize bottom tabs
        tab_news_layout = findViewById(R.id.tab_news_layout);
        tab_attention_layout = findViewById(R.id.tab_attention_layout);
        tab_mood_layout = findViewById(R.id.tab_mood_layout);
        tab_more_layout = findViewById(R.id.tab_more_layout);
        tab_news_layout.setOnClickListener(this);
        tab_attention_layout.setOnClickListener(this);
        tab_mood_layout.setOnClickListener(this);
        tab_more_layout.setOnClickListener(this);

        View btn_create_post = findViewById(R.id.btn_create_post);
        btn_create_post.setOnClickListener(this);

        //temporarily remove mood tab
//        View tab_mood_layout = findViewById(R.id.tab_mood_layout);
//        ((ViewGroup) tab_mood_layout.getParent()).removeView(tab_mood_layout);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.message_feeling)
                .setItems(R.array.mood, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog_mood.dismiss();
                        submitMood(which);
                    }
                });
        dialog_mood = builder.create();
        dialog_mood.setCancelable(false);
        dialog_mood.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_news_layout:
                setTabSelection(TAB_NEWS);
                break;
            case R.id.tab_attention_layout:
                setTabSelection(TAB_ATTENTION);
                break;
            case R.id.tab_mood_layout:
                setTabSelection(TAB_MOOD);
                break;
            case R.id.tab_more_layout:
                setTabSelection(TAB_MORE);
                break;
            case R.id.btn_create_post:
                news_fragment.startPostingProgress();
                break;
        }
    }

    private void updateCurrentTab(int tab) {
        SharedPreferences preferences = getSharedPreferences("buddy", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(getResources().getString(R.string.key_tab), tab);
        editor.apply();
    }

    private void setTabSelection(int index) {
        clearTabSelection();
        //begin a new fragment transaction
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        //hide all the fragments in case multiple fragments are shown at the same time
        hideFragments(transaction);
        switch (index) {
            case TAB_NEWS:
                //change the news tab background to checked
                tab_news_layout.setBackgroundResource(R.drawable.background_tab_checked);
                //show the news fragment
                if (news_fragment == null) {
                    news_fragment = new NewsFragment();
                    transaction.add(R.id.content, news_fragment);
                } else {
                    transaction.show(news_fragment);
                }
                news_fragment.tryRefreshNewsList();
                updateActionBar(getResources().getString(R.string.tab_news), true);
                updateCurrentTab(TAB_NEWS);
                break;
            case TAB_ATTENTION:
                //change the attention tab background to checked
                tab_attention_layout.setBackgroundResource(R.drawable.background_tab_checked);
                //show the attention fragment
                if (attention_fragment == null) {
                    attention_fragment = new AttentionFragment();
                    transaction.add(R.id.content, attention_fragment);
                } else {
                    transaction.show(attention_fragment);
                }
                attention_fragment.tryRefreshAttentionList();
                updateActionBar(getResources().getString(R.string.tab_attention), false);
                updateCurrentTab(TAB_ATTENTION);
                break;
            case TAB_MOOD:
                //change the mood tab background to checked
                tab_mood_layout.setBackgroundResource(R.drawable.background_tab_checked);
              //show the mood fragment
               if (mood_fragment == null) {
                    mood_fragment = new MoodFragment();
                    transaction.add(R.id.content, mood_fragment);
               } else {
                    transaction.show(mood_fragment);
                }
                mood_fragment.tryRefreshMoodList();
                updateActionBar(getResources().getString(R.string.tab_mood), false);
                updateCurrentTab(TAB_MOOD);
               break;
            case TAB_MORE:
                //change the more tab background to checked
                tab_more_layout.setBackgroundResource(R.drawable.background_tab_checked);
                //show the more fragment
                if (more_fragment == null) {
                    more_fragment = new MoreFragment();
                    transaction.add(R.id.content, more_fragment);
                } else {
                    transaction.show(more_fragment);
                }
                updateActionBar(getResources().getString(R.string.tab_more), false);
                updateCurrentTab(TAB_MORE);
                break;
        }
        transaction.commit();
    }

    private void clearTabSelection() {
        tab_news_layout.setBackgroundResource(R.drawable.background_tab_unchecked);
        tab_attention_layout.setBackgroundResource(R.drawable.background_tab_unchecked);
        tab_mood_layout.setBackgroundResource(R.drawable.background_tab_unchecked);
        tab_more_layout.setBackgroundResource(R.drawable.background_tab_unchecked);
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (news_fragment != null) {
            transaction.hide(news_fragment);
        }
        if (attention_fragment != null) {
            transaction.hide(attention_fragment);
        }
        if (mood_fragment != null) {
            transaction.hide(mood_fragment);
        }
        if (more_fragment != null) {
            transaction.hide(more_fragment);
        }
    }

    private void updateActionBar(String title, boolean show_add_btn) {
        ((TextView) findViewById(R.id.abs_title)).setText(title);
        if (show_add_btn) {
            findViewById(R.id.btn_create_post).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.btn_create_post).setVisibility(View.INVISIBLE);
        }
    }

}
