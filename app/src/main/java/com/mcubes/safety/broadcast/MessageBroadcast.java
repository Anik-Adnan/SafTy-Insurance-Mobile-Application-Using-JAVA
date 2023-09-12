package com.mcubes.safety.broadcast;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.VIBRATOR_SERVICE;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.mcubes.safety.MainActivity;
import com.mcubes.safety.PreferenceKeys;
import com.mcubes.safety.R;

@SuppressWarnings("deprecation")
public class MessageBroadcast extends BroadcastReceiver {
    private Context context;
    private static MessageBroadcast instance;
    private final Handler handler = new Handler();
    private int pressCount = 0;
    private static final int REQUIRED_PRESS_COUNT = 3;
    private static final long TIME_WINDOW_MS = 1000;
    private final Runnable resetCounterRunnable = () -> pressCount = 0;
    SharedPreferences sharedPreferences;


    private MessageBroadcast() {
    }

    public static MessageBroadcast getInstance() {
        if (instance == null)
            instance = new MessageBroadcast();
        return instance;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String action = intent.getAction();
        if (action != null && action.equals("android.media.VOLUME_CHANGED_ACTION")) {
            pressCount++;
            if (pressCount == REQUIRED_PRESS_COUNT) {
                sharedPreferences = context.getSharedPreferences(PreferenceKeys.PREFS_NAME, MODE_PRIVATE);
                String savedPhoneNumber1 = sharedPreferences.getString(PreferenceKeys.PHONE_NUMBER_KEY_1, "");
                String savedPhoneNumber2 = sharedPreferences.getString(PreferenceKeys.PHONE_NUMBER_KEY_2, "");
                String savedPhoneNumber3 = sharedPreferences.getString(PreferenceKeys.PHONE_NUMBER_KEY_3, "");
                String savedSmsContent = sharedPreferences.getString(PreferenceKeys.FULL_SMS_CONTENTS, "");
                // sending sms
                sendMessageToFriend(savedPhoneNumber1,savedSmsContent);
                sendMessageToFriend(savedPhoneNumber2,savedSmsContent);
                sendMessageToFriend(savedPhoneNumber3,savedSmsContent);

                pressCount=0;

            }
            handler.postDelayed(resetCounterRunnable, TIME_WINDOW_MS);
        }
    }

    public void sendMessageToFriend(String phoneNumber,String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);

            Toast toast = Toast.makeText(context,"SMS send successfully",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(context,"Permission Not Allowed",Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
        }
    }




}
