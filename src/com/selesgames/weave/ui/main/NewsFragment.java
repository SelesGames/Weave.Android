package com.selesgames.weave.ui.main;

import javax.inject.Inject;

import rx.Scheduler;
import rx.functions.Action1;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ProgressBar;
import butterknife.InjectView;

import com.selesgames.weave.ForActivity;
import com.selesgames.weave.OnMainThread;
import com.selesgames.weave.R;
import com.selesgames.weave.api.MobilizerService;
import com.selesgames.weave.model.News;
import com.selesgames.weave.model.NewsItem;
import com.selesgames.weave.ui.BaseFragment;

public class NewsFragment extends BaseFragment {

    public static final String KEY_NEWS = "news";

    public static final String KEY_NEWS_ITEM = "news_item";

    public static NewsFragment newInstance(News news) {
        Bundle b = new Bundle();
        b.putParcelable(KEY_NEWS, news);

        NewsFragment f = new NewsFragment();
        f.setArguments(b);
        return f;
    }

    @Inject
    @ForActivity
    Context mContext;

    @Inject
    CategoriesController mController;

    @Inject
    @OnMainThread
    Scheduler mScheduler;

    @Inject
    MobilizerService mMobilizerService;

    @InjectView(R.id.web)
    WebView mWebView;

    @InjectView(R.id.progress)
    ProgressBar mProgress;

    private News mNews;

    private NewsItem mNewsItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getArguments();
        mNews = b.getParcelable(KEY_NEWS);

        // Ensure the news item in the saved state matches the news we want to
        // load
        if (savedInstanceState != null) {
            NewsItem item = (NewsItem) savedInstanceState.get(KEY_NEWS_ITEM);
            if (item != null && item.getUrl().equals(mNews.getLink())) {
                mNewsItem = item;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mNewsItem == null) {
            mMobilizerService.getPage(mNews.getLink()).observeOn(mScheduler).subscribe(new Action1<NewsItem>() {

                @Override
                public void call(NewsItem item) {
                    mNewsItem = item;
                    displayNews();
                }

            }, new Action1<Throwable>() {

                @Override
                public void call(Throwable t) {
                    Log.e("WEAVE", "Could not load news", t);
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        displayNews();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mNewsItem != null) {
            outState.putParcelable(KEY_NEWS_ITEM, mNewsItem);
        }
    }

    private void displayNews() {
        if (mNewsItem != null) {
            mWebView.loadDataWithBaseURL(null, mNewsItem.getContent(), "text/html", "utf-8", null);
            mProgress.setVisibility(View.GONE);
        }
    }

}
