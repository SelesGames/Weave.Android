package com.selesgames.weave.ui.main;

import javax.inject.Inject;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.InjectView;

import com.selesgames.weave.ForActivity;
import com.selesgames.weave.R;
import com.selesgames.weave.WeavePrefs;
import com.selesgames.weave.model.Feed;
import com.selesgames.weave.model.News;
import com.selesgames.weave.ui.BaseFragment;
import com.squareup.picasso.Picasso;

public class NewsFragment extends BaseFragment {

    private static final String KEY_FEED = "feed";
    
    private static final String KEY_NEWS = "news";

    public static NewsFragment newInstance(Feed feed, News news) {
        Bundle b = new Bundle();
        b.putParcelable(KEY_FEED, feed);
        b.putParcelable(KEY_NEWS, news);

        NewsFragment f = new NewsFragment();
        f.setArguments(b);
        return f;
    }

    @Inject
    @ForActivity
    Context mContext;

    @Inject
    NewsController mController;

    @Inject
    WeavePrefs mPrefs;

    @Inject
    Picasso mPicasso;

    @InjectView(R.id.progress)
    ProgressBar mProgress;
    
    @InjectView(R.id.title)
    TextView mTitle;
    
    @InjectView(R.id.source)
    TextView mSource;
    
    @InjectView(R.id.time)
    TextView mTime;
    
    @InjectView(R.id.image)
    ImageView mImage;

    private Feed mFeed;
    
    private News mNews;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getArguments();
        mFeed = b.getParcelable(KEY_FEED);
        mNews = b.getParcelable(KEY_NEWS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTitle.setText(mNews.getTitle());

        mSource.setText(mFeed.getName());

        long date = mNews.getOriginalDownloadDateTime().getTime();
        CharSequence time = DateUtils.getRelativeTimeSpanString(mContext, date, true);
        mTime.setText(time);

        String imageUrl = mNews.getImageUrl();
        if (TextUtils.isEmpty(imageUrl)) {
            mImage.setVisibility(View.GONE);
        } else {
            mImage.setVisibility(View.VISIBLE);
            mPicasso.load(imageUrl).fit().centerCrop().into(mImage);
        }

        getView().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mController.onNewsSelected(mFeed, mNews);
            }
        });
    }

}
