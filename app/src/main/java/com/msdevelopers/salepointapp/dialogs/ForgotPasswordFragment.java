package com.msdevelopers.salepointapp.dialogs;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.msdevelopers.salepointapp.R;


import java.util.regex.Pattern;

public class ForgotPasswordFragment extends DialogFragment implements View.OnClickListener {

    private Callback callback;

    private FirebaseAuth mAuth;
    private Button web_link,register_reset,try_again;
    private ProgressBar progressBar;
    private Button action,Back;
    private TextInputLayout textInputEmail;
    private TextView resetText,resetHeader,Text_Error;


    public static final Pattern EMAIL_ADDRESS
            = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );


    public static ForgotPasswordFragment newInstance() {
        return new ForgotPasswordFragment();
    }
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle( DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.forgotpassword_fragment, container, false);
        ImageView close = view.findViewById(R.id.close);
         action = view.findViewById(R.id.forgot_password);
        resetHeader= view.findViewById( R.id.reset_text );
        register_reset= view.findViewById( R.id.reset_register_link );
        try_again= view.findViewById( R.id.reset_again );
        web_link= view.findViewById( R.id.reset_weblink );
        Text_Error= view.findViewById( R.id.erro_text );
        Back= view.findViewById( R.id.back_btn );
        this.getActivity().getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mAuth= FirebaseAuth.getInstance();
        textInputEmail= view.findViewById( R.id.user_email);
        resetText= view.findViewById( R.id.resetText );
        progressBar= view.findViewById( R.id.progressBar );

        web_link.setVisibility( View.GONE );
        register_reset.setVisibility( View.GONE );
        try_again.setVisibility( View.GONE );
        Text_Error.setVisibility( View.GONE );
        resetText.setVisibility( View.GONE );

        close.setOnClickListener(this);
        action.setOnClickListener(this);
        register_reset.setOnClickListener( this );
        try_again.setOnClickListener( this );

        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {

            case R.id.close:
                dismiss();
                break;

            case R.id.forgot_password:
                Validate();
                break;
            case R.id.reset_register_link:
                dismiss();
                break;
            case R.id.reset_again:
                Text_Error.setVisibility( View.GONE );
                try_again.setVisibility( View.GONE );
                register_reset.setVisibility( View.GONE );
                textInputEmail.setVisibility( View.VISIBLE );
                textInputEmail.setError( "Invalid Email Address" );
                resetHeader.setVisibility( View.VISIBLE );
                action.setVisibility( View.VISIBLE );
                break;

        }

    }

    public interface Callback {

        void onActionClick(String name);

    }

    private void Validate() {
        String Email_Address = textInputEmail.getEditText().getText().toString().trim();

        if (Email_Address.isEmpty()){
            textInputEmail.setError("Email Address Required");
            return;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(Email_Address).matches()) {
            textInputEmail.setError( "Please enter a valid email address" );

        }else{
            progressBar.setVisibility( View.VISIBLE );
            fetchSignInMethodsForEmail(Email_Address);
        }
    }

    private void fetchSignInMethodsForEmail(final String Email_Address) {

        mAuth.fetchSignInMethodsForEmail( Email_Address ).addOnCompleteListener( new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.getResult().getSignInMethods().isEmpty()){
                    progressBar.setVisibility( View.GONE );
                    Text_Error.setVisibility( View.VISIBLE );
                    Text_Error.setText( "Failed! Email Address you have entered is Invalid.Try again or create new Account" );
                    action.setVisibility( View.GONE );
                    resetHeader.setVisibility( View.GONE );
                    textInputEmail.setVisibility( View.GONE );
                    try_again.setVisibility( View.VISIBLE );
                    register_reset.setVisibility( View.VISIBLE );


                } else {
                    mAuth.sendPasswordResetEmail( Email_Address ).addOnCompleteListener( new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility( View.GONE );
                            if (task.isSuccessful()) {
                                resetText.setText( "A Link to reset new password has been sent to the email Address you have provided above." );
                                action.setVisibility( View.GONE );
                                resetHeader.setVisibility( View.GONE );
                                resetText.setVisibility( View.VISIBLE );
                                web_link.setVisibility( View.VISIBLE );
                                Back.setVisibility( View.GONE );
                                textInputEmail.setVisibility( View.GONE );
                            } else {
                                resetText.setText( task.getException().getMessage() );
                            }
                        }
                    } );

                }
            }
        } );
    }
}