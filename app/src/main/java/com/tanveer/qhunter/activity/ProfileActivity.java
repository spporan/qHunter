package com.tanveer.qhunter.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanveer.qhunter.R;
import com.tanveer.qhunter.model.User;

import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {
    TextView name;
    TextView organization;
    TextView phone;
    TextView email;
    TextView address;
    TextView messenger;


    public final static int QRcodeWidth = 500 ;
    private static final String IMAGE_DIRECTORY = "/QRcodeDemonuts";
    Bitmap bitmap ;
    private ImageView iv;
    Button btnLogInOut;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    boolean loginState = false;
    
//    JSONObject json;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);




        try{
            String jsonString = getIntent().getStringExtra("result");
            JSONObject jObj;
            jObj = new JSONObject(jsonString);
            name.setText(jObj.getString("name").toString());
            organization.setText(jObj.getString("organization").toString());
            phone.setText(jObj.getString("phone").toString());
            email.setText(jObj.getString("email").toString());
            address.setText(jObj.getString("address").toString());
            messenger.setText(jObj.getString("messenger").toString());
        } catch (Exception e){
//            Toast.makeText(this, "Invalid Data!\n"+e, Toast.LENGTH_SHORT).show();

        }


        btnLogInOut = (Button) findViewById(R.id.btnLogInOut);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null) {
                    try {
                        loginState = true;
                        phone.setText(user.getPhoneNumber());
                        btnLogInOut.setText("Log Out");
                        Log.d("Auth Listener", "onAuthStateChanged:signed_in:" + user.getUid());
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Phone Number Not Found", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    loginState = false;
                    btnLogInOut.setText("Log In");
                    Toast.makeText(getApplicationContext(), "Login First", Toast.LENGTH_SHORT).show();
                    Log.d("Auth Listener", "Not Logged in:" );
                }
            }
        };


        findViewById(R.id.btnLogInOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginState){
                    FirebaseAuth.getInstance().signOut();

                    Intent intent = new Intent(ProfileActivity.this, BottomNavBarActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    startActivity(intent);
                } else {
                    startActivity(new Intent(ProfileActivity.this, RegisterActivity.class));
                }
            }
        });

        findViewById(R.id.btnRetry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        findViewById(R.id.btnEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), QRGenActivity.class));
            }
        });


    }
    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        name = findViewById(R.id.name);
        organization = findViewById(R.id.organization);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        address = findViewById(R.id.address);
        messenger = findViewById(R.id.messenger);

        if(user != null) {
            try {
                loginState = true;
                name.setText(user.getDisplayName());
                btnLogInOut.setText("Log Out");
                Log.d("Auth Listener", "onAuthStateChanged:signed_in:" + user.getUid());
                findViewById(R.id.btnEdit).setVisibility(View.VISIBLE);
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(ProfileActivity.this, "Error Occured!", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Phone Number Not Found", Toast.LENGTH_SHORT).show();
            }
        }else {
            loginState = false;
            btnLogInOut.setText("Log In");
            Toast.makeText(getApplicationContext(), "Login First", Toast.LENGTH_SHORT).show();
            Log.d("Auth Listener", "Not Logged in:" );
            findViewById(R.id.btnEdit).setVisibility(View.GONE);
        }
    }



}
