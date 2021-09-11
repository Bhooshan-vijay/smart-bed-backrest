package com.smartbackrest.screens.medication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smartbackrest.ApplicationData;
import com.smartbackrest.db.DBManager;
import com.smartbackrest.screens.bluetoothdeviceselection.BLEDeviceSelectionActivity;
import com.smartbackrest.screens.mealselection.MealActivity;
import com.smartbackrest.screens.psychologicalprofile.PsychologicalProfileActivity;
import com.smartbackrest.R;
import com.smartbackrest.screens.help.HelpActivity;

import java.util.ArrayList;

public class MedicationActivity extends AppCompatActivity {

    private final String TAG = "MedicationActivity";
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);
        //Set title
        TextView title = findViewById(R.id.screen_title);
        title.setText("Medication Profile");

        //Back Icon click
        ImageView backIcon = findViewById(R.id.iv_backIcon);
        backIcon.setOnClickListener(view -> finish());

        findViewById(R.id.layoutHelp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MedicationActivity.this, HelpActivity.class);
                intent.putStringArrayListExtra("list", new ArrayList<String>() {{
                    add("In this page the application asks if the patient is in digestive medication or not the person " +
                            "can click “yes” or “no”.");
                }});
                startActivity(intent);
            }
        });

        Chip chipAnxityissue = findViewById(R.id.chipAnxityissue);
        chipAnxityissue.setOnCheckedChangeListener((compoundButton, b) -> {
            ApplicationData.getInstance().getUser().setHasAnxietyIssues(b);
        });

        Chip stomachSurgery = findViewById(R.id.stomachSurgery);
        stomachSurgery.setOnCheckedChangeListener((compoundButton, b) -> {
            ApplicationData.getInstance().getUser().setHasStomachSurgeries(b);
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    public void onYesClick(View view) {
        ApplicationData.getInstance().getUser().setOnDigestiveMedication(true);
        setUserDataIntoFirestore();
        saveProfile();
    }

    public void onNoClick(View view) {
        ApplicationData.getInstance().getUser().setOnDigestiveMedication(false);
        setUserDataIntoFirestore();
        saveProfile();
//        finish();
//        startActivity(new Intent(this, PsychologicalProfileActivity.class));
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
