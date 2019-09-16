package com.tanveer.qhunter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;

public class QRGenActivity extends AppCompatActivity {

    EditText name, organization, phone, email, messenger, address;
    Button generate, exit, pick;
    //    ImageView qrImage;
    private static final String IMAGE_DIRECTORY = "/QRImage/";
    private int GALLERY = 1, CAMERA = 2;
    public final static int QRcodeWidth = 500 ;
    Bitmap bitmap ;
    private ImageView iv;
    ProgressDialog progressDialog;
    private boolean loginState;
    private Button btnSave;
    private DatabaseReference mDatabase;
    private String uid;
    private Button btnShare;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_gen);
        iv = (ImageView) findViewById(R.id.iv);
        progressDialog = new ProgressDialog(this);

        name = (EditText) findViewById(R.id.name);
        organization = (EditText) findViewById(R.id.organization);
        phone = (EditText) findViewById(R.id.phone);
        email = (EditText) findViewById(R.id.email);
        messenger = (EditText) findViewById(R.id.messenger);
        address = (EditText) findViewById(R.id.address);

        generate = (Button) findViewById(R.id.generate);
        exit = (Button) findViewById(R.id.exit);

        btnSave = (Button) findViewById(R.id.btnSave);

        mDatabase = FirebaseDatabase.getInstance().getReference("user");

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                User user = new User(name.getText().toString(), organization.getText().toString(), phone.getText().toString(), email.getText().toString(), messenger.getText().toString(), address.getText().toString());
                mDatabase.child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).setValue(user);

                Toast.makeText(QRGenActivity.this, "Data saved Successfully!", Toast.LENGTH_SHORT).show();
            }
        });
        btnShare = (Button) findViewById(R.id.btnShare);
        btnShare.setVisibility(View.GONE);
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharePicture();
            }
        });

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("QR Generate", "QR is Generating...");

                try {
                    final String scanResult = "{'name':'"+name.getText().toString()+"', 'organization': '"+organization.getText().toString()+"', 'phone': '"+phone.getText().toString()+"', 'email': '"+email.getText().toString()+"', 'address': '"+address.getText().toString()+"', 'messenger': '"+messenger.getText().toString()+"'}";;
                    bitmap = TextToImageEncode(scanResult);
                    iv.setImageBitmap(bitmap); // Set image

                    // Save
//                    String path = saveImage(bitmap);  //give read write permission
//                    Toast.makeText(getApplicationContext(), "QRCode saved to -> "+path, Toast.LENGTH_SHORT).show();
//                    Log.d("SavedTO", path);

                    Log.d("QR Generate", "QR is Generated Successfully...");
                    btnShare.setVisibility(View.VISIBLE);

                } catch (WriterException e) {
                    e.printStackTrace();
                }

            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRGenActivity.this.finish();
            }
        });




    }

    public void progress(String Title, String Message, boolean Cancel){
        progressDialog.setTitle(Title);
        progressDialog.setMessage(Message);
        progressDialog.setCancelable(Cancel);
        progressDialog.show();
    }

    public void sharePicture(){
        Log.d("SharePicture", "Started");
        try {

            File cachePath = new File(getApplicationContext().getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        File imagePath = new File(getApplicationContext().getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Log.d("SharePicture", "FileProvider");
        Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), "com.tanveer.qhunter.fileprovider", newFile);
        Log.d("SharePicture", "FileProvider DOne");
        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }
        Log.d("SharePicture", "Finished");

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

    protected void onStart(){
        super.onStart();
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        name = (EditText) findViewById(R.id.name);
        organization = (EditText) findViewById(R.id.organization);
        phone = (EditText) findViewById(R.id.phone);
        email = (EditText) findViewById(R.id.email);
        messenger = (EditText) findViewById(R.id.messenger);
        address = (EditText) findViewById(R.id.address);

        if(user != null) {
            try {
                loginState = true;
                name.setText(user.getDisplayName());
                Log.d("Auth Listener", "onAuthStateChanged:signed_in:" + user.getUid());

                findViewById(R.id.btnSave).setVisibility(View.VISIBLE);

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user");
                databaseReference.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        Toast.makeText(QRGenActivity.this, dataSnapshot.toString(), Toast.LENGTH_LONG).show();
                        User user = dataSnapshot.getValue(User.class);
                        name.setText(user.getName().toString());
                        organization.setText((user.getOrganization().toString()));
                        phone.setText(user.getPhone().toString());
                        email.setText(user.getEmail().toString());
                        messenger.setText(user.getMessenger().toString());
                        address.setText(user.getAddress().toString());
//                        Toast.makeText(QRGenActivity.this, user.getPhone(), Toast.LENGTH_SHORT).show();

//                        Log.d("USER: ", user.toString());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(QRGenActivity.this, "Error Occured!", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Phone Number Not Found", Toast.LENGTH_SHORT).show();
            }
        }else {
            loginState = false;
            Toast.makeText(getApplicationContext(), "Login First", Toast.LENGTH_SHORT).show();
            Log.d("Auth Listener", "Not Logged in:" );
            findViewById(R.id.btnSave).setVisibility(View.GONE);
        }
    }


//    private void showPictureDialog(){
//        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
//        pictureDialog.setTitle("Select Action");
//        String[] pictureDialogItems = {
//                "Select photo from gallery",
//                "Capture photo from camera" };
//        pictureDialog.setItems(pictureDialogItems,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch (which) {
//                            case 0:
//                                choosePhotoFromGallary();
//                                break;
//                            case 1:
//                                takePhotoFromCamera();
//                                break;
//                        }
//                    }
//                });
//        pictureDialog.show();
//    }
//
//    public void choosePhotoFromGallary() {
//        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//
//        startActivityForResult(galleryIntent, GALLERY);
//    }
//
//    private void takePhotoFromCamera() {
//        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(intent, CAMERA);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == this.RESULT_CANCELED) {
//            return;
//        }
//        if (requestCode == GALLERY) {
//            if (data != null) {
//                Uri contentURI = data.getData();
//                try {
//                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
////                    String path = saveImage(bitmap);
////                    Toast.makeText(QrGenActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
////                    qrImage.setImageBitmap(bitmap);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    Toast.makeText(QRGenActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//        } else if (requestCode == CAMERA) {
//            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
////            qrImage.setImageBitmap(thumbnail);
////            saveImage(thumbnail);
////            Toast.makeText(QrGenActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    public String ImageToString(){
////        Bitmap bitmap = ((BitmapDrawable) qrImage.getDrawable()).getBitmap();
////        String res = bitmapToBase64(bitmap);
////        Log.d("img", res);
////        return res;
//        return "";
//    }
//
//    private String bitmapToBase64(Bitmap bitmap) {
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
//        byte[] byteArray = byteArrayOutputStream .toByteArray();
//        return Base64.encodeToString(byteArray, Base64.DEFAULT);
//    }
//
//    public String saveImage(Bitmap myBitmap) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
//        File wallpaperDirectory = new File(
//                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
//        // have the object build the directory structure, if needed.
//
//        if (!wallpaperDirectory.exists()) {
//            Log.d("dirrrrrr", "" + wallpaperDirectory.mkdirs());
//            wallpaperDirectory.mkdirs();
//        }
//
//        try {
//            File f = new File(wallpaperDirectory, Calendar.getInstance()
//                    .getTimeInMillis() + ".jpg");
//            f.createNewFile();   //give read write permission
//            FileOutputStream fo = new FileOutputStream(f);
//            fo.write(bytes.toByteArray());
//            MediaScannerConnection.scanFile(this,
//                    new String[]{f.getPath()},
//                    new String[]{"image/jpeg"}, null);
//            fo.close();
//            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());
//
//            return f.getAbsolutePath();
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
//        return "";
//
//    }

}
