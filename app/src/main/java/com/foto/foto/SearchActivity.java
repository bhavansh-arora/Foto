package com.foto.foto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class SearchActivity extends AppCompatActivity {
    private TextToSpeech textToSpeech;
    String mostRecentUtteranceID;
    private EditText name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        name = findViewById(R.id.name);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS) {

                    int result = textToSpeech.setLanguage(Locale.getDefault());

                    if(result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(SearchActivity.this, "OOPS! Language is not supported.", Toast.LENGTH_SHORT).show();
                    } else {
                        ttsInitialized();
                    }
                } else {
                    Toast.makeText(SearchActivity.this, "Initialization failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        if(textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0).trim().toLowerCase();
                    String altered_text = text.replace('-', ' ');
                    Intent intent = new Intent(this, ViewData.class);
                    intent.putExtra("search_data",altered_text);
                    intent.putExtra("screen","search");
                    startActivity(intent);
                    finish();

                } else {
                    HashMap<String, String> params = new HashMap<>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, mostRecentUtteranceID);
                    speak(params,"No command detected. Please say the product name after the beep.");
                    Toast.makeText(this, "No command detected. Try again!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void ttsInitialized() {

        // *** set UtteranceProgressListener AFTER tts is initialized ***
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            // this method will always called from a background thread.
            public void onDone(String utteranceId) {
                // only respond to the most recent utterance
                if (!utteranceId.equals(mostRecentUtteranceID)) {
                    Log.i("XXX", "onDone() blocked: utterance ID mismatch.");
                    return;
                } // else continue...

                boolean wasCalledFromBackgroundThread = (Thread.currentThread().getId() != 1);
                Log.i("XXX", "was onDone() called on a background thread? : " + wasCalledFromBackgroundThread);

                Log.i("XXX", "onDone working.");

                // for demonstration only... avoid references to
                // MainActivity (unless you use a WeakReference)
                // inside the onDone() method, as it
                // can cause a memory leak.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // *** toast will not work if called from a background thread ***
                        listen();
                    }
                });
            }

            @Override
            public void onError(String utteranceId) {

            }
        });

        // set unique utterance ID for each utterance
        mostRecentUtteranceID = (new Random().nextInt() % 9999999) + ""; // "" is String force

        // set params
        // *** this method will work for more devices: API 19+ ***
        HashMap<String, String> params = new HashMap<>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, mostRecentUtteranceID);
        speak(params,"You choosed product search. Please say the product name after the beep.");
    }
    private void speak(HashMap<String, String> params,String message) {
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, params);
    }
    public void listen() {
        //intent is show speech to text dialog
        Intent intent= new Intent( RecognizerIntent.ACTION_RECOGNIZE_SPEECH );
        intent.putExtra( RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Start intent
        try {
            startActivityForResult( intent, 10);
        }

        catch(Exception e){
            Toast.makeText( this,""+e.getMessage(), Toast.LENGTH_SHORT ).show();
            textToSpeech.speak(e.getMessage(), TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    public void search(View view) {
        if(name.getText().toString().trim().isEmpty()) {
            name.setError("Please enter the product name");
            name.requestFocus();
        } else {
            Intent intent = new Intent(this, ViewData.class);
            intent.putExtra("search_data",name.getText().toString().trim());
            intent.putExtra("screen","search");
            startActivity(intent);
            finish();
        }
    }
}