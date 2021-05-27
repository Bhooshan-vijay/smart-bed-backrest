package com.smartbackrest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.smartbackrest.screens.fowlerconfirmation.fowlerConfirmationActivity;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Set title
        TextView title = findViewById(R.id.screen_title);
        title.setText("Account");

        //Back Icon click
        ImageView backIcon = findViewById(R.id.iv_backIcon);
        backIcon.setOnClickListener(view -> finish());
    }

    private void openFowlerConfirmationActivity() {
        Intent intent = new Intent(getApplicationContext(), fowlerConfirmationActivity.class);
        startActivity(intent);
        finish();
    }
}