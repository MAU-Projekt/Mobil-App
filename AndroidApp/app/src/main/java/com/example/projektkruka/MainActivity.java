package com.example.projektkruka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
    private TextView outputText;
    private int lightvalue = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        outputText = (TextView) findViewById(R.id.outputTextView);
        outputText.setText(String.valueOf(lightvalue));

        Button btnTand = findViewById(R.id.btnTand);
        Button btnSlack = findViewById(R.id.btnSlack);

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
