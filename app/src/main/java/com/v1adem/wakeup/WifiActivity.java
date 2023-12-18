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
    private boolean receiverActive = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        Button startButton = findViewById(R.id.start_button);
        Button cancelButton = findViewById(R.id.cancel_button);
        Button languageButton = findViewById(R.id.language_button);
        Button byChargerButton = findViewById(R.id.byCharger_button);
        Button instructionButton = findViewById(R.id.instruction_button);

        // Managing wifi receiving
        receiver = new WifiReceiver(this);
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
        mediaPlayer.setVolume(100, 100);

        startButton.setOnClickListener(v -> {
            startButton.setVisibility(View.INVISIBLE);
            cancelButton.setVisibility(View.VISIBLE);

            languageButton.setEnabled(false);
            byChargerButton.setEnabled(false);

            registerReceiver(receiver, filter);
            receiverActive = true;
        });
        cancelButton.setOnClickListener(v -> {
            startButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.INVISIBLE);

            languageButton.setEnabled(true);
            byChargerButton.setEnabled(true);

            unregisterReceiver(receiver); // Unregister the receiver to avoid memory leaks
            receiverActive = false;
            mediaPlayer.stop();
            recreate();
        });

        // Managing language changing
        languageManager = new LanguageManager(this);
        languageButton.setOnClickListener(v -> {
            if (languageManager.getLang().equals("en-US")) {
                languageManager.updateResource("uk");
            } else {
                languageManager.updateResource("en-US");
            }
            recreate();

            Log.d("BUTTONS", "User changed language");
        });

        // Managing type of electricity receiving
        byChargerButton.setOnClickListener(v -> {
            if (receiverActive) {
                unregisterReceiver(receiver);
            }
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

        instructionButton.setOnClickListener(v -> {
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
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiverActive) {
            unregisterReceiver(receiver);
        }
    }
}
