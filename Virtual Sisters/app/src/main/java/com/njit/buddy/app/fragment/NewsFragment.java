package com.njit.buddy.app.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.njit.buddy.app.R;
import com.njit.buddy.app.entity.Post;
import com.njit.buddy.app.network.task.PostListTask;
import com.njit.buddy.app.network.task.PostCreateTask;
import com.njit.buddy.app.util.Log;
import com.njit.buddy.app.widget.*;

import java.util.ArrayList;

/**
 * @author toyknight 8/15/2015.
 */
public class NewsFragment extends Fragment implements CategorySelectorListener {

    private SwipeRefreshLayout swipe_container;

    private AlertDialog category_list;
    private AlertDialog post_dialog;
    private EditText content_input;

    private CategorySelector category_selector;

    private int selected_category;

    private int current_page;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createDialogs();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        swipe_container = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_container_news);
        swipe_container.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        tryRefreshNewsList();
                    }
                }
        );
        BuddyScrollView post_scroll_view = (BuddyScrollView) getActivity().findViewById(R.id.scroll_view_news);
        post_scroll_view.setScrollListener(new BuddyScrollListener() {
            @Override
            public void onBottomReached() {
                tryReadMorePosts();
            }
        });

        category_selector = (CategorySelector) getActivity().findViewById(R.id.category_selector);
        category_selector.setCategorySelectorListener(this);
    }

    private void createDialogs() {
        //create category list
        AlertDialog.Builder category_builder = new AlertDialog.Builder(getActivity());
        category_builder.setTitle(R.string.message_choose_category)
                .setItems(R.array.category, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showPostDialog(which);
                    }
                });
        category_list = category_builder.create();
        //create post dialog
        AlertDialog.Builder post_builder = new AlertDialog.Builder(getActivity());
        post_builder.setTitle(R.string.message_say_something);
        content_input = new EditText(getActivity());
        post_builder.setView(content_input);
        post_builder.setPositiveButton(getResources().getString(R.string.label_post), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String content = content_input.getText().toString();
                tryPost(content, selected_category);
                post_dialog.dismiss();
            }
        });
        post_builder.setNegativeButton(getResources().getString(R.string.label_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        post_dialog = post_builder.create();
    }

    private void showPostDialog(int selected_category) {
        this.selected_category = selected_category;
        content_input.setText("");
        category_list.dismiss();
        post_dialog.show();
    }

    public void startPostingProgress() {
        category_list.show();
    }

    public void tryPost(String content, int selected_category) {
        PostCreateTask task = new PostCreateTask() {
            @Override
            public void onSuccess(Integer result) {
                category_selector.reset();
                tryRefreshNewsList();
            }

            @Override
            public void onFail(int error_code) {
                Log.error("Post", error_code);
            }
        };
        task.execute(Integer.toString(selected_category), content);
    }

    public void tryRefreshNewsList() {
        current_page = 0;
        PostListTask task = new PostListTask() {
            @Override
            public void onSuccess(ArrayList<Post> post_list) {
                swipe_container.setRefreshing(false);
                setPostList(post_list);
            }

            @Override
            public void onFail(int error_code) {
                swipe_container.setRefreshing(false);
                Log.error("News", error_code);
            }
        };
        task.execute(current_page, getSelectedCategory(), 0, 0);
    }

    public void tryReadMorePosts() {
        PostListTask task = new PostListTask() {
            @Override
            public void onSuccess(ArrayList<Post> post_list) {
                swipe_container.setRefreshing(false);
                addPostList(post_list);
                //this is a test
            }

            @Override
            public void onFail(int error_code) {
                swipe_container.setRefreshing(false);
                Log.error("News", error_code);
            }
        };
        task.execute(++current_page, getSelectedCategory(), 0, 0);
    }

    public void setPostList(ArrayList<Post> post_list) {
        LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.news_layout);
        layout.removeAllViews();
        for (Post post : post_list) {
            PostView post_view = new PostView(getActivity(), post);
            layout.addView(post_view);
        }
    }

    public void addPostList(ArrayList<Post> post_list) {
        LinearLayout layout = (LinearLayout) getActivity().findViewById(R.id.news_layout);
        for (Post post : post_list) {
            PostView post_view = new PostView(getActivity(), post);
            layout.addView(post_view);
        }
    }

    private int getSelectedCategory() {
        if (category_selector == null) {
            return -1;
        } else {
            return category_selector.getCurrentCategoryIndex();
        }
    }

    @Override
    public void onSelectedCategoryChange() {
        tryRefreshNewsList();
    }
}
