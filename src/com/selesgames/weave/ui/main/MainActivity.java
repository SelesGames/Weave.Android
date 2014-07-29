package com.selesgames.weave.ui.main;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.selesgames.weave.ForActivity;
import com.selesgames.weave.R;
import com.selesgames.weave.WeavePrefs;
import com.selesgames.weave.model.News;
import com.selesgames.weave.modules.ActivityModule;
import com.selesgames.weave.ui.BaseActivity;
import com.selesgames.weave.ui.onboarding.OnboardingActivity;

import dagger.Module;
import dagger.Provides;

public class MainActivity extends BaseActivity implements CategoriesController {

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
        mFragments.add(CategoriesFragment.newInstance());

        mAdapter = new Adapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mAdapter);
    }

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
    public void onNewsSelected(News news) {
        // Remove anything to the right
        int currentIndex = mViewPager.getCurrentItem();
        for (int i = mFragments.size() - 1; i > currentIndex; i--) {
            mFragments.remove(i);
        }
        
        Fragment f = NewsFragment.newInstance(news);
        mFragments.add(f);
        mAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(mAdapter.getCount() - 1, true);
    }

    @Override
    protected List<Object> getModules() {
        List<Object> modules = new ArrayList<Object>(super.getModules());
        modules.add(new MainActivityModule());
        return modules;
    }

    @Module(injects = { MainActivity.class, CategoriesFragment.class, CategoryFragment.class, NewsFragment.class }, addsTo = ActivityModule.class)
    public class MainActivityModule {

        @Provides
        CategoriesController provideController() {
            return MainActivity.this;
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
