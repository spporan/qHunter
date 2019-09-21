package com.tanveer.qhunter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.tanveer.qhunter.R;
import com.tanveer.qhunter.fragment.QRScannerFragment;

import org.json.JSONException;
import org.json.JSONObject;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
public class BottomNavBarActivity extends AppCompatActivity {

    public static TextView textView;
    public static String result = "";
    private int progressStatus = 0;
    private MeowBottomNavigation meowBottomNavigation;
    final FragmentManager fm = getSupportFragmentManager();
    private IntentIntegrator qrScan;
    JSONObject obj;
    Fragment active ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);
        meowBottomNavigation=findViewById(R.id.bottom_nav);
        qrScan = new IntentIntegrator(this);
        qrScan.setOrientationLocked(true);
        meowBottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.printer));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.qr_code_icon));
        meowBottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.profile));

        meowBottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {

                switch (model.getId()) {
                    case 1:
                        Toast.makeText(getApplicationContext(),"Generated QR",Toast.LENGTH_LONG).show();
                        break;
                    case 2:
//                        QRScannerFragment qrScannerFragment=new QRScannerFragment(obj);
//                        fm.beginTransaction().add(R.id.fragment_container, qrScannerFragment,"2").commit();

//                        Intent intent=new Intent(getApplicationContext(),QRScannerActivity.class);
//                        startActivity(intent);
                        qrScan.initiateScan();


                        break;
                    case 3:
                        Toast.makeText(getApplicationContext(),"Profile",Toast.LENGTH_LONG).show();
                        break;

                }
                return null;
            }
        });


    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                   obj = new JSONObject(result.getContents());
                    QRScannerFragment qrScannerFragment=new QRScannerFragment(obj);
                    fm.beginTransaction().add(R.id.fragment_container, qrScannerFragment,"2").commitAllowingStateLoss();

                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}
