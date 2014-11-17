package com.selesgames.weave.ui.onboarding;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.selesgames.weave.ForActivity;
import com.selesgames.weave.modules.ActivityModule;
import com.selesgames.weave.modules.MicrosoftServiceClientModule;
import com.selesgames.weave.ui.BaseActivity;
import com.selesgames.weave.ui.main.MainActivity;

import dagger.Module;
import dagger.Provides;

public class OnboardingActivity extends BaseActivity {

    @Inject
    @ForActivity
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, OnboardingFragment.newInstance())
                    .commit();
        }
    }

    @Override
    protected List<Object> getModules() {
        List<Object> modules = new ArrayList<Object>(super.getModules());
        modules.add(new OnboardingModule());
        return modules;
    }

    @Module(injects = { OnboardingActivity.class, OnboardingFragment.class }, addsTo = ActivityModule.class, includes = { MicrosoftServiceClientModule.class })
    public class OnboardingModule {

        @Provides
        OnboardingController provideController() {
            return new OnboardingController() {
                
                @Override
                public void finished() {
                    Intent i = new Intent(mContext, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            };
        }
    }

}
