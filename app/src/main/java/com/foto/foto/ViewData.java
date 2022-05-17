package com.foto.foto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ViewData extends AppCompatActivity {
    List<Model> modelList = new ArrayList<>();
    RecyclerView list_data;
    RecyclerView.LayoutManager layoutManager;
    FirebaseFirestore firebaseFirestore;
    CustomAdapter customAdapter;
    ProgressDialog progressDialog;
    String data, screen;
    private TextToSpeech textToSpeech;
    String mostRecentUtteranceID;
    TextView status;
    Handler handler;
    Runnable runnable;
    String text_data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);
        firebaseFirestore = FirebaseFirestore.getInstance();
        progressDialog = new ProgressDialog(this);
        list_data = findViewById(R.id.list_recycler_view);
        status = findViewById(R.id.status);
        list_data.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        list_data.setLayoutManager(layoutManager);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS) {

                    int result = textToSpeech.setLanguage(Locale.getDefault());

                    if(result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(ViewData.this, "OOPS! Language is not supported.", Toast.LENGTH_SHORT).show();
                    } else {
                        ttsInitialized();
                    }
                } else {
                    Toast.makeText(ViewData.this, "Initialization failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Bundle bundle = getIntent().getExtras();
        if(bundle != null ) {
            data = bundle.getString("search_data");
            screen = bundle.getString("screen");
        }
        showData(data);
    }

    private void showData(String s) {
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Searching......");
        progressDialog.show();
        if(screen.equals("search")) {
            firebaseFirestore.collection("products")
                    .whereEqualTo("search",s.toLowerCase())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            modelList.clear();
                            progressDialog.dismiss();
                            for (DocumentSnapshot doc: task.getResult()) {
                                Model model = new Model(doc.getString("id"),
                                        doc.getString("title"),
                                        doc.getString("price"),
                                        doc.getString("mfgDate"),
                                        doc.getString("expiryDate"),
                                        doc.getString("barcodeId"),
                                        (List<String>) doc.get("ingredients")
                                );
                                modelList.add(model);
                            }
                            if(modelList.isEmpty()) {
                                status.setText("OOPS! No Product found");
                                HashMap<String, String> params = new HashMap<>();
                                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, mostRecentUtteranceID);
                                speak(params,"OOPS! We cannot find any product with that name.");
                                status.setVisibility(View.VISIBLE);
                            } else {
                                status.setVisibility(View.GONE);
                                for(int j=0; j<modelList.size();j++) {
                                    String title = modelList.get(j).getTitle();
                                    String price = modelList.get(j).getPrice();
                                    String mfgdate = modelList.get(j).getMfgDate();
                                    String expirydate = modelList.get(j).getExpiryDate();
                                    String barcodeId = modelList.get(j).getBarcodeId();
                                    List<String> ingredients = modelList.get(j).getIngredients();
                                    String concatenatedIngredientNames = "";
                                    for (int i = 0; i < ingredients.size(); i++) {
                                        concatenatedIngredientNames += ingredients.get(i);
                                        if (i < ingredients.size() - 1) concatenatedIngredientNames += ", ";
                                    }
                                    text_data = "Product "+(j+1)+ ", " + title+"," + "price ," +price+"Php ,"+"Manufacturing date" + mfgdate+", "+ "expiry date"
                                            + expirydate+", " + "ingredients" + concatenatedIngredientNames;
                                    HashMap<String, String> params = new HashMap<>();
                                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, mostRecentUtteranceID);
                                    speak(params,text_data);
                                }
                            }
                            customAdapter = new CustomAdapter(ViewData.this, modelList);
                            list_data.setAdapter(customAdapter);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ViewData.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            firebaseFirestore.collection("products")
                    .whereEqualTo("barcodeId",s)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            modelList.clear();
                            progressDialog.dismiss();
                            for (DocumentSnapshot doc: task.getResult()) {
                                Model model = new Model(doc.getString("id"),
                                        doc.getString("title"),
                                        doc.getString("price"),
                                        doc.getString("mfgDate"),
                                        doc.getString("expiryDate"),
                                        doc.getString("barcodeId"),
                                        (List<String>) doc.get("ingredients")
                                );
                                modelList.add(model);
                            }
                            if(modelList.isEmpty()) {
                                status.setText("OOPS! No Product found");
                                HashMap<String, String> params = new HashMap<>();
                                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, mostRecentUtteranceID);
                                speak(params,"OOPS! We cannot find any product with that barcode I.D. .");
                                status.setVisibility(View.VISIBLE);
                            } else {
                                status.setVisibility(View.GONE);
                                for(int j=0; j<modelList.size();j++) {
                                    String title = modelList.get(j).getTitle();
                                    String price = modelList.get(j).getPrice();
                                    String mfgdate = modelList.get(j).getMfgDate();
                                    String expirydate = modelList.get(j).getExpiryDate();
                                    String barcodeId = modelList.get(j).getBarcodeId();
                                    List<String> ingredients = modelList.get(j).getIngredients();
                                    String concatenatedIngredientNames = "";
                                    for (int i = 0; i < ingredients.size(); i++) {
                                        concatenatedIngredientNames += ingredients.get(i);
                                        if (i < ingredients.size() - 1) concatenatedIngredientNames += ", ";
                                    }
                                    text_data = "Product "+(j+1)+ ", " + title+"," + "price ," +price+"Php ,"+"Manufacturing date" + mfgdate+", "+ "expiry date"
                                            + expirydate+", " + "ingredients" + concatenatedIngredientNames;
                                    HashMap<String, String> params = new HashMap<>();
                                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, mostRecentUtteranceID);
                                    speak(params,text_data);
                                }
                            }
                            customAdapter = new CustomAdapter(ViewData.this, modelList);
                            list_data.setAdapter(customAdapter);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ViewData.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
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
                if (!utteranceId.equals(mostRecentUtteranceID)&&!utteranceId.equals(mostRecentUtteranceID+"-")) {
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
                        if(utteranceId.equals(mostRecentUtteranceID+"-")) {
                            listen();
                        } else {
                            handler = new Handler(Looper.getMainLooper());
                            runnable = new Runnable() {
                                @Override
                                public void run() {
                                    HashMap<String, String> params = new HashMap<>();
                                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, mostRecentUtteranceID+"-");
                                    speak(params,"To repeat the product details, say 1. To search again say 2. Back to home, say 3. To exit the app say 4 after the beep.");
                                    handler.removeCallbacks(runnable);

                                }
                            };
                            handler.postDelayed(runnable,2000);
                        }
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
    }
    private void speak(HashMap<String, String> params,String message) {
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, params);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0).trim().toLowerCase(Locale.ROOT);
                    if(text.equals("one") || text.equals("1")) {
                        if(text_data!=null) {
                            HashMap<String, String> params = new HashMap<>();
                            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, mostRecentUtteranceID);
                            speak(params,text_data);
                        } else {
                            HashMap<String, String> params = new HashMap<>();
                            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, mostRecentUtteranceID);
                            speak(params,"OOPS! We cannot find any product.");
                        }
                    } else if(text.equals("two") || text.equals("2") || text.equals("tu") ) {
                        Intent intent = new Intent(this, SearchActivity.class);
                        startActivity(intent);
                        finish();
                    } else if(text.equals("three") || text.equals("3")) {
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else if(text.equals("four") || text.equals("4")) {
                        finish();
                    }
                    else {
                        HashMap<String, String> params = new HashMap<>();
                        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, mostRecentUtteranceID+"-");
                        speak(params,"The command did not match. To repeat the product details, say 1. To search again say 2. Back to home, say 3. To exit the app say 4 after the beep.");
                        Toast.makeText(this, "No command matched. Try again!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    HashMap<String, String> params = new HashMap<>();
                    params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, mostRecentUtteranceID+"-");
                    speak(params,"No command detected. To repeat the product details, say 1. To search again say 2. Back to home, say 3. To exit the app say 4 after the beep.");
                    Toast.makeText(this, "No command detected. Try again!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    @Override
    protected void onDestroy() {
        if(textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
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
}