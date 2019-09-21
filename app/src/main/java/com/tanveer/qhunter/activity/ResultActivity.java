package com.tanveer.qhunter.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.tanveer.qhunter.R;

import org.json.JSONException;
import org.json.JSONObject;

public class ResultActivity extends AppCompatActivity {
    TextView result;
    JSONObject jsonObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        result = (TextView) findViewById(R.id.result);
        result.setText(getIntent().getStringExtra("result"));
        try {
            jsonObject = new JSONObject(result.getText().toString());
            Toast.makeText(this, jsonObject.getString("name"), Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
