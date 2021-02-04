package com.msdevelopers.salepointapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.msdevelopers.salepointapp.R;
import com.msdevelopers.salepointapp.fragments.ProfileDialog;
import com.msdevelopers.salepointapp.helpers.TabsAccessorAdapterUsers;

import java.util.HashMap;

public class MainUserActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdapterUsers myTabsAccessorAdapter;

    private FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private String currentUserID;
    AlertDialog.Builder builder;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main_user );
        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");

        builder = new AlertDialog.Builder( this );

        progressDialog = new ProgressDialog( this );

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        myViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        myTabsAccessorAdapter = new TabsAccessorAdapterUsers(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessorAdapter);

        myTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.user_option, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.logout)
        {

            SignOut();
        }

        if (item.getItemId() == R.id.settings)
        {

            ProfileDialog dialog = ProfileDialog.newInstance();
            ((ProfileDialog) dialog).setCallback(new ProfileDialog.Callback() {
                @Override
                public void onActionClick(String name) {
                    Toast.makeText(MainUserActivity.this, name, Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show(getSupportFragmentManager(), "tag");
        }


        return true;
    }

    private void SignOut() {
        builder.setMessage(R.string.dialog_message).setTitle(R.string.dialog_title);
        builder.setMessage("You are about to Sign Out! Do you wish to Continue?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        MakeMeOffline();

                    }
                })
                .setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                        Toast.makeText(getApplicationContext(), "Cancelled",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle("Sign Out ");
        alert.show();
    }

    private void MakeMeOffline() {
        progressDialog.setMessage( "Logging Out...." );
        progressDialog.show();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put( "ShopOpen", "false" );

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference( "Users" );
        ref.child( mAuth.getUid() ).updateChildren( hashMap ).addOnSuccessListener( new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                mAuth.signOut();
                Intent intent = new Intent( MainUserActivity.this, Register_LoginActivity.class );
                intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                startActivity( intent );
                finish();

                progressDialog.dismiss();
                Toast.makeText( MainUserActivity.this, "Logged out", Toast.LENGTH_SHORT ).show();
            }
        } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText( MainUserActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT ).show();
            }
        } );
    }

}