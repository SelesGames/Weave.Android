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
import android.support.v4.view.ViewPager;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.selesgames.weave.ForActivity;
import com.selesgames.weave.R;
import com.selesgames.weave.WeavePrefs;
import com.selesgames.weave.ui.BaseActivity;
import com.selesgames.weave.ui.onboarding.OnboardingActivity;

public class MainActivity extends BaseActivity {

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

        // TODO: If first load, start onboarding

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

    }
}
