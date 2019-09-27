package com.tanveer.qhunter.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.tanveer.qhunter.R;
import com.tanveer.qhunter.utills.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.tanveer.qhunter.utills.Constant.ADDRESS_KEY;
import static com.tanveer.qhunter.utills.Constant.EMAIL_KEY;
import static com.tanveer.qhunter.utills.Constant.GEO_KEY;
import static com.tanveer.qhunter.utills.Constant.MESSENGER_KEY;
import static com.tanveer.qhunter.utills.Constant.PHONE_KEY;


public class QRGenerateFragment extends Fragment  {
    private String[] appPermissions = {
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    private static final int PERMISSIONS_REQUEST_CODE = 1240;
    private EditText nameView,numberView,addressView,messengerView,emailView;;
    private TextView geolocationView;
    private CircleImageView imageView;
    private Button informbtn;
    private JSONObject jsonObject;
    private String geoView;
    private Button generateBtn;
    private Bitmap bitmap;
    private ImageView iv;
    public final static int QRcodeWidth = 500 ;
    private Handler handler;

    public QRGenerateFragment(){
        this.jsonObject = null;
    }

    public QRGenerateFragment(JSONObject jsonObject){
        this.jsonObject=jsonObject;

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        handler = new Handler();
        View mView=inflater.inflate(R.layout.activity_qr_generate,container,false);
        nameView=mView.findViewById(R.id.name_view);
        numberView=mView.findViewById(R.id.number_view);
        emailView= mView.findViewById(R.id.email_view);
        addressView=mView.findViewById(R.id.address_view);
        messengerView=mView.findViewById(R.id.messenger_view);
        generateBtn = mView.findViewById(R.id.generateBtn);
        iv = mView.findViewById(R.id.profie_image_view);

        generateBtn.setOnClickListener(v -> {
            Log.d(TAG, "QR is Generating...");
            ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("QR is Generating...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            // start the time consuming task in a new thread
            Thread thread = new Thread() {
                public void run () {
                    // this is the time consuming task (like, network call, database call)
                    try{
                        qrGenerate();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    // Now we are on a different thread than UI thread
                    // and we would like to update our UI, as this task is completed
                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            // Update your UI or do any Post job after the time consuming task
//                                updateUI();
                            iv.setImageBitmap(bitmap);
                            // remember to dismiss the progress dialog on UI thread
                            progressDialog.dismiss();

                        }
                    });

                }
            };

            thread.start();
        });

        try {
            jsonObjTOString();
        } catch (JSONException e) {
            e.printStackTrace();
        }



        return mView;
    }

    private void qrGenerate(){
        try{
            final String scanResult = "{'name':'"+nameView.getText().toString()+"', 'phone': '"+numberView.getText().toString()+"', 'email': '"+emailView.getText().toString()+"', 'address': '"+addressView.getText().toString()+"', 'messenger': '"+messengerView.getText().toString()+"'}";
            bitmap = TextToImageEncode(scanResult);
        } catch (WriterException ex) {
            ex.printStackTrace();
        }
    }


    private Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    QRcodeWidth, QRcodeWidth, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        getResources().getColor(R.color.black):getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }
    private boolean checkAndRequestPermissions(){
        List<String> listPermissionsNeeded = new ArrayList<>();
        for(String perm: appPermissions){
            if(ContextCompat.checkSelfPermission(getActivity(), perm) != PackageManager.PERMISSION_GRANTED){
                listPermissionsNeeded.add(perm);
            }
        }

        if(!listPermissionsNeeded.isEmpty()){
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSIONS_REQUEST_CODE);
            return false;
        }
        return  true;
    }



    private void jsonObjTOString() throws JSONException {

        if(jsonObject != null){
            nameView.setText(jsonObject.getString(Constant.NAME_KEY));
            numberView.setText(jsonObject.getString(PHONE_KEY).toString());
            emailView.setText(jsonObject.getString(EMAIL_KEY).toString());
            addressView.setText(jsonObject.getString(ADDRESS_KEY).toString());
            messengerView.setText(jsonObject.getString(MESSENGER_KEY).toString());
            geoView = jsonObject.getString(GEO_KEY).toString();
        }

    }


    @Override
    public void onResume() {
        super.onResume();

    }
    @Override
    public void onPause() {
        super.onPause();

    }




}
