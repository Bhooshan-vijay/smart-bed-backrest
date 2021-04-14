package com.smartbackrest.screens.fowlerconfirmation;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.smartbackrest.screens.bluetoothdeviceselection.BLEDeviceSelectionActivity;
import com.smartbackrest.screens.diseasconfirmation.DiseasConfirmationActivity;
import com.smartbackrest.R;

public class fowlerConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fowler_confirmation);

        openBluetoothDeviceIfNotPaired();

    }

    public void goToDiseaseConfirmationActivity(View v) {
        Intent intent = new Intent(fowlerConfirmationActivity.this, DiseasConfirmationActivity.class);
        startActivity(intent);
    }

    private void openBluetoothDeviceIfNotPaired() {
        String storedDevice = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE).getString("device_name", "");
        if (storedDevice.isEmpty()) {
            startActivity(new Intent(this, BLEDeviceSelectionActivity.class));
        }
    }

}
