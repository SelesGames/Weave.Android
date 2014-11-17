package com.selesgames.weave.ui;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import butterknife.ButterKnife;

public class BaseDialogFragment extends DialogFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((BaseActivity) getActivity()).inject(this);
        ButterKnife.inject(this, getView());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

}