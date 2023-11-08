package com.v1adem.wakeup;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class WifiActivity extends AppCompat implements WifiStatusListener{
    private LanguageManager languageManager;
    private WifiReceiver receiver;
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        // Managing wifi receiving
        receiver = new WifiReceiver(this);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
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

        // Managing type of electricity receiving
        findViewById(R.id.byCharger_button).setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(),ChargerActivity.class);
            startActivity(i);
        });

        // Managing the showing instruction
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.activity_wifi_instruction, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setView(promptsView);

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        findViewById(R.id.instruction_button).setOnClickListener(v -> {
            alertDialog.show();
            Log.d("BUTTONS", "User opened instruction");
        });
    }

    @Override
    public void onWifiStatusChanged(boolean isWifi) {
        if (isWifi) {
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
