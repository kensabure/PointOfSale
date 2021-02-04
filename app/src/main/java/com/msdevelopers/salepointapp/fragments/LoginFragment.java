package com.msdevelopers.salepointapp.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.msdevelopers.salepointapp.R;
import com.msdevelopers.salepointapp.activities.MainSellerActivity;
import com.msdevelopers.salepointapp.activities.MainUserActivity;
import com.msdevelopers.salepointapp.activities.SignInActivity;
import com.rey.material.widget.FloatingActionButton;

import java.util.HashMap;
import java.util.concurrent.Executor;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";

    public static final String SHARED_PRES = "sharedPrefs";
    public static final String PREF_EMAIL = "Email_Address";
    public static final String PREF_PASSWORD = "Password";
    public static final String CHECK = "check";

    private FloatingActionButton btnLogin;
    private TextView forgot_password, textView;
    private TextView header;
    private Button CreateAccount;
    private CircleImageView Header;
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputUsername;
    private TextInputLayout textInputPassword;
    private ProgressDialog loadingBar;

    private CheckBox checkBox;
    private String email;
    private String password;
    private Boolean CheckedOnOf;

    FirebaseDatabase rootNode;
    DatabaseReference reference;
    FirebaseAuth firebaseAuth;;
    private final static  int RC_SIGN_IN= 113;

    private TextView signInButton;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user !=null){

        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_login, container, false );


        signInButton= view.findViewById(R.id.googlebtn);
        mAuth= FirebaseAuth.getInstance();
        checkBox = view.findViewById( R.id.remember_me );

        textInputEmail = view.findViewById( R.id.user_email );
        textInputPassword = view.findViewById( R.id.user_password );
        CreateAccount = view.findViewById( R.id.register_link );
        btnLogin = view.findViewById( R.id.loginbtn );
        forgot_password = view.findViewById( R.id.forgot_password );
        loadingBar = new ProgressDialog( getContext() );

        rootNode = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();


        loadData();
        UpdateData();

        forgot_password.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgot();
            }
        } );

        btnLogin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        } );


        return view;
    }

    public void forgot() {




    }

    public void validate() {

        saveData();

         email = textInputEmail.getEditText().getText().toString().trim();
         password = textInputPassword.getEditText().getText().toString().trim();

        if (email.isEmpty()) {
            textInputEmail.setError( " enter email address" );
            textInputEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            textInputPassword.setError( "enter Password" );
            textInputPassword.requestFocus();
            return;
        } else {

            loadingBar.setMessage( "Logging in........." );
            loadingBar.setCanceledOnTouchOutside( false );
            loadingBar.show();
            loginUser();
        }

    }

    private void saveData() {

        SharedPreferences sharedPreferences=getActivity().getSharedPreferences( SHARED_PRES,MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString( PREF_EMAIL,textInputEmail.getEditText().getText().toString() );
        editor.putString( PREF_PASSWORD,textInputPassword.getEditText().getText().toString() );
        editor.putBoolean( CHECK,checkBox.isChecked() );
        editor.apply();
    }
    public void loadData(){

        SharedPreferences sharedPreferences=getActivity().getSharedPreferences( SHARED_PRES,MODE_PRIVATE);
        email=sharedPreferences.getString( PREF_EMAIL,"" );
        password=sharedPreferences.getString( PREF_PASSWORD,"" );
        CheckedOnOf=sharedPreferences.getBoolean( CHECK,false);
    }

    public void UpdateData(){

        textInputEmail.getEditText().setText( email );
        textInputPassword.getEditText().setText( password );
        checkBox.setChecked( CheckedOnOf );
    }

    private void loginUser() {
        firebaseAuth.signInWithEmailAndPassword( email,password ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    MakeMeOnline();
                }else {
                    loadingBar.dismiss();
                    Toast.makeText( getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT ).show();

                }
            }
        } );

    }

    private void MakeMeOnline() {
        loadingBar.setMessage( "Checking Info...." );
        loadingBar.show();
        HashMap<String,Object> hashMap=new HashMap<>(  );
        hashMap.put( "online","true" );
        hashMap.put( "shopOpen", "true" );

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseAuth.getUid()).updateChildren( hashMap ).addOnSuccessListener( new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                checkUserType();
            }
        } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingBar.dismiss();
                Toast.makeText( getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT ).show();
            }
        } );
    }

    private void checkUserType() {

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild(  "uid").equalTo( firebaseAuth.getUid() )
            .addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds:snapshot.getChildren()){
                    String UserType= ""+ds.child( "accountType" ).getValue();
                    String seller= ""+ds.child( "accountType" ).getValue();
                    String User= ""+ds.child( "accountType" ).getValue();
                    String shopUid= ""+ds.child( "shopUid" ).getValue();
                    String timestamp= ""+ds.child( "timestamp" ).getValue();
                    if (seller.equals( "Seller" )) {
                        Intent register = new Intent(getContext(), MainSellerActivity.class);
                        register.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(register);
                    }
                    else if (User.equals( "User" )) {
                        Intent register = new Intent(getContext(), MainUserActivity.class);
                        register.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(register);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText( getContext(), ""+error.getMessage(), Toast.LENGTH_SHORT ).show();
            }
        } );
    }


}