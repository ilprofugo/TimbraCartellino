package com.fabrizioconsalvi.timbramise;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private TextView risultatoPrimaUscita, risultatoPrimaUscitaConPP, risultatoUscitaMassima;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timePicker = findViewById(R.id.timePicker);
        risultatoPrimaUscita = findViewById(R.id.risultatoPrimaUscita);
        risultatoPrimaUscitaConPP = findViewById(R.id.risultatoPrimaUscitaConPP);
        risultatoUscitaMassima = findViewById(R.id.risultatoUscitaMassima);

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
}