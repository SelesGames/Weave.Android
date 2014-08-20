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
import butterknife.ButterKnife;
import butterknife.InjectView;

import com.selesgames.weave.ForActivity;
import com.selesgames.weave.R;
import com.selesgames.weave.ui.BaseFragment;
import com.selesgames.weave.ui.main.NewsGroup.NewsItem;
import com.squareup.picasso.Picasso;

public class NewsGroupFragment extends BaseFragment {

    private static final String KEY_GROUP = "group";
    
    public static NewsGroupFragment newInstance(NewsGroup group) {
        Bundle b = new Bundle();
        b.putParcelable(KEY_GROUP, group);

        NewsGroupFragment f = new NewsGroupFragment();
        f.setArguments(b);
        return f;
    }

    @Inject
    @ForActivity
    Context mContext;

    @Inject
    NewsController mController;
    
    @Inject
    Picasso mPicasso;
    
    @InjectView(R.id.progress)
    ProgressBar mProgress;
    
    @InjectView(R.id.container)
    ViewGroup mContainer;

    private NewsGroup mGroup;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getArguments();
        mGroup = b.getParcelable(KEY_GROUP);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_group, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        LayoutInflater inflater = LayoutInflater.from(mContext);
        
        for (final NewsItem item : mGroup.getNews()) {
            View view = inflater.inflate(R.layout.news_group_item, mContainer, false);
            mContainer.addView(view);
            
            TextView title = ButterKnife.findById(view, R.id.title);
            title.setText(item.news.getTitle());

            TextView source = ButterKnife.findById(view, R.id.source);
            source.setText(item.feed.getName());

            TextView time = ButterKnife.findById(view, R.id.time);
            long date = item.news.getOriginalDownloadDateTime().getTime();
            CharSequence formattedTime = DateUtils.getRelativeTimeSpanString(mContext, date, true);
            time.setText(formattedTime);

            ImageView image = ButterKnife.findById(view, R.id.image);
            String imageUrl = item.news.getImageUrl();
            if (TextUtils.isEmpty(imageUrl)) {
                image.setVisibility(View.GONE);
            } else {
                image.setVisibility(View.VISIBLE);
                mPicasso.load(imageUrl).fit().centerCrop().into(image);
            }
            
            view.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    mController.onNewsSelected(item.feed, item.news);
                }
            });
        }
    }

}
