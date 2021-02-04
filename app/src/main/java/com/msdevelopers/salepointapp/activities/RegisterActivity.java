package com.msdevelopers.salepointapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;;
import com.msdevelopers.salepointapp.R;
import com.rey.material.widget.FloatingActionButton;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class RegisterActivity extends AppCompatActivity {
    //declare views
    TextView ctry,area;
    //variable address
    String address;
    //variables LAT AND LNG
    double lat, lng;
    //Geocoder
    Geocoder geocoder;
    //model list
    List<Address> addresses;
    private static int REQUEST_CODE_LOCATION_PERMISSION = 2222;
    private String name, phone, email, password, S_Country, S_Area;
    private TextInputLayout Name, Phone, Email, Passord, Country, City;
    private FloatingActionButton Register;
    private TextView selleRegister;
    private ImageButton gps;


    private static final int LOCATION_REQUEST_CODE = 100;
    private String[] locationPermission;
    private LocationManager locationManager;
    private double latitude = 0.0, longitude = 0.0;

    private FirebaseAuth firebaseAuth;
    private String user_id;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register );
        this.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );

        locationPermission = new String[]{ACCESS_FINE_LOCATION};

        ctry = findViewById( R.id.country );
        area = findViewById( R.id.area );

        //new instance of the Geocoder class
        geocoder = new Geocoder( this, Locale.getDefault() );

        //permission popup
        permissionPop();

        Name = findViewById( R.id.full_name );
        Phone = findViewById( R.id.phone );
        Email = findViewById( R.id.user_email );
        Passord = findViewById( R.id.user_password );
        Register = findViewById( R.id.register_btn );
        selleRegister = findViewById( R.id.selle_register );
        gps = findViewById( R.id.gps );

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog( this );
        progressDialog.setTitle( "Please Wait....." );
        progressDialog.setCanceledOnTouchOutside( false );


        Register.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        } );
    }

    private void permissionPop() {
        //get the location services of the device via the LocationManager class
        LocationManager locationManager = (LocationManager) getSystemService( LOCATION_SERVICE );
        //check if providers ARE enabled
        if (!locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) || !locationManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER )) {
            //alert dialog to ask user for permission to enable the above services
            AlertDialog.Builder builder = new AlertDialog.Builder( this );
            //customizing popup
            builder.setTitle( "Location Permissions" );
            builder.setIcon( R.drawable.ic_launcher_background );

            //set controls
            builder.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //intent to start a service
                    Intent intent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS );
                    startActivity( intent );
                }
            } );

            builder.setNegativeButton( "No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText( RegisterActivity.this, "App may not work well", Toast.LENGTH_SHORT ).show();
                }
            } );

            //show your alert dialog
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside( false );
            alertDialog.show();


        } else {
            Toast.makeText( this, "Location is on", Toast.LENGTH_SHORT ).show();
        }

    }

    public void getUserLoc(View v) {
        getLocation();
    }

    private void getLocation() {
        new AlertDialog.Builder( this )
                .setTitle( "Allow This App to get your current location" )
                .setMessage( "this will allow you to find vendors easily for better services" )

                .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (ContextCompat.checkSelfPermission( getApplicationContext(), ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                            //from this point it will move to the onRequestPermissionResult method
                            ActivityCompat.requestPermissions(
                                    RegisterActivity.this,
                                    new String[]{ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION
                            );

                        } else {
                            //if the permission is already set
                            getCurrentLocation();
                        }

                    }
                } )

                .setNegativeButton( "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText( RegisterActivity.this, "location not on app may not work properly", Toast.LENGTH_LONG ).show();
                    }
                } )

                .setIcon( android.R.drawable.ic_dialog_alert )
                .setCancelable( false )
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            getCurrentLocation();
        } else {
            Toast.makeText( this, "Permission denied", Toast.LENGTH_SHORT ).show();
        }
    }

    private void getCurrentLocation() {
        //create a new instance of the LocationRequest class

        LocationRequest locationRequest = new LocationRequest();

        //set metadata
        locationRequest.setInterval( 1000 );
        locationRequest.setFastestInterval( 3000 );
        locationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );

        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient( RegisterActivity.this )
                .requestLocationUpdates( locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult( locationResult );
                        LocationServices.getFusedLocationProviderClient( RegisterActivity.this )
                                .removeLocationUpdates( this );
                        //check for location result
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            //set up location index which will allow us to get the location lat and longitude
                            int latestlocationIndex = locationResult.getLocations().size() - 1; //removing the last known location from our  list

                            //getting lat anf lng
                            lat = locationResult.getLocations().get( latestlocationIndex ).getLatitude();
                            lng = locationResult.getLocations().get( latestlocationIndex ).getLongitude();

                            Log.d( "ken", "lat is " + lat + " lon is " + lng );

                            //using geocoder to convert lat and lng
                            try {
                                addresses = geocoder.getFromLocation( lat, lng, 1 );
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            //decode address further
                            //using the list i can access the details of the user current location
                            //address
                            address = addresses.get( 0 ).getAddressLine( 0 );
                            //get other info about users current location
                            String city = addresses.get( 0 ).getLocality();
                            String state = addresses.get( 0 ).getAdminArea();
                            String country = addresses.get( 0 ).getCountryName();
                            String postalCode = addresses.get( 0 ).getPostalCode();
                            String knownName = addresses.get( 0 ).getFeatureName();

                            //set text to textview
                            ctry.setText(  country );
                            area.setText( city );

                        }
                    }
                }, Looper.getMainLooper() );
     }

     private void inputData() {
        name=Name.getEditText().getText().toString();
        phone=Phone.getEditText().getText().toString();
        email= Email.getEditText().getText().toString();
        S_Country=ctry.getText().toString();
        S_Area =area.getText().toString();
        password=Passord.getEditText().getText().toString();

        if (TextUtils.isEmpty( name )){
            Toast.makeText( this, "Name Required", Toast.LENGTH_SHORT ).show();
            return;
        }

        if (TextUtils.isEmpty( phone )){
            Toast.makeText( this, "Phone Required", Toast.LENGTH_SHORT ).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email  ).matches()){
            Toast.makeText( this, "Invalid Email", Toast.LENGTH_SHORT ).show();
            return;
        }else if (TextUtils.isEmpty( email )){
            Toast.makeText( this, "Email Required", Toast.LENGTH_SHORT ).show();
            return;
        }

        else if ( lat==0.0 || lng== 0.0){
            Toast.makeText( this, "PLease Click Red Icon Location", Toast.LENGTH_SHORT ).show();
            return;
        }

        if (TextUtils.isEmpty( password )){
            Toast.makeText( this, "Password Required", Toast.LENGTH_SHORT ).show();
            return;
        }else if (password.length()<6 ){
            Toast.makeText( this, "Password must be at least 6 character long...", Toast.LENGTH_SHORT ).show();
            return;
        }

        CreateAccount();

    }

    private void CreateAccount() {
        progressDialog.setMessage( "Creating Account" );
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword( email,password ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                saveDateToFirebase();
            }
        } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText( RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT ).show();
            }
        } );
    }

    private void saveDateToFirebase() {
        progressDialog.setMessage( "Saving Info..." );
        String timeStamp= ""+System.currentTimeMillis();


        HashMap<String,Object> hashMap=new HashMap<>(  );
        hashMap.put( "uid",""+firebaseAuth.getUid());
        hashMap.put( "email",""+email );
        hashMap.put( "name",""+name );
        hashMap.put( "phone",""+phone );
        hashMap.put( "country",""+ctry );
        hashMap.put( "area",""+area );
        hashMap.put( "latitude",""+lat );
        hashMap.put( "longitude",""+lng );
        hashMap.put( "timestamp",""+timeStamp );
        hashMap.put( "accountType","User" );
        hashMap.put( "online","online" );
        hashMap.put( "ProfileImage","" );

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child( firebaseAuth.getUid() ).setValue( hashMap )
                .addOnCompleteListener( new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        Intent register = new Intent(RegisterActivity.this, MainUserActivity.class);
                        register.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(register);
                    }

                } )
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText( RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT ).show();

                    }
                } );

    }

    public void Back(View view) {
        startActivity( new Intent( RegisterActivity.this, SignInActivity.class ) );
    }

    public void Seller(View view) {
        startActivity( new Intent( RegisterActivity.this, SellerRegisterActivity.class ) );
    }
    @Override
    protected void onStart() {
        getLocation();
        super.onStart();
    }
}