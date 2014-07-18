package com.selesgames.weave;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import butterknife.ButterKnife;

public class BaseFragment extends Fragment {

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