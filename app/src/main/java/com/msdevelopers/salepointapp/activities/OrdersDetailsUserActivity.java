package com.msdevelopers.salepointapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.msdevelopers.salepointapp.Adapter.AdapterOrderedItem;
import com.msdevelopers.salepointapp.R;
import com.msdevelopers.salepointapp.model.ModelCart_OrderedItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class OrdersDetailsUserActivity extends Activity {
    private String OrderTo,OrderId;


    private ImageView back_btn;
    private TextView orderIdTv,orderDateTv,shopNameTv,ItemsTotalTv,amountTv,shipAddress,status;
    private RecyclerView OrderedRecyclerView;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelCart_OrderedItem> orderedItemList;
    private AdapterOrderedItem adapterOrderedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_orders_details_user );

        back_btn = findViewById( R.id.back_btn );
        orderIdTv = findViewById( R.id.orderIdTv );
        orderDateTv = findViewById( R.id.orderDateTv );
        status = findViewById( R.id.status );
        shopNameTv = findViewById( R.id.shopNameTv );
        ItemsTotalTv = findViewById( R.id.ItemsTotalTv );
        amountTv = findViewById( R.id.amountTv );
        shipAddress = findViewById( R.id.shipAddress );
        OrderedRecyclerView = findViewById( R.id.OrderedRecyclerView );
        
        
        firebaseAuth = FirebaseAuth.getInstance();
        OrderId = getIntent().getStringExtra( "OrderId" );
        OrderTo = getIntent().getStringExtra( "OrderTo" );

        loadShopInfo();
        loadOrderDetails();
        loadOrderItems();




        back_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        } );
    }

    private void loadOrderItems() {
        orderedItemList= new ArrayList<>(  );

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");

        ref.child( OrderTo ).child( "Orders" ).child( OrderId ).child( "Items" ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              orderedItemList.clear();

              for (DataSnapshot ds: snapshot.getChildren()){
                  ModelCart_OrderedItem modelCart_orderedItem = ds.getValue(ModelCart_OrderedItem.class);
                  orderedItemList.add( modelCart_orderedItem );
              }
              adapterOrderedItem= new AdapterOrderedItem( OrdersDetailsUserActivity.this,orderedItemList );
              OrderedRecyclerView.setAdapter( adapterOrderedItem );

              ItemsTotalTv.setText( ""+snapshot.getChildrenCount() );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void loadOrderDetails() {

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");

        ref.child( OrderTo ).child( "Orders" ).child( OrderId ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String OrderBy = "" + snapshot.child( "OrderBy" ).getValue();
                String OrderCost= "" + snapshot.child( "OrderCost" ).getValue();
                String OrderId= "" + snapshot.child( "OrderId" ).getValue();
                String OrderStatus= "" + snapshot.child( "OrderStatus" ).getValue();
                String OrderTime= "" + snapshot.child( "OrderTime" ).getValue();
                String OrderTo= "" + snapshot.child( "OrderTo" ).getValue();
                String latitude= "" + snapshot.child( "latitude" ).getValue();
                String longitude= "" + snapshot.child( "OrderTime" ).getValue();
                String delivery= "" + snapshot.child( "deliveryFee" ).getValue();

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis( Long.parseLong( OrderTime ) );
                String formatedDate = DateFormat.format( "dd/MM/yyyy hh:mm a ", calendar ).toString();

               orderDateTv.setText( formatedDate );

                orderIdTv.setText( OrderId );
                amountTv.setText( ""+OrderCost);
                status.setText( OrderStatus );



                if (OrderStatus.equals( "In Progress" )){
                    status.setTextColor( getApplicationContext().getResources().getColor( R.color.colorPrimary ) );
                }
                else if (OrderStatus.equals( "Completed" )){
                    status.setTextColor( getApplicationContext().getResources().getColor( R.color.green ) );
                }
                else if (OrderStatus.equals( "Cancelled" )){
                    status.setTextColor( getApplicationContext().getResources().getColor( R.color.red ) );
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );

    }

    private void loadShopInfo() {

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.child( OrderTo );
        ref.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String ShopName = "" + snapshot.child( "shopName" ).getValue();

                shopNameTv.setText( ShopName );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }
}
