package com.v1adem.wakeup;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;


public class ChargerActivity extends AppCompat implements ChargingStatusListener{
    private LanguageManager languageManager;
    private ChargingReceiver receiver;
    private  MediaPlayer mediaPlayer;
    private boolean receiverActive = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charger);

        Button startButton = findViewById(R.id.start_button);
        Button cancelButton = findViewById(R.id.cancel_button);
        Button languageButton = findViewById(R.id.language_button);
        Button byWifiButton = findViewById(R.id.byWifi_button);
        Button instructionButton = findViewById(R.id.instruction_button);


        // Managing the main logic for start_button
        receiver = new ChargingReceiver(this);
        IntentFilter filter = new IntentFilter(Intent.ACTION_POWER_CONNECTED);
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
        mediaPlayer.setVolume(100, 100);

        startButton.setOnClickListener(v -> {
            startButton.setVisibility(View.INVISIBLE);
            cancelButton.setVisibility(View.VISIBLE);

            languageButton.setEnabled(false);
            byWifiButton.setEnabled(false);

            registerReceiver(receiver, filter);
            receiverActive = true;
        });
        cancelButton.setOnClickListener(v -> {
            startButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.INVISIBLE);

            languageButton.setEnabled(true);
            byWifiButton.setEnabled(true);

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
        byWifiButton.setOnClickListener(v -> {
            if (receiverActive) {
                unregisterReceiver(receiver);
            }
            Intent i = new Intent(getApplicationContext(), WifiActivity.class);
            startActivity(i);
        });

        // Managing the showing instruction
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.activity_charger_instruction, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setView(promptsView);

        // Instruction dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        instructionButton.setOnClickListener(v -> {
            alertDialog.show();
            Log.d("BUTTONS", "User opened instruction");
        });
    }

    @Override
    public void onChargingStatusChanged(boolean isCharging) {
        if (isCharging) {
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