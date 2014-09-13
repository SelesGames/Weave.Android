package com.selesgames.weave.ui.main;

import javax.inject.Inject;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;

import com.selesgames.weave.ForActivity;
import com.selesgames.weave.R;
import com.selesgames.weave.ui.BaseFragment;
import com.squareup.picasso.Picasso;

public class AdFragmentOld extends BaseFragment {

    public static AdFragmentOld newInstance() {
        return new AdFragmentOld();
    }

    @Inject
    @ForActivity
    Context mContext;

    @Inject
    Picasso mPicasso;

    @InjectView(R.id.title)
    TextView mTitle;

    @InjectView(R.id.call_to_action)
    TextView mCallToAction;

    @InjectView(R.id.image)
    ImageView mImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ad_old, container, false);
    }

//    @Override
//    public void onActivityCreated(Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        MoPubNative moPub = new MoPubNative(mContext, getString(R.string.mopub_ad_unit_id), new MoPubNativeListener() {
//
//            @Override
//            public void onNativeLoad(final NativeResponse response) {
//                mTitle.setText(String.format("%s\n%s", response.getTitle(), response.getText()));
//                mCallToAction.setText(response.getCallToAction());
//                mPicasso.load(response.getMainImageUrl()).fit().centerCrop().into(mImage);
//                getView().setOnClickListener(new OnClickListener() {
//                    
//                    @Override
//                    public void onClick(View v) {
//                        response.handleClick(v);
//                    }
//                });
//            }
//
//            @Override
//            public void onNativeImpression(View view) {
//
//            }
//
//            @Override
//            public void onNativeFail(NativeErrorCode error) {
//                Log.w("AD", "Could not load native ad");
//            }
//
//            @Override
//            public void onNativeClick(View view) {
//
//            }
//        });
//
//        EnumSet<RequestParameters.NativeAdAsset> assetsSet = EnumSet.of(RequestParameters.NativeAdAsset.TITLE,
//                RequestParameters.NativeAdAsset.TEXT, RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT,
//                RequestParameters.NativeAdAsset.MAIN_IMAGE, RequestParameters.NativeAdAsset.ICON_IMAGE,
//                RequestParameters.NativeAdAsset.STAR_RATING);
//
//        RequestParameters requestParameters = new RequestParameters.Builder().desiredAssets(assetsSet).build();
//
//        moPub.makeRequest(requestParameters);
//    }

}
