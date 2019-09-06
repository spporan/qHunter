package com.tanveer.qhunter;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class QRGenActivity extends AppCompatActivity {

    EditText name, organization, phone, email, messenger, address;
    Button generate, exit, pick;
    //    ImageView qrImage;
    private static final String IMAGE_DIRECTORY = "/QRImage/";
    private int GALLERY = 1, CAMERA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_gen);

        name = (EditText) findViewById(R.id.name);
        organization = (EditText) findViewById(R.id.organization);
        phone = (EditText) findViewById(R.id.phone);
        email = (EditText) findViewById(R.id.email);
        messenger = (EditText) findViewById(R.id.messenger);
        address = (EditText) findViewById(R.id.address);
//        qrImage = (ImageView) findViewById(R.id.QR_Image);

        generate = (Button) findViewById(R.id.generate);
        exit = (Button) findViewById(R.id.exit);
//        pick = (Button)findViewById(R.id.pick);
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String img = ImageToString();
                String img = "";
                final String scanResult = "{'image':'"+img+"', 'name':'"+name.getText().toString()+"', 'organization': '"+organization.getText().toString()+"', 'phone': '"+phone.getText().toString()+"', 'email': '"+email.getText().toString()+"', 'address': '"+address.getText().toString()+"', 'messenger': '"+messenger.getText().toString()+"'}";;
//                final String scanResult = "{'name':'"+name.getText().toString()+"', 'organization': '"+organization.getText().toString()+"', 'phone': '"+phone.getText().toString()+"', 'email': '"+email.getText().toString()+"', 'address': '"+address.getText().toString()+"', 'messenger': '"+messenger.getText().toString()+"'}";;
                Intent intent = new Intent(getBaseContext(), QRGenActivity.class);
                intent.putExtra("JSON_DATA", scanResult);
                startActivity(intent);
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRGenActivity.this.finish();
            }
        });

//        pick.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showPictureDialog();
//            }
//        });
    }
    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
//                    String path = saveImage(bitmap);
//                    Toast.makeText(QrGenActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
//                    qrImage.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(QRGenActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
//            qrImage.setImageBitmap(thumbnail);
//            saveImage(thumbnail);
//            Toast.makeText(QrGenActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    public String ImageToString(){
//        Bitmap bitmap = ((BitmapDrawable) qrImage.getDrawable()).getBitmap();
//        String res = bitmapToBase64(bitmap);
//        Log.d("img", res);
//        return res;
        return "";
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
