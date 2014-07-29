package com.selesgames.weave.ui.onboarding;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Action1;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.OnClick;

import com.microsoft.windowsazure.mobileservices.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.UserAuthenticationCallback;
import com.selesgames.weave.ForActivity;
import com.selesgames.weave.OnMainThread;
import com.selesgames.weave.R;
import com.selesgames.weave.WeavePrefs;
import com.selesgames.weave.api.IdentityService;
import com.selesgames.weave.api.UserService;
import com.selesgames.weave.model.User;
import com.selesgames.weave.model.UserInfo;
import com.selesgames.weave.ui.BaseFragment;

public class OnboardingFragment extends BaseFragment {

    @Inject
    @ForActivity
    Context mContext;

    @Inject
    OnboardingController mController;

    @Inject
    WeavePrefs mPrefs;

    @Inject
    @OnMainThread
    Scheduler mScheduler;

    @Inject
    UserService mUserService;

    @Inject
    IdentityService mIdentityService;

    @Inject
    MobileServiceClient mClient;

    @InjectView(R.id.social_container)
    View mSocialContainer;

    @InjectView(R.id.create_account)
    Button mCreateAccount;

    private Handler handler = new Handler();

    private Observable<User> mCreateUserObservable;

    public static OnboardingFragment newInstance() {
        return new OnboardingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_onboarding, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mCreateAccount.setVisibility(View.INVISIBLE);
        mSocialContainer.setVisibility(View.INVISIBLE);

        // Create user
        if (mPrefs.getUserId() == null) {
            mPrefs.setUserId("0d13bf82-0f14-475f-9725-f97e5a123d5a");
            mController.finished();
            //createAccount();
        } else {
            mSocialContainer.setVisibility(View.VISIBLE);
        }
    }

    private void createAccount() {

        mCreateUserObservable = mUserService.create();
        mCreateUserObservable.observeOn(mScheduler).subscribe(new Action1<User>() {

            @Override
            public void call(User user) {
                // Save user ID
                mPrefs.setUserId(user.getId());

                // Show social options
                mSocialContainer.setVisibility(View.VISIBLE);
                TranslateAnimation animation = new TranslateAnimation(0, 0, mSocialContainer.getHeight(), 0);
                animation.setDuration(500);
                mSocialContainer.startAnimation(animation);
            }
        }, new Action1<Throwable>() {

            @Override
            public void call(Throwable t) {
                Log.e("WEAVE", "Could not create user", t);
                mCreateAccount.setVisibility(View.VISIBLE);
                TranslateAnimation animation = new TranslateAnimation(0, 0, mCreateAccount.getHeight(), 0);
                animation.setDuration(500);
                mCreateAccount.startAnimation(animation);
            }
        });
    }

    @OnClick(R.id.create_account)
    void onCreateAccount() {
        // Slide out
        TranslateAnimation animation = new TranslateAnimation(0, 0, 0, mCreateAccount.getHeight());
        animation.setDuration(500);
        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mCreateAccount.setVisibility(View.INVISIBLE);
            }
        });
        mCreateAccount.startAnimation(animation);

        createAccount();
    }

    @OnClick(R.id.skip_social)
    void onSkipSocial() {
        mController.finished();
    }

    @OnClick(R.id.login_facebook)
    void onLoginFacebook() {
        login(MobileServiceAuthenticationProvider.Facebook);
    }

    @OnClick(R.id.login_twitter)
    void onLoginTwitter() {

    }

    @OnClick(R.id.login_google_plus)
    void onLoginGooglePlus() {

    }

    @OnClick(R.id.login_windows)
    void onLoginWindows() {

    }

    private void login(MobileServiceAuthenticationProvider provider) {
        mClient.login(provider, new UserAuthCallback(provider));
    }

    private class UserAuthCallback implements UserAuthenticationCallback {

        private MobileServiceAuthenticationProvider mProvider;

        public UserAuthCallback(MobileServiceAuthenticationProvider provider) {
            mProvider = provider;
        }

        @Override
        public void onCompleted(MobileServiceUser user, Exception e, ServiceFilterResponse response) {
            if (e != null) {
                Toast.makeText(mContext, R.string.onboarding_login_fail, Toast.LENGTH_LONG).show();
            } else {
                String userId = mPrefs.getUserId();

                final String token = user.getAuthenticationToken();

                Observable<UserInfo> observable = null;

                switch (mProvider) {
                case Facebook:
                    observable = mIdentityService.saveFacebookIdentity(userId, token);
                    break;
                case Twitter:
                    observable = mIdentityService.saveTwitterIdentity(userId, token);
                    break;
                case Google:
                    observable = mIdentityService.saveGoogleIdentity(userId, token);
                    break;
                case MicrosoftAccount:
                    observable = mIdentityService.saveMicrosoftIdentity(userId, token);
                    break;
                }

                observable.subscribe(new Action1<UserInfo>() {

                    @Override
                    public void call(UserInfo userInfo) {
                        String userId = userInfo.getUserId();
                        if (userId != null) {
                            mPrefs.setUserId(userId);
                        } else {
                            // TODO: Log error / Alert user
                        }

                        mController.finished();
                    }

                }, new Action1<Throwable>() {

                    @Override
                    public void call(Throwable t) {
                        Log.e("WEAVE", "Could not connect social account", t);
                    }
                });
            }
        }
    };

}
