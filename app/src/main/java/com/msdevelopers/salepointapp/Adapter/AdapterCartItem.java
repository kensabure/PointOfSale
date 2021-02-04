package com.msdevelopers.salepointapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.msdevelopers.salepointapp.R;
import com.msdevelopers.salepointapp.activities.ShopDetailsActivity;
import com.msdevelopers.salepointapp.model.ModelCart_OrderedItem;

import java.util.ArrayList;

import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterCartItem extends RecyclerView.Adapter<AdapterCartItem.HolderCartItem>{


    private Context context;
    private ArrayList<ModelCart_OrderedItem>cartItems;

    public AdapterCartItem(Context context, ArrayList<ModelCart_OrderedItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }



    @NonNull
    @Override
    public HolderCartItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from( context ).inflate( R.layout.row_cartitem,parent,false );
        return new HolderCartItem(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final HolderCartItem holder, final int position) {

        ModelCart_OrderedItem modelCartOrderedItem = cartItems.get(position  );
        final String id= modelCartOrderedItem.getId();
        final String title= modelCartOrderedItem.getName();
        final String cost= modelCartOrderedItem.getCost();
        final String price= modelCartOrderedItem.getPrice();
        final String quantity= modelCartOrderedItem.getQuantity();

        holder.ItemTitleTv.setText( ""+title );
        holder.ItemQty.setText( "["+quantity+"]" );
        holder.ItemPriceTv.setText(""+ cost );
        holder.itemPriceEachTv.setText( ""+price );

        holder.ItemRemove.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyDB easiestDB = EasyDB.init(context,"ITEMS_DB")
                        .setTableName("ITEMS_TABLE")
                        .addColumn(new Column("Item_Id",new String[]{"text","unique"}))
                        .addColumn(new Column("Item_PID",new String[]{"text","not null"}))
                        .addColumn(new Column("Item_Name",new String[]{"text","not null"}))
                        .addColumn(new Column("Item_Price_Each",new String[]{"text","not null"}))
                        .addColumn(new Column("Item_Price",new String[]{"text","not null"}))
                        .addColumn(new Column("Item_Quantity",new String[]{"text","not null"}))
                        .doneTableColumn();

                easiestDB.deleteRow( 1,id );
                Toast.makeText( context, "Delete Successfully ", Toast.LENGTH_SHORT ).show();
                cartItems.remove( position );
                notifyItemChanged( position );
                notifyDataSetChanged();

                double tx = Double.parseDouble( ((((ShopDetailsActivity)context).totalTv.getText().toString().trim().replace( "","" ))) );
                double totalP= tx-Double.parseDouble( cost.replace( "","" ) );
                double delivery = Double.parseDouble((((ShopDetailsActivity)context).deliveryFee.replace( "","" )));
                double sTotalPrice = Double.parseDouble( String.format( "%.2f",totalP ) )- Double.parseDouble( String.format( "%.2f",delivery ) );


                ((ShopDetailsActivity)context).AllTotalPrice=0.00;
                ((ShopDetailsActivity)context).sTotalTv.setText( ""+String.format( "%.2f",sTotalPrice ) );
                ((ShopDetailsActivity)context).totalTv.setText( ""+String.format( "%.2f",Double.parseDouble( String.format( "%.2f",totalP ) ) ) );
                ((ShopDetailsActivity)context).cartCounter();

            }
        } );


    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }



    class HolderCartItem extends RecyclerView.ViewHolder {

        private  TextView ItemTitleTv,ItemPriceTv,itemPriceEachTv,ItemQty;
        private ImageButton ItemRemove;
        public HolderCartItem(@NonNull View itemView) {
            super( itemView );
            ItemTitleTv=itemView.findViewById( R.id.ItemTitleTv );
            ItemPriceTv=itemView.findViewById( R.id.ItemPriceTv );
            itemPriceEachTv=itemView.findViewById( R.id.itemPriceEachTv );
            ItemQty=itemView.findViewById( R.id.ItemQty );
            ItemRemove=itemView.findViewById( R.id.ItemRemove );

        }
    }
}
