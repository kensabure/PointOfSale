package com.msdevelopers.salepointapp.activities;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.msdevelopers.salepointapp.Adapter.AdapterCartItem;
import com.msdevelopers.salepointapp.Adapter.AdapterProductUser;
import com.msdevelopers.salepointapp.Model.ModelProducts;
import com.msdevelopers.salepointapp.R;
import com.msdevelopers.salepointapp.model.Constraints;
import com.msdevelopers.salepointapp.model.ModelCart_OrderedItem;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class ShopDetailsActivity extends Activity {

    private ImageView shopIv;
    private TextView shopNameTv,phoneNoTv,email_address,OpenClose,DeliveryFee,area,filterTv,cartCounterTv;
    private ImageButton mapBtn,callBtn,cartBtn,back_btn,filterProduct;
    private EditText search;
    private RecyclerView recyclerShop;

    private String uid;
    private String myLatitude,myLongitude,myPhone;
    private String shopName,shopEmail,shopArea,shopPhone,shopLatitude,ShopId,
            shopLongitude,shopOpen,ProfileImage;
    public String deliveryFee;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    private ArrayList<ModelProducts> productsList;
    private AdapterProductUser adapterProductUser;

    private ArrayList<ModelCart_OrderedItem> cartItemList;
    private AdapterCartItem adapterCartItem;

    private ProgressDialog progressDialog;
    private AlertDialog dialog;
    private   EasyDB easiestDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_shop_details );
        this.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        shopNameTv=findViewById( R.id.shopNameTv );
        shopNameTv=findViewById( R.id.shopNameTv );
        phoneNoTv=findViewById( R.id.phoneNoTv );
        email_address=findViewById( R.id.email_address );
        OpenClose=findViewById( R.id.OpenClose );
        DeliveryFee=findViewById( R.id.deliveryFee );
        area=findViewById( R.id.area );
        filterTv=findViewById( R.id.filterTv );
        shopIv=findViewById( R.id.shopIv );
        cartCounterTv=findViewById( R.id.cartCounterTv );
        mapBtn=findViewById( R.id.mapBtn );
        callBtn=findViewById( R.id.callBtn );
        cartBtn=findViewById( R.id.cartBtn );
        back_btn=findViewById( R.id.back_btn );
        filterProduct=findViewById( R.id.filterProduct );
        search=findViewById( R.id.search );
        recyclerShop=findViewById( R.id.recyclerShop );

        uid=getIntent().getStringExtra( "uid" );

        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();

        progressDialog = new ProgressDialog( this );
        progressDialog.setCanceledOnTouchOutside( false );
        progressDialog.setMessage( "Submitting Order..Please  Waiting!" );

        easiestDB = EasyDB.init(this,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id",new String[]{"text","unique"}))
                .addColumn(new Column("Item_PID",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price_Each",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Quantity",new String[]{"text","not null"}))
                .doneTableColumn();

        loadMyInfo();
        loadShopDetails();
        loadShopProducts();
        deleteCartItem();
        cartCounter();
        
        
        back_btn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        } );
        cartBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showCartDialog();
            }
        } );
        callBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhone();
            }
        } );
        mapBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        } );

        search.addTextChangedListener( new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                try {
                    adapterProductUser.getFilter().filter( s );
                }catch (Exception e){
                    e.getStackTrace();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        } );

        filterProduct.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder  = new AlertDialog.Builder( ShopDetailsActivity.this );
                builder.setTitle( "Select Category" )
                        .setItems( Constraints.ItemCategories1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String selected = Constraints.ItemCategories1[which];
                                filterTv.setText( selected );
                                if (selected.equals( "All" )){
                                    loadShopProducts();
                                }else {
                                    adapterProductUser.getFilter().filter(selected);
                                }
                            }
                        } ).show();
            }
        } );

    }

    private void deleteCartItem() {

        easiestDB.deleteAllDataFromTable();
    }

    public void cartCounter(){
        int count = easiestDB.getAllData().getCount();
        if (count<=0){
            cartCounterTv.setVisibility( View.GONE );

        }else {
            cartCounterTv.setVisibility( View.VISIBLE );
            cartCounterTv.setText( ""+count );
        }
    }

    public Double AllTotalPrice=0.00;
    public TextView sTotalTv,dFee,totalTv;
    private void showCartDialog() {

        cartItemList= new ArrayList<>(  );
        View view= LayoutInflater.from( this ).inflate( R.layout.dialog_cart,null );

        RecyclerView cartRecycler=view.findViewById( R.id.cartRecycler );

        sTotalTv=view.findViewById( R.id.sTotalTv );
        dFee=view.findViewById( R.id.deliveryFee );
        totalTv=view.findViewById( R.id.totalTv );
        Button checkOutBtn=view.findViewById( R.id.checkOutBtn );


        AlertDialog.Builder builder= new AlertDialog.Builder( this );
        builder.setView( view );


        EasyDB easiestDB = EasyDB.init(this,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id",new String[]{"text","unique"}))
                .addColumn(new Column("Item_PID",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price_Each",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Quantity",new String[]{"text","not null"}))
                .doneTableColumn();

        Cursor res = easiestDB.getAllData();
        while (res.moveToNext()){
            String id =res.getString( 1 );
            String pId =res.getString( 2 );
            String name =res.getString( 3 );
            String price =res.getString( 4 );
            String cost =res.getString( 5 );
            String quantity =res.getString( 6 );

            AllTotalPrice= AllTotalPrice + Double.parseDouble( cost );

            ModelCart_OrderedItem modelCartOrderedItem = new ModelCart_OrderedItem( ""+id,
                    ""+pId,
                    ""+name,
                    ""+cost,
                    ""+price,
                    ""+quantity );
            cartItemList.add( modelCartOrderedItem );
            
        }
        adapterCartItem = new AdapterCartItem( ShopDetailsActivity.this,cartItemList );
        cartRecycler.setAdapter( adapterCartItem );

        dFee.setText( ""+ deliveryFee);
        sTotalTv.setText( ""+ String.format( "%.2f",AllTotalPrice ) );
        totalTv.setText( ""+ (AllTotalPrice + Double.parseDouble( deliveryFee.replace( "","" ) )) );

        if (AllTotalPrice.equals( 0.00 )){
            dFee.setText(  ""+0.00 );
            sTotalTv.setText(""+ 0.00 );
            totalTv.setText( ""+ 0.00 );
        }

        dialog = builder.create();
        dialog.show();

        dialog.setOnCancelListener( new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                AllTotalPrice = 0.00;
            }
        } );

        checkOutBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cartItemList.size()==0){

                    Toast.makeText( ShopDetailsActivity.this, "No Items...Please Add To Cart ", Toast.LENGTH_SHORT ).show();
                    return;
                }

                SubmitOrder();

            }
        } );

    }

    private void SubmitOrder() {

        progressDialog.show();
        final String timeStamp = ""+ System.currentTimeMillis();

        String cost = totalTv.getText().toString().trim().replace( "","" );

        HashMap<String,String>hashMap = new HashMap<>(  );
        hashMap.put( "OrderId", ""+timeStamp );
        hashMap.put( "OrderTime", ""+timeStamp );
        hashMap.put( "OrderStatus", "In Progress" );
        hashMap.put( "OrderCost", ""+cost );
        hashMap.put( "OrderBy", ""+firebaseAuth.getUid() );
        hashMap.put( "OrderTo", ""+uid );
        hashMap.put( "latitude", ""+myLatitude );
        hashMap.put( "longitude", ""+myLongitude );

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child( uid ).child( "Orders" );
        ref.child( timeStamp ).setValue( hashMap )
                .addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        for (int i=0; i<cartItemList.size(); i++){
                            String pId = cartItemList.get( i ).getpId();
                            String id = cartItemList.get( i ).getId();
                            String cost = cartItemList.get( i ).getCost();
                            String name = cartItemList.get( i ).getName();
                            String price = cartItemList.get( i ).getPrice();
                            String quantity = cartItemList.get( i ).getQuantity();

                            HashMap<String,String> hashMap1 = new HashMap<>(  );

                            hashMap1.put( "pId",pId );
                            hashMap1.put( "name",name );
                            hashMap1.put( "cost",cost );
                            hashMap1.put( "price",price );
                            hashMap1.put( "quantity",quantity );

                            ref.child( timeStamp ).child( "Items" ).child( id ).setValue( hashMap1 );
                        }
                        progressDialog.dismiss();
                        Toast.makeText( ShopDetailsActivity.this, "Order Placed Successfully", Toast.LENGTH_SHORT ).show();

                        dialog.dismiss();
                        Intent intent = new Intent( ShopDetailsActivity.this, MainUserActivity.class );
                        startActivity( intent );


                    }
                } )
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progressDialog.dismiss();
                        Toast.makeText( ShopDetailsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT ).show();
                    }
                } );
    }

    private void openMap() {
        String address = "https://maps.google.com/maps?saddr="+myLatitude + ","  +myLongitude + "&daddr=" +shopLatitude+ "," +shopLongitude;
        Intent intent= new Intent( Intent.ACTION_VIEW, Uri.parse( address ) );
        startActivity( intent );
    }

    private void dialPhone() {
        startActivity( new Intent( Intent.ACTION_DIAL, Uri.parse( "tel:"+Uri.encode( shopPhone ) ) ) );
        Toast.makeText( this, ""+shopPhone, Toast.LENGTH_SHORT ).show();
    }

    private void loadMyInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        ref.orderByChild( "uid" ).equalTo( firebaseAuth.getUid() )
                .addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                            myLatitude=""+snapshot.child( "latitude" ).getValue();
                            myLongitude=""+snapshot.child( "longitude" ).getValue();
                            myPhone=""+snapshot.child( "phone" ).getValue();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );
    }

    private void loadShopDetails() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");

        ref.child( uid ).addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                shopName=""+snapshot.child( "shopName" ).getValue();
                                shopEmail=""+snapshot.child( "email" ).getValue();
                                shopPhone=""+snapshot.child( "phone" ).getValue();
                                shopArea=""+snapshot.child( "area" ).getValue();
                                shopLatitude=""+snapshot.child( "latitude" ).getValue();
                                shopLongitude=""+snapshot.child( "longitude" ).getValue();
                                deliveryFee=""+snapshot.child( "deliveryFee" ).getValue();
                                ProfileImage=""+snapshot.child( "ProfileImage" ).getValue();
                                shopOpen=""+snapshot.child( "shopOpen" ).getValue();

                                shopNameTv.setText( shopName );
                                email_address.setText( shopEmail );
                                DeliveryFee.setText( "Delivery Fee:"+deliveryFee );
                                area.setText( shopArea );
                                phoneNoTv.setText( shopPhone );

                                if (shopOpen.equals( "true" )){
                                    OpenClose.setText( "Open" );
                                }else {
                                    OpenClose.setText( "Closed" );
                                }

                                try {
                                    Picasso.get().load(ProfileImage).into( shopIv );
                                }catch (Exception e){

                                }

                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );
    }
    private void loadShopProducts() {
        productsList= new ArrayList<>(  );

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.child( uid ).child( "products")
                .addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        productsList.clear();

                        for (DataSnapshot ds : snapshot.getChildren()){
                            ModelProducts modelProducts= ds.getValue(ModelProducts.class);
                            productsList.add( modelProducts );
                        }
                        adapterProductUser = new AdapterProductUser( ShopDetailsActivity.this,productsList );
                        recyclerShop.setAdapter( adapterProductUser );
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );
    }

    public void prepareNotificationMessage(String OrderId){
        String NOTIFICATION_TOPIC = "/topic/" + Constraints.FCM_TOPIC;
        String NOTIFICATION_TITLE = "New Order "+ OrderId;
        String NOTIFICATION_MESSAGE = "CONGRATULATIONS...! You have new Order.";
        String NOTIFICATION_TYPE = "NewOrder";

        JSONObject notificationsJo = new JSONObject(  );
        JSONObject notificationsJBodyJo = new JSONObject(  );

        try {

            notificationsJBodyJo.put( "notificationType",NOTIFICATION_TYPE );
            notificationsJBodyJo.put( "buyerUid",firebaseAuth.getUid() );
            notificationsJBodyJo.put( "SellerUid",uid );
            notificationsJBodyJo.put( "OrderId",OrderId );
            notificationsJBodyJo.put( "notificationTitle",NOTIFICATION_TITLE );
            notificationsJBodyJo.put( "notificationMessage",NOTIFICATION_MESSAGE );

            notificationsJo.put( "to", NOTIFICATION_TOPIC );
            notificationsJo.put( "data", notificationsJo );

        }catch (Exception e){

            Toast.makeText( this, ""+e.getMessage(), Toast.LENGTH_SHORT ).show();

        }

        sendNotification(notificationsJo,OrderId);
    }

    private void sendNotification(JSONObject notificationsJo, final String orderId) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest( "https://fcm.googleapis.com/fcm/send", notificationsJo, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Intent intent = new Intent( ShopDetailsActivity.this, OrdersDetailsUserActivity.class );
                intent.putExtra( "OrderTo",uid );
                intent.putExtra( "OrderId", orderId);
                startActivity( intent );
            }
        } ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                Map<String, String>headers = new HashMap<>(  );
                headers.put( "Content-Type","application/json" );
                headers.put( "Authorization","key="+Constraints.FCM_KEY );

                return headers;
            }
        };

        Volley.newRequestQueue( this ).add( jsonObjectRequest );
    }

}
