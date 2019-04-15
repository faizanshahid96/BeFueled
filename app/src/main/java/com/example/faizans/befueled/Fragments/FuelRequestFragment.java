package com.example.faizans.befueled.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.faizans.befueled.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FuelRequestFragment extends Fragment implements View.OnClickListener {

    FirebaseAuth mAuth;
    DatabaseReference myRef;
    private String userID;
    private ArrayList<String> list;
    Button mBtnNext;
    private String TAG = "FuelRequestFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fuel_request, container, false);
        mAuth = FirebaseAuth.getInstance();

        userID = mAuth.getCurrentUser().getUid();
        myRef = FirebaseDatabase.getInstance().getReference().child("CustomerCarInformation").child(userID);
        mBtnNext = view.findViewById(R.id.btn_next);
        Log.d(TAG, "onCreateView:userid " + userID);
        mBtnNext.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.ic_wheel_steering || v.getId() == R.id.text_add_vehicle) {
//
//        }
        if (v.getId() == R.id.btn_next) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new SelectVehicleFragment());
            transaction.addToBackStack("fragment_container");
            transaction.commit();
        }
    }


}
