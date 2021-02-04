package com.msdevelopers.salepointapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.msdevelopers.salepointapp.Filter.FilterProductsUser;
import com.msdevelopers.salepointapp.Model.ModelProducts;
import com.msdevelopers.salepointapp.R;
import com.msdevelopers.salepointapp.activities.ShopDetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import p32929.androideasysql_library.Column;
import p32929.androideasysql_library.EasyDB;

public class AdapterProductUser extends RecyclerView.Adapter<AdapterProductUser.HolderProductUse> implements Filterable {

    public ArrayList<ModelProducts> productList,filterList;
    private Context context;
    private FilterProductsUser filter;

    public AdapterProductUser(Context context, ArrayList<ModelProducts> productsList) {
        this.context = context;
        this.productList = productsList;
        this.filterList = productsList;
    }

    @NonNull
    @Override
    public HolderProductUse onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from( context ).inflate( R.layout.row_product_user,parent,false );
        return new HolderProductUse( view );
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductUse holder, int position) {
        final ModelProducts modelProducts= productList.get( position );

        String pid = modelProducts.getPid();
        String uid = modelProducts.getUid();
        String discountAvailable = modelProducts.getDiscountAvailable();
        String priceDiscountNote = modelProducts.getPriceDiscountNote();
        String priceDiscount = modelProducts.getPriceDiscount();
        String category = modelProducts.getCategory();
        String description = modelProducts.getDescription();
        String image = modelProducts.getImage();
        String title = modelProducts.getTitle();
        String time = modelProducts.getTime();
        String date = modelProducts.getDate();
        String price = modelProducts.getPrice();

        holder. ProductName.setText( title );
        holder. ProductDes.setText( description );
        holder. discountOff.setText( priceDiscountNote );
        holder. discountOff.setText( priceDiscountNote );
        holder. disCountedPrice.setText( priceDiscount );
        holder. originalPrice.setText( price );

        if (discountAvailable.equals( "true" )){
            holder.disCountedPrice.setVisibility( View.VISIBLE );
            holder.discountOff.setVisibility( View.VISIBLE );
            holder.originalPrice.setPaintFlags( holder.originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG );
        }
        else {
            holder.disCountedPrice.setVisibility( View.GONE );
            holder.discountOff.setVisibility( View.GONE );
            holder.originalPrice.setPaintFlags( 0 );
        }

        try{
            Picasso.get().load( image ).placeholder( R.drawable.ic_shopping_cart_white_24dp ).into( holder.productIconIv );

        }catch (Exception e){
            holder.productIconIv.setImageResource( R.drawable.ic_shopping_cart_white_24dp );
        }

        holder.AddToCart.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCartDialog(modelProducts);
            }
        } );
        holder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        } );

    }

    private double cost=0;
    private double Total=0;
    private int Quantity=0;
    private void showCartDialog(ModelProducts modelProducts) {
        View view= LayoutInflater.from( context ).inflate( R.layout.dialog_quantity,null );

        CircleImageView productIconIv=view.findViewById( R.id.productIconIv );
        TextView discountNote=view.findViewById( R.id.discountNote );
        final TextView titleTv=view.findViewById( R.id.titleTv );
        TextView descTv=view.findViewById( R.id.descTv );
        TextView disCountedPrice=view.findViewById( R.id.disCountedPrice );
        final TextView originalPrice=view.findViewById( R.id.originalPrice );
        final TextView counter=view.findViewById( R.id.counter );
        ImageView less=view.findViewById( R.id.less );
        ImageView increase=view.findViewById( R.id.increase );
        final TextView total=view.findViewById( R.id.total );
        Button continueBtn=view.findViewById( R.id.continueBtn );


        final String uid = modelProducts.getUid();
        String priceDiscountNote = modelProducts.getPriceDiscountNote();
        String priceDiscounted = modelProducts.getPriceDiscount();
        String description = modelProducts.getDescription();
        String discountAvailable = modelProducts.getDiscountAvailable();
        String image = modelProducts.getImage();
        String title = modelProducts.getTitle();
        String Image = modelProducts.getImage();
        String Price = modelProducts.getPrice();


        final String price;
        if (modelProducts.getDiscountAvailable().equals( "true" )){
            price= modelProducts.getPriceDiscount();
            discountNote.setVisibility( View.VISIBLE );
            originalPrice.setPaintFlags( originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG );
        }else {
            discountNote.setVisibility( View.GONE );
            disCountedPrice.setVisibility( View.GONE );
            price=modelProducts.getPrice();
        }

        cost= Double.parseDouble( price.replaceAll( "","" ) );
        Total= Double.parseDouble( price.replaceAll( "","" ) );
        Quantity=1;

        AlertDialog.Builder builder= new AlertDialog.Builder( context );
        builder.setView( view );

        try{
            Picasso.get().load(Image).placeholder( R.drawable.ic_shopping_cart_white_24dp ).into( productIconIv );
        }catch (Exception e){
            productIconIv.setImageResource( R.drawable.ic_shopping_cart_white_24dp );
        }

        titleTv.setText( ""+title );
        descTv.setText( ""+description );
        disCountedPrice.setText( ""+priceDiscounted );
        discountNote.setText( ""+priceDiscountNote );
        originalPrice.setText( ""+Price );
        counter.setText( ""+Quantity );
        total.setText( ""+Total );


        final AlertDialog dialog= builder.create();
        dialog.show();

        increase.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Total = Total + cost;
                Quantity++;
                total.setText( ""+Total );
                counter.setText( ""+Quantity );
            }
        } );

        less.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Quantity>1){
                    Total = Total - cost;
                    Quantity--;
                    total.setText( ""+Total );
                    counter.setText( ""+Quantity );
                }

            }
        } );

        continueBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title= titleTv.getText().toString().trim();
                String priceEach= price;
                String price= total.getText().toString().trim().replace( "","" );
                String quantity= counter.getText().toString().trim();
                
                AddToCart(uid,title,priceEach,price,quantity);

                dialog.dismiss();

            }
        } );

    }

    private int itemId = 1;
    private void AddToCart(String uid, String title, String priceEach, String price, String quantity) {
        itemId++;

        EasyDB easiestDB = EasyDB.init(context,"ITEMS_DB")
                .setTableName("ITEMS_TABLE")
                .addColumn(new Column("Item_Id",new String[]{"text","unique"}))
                .addColumn(new Column("Item_PID",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Name",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price_Each",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Price",new String[]{"text","not null"}))
                .addColumn(new Column("Item_Quantity",new String[]{"text","not null"}))
                .doneTableColumn();

        Boolean b = easiestDB
                .addData(  "Item_Id",itemId )
                .addData(  "Item_PID",uid )
                .addData(  "Item_Name",title )
                .addData(  "Item_Price_Each",priceEach )
                .addData(  "Item_Price",price )
                .addData(  "Item_Quantity",quantity )
                .doneDataAdding();

        Toast.makeText( context, "Added to Cart...", Toast.LENGTH_SHORT ).show();
        ((ShopDetailsActivity)context).cartCounter();
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter==null){
            filter= new FilterProductsUser( this,filterList );
        }
        return filter;
    }

    class HolderProductUse extends RecyclerView.ViewHolder{

        private ImageView productIconIv,nextId;
        private TextView discountOff,ProductName,ProductDes
                ,disCountedPrice,originalPrice;
        private Button AddToCart;

        public HolderProductUse(@NonNull View itemView) {
            super( itemView );
            productIconIv=itemView.findViewById( R.id.productIconIv );
            nextId=itemView.findViewById( R.id.nextId );
            ProductName=itemView.findViewById( R.id.ProductName );
            discountOff=itemView.findViewById( R.id.discountOff );
            ProductDes=itemView.findViewById( R.id.ProductDes );
            AddToCart=itemView.findViewById( R.id.AddToCart );
            disCountedPrice=itemView.findViewById( R.id.disCountedPrice );
            originalPrice=itemView.findViewById( R.id.originalPrice );


        }
    }
}
