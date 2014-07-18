package com.selesgames.weave.modules;

import java.util.concurrent.Executor;

import javax.inject.Singleton;

import rx.Scheduler;
import rx.schedulers.Schedulers;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.selesgames.weave.BuildConfig;
import com.selesgames.weave.Debug;
import com.selesgames.weave.ForApplication;
import com.selesgames.weave.MainThread;
import com.selesgames.weave.WeaveApplication;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;

/**
 * A module for Android-specific dependencies which require a {@link Context} or
 * {@link android.app.Application} to create.
 */
@Module(library = true, includes = { NetworkModule.class })
public class WeaveModule {
    private final WeaveApplication application;

    public WeaveModule(WeaveApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    @ForApplication
    Context provideApplicationContext() {
        return application;
    }

    @Provides
    @Debug
    boolean provideDebug() {
        return BuildConfig.DEBUG && true;
    }

    @Provides
    @Singleton
    @MainThread
    Scheduler provideMainThread() {
        final Handler handler = new Handler(Looper.getMainLooper());
        return Schedulers.from(new Executor() {

            @Override
            public void execute(Runnable command) {
                handler.post(command);
            }
        });
    }

    @Provides
    @Singleton
    Picasso providePicasso(@ForApplication Context context, @Debug boolean debug) {
        Picasso.Builder builder = new Picasso.Builder(context).loggingEnabled(debug).indicatorsEnabled(debug);
        return builder.build();
    }

}