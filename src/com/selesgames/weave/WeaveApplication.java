package com.selesgames.weave;

import android.app.Application;
import dagger.ObjectGraph;

import java.util.Arrays;
import java.util.List;

import com.selesgames.weave.modules.NetworkModule;
import com.selesgames.weave.modules.WeaveModule;

public class WeaveApplication extends Application {
    private ObjectGraph applicationGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationGraph = ObjectGraph.create(getModules().toArray());
    }

    protected List<Object> getModules() {
        return Arrays.<Object> asList(new WeaveModule(this), new NetworkModule(this));
    }

    public ObjectGraph getApplicationGraph() {
        return applicationGraph;
    }
}