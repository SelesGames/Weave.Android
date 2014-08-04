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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.InjectView;

import com.selesgames.weave.ForActivity;
import com.selesgames.weave.OnMainThread;
import com.selesgames.weave.R;
import com.selesgames.weave.WeavePrefs;
import com.selesgames.weave.api.UserService;
import com.selesgames.weave.model.Feed;
import com.selesgames.weave.model.User;
import com.selesgames.weave.ui.BaseFragment;

public class CategoriesFragment extends BaseFragment {

    private static final String KEY_USER = CategoriesFragment.class.getCanonicalName() + ".user";

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

    @InjectView(R.id.list)
    ListView mListView;

    @InjectView(R.id.progress)
    ProgressBar mProgress;

    private Adapter mAdapter;

    private User mUser;

    public static CategoriesFragment newInstance() {
        return new CategoriesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mUser = savedInstanceState.getParcelable(KEY_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mUser == null) {

            mUserService.getInfo(mPrefs.getUserId(), true).observeOn(mScheduler).subscribe(new Action1<User>() {

                @Override
                public void call(User user) {
                    mUser = user;
                    displayCategories();
                }

            }, new Action1<Throwable>() {

                @Override
                public void call(Throwable t) {
                    Log.e("WEAVE", "Could not load categories", t);
                    // TODO: Show error state
                }
            });
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        displayCategories();
    }

    private void displayCategories() {
        if (mUser != null) {
            Map<String, List<Feed>> categoryMap = new HashMap<String, List<Feed>>();
            for (Feed f : mUser.getFeeds()) {
                String category = f.getCategory();
                List<Feed> feeds = categoryMap.get(category);
                if (feeds == null) {
                    feeds = new ArrayList<Feed>();
                    categoryMap.put(category, feeds);
                }
                feeds.add(f);
            }

            mAdapter = new Adapter(mContext, categoryMap);
            mListView.setAdapter(mAdapter);
            mProgress.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mUser != null) {
            outState.putParcelable(KEY_USER, mUser);
        }
    }

    private class Adapter extends BaseAdapter {

        private LayoutInflater mInflater;

        private Map<String, List<Feed>> mCategoryMap;

        private List<String> mCategories;

        public Adapter(Context context, Map<String, List<Feed>> categoryMap) {
            mInflater = LayoutInflater.from(context);
            mCategoryMap = categoryMap;

            mCategories = new ArrayList<String>();
            for (String key : categoryMap.keySet()) {
                mCategories.add(key);
            }
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

            final String category = mCategories.get(position);
            List<Feed> feeds = mCategoryMap.get(category);
            StringBuilder feedsText = new StringBuilder();
            final String SPACER = ", ";
            for (int i = 0; i < feeds.size() && i < 5; i++) {
                Feed f = feeds.get(i);
                feedsText.append(f.getName());
                feedsText.append(SPACER);
            }

            ((TextView) convertView.findViewById(R.id.name)).setText(category);
            ((TextView) convertView.findViewById(R.id.feeds)).setText(feedsText.substring(0, feedsText.length()
                    - SPACER.length() - 1));

            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    mController.onCategorySelected(category);
                }
            });

            return convertView;
        }

    }

}
