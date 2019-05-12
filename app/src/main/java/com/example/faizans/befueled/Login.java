package com.example.faizans.befueled;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.AccountKitLoginResult;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Login extends AppCompatActivity implements View.OnClickListener {


    private static final String TAG = "";
    private Context mContext;
    private ProgressDialog mProgressdialog;
    private EditText mEmail, mPassword;
    private static final int REQUEST_CODE = 1000;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    private Button mbtnfb;
    DatabaseReference myUserRef;
    // [END declare_auth]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = Login.this;
        mEmail = findViewById(R.id.input_email);
        mPassword = findViewById(R.id.input_password);
//         Initialize Firebase Auth
//        mbtnfb = findViewById(R.id.btn_fb);
//        mbtnfb.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        myUserRef = FirebaseDatabase.getInstance().getReference().child("users");
//         [END initialize_auth]
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        }

    }

    private void signIn(String email, String password) {
        final ProgressDialog dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(mContext, "login successful", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            Intent intent = new Intent(mContext, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            dialog.dismiss();
//                            Toast.makeText(mContext, "login Failed", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void signUp(View view) {
        Intent intent = new Intent(this, PhoneVerificationActivity.class);
        startActivity(intent);
    }


//    public void onClickListener(View view) {
//        if (view.getId() == R.id.btn_fb) {
//            signInWithPhone();
//        }
//    }


//    private void signInWithPhone() {
//        Intent intent = new Intent(Login.this, AccountKitActivity.class);
//        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
//                new AccountKitConfiguration.AccountKitConfigurationBuilder(LoginType.PHONE,
//                        AccountKitActivity.ResponseType.TOKEN);
//        intent.putExtra(AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,configurationBuilder.build());
//        startActivityForResult(intent,REQUEST_CODE);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            AccountKitLoginResult result = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
            if (result.getError() != null) {
                Toast.makeText(mContext, "" + result.getError().getErrorType().getMessage(), Toast.LENGTH_SHORT).show();
                return;
            } else if (result.wasCancelled()) {
                Toast.makeText(mContext, "Cancel Login", Toast.LENGTH_SHORT).show();
                return;
            } else {
                if (result.getAccessToken() != null) {
                    final ProgressDialog dialog = ProgressDialog.show(this, "",
                            "Loading. Please wait...", true);
                    dialog.show();
                }
                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                    @Override
                    public void onSuccess(Account account) {
                        String userphone = account.getPhoneNumber().toString();


                    }

                    @Override
                    public void onError(AccountKitError accountKitError) {

                    }
                });
            }


        }

    }

    private boolean isStringNull(String str) {
        return str.equals("");
    }
    
    public void signin(View view) {
        String email = mEmail.getText().toString();
        String password = mPassword.getText().toString();
        if (!isStringNull(email) && !isStringNull(password)){
            signIn(email,password);
        }
        else
            Toast.makeText(mContext, "Fill in the fields", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.btn_fb) {
////            signInWithPhone();
//        }
    }
}

