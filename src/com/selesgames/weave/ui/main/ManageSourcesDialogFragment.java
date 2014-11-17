package com.selesgames.weave.ui.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import rx.Observer;
import rx.Scheduler;
import rx.functions.Func1;
import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import timber.log.Timber;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.selesgames.weave.ForActivity;
import com.selesgames.weave.OnMainThread;
import com.selesgames.weave.R;
import com.selesgames.weave.WeavePrefs;
import com.selesgames.weave.api.UserService;
import com.selesgames.weave.model.Feed;
import com.selesgames.weave.model.User;
import com.selesgames.weave.ui.BaseDialogFragment;

public class ManageSourcesDialogFragment extends BaseDialogFragment {

    public static ManageSourcesDialogFragment newInstance() {
        ManageSourcesDialogFragment fragment = new ManageSourcesDialogFragment();
        return fragment;
    }

    @Inject
    @ForActivity
    Context mContext;

    @Inject
    UserService mUserService;

    @Inject
    WeavePrefs mPrefs;

    @Inject
    @OnMainThread
    Scheduler mScheduler;

    @InjectView(R.id.list)
    ExpandableStickyListHeadersListView mList;

    @InjectView(R.id.progress)
    ProgressBar mProgress;

    private Adapter mAdapter;

    private List<Feed> mFeeds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_manage_sources_dialog, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set title
        getDialog().setTitle(R.string.settings_manage_sources);

        // Build adapter
        mFeeds = new ArrayList<Feed>();
        mAdapter = new Adapter(mContext, mFeeds);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final FeedMenu menu = new FeedMenu(mContext);
                menu.show(view);
                menu.setOnClickListener(new FeedMenu.OnClickListener() {
                    
                    @Override
                    public void onEdit() {
                        // TODO
                    }
                    
                    @Override
                    public void onDelete() {
                        mUserService.removeFeed(mPrefs.getUserId(), mFeeds.get(position).getId()).observeOn(mScheduler).subscribe(new Observer<Void>() {

                            @Override
                            public void onCompleted() {
                                mFeeds.remove(position);
                                mAdapter.notifyDataSetChanged();
                                menu.dismiss();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Timber.e(e, "Could not remove delete feed");
                                
                                // TODO: Jackson and 204's don't mix. The "end of input" leaves Jackson without anything to convert,
                                // despite this being a Void response. Maybe a jackson/retrofit update will solve?
                                // Act as if no error occurred for now... 
                                onCompleted();
                            }

                            @Override
                            public void onNext(Void response) {
                                
                            }
                        });
                    }
                });
            }
        });
        mList.setAdapter(mAdapter);
        mList.setOnHeaderClickListener(new StickyListHeadersListView.OnHeaderClickListener() {
            @Override
            public void onHeaderClick(StickyListHeadersListView l, View header, int itemPosition, long headerId,
                    boolean currentlySticky) {
                if (mList.isHeaderCollapsed(headerId)) {
                    mList.expand(headerId);
                } else {
                    mList.collapse(headerId);
                }
            }
        });

        mUserService.getInfo(mPrefs.getUserId(), false).map(new Func1<User, List<Feed>>() {

            @Override
            public List<Feed> call(User user) {
                Collections.sort(user.getFeeds(), new Comparator<Feed>() {

                    @Override
                    public int compare(Feed lhs, Feed rhs) {
                        return lhs.getCategory().compareTo(rhs.getCategory());
                    }
                });
                return user.getFeeds();
            }
        }).observeOn(mScheduler).subscribe(new Observer<List<Feed>>() {

            @Override
            public void onCompleted() {
                mProgress.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "Could not load feeds");
                mProgress.setVisibility(View.GONE);
            }

            @Override
            public void onNext(List<Feed> feeds) {
                mFeeds.clear();
                mFeeds.addAll(feeds);
                mAdapter.notifyDataSetChanged();

                // Collapse all headers
                for (int i = 0; i < mAdapter.getCount(); i++) {
                    mList.collapse(mAdapter.getHeaderId(i));
                }
            }
        });
    }

    private static class Adapter extends BaseAdapter implements StickyListHeadersAdapter {
        
        private LayoutInflater mInflater;

        private List<Feed> mFeeds;

        public Adapter(Context context, List<Feed> feeds) {
            mInflater = LayoutInflater.from(context);
            mFeeds = feeds;
        }

        @Override
        public int getCount() {
            return mFeeds.size();
        }

        @Override
        public Object getItem(int position) {
            return mFeeds.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.listitem_feed, parent, false);
            }

            final Feed feed = mFeeds.get(position);
            TextView text = ButterKnife.findById(convertView, R.id.text);
            text.setText(feed.getName());

            return convertView;
        }

        @Override
        public View getHeaderView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.listitem_header, parent, false);
            }

            Feed feed = mFeeds.get(position);
            TextView textView = (TextView) convertView;
            textView.setText(feed.getCategory());

            return convertView;
        }

        @Override
        public long getHeaderId(int position) {
            return mFeeds.get(position).getCategory().hashCode();
        }

    }

}
