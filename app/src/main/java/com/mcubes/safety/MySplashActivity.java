package com.mcubes.safety;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

public class MySplashActivity extends AppCompatActivity {
    private TextView rotateText ;
    SharedPreferences sharedPreferences;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_splash);
        rotateText = findViewById(R.id.splash);

        //Splash Animations
        Animation splashAnimation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.splash_animation);
        rotateText.setAnimation(splashAnimation);

        // Splash Screen Toast
        Toast toast = Toast.makeText(MySplashActivity.this,"Apps is running backround",Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM,0,0);
        toast.show();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sharedPreferences = getSharedPreferences(PreferenceKeys.PREFS_NAME,MODE_PRIVATE);
                Boolean check = sharedPreferences.getBoolean(PreferenceKeys.TERMS_ACCPETED,false);

                Intent iNext;
                if(check){ // if true terms and Conditoins are cheked akready
                    iNext = new Intent(MySplashActivity.this,MainActivity.class);
                }
                else { // for terms and conditions are nt accepted
                    iNext = new Intent(MySplashActivity.this, TermsAndConditionsActivity.class);
                }
                startActivity(iNext);
                finish();
            }
        },2000);

    }
}