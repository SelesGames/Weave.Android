package com.selesgames.weave;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.selesgames.weave.fragments.CategoriesFragment;

public class MainActivity extends BaseActivity {

    @InjectView(R.id.pager)
    ViewPager mViewPager;

    private Adapter mAdapter;

    private List<Fragment> mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
