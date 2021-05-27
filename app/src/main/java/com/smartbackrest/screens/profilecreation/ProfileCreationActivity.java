package com.smartbackrest.screens.profilecreation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shawnlin.numberpicker.NumberPicker;
import com.smartbackrest.ApplicationData;
import com.smartbackrest.screens.mealselection.MealActivity;
import com.smartbackrest.screens.medicalprofile.MedicalProfileActivity;
import com.smartbackrest.R;
import com.smartbackrest.db.DBManager;
import com.smartbackrest.model.User;
import com.smartbackrest.screens.help.HelpActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ProfileCreationActivity extends AppCompatActivity {

    private static final String TAG = "ProfileCreationActivity";

    private Context context;
    private long selectedDate = -1L;
    private boolean isMale, isFemale;
    private Chip chipMale, chipFemale;
    private boolean useSlider = true;
    private int age = -1;
    private Spinner spinner;
    RelativeLayout layout_createUser, layout_userDetails;
    private ProgressBar progressBar;
    private DBManager dbManager;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_creation);
        //Set title
        TextView title = findViewById(R.id.screen_title);
        title.setText("Create Profile");

        //Back Icon click
        ImageView backIcon = findViewById(R.id.iv_backIcon);
        backIcon.setOnClickListener(view -> finish());

        context = this;

        db = FirebaseFirestore.getInstance();
        dbManager = new DBManager(this).open();

        TextInputLayout textInputCustomEndIcon = findViewById(R.id.layoutBirthDate);
        textInputCustomEndIcon.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                Calendar calendar1 = Calendar.getInstance();
                                calendar1.set(year, month, day);

                                selectedDate = calendar1.getTimeInMillis();
                                ((EditText) findViewById(R.id.edtBirthDate)).setText(day + "/" + (month + 1) + "/" + year);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        chipMale = (Chip) findViewById(R.id.chipMale);
        chipFemale = (Chip) findViewById(R.id.chipFemale);
        spinner = (Spinner) findViewById(R.id.spinner);
        progressBar = findViewById(R.id.progressBar);

        List<String> list = new ArrayList<String>();
        list.add("10 - 30");
        list.add("31 - 50");
        list.add("51 - 70");
        list.add("71 - 90");
        list.add("91 - 120");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_age_group, list);
        dataAdapter.setDropDownViewResource(R.layout.spinner_age_group);
        spinner.setAdapter(dataAdapter);
        spinner.setPrompt("Select Age");
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if (list.get(position).equals("10 - 30")) {
                    age = 20;
                } else if (list.get(position).equals("31 - 50")) {
                    age = 40;
                } else if (list.get(position).equals("51 - 70")) {
                    age = 60;
                } else if (list.get(position).equals("71 - 90")) {
                    age = 80;
                } else if (list.get(position).equals("91 - 120")) {
                    age = 100;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        if (useSlider) {
            findViewById(R.id.number_picker).setVisibility(View.GONE);
            findViewById(R.id.txtSelectAge).setVisibility(View.VISIBLE);

            (findViewById(R.id.layoutBirthDate)).setVisibility(View.INVISIBLE);
        } else {
            findViewById(R.id.number_picker).setVisibility(View.GONE);
            findViewById(R.id.txtSelectAge).setVisibility(View.GONE);

            (findViewById(R.id.layoutBirthDate)).setVisibility(View.VISIBLE);
        }

        ((NumberPicker) findViewById(R.id.number_picker)).setOnValueChangedListener((picker, oldVal, newVal) -> age = newVal);

        findViewById(R.id.layoutHelp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileCreationActivity.this, HelpActivity.class);
                intent.putStringArrayListExtra("list", new ArrayList<String>() {{
                    add("In this page the application asks for the basic details of the patient and the date of birth which is" +
                            " very important to enter as it is a part of calculating the time of the decline event of the Smart Bed\n" +
                            "Backrest.");
                }});
                startActivity(intent);
            }
        });

        layout_userDetails = findViewById(R.id.layout_userDetails);
        layout_createUser = findViewById(R.id.layout_createUser);

