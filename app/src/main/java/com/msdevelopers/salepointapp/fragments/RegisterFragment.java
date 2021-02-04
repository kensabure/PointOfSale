package com.msdevelopers.salepointapp.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import com.msdevelopers.salepointapp.activities.MainSellerActivity;
import com.msdevelopers.salepointapp.activities.MainUserActivity;
import com.msdevelopers.salepointapp.activities.RegisterActivity;
import com.msdevelopers.salepointapp.activities.SellerRegisterActivity;
import com.muddzdev.styleabletoast.StyleableToast;
import com.rey.material.widget.FloatingActionButton;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.LOCATION_SERVICE;

public class RegisterFragment extends Fragment {


    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    //"(?=.*[0-9])" +         //at least 1 digit
                    //"(?=.*[a-z])" +         //at least 1 lower case letter
                    //"(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{4,}" +               //at least 4 characters
                    "$");
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

    private FloatingActionButton next_Registration;
    private ImageView cancel;
    private TextView vendor_register;
    private CircleImageView Header;
    private TextInputLayout textInputEmail,textInputName,textInputNo;
    private TextInputLayout textInputUsername;
    private TextInputLayout textInputPassword;
    private LinearLayout linearLayout_user,linearLayout_vendor;
    private ProgressDialog progressDialog;
    public String name,phone,email,password;
    private String S_FullName,S_ShopName,S_DeliveryFee,S_Phone,S_Country,S_Area,S_Email,S_Password;

    FirebaseDatabase rootNode;
    DatabaseReference reference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String shop_uid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_register,container,false);


        textInputNo = view.findViewById( R.id.phone_no);
        textInputName = view.findViewById(R.id.names);
        textInputEmail = view.findViewById(R.id.user_email);
        textInputPassword = view.findViewById(R.id.user_password);
        next_Registration =  view.findViewById(R.id.next_btn);
        cancel = view.findViewById( R.id.cancel_action);
        vendor_register= view.findViewById( R.id.vendor_register );
        vendor_register= view.findViewById( R.id.vendor_register );
        linearLayout_user= view.findViewById( R.id.user_layout );
        linearLayout_vendor= view.findViewById( R.id.vendor_layout );

        ctry = view.findViewById( R.id.country );
        area = view.findViewById( R.id.area );
        Name = view.findViewById( R.id.seller_name );
        Phone = view.findViewById( R.id.seller_phone );
        Email = view.findViewById( R.id.seller_email );
        ShopName = view.findViewById( R.id.shop_name );
        Fee = view.findViewById( R.id.delivery_fee );
        Password = view.findViewById( R.id.seller_password );
        Register = view.findViewById( R.id.register_seller );


        geocoder = new Geocoder( getContext(), Locale.getDefault() );
        locationPermission = new String[] {Manifest.permission.ACCESS_FINE_LOCATION };
        locationPermission = new String[]{ACCESS_FINE_LOCATION};




        progressDialog = new ProgressDialog( getContext() );
        progressDialog.setTitle( "Please Wait....." );
        progressDialog.setCanceledOnTouchOutside( false );

        rootNode = FirebaseDatabase.getInstance();
        reference= rootNode.getReference("Users");
        firebaseAuth= FirebaseAuth.getInstance();

        permissionPop();

        vendor_register.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout_user.setVisibility( View.GONE );
                linearLayout_vendor.setVisibility( View.VISIBLE );
            }
        } );

        cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayout_user.setVisibility( View.VISIBLE );
                linearLayout_vendor.setVisibility( View.GONE );
            }
        } );

        Register.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputData();
            }
        } );
        next_Registration.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                phone = textInputNo.getEditText().getText().toString().trim();
                name = textInputName.getEditText().getText().toString().trim();
                email = textInputEmail.getEditText().getText().toString().trim();
                password = textInputPassword.getEditText().getText().toString().trim();


                if (name.isEmpty()) {
                    textInputName.setError( "Name Required" );
                    textInputName.requestFocus();
                    return;
                }


                if (phone.isEmpty()) {
                    textInputNo.setError( "Phone Required" );
                    textInputNo.requestFocus();
                    return;
                }

                if (email.isEmpty()) {
                    textInputEmail.setError( "Email Address Required" );
                    textInputEmail.requestFocus();
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher( email ).matches()) {
                    textInputEmail.setError( "Please enter a valid email address" );
                    return;
                }
                if (password.isEmpty()) {
                    textInputPassword.setError( "Password Required" );
                    textInputPassword.requestFocus();
                    return;
                }
                else {
                    CreateAccount();
                }

            }
        } );
        return view;
    }

    private void permissionPop() {
        //get the location services of the device via the LocationManager class
        LocationManager locationManager = (LocationManager) getActivity().getSystemService( LOCATION_SERVICE );

        //check if providers ARE enabled
        if (!locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) || !locationManager.isProviderEnabled( LocationManager.NETWORK_PROVIDER )) {
            //alert dialog to ask user for permission to enable the above services
            AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
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
                    Toast.makeText( getContext(), "App may not work well", Toast.LENGTH_SHORT ).show();
                }
            } );

            //show your alert dialog
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside( false );
            alertDialog.show();


        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {
            getCurrentLocation();
        } else {
            Toast.makeText( getContext(), "Permission denied", Toast.LENGTH_SHORT ).show();
        }
    }

    private void getCurrentLocation() {
        //create a new instance of the LocationRequest class

        LocationRequest locationRequest = new LocationRequest();

        //set metadata
        locationRequest.setInterval( 1000 );
        locationRequest.setFastestInterval( 3000 );
        locationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );

        if (ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( getContext(), Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient( getActivity() )
                .requestLocationUpdates( locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult( locationResult );
                        LocationServices.getFusedLocationProviderClient( getActivity() )
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

        S_FullName=Name.getEditText().getText().toString();
        S_ShopName=ShopName.getEditText().getText().toString();
        S_DeliveryFee=Fee.getEditText().getText().toString();
        S_Phone=Phone.getEditText().getText().toString();
        S_Email= Email.getEditText().getText().toString();
        S_Country=ctry.getText().toString();
        S_Area =area.getText().toString();
        S_Password=Password.getEditText().getText().toString();


        if (S_FullName.isEmpty()){
            Name.setError("Name Required");
            Name.requestFocus();
            return;
        }

        if (S_ShopName.isEmpty()){
            ShopName.setError("Shop Required");
            ShopName.requestFocus();
            return;
        }

        if (S_Phone.isEmpty()){
            Phone.setError("Phone Required");
            Phone.requestFocus();
            return;
        }

        if (S_Email.isEmpty()){
            Email.setError("Email Address Required");
            Email.requestFocus();
            return;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(S_Email).matches()) {
            Email.setError( "Please enter a valid email address" );
            return;
        }

        if (S_Country.isEmpty()){
            ctry.setError("Country Required");
            ctry.requestFocus();
            return;
        }
        if (S_Area.isEmpty()){
            area.setError("Area Required");
            area.requestFocus();
            return;
        }
        if (S_Password.isEmpty()){
            Password.setError("Password Required");
            Password.requestFocus();
            return;
        }else {
            CreateAccountSeller();
        }
    }

    private void CreateAccountSeller() {
        progressDialog.setMessage( "Creating Account" );
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword( S_Email,S_Password ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                saveDateToFirebaseSeller();
            }
        } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText( getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT ).show();
            }
        } );
    }

    private void saveDateToFirebaseSeller() {
        progressDialog.setMessage( "Saving Info..." );
        String timeStamp = "" + System.currentTimeMillis();

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
                            Intent register = new Intent(  getActivity(), MainSellerActivity.class );
                            register.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                            startActivity( register );
                        }

                    } )
                    .addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(  getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT ).show();

                        }
                    } );
        } else {
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
                            Intent register = new Intent(  getActivity(), MainSellerActivity.class );
                            register.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
                            startActivity( register );
                        }

                    } )
                    .addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(  getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT ).show();

                        }
                    } );
        }
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
                Toast.makeText( getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT ).show();
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
                        Intent register = new Intent(getActivity(), MainUserActivity.class);
                        register.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(register);
                    }

                } )
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText( getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT ).show();

                    }
                } );

    }

    @Override
    public void onStart() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }else {
            getCurrentLocation();
        }
        super.onStart();
    }
}