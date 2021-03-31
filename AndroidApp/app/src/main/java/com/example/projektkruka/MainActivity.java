package com.example.projektkruka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.FragmentTransaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private TextView outputText;
    private String lightvalue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        outputText = (TextView) findViewById(R.id.outputTextView);
        outputText.setText(String.valueOf(lightvalue));

        Button btnTand = findViewById(R.id.btnTand);
        Button btnSlack = findViewById(R.id.btnSlack);
        Button btnUpdate = findViewById(R.id.btnUpdate);

        btnTand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Tänd");

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
                lightvalue = getSiteString("https://tools.learningcontainer.com/sample-json-file.json");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        outputText.setText(lightvalue);

                    }
                });

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
                output = buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally{
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


    public void ChangeFragment(View view) {
        Fragment fragment;
        if (view == findViewById(R.id.btnkruka1)) {
            fragment = new Fragment1();
            FragmentManager fm = getSupportFragmentManager();
            androidx.fragment.app.FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.Fragment, fragment);
            ft.commit();
        }
        if (view == findViewById(R.id.btnkruka2)) {
            fragment = new Fragment2();
            FragmentManager fm = getSupportFragmentManager();
            androidx.fragment.app.FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.Fragment, fragment);
            ft.commit();
        }
        }



    }
