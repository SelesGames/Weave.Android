package com.selesgames.weave.modules;

import javax.inject.Singleton;

import android.content.Context;

import com.selesgames.weave.BaseActivity;
import com.selesgames.weave.ForActivity;
import com.selesgames.weave.MainActivity;
import com.selesgames.weave.fragments.CategoriesFragment;

import dagger.Module;
import dagger.Provides;

@Module(injects = { MainActivity.class, CategoriesFragment.class }, addsTo = WeaveModule.class, library = true)
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