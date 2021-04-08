package com.example.projektkruka;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    private String lightvalue;
    private String tempvalue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        outputText = findViewById(R.id.outputTextView);

        Button btnTand = findViewById(R.id.btnTand);
        Button btnSlack = findViewById(R.id.btnSlack);
        Button btnUpdate = findViewById(R.id.btnUpdate);


        btnTand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doOtherStuff();
            }
        });
        btnSlack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Släck");

            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                doStuff();
            }
        });
    }

    private void doStuff(){
        new Thread(new Runnable(){

            @Override
            public void run() {
                lightvalue = getSiteString("https://jsonplaceholder.typicode.com/todos/1");
                tempvalue = getSiteString("https://jsonplaceholder.typicode.com/todos/1");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        outputText = findViewById(R.id.outputTextView);
                        outputText2 = findViewById(R.id.outputTextView2);
                        if(outputText != null) {
                            outputText.setText(lightvalue);
                        }
                        if(outputText2 != null) {
                            outputText2.setText(tempvalue);
                        }


                    }
                });

            }
        }).start();
    }

    private void doOtherStuff(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendSiteString("https://jsonplaceholder.typicode.com/todos/1","Tänd");

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
        String output = "";

        try {
            url = new URL(site);
            connection =(HttpURLConnection) url.openConnection();
            connection.connect();
            OutputStream stream = connection.getOutputStream();
            writer = new BufferedWriter(new OutputStreamWriter(stream));
            StringBuffer buffer = new StringBuffer();
            String line = "";
            while(message != null){
                buffer.append(message);
            }
            writer.write(String.valueOf(buffer));


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
