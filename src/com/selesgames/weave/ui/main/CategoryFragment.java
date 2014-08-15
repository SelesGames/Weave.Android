package com.selesgames.weave.ui.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Scheduler;
import rx.functions.Action1;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import butterknife.InjectView;

import com.selesgames.weave.ForActivity;
import com.selesgames.weave.OnMainThread;
import com.selesgames.weave.R;
import com.selesgames.weave.WeavePrefs;
import com.selesgames.weave.api.UserService;
import com.selesgames.weave.model.CategoryNews;
import com.selesgames.weave.model.Feed;
import com.selesgames.weave.model.News;
import com.selesgames.weave.ui.BaseFragment;
import com.selesgames.weave.view.VerticalViewPager;
import com.squareup.picasso.Picasso;

public class CategoryFragment extends BaseFragment {

    private static final String KEY_CATEGORY_ID = "category_id";

    private static final String KEY_FORWARD = "forward";

    private static final String KEY_NEWS = CategoryFragment.class.getCanonicalName() + ".news";

    private static final String KEY_FEEDS = CategoryFragment.class.getCanonicalName() + ".feeds";

    public static CategoryFragment newInstance(String categoryId) {
        Bundle b = new Bundle();
        b.putString(KEY_CATEGORY_ID, categoryId);
        b.putBoolean(KEY_FORWARD, true);

        CategoryFragment f = new CategoryFragment();
        f.setArguments(b);
        return f;
    }

    @Inject
    @ForActivity
    Context mContext;

    @Inject
    CategoryController mController;

    @Inject
    @OnMainThread
    Scheduler mScheduler;

    @Inject
    WeavePrefs mPrefs;

    @Inject
    UserService mUserService;

    @Inject
    Picasso mPicasso;

    @InjectView(R.id.pager)
    VerticalViewPager mViewPager;

    @InjectView(R.id.progress)
    ProgressBar mProgress;

    private Adapter mAdapter;

    private String mCategoryId;

    private List<News> mNews;

    private List<Feed> mFeeds;

    private Map<String, Feed> mFeedMap;

    private boolean mForward;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getArguments();
        mCategoryId = b.getString(KEY_CATEGORY_ID);
        mForward = b.getBoolean(KEY_FORWARD);
        b.remove(KEY_FORWARD);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_category, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFeedMap = new HashMap<String, Feed>();

        if (savedInstanceState == null || mForward) {
            mNews = new ArrayList<News>();
            mFeeds = new ArrayList<Feed>();
        } else {
            mNews = savedInstanceState.getParcelableArrayList(KEY_NEWS);
            mFeeds = savedInstanceState.getParcelableArrayList(KEY_FEEDS);
            populateFeedMap(mFeeds);
        }

        mAdapter = new Adapter(getChildFragmentManager(), mNews, mFeedMap, !mForward);
        mViewPager.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            
            @Override
            public void onPageSelected(int position) {
                News news = mNews.get(position);
                Feed feed = mFeedMap.get(news.getFeedId());
                mController.onNewsFocussed(feed, news);
            }
            
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                
            }
            
            @Override
            public void onPageScrollStateChanged(int state) {
                
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(KEY_NEWS, (ArrayList<News>) mNews);
        outState.putParcelableArrayList(KEY_FEEDS, (ArrayList<Feed>) mFeeds);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mNews.isEmpty()) {
            mUserService.getFeedsForCategory(mPrefs.getUserId(), mCategoryId, "Mark", 0, 20).observeOn(mScheduler)
                    .subscribe(new Action1<CategoryNews>() {

                        @Override
                        public void call(CategoryNews news) {
                            mProgress.setVisibility(View.GONE);

                            List<Feed> feeds = news.getFeeds();
                            mFeeds.removeAll(feeds);
                            mFeeds.addAll(feeds);

                            populateFeedMap(news.getFeeds());

                            mNews.addAll(news.getNews());
                            mAdapter.notifyDataSetChanged();

                            for (News n : news.getNews()) {
                                Picasso.with(mContext).load(n.getImageUrl()).fetch();
                            }
                            
                            // Focus the first item
                            News n = mNews.get(0);
                            Feed feed = mFeedMap.get(n.getFeedId());
                            mController.onNewsFocussed(feed, n);
                        }

                    }, new Action1<Throwable>() {

                        @Override
                        public void call(Throwable t) {
                            Log.e("WEAVE", "Could not load news", t);
                        }
                    });
        } else {
            mProgress.setVisibility(View.GONE);
        }
    }

    private void populateFeedMap(List<Feed> feeds) {
        for (Feed f : feeds) {
            mFeedMap.put(f.getId(), f);
        }
    }

    private static class Adapter extends FragmentStatePagerAdapter {

        private List<News> mNews;

        private Map<String, Feed> mFeedMap;
        
        private boolean mShouldRestore;

        public Adapter(FragmentManager fm, List<News> news, Map<String, Feed> feedMap, boolean shouldRestore) {
            super(fm);
            mNews = news;
            mFeedMap = feedMap;
            mShouldRestore = shouldRestore;
        }

        @Override
        public Fragment getItem(int position) {
            News news = mNews.get(position);
            Feed feed = mFeedMap.get(news.getFeedId());
            return NewsFragment.newInstance(feed, news);
        }

        @Override
        public int getCount() {
            return mNews.size();
        }

//        @Override
//        public Parcelable saveState() {
//            // do not save
//            return new Bundle();
//        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
            if (mShouldRestore) {
                super.restoreState(state, loader);
            }
        }

    }

}