//        if (getIntent() != null) {
//            if (getIntent().getStringExtra("isUserCreated") != null) {
//                String isUserCreated = getIntent().getStringExtra("isUserCreated");
//                if (isUserCreated.equals("true")) {
//                    if (isInternetConnectionOn()) {
//                        fetchUserFromFirestore();
//                    } else {
//                        User user = dbManager.fetchUser();
//                        setUserDetails(user);
//                    }
//                    layout_createUser.setVisibility(View.GONE);
//                    layout_userDetails.setVisibility(View.VISIBLE);
//                } else {
//                    layout_createUser.setVisibility(View.VISIBLE);
//                    layout_userDetails.setVisibility(View.GONE);
//                }
//            }
//        }

        progressBar.setVisibility(View.VISIBLE);
        if (isInternetConnectionOn()) {
            fetchUserFromFirestore();
        } else {
            User user = dbManager.fetchUser();
            setUserDetails(user);
        }

    }

    private boolean isInternetConnectionOn() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    public void setUserDetails(User user) {
        if (user != null) {
            layout_createUser.setVisibility(View.GONE);
            layout_userDetails.setVisibility(View.VISIBLE);

            TextView title = findViewById(R.id.screen_title);
            title.setText("Profile");
        } else {
            layout_createUser.setVisibility(View.VISIBLE);
            layout_userDetails.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            TextView title = findViewById(R.id.screen_title);
            title.setText("Create Profile");

            return;
        }

        TextView tvFirstName = findViewById(R.id.tvFirstName);
        tvFirstName.setText(user.getFirstName());

        TextView tvLastName = findViewById(R.id.tvLastName);
        tvLastName.setText(user.getLastName());

        TextView tvAge = findViewById(R.id.tvAge);
        String age = "";

        if (user.getDob() == 20) {
            age = "10 - 30";
        } else if (user.getDob() == 40) {
            age = "31 - 50";
        } else if (user.getDob() == 60) {
            age = "51 - 70";
        } else if (user.getDob() == 80) {
            age = "71 - 90";
        } else if (user.getDob() == 100) {
            age = "91 - 120";
        }
        tvAge.setText(age);

        TextView tvGender = findViewById(R.id.tvGender);
        if (user.getGender() == 1) {
            String gender = "male";
            tvGender.setText(gender);
        } else {
            String gender = "female";
            tvGender.setText(gender);
        }


        boolean hasDiabetes = user.isHasDiabetes();
        boolean hasParkinsons = user.isHasParkinsons();
        boolean hasScleroderma = user.isHasScleroderma();
        boolean hasMultipleSclerosis = user.isHasMultipleSclerosis();
        boolean onDigestiveMedication = user.isOnDigestiveMedication();
        boolean hasAnxietyIssues = user.isHasAnxietyIssues();
        boolean hasStomachSurgeries = user.isHasStomachSurgeries();

        TextView txtDiabetes = findViewById(R.id.txtDiabetes);
        if (hasDiabetes) {
            txtDiabetes.setVisibility(View.VISIBLE);
        } else {
            txtDiabetes.setVisibility(View.GONE);
        }
        TextView txtParkinsons = findViewById(R.id.txtParkinsons);
        if (hasParkinsons) {
            txtParkinsons.setVisibility(View.VISIBLE);
        } else {
            txtParkinsons.setVisibility(View.GONE);
        }
        TextView txtScleroderma = findViewById(R.id.txtScleroderma);
        if (hasScleroderma) {
            txtScleroderma.setVisibility(View.VISIBLE);
        } else {
            txtScleroderma.setVisibility(View.GONE);
        }
        TextView txtMultiplesclerosis = findViewById(R.id.txtMultiplesclerosis);
        if (hasMultipleSclerosis) {
            txtMultiplesclerosis.setVisibility(View.VISIBLE);
        } else {
            txtMultiplesclerosis.setVisibility(View.GONE);
        }

        TextView tvDigestiveMedication = findViewById(R.id.tvDigestiveMedication);
        if (onDigestiveMedication) {
            tvDigestiveMedication.setText("YES");
        } else {
            tvDigestiveMedication.setText("NO");
        }
        TextView tvAnxietyIssues = findViewById(R.id.tvAnxietyIssues);
        if (hasAnxietyIssues) {
            tvAnxietyIssues.setText("YES");
        } else {
            tvAnxietyIssues.setText("NO");
        }
        TextView tvStomachSurgeries = findViewById(R.id.tvStomachSurgeries);
        if (hasStomachSurgeries) {
            tvStomachSurgeries.setText("YES");
        } else {
            tvStomachSurgeries.setText("NO");
        }


        Button btnEdit = findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(v -> {
            dbManager.clearUsers();
            db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())
                    .delete();
            layout_createUser.setVisibility(View.VISIBLE);
            layout_userDetails.setVisibility(View.GONE);
        });


        Button btnContinue = findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(v -> {
            startActivity(new Intent(this, MealActivity.class));
        });

//        txtDiseases.setText(user.g());

        progressBar.setVisibility(View.GONE);

    }

    private void fetchUserFromFirestore() {
        DocumentReference docRef = db.collection("Users").document(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                User user = documentSnapshot.toObject(User.class);

                setUserDetails(user);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                layout_createUser.setVisibility(View.VISIBLE);
                layout_userDetails.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void goForMedicalProfile(View view) {
        String fName = ((EditText) findViewById(R.id.edtFName)).getText().toString();
        String lName = ((EditText) findViewById(R.id.edtLName)).getText().toString();
        String ageET = ((EditText) findViewById(R.id.edtBirthDate)).getText().toString();

        if (!useSlider)
            Log.i(TAG, String.format("goForMedicalProfile: age is %s", ((EditText) findViewById(R.id.edtBirthDate)).getText().toString()));
        else
            Log.i(TAG, String.format("goForMedicalProfile: age is %s", age));

        if (!fName.isEmpty()) {
            if (!lName.isEmpty()) {

                if (!useSlider) {
                    if (!ageET.isEmpty() && Integer.valueOf(ageET) > 10) {
                        verifyGenderAndGoToNext(fName, lName, Integer.valueOf(ageET));
                    } else {
                        Toast.makeText(context, "Please enter valid age", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (age != -1 && age > 10) {
                        verifyGenderAndGoToNext(fName, lName, age);
                    } else {
                        Toast.makeText(context, "Please enter valid age", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(context, "Last name required", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "First name required", Toast.LENGTH_SHORT).show();
        }
    }

    public void finishActivity(View view) {
        finish();
    }


    private void verifyGenderAndGoToNext(String fName, String lName, int age) {
        if (chipMale.isChecked() || chipFemale.isChecked()) {
            ApplicationData.getInstance().getUser().setFirstName(fName).setLastName(lName).setDob(age).setGender(chipMale.isChecked() ? 1 : 0);
            startActivity(new Intent(this, MedicalProfileActivity.class));
        } else {
            Toast.makeText(context, "Please select your gender", Toast.LENGTH_SHORT).show();
        }
    }
}
