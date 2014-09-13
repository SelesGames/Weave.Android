package com.selesgames.weave.ui.main;

import javax.inject.Inject;

import timber.log.Timber;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adsdk.sdk.nativeads.NativeAd;
import com.adsdk.sdk.nativeads.NativeAdListener;
import com.adsdk.sdk.nativeads.NativeAdManager;
import com.adsdk.sdk.nativeads.NativeAdView;
import com.adsdk.sdk.nativeads.NativeViewBinder;
import com.selesgames.weave.ForActivity;
import com.selesgames.weave.R;
import com.selesgames.weave.ui.BaseFragment;
import com.squareup.picasso.Picasso;

public class AdFragment extends BaseFragment implements NativeAdListener {

    public static AdFragment newInstance() {
        return new AdFragment();
    }

    @Inject
    @ForActivity
    Context mContext;

    @Inject
    Picasso mPicasso;
    
    NativeAdManager mAdManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ad, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdManager = new NativeAdManager(mContext, "http://my.mobfox.com/request.php", true, getString(R.string.mobfox_publisher_id), this, null);
        mAdManager.requestAd();
    }

    @Override
    public void adClicked() {
        Timber.v("Add clicked");
    }

    @Override
    public void adFailedToLoad() {
        Timber.w("Could not load ad");
    }

    @Override
    public void adLoaded(NativeAd ad) {
        Timber.v("Ad loaded");
        
        NativeViewBinder adBinder = new NativeViewBinder(R.layout.mobfox_native_ad);
        adBinder.bindTextAsset("headline", R.id.headline);
        // adBinder.bindTextAsset("description", R.id.description);
        // adBinder.bindImageAsset("icon", R.id.iconView);
        adBinder.bindImageAsset("main", R.id.image);
        adBinder.bindTextAsset("cta", R.id.call_to_action);
        NativeAdView adView = mAdManager.getNativeAdView(ad, adBinder);
        ViewGroup rootView = (ViewGroup) getView();
        rootView.addView(adView);
    }

    @Override
    public void impression() {
        Timber.v("Ad impression");
    }

}
