package com.app.tosstra.fragments.driver;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.app.tosstra.R;

public class AddANewJobFragment extends Fragment {

    private EditText et_amount;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.add_a_new_job_fragment, container, false);
        initUI(view);
        return view;
    }

    private void initUI(View view) {
        et_amount=view.findViewById(R.id.et_amount);
    }

}