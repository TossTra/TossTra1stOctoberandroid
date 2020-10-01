package com.app.tosstra.fragments.dispacher;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.tosstra.adapters.ChildActiveDriverAdapter;
import com.app.tosstra.R;


public class ChildActiveDriverFragment extends Fragment {
    private RecyclerView recyclerview;
    private ChildActiveDriverAdapter adapter;


    public ChildActiveDriverFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_activer_driver_fragments, container, false);
        recyclerview=view.findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new ChildActiveDriverAdapter(getContext());
        recyclerview.setAdapter(adapter);

        return view;
    }
}