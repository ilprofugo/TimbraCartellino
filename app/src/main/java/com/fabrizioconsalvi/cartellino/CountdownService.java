package com.fabrizioconsalvi.cartellino;

import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class CountdownService extends Service {
    private static final long COUNTDOWN_INTERVAL = 1000; // Intervallo di 1 secondo
    private static final long COUNTDOWN_TIME = 7 * 60 * 60 * 1000 + 12 * 60 * 1000; // Tempo totale in millisecondi

    private CountDownTimer countDownTimer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startCountdown();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopCountdown();
    }

    private void startCountdown() {
        countDownTimer = new CountDownTimer(COUNTDOWN_TIME, COUNTDOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Calcola il tempo rimanente

                long remainingTimeMillis = millisUntilFinished;

                // Invia l'aggiornamento del tempo rimanente all'attività
                sendCountdownUpdate(remainingTimeMillis);

                // Esegui le azioni desiderate durante ogni tick del conto alla rovescia
                long hours = millisUntilFinished / (1000 * 60 * 60);
                long minutes = (millisUntilFinished % (1000 * 60 * 60)) / (1000 * 60);
                long seconds = (millisUntilFinished % (1000 * 60)) / 1000;

                String countdownText = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                Toast.makeText(CountdownService.this, countdownText, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                // Esegui le azioni desiderate quando il conto alla rovescia è terminato
                Toast.makeText(CountdownService.this, "Conto alla rovescia terminato", Toast.LENGTH_SHORT).show();
            }
        };

        countDownTimer.start();
    }

    private void stopCountdown() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Metodo per inviare un broadcast con il tempo rimanente
    private void sendCountdownUpdate(long remainingTimeMillis) {
        Intent intent = new Intent("COUNTDOWN_UPDATE");
        intent.putExtra("remaining_time", remainingTimeMillis);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}

