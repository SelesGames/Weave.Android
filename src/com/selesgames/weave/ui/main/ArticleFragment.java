package com.selesgames.weave.ui.main;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action1;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.webkit.WebView;
import android.widget.ProgressBar;
import butterknife.InjectView;
import butterknife.OnClick;

import com.selesgames.weave.ForActivity;
import com.selesgames.weave.Formatter;
import com.selesgames.weave.OnMainThread;
import com.selesgames.weave.R;
import com.selesgames.weave.WeavePrefs;
import com.selesgames.weave.api.MobilizerService;
import com.selesgames.weave.model.Article;
import com.selesgames.weave.model.Feed;
import com.selesgames.weave.model.News;
import com.selesgames.weave.ui.BaseFragment;

public class ArticleFragment extends BaseFragment {

    public static final String KEY_FEED = "feed";

    public static final String KEY_NEWS = "news";

    public static final String KEY_ARTICLE = ArticleFragment.class.getCanonicalName() + ".article";

    private static final int SETTINGS_PANEL_CLOSE_TIMEOUT_MS = 3000;

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

    @Inject
    WeavePrefs mPrefs;

    @InjectView(R.id.web)
    WebView mWebView;

    @InjectView(R.id.progress)
    ProgressBar mProgress;

    @InjectView(R.id.settings_panel)
    View mSettingsPanel;

    @InjectView(R.id.marker)
    View mMarker;

    @InjectView(R.id.cog)
    View mCog;

    private Feed mFeed;

    private News mNews;

    private Article mArticle;

    private float mSettingsOffscreenAlpha;

    private int mSettingsOffscreenWidth;

    private Subscription mSettingsPanelCloseSubscription;

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

        getView().getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    getView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                mSettingsOffscreenAlpha = mSettingsPanel.getAlpha();

                mSettingsOffscreenWidth = (int) mMarker.getX();
                mSettingsPanel.setX(-mSettingsOffscreenWidth);
            }
        });

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
            SimpleDateFormat dateFormat = new SimpleDateFormat("cccc, MMMM dd h:mma", Locale.getDefault());
            dateFormat.setTimeZone(Calendar.getInstance().getTimeZone());
            Date publishedDate = mArticle.getDatePublished();
            String publishedAt = "";
            if (publishedDate != null) {
                publishedAt = dateFormat.format(publishedDate);
            }

            TypedValue typedValue = new TypedValue();
            Theme theme = mContext.getTheme();
            // Background color
            theme.resolveAttribute(R.attr.backgroundColor, typedValue, true);
            String backgroundColor = String.format("#%06X", (0xFFFFFF & typedValue.data));
            // Text color
            theme.resolveAttribute(R.attr.textColor, typedValue, true);
            String textColor = String.format("#%06X", (0xFFFFFF & typedValue.data));
            String textSize = mPrefs.getReadingFontSize() + "px";

            Formatter formatter = new Formatter(mContext, mFeed.getName(), mArticle.getTitle(), mNews.getImageUrl(),
                    mArticle.getUrl(), publishedAt, textColor, backgroundColor, "Arial", textSize, "#FF0000", "12px");

            String html = formatter.createHtml(mArticle.getContent());
            // mWebView.getSettings().setTextZoom(textZoom);
            // mWebView.getSettings().setDefaultFontSize(size);
            mWebView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
            mProgress.setVisibility(View.GONE);

        }
    }

    private void resetSettingsPanelCloseSubscription() {
        if (mSettingsPanelCloseSubscription != null) {
            mSettingsPanelCloseSubscription.unsubscribe();
        }
        mSettingsPanelCloseSubscription = Observable.timer(SETTINGS_PANEL_CLOSE_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                .observeOn(mScheduler).subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long e) {
                        onCogClicked(mCog);
                    }

                });
    }

    @OnClick(R.id.cog)
    public void onCogClicked(final View cog) {
        if (mSettingsPanel.getX() == -mSettingsOffscreenWidth) {
            // Show
            ObjectAnimator animTranslation = ObjectAnimator.ofFloat(mSettingsPanel, "x", -mSettingsOffscreenWidth, 0);
            ObjectAnimator animAlpha = ObjectAnimator.ofFloat(mSettingsPanel, "alpha", mSettingsOffscreenAlpha, 1);

            AnimatorSet set = new AnimatorSet();
            set.setDuration(500);
            set.playTogether(animTranslation, animAlpha);
            set.start();

            resetSettingsPanelCloseSubscription();
        } else {
            // Hide
            if (mSettingsPanelCloseSubscription != null) {
                mSettingsPanelCloseSubscription.unsubscribe();
            }

            ObjectAnimator animTranslation = ObjectAnimator.ofFloat(mSettingsPanel, "x", 0, -mSettingsOffscreenWidth);
            ObjectAnimator animAlpha = ObjectAnimator.ofFloat(mSettingsPanel, "alpha", mSettingsPanel.getAlpha(),
                    mSettingsOffscreenAlpha);

            AnimatorSet set = new AnimatorSet();
            set.setDuration(500);
            set.playTogether(animTranslation, animAlpha);
            set.start();
        }
    }

    @OnClick(R.id.font_size_small)
    public void onSmallFontSizeClicked() {
        resetSettingsPanelCloseSubscription();
        mPrefs.setReadingFontSize(mContext.getResources().getInteger(R.integer.font_size_small));
        displayNews();
    }

    @OnClick(R.id.font_size_medium)
    public void onMediumFontSizeClicked() {
        resetSettingsPanelCloseSubscription();
        mPrefs.setReadingFontSize(mContext.getResources().getInteger(R.integer.font_size_medium));
        displayNews();
    }

    @OnClick(R.id.font_size_large)
    public void onLargeFontSizeClicked() {
        resetSettingsPanelCloseSubscription();
        mPrefs.setReadingFontSize(mContext.getResources().getInteger(R.integer.font_size_large));
        displayNews();
    }
}
