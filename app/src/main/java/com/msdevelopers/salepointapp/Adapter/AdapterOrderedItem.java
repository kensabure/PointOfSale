package com.msdevelopers.salepointapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.msdevelopers.salepointapp.R;
import com.msdevelopers.salepointapp.model.ModelCart_OrderedItem;

import java.util.ArrayList;

public class AdapterOrderedItem extends RecyclerView.Adapter<AdapterOrderedItem.HolderOrderedItem>{

    private Context context;
    private ArrayList<ModelCart_OrderedItem> orderedItemList;

    public AdapterOrderedItem(Context context, ArrayList<ModelCart_OrderedItem> orderedItemList) {
        this.context = context;
        this.orderedItemList = orderedItemList;
    }

    @NonNull
    @Override
    public HolderOrderedItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from( context ).inflate( R.layout.row_ordereditem,parent,false );
        return new HolderOrderedItem( view );
    }

    @Override
    public void onBindViewHolder(@NonNull HolderOrderedItem holder, int position) {

        ModelCart_OrderedItem modelCartOrderedItem = orderedItemList.get(position  );
        final String id= modelCartOrderedItem.getId();
        final String title= modelCartOrderedItem.getName();
        final String cost= modelCartOrderedItem.getCost();
        final String price= modelCartOrderedItem.getPrice();
        final String quantity= modelCartOrderedItem.getQuantity();

        holder.ItemTitleTv.setText( ""+title );
        holder.ItemQty.setText( "["+quantity+"]" );
        holder.ItemPriceTv.setText(""+ cost );
        holder.itemPriceEachTv.setText( ""+price );

    }

    @Override
    public int getItemCount() {
        return orderedItemList.size();
    }

    class HolderOrderedItem extends RecyclerView.ViewHolder{

        private TextView ItemTitleTv,ItemPriceTv,itemPriceEachTv,ItemQty;

        public HolderOrderedItem(@NonNull View itemView) {
            super( itemView );

            ItemTitleTv=itemView.findViewById( R.id.ItemTitleTv );
            ItemPriceTv=itemView.findViewById( R.id.ItemPriceTv );
            itemPriceEachTv=itemView.findViewById( R.id.itemPriceEachTv );
            ItemQty=itemView.findViewById( R.id.ItemQty );
        }
    }
}
