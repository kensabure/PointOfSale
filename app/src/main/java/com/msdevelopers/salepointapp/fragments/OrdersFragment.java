package com.msdevelopers.salepointapp.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.msdevelopers.salepointapp.Adapter.AdapterOrderShop;
import com.msdevelopers.salepointapp.Adapter.AdapterProductSeller;
import com.msdevelopers.salepointapp.Model.ModelProducts;
import com.msdevelopers.salepointapp.R;
import com.msdevelopers.salepointapp.model.ModelOrderUser;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class OrdersFragment extends Fragment {

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private String[] cameraPermission;
    private String[] storagePermission;
    private String shopUid;

    private TextView SellerNameTv,tab_products,tab_orders,FilterTv,searchOrders;
    private ImageButton ProfileEdit,FilterBtn,add_more,logout,filterOrders;
    private TextView SellerBusiness;
    private CircleImageView imageView;
    private FloatingActionButton fab;
    private RelativeLayout Rel1,Rel2;
    private EditText Search;
    private RecyclerView recyclerView,OrderRecyclerView;

    private ArrayList<ModelProducts> productsList;
    private AdapterProductSeller adapterProductSeller;

    private ArrayList<ModelOrderUser> ordersList;
    private AdapterOrderShop adapterOrderShop;

    private View view;

    public OrdersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_orders, container, false);


        OrderRecyclerView = view.findViewById( R.id.OrderRecyclerView );
        filterOrders = view.findViewById( R.id.filterOrders );
        searchOrders = view.findViewById( R.id.searchOrders );

        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();


        loadAllOrders();

        filterOrders.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[]options= {"All", "In Progress","Completed","Cancelled"};
                AlertDialog.Builder builder  = new AlertDialog.Builder( getContext() );
                builder.setTitle( "Filter Order" )
                        .setItems( options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String selected = options[which];

                                if (which==0){
                                    searchOrders.setText( "All Orders" );
                                    adapterOrderShop.getFilter().filter( "" );
                                }else {
                                    searchOrders.setText( "Showing "+selected+" Orders" );
                                    adapterOrderShop.getFilter().filter( selected );
                                }
                            }
                        } ).show();
            }
        } );
        
        return view;

    }

    private void loadAllOrders() {
        ordersList= new ArrayList<>(  );

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child( firebaseAuth.getUid() ).child( "Orders" )
                .addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        ordersList.clear();
                        for (DataSnapshot ds: snapshot.getChildren()){
                            ModelOrderUser modelOrderUser = ds.getValue(ModelOrderUser.class);
                            ordersList.add( modelOrderUser );

                        }
                        adapterOrderShop = new AdapterOrderShop( getContext(),ordersList );
                        OrderRecyclerView.setAdapter( adapterOrderShop );
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );
    }
}