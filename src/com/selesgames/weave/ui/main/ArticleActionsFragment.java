package com.selesgames.weave.ui.main;

import javax.inject.Inject;

import rx.Scheduler;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageButton;
import butterknife.InjectView;

import com.selesgames.weave.ForActivity;
import com.selesgames.weave.OnMainThread;
import com.selesgames.weave.R;
import com.selesgames.weave.model.Article;
import com.selesgames.weave.model.Feed;
import com.selesgames.weave.model.News;
import com.selesgames.weave.ui.BaseFragment;
import com.selesgames.weave.view.ArcPositioner;

public class ArticleActionsFragment extends BaseFragment {

    public static final String KEY_FEED = "feed";

    public static final String KEY_NEWS = "news";

    public static final String KEY_ARTICLE = "article";

    public static ArticleActionsFragment newInstance(Feed feed, News news, Article arcitle) {
        Bundle b = new Bundle();
        b.putParcelable(KEY_FEED, feed);
        b.putParcelable(KEY_NEWS, news);
        b.putParcelable(KEY_ARTICLE, arcitle);

        ArticleActionsFragment f = new ArticleActionsFragment();
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
    Point mScreenSize;

    @InjectView(R.id.pocket)
    ImageButton mPocket;

    @InjectView(R.id.facebook)
    ImageButton mFacebook;

    @InjectView(R.id.twitter)
    ImageButton mTwitter;

    @InjectView(R.id.email)
    ImageButton mEmail;

    @InjectView(R.id.sms)
    ImageButton mSms;

    @InjectView(R.id.share)
    ImageButton mShare;

    private Feed mFeed;

    private News mNews;

    private Article mArticle;

    private boolean mHasAnimated;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getArguments();
        mFeed = b.getParcelable(KEY_FEED);
        mNews = b.getParcelable(KEY_NEWS);
        mArticle = b.getParcelable(KEY_ARTICLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article_actions, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final View view = getView();
        ViewTreeObserver observer = view.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                ViewTreeObserver observer = view.getViewTreeObserver();
                observer.removeGlobalOnLayoutListener(this);

                int halfSize = mFacebook.getWidth() / 2;

                View view = getView();
                Resources resources = getResources();
                int padding = resources.getDimensionPixelOffset(R.dimen.social_menu_padding);
                int x = view.getWidth() - halfSize - padding;
                int y = view.getHeight() - halfSize - padding;

                int radiusOuter = resources.getDimensionPixelOffset(R.dimen.social_menu_radius_outer);
                int radiusInner = resources.getDimensionPixelOffset(R.dimen.social_menu_radius_inner);
                Point point = new Point(x, y);

                new ArcPositioner.Builder().addView(mTwitter).addView(mFacebook).addView(mShare).setStartAngle(270)
                        .setEndAngle(180).setRadius(radiusOuter).centerAround(point).build().position();

                new ArcPositioner.Builder().addView(mEmail).addView(mSms).setStartAngle(270).setEndAngle(180)
                        .setRadius(radiusInner).centerAround(point).build().position();
            }
        });

    }
}
