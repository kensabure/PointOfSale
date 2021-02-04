package com.msdevelopers.salepointapp.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.msdevelopers.salepointapp.Filter.FilterProducts;
import com.msdevelopers.salepointapp.Model.ModelProducts;
import com.msdevelopers.salepointapp.R;
import com.msdevelopers.salepointapp.model.Constraints;
import com.msdevelopers.salepointapp.model.ModelShop;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class AdapterProductSeller extends AdapterProductSellers implements Filterable{

    private Context context;
    public ArrayList<ModelProducts>productList,filterList;
    private FilterProducts filter;
    private BottomSheetDialog bottomSheetDialog;
    private EditText textInputPassword, textInputUsername;

    private String Desc;
    private String Price;
    private String category;
    private String Title;
    private String Discount;
    private String Image;
    private String DiscountNote;
    private boolean DiscountAvailable= false;
    private String saveCurrentDate;
    private String saveCurrentTime;

    private ProgressDialog loadingbar;
    private String Class5RandomKey, downloadImageUrl;
    private StorageReference Class5mageRef;
    private DatabaseReference Class5Ref;

    private FirebaseAuth firebaseAuth;

    private Uri imageUri;
    Bitmap selectedImage;
    FragmentManager fm;
    FragmentActivity activity;
    DialogFragment dialog;


    TextView spinner,id;
    Button button;
    Button Take;
    ImageView imageView;
    EditText titleTV,descTV,priceTV,discountTV,discountNoteTV;
    LinearLayout layout;
    SwitchCompat switchCompat;

    private ModelProducts modelProducts;
    private ModelShop modelShop;
    private String string ;

    public AdapterProductSeller(Context context, ArrayList<ModelProducts> productList) {
        this.context = context;
        this.productList = productList;
        this.filterList = productList;
    }

    @NonNull
    @Override
    public HolderProductSeller onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( context ).inflate( R.layout.row_product,parent,false );
        return new HolderProductSeller(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderProductSeller holder, int position) {
        final ModelProducts modelProduct= productList.get( position );
        final ModelProducts modelProduct1= productList.get( position );
        modelShop = new ModelShop();

        String pid = modelProduct.getPid();
        String uid = modelProduct.getUid();
        String discountAvailable = modelProduct.getDiscountAvailable();
        String priceDiscountNote = modelProduct.getPriceDiscountNote();
        String priceDiscount = modelProduct.getPriceDiscount();
        String category = modelProduct.getCategory();
        String description = modelProduct.getDescription();
        String image = modelProduct.getImage();
        String title = modelProduct.getTitle();
        String time = modelProduct.getTime();
        String date = modelProduct.getDate();
        String price = modelProduct.getPrice();



        holder.productName.setText( title );
        holder.DiscountOff.setText( priceDiscountNote );
        holder.DiscountedPrice.setText(  "Ksh."+priceDiscount );
        holder.OriginalPrice.setText(  "Ksh."+price );

        if (discountAvailable.equals( "true" )){
            holder.DiscountedPrice.setVisibility( View.VISIBLE );
            holder.DiscountOff.setVisibility( View.VISIBLE );
            holder.OriginalPrice.setPaintFlags( holder.OriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG );
        }
        else {
            holder.DiscountedPrice.setVisibility( View.GONE );
            holder.DiscountOff.setVisibility( View.GONE );
            holder.OriginalPrice.setPaintFlags( 0 );
        }

        try{
            Picasso.get().load( image ).placeholder( R.drawable.ic_shopping_cart_white_24dp ).into( holder.imageView );

        }catch (Exception e){
            holder.imageView.setImageResource( R.drawable.ic_shopping_cart_white_24dp );
        }
        holder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShowDetails(modelProduct);

            }
        } );
    }



    private void ShowDetails(final ModelProducts modelProduct) {
       bottomSheetDialog= new BottomSheetDialog( context );

        View view= LayoutInflater.from( context ).inflate( R.layout.bs_product_detail,null );

        bottomSheetDialog.setContentView( view );


        ImageButton backBtn= view.findViewById( R.id.backBtn );
        ImageButton deleteBtn= view.findViewById( R.id.deleteBtn );
        ImageButton editBtn= view.findViewById( R.id.editBtn );
        TextView disCountedPrice=view.findViewById( R.id.disCountedPrice );
        TextView originalPrice=view.findViewById( R.id.originalPrice );
        TextView discountNoteTv=view.findViewById( R.id.discountNoteTv );
        TextView titleTv=view.findViewById( R.id.titleTv );
        TextView DescTv=view.findViewById( R.id.DescTv );
        TextView CategoryTv=view.findViewById( R.id.CategoryTv );
        ImageView productIconIv= view.findViewById( R.id.productIconIv );

        final String pid = modelProduct.getPid();
        String uid = modelProduct.getUid();
        final String discountAvailable = modelProduct.getDiscountAvailable();
        String priceDiscountNote = modelProduct.getPriceDiscountNote();
        String priceDiscount = modelProduct.getPriceDiscount();
        final String category = modelProduct.getCategory();
        String description = modelProduct.getDescription();
        String image = modelProduct.getImage();
         final String title = modelProduct.getTitle();
        String time = modelProduct.getTime();
        String date = modelProduct.getDate();
        String price = modelProduct.getPrice();

        titleTv.setText( title );
        DescTv.setText( description );
        CategoryTv.setText( category );
        discountNoteTv.setText( priceDiscountNote );
        originalPrice.setText( ""+price );
        disCountedPrice.setText( ""+priceDiscount );

        if (discountAvailable.equals( "true" )){
            disCountedPrice.setVisibility( View.VISIBLE );
            discountNoteTv.setVisibility( View.VISIBLE );
            originalPrice.setPaintFlags( originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG );
        }
        else {
            disCountedPrice.setVisibility( View.GONE );
            discountNoteTv.setVisibility( View.GONE );

        }
        try{
            Picasso.get().load( image ).placeholder( R.drawable.ic_shopping_cart_white_24dp ).into( productIconIv );

        }catch (Exception e){
           productIconIv.setImageResource( R.drawable.ic_shopping_cart_white_24dp );
        }

        bottomSheetDialog.show();
        editBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheetDialog.dismiss();
                dialog = new DialogFragment();
                activity=(FragmentActivity)(context);
                fm= activity.getSupportFragmentManager();
                activity.setContentView( R.layout.activity_edit_product );


                Intent intent= new Intent( context,fm.beginTransaction().getClass() );
                intent.putExtra( modelProduct.getPid(),modelProduct.getPid() );

                ImageButton close = activity.findViewById( R.id.close_post );
                TextView action = activity.findViewById( R.id.save_post );
                spinner = activity.findViewById( R.id.spinner_post );
                id = activity.findViewById( R.id.pid );
                imageView = activity.findViewById( R.id.select_product_image );
                button = activity.findViewById( R.id.choose_image );
                titleTV = activity.findViewById( R.id.productTitle );
                priceTV = activity.findViewById( R.id.product_price );
                descTV = activity.findViewById( R.id.product_desc );
                discountTV = activity.findViewById( R.id.product_price_discount );
                discountNoteTV = activity.findViewById( R.id.discount_note );
                layout=activity.findViewById( R.id.discount );
                switchCompat=activity.findViewById( R.id.switch_price_discount );


                titleTV.setText( modelProduct.getTitle() );
                priceTV.setText( modelProduct.getPrice() );
                spinner.setText( modelProduct.getCategory() );
                id.setText( modelProduct.getPid() );
                descTV.setText( modelProduct.getDescription() );
                discountTV.setText( modelProduct.getPriceDiscount() );
                discountNoteTV.setText( modelProduct.getPriceDiscountNote() );
                if (modelProduct.getDiscountAvailable().equals( "true" )){
                    switchCompat.setChecked( true );
                    layout.setVisibility( View.VISIBLE );
                }
                else {
                    layout.setVisibility( View.GONE );
                    switchCompat.setChecked( false );
                }

                try{
                    Picasso.get().load( modelProduct.getImage() ).placeholder( R.drawable.ic_insert_photo_black_24dp ).into( imageView );

                }catch (Exception e){
                    imageView.setImageResource( R.drawable.ic_shopping_cart_white_24dp );
                }

                loadingbar = new ProgressDialog( context );
                firebaseAuth= FirebaseAuth.getInstance();


                com.rey.material.widget.FloatingActionButton fab = (com.rey.material.widget.FloatingActionButton) activity.findViewById( R.id.fab );
                fab.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SelectImage();
                    }
                } );

                close.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        activity.recreate();
                    }
                } );

                action.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        validateProductData();
                    }
                } );

                spinner.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CategoryDialog();
                    }
                } );

                switchCompat.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){

                            layout.setVisibility( View.VISIBLE );
                        }
                        else {
                            layout.setVisibility( View.GONE );
                        }
                    }
                } );
                fm.beginTransaction();
            }
        } );

        deleteBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder= new AlertDialog.Builder( context );
                builder.setTitle( "Delete" )
                        .setMessage( "Are you Sure you want to delete product "+title+"?" )
                        .setPositiveButton( "DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteProduct(pid);

                            }
                        } )
                        .setNegativeButton( "NOT NOW", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        } )
                        .show();
            }
        } );

        backBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        } );

    }

    private void SelectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder( context );
        builder.setTitle( "Choose your profile picture" );

        builder.setItems( options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals( "Take Photo" )) {
                    Intent takePicture = new Intent( android.provider.MediaStore.ACTION_IMAGE_CAPTURE );
                    activity.startActivityForResult( takePicture, 0 );

                } else if (options[item].equals( "Choose from Gallery" )) {
                    Intent pickPhoto = new Intent( Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
                    activity.startActivityForResult( pickPhoto, 1 );//one can be replaced with any action code

                } else if (options[item].equals( "Cancel" )) {
                    dialog.dismiss();
                }
            }
        } );
        builder.show();
    }

    private void CategoryDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder= new androidx.appcompat.app.AlertDialog.Builder( context );
        builder.setTitle( "Available Category" )
                .setItems( Constraints.ItemCategories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        category = Constraints.ItemCategories[which];
                        spinner.setText( category );

                    }
                } ).show();
    }


    private void validateProductData() {

        Desc=descTV.getText().toString().trim();
        Price = priceTV.getText().toString().trim();
        string = id.getText().toString().trim();
        category=spinner.getText().toString().trim();
        Title=titleTV.getText().toString().trim();
        DiscountAvailable=switchCompat.isChecked();

        if(TextUtils.isEmpty(category)){
            Toast.makeText(context, "Please Select Category...", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(TextUtils.isEmpty(Title)){
            Toast.makeText(context, "Please Enter Product Name", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(TextUtils.isEmpty(Desc)){
            Toast.makeText(context, "Please Enter Product Description", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(TextUtils.isEmpty( Price )){
            Toast.makeText(context, "Please Enter Product Price", Toast.LENGTH_SHORT).show();
            return;
        }
        if (DiscountAvailable){
            Discount=discountTV.getText().toString().trim();
            DiscountNote=discountNoteTV.getText().toString().trim();
            if(TextUtils.isEmpty( Discount )){
                Toast.makeText(context, "Please Enter Product Discount", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else {
            Discount= "0";
            DiscountNote="";

        }
        updateProductInformation();

    }



    private <Pid> void updateProductInformation() {
        loadingbar.setTitle("Uploading");
        loadingbar.setMessage("Please wait...............");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();


        final String timestamp= ""+System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, YYYY");
        saveCurrentDate = currentDate.format((calendar.getTime()));

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format((calendar.getTime()));

        Class5RandomKey = saveCurrentDate + saveCurrentTime;

        if (imageUri == null) {
            HashMap<String, Object> productMap = new HashMap<>();

            productMap.put("title", ""+Title);
            productMap.put("price", ""+Price);
            productMap.put("pid", ""+string);
            productMap.put("priceDiscount", ""+Discount);
            productMap.put("priceDiscountNote", ""+DiscountNote);
            productMap.put("discountAvailable", ""+DiscountAvailable);
            productMap.put("category", ""+category);
            productMap.put("description", ""+Desc);


            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child( "products" ).child(string ).updateChildren(productMap).addOnSuccessListener( new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    loadingbar.dismiss();
                    Toast.makeText( context, "Updated Successfully", Toast.LENGTH_SHORT ).show();

                }
            } ).addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loadingbar.dismiss();
                    Toast.makeText( context, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();


                }
            } );
        }
        else {

            String filepath="product_image/"+""+timestamp;
            final StorageReference filePath = FirebaseStorage.getInstance().getReference(filepath);
            final UploadTask uploadTask = filePath.putFile(imageUri);

            uploadTask.addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String message  = e.toString();
                    Toast.makeText( context, "Error "+message, Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                }
            } ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> urlTask=uploadTask.continueWithTask( new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful())
                            {
                                throw  task.getException();
                            }
                            downloadImageUrl = filePath.getDownloadUrl().toString();
                            return filePath.getDownloadUrl();
                        }
                    } ).addOnCompleteListener( new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful())
                                downloadImageUrl = task.getResult().toString();
                            HashMap<String, Object> productMap = new HashMap<>();

                            productMap.put("title", ""+Title);
                            productMap.put("image", ""+downloadImageUrl);
                            productMap.put("price", ""+Price);
                            productMap.put("pid", ""+string);
                            productMap.put("priceDiscount", ""+Discount);
                            productMap.put("priceDiscountNote", ""+DiscountNote);
                            productMap.put("discountAvailable", ""+DiscountAvailable);
                            productMap.put("category", ""+category);
                            productMap.put("description", ""+Desc);


                            DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
                            reference.child(firebaseAuth.getUid()).child( "products" ).child( string ).updateChildren(productMap).addOnSuccessListener( new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    loadingbar.dismiss();
                                    Toast.makeText( context, "Added Successfully", Toast.LENGTH_SHORT ).show();

                                }
                            } ).addOnFailureListener( new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loadingbar.dismiss();
                                    Toast.makeText( context, "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();


                                }
                            } );
                        }
                    } );

                }
            } );

        }

    }

    private void deleteProduct(String pid) {
        FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.child( firebaseAuth.getUid() ).child( "products" ).child( pid ).removeValue()
                .addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText( context, "Product Deleted ", Toast.LENGTH_SHORT ).show();
                        bottomSheetDialog.dismiss();

                    }
                } )
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText( context, "Failed to Delete! Try Again"+e.getMessage(), Toast.LENGTH_SHORT ).show();
                    }
                } );
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter==null){
            filter= new FilterProducts( this,filterList );
        }
        return filter;
    }



    class HolderProductSeller extends RecyclerView.ViewHolder{

        private ImageView imageView,nextImg;
        private TextView productName,DiscountOff,DiscountedPrice,OriginalPrice;
        public HolderProductSeller(@NonNull View itemView) {
            super( itemView );

            imageView=itemView.findViewById( R.id.productIconIv );
            nextImg=itemView.findViewById( R.id.nextId );
            productName=itemView.findViewById( R.id.ProductName );
            DiscountOff=itemView.findViewById( R.id.discountOff );
            DiscountedPrice=itemView.findViewById( R.id.disCountedPrice );
            OriginalPrice=itemView.findViewById( R.id.originalPrice );
        }
    }
}
