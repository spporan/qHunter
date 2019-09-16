package com.tanveer.qhunter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    ZXingScannerView scannerView;
    private static final String TAG = "QRScannerActivity";
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);

        checkForPermission();
        setContentView(scannerView);

    }

    public void checkForPermission(){
        Log.d(TAG,"verifyPermissions: asking user for permissions");

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(QRScannerActivity.this, Manifest.permission.CAMERA)){
                ActivityCompat.requestPermissions(QRScannerActivity.this, new String[] {Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
            }

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        }
    }
    @Override
    public void handleResult(Result result) {
        Intent intent = new Intent(this.getApplicationContext(), ProfileActivity.class);
//        String jsonString  ="{'name': 'Tanveer Hoque','organization':'DIU', 'phone': '01685534877', 'email':'thaque20@gmail.com', 'messenger': 'thaque20', 'address': 'Kallyanpur'}";
//        intent.putExtra("result", jsonString);
        intent.putExtra("result", result.getText().toString());
        startActivity(intent);
    }

    @Override
    protected void onResume(){
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }
}
