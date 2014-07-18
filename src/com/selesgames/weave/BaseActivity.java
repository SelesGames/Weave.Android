package com.selesgames.weave;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import dagger.ObjectGraph;

import java.util.Arrays;
import java.util.List;

import com.selesgames.weave.modules.ActivityModule;

/**
 * Base activity which sets up a per-activity object graph and performs
 * injection.
 */
public abstract class BaseActivity extends FragmentActivity {
    private ObjectGraph activityGraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WeaveApplication application = (WeaveApplication) getApplication();
        activityGraph = application.getApplicationGraph().plus(getModules().toArray());
        activityGraph.inject(this);
    }

    @Override
    protected void onDestroy() {
        activityGraph = null;

        super.onDestroy();
    }

    protected List<Object> getModules() {
        return Arrays.<Object> asList(new ActivityModule(this));
    }

    public void inject(Object object) {
        activityGraph.inject(object);
    }
}