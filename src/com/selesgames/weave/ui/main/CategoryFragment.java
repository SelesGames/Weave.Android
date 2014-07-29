package com.selesgames.weave.ui.main;

import javax.inject.Inject;

import rx.Scheduler;
import rx.functions.Action1;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.InjectView;

import com.selesgames.weave.ForActivity;
import com.selesgames.weave.OnMainThread;
import com.selesgames.weave.R;
import com.selesgames.weave.WeavePrefs;
import com.selesgames.weave.api.UserService;
import com.selesgames.weave.model.CategoryNews;
import com.selesgames.weave.model.News;
import com.selesgames.weave.ui.BaseFragment;
import com.squareup.picasso.Picasso;

public class CategoryFragment extends BaseFragment {

    private static final String KEY_CATEGORY_ID = "category_id";

    public static CategoryFragment newInstance(String categoryId) {
        Bundle b = new Bundle();
        b.putString(KEY_CATEGORY_ID, categoryId);

        CategoryFragment f = new CategoryFragment();
        f.setArguments(b);
        return f;
    }

    @Inject
    @ForActivity
    Context mContext;

    @Inject
    CategoriesController mController;

    @Inject
    @OnMainThread
    Scheduler mScheduler;

    @Inject
    WeavePrefs mPrefs;

    @Inject
    UserService mUserService;

    @Inject
    Picasso mPicasso;

    @InjectView(R.id.list)
    ListView mListView;

    @InjectView(R.id.progress)
    ProgressBar mProgress;

    NewsAdapter mAdapter;

    private String mCategoryId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getArguments();
        mCategoryId = b.getString(KEY_CATEGORY_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mAdapter == null) {
            mUserService.getFeedsForCategory(mPrefs.getUserId(), mCategoryId, "Mark", 0, 20).observeOn(mScheduler)
                    .subscribe(new Action1<CategoryNews>() {

                        @Override
                        public void call(CategoryNews news) {
                            mProgress.setVisibility(View.GONE);
                            mAdapter = new NewsAdapter(mContext, mPicasso, news);
                            mAdapter.setOnClickListener(new NewsAdapter.ClickListener() {

                                @Override
                                public void onItemClicked(News news) {
                                    mController.onNewsSelected(news);
                                }

                            });
                            mListView.setAdapter(mAdapter);

                            for (News n : news.getNews()) {
                                Picasso.with(mContext).load(n.getImageUrl()).skipMemoryCache().fetch();
                            }
                        }

                    }, new Action1<Throwable>() {

                        @Override
                        public void call(Throwable t) {
                            Log.e("WEAVE", "Could not load news", t);
                        }
                    });
        } else {
            mListView.setAdapter(mAdapter);
            mProgress.setVisibility(View.GONE);
        }
    }

}
