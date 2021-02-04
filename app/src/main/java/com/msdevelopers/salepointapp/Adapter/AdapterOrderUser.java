package com.msdevelopers.salepointapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.msdevelopers.salepointapp.R;
import com.msdevelopers.salepointapp.activities.OrdersDetailsUserActivity;
import com.msdevelopers.salepointapp.model.ModelOrderUser;


import java.util.ArrayList;
import java.util.Calendar;

public class AdapterOrderUser extends RecyclerView.Adapter<AdapterOrderUser.HolderOrderUser>{

    private Context context;
    private ArrayList<ModelOrderUser>modelOrderUseList;

    public AdapterOrderUser(Context context, ArrayList<ModelOrderUser> modelOrderUseList) {
        this.context = context;
        this.modelOrderUseList = modelOrderUseList;
    }

    @NonNull
    @Override
    public HolderOrderUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from( context ).inflate( R.layout.row_order_user,parent,false );
        return new HolderOrderUser( view );
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderUser holder, int position) {

        final ModelOrderUser modelOrderUser= modelOrderUseList.get( position );

        final String OrderId = modelOrderUser.getOrderId();
        String OrderBy = modelOrderUser.getOrderBy();
        String OrderCost = modelOrderUser.getOrderCost();
        String OrderStatus = modelOrderUser.getOrderStatus();
        String OrderTime = modelOrderUser.getOrderTime();
        final String OrderTo = modelOrderUser.getOrderTo();
        
        loadShopInfo(modelOrderUser,holder);

        holder.amountTv.setText( "Amount: "+OrderCost );
        holder.status.setText( "Order Status: "+OrderStatus );
        holder.orderIdTv.setText( "OrderID: "+OrderId );

        if (OrderStatus.equals( "In Progress" )){
            holder.status.setTextColor( context.getResources().getColor( R.color.colorPrimary ) );
        }
        else if (OrderStatus.equals( "Completed" )){
            holder.status.setTextColor( context.getResources().getColor( R.color.green ) );
        }
        else if (OrderStatus.equals( "Cancelled" )){
            holder.status.setTextColor( context.getResources().getColor( R.color.red ) );
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis( Long.parseLong( OrderTime ) );
        String formatedDate = DateFormat.format( "dd/MM/yyyy", calendar ).toString();
        
        holder.orderDateTv.setText( formatedDate );

        holder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( context, OrdersDetailsUserActivity.class );
                intent.putExtra( "OrderId",OrderId );
                intent.putExtra( "OrderTo",OrderTo );
                context.startActivity( intent );
            }
        } );




    }

    private void loadShopInfo(ModelOrderUser modelOrderUser, final HolderOrderUser holder) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child( modelOrderUser.getOrderTo() ).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String ShopName = ""+snapshot.child( "shopName" ).getValue();
                holder.shopNameTv.setText( ShopName );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }

    @Override
    public int getItemCount() {
        return modelOrderUseList.size();
    }

    class HolderOrderUser extends RecyclerView.ViewHolder{


        private TextView orderIdTv,orderDateTv,shopNameTv,amountTv,status;
        private ImageView nextIv;

        public HolderOrderUser(@NonNull View itemView) {
            super( itemView );

            orderIdTv = itemView.findViewById( R.id.orderIdTv );
            orderDateTv = itemView.findViewById( R.id.orderDateTv );
            shopNameTv = itemView.findViewById( R.id.shopNameTv );
            amountTv = itemView.findViewById( R.id.amountTv );
            status = itemView.findViewById( R.id.status );
            nextIv = itemView.findViewById( R.id.nextIv );
        }
    }
}
