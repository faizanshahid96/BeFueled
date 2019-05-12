package com.example.faizans.befueled.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.faizans.befueled.R;
import com.example.faizans.befueled.Utils.FuelRequestInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class FuelRequestFragment extends Fragment implements View.OnClickListener {

    FirebaseAuth mAuth;
    DatabaseReference myRef;
    RadioGroup mRadioGroup;
    RadioButton mRadioBtn;
    EditText mQtyOfFuel;
    FuelRequestInfo fuelRequestInfo;
    private String userID;
    private Button mBtnNext;
    private String TAG = "FuelRequestFragment";
    private boolean mChecked = false;
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String qtyfuel = mQtyOfFuel.getText().toString();
            mBtnNext.setEnabled(!qtyfuel.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

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
//        mBtnNext.setEnabled(false);
        Log.d(TAG, "onCreateView:userid " + userID);
        mBtnNext.setOnClickListener(this);
        mQtyOfFuel = view.findViewById(R.id.input_fuel_qty);
        mQtyOfFuel.addTextChangedListener(textWatcher);
        mRadioGroup = view.findViewById(R.id.radioGroup);
        initwidgets();
        return view;
    }

    private void initwidgets() {
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_two_hrs:
                        FuelRequestInfo.setTimeFrame("180");
                        mQtyOfFuel.setEnabled(true);
                        break;
                    case R.id.rb_90_min:
                        FuelRequestInfo.setTimeFrame("90");
                        mQtyOfFuel.setEnabled(true);
                        break;
                    case R.id.rb_one_hrs:
                        FuelRequestInfo.setTimeFrame("60");
                        mQtyOfFuel.setEnabled(true);
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_next) {
            FuelRequestInfo.setFuelQuantity(String.valueOf(mQtyOfFuel.getText()));
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new SelectVehicleFragment());
            transaction.addToBackStack("fragment_container");
            transaction.commit();
        }
    }


}
