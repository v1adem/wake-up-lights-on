package com.v1adem.wakeup;

import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompat implements ChargingStatusListener{
    private LanguageManager languageManager;
    private ChargingReceiver receiver;
    private  MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Managing the main logic for start_button
        receiver = new ChargingReceiver(this);
        IntentFilter filter = new IntentFilter(Intent.ACTION_POWER_CONNECTED);
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
        mediaPlayer.setVolume(100, 100);

        Button startButton = findViewById(R.id.start_button);
        Button cancelButton = findViewById(R.id.cancel_button);
        startButton.setOnClickListener(v -> {
            startButton.setVisibility(View.INVISIBLE);
            cancelButton.setVisibility(View.VISIBLE);

            registerReceiver(receiver, filter);
        });
        cancelButton.setOnClickListener(v -> {
            startButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.INVISIBLE);

            unregisterReceiver(receiver); // Unregister the receiver to avoid memory leaks
            mediaPlayer.stop();
        });

        // Managing language changing
        languageManager = new LanguageManager(this);
        findViewById(R.id.language_button).setOnClickListener(v -> {
            if (languageManager.getLang().equals("en-US")) {
                languageManager.updateResource("uk");
            } else {
                languageManager.updateResource("en-US");
            }
            recreate();

            Log.d("BUTTONS", "User changed language");
        });

        // Managing the showing instruction
        // TODO


    }

    @Override
    public void onChargingStatusChanged(boolean isCharging) {
        if (isCharging) {
            // Device is charging
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
            Log.d("ChargingStatus", "Device is charging");
        } else {
            // Device is not charging
            Log.d("ChargingStatus", "Device is not charging");
        }
    }
}