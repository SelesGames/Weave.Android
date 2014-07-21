package com.selesgames.weave.modules;

import javax.inject.Singleton;

import android.content.Context;

import com.selesgames.weave.ForActivity;
import com.selesgames.weave.ui.BaseActivity;
import com.selesgames.weave.ui.main.CategoriesFragment;
import com.selesgames.weave.ui.main.MainActivity;
import com.selesgames.weave.ui.onboarding.OnboardingActivity;
import com.selesgames.weave.ui.onboarding.OnboardingFragment;

import dagger.Module;
import dagger.Provides;

@Module(injects = { MainActivity.class, CategoriesFragment.class, OnboardingActivity.class, OnboardingFragment.class }, addsTo = WeaveModule.class, library = true)
public class ActivityModule {

    private final BaseActivity activity;

    public ActivityModule(BaseActivity activity) {
        this.activity = activity;
    }

    @Provides
    @Singleton
    @ForActivity
    Context provideActivityContext() {
        return activity;
    }

}