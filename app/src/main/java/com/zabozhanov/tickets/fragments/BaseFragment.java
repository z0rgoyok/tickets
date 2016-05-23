package com.zabozhanov.tickets.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.zabozhanov.tickets.ui.MainActivity;

/**
 * Created by z0rgoyok on 16.05.16.
 */
public class BaseFragment extends Fragment {

    private MainActivity mainActivity;

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity)getActivity();
    }
}
