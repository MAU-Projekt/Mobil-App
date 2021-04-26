package com.example.projektkruka;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.jjoe64.graphview.GraphView;
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
    private LineGraphSeries<DataPoint> series;
    private GraphView graph;


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

        double y,x;
        x = 0.0;

        series= new LineGraphSeries<DataPoint>();
        for(int i =0; i<500; i++){
            x = x + 0.1;
            y = Math.sin(x);
            series.appendData(new DataPoint(x, y), true, 500);
        }

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

                @Override
                public void run() {
                    while(true){
                    value1 = getSiteString(url1);//url1
                    value2 = getSiteString(url2);//url2

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            outputText = findViewById(R.id.outputTextView);
                            outputText2 = findViewById(R.id.outputTextView2);
                            graph = (GraphView) findViewById(R.id.tempgraph);
                            if (outputText != null) {
                                outputText.setText(value1);
                            }
                            if (outputText2 != null) {
                                outputText2.setText(value2);
                            }
                            if(graph != null) {
                                graph.addSeries(series);
                            }
                        }
                    });
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }           }
                }
            }).start();
        }

    private void tandLampa(){
        new Thread(new Runnable() {
            @Override
            public void run() {
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

    private String getSiteString(String site){
        HttpURLConnection connection = null;
        URL url;
        BufferedReader reader = null;
        String output = "";

            try {
                url = new URL(site);
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
                //output = jsonObject.getJSONObject("cpu_thermal-virtual-0").getString("temp1");
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
                krukorTot = krukorTot+1;
                if(krukorTot == 1){
                    url1 = result.getContents();
                    editor.putString("url1", result.getContents()); // Storing string
                    editor.apply();
                    builder.setMessage("KRUKA1 tillagd");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else if(krukorTot ==2){
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
