package com.selesgames.weave.fragments;

import java.util.List;

import javax.inject.Inject;

import rx.Scheduler;
import rx.functions.Action1;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.InjectView;

import com.selesgames.weave.BaseFragment;
import com.selesgames.weave.ForActivity;
import com.selesgames.weave.MainThread;
import com.selesgames.weave.R;
import com.selesgames.weave.api.CategoryService;
import com.selesgames.weave.model.Category;
import com.selesgames.weave.model.CategoryFeed;

public class CategoriesFragment extends BaseFragment {

    @Inject
    @ForActivity
    Context mContext;

    @Inject
    @MainThread
    Scheduler mScheduler;

    @Inject
    CategoryService mCategoryService;

    @InjectView(R.id.list)
    ListView mListView;

    Adapter mAdapter;

    public static CategoriesFragment newInstance() {
        return new CategoriesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }

    @Override
    public void onStart() {
        super.onStart();

        if (mAdapter == null) {
            mCategoryService.getCategories().observeOn(mScheduler).subscribe(new Action1<CategoryFeed>() {

                @Override
                public void call(CategoryFeed feed) {
                    mAdapter = new Adapter(mContext, feed.getCategories());
                    mListView.setAdapter(mAdapter);
                }

            }, new Action1<Throwable>() {

                @Override
                public void call(Throwable t) {
                    Log.e("WEAVE", "Could not load categories", t);
                }
            });
        }
    }

    private static class Adapter extends BaseAdapter {

        private LayoutInflater mInflater;

        private List<Category> mCategories;

        public Adapter(Context context, List<Category> categories) {
            mInflater = LayoutInflater.from(context);
            mCategories = categories;
        }

        @Override
        public int getCount() {
            return mCategories.size();
        }

        @Override
        public Object getItem(int position) {
            return mCategories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.listitem_category, parent, false);
            }

            Category category = mCategories.get(position);

            ((TextView) convertView.findViewById(R.id.name)).setText(category.getType());

            return convertView;
        }

    }

}
