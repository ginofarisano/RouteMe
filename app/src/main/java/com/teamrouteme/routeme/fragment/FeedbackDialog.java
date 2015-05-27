package com.teamrouteme.routeme.fragment;


import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;


import com.teamrouteme.routeme.R;

import java.util.ArrayList;

public class FeedbackDialog extends DialogFragment {


    public FeedbackDialog() {
        // Empty constructor required for DialogFragment
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = inflater.inflate(R.layout.fragment_feedback_dialog, container);

        Bundle b = getArguments();

        return view;
    }

}