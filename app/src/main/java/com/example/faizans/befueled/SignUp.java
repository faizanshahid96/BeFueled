package com.example.faizans.befueled;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.faizans.befueled.Fragments.AddVehicleFragment;
import com.example.faizans.befueled.Utils.FirebaseMethods;
import com.example.faizans.befueled.databinding.ActivitySignUpBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUp extends AppCompatActivity {

    private static final String TAG = "";
    private EditText mFirstname, mLastname, mEmail, mPhone, mPassword;
    private String firstname, lastname, email, password;
    private long phone;
    private Button mBtnRegister;
    private Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseMethods mFirebaseMethods;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private String mPhoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //         Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
//         [END initialize_auth]
        mAuth.signOut();
        ActivitySignUpBinding activitySignUpBinding = DataBindingUtil
                .setContentView(this, R.layout.activity_sign_up);
        activitySignUpBinding.setActivitySignup(this);
        mContext = SignUp.this;
        mFirebaseMethods = new FirebaseMethods(mContext);
        mPhoneNumber = getIntent().getStringExtra("phonenumber");
        mFirstname = activitySignUpBinding.inputFirstname;
        mLastname = activitySignUpBinding.inputLastname;
        mEmail = activitySignUpBinding.inputEmail;
        mPhone = activitySignUpBinding.inputPhone;
        mPhone.setText(mPhoneNumber);
        mPassword = activitySignUpBinding.inputPassword;
        mBtnRegister = activitySignUpBinding.buttonRegister;
        findViewById(R.id.fragment_signUp_container).setVisibility(View.GONE);
        setupFirebaseAuth();

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void init() {
        email = mEmail.getText().toString();
        password = mPassword.getText().toString();
        firstname = mFirstname.getText().toString();
        lastname = mLastname.getText().toString();
        String str = mPhone.getText().toString();
        phone =  Long.parseLong(str);
//        Toast.makeText(mContext, "Values "+ email+" "+password+" "+firstname+" "+lastname+" "+phone
//                ,Toast.LENGTH_SHORT).show();
        if (checkInputs(email, firstname, password, phone, lastname)) {
            mFirebaseMethods.registerNewEmail(email, firstname, password, phone, lastname);
//            mFirebaseMethods.addNewUser(email, firstname, lastname, phone);
//            Bundle bundle = new Bundle();
//            bundle.putBoolean("isFromSignUp",true);
//            AddVehicleFragment addVehicleFragment = new AddVehicleFragment();
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_signUp_container, addVehicleFragment).commit();
            Intent intent = new Intent(this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }



    }

    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.d(TAG, "onAuthStateChanged: user" + user);
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            checkIfUsernameExists(username);
                            mFirebaseMethods.addNewUser(email, firstname, lastname, phone);

                            Toast.makeText(SignUp.this, "Signed Up", Toast.LENGTH_SHORT).show();
                            mAuth.signOut();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    finish();

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }


    private boolean checkInputs(String email, String firstname, String password, long phone, String lastname) {
        Log.d(TAG, "checkInputs: checking inputs for null values.");
        String sphone  = phone + "";
        if (email.equals("") || firstname.equals("") || lastname.equals("") || password.equals("") || sphone.equals("")) {
            Toast.makeText(mContext, "All fields must be filled out.1 "+ email+" "+password+" "+firstname+" "+lastname+" "+phone
                    ,Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void login(View view) {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    public void mainactivity(View view) {
//        Intent intent = new Intent(this,MainActivity.class);
//        startActivity(intent);

//        final ProgressDialog dialog = ProgressDialog.show(SignUp.this, "",
//                "Registering. Please wait...", true);
        init();
//        dialog.dismiss();
    }
}
