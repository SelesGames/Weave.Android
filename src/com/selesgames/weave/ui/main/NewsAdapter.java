package com.selesgames.weave.ui.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.selesgames.weave.R;
import com.selesgames.weave.model.CategoryNews;
import com.selesgames.weave.model.Feed;
import com.selesgames.weave.model.News;
import com.squareup.picasso.Picasso;

public class NewsAdapter extends BaseAdapter {

    public interface ClickListener {
        void onItemClicked(Feed feed, News news);
    }

    private Context mContext;

    private LayoutInflater mInflater;

    private Picasso mPicasso;

    private CategoryNews mFeed;

    private List<News> mNews;

    private Map<String, Feed> mFeedMap;

    private ClickListener mClickListener;

    public NewsAdapter(Context context, Picasso picasso, CategoryNews categoryNews) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mPicasso = picasso;
        mFeed = categoryNews;
        mNews = categoryNews.getNews();

        mFeedMap = new HashMap<String, Feed>();
        for (Feed f : categoryNews.getFeeds()) {
            mFeedMap.put(f.getId(), f);
        }
    }

    public void setOnClickListener(ClickListener listener) {
        mClickListener = listener;
    }

    @Override
    public int getCount() {
        return mFeed.getNews().size();
    }

    @Override
    public Object getItem(int position) {
        return mFeed.getNews().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listitem_news, parent, false);
        }

        final News news = mNews.get(position);

        ((TextView) convertView.findViewById(R.id.title)).setText(news.getTitle());

        String source = mFeedMap.get(news.getFeedId()).getName();
        ((TextView) convertView.findViewById(R.id.source)).setText(source);

        long date = news.getOriginalDownloadDateTime().getTime();
        CharSequence time = DateUtils.getRelativeTimeSpanString(mContext, date, true);
        ((TextView) convertView.findViewById(R.id.time)).setText(time);

        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        String imageUrl = news.getImageUrl();
        if (TextUtils.isEmpty(imageUrl)) {
            image.setVisibility(View.GONE);
        } else {
            image.setVisibility(View.VISIBLE);
            mPicasso.load(imageUrl).fit().centerCrop().into(image);
        }

        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    Feed feed = mFeedMap.get(news.getFeedId());
                    mClickListener.onItemClicked(feed, news);
                }
            }
        });

        return convertView;
    }
}