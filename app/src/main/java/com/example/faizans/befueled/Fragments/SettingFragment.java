package com.example.faizans.befueled.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.faizans.befueled.Fragments.MapFragment;
import com.example.faizans.befueled.R;

public class SettingFragment extends Fragment implements View.OnClickListener {

    ImageView imageView_back;
    TextView mManageVehicle;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_setting,container,false);
        mManageVehicle = view.findViewById(R.id.text_manage_vehicle);
        imageView_back = view.findViewById(R.id.back);
        imageView_back.setOnClickListener(this);
        mManageVehicle.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.back) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new MapFragment());
            transaction.addToBackStack("fragment_container");
            transaction.commit();
        }
        if (v.getId() == R.id.text_manage_vehicle) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new VehicleManageFragment());
            transaction.addToBackStack("fragment_container");
            transaction.commit();
        }
    }
}
