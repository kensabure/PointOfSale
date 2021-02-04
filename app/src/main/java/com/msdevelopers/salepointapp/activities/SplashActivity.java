package com.msdevelopers.salepointapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.msdevelopers.salepointapp.R;

public class SplashActivity extends Activity {

    TextView AppName;
    ImageView appLog;
    private FirebaseAuth firebaseAuth;
    private String user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        firebaseAuth= FirebaseAuth.getInstance();

        new Handler().postDelayed( new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                SignIn();
            }
        }, 3000); // ----Main Activity Start After 3 Sec.

    }

    private void SignIn() {
        FirebaseUser user=firebaseAuth.getCurrentUser();
        if (user==null){
            Intent register = new Intent(SplashActivity.this, Register_LoginActivity.class);
            register.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(register);
        }else if (user!=null){
            checkUserType();
        }
    }

    private void checkUserType() {

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild( "uid" ).equalTo( firebaseAuth.getUid() ).addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    String UserType= ""+ds.child( "accountType" ).getValue();
                    String seller= ""+ds.child( "accountType" ).getValue();
                    String User= ""+ds.child( "accountType" ).getValue();
                    String shopUid= ""+ds.child( "shopUid" ).getValue();
                    String timestamp= ""+ds.child( "timestamp" ).getValue();

                    if (seller.equals( "Seller" )) {
                        Intent register = new Intent(SplashActivity.this, MainSellerActivity.class);
                        register.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(register);
                    }
                    else if (User.equals( "User" )) {
                        Intent register = new Intent(SplashActivity.this, MainUserActivity.class);
                        register.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(register);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText( SplashActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT ).show();
            }
        } );
    }

}