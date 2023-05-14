package com.fabrizioconsalvi.cartellino;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private Button timbraButton, uscitaButton, resetButton;
    private TextView risultatoPrimaUscita, risultatoPrimaUscitaConPP, risultatoUscitaMassima, tvTempoRimanente;
    private TextView countdownTextView;
    private CountDownTimer countDownTimer;
    private long remainingTimeMillis;
    private static final String CHANNEL_ID = "countdown_channel";
    private static final int NOTIFICATION_ID = 1;
    private boolean notificationSent = false;
    private boolean entrataTimbrata = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Countdown", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        timePicker = findViewById(R.id.timePicker);
        timbraButton = findViewById(R.id.entrataButton);
        risultatoPrimaUscita = findViewById(R.id.risultatoPrimaUscita);
        risultatoPrimaUscitaConPP = findViewById(R.id.risultatoPrimaUscitaConPP);
        risultatoUscitaMassima = findViewById(R.id.risultatoUscitaMassima);
        countdownTextView = findViewById(R.id.countdownTextView);
        tvTempoRimanente = findViewById(R.id.tvTempoRimanente);
        uscitaButton = findViewById(R.id.uscitaButton);
        resetButton = findViewById(R.id.resetButton);


        timbraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entrataTimbrata = true;
                // Avvia il servizio CountdownService
                startService(new Intent(getApplicationContext(), CountdownService.class));

                countdownTextView.setVisibility(View.VISIBLE);
                tvTempoRimanente.setVisibility(View.VISIBLE);
                timbraButton.setEnabled(false);
                timbraButton.setAlpha(0.5f);

                // Resta abilitato il pulsante "uscitaButton"
                uscitaButton.setEnabled(true);
                uscitaButton.setAlpha(1.0f);

                // Resta abilitato il pulsante "resetButton"
                resetButton.setEnabled(true);
                resetButton.setAlpha(1.0f);
                startCountdown();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                entrataTimbrata = true;

                // Interrompe il servizio CountdownService
                stopService(new Intent(getApplicationContext(), CountdownService.class));

                countdownTextView.setVisibility(View.VISIBLE);
                tvTempoRimanente.setVisibility(View.VISIBLE);
                timbraButton.setEnabled(true);
                timbraButton.setAlpha(1f);

                // Resta abilitato il pulsante "uscitaButton"
                uscitaButton.setEnabled(false);
                uscitaButton.setAlpha(0.5f);

                // Resta abilitato il pulsante "resetButton"
                resetButton.setEnabled(true);
                resetButton.setAlpha(1.0f);
                tvTempoRimanente.setVisibility(View.GONE);
                countdownTextView.setVisibility(View.GONE);

                //  Stop countDownTimer
                countDownTimer.cancel();
                remainingTimeMillis = 0;
                updateCountdownTextView();
            }
        });

        // Imposta il valore di default del TimePicker all'orario attuale
        Calendar currentTime = Calendar.getInstance();
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);
        timePicker.setHour(currentHour);
        timePicker.setMinute(currentMinute);

        updateTimeTextView(currentHour, currentMinute, 7, 12, risultatoPrimaUscita);
        updateTimeTextView(currentHour, currentMinute, 7, 42, risultatoPrimaUscitaConPP);
        updateTimeTextView(currentHour, currentMinute, 9, 24, risultatoUscitaMassima);

        // Aggiorna la TextView quando viene modificato il TimePicker
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                updateTimeTextView(hourOfDay, minute, 7, 12, risultatoPrimaUscita);
                updateTimeTextView(hourOfDay, minute, 7, 42, risultatoPrimaUscitaConPP);
                updateTimeTextView(hourOfDay, minute, 9, 24, risultatoUscitaMassima);
            }
        });
    }

    private void updateTimeTextView(int hourOfDay, int minute, int ore, int minuti, TextView tvDaAggiornare) {
        // Aggiungi 7 ore e 12 minuti all'ora selezionata
        Calendar updatedTime = Calendar.getInstance();
        updatedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        updatedTime.set(Calendar.MINUTE, minute);
        updatedTime.add(Calendar.HOUR_OF_DAY, ore);
        updatedTime.add(Calendar.MINUTE, minuti);

        // Formatta l'orario nel formato desiderato (HH:mm)
        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d",
                updatedTime.get(Calendar.HOUR_OF_DAY),
                updatedTime.get(Calendar.MINUTE));

        // Aggiorna la TextView con il nuovo orario
        tvDaAggiornare.setText(formattedTime);
    }

    private void startCountdown() {
        // Ottieni l'ora attuale
        Calendar currentTime = Calendar.getInstance();
        int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentTime.get(Calendar.MINUTE);
        int currentSecond = currentTime.get(Calendar.SECOND);

        // Aggiungi 7 ore e 12 minuti all'ora attuale
        currentTime.add(Calendar.HOUR_OF_DAY, 7);
        currentTime.add(Calendar.MINUTE, 12);
        currentTime.add(Calendar.SECOND, 0);

        // Calcola la differenza in millisecondi tra l'ora attuale e l'ora calcolata
        long targetTimeMillis = currentTime.getTimeInMillis();
        long currentTimeMillis = System.currentTimeMillis();
        long differenceMillis = targetTimeMillis - currentTimeMillis;

        // Avvia il contatore con la differenza di tempo rimanente
        countDownTimer = new CountDownTimer(differenceMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTimeMillis = millisUntilFinished;
                updateCountdownTextView();
            }

            @Override
            public void onFinish() {
                remainingTimeMillis = 0;
                updateCountdownTextView();
            }
        };

        countDownTimer.start();
    }

    // Aggiorna la countdownTextView con il tempo rimanente
    private void updateCountdownTextView(long remainingTimeMillis) {
        // Conversione del tempo rimanente in ore, minuti e secondi
        int hours = (int) (remainingTimeMillis / (1000 * 60 * 60));
        int minutes = (int) ((remainingTimeMillis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) ((remainingTimeMillis % (1000 * 60)) / 1000);

        // Formatta il tempo rimanente nel formato desiderato (HH:mm:ss)
        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

        // Aggiorna la TextView con il tempo rimanente
        countdownTextView.setText(formattedTime);
    }

    private void updateCountdownTextView() {
        // Conversione del tempo rimanente in ore, minuti e secondi

        int hours = (int) (remainingTimeMillis / (1000 * 60 * 60));
        int minutes = (int) ((remainingTimeMillis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) ((remainingTimeMillis % (1000 * 60)) / 1000);

        // Formatta il tempo rimanente nel formato desiderato (HH:mm:ss)
        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);

        // Aggiorna la TextView con il tempo rimanente
        countdownTextView.setText(formattedTime);

        // Controlla se mancano 15 minuti allo scadere e invia la notifica solo una volta
        if (hours == 0 && minutes == 15 && !notificationSent) {
            sendNotification("Mancano 15 minuti allo scadere!");
            notificationSent = true;
        }
    }

    private void sendNotification(String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder builder = new Notification.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_bell)
                .setContentTitle("Countdown Notification")
                .setContentText(message)
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }

        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Assicurati di fermare il contatore quando l'Activity viene distrutta
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    // Dichiarazione del BroadcastReceiver per ricevere l'aggiornamento del countdown
    private BroadcastReceiver countdownReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long remainingTimeMillis = intent.getLongExtra("remaining_time", 0);
            updateCountdownTextView(remainingTimeMillis);
        }
    };


    // Registra il BroadcastReceiver nell'onResume dell'attività
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter("COUNTDOWN_UPDATE");
        LocalBroadcastManager.getInstance(this).registerReceiver(countdownReceiver, intentFilter);
    }

    // Deregistra il BroadcastReceiver nell'onPause dell'attività
    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(countdownReceiver);
    }
}