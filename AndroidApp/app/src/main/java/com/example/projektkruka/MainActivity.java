package com.example.projektkruka;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;

public class MainActivity extends AppCompatActivity {
    private TextView outputText;
    private TextView outputText2;
    private String value1;
    private String value2;
    private String url1;
    private String url2;
    private int krukorTot = 0;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private LineGraphSeries<DataPoint> seriestemp;
    private LineGraphSeries<DataPoint> serieshum;
    private GraphView graphtemp;
    private GraphView graphhum;
    private int x = 0;
    private Timestamp timestamp = new Timestamp(System.currentTimeMillis());
    private long time = timestamp.getTime() - 172800000L; //2 dygn



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        outputText = findViewById(R.id.outputTextView);

        Button btnTand = findViewById(R.id.btnTand);
        Button btnSlack = findViewById(R.id.btnSlack);
        FloatingActionButton btnAdd = findViewById(R.id.btnaddqr);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // create sharedPreference
        editor = pref.edit(); // create editor
        url1 = pref.getString("url1", null); // getting String
        url2 = pref.getString("url2", null); // getting String
        System.out.println(url1);
        System.out.println(url2);

        seriestemp = new LineGraphSeries<DataPoint>();
        serieshum= new LineGraphSeries<DataPoint>();

        btnTand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tandLampa();
            }
        });
        btnSlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slackLampa();

            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCode();
            }
        });

        getData();
    }


    private void getData() {
            new Thread(new Runnable() {

                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void run() {
                    while(true){
                    value1 = getSiteString(url1);//url1
                    value2 = getSiteString(url2);//url2
                    if(time < timestamp.getTime()) {
                        time = time + 3600000L; //+1h i millis
                        getSiteString("https://kruka.xyz/data/test/" + time / 60000); //grafdata, timestamp i min
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            outputText = findViewById(R.id.outputTextView);
                            outputText2 = findViewById(R.id.outputTextView2);
                            graphtemp = (GraphView) findViewById(R.id.tempgraph);
                            graphhum = (GraphView) findViewById(R.id.humgraph);
                            if (outputText != null) {
                                outputText.setText(value1);
                            }
                            if (outputText2 != null) {
                                outputText2.setText(value2);
                            }
                            if(graphtemp != null) {
                                graphtemp.addSeries(seriestemp);
                                graphtemp.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(MainActivity.this));
                                graphtemp.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space
                                graphtemp.getViewport().setScalableY(true);
                            }
                            if(graphhum != null) {
                                graphhum.addSeries(serieshum);
                                graphhum.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(MainActivity.this));
                                graphhum.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space
                                graphhum.getViewport().setScalableY(true);
                            }
                        }
                    });
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                            }
                }
            }).start();
        }

    private void tandLampa(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                //if(outputText != null) tända rätt kruka
                //if(outputText2 != null) tända rätt kruka
                sendSiteString("http://84.217.9.249:80/light/kugaljus","PUT");

            }
        }).start();
    }
    private void slackLampa(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendSiteString("http://84.217.9.249:80/light/kugaljus","DELETE");

            }
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getSiteString(String site){
        HttpURLConnection connection = null;
        URL url;
        BufferedReader reader = null;
        String output = "";

            try {
                url = new URL(site);
                System.out.println(site);
                connection =(HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while((line = reader.readLine()) != null){
                    buffer.append(line);
                }
                JSONObject jsonObject = new JSONObject(String.valueOf(buffer));

                if(!site.equals(url1) && !site.equals(url2)){
                    jsonObject.keys().forEachRemaining(key -> {
                    try {
                        Object value = jsonObject.get(key);
                        JSONObject jsonObject2 = new JSONObject(String.valueOf(value));
                        int value2 = Integer.parseInt(jsonObject2.getString("temperature"));
                        int value3 = Integer.parseInt(jsonObject2.getString("humidity"));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(Integer.parseInt(key) > x) {
                                    seriestemp.appendData(new DataPoint(Double.parseDouble(key)*60000, value2), true, 1000);
                                    serieshum.appendData(new DataPoint(Double.parseDouble(key)*60000, value3), true, 1000);
                                    x = Integer.parseInt(key);
                                }
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    });
                }
                output = jsonObject.toString();
                output = output.replaceAll("[{}\"]","");


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally{
                if(connection != null) {
                    connection.disconnect();
                }
                try {
                    if(reader != null){
                        reader.close();}
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return output;



    }
    private void sendSiteString(String site, String message){
        HttpURLConnection connection = null;
        URL url;
        BufferedWriter writer = null;

        try {
            url = new URL(site);
            connection =(HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod(message);
            OutputStream stream = connection.getOutputStream();
            writer = new BufferedWriter(new OutputStreamWriter(stream));
            writer.write(message);
            writer.flush();
            connection.getInputStream();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(connection != null) {
                connection.disconnect();
            }
            try {
                if(writer != null){
                    writer.close();}
            } catch (IOException e) {
                e.printStackTrace();
            }
        }



    }
    private void scanCode(){
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scanning code");
        integrator.initiateScan();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null){
            if(result.getContents() != null){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                if(outputText != null){
                    url1 = result.getContents();
                    editor.putString("url1", result.getContents()); // Storing string
                    editor.apply();
                    builder.setMessage("KRUKA1 tillagd");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else if(outputText2 != null){
                    url2 = result.getContents();
                    editor.putString("url2", result.getContents()); // Storing string
                    editor.apply();
                    builder.setMessage("KRUKA2 tillagd");
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }



            }
            else{
                Toast.makeText(this, "no result", Toast.LENGTH_LONG).show();

            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }

    }



    }
