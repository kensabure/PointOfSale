package com.msdevelopers.salepointapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.msdevelopers.salepointapp.R;
import com.msdevelopers.salepointapp.activities.ShopDetailsActivity;
import com.msdevelopers.salepointapp.model.ModelShop;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterShop extends RecyclerView.Adapter<AdapterShop.HolderShop>{

    private Context context;
    ArrayList<ModelShop>shopsList;

    public AdapterShop(Context context, ArrayList<ModelShop> shopsList) {
        this.context = context;
        this.shopsList = shopsList;
    }
    @NonNull
    @Override
    public HolderShop onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from( context ).inflate( R.layout.row_shops,parent,false );
        return new HolderShop( view );
    }

    @Override
    public void onBindViewHolder(@NonNull final HolderShop holder, int position) {
        final ModelShop modelShop= shopsList.get( position );

        final String shopUid = modelShop.getUid();
        String email = modelShop.getEmail();
        String name = modelShop.getName();
        String shopName = modelShop.getShopName();
        String deliveryFee = modelShop.getDeliveryFee();
        String phone = modelShop.getPhone();
        final String country = modelShop.getCountry();
        String area = modelShop.getArea();
        String latitude = modelShop.getLatitude();
        String longitude = modelShop.getLongitude();
        String timestamp = modelShop.getTimestamp();
        String accountType = modelShop.getAccountType();
        String online = modelShop.getOnline();
        final String shopOpen = modelShop.getShopOpen();
        String ProfileImage = modelShop.getProfileImage();

        holder.shopNameTv.setText( shopName );
        holder.phoneNoTv.setText( phone );
        holder.address.setText( area );

        if (shopOpen.equals( "true" )){
            holder.OnlineIv.setVisibility( View.VISIBLE );
        }else {
            holder.OnlineIv.setVisibility( View.GONE );
        }
        if (shopOpen.equals( "true" )){
            holder.closed.setVisibility( View.GONE );
        }else {
            holder.closed.setVisibility( View.VISIBLE );
        }
        try {
            Picasso.get().load(ProfileImage).placeholder( R.drawable.posicon ).into( holder.shopIv );
        }catch (Exception e){

            holder.shopIv.setImageResource( R.drawable.posicon );
        }

        holder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shopOpen.equals( "false" )){
                    Toast.makeText( context, "Shop Closed: Check later", Toast.LENGTH_SHORT ).show();

                }else {
                Intent intent=new Intent( context, ShopDetailsActivity.class );
                intent.putExtra( "uid",shopUid );
                context.startActivity( intent );
                }
            }
        } );

    }

    @Override
    public int getItemCount() {
        return shopsList.size();
    }

    class HolderShop extends RecyclerView.ViewHolder{

        private CircleImageView shopIv;
        private ImageView OnlineIv;
        private TextView closed,shopNameTv,phoneNoTv,address;
        private RatingBar ratingBar;

        public HolderShop(@NonNull View itemView) {
            super( itemView );
            shopIv=itemView.findViewById( R.id.shopIv );
            OnlineIv=itemView.findViewById( R.id.OnlineIv );
            closed=itemView.findViewById( R.id.closed );
            shopNameTv=itemView.findViewById( R.id.shopNameTv );
            phoneNoTv=itemView.findViewById( R.id.phoneNoTv );
            address=itemView.findViewById( R.id.address );
            ratingBar=itemView.findViewById( R.id.ratingBar );
        }
    }
}
