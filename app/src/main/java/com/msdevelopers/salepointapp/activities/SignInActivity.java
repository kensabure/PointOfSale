package com.msdevelopers.salepointapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.msdevelopers.salepointapp.R;
import com.msdevelopers.salepointapp.dialogs.ForgotPasswordFragment;
import com.rey.material.widget.FloatingActionButton;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {

    private String Email,Password;
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;
    private CheckBox checkBox;
    private String email;
    private String password;
    private Boolean CheckedOnOf;
    private TextView SignUp,Reset;
    private FloatingActionButton floatingActionButton;

    private FirebaseAuth firebaseAuth;
    private String user_id;
    private ProgressDialog progressDialog;
    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_sign_in );


        this.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        textInputEmail=findViewById( R.id.user_email);
        textInputPassword=findViewById( R.id.user_password);
        checkBox=findViewById( R.id.remember_me);
        Reset=findViewById( R.id.forgot_password);
        SignUp=findViewById( R.id.register_link);
        floatingActionButton=findViewById( R.id.loginbtn);

        firebaseAuth= FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog( this );
        progressDialog.setTitle( "Please Wait....." );
        progressDialog.setCanceledOnTouchOutside( false );

        floatingActionButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        } );

        SignUp.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( getApplicationContext(), RegisterActivity.class );
                startActivity( intent );
            }
        } );

        Reset.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = ForgotPasswordFragment.newInstance();
                ((ForgotPasswordFragment) dialog).setCallback( new ForgotPasswordFragment.Callback() {
                    @Override
                    public void onActionClick(String name) {
                        Toast.makeText(SignInActivity.this, name, Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show(getSupportFragmentManager(), "tag");
            }
        } );

    }


    private void validate() {
        progressDialog.setMessage( " Logging In....." );
        Email = textInputEmail.getEditText().getText().toString();
        Password=textInputPassword.getEditText().getText().toString();

        if (TextUtils.isEmpty( Email )){
            Toast.makeText( this, "Email Required", Toast.LENGTH_SHORT ).show();
            return;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(Email  ).matches()){
            Toast.makeText( this, "Invalid Email", Toast.LENGTH_SHORT ).show();
            return;
        }

        if (TextUtils.isEmpty( Password )){
            Toast.makeText( this, "Password Required", Toast.LENGTH_SHORT ).show();
            return;
        }else if (Password.length()<6 ){
            Toast.makeText( this, "Password must be at least 6 character long...", Toast.LENGTH_SHORT ).show();
            return;
        }else {
            loginUser();
        }


    }

    private void loginUser() {

        firebaseAuth.signInWithEmailAndPassword( Email,Password ).addOnSuccessListener( new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                MakeMeOnline();

            }
        } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( SignInActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT ).show();
                progressDialog.dismiss();

            }
        } );
    }

    private void MakeMeOnline() {
        progressDialog.setMessage( "Checking Info...." );
        progressDialog.show();
        HashMap<String,Object>hashMap=new HashMap<>(  );
        hashMap.put( "online","true" );

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren( hashMap ).addOnSuccessListener( new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                checkUserType();
            }
        } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText( SignInActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT ).show();
            }
        } );
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
                         Intent register = new Intent(SignInActivity.this, MainSellerActivity.class);
                         register.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                         startActivity(register);
                    }
                    else if (User.equals( "User" )) {
                         Intent register = new Intent(SignInActivity.this, MainUserActivity.class);
                         register.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                         startActivity(register);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText( SignInActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT ).show();
            }
        } );
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}