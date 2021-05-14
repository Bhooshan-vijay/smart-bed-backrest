package com.smartbackrest.screens.psychologicalprofile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smartbackrest.ApplicationData;
import com.smartbackrest.R;
import com.smartbackrest.db.DBManager;
import com.smartbackrest.screens.bluetoothdeviceselection.BLEDeviceSelectionActivity;
import com.smartbackrest.screens.help.HelpActivity;
import com.smartbackrest.screens.mealselection.MealActivity;
import com.smartbackrest.screens.profilecreation.ProfileCreationActivity;
import com.smartbackrest.screens.timerset.TimerSetActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PhysiologicalProfileActivity extends AppCompatActivity {

    private int phase = 1;
    private final String TAG = "PhysiologicalProfile";
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physiological_profile);
        //Set title
        TextView title = findViewById(R.id.screen_title);
        title.setText("Physiological Profile");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        setView();

        findViewById(R.id.layoutHelp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PhysiologicalProfileActivity.this, HelpActivity.class);
                intent.putStringArrayListExtra("list", new ArrayList<String>() {{
                    add("In this page the application asks if the person has had any stomach surgeries the person can " +
                            "choose any of the button respectively.");
                    add("When you click on any of the button then:" +
                            "The application asks if the person has any pain related issues.");
                }});
                startActivity(intent);
            }
        });
    }

    private void setView() {
        if (phase == 1) {
            ((TextView) findViewById(R.id.txtQuestion)).setText("Have you had stomach surgeries ?");
            findViewById(R.id.btnNotSure).setVisibility(View.VISIBLE);
        } else {
            ((TextView) findViewById(R.id.txtQuestion)).setText("Do you have pain related issues ?");
            findViewById(R.id.btnNotSure).setVisibility(View.VISIBLE);
        }
    }

    public void onNotSureClick(View view) {
        ApplicationData.getInstance().getUser().setHasStomachSurgeries(true);
        saveProfile();
    }

    private void saveProfile() {
        final DBManager dbManager = new DBManager(this).open();
        if (dbManager.insertUser(ApplicationData.getInstance().getUser()) > 0) {
            if (!isBluetoothDevicePaired()) {
                startActivityForResult(new Intent(this, BLEDeviceSelectionActivity.class), 1);
            } else {
                startActivity(new Intent(this, MealActivity.class));
            }
        } else {
            Toast.makeText(this, "Error creating profile, please try again!!!!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isBluetoothDevicePaired() {
        String storedDevice = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE).getString("device_name", "");
        if (storedDevice.isEmpty()) {
            return false;
        }
        return true;
    }

    public void onNoClick(View view) {
        ApplicationData.getInstance().getUser().setHasStomachSurgeries(false);
        setUserDataIntoFirestore();
        saveProfile();
    }

    public void onYesClick(View view) {
        ApplicationData.getInstance().getUser().setHasStomachSurgeries(true);
        setUserDataIntoFirestore();
        saveProfile();
    }

    private void setUserDataIntoFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Users").document(firebaseUser.getPhoneNumber()).
                set(ApplicationData.getInstance().getUser())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String result = data.getStringExtra("result");
            if (result.length() > 0) {
                startActivity(new Intent(this, MealActivity.class));
            }
        }
    }
}
