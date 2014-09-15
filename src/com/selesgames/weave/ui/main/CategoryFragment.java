package com.selesgames.weave.ui.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import butterknife.InjectView;

import com.selesgames.weave.ForActivity;
import com.selesgames.weave.OnComputationThread;
import com.selesgames.weave.OnMainThread;
import com.selesgames.weave.R;
import com.selesgames.weave.WeavePrefs;
import com.selesgames.weave.api.UserService;
import com.selesgames.weave.model.CategoryNews;
import com.selesgames.weave.model.Feed;
import com.selesgames.weave.model.News;
import com.selesgames.weave.ui.BaseFragment;
import com.selesgames.weave.ui.main.NewsGroup.NewsItem;
import com.selesgames.weave.view.VerticalViewPager;
import com.squareup.picasso.Picasso;

public class CategoryFragment extends BaseFragment {

    private static final String KEY_CATEGORY_ID = CategoryFragment.class.getCanonicalName() + ".category_id";

    private static final String KEY_NEWS_GROUPS = CategoryFragment.class.getCanonicalName() + ".news_groups";

    private static final String KEY_FEEDS = CategoryFragment.class.getCanonicalName() + ".feeds";

    private static final int NEWS_ITEM_TAKE = 20;

    /** Number of pages away from end before more news loads */
    private static final int LOAD_MORE_THRESHOLD = 4;

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
    CategoryController mController;

    @Inject
    @OnMainThread
    Scheduler mMainScheduler;

    @Inject
    @OnComputationThread
    Scheduler mComputationScheduler;

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

    private List<Feed> mFeeds;

    private Map<String, Feed> mFeedMap;

    private List<NewsGroup> mNewsGroups;

    private int mLastPage;

    private boolean mLoading;

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mFeedMap = new HashMap<String, Feed>();

        boolean restoreState = savedInstanceState == null
                || mCategoryId.equals(savedInstanceState.getString(KEY_CATEGORY_ID));

        if (savedInstanceState == null || !restoreState) {
            mNewsGroups = new ArrayList<NewsGroup>();
            mFeeds = new ArrayList<Feed>();
        } else {
            mNewsGroups = savedInstanceState.getParcelableArrayList(KEY_NEWS_GROUPS);
            mFeeds = savedInstanceState.getParcelableArrayList(KEY_FEEDS);
            populateFeedMap(mFeeds);
        }

        mAdapter = new Adapter(getChildFragmentManager(), mNewsGroups, getResources().getInteger(R.integer.display_ad_every_n_news), restoreState);
        mViewPager.setAdapter(mAdapter);
        // mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        // The page change listener is set here because setting it before the
        // view is restored will trigger a page change. We can remove this hack
        // if we maintain better state in the controller and be more selective
        // in handling this event. (i.e. page change when restoring state, but
        // we don't want the article loaded)
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if (mAdapter.isNewsItemPosition(position)) {
                    NewsGroup group = mNewsGroups.get(mAdapter.getOffsetNewsPosition(position));
                    if (group.getNews().size() == 1) { 
                        NewsItem item = group.getNews().get(0);
                        mController.onNewsFocussed(item.feed, item.news);
                    } else {
                        mController.onNewsUnfocussed();
                    }
                } else {
                    mController.onNewsUnfocussed();
                }

                // Load more
                if (mAdapter.getCount() - 1 - position < LOAD_MORE_THRESHOLD && !mLoading) {
                    mLoading = true;
                    loadNews(++mLastPage).observeOn(mMainScheduler).subscribe(new Action1<List<NewsGroup>>() {

                        @Override
                        public void call(List<NewsGroup> groups) {
                            mNewsGroups.addAll(groups);
                            mAdapter.notifyDataSetChanged();
                        }

                    }, new Action1<Throwable>() {

                        @Override
                        public void call(Throwable t) {
                            Timber.e("Could not load news", t);
                        }
                    }, new Action0() {

                        @Override
                        public void call() {
                            mLoading = false;
                        }
                    });
                    ;
                }
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

