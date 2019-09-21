package com.tanveer.qhunter.fragment;

import android.Manifest;
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
import androidx.appcompat.view.menu.MenuView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.tanveer.qhunter.R;
import com.tanveer.qhunter.activity.ProfileActivity;
import com.tanveer.qhunter.utills.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.tanveer.qhunter.activity.QRScannerActivity.TAG;
import static com.tanveer.qhunter.utills.Constant.ADDRESS_KEY;
import static com.tanveer.qhunter.utills.Constant.EMAIL_KEY;
import static com.tanveer.qhunter.utills.Constant.MESSENGER_KEY;
import static com.tanveer.qhunter.utills.Constant.PHONE_KEY;

public class QRScannerFragment extends Fragment  {
    private TextView nameView,numberView,addressView,messengerView,emailView;
    private CircleImageView imageView;
    private Button informbtn;
    private ImageView numberBtn,emailBtn,addressBtn,messengerBtn;
    private JSONObject jsonObject;

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

        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
            }
        });
        return mView;
    }

    private void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto",emailView.getText().toString(), null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Found");
        startActivity(Intent.createChooser(emailIntent, "Send email"));

    }

    private void jsonObjTOString() throws JSONException {


        nameView.setText(jsonObject.getString(Constant.NAME_KEY));
        numberView.setText(jsonObject.getString(PHONE_KEY).toString());
        emailView.setText(jsonObject.getString(EMAIL_KEY).toString());
        addressView.setText(jsonObject.getString(ADDRESS_KEY).toString());
        messengerView.setText(jsonObject.getString(MESSENGER_KEY).toString());


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
