package com.msdevelopers.salepointapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.msdevelopers.salepointapp.Adapter.AdapterOrderedItem;
import com.msdevelopers.salepointapp.R;
import com.msdevelopers.salepointapp.model.Constraints;
import com.msdevelopers.salepointapp.model.ModelCart_OrderedItem;


import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrdersDetailsSellerActivity extends Activity {

    private String OrderBy,OrderId;
    private String myLatitude,myLongitude,myPhone;
    private String shopName,shopEmail,shopArea,shopPhone,shopLatitude,ShopId,
            shopLongitude,shopOpen,ProfileImage;
    String SourceLatitude,SourceLongitude,DestinationLatitude,DestinationLongitude;

    private ImageView back_btn,editBtn,mapBtn,phoneIv;
    private TextView orderIdTv,orderDateTv,EmailTv,ItemsTotalTv,amountTv,shipAddress,status,phoneNoTv;
    private RecyclerView OrderedRecyclerView;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelCart_OrderedItem> orderedItemList;
    private AdapterOrderedItem adapterOrderedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_orders_details_seller );

        back_btn = findViewById( R.id.back_btn );
        editBtn = findViewById( R.id.editBtn );
        mapBtn = findViewById( R.id.mapBtn );
        phoneNoTv = findViewById( R.id.phoneNoTv );
        phoneIv = findViewById( R.id.phoneIv );
        orderIdTv = findViewById( R.id.orderIdTv );
        orderDateTv = findViewById( R.id.orderDateTv );
        status = findViewById( R.id.status );
        EmailTv = findViewById( R.id.EmailTv );
        ItemsTotalTv = findViewById( R.id.ItemsTotalTv );
        amountTv = findViewById( R.id.amountTv );
        shipAddress = findViewById( R.id.shipAddress );
        OrderedRecyclerView = findViewById( R.id.OrderedRecyclerView );


        OrderId = getIntent().getStringExtra( "OrderId" );
        OrderBy = getIntent().getStringExtra( "OrderBy" );
        firebaseAuth = FirebaseAuth.getInstance();

        loadMyInfo();
        loadBuyerInfo();
        loadOrderDetails();
        loadOrderItems();



        editBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editOrderStatus();
            }
        } );
        back_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        } );
        mapBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        } );
        phoneIv.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhone();
            }
        } );
    }

    private void editOrderStatus() {
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        final String[] options ={"In Progress","Completed","Cancelled"};
        builder.setTitle( "Edit Order Status" )
                .setItems( options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selected = options[which];
                        editStatus(selected);

                    }
                } )
                .show();
    }

    private void editStatus(final String selected) {
        HashMap<String, Object> hashMap = new HashMap<>(  );
        hashMap.put( "OrderStatus",""+selected );
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference( "Users" );

        ref.child( firebaseAuth.getUid() ).child( "Orders" ).child( OrderId )
                .updateChildren( hashMap ).addOnSuccessListener( new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                String message="Order is Now"+selected;
                Toast.makeText( OrdersDetailsSellerActivity.this,message , Toast.LENGTH_SHORT ).show();

                prepareNotificationMessage( OrderId,message );

            }
        } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( OrdersDetailsSellerActivity.this, "FAILED"+e.getMessage(), Toast.LENGTH_SHORT ).show();
            }
        } );

    }

    private void loadOrderItems() {
        orderedItemList = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference( "Users" );

        ref.child( firebaseAuth.getUid() ).child( "Orders" ).child( OrderId ).child( "Items" ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderedItemList.clear();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelCart_OrderedItem modelCart_orderedItem = ds.getValue( ModelCart_OrderedItem.class );
                    orderedItemList.add( modelCart_orderedItem );
                }
                adapterOrderedItem = new AdapterOrderedItem( OrdersDetailsSellerActivity.this, orderedItemList );
                OrderedRecyclerView.setAdapter( adapterOrderedItem );

                ItemsTotalTv.setText( "" + snapshot.getChildrenCount() );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    private void loadOrderDetails() {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference( "Users" );

            ref.child( firebaseAuth.getUid() ).child( "Orders" ).child( OrderId ).addValueEventListener( new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    String OrderBy = "" + snapshot.child( "OrderBy" ).getValue();
                    String OrderCost = "" + snapshot.child( "OrderCost" ).getValue();
                    String OrderId = "" + snapshot.child( "OrderId" ).getValue();
                    String OrderStatus = "" + snapshot.child( "OrderStatus" ).getValue();
                    String OrderTime = "" + snapshot.child( "OrderTime" ).getValue();
                    String OrderTo = "" + snapshot.child( "OrderTo" ).getValue();
                    String latitude = "" + snapshot.child( "latitude" ).getValue();
                    String longitude = "" + snapshot.child( "OrderTime" ).getValue();
                    String delivery = "" + snapshot.child( "deliveryFee" ).getValue();

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis( Long.parseLong( OrderTime ) );
                    String formatedDate = DateFormat.format( "dd/MM/yyyy hh:mm a ", calendar ).toString();

                    orderDateTv.setText( formatedDate );

                    orderIdTv.setText( OrderId );
                    amountTv.setText( "" + OrderCost );
                    status.setText( OrderStatus );


                    if (OrderStatus.equals( "In Progress" )) {
                        status.setTextColor( getApplicationContext().getResources().getColor( R.color.colorPrimary ) );
                    } else if (OrderStatus.equals( "Completed" )) {
                        status.setTextColor( getApplicationContext().getResources().getColor( R.color.green ) );
                    } else if (OrderStatus.equals( "Cancelled" )) {
                        status.setTextColor( getApplicationContext().getResources().getColor( R.color.red ) );
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            } );
    }

    private void findAddress(String latitude, String longitude) {
        double lat = Double.parseDouble( latitude );
        double lng = Double.parseDouble( longitude );

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder( this, Locale.getDefault() );

        try {
            addresses = geocoder.getFromLocation( lat,lng, 0 );

            String address = addresses.get( 0 ).getAddressLine( 0 );


        } catch (Exception e) {
            Toast.makeText( this, ""+e.getMessage(), Toast.LENGTH_SHORT ).show();
        }
    }

    private void openMap() {
        String address = "https://maps.google.com/maps?saddr="+SourceLatitude + ","  +SourceLongitude + "&daddr=" +DestinationLatitude+ "," +DestinationLongitude;
        Intent intent= new Intent( Intent.ACTION_VIEW, Uri.parse( address ) );
        startActivity( intent );
    }
    private void loadMyInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference( "Users" );
        ref.child( firebaseAuth.getUid() )
                .addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        DestinationLatitude = "" + snapshot.child( "latitude" ).getValue();
                        DestinationLongitude = "" + snapshot.child( "longitude" ).getValue();
                        myPhone = "" + snapshot.child( "phone" ).getValue();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );
    }

        private void loadBuyerInfo() {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference( "Users" );
            ref.child( OrderBy )
                    .addValueEventListener( new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            SourceLatitude = "" + snapshot.child( "latitude" ).getValue();
                            SourceLatitude = "" + snapshot.child( "longitude" ).getValue();
                            myPhone = "" + snapshot.child( "phone" ).getValue();
                            shopEmail = "" + snapshot.child( "email" ).getValue();

                            phoneNoTv.setText( myPhone );
                            EmailTv.setText( shopEmail );
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    } );
    }

    private void dialPhone() {
        String phone = phoneNoTv.getText().toString();
        startActivity( new Intent( Intent.ACTION_DIAL, Uri.parse( "tel:"+Uri.encode( phone ) ) ) );
        Toast.makeText( this, ""+phone, Toast.LENGTH_SHORT ).show();
    }

    public void prepareNotificationMessage(String OrderId,String message){
        String NOTIFICATION_TOPIC = "/topic/" + Constraints.FCM_TOPIC;
        String NOTIFICATION_TITLE = "Your Order "+ OrderId;
        String NOTIFICATION_MESSAGE = ""+message;
        String NOTIFICATION_TYPE = "OrderStatusChanged";

        JSONObject notificationsJo = new JSONObject(  );
        JSONObject notificationsJBodyJo = new JSONObject(  );

        try {

            notificationsJBodyJo.put( "notificationType",NOTIFICATION_TYPE );
            notificationsJBodyJo.put( "buyerUid",OrderBy );
            notificationsJBodyJo.put( "SellerUid",firebaseAuth.getUid() );
            notificationsJBodyJo.put( "OrderId",OrderId );
            notificationsJBodyJo.put( "notificationTitle",NOTIFICATION_TITLE );
            notificationsJBodyJo.put( "notificationMessage",NOTIFICATION_MESSAGE );

            notificationsJo.put( "to", NOTIFICATION_TOPIC );
            notificationsJo.put( "data", notificationsJo );

        }catch (Exception e){

            Toast.makeText( this, ""+e.getMessage(), Toast.LENGTH_SHORT ).show();

        }

        sendNotification(notificationsJo);
    }

    private void sendNotification(JSONObject notificationsJo) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( "https://fcm.googleapis.com/fcm/send", notificationsJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


            }
        } ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String>headers = new HashMap<>(  );
                headers.put( "Content-Type","application/json" );
                headers.put( "Authorization","key="+ Constraints.FCM_KEY );

                return headers;
            }
        };

        Volley.newRequestQueue( this ).add( jsonObjectRequest );
    }
}
