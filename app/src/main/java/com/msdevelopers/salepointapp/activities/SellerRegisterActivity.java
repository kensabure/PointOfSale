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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.msdevelopers.salepointapp.R;
import com.rey.material.widget.FloatingActionButton;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class SellerRegisterActivity extends AppCompatActivity {

    TextView ctry,area;
    //variable address
    String address;
    //variables LAT AND LNG
    private double lat =0.0,lng =0.0;
    //Geocoder
    Geocoder geocoder;
    //model list
    List<Address> addresses;
    private static int REQUEST_CODE_LOCATION_PERMISSION = 2222;

    private TextInputLayout Name, Phone, Email, Password, Fee, ShopName, Country, City;
    private ImageButton imageButton;
    private FloatingActionButton Register;

    private static final int LOCATION_REQUEST_CODE = 100;
    private String[] locationPermission;
    private LocationManager locationManager;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String shop_uid;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_seller_register );
        this.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );

        locationPermission = new String[] {Manifest.permission.ACCESS_FINE_LOCATION };
        locationPermission = new String[]{ACCESS_FINE_LOCATION};

        ctry = findViewById( R.id.country );
        area = findViewById( R.id.area );

        //new instance of the Geocoder class
        geocoder = new Geocoder( this, Locale.getDefault() );

        //permission popup
        permissionPop();


        Name = findViewById( R.id.seller_name );
        Phone = findViewById( R.id.seller_phone );
        Email = findViewById( R.id.seller_email );
        ShopName = findViewById( R.id.shop_name );
        Fee = findViewById( R.id.delivery_fee );
        Password = findViewById( R.id.seller_password );
        Register = findViewById( R.id.register_seller );

        firebaseAuth= FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog( this );
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
                    Toast.makeText( SellerRegisterActivity.this, "App may not work well", Toast.LENGTH_SHORT ).show();
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
                .setMessage( "this will allow Users find you easily  for better services" )

                .setPositiveButton( "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (ContextCompat.checkSelfPermission( getApplicationContext(), ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                            //from this point it will move to the onRequestPermissionResult method
                            ActivityCompat.requestPermissions(
                                    SellerRegisterActivity.this,
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
                        Toast.makeText( SellerRegisterActivity.this, "location not on app may not work properly", Toast.LENGTH_LONG ).show();
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
        LocationServices.getFusedLocationProviderClient( SellerRegisterActivity.this )
                .requestLocationUpdates( locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult( locationResult );
                        LocationServices.getFusedLocationProviderClient( SellerRegisterActivity.this )
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


    private String S_FullName,S_ShopName,S_DeliveryFee,S_Phone,S_Country,S_Area,S_Email,S_Password;

    private void inputData() {
        S_FullName=Name.getEditText().getText().toString();
        S_ShopName=ShopName.getEditText().getText().toString();
        S_DeliveryFee=Fee.getEditText().getText().toString();
        S_Email= Email.getEditText().getText().toString();
        S_Phone=Phone.getEditText().getText().toString();
        S_Country=ctry.getText().toString();
        S_Area =area.getText().toString();
        S_Password=Password.getEditText().getText().toString();

        if (TextUtils.isEmpty( S_FullName )){
            Toast.makeText( this, "Name Required", Toast.LENGTH_SHORT ).show();
            return;
        }
        if (TextUtils.isEmpty( S_ShopName )){
            Toast.makeText( this, "Shop Name Required", Toast.LENGTH_SHORT ).show();
            return;
        }

        if (TextUtils.isEmpty( S_Phone )){
            Toast.makeText( this, "Phone Required", Toast.LENGTH_SHORT ).show();
            return;
        }

        else if ( lat==0.0 || lng== 0.0){
            Toast.makeText( this, "PLease Click Red Icon Location", Toast.LENGTH_SHORT ).show();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(S_Email  ).matches()){
            Toast.makeText( this, "Invalid Email", Toast.LENGTH_SHORT ).show();
            return;
        }else if (TextUtils.isEmpty( S_Email )){
            Toast.makeText( this, "Email Required", Toast.LENGTH_SHORT ).show();
            return;
        }
        else if ( lat==0.0 || lng== 0.0){
            Toast.makeText( this, "PLease Click Red Icon Location", Toast.LENGTH_SHORT ).show();
            return;
        }

        if (TextUtils.isEmpty( S_Password )){
            Toast.makeText( this, "Password Required", Toast.LENGTH_SHORT ).show();
            return;
        }else if (S_Password.length()<6 ){
            Toast.makeText( this, "Password must be at least 6 character long...", Toast.LENGTH_SHORT ).show();
            return;
        }

        CreateAccount();

    }

    private void CreateAccount() {
        progressDialog.setMessage( "Creating Account" );
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword( S_Email,S_Password ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                saveDateToFirebase();
            }
        } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText( SellerRegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT ).show();
            }
        } );
    }

    private void saveDateToFirebase() {
        progressDialog.setMessage( "Saving Info..." );
        String timeStamp= ""+System.currentTimeMillis();

        if (S_DeliveryFee == null) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put( "uid", "" + firebaseAuth.getUid() );
            hashMap.put( "email", "" + S_Email );
            hashMap.put( "name", "" + S_FullName );
            hashMap.put( "shopName", "" + S_ShopName );
            hashMap.put( "deliveryFee", "" + "Free" );
            hashMap.put( "phone", "" + S_Phone );
            hashMap.put( "country", "" + S_Country );
            hashMap.put( "area", "" + S_Area );
            hashMap.put( "latitude", "" + lat );
            hashMap.put( "longitude", "" + lng );
            hashMap.put( "timestamp", "" + timeStamp );
            hashMap.put( "accountType", "Seller" );
            hashMap.put( "online", "online" );
            hashMap.put( "shopOpen", "true" );
            hashMap.put( "ProfileImage", "" );


            DatabaseReference ref = FirebaseDatabase.getInstance().getReference( "Users" );
            ref.child( firebaseAuth.getUid() ).setValue( hashMap )
                    .addOnCompleteListener( new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            Intent register = new Intent(SellerRegisterActivity.this, MainSellerActivity.class);
                            register.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(register);
                        }

                    } )
                    .addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText( SellerRegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT ).show();

                        }
                    } );
        }
        else {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put( "uid", "" + firebaseAuth.getUid() );
            hashMap.put( "email", "" + S_Email );
            hashMap.put( "name", "" + S_FullName );
            hashMap.put( "shopName", "" + S_ShopName );
            hashMap.put( "deliveryFee", "" + S_DeliveryFee );
            hashMap.put( "phone", "" + S_Phone );
            hashMap.put( "country", "" + S_Country );
            hashMap.put( "area", "" + S_Area );
            hashMap.put( "latitude", "" + lat );
            hashMap.put( "longitude", "" + lng );
            hashMap.put( "timestamp", "" + timeStamp );
            hashMap.put( "accountType", "Seller" );
            hashMap.put( "online", "online" );
            hashMap.put( "shopOpen", "true" );
            hashMap.put( "ProfileImage", "" );


            DatabaseReference ref = FirebaseDatabase.getInstance().getReference( "Users" );
            ref.child( firebaseAuth.getUid() ).setValue( hashMap )
                    .addOnCompleteListener( new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            Intent register = new Intent(SellerRegisterActivity.this, MainSellerActivity.class);
                            register.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(register);
                        }

                    } )
                    .addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText( SellerRegisterActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT ).show();

                        }
                    } );
        }

    }

    public void Back(View view) {
        startActivity( new Intent( SellerRegisterActivity.this, SignInActivity.class ) );
    }

    @Override
    protected void onStart() {
        getLocation();
        super.onStart();
    }
}