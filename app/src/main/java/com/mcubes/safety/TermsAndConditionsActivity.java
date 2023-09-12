package com.mcubes.safety;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class TermsAndConditionsActivity extends AppCompatActivity {
    private CheckBox checkBoxAcceptt;
    private Button acceptButtonn;
     SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);

        checkBoxAcceptt = findViewById(R.id.checkBoxAccept);
        acceptButtonn = findViewById(R.id.acceptButton);

        acceptButtonn.setEnabled(false);

        checkBoxAcceptt.setOnCheckedChangeListener((buttonView, isChecked) -> {
            acceptButtonn.setEnabled(isChecked);
        });


        acceptButtonn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences(PreferenceKeys.PREFS_NAME,MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(PreferenceKeys.TERMS_ACCPETED,true);
                editor.apply();

                Intent mainActivity = new Intent(TermsAndConditionsActivity.this,MainActivity.class);
                startActivity(mainActivity);
                finish();

            }
        });

    }
}