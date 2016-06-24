package com.vaddi.hemanth.myhome;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class SettingsActivity extends AppCompatActivity {

    LinearLayout nameView;
    LinearLayout emailView;
    LinearLayout passwordView;
    TextView name;
    TextView email;
    TextView password;
    CheckBox isAutoMode;
    Switch isCelsius;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Firebase.setAndroidContext(this);
        final Firebase mySettings = new Firebase("https://miniprojecthello.firebaseio.com/settings");

        nameView = (LinearLayout)findViewById(R.id.nameView);
        emailView = (LinearLayout)findViewById(R.id.emailView);
        passwordView = (LinearLayout)findViewById(R.id.passwordView);
        name = (TextView)findViewById(R.id.name);
        email = (TextView)findViewById(R.id.email);
        password = (TextView)findViewById(R.id.password);
        isAutoMode = (CheckBox) findViewById(R.id.isAutoMode);
        isCelsius = (Switch) findViewById(R.id.isCelcius);

        nameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("Change Name");
                final EditText input = new EditText(SettingsActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText(name.getText());
                builder.setView(input);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mySettings.child("1").child("name").setValue(input.getText().toString());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
        passwordView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("Change Password");
                final EditText input = new EditText(SettingsActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText(password.getText());
                builder.setView(input);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mySettings.child("1").child("password").setValue(input.getText().toString());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
        emailView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("Change Email ID");
                final EditText input = new EditText(SettingsActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                input.setText(email.getText());
                builder.setView(input);
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mySettings.child("1").child("email").setValue(input.getText().toString());
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
        isAutoMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mySettings.child("1").child("autoMode").setValue(isChecked);
            }
        });
        isCelsius.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mySettings.child("1").child("temperatureUnit").setValue(isChecked);
                if(isChecked)
                    isCelsius.setText("Celcius");
                else
                    isCelsius.setText("Fahrenheit");
            }
        });

        mySettings.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("Settings",dataSnapshot+"");
                SettingsProperties settingsProperties = dataSnapshot.getValue(SettingsProperties.class);
                name.setText(settingsProperties.getName());
                email.setText(settingsProperties.getEmail());
                password.setText(settingsProperties.getPassword());
                isAutoMode.setChecked(settingsProperties.isAutoMode());
                isCelsius.setChecked(settingsProperties.getTemperatureUnit());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("Settings",dataSnapshot.getValue(SettingsProperties.class).toString());
                SettingsProperties settingsProperties = dataSnapshot.getValue(SettingsProperties.class);
                name.setText(settingsProperties.getName());
                email.setText(settingsProperties.getEmail());
                password.setText(settingsProperties.getPassword());
                isAutoMode.setChecked(settingsProperties.isAutoMode());
                isCelsius.setChecked(settingsProperties.getTemperatureUnit());
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
