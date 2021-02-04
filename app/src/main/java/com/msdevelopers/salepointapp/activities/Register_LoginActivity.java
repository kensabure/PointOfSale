package com.msdevelopers.salepointapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.msdevelopers.salepointapp.R;
import com.msdevelopers.salepointapp.fragments.LoginFragment;
import com.msdevelopers.salepointapp.fragments.RegisterFragment;
import com.msdevelopers.salepointapp.helpers.SectionsPageAdapter;
import com.rey.material.widget.FloatingActionButton;

import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class Register_LoginActivity extends AppCompatActivity {

    private static final String TAG= ("Register_LoginActivity");


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
    private SectionsPageAdapter msectionsPageAdapter;
    private ViewPager mViewPager;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register__login );
        this.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        msectionsPageAdapter= new SectionsPageAdapter(getSupportFragmentManager());

        locationPermission = new String[] {Manifest.permission.ACCESS_FINE_LOCATION };
        locationPermission = new String[]{ACCESS_FINE_LOCATION};



        //new instance of the Geocoder class
        geocoder = new Geocoder( this, Locale.getDefault() );

        //permission popup


        mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);

        mViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setText("Login");
        tabLayout.getTabAt(1).setText("Register");
        tabLayout.setTabGravity( TabLayout.GRAVITY_FILL );

        mViewPager.addOnPageChangeListener(  new TabLayout.TabLayoutOnPageChangeListener( tabLayout ) );
        tabLayout.addOnTabSelectedListener( new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        mViewPager.setCurrentItem( 0 );
                        getSupportActionBar().setTitle( "Login");
                        break;

                    case 1:
                        mViewPager.setCurrentItem( 1 );
                        getSupportActionBar().setTitle( "Register");
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        } );

    }

    private void setupViewPager(ViewPager mViewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new LoginFragment());
        adapter.addFragment(new RegisterFragment());
        mViewPager.setAdapter(adapter);
    }
}
