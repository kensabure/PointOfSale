package com.msdevelopers.salepointapp.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.msdevelopers.salepointapp.Adapter.AdapterProductSeller;
import com.msdevelopers.salepointapp.Model.ModelProducts;
import com.msdevelopers.salepointapp.R;
import com.msdevelopers.salepointapp.model.Constraints;
import com.rey.material.widget.FloatingActionButton;

import java.util.ArrayList;

import p32929.androideasysql_library.EasyDB;


public class OffersFragment extends Fragment {

    private ImageButton ProfileEdit, FilterBtn, add_more, logout, filterOrders;
    private TextView checkTv, tab_products, tab_orders, FilterTv, searchOrders;
    private EditText Search;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    public String uid;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private ArrayList<ModelProducts> productsList;
    private AdapterProductSeller adapterProductSeller;

    private ProgressDialog progressDialog;
    private AlertDialog dialog;
    private EasyDB easiestDB;
    private View view;

    public OffersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate( R.layout.fragment_offers, container, false );
        this.getActivity().getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );
        recyclerView = view.findViewById( R.id.recyclerView );

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        FilterTv = view.findViewById( R.id.filterTv );
        checkTv = view.findViewById( R.id.checkTv );
        FilterBtn = view.findViewById( R.id.filterProduct );
        Search = view.findViewById( R.id.search );

        loadAllProducts();
        Search.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                try {
                    adapterProductSeller.getFilter().filter( s );
                } catch (Exception e) {
                    e.getStackTrace();

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );

        FilterBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
                builder.setTitle( "Select Category" )
                        .setItems( Constraints.ItemCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String selected = Constraints.ItemCategories1[which];
                                FilterTv.setText( selected );
                                if (selected.equals( "All" )) {
                                    loadAllProducts();
                                } else {
                                    loadFilteredProduct( selected );
                                }
                            }
                        } ).show();
            }
        } );

        return view;
    }

    private void loadFilteredProduct(final String selected) {

        productsList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference( "Users" );
        reference.child( firebaseAuth.getUid() ).child( "products" )
                .addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productsList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String category = "" + ds.child( "category" ).getValue();

                            if (selected.equals( category )) {
                                ModelProducts modelProducts = ds.getValue( ModelProducts.class );
                                productsList.add( modelProducts );
                                FilterTv.setText( selected );
                                FilterTv.setTextColor( getActivity().getResources().getColor( R.color.blackTextColor ) );
                            } else
                                FilterTv.setText( "Not found" );
                            FilterTv.setTextColor( getActivity().getResources().getColor( R.color.red ) );

                        }
                        adapterProductSeller = new AdapterProductSeller( getContext(), productsList );
                        recyclerView.setAdapter( adapterProductSeller );
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText( getContext(), "Failed" + error.getMessage(), Toast.LENGTH_SHORT ).show();
                    }
                } );
    }

    private void loadAllProducts() {
        productsList = new ArrayList<>();

        String time = "" + System.currentTimeMillis();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference( "Users" );
        reference.child( firebaseAuth.getUid() ).child( "products" ).orderByChild( "discountAvailable" ).equalTo( "true" )
                .addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        productsList.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {

                            ModelProducts modelProducts = ds.getValue( ModelProducts.class );
                            productsList.add( modelProducts );
                        }
                        adapterProductSeller = new AdapterProductSeller( getContext(), productsList );
                        recyclerView.setAdapter( adapterProductSeller );
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText( getContext(), "Failed" + error.getMessage(), Toast.LENGTH_SHORT ).show();
                    }
                } );
    }

    @Override
    public void onStart() {


        super.onStart();
    }
}