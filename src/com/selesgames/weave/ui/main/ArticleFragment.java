package com.selesgames.weave.ui.main;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import javax.inject.Inject;

import rx.Scheduler;
import rx.functions.Action1;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import butterknife.InjectView;

import com.selesgames.weave.ForActivity;
import com.selesgames.weave.Formatter;
import com.selesgames.weave.OnMainThread;
import com.selesgames.weave.R;
import com.selesgames.weave.api.MobilizerService;
import com.selesgames.weave.model.Article;
import com.selesgames.weave.model.Feed;
import com.selesgames.weave.model.News;
import com.selesgames.weave.ui.BaseFragment;
import com.selesgames.weave.ui.ObservableWebView;

public class ArticleFragment extends BaseFragment {

    public static final String KEY_FEED = "feed";

    public static final String KEY_NEWS = "news";

    public static final String KEY_ARTICLE = ArticleFragment.class.getCanonicalName() + ".article";

    public static ArticleFragment newInstance(Feed feed, News news) {
        Bundle b = new Bundle();
        b.putParcelable(KEY_FEED, feed);
        b.putParcelable(KEY_NEWS, news);

        ArticleFragment f = new ArticleFragment();
        f.setArguments(b);
        return f;
    }

    @Inject
    @ForActivity
    Context mContext;

    @Inject
    ArticleController mController;

    @Inject
    @OnMainThread
    Scheduler mScheduler;

    @Inject
    MobilizerService mMobilizerService;

    @InjectView(R.id.web)
    ObservableWebView mWebView;

    @InjectView(R.id.progress)
    ProgressBar mProgress;

    private Feed mFeed;

    private News mNews;

    private Article mArticle;

    private Formatter mFormatter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getArguments();
        mFeed = b.getParcelable(KEY_FEED);
        mNews = b.getParcelable(KEY_NEWS);

        // Ensure the article in the saved state matches the one we want to load
        if (savedInstanceState != null) {
            Article article = (Article) savedInstanceState.get(KEY_ARTICLE);
            if (article != null && article.getUrl().equals(mNews.getLink())) {
                mArticle = article;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mArticle == null) {
            mMobilizerService.getArticle(mNews.getLink()).observeOn(mScheduler).subscribe(new Action1<Article>() {

                @Override
                public void call(Article article) {
                    mArticle = article;
                    displayNews();
                    mController.onArticleLoaded(mFeed, mNews, mArticle);
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
        return inflater.inflate(R.layout.fragment_article, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // mWebView.setOnScrollChangedCallback(new OnScrollChangedCallback() {
        //
        // @Override
        // public void onScroll(int l, int t) {
        // LayoutParams params = mHeaderImage.getLayoutParams();
        // Resources r = getResources();
        // int px = r.getDimensionPixelSize(R.dimen.article_header_height);
        // int change = Math.max(0, t);
        // change = Math.min(change, px);
        // params.height = px - change;
        // mHeaderImage.setLayoutParams(params);
        // }
        // });

        displayNews();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mArticle != null) {
            outState.putParcelable(KEY_ARTICLE, mArticle);
        }
    }

    private void displayNews() {
        if (mArticle != null) {
            if (mFormatter == null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("cccc, MMMM dd h:mma", Locale.getDefault());
                dateFormat.setTimeZone(Calendar.getInstance().getTimeZone());
                String publishedAt = dateFormat.format(mArticle.getDatePublished());

                mFormatter = new Formatter(mContext, mFeed.getName(), mArticle.getTitle(), mNews.getImageUrl(),
                        mArticle.getUrl(), publishedAt, "#000000", "#FFFFFF", "Arial", "18px", "#FF0000", "12px");
            }
            String html = mFormatter.createHtml(mArticle.getContent());
            mWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
            mProgress.setVisibility(View.GONE);

        }
    }
}
