package com.tanveer.qhunter.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.tanveer.qhunter.R;
import com.tanveer.qhunter.fragment.QRGenerateFragment;
import com.tanveer.qhunter.fragment.QRScannerFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
public class BottomNavBarActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE = 342;
    private static final int PICK_PHOTO_FOR_AVATAR = 2356;
    private String[] appPermissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static TextView textView;
    public static String result = "";
    private int progressStatus = 0;
    private MeowBottomNavigation meowBottomNavigation;
    final FragmentManager fm = getSupportFragmentManager();
    private IntentIntegrator qrScan;
    JSONObject obj;
    Fragment active ;
    private static final int PICK_IMAGE = 234;

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
                try {
                    for(Fragment fragment : getSupportFragmentManager().getFragments()){
                        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    }
                    switch (model.getId()) {
                        case 1:
                            QRGenerateFragment qrGenerateFragment = new QRGenerateFragment();
                            Toast.makeText(BottomNavBarActivity.this, "Its ok", Toast.LENGTH_SHORT).show();
                            fm.beginTransaction().add(R.id.fragment_container, qrGenerateFragment, "1").commitAllowingStateLoss();
                            break;
                        case 2:
                            //                        QRScannerFragment qrScannerFragment=new QRScannerFragment(obj);
                            //                        fm.beginTransaction().add(R.id.fragment_container, qrScannerFragment,"2").commit();

                            //                        Intent intent=new Intent(getApplicationContext(),QRScannerActivity.class);
                            //                        startActivity(intent);
                            if (checkAndRequestPermissions()) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(BottomNavBarActivity.this);
                                builder.setTitle("Scan QR from?");
                                builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(BottomNavBarActivity.this, "Yes Pressed", Toast.LENGTH_SHORT).show();
                                        qrScan.initiateScan();
                                    }
                                });
                                builder.setNegativeButton("Gallery   ", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        pickImage();
                                    }
                                });

                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                            break;
                        case 3:
                            Toast.makeText(getApplicationContext(), "Profile", Toast.LENGTH_LONG).show();
                            break;

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        });


    }

    public void pickImage() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PICK_PHOTO_FOR_AVATAR);
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");
//        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
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
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == RESULT_OK && data !=null){
                try {
                    Uri imageUri = data.getData();
                    String real_path = getRealPathFromUri(BottomNavBarActivity.this, imageUri);
                    Bitmap bMap = decodeUri(BottomNavBarActivity.this, imageUri, 300);
                    if(bMap != null){
//                        Toast.makeText(this, "Image Loaded!", Toast.LENGTH_SHORT).show();
                        int[] intArray = new int[bMap.getWidth()*bMap.getHeight()];
                        //copy pixel data from the Bitmap into the 'intArray' array
                        bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());

                        LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
                        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                        Reader reader = new MultiFormatReader();
                        Result rs = reader.decode(bitmap);
                        String contents = rs.getText();
                        obj = new JSONObject(contents);
                        QRScannerFragment qrScannerFragment = new QRScannerFragment(obj);
                        fm.beginTransaction().add(R.id.fragment_container, qrScannerFragment, "2").commitAllowingStateLoss();
                    } else {
                        Toast.makeText(this, "Error Loading", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null,
                    null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    public static Bitmap decodeUri(Context context, Uri uri,
                                   final int requiredSize) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getContentResolver()
                .openInputStream(uri), null, o);

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        while (true) {
            if (width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(context.getContentResolver()
                .openInputStream(uri), null, o2);
    }


    private boolean checkAndRequestPermissions(){
        List<String> listPermissionsNeeded = new ArrayList<>();
        for(String perm: appPermissions){
            if(ContextCompat.checkSelfPermission(getApplicationContext(), perm) != PackageManager.PERMISSION_GRANTED){
                listPermissionsNeeded.add(perm);
            }
        }

        if(!listPermissionsNeeded.isEmpty()){
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSIONS_REQUEST_CODE);
            return false;
        }
        return  true;
    }


}
