package com.smartbackrest.screens.featureselection;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.smartbackrest.ApplicationData;
import com.smartbackrest.screens.bluetoothdeviceselection.BLEDeviceSelectionActivity;
import com.smartbackrest.screens.profilecreation.ProfileCreationActivity;
import com.smartbackrest.R;
import com.smartbackrest.screens.timerset.TimerSetActivity;
import com.smartbackrest.db.DBManager;
import com.smartbackrest.model.User;
import com.smartbackrest.screens.help.HelpActivity;

import java.util.ArrayList;

public class FeatureSelectionActivity extends AppCompatActivity {

    private static final String TAG = "FeatureSelectionActivit";
    private Button btnDemoMode;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature_selection);

        //Set title
        TextView title = findViewById(R.id.screen_title);
        title.setText("Home");


        btnDemoMode = findViewById(R.id.btnDemoMode);

        updateDemoModeView();
        btnDemoMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE);
                if (sharedPreferences.getBoolean("demo_mode", false)) {
                    sharedPreferences.edit().putBoolean("demo_mode", false).apply();
                } else {
                    sharedPreferences.edit().putBoolean("demo_mode", true).apply();
                }
                updateDemoModeView();
            }
        });

        findViewById(R.id.btnClearProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBManager dbManager = new DBManager(FeatureSelectionActivity.this).open();
                dbManager.clearUsers();
            }
        });

        findViewById(R.id.imgHelp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, HelpActivity.class);
                intent.putStringArrayListExtra("list", new ArrayList<String>() {{
                    add("Manual Mode: When you click on the manual mode, It means that you want to set the time of decline of the smart bed backrest by yourself.");
                    add("Automatic mode: When you click on the Automatic Mode, It means that you want to create a profile in which it will ask some details like what your name is , then what is your birthdate. Then after the profile the application will ask you to enter what kind of food you ate in order to calculate the amount of time it will take to decline the bed.");
                    add("Enable Demo Mode: When you click on the enable demo mode, It means that you just want to go through application without it really sending any data to the Smart Bed Backrest device.");
                }});
                startActivity(intent);
            }
        });

    }

    private void updateDemoModeView() {
        if (getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE).getBoolean("demo_mode", false)) {
            btnDemoMode.setText("Disable demo mode");
        } else {
            btnDemoMode.setText("Enable demo mode");
        }
    }

    public void goToProfileCreation(View view) {
        DBManager dbManager = new DBManager(this).open();
        User user = dbManager.fetchUser();
        ApplicationData.getInstance().clear();
        if (getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE).getBoolean("demo_mode", false)) {
            startActivity(new Intent(this, ProfileCreationActivity.class));
        } else {
            if (user != null) {
                ApplicationData.getInstance().setUser(user);
            }
//            if (!isBluetoothDevicePaired()) {
//                startActivityForResult(new Intent(this, BLEDeviceSelectionActivity.class), 1);
//            } else {
                Intent intent = new Intent(this, ProfileCreationActivity.class);
                //   intent.putExtra("isUserCreated","true");
                startActivity(intent);
//            }
//            } else {
//                Intent intent = new Intent(this, ProfileCreationActivity.class);
//                intent.putExtra("isUserCreated","false");
//                startActivity(intent);
//            }
        }
    }


    public void goToDeviceSelection(View view) {
        ApplicationData.getInstance().clear();

        if (!isBluetoothDevicePaired()) {
            startActivityForResult(new Intent(this, BLEDeviceSelectionActivity.class).putExtra("mode", "expert_mode"), 2);
        } else {
            startActivity(new Intent(this, TimerSetActivity.class));
        }
    }

    private boolean isBluetoothDevicePaired() {
        String storedDevice = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE).getString("device_name", "");
        if (storedDevice.isEmpty()) {
            return false;
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            String result = data.getStringExtra("result");
            if (result.length() > 0) {
                Intent intent = new Intent(this, ProfileCreationActivity.class);
                startActivity(intent);
            }
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            String result = data.getStringExtra("result");
            if (result.length() > 0) {
                startActivity(new Intent(this, TimerSetActivity.class));
            }
        }
    }
}
