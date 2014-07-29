package com.selesgames.weave.modules;

import javax.inject.Singleton;

import android.content.Context;

import com.selesgames.weave.ForActivity;
import com.selesgames.weave.ui.BaseActivity;
import com.selesgames.weave.ui.main.MainActivity;

import dagger.Module;
import dagger.Provides;

@Module(injects = { MainActivity.class }, addsTo = WeaveModule.class, library = true)
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