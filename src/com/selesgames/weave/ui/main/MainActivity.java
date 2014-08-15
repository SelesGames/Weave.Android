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
import android.view.Display;
import butterknife.ButterKnife;
import butterknife.InjectView;

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

public class MainActivity extends BaseActivity implements CategoriesController, CategoryController, NewsController, ArticleController {

    @Inject
    @ForActivity
    Context mContext;

    @Inject
    WeavePrefs mPrefs;

    @InjectView(R.id.pager)
    ViewPager mViewPager;

    private Adapter mAdapter;

    private List<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mPrefs.getUserId() == null) {
            startActivity(new Intent(mContext, OnboardingActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        mFragments = new ArrayList<Fragment>();
//        mFragments.add(ArticleActionsFragment.newInstance(null, null, null));
        mFragments.add(CategoriesFragment.newInstance());

        mAdapter = new Adapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                Fragment f = mFragments.get(position);
                if (f instanceof OnFragmentSelectedListener) {
                    ((OnFragmentSelectedListener) f).onFragmentSelected();
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
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public int getItemPosition(Object object) {
            return mFragments.indexOf(object) > -1 ? PagerAdapter.POSITION_UNCHANGED : PagerAdapter.POSITION_NONE;
        }

    }

    @Override
    public void onCategorySelected(String category) {
        // Remove anything to the right
        int currentIndex = mViewPager.getCurrentItem();
        for (int i = mFragments.size() - 1; i > currentIndex; i--) {
            mFragments.remove(i);
        }

        // Add new view
        Fragment f = CategoryFragment.newInstance(category);
        mFragments.add(f);
        mAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(mAdapter.getCount() - 1, true);
    }
    
    @Override
    public void onNewsFocussed(Feed feed, News news) {
     // Remove anything to the right
        int currentIndex = mViewPager.getCurrentItem();
        for (int i = mFragments.size() - 1; i > currentIndex; i--) {
            mFragments.remove(i);
        }

        Fragment f = ArticleFragment.newInstance(feed, news);
        mFragments.add(f);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNewsSelected(Feed feed, News news) {
        // Remove anything to the right
//        int currentIndex = mViewPager.getCurrentItem();
//        for (int i = mFragments.size() - 1; i > currentIndex; i--) {
//            mFragments.remove(i);
//        }
//
//        Fragment f = ArticleFragment.newInstance(feed, news);
//        mFragments.add(f);
//        mAdapter.notifyDataSetChanged();
//        mViewPager.setCurrentItem(mAdapter.getCount() - 1, true);
        
        int position = mViewPager.getCurrentItem() + 1;
        if (position < mAdapter.getCount()) {
            mViewPager.setCurrentItem(position, true);
        }
    }

    @Override
    public void onArticleLoaded(Feed feed, News news, Article article) {
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

    @Module(injects = { MainActivity.class, CategoriesFragment.class, CategoryFragment.class, NewsFragment.class, ArticleFragment.class,
            ArticleActionsFragment.class }, addsTo = ActivityModule.class)
    public class MainActivityModule {

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
