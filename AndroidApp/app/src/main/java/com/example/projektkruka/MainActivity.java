package com.example.projektkruka;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        outputText = findViewById(R.id.outputTextView);

        Button btnTand = findViewById(R.id.btnTand);
        Button btnSlack = findViewById(R.id.btnSlack);


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

        getData();
    }


    private void getData() {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    while(true){
                    value1 = getSiteString("http://84.217.9.249:3000/light/kugaljus");
                    value2 = getSiteString("http://84.217.9.249:3000/light/kugaljus");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            outputText = findViewById(R.id.outputTextView);
                            outputText2 = findViewById(R.id.outputTextView2);
                            if (outputText != null) {
                                outputText.setText(value1);
                            }
                            if (outputText2 != null) {
                                outputText2.setText(value2);
                            }
                        }
                    });
                    try {
                        Thread.sleep(4000);
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
                sendSiteString("http://84.217.9.249:3000/light/kugaljus","PUT");

            }
        }).start();
    }
    private void slackLampa(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendSiteString("http://84.217.9.249:3000/light/kugaljus","DELETE");

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
                output = (jsonObject.toString(4));// 4 is number of spaces for indent;
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



    }
