package com.selesgames.weave.ui.main;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.selesgames.weave.ForActivity;
import com.selesgames.weave.R;
import com.selesgames.weave.WeavePrefs;
import com.selesgames.weave.model.Article;
import com.selesgames.weave.model.Feed;
import com.selesgames.weave.model.News;
import com.selesgames.weave.modules.ActivityModule;
import com.selesgames.weave.ui.BaseActivity;
import com.selesgames.weave.ui.onboarding.OnboardingActivity;

import dagger.Module;
import dagger.Provides;

public class MainActivity extends BaseActivity implements SettingsController, CategoriesController, CategoryController,
        NewsController, ArticleController {

    private static final String KEY_CATEGORY = MainActivity.class.getCanonicalName() + ".category";

    private static final String KEY_FEED = MainActivity.class.getCanonicalName() + ".feed";

    private static final String KEY_NEWS = MainActivity.class.getCanonicalName() + ".news";

    private static final String KEY_ARTICLE = MainActivity.class.getCanonicalName() + ".article";

    @Inject
    @ForActivity
    Context mContext;

    @Inject
    WeavePrefs mPrefs;

    @Inject
    Tracker mTracker;

    @InjectView(R.id.pager)
    ViewPager mViewPager;

    private Adapter mAdapter;

    private List<Fragment> mFragments;

    private String mCategory;

    private Feed mFeed;

    private News mNews;

    private Article mArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the theme
        setTheme(mPrefs.getThemeId() == 0 ? R.style.AppThemeLight : R.style.AppThemeDark);

        // Analytics
        mTracker.setScreenName(getClass().getSimpleName());
        mTracker.send(new HitBuilders.AppViewBuilder().build());

        if (mPrefs.getUserId() == null) {
            startActivity(new Intent(mContext, OnboardingActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        // Load fragments
        // mFragments.add(ArticleActionsFragment.newInstance(null, null, null));
        mFragments = new ArrayList<Fragment>();
        mFragments.add(SettingsFragment.newInstance());
        Fragment categoriesFragment = CategoriesFragment.newInstance();
        mFragments.add(categoriesFragment);
        if (savedInstanceState != null) {
            mCategory = savedInstanceState.getString(KEY_CATEGORY);
            if (mCategory != null) {
                mFragments.add(CategoryFragment.newInstance(mCategory));
            }

            mFeed = savedInstanceState.getParcelable(KEY_FEED);
            mNews = savedInstanceState.getParcelable(KEY_NEWS);
            if (mFeed != null && mNews != null) {
                mFragments.add(ArticleFragment.newInstance(mFeed, mNews));

                mArticle = savedInstanceState.getParcelable(KEY_ARTICLE);
                if (mArticle != null) {
                    mFragments.add(ArticleActionsFragment.newInstance(mFeed, mNews, mArticle));
                }
            }
        }

        mAdapter = new Adapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mFragments.indexOf(categoriesFragment), false);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                Fragment f = mFragments.get(position);
                if (f instanceof OnFragmentSelectedListener) {
                    ((OnFragmentSelectedListener) f).onFragmentSelected();
                }

                mTracker.setScreenName(f.getClass().getSimpleName());
                mTracker.send(new HitBuilders.AppViewBuilder().build());
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(KEY_CATEGORY, mCategory);
        outState.putParcelable(KEY_FEED, mFeed);
        outState.putParcelable(KEY_NEWS, mNews);
        outState.putParcelable(KEY_ARTICLE, mArticle);
    }

    // Custom pager
    // https://github.com/JakeWharton/adjacent-fragment-pager-sample/blob/master/src/main/java/com/jakewharton/example/ExampleActivity.java
    private static class Adapter extends FragmentStatePagerAdapter {

        private List<Fragment> mFragments;

        public Adapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            mFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            Log.d("TEST", "getItem: " + position);
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public int getItemPosition(Object object) {
            Fragment f = (Fragment) object;
            for (Fragment frag : mFragments) {
                if (frag.getClass().getCanonicalName().equals(f.getClass().getCanonicalName())) {
                    Log.d("TEST", "getItemPosition: UNCHANGED");
                    return PagerAdapter.POSITION_UNCHANGED;
                }
            }
            Log.d("TEST", "getItemPosition: NONE");
            return PagerAdapter.POSITION_NONE;
            // return mFragments.indexOf(object) > -1 ?
            // PagerAdapter.POSITION_UNCHANGED : PagerAdapter.POSITION_NONE;
        }

    }

    @Override
    public void onThemeChanged() {
        recreate();
    }

    @Override
    public void manageSources() {
        ManageSourcesDialogFragment fragment = ManageSourcesDialogFragment.newInstance();
        fragment.show(getSupportFragmentManager(), null);
    }

    @Override
    public void onCategorySelected(String category) {
        int currentIndex = mViewPager.getCurrentItem();
        if (mCategory != null && mCategory.equals(category)) {
            int position = currentIndex + 1;
            if (position < mAdapter.getCount()) {
                mViewPager.setCurrentItem(position, true);
            }
        } else {
            // Keep for saved state
            mCategory = category;

            // Remove anything to the right
            for (int i = mFragments.size() - 1; i > currentIndex; i--) {
                mFragments.remove(i);
            }

            // Must notify data set changed twice so that fragment is actually
            // removed (comparison is done using fragment class name).
            // Alternatively we could add a method to the fragment to load a new
            // item rather than destroying and recreating it.
            mAdapter.notifyDataSetChanged();

            // Add new view
            Fragment f = CategoryFragment.newInstance(category);
            mFragments.add(f);
            mAdapter.notifyDataSetChanged();
            mViewPager.setCurrentItem(mAdapter.getCount() - 1, true);
        }
    }

    @Override
    public void onNewsFocussed(Feed feed, News news) {
        // Keep for saved state
        mFeed = feed;
        mNews = news;

        // Remove anything to the right
        int currentIndex = mViewPager.getCurrentItem();
        for (int i = mFragments.size() - 1; i > currentIndex; i--) {
            mFragments.remove(i);
        }

        // Must notify data set changed twice so that fragment is actually
        // removed (comparison is done using fragment class name).
        // Alternatively we could add a method to the fragment to load a new
        // item rather than destroying and recreating it.
        mAdapter.notifyDataSetChanged();

        Fragment f = ArticleFragment.newInstance(feed, news);
        mFragments.add(f);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNewsUnfocussed() {
        // Set for saved state
        mFeed = null;
        mNews = null;

        // Remove anything to the right
        int currentIndex = mViewPager.getCurrentItem();
        for (int i = mFragments.size() - 1; i > currentIndex; i--) {
            mFragments.remove(i);
        }
        mAdapter.notifyDataSetChanged();
    }

    // TODO: Keep track of currently shown news and only load a new fragment
    // when necessary
    @Override
    public void onNewsSelected(Feed feed, News news) {
        int currentIndex = mViewPager.getCurrentItem();
        if (mFeed != null && mFeed.equals(feed) && mNews != null && mNews.equals(news)) {
            int position = currentIndex + 1;
            if (position < mAdapter.getCount()) {
                mViewPager.setCurrentItem(position, true);
            }
        } else {
            // Keep for saved state
            mFeed = feed;
            mNews = news;

            // Remove anything to the right
            for (int i = mFragments.size() - 1; i > currentIndex; i--) {
                mFragments.remove(i);
            }

            // Must notify data set changed twice so that fragment is actually
            // removed (comparison is done using fragment class name).
            // Alternatively we could add a method to the fragment to load a new
            // item rather than destroying and recreating it.
            mAdapter.notifyDataSetChanged();

            Fragment f = ArticleFragment.newInstance(feed, news);
            mFragments.add(f);
            mAdapter.notifyDataSetChanged();
            mViewPager.setCurrentItem(mAdapter.getCount() - 1, true);
        }
    }

    @Override
    public void onArticleLoaded(Feed feed, News news, Article article) {
        mArticle = article;
        Fragment f = ArticleActionsFragment.newInstance(feed, news, article);
        mFragments.add(f);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected List<Object> getModules() {
        List<Object> modules = new ArrayList<Object>(super.getModules());
        modules.add(new MainActivityModule());
        return modules;
    }

    @Module(injects = { MainActivity.class, SettingsFragment.class, ManageSourcesDialogFragment.class,
            CategoriesFragment.class, CategoryFragment.class, NewsFragment.class, NewsGroupFragment.class,
            ArticleFragment.class, ArticleActionsFragment.class, AdFragment.class }, addsTo = ActivityModule.class)
    public class MainActivityModule {

        @Provides
        SettingsController provideSettingsController() {
            return MainActivity.this;
        }

        @Provides
        CategoriesController provideCategoriesController() {
            return MainActivity.this;
        }

        @Provides
        CategoryController provideCategoryController() {
            return MainActivity.this;
        }

        @Provides
        NewsController provideNewsController() {
            return MainActivity.this;
        }

        @Provides
        ArticleController provideArticleController() {
            return MainActivity.this;
        }

        @Provides
        Point provideScreenSize() {
            Point point = new Point();
            Display display = getWindowManager().getDefaultDisplay();
            display.getSize(point);
            return point;
        }
    }

    @Override
    public void onBackPressed() {
        if (mViewPager == null || mViewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
        }
    }

}
