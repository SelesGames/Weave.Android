package com.selesgames.weave;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;
import timber.log.Timber.DebugTree;
import android.app.Application;

import com.selesgames.weave.modules.NetworkModule;
import com.selesgames.weave.modules.WeaveModule;

import dagger.ObjectGraph;

public class WeaveApplication extends Application {

    private ObjectGraph applicationGraph;

    @Inject
    @Debug
    boolean debug;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationGraph = ObjectGraph.create(getModules().toArray());
        applicationGraph.inject(this);

        if (debug) {
            Timber.plant(new DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
    }

    protected List<Object> getModules() {
        return Arrays.<Object> asList(new WeaveModule(this), new NetworkModule());
    }

    public ObjectGraph getApplicationGraph() {
        return applicationGraph;
    }

    private static class CrashReportingTree extends Timber.HollowTree {
        @Override
        public void i(String message, Object... args) {
            // TODO e.g., Crashlytics.log(String.format(message, args));
        }

        @Override
        public void i(Throwable t, String message, Object... args) {
            i(message, args); // Just add to the log.
        }

        @Override
        public void e(String message, Object... args) {
            i("ERROR: " + message, args); // Just add to the log.
        }

        @Override
        public void e(Throwable t, String message, Object... args) {
            e(message, args);

            // TODO e.g., Crashlytics.logException(t);
        }
    }

}