        outState.putString(KEY_CATEGORY_ID, mCategoryId);
        outState.putParcelableArrayList(KEY_NEWS_GROUPS, (ArrayList<NewsGroup>) mNewsGroups);
        outState.putParcelableArrayList(KEY_FEEDS, (ArrayList<Feed>) mFeeds);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mNewsGroups.isEmpty()) {
            // Load first page
            mLoading = true;
            loadNews(0).observeOn(mMainScheduler).subscribe(new Action1<List<NewsGroup>>() {

                @Override
                public void call(List<NewsGroup> groups) {
                    mProgress.setVisibility(View.GONE);

                    mNewsGroups.addAll(groups);
                    mAdapter.notifyDataSetChanged();

                    // Focus the first item
                    NewsGroup group = mNewsGroups.get(0);
                    if (group.getNews().size() == 1) {
                        NewsItem item = group.getNews().get(0);
                        mController.onNewsFocussed(item.feed, item.news);
                    }
                }

            }, new Action1<Throwable>() {

                @Override
                public void call(Throwable t) {
                    Log.e("WEAVE", "Could not load news", t);
                }
            }, new Action0() {

                @Override
                public void call() {
                    mLoading = false;
                }
            });
        } else {
            mProgress.setVisibility(View.GONE);
        }
    }

    private Observable<List<NewsGroup>> loadNews(int page) {
        return mUserService
                .getFeedsForCategory(mPrefs.getUserId(), mCategoryId, "Mark", NEWS_ITEM_TAKE * page, NEWS_ITEM_TAKE)
                .observeOn(mComputationScheduler).map(new Func1<CategoryNews, List<NewsGroup>>() {

                    @Override
                    public List<NewsGroup> call(CategoryNews categoryNews) {
                        List<NewsGroup> groups = new ArrayList<NewsGroup>();

                        List<Feed> feeds = categoryNews.getFeeds();
                        mFeeds.removeAll(feeds);
                        mFeeds.addAll(feeds);

                        populateFeedMap(categoryNews.getFeeds());

                        List<News> news = categoryNews.getNews();

                        for (News n : categoryNews.getNews()) {
                            Picasso.with(mContext).load(n.getImageUrl()).fetch();
                        }

                        int i = 0;
                        while (i < news.size()) {
                            NewsGroup group = new NewsGroup();
                            News n = news.get(i);

                            // Check next two items
                            News newsOne = null;
                            News newsTwo = null;
                            if (i + 1 < news.size()) {
                                newsOne = news.get(i + 1);
                            }
                            if (i + 2 < news.size()) {
                                newsTwo = news.get(i + 2);
                            }

                            // Always add first news item
                            group.addNews(n, mFeedMap.get(n.getFeedId()));

                            boolean emptyZero = TextUtils.isEmpty(n.getImageUrl());
                            boolean emptyOne = newsOne != null && TextUtils.isEmpty(newsOne.getImageUrl());
                            boolean emptyTwo = newsTwo != null && TextUtils.isEmpty(newsTwo.getImageUrl());
                            if (emptyZero || emptyOne || emptyTwo) {
                                if (newsOne != null) {
                                    group.addNews(newsOne, mFeedMap.get(newsOne.getFeedId()));
                                }
                                if (newsTwo != null) {
                                    group.addNews(newsTwo, mFeedMap.get(newsTwo.getFeedId()));
                                }
                            }

                            groups.add(group);

                            i += group.getNews().size();
                        }

                        return groups;
                    }

                });
    }

    private void populateFeedMap(List<Feed> feeds) {
        for (Feed f : feeds) {
            mFeedMap.put(f.getId(), f);
        }
    }

    private static class Adapter extends FragmentStatePagerAdapter {

        private List<NewsGroup> mNewsGroups;

        private int mDistanceBetweenAds;
        
        private boolean mRestoreState;

        public Adapter(FragmentManager fm, List<NewsGroup> newsGroups, int distanceBetweenAds, boolean restoreState) {
            super(fm);
            mNewsGroups = newsGroups;
            mDistanceBetweenAds = distanceBetweenAds;
            mRestoreState = restoreState;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f;
            if (isNewsItemPosition(position)) {
                int newsPosition = getOffsetNewsPosition(position);
                NewsGroup group = mNewsGroups.get(newsPosition);
                List<NewsItem> items = group.getNews();
                if (items.size() == 1) {
                    NewsItem item = items.get(0);
                    f = NewsFragment.newInstance(item.feed, item.news);
                } else {
                    f = NewsGroupFragment.newInstance(group);
                }
            } else {
                f = AdFragment.newInstance();
            }
            return f;
        }

        @Override
        public int getCount() {
            int newsCount = mNewsGroups.size();
            int adCount = mDistanceBetweenAds > 0 ? newsCount / mDistanceBetweenAds : 0;
            return newsCount + adCount;
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
            if (mRestoreState) {
                super.restoreState(state, loader);
            }
        }
        
        public boolean isNewsItemPosition(int position) {
            return position == 0 || mDistanceBetweenAds == 0 || position % mDistanceBetweenAds != 0;
        }
        
        public int getOffsetNewsPosition(int position) {
            int newsOffset = mDistanceBetweenAds > 0 ? position / mDistanceBetweenAds : 0;
            return position - newsOffset;
        }

    }

}
