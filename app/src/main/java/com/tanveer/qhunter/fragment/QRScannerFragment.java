package com.tanveer.qhunter.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.tanveer.qhunter.R;
import com.tanveer.qhunter.utills.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.tanveer.qhunter.utills.Constant.ADDRESS_KEY;
import static com.tanveer.qhunter.utills.Constant.EMAIL_KEY;
import static com.tanveer.qhunter.utills.Constant.GEO_KEY;
import static com.tanveer.qhunter.utills.Constant.MESSENGER_KEY;
import static com.tanveer.qhunter.utills.Constant.PHONE_KEY;

public class QRScannerFragment extends Fragment  {
    private String[] appPermissions = {
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    private static final int CALL_REQUEST = 1;
    private static final int PERMISSIONS_REQUEST_CODE = 1240;
    private TextView nameView,numberView,addressView,messengerView,emailView;
    private CircleImageView imageView;
    private Button informbtn;
    private ImageView numberBtn,emailBtn,addressBtn,messengerBtn;
    private JSONObject jsonObject;
    private String geoView;

    public QRScannerFragment(JSONObject jsonObject){
    this.jsonObject=jsonObject;

}
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View mView=inflater.inflate(R.layout.activity_qrscanner,container,false);
        nameView=mView.findViewById(R.id.name_view);
        numberView=mView.findViewById(R.id.number_view);
        addressView=mView.findViewById(R.id.address_view);
        messengerView=mView.findViewById(R.id.messenger_view);
        emailView= mView.findViewById(R.id.email_view);


        numberBtn=mView.findViewById(R.id.phone_icon);
        emailBtn=mView.findViewById(R.id.email_icon);
        addressBtn=mView.findViewById(R.id.address_icon);
        messengerBtn=mView.findViewById(R.id.messenger_icon);
        informbtn=mView.findViewById(R.id.inform_btn);
        try {
            jsonObjTOString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        numberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkAndRequestPermissions()){
                    sendCall();
                }
            }
        });
        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Intent.createChooser(new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",emailView.getText().toString(), null)).putExtra(Intent.EXTRA_SUBJECT, "Found your belongings!"), "Send email"));
            }
        });

        messengerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messenger = messengerView.getText().toString();
                if(!messenger.startsWith("http://m.me/")){
                    messenger = "http://m.me/"+messenger;
                } else if(messenger.startsWith("m.me/")){
                    messenger = "http://"+messenger;
                }
                Uri uri = Uri.parse(messenger);

                Intent toMessenger = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(toMessenger);
                try {
                    startActivity(toMessenger);
                } catch(android.content.ActivityNotFoundException e){
                    Toast.makeText(getActivity(), "Please install Messenger", Toast.LENGTH_SHORT).show();
                }
            }
        });
        addressBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String geo = "";
                if(checkAndRequestPermissions()){
                    if(!addressView.getText().toString().isEmpty()){
                        geo = "geo:0,0?q="+addressView.getText().toString();
                    } else {
                        geo = "geo:"+geoView;
                        Log.d("TAG", geoView);
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geo));
                    startActivity(intent);

                }
            }
        });
        return mView;
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

    private void sendMap(){
        if(checkAndRequestPermissions()){

        }
    }

    private void sendCall(){
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE},CALL_REQUEST);
            Toast.makeText(getActivity(), "Phone Request denied!", Toast.LENGTH_LONG).show();
            Log.v("TAG","Phone Request denied!");
        }
        else
        {
            Log.v("TAG","Phone Request granted!");
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+numberView.getText().toString()));
            startActivity(intent);
        }
    }


    private void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto",emailView.getText().toString(), null)).putExtra(Intent.EXTRA_SUBJECT, "Found your belongings!");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Found");
        startActivity(Intent.createChooser(emailIntent, "Send email"));

    }

    private void jsonObjTOString() throws JSONException {


        nameView.setText(jsonObject.getString(Constant.NAME_KEY));
        numberView.setText(jsonObject.getString(PHONE_KEY).toString());
        emailView.setText(jsonObject.getString(EMAIL_KEY).toString());
        addressView.setText(jsonObject.getString(ADDRESS_KEY).toString());
        messengerView.setText(jsonObject.getString(MESSENGER_KEY).toString());
        geoView = jsonObject.getString(GEO_KEY).toString();

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
