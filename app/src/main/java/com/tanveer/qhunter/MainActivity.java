package com.tanveer.qhunter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button qrBtn, profileBtn, generateBtn, pdfBtn, jsonBtn, loginBtn, registerBtn;
    public static TextView textView;
    public static String result = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);

        loginBtn = (Button) findViewById(R.id.loginBtn);
        qrBtn = (Button) findViewById(R.id.qrBtn);
        profileBtn = (Button) findViewById(R.id.profileBtn);
        generateBtn = (Button) findViewById(R.id.generateBtn);
        pdfBtn = (Button) findViewById(R.id.pdfBtn);
        jsonBtn = (Button) findViewById(R.id.jsonBtn);
//        registerBtn = (Button) findViewById(R.id.registerBtn);

        qrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, QRScannerActivity.class));
            }
        });

        findViewById(R.id.registerBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });


        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    }
}
