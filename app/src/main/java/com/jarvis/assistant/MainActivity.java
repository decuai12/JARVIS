package com.jarvis.assistant;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.speech.tts.TextToSpeech;
import android.speech.RecognizerIntent;
import android.content.Intent;
import android.widget.Toast;
import java.util.Locale;
import java.util.ArrayList;

public class MainActivity extends Activity {

    TextView status;
    TextToSpeech jarvis;
    static final int SPEECH_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(30, 30, 30, 30);
        layout.setBackgroundColor(Color.rgb(10, 15, 25));

        TextView title = new TextView(this);
        title.setText("J.A.R.V.I.S");
        title.setTextSize(32);
        title.setTextColor(Color.CYAN);
        title.setGravity(Gravity.CENTER);

        status = new TextView(this);
        status.setText("Systems Online");
        status.setTextSize(18);
        status.setTextColor(Color.WHITE);
        status.setGravity(Gravity.CENTER);

        Button listen = new Button(this);
        listen.setText("TALK TO JARVIS");

        layout.addView(title);
        layout.addView(status);
        layout.addView(listen);

        setContentView(layout);

        jarvis = new TextToSpeech(this, result -> {
            if (result == TextToSpeech.SUCCESS) {
                jarvis.setLanguage(Locale.US);
            }
        });

        listen.setOnClickListener(v -> listen());
    }

    void listen() {
        Intent intent = new Intent(
            RecognizerIntent.ACTION_RECOGNIZE_SPEECH
        );

        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );

        intent.putExtra(
            RecognizerIntent.EXTRA_PROMPT,
            "Speak to JARVIS"
        );

        try {
            startActivityForResult(intent, SPEECH_CODE);
        } catch (Exception e) {
            status.setText("Speech recognition unavailable");
        }
    }

    @Override
    protected void onActivityResult(
        int requestCode, int resultCode, Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_CODE &&
            resultCode == RESULT_OK && data != null) {

            ArrayList<String> results =
                data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS
                );

            if (results != null && !results.isEmpty()) {
                processCommand(results.get(0));
            }
        }
    }

    void processCommand(String command) {
        String text = command.toLowerCase(Locale.ROOT);

        if (text.contains("hello") ||
            text.contains("مرحبا")) {

            reply("Hello Sir. I am JARVIS.");

        } else if (text.contains("time") ||
                   text.contains("الساعة")) {

            String time = new java.text.SimpleDateFormat(
                "hh:mm a", Locale.getDefault()
            ).format(new java.util.Date());

            reply("The time is " + time);

        } else {
            reply("Sorry Sir. I don't understand.");
        }
    }

    void reply(String message) {
        status.setText(message);
        jarvis.speak(
            message,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "jarvis"
        );
    }

    @Override
    protected void onDestroy() {
        if (jarvis != null) {
            jarvis.stop();
            jarvis.shutdown();
        }
        super.onDestroy();
    }
  }
