package com.mcubes.safety;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mcubes.safety.service.MessageService;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText phoneNumberEditText_1,phoneNumberEditText_2,phoneNumberEditText_3,smsContentEditText;
    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final int SMS_PERMISSION_REQUEST_CODE = 1;

    Button saveButton,getlocationButton,sendSMSButton;

    SharedPreferences sharedPreferences;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //askpermissions
        askLocationPermission();


        if (!isMessageServiceRunning()) {
            Intent intent = new Intent(this, MessageService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            }
        }

        //phone number textEdit
        phoneNumberEditText_1=findViewById(R.id.phone_number_edit_text_1);
        phoneNumberEditText_2=findViewById(R.id.phone_number_edit_text_2);
        phoneNumberEditText_3=findViewById(R.id.phone_number_edit_text_3);
        smsContentEditText = findViewById(R.id.sms_content_edit_text);

        //button
        saveButton = findViewById(R.id.save_button);
        getlocationButton = findViewById(R.id.getLocation_button);
        sendSMSButton = findViewById(R.id.send_button);

        //initialize
        Intent locationIntent = new Intent(MainActivity.this,GetLocationActivity.class);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        sharedPreferences = getSharedPreferences(PreferenceKeys.PREFS_NAME, MODE_PRIVATE);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber1 = phoneNumberEditText_1.getText().toString();
                String phoneNumber2 = phoneNumberEditText_2.getText().toString();
                String phoneNumber3 = phoneNumberEditText_3.getText().toString();
                String smsContent = smsContentEditText.getText().toString();

                // Save data to SharedPreferences
                sharedPreferences = getSharedPreferences(PreferenceKeys.PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(PreferenceKeys.PHONE_NUMBER_KEY_1, phoneNumber1);
                editor.putString(PreferenceKeys.PHONE_NUMBER_KEY_2, phoneNumber2);
                editor.putString(PreferenceKeys.PHONE_NUMBER_KEY_3, phoneNumber3);
                editor.putString(PreferenceKeys.SMS_CONTENT_KEY, smsContent);
                editor.apply();

                Toast toast = Toast.makeText(MainActivity.this,"Saved",Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER,0,0);
                toast.show();
            }
        });

        // show text feild
        String savedPhoneNumber1 = sharedPreferences.getString(PreferenceKeys.PHONE_NUMBER_KEY_1,"");
        String savedPhoneNumber2 = sharedPreferences.getString(PreferenceKeys.PHONE_NUMBER_KEY_2,"");
        String savedPhoneNumber3 = sharedPreferences.getString(PreferenceKeys.PHONE_NUMBER_KEY_3,"");
        String savedSmsContent = sharedPreferences.getString(PreferenceKeys.SMS_CONTENT_KEY, "");
        phoneNumberEditText_1.setText(savedPhoneNumber1);
        phoneNumberEditText_2.setText(savedPhoneNumber2);
        phoneNumberEditText_3.setText(savedPhoneNumber3);
        smsContentEditText.setText(savedSmsContent);


        getlocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastLocation();
                startActivity(locationIntent);
            }
        });

        sendSMSButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences = getSharedPreferences(PreferenceKeys.PREFS_NAME, MODE_PRIVATE);
                // Load existing data from SharedPreferences
                String savedPhoneNumber1 = sharedPreferences.getString(PreferenceKeys.PHONE_NUMBER_KEY_1, "");
                String savedPhoneNumber2 = sharedPreferences.getString(PreferenceKeys.PHONE_NUMBER_KEY_2, "");
                String savedPhoneNumber3 = sharedPreferences.getString(PreferenceKeys.PHONE_NUMBER_KEY_3, "");
                String savedSmsContent = sharedPreferences.getString(PreferenceKeys.FULL_SMS_CONTENTS, "");
                sendSMS(savedPhoneNumber1,savedSmsContent);
                sendSMS(savedPhoneNumber2,savedSmsContent);
                sendSMS(savedPhoneNumber3,savedSmsContent);
            }
        });


    }



    private void getLastLocation(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
           fusedLocationProviderClient.getLastLocation()
                   .addOnSuccessListener(new OnSuccessListener<Location>() {
                       @Override
                       public void onSuccess(Location location) {
                           if(location != null){
                               Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
//                               List<Address> addresses=null;
                               try {
                                   List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                   double lattiude = addresses.get(0).getLatitude();
                                   double longitude = addresses.get(0).getLongitude();
                                   String city =addresses.get(0).getLocality();
                                   String country = addresses.get(0).getCountryName();
                                   String address= addresses.get(0).getAddressLine(0);



                                   String maplink ="https://maps.google.com/?q=" + lattiude+","+longitude;
                                   String fullLocationContents = "My Current location"+
                                           "\nMapLink: "+maplink;


                                   sharedPreferences = getSharedPreferences(PreferenceKeys.PREFS_NAME,MODE_PRIVATE);
                                   String smsContent= sharedPreferences.getString(PreferenceKeys.SMS_CONTENT_KEY,"");
                                   String fullSmsContents =smsContent+"\n"+ fullLocationContents;


                                   SharedPreferences.Editor editor = sharedPreferences.edit();
                                   editor.putString(PreferenceKeys.LATTITUDE_KEY, ""+lattiude);
                                   editor.putString(PreferenceKeys.LONGITUDE_KEY, ""+longitude);
                                   editor.putString(PreferenceKeys.CITY_KEY, city);
                                   editor.putString(PreferenceKeys.COUNTRY_KEY, country);
                                   editor.putString(PreferenceKeys.ADDRESS_KEY, address);
                                   editor.putString(PreferenceKeys.MAP_LINK_KEY,maplink);
                                   editor.putString(PreferenceKeys.FULL_LOCATION_CONTENTS,fullLocationContents);
                                   editor.putString(PreferenceKeys.FULL_SMS_CONTENTS,fullSmsContents);

                                   editor.apply();
                               }catch (IOException e){
                                   e.printStackTrace();
                               }

                           }
                       }
                   });
        }
        else {
            askLocationPermission();
        }
    }

    private void askLocationPermission(){
        ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // LOCATION PERMISSION
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }else {
                Toast.makeText(MainActivity.this,"Please provide the required permission",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    private void sendSMS(String phoneNumber, String message) {
        try {
            getLastLocation();
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            // just vibrate the device
            vibrateDevice();
            //toast
            Toast toast = Toast.makeText(MainActivity.this,"SMS send successfully",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(MainActivity.this,"Permission Not Allowed",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }

    }


    @SuppressWarnings("deprecation")
    public boolean isMessageServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo info : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (MessageService.class.getName().equals(info.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @SuppressLint("NewApi")
    private void vibrateDevice() {
        // Get the Vibrator service
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        if (vibrator != null) {
            // Vibrate for 500 milliseconds
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }


}