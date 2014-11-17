package com.selesgames.weave.ui.main;

import javax.inject.Inject;

import rx.Scheduler;
import rx.functions.Action0;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import butterknife.InjectView;
import butterknife.OnClick;

import com.selesgames.weave.ForActivity;
import com.selesgames.weave.OnMainThread;
import com.selesgames.weave.R;
import com.selesgames.weave.WeavePrefs;
import com.selesgames.weave.ui.BaseFragment;

public class SettingsFragment extends BaseFragment {

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Inject
    @ForActivity
    Context mContext;

    @Inject
    SettingsController mController;

    @Inject
    @OnMainThread
    Scheduler mMainScheduler;

    @Inject
    WeavePrefs mPrefs;

    @InjectView(R.id.theme)
    Spinner mTheme;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mTheme.setSelection(mPrefs.getThemeId());
        mMainScheduler.createWorker().schedule(new Action0() {

            @Override
            public void call() {
                mTheme.setOnItemSelectedListener(new OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        mPrefs.setThemeId(position);
                        mController.onThemeChanged();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // TODO Auto-generated method stub

                    }

                });
            }
        });

    }
    
    @OnClick(R.id.manage_sources)
    public void onManageSourcesClicked() {
        mController.manageSources();
    }

}
