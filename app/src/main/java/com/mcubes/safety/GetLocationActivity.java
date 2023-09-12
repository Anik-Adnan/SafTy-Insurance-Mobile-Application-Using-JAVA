package com.mcubes.safety;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;

public class GetLocationActivity extends AppCompatActivity {
     TextView locationDetailsTextView;
     Button copyButton;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);

        locationDetailsTextView = findViewById(R.id.location_text_view);
        copyButton = findViewById(R.id.copy_button);



        sharedPreferences = getSharedPreferences(PreferenceKeys.PREFS_NAME, MODE_PRIVATE);
        String details = sharedPreferences.getString(PreferenceKeys.FULL_SMS_CONTENTS,"");
        locationDetailsTextView.setText(details);

        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String copyText = locationDetailsTextView.getText().toString();
                ClipboardManager clipboardManager = (ClipboardManager)  getSystemService(CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Copied Text",copyText);
                clipboardManager.setPrimaryClip(clipData);

                Toast toast = Toast.makeText(GetLocationActivity.this,"Copied",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        });
    }
}