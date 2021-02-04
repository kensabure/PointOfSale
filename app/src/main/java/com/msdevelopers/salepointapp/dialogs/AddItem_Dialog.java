package com.msdevelopers.salepointapp.dialogs;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.msdevelopers.salepointapp.R;
import com.msdevelopers.salepointapp.model.Constraints;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class AddItem_Dialog extends DialogFragment implements View.OnClickListener {

    private static final int RESULT_CANCELED =1 ;
    private Callback callback;
    private String Desc;
    private String Price;
    private String category;
    private String Title;
    private String Discount;
    private String DiscountNote;
    private boolean DiscountAvailable= false;
    private String saveCurrentDate;
    private String saveCurrentTime;

    private static final int galleryPick = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    private final int CAMERA_REQUEST_CODE = 200;
    private final int STORAGE_REQUEST_CODE = 300;
    private final int IMAGE_PICK_GALLERY_CODE = 400;
    private final int IMAGE_PICK_CAMERA_CODE = 500;

    private static final String IMAGE_DIRECTORY = "/e-ordering";
    private int GALLERY = 1, CAMERA = 2;

    private String myUri = "";
    private StorageTask uploadTask;
    private FirebaseAuth mAuth;

    // request code
    // instance for firebase storage and StorageReference
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private String[] cameraPermission;
    private String[] storagePermission;


    private Uri imageUri;
    private Bitmap bitmap;
    Bitmap selectedImage;

    private ProgressDialog loadingbar;
    private String  downloadImageUrl;
    private StorageReference Class5mageRef;
    private DatabaseReference Class5Ref;

    TextView spinner;
    Button button;
    Button Take;
    ImageView imageView;
    EditText title,desc,price,discount,discountNote;
    LinearLayout layout;
    SwitchCompat switchCompat;


    public static AddItem_Dialog newInstance() {
        return new AddItem_Dialog();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setStyle( DialogFragment.STYLE_NORMAL, R.style.FullscreenDialogTheme );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate( R.layout.activity_post__item_layout, container, false );
        ImageButton close = view.findViewById( R.id.close_post );
        TextView action = view.findViewById( R.id.save_post );
        spinner = view.findViewById( R.id.spinner_post );
        imageView = view.findViewById( R.id.select_product_image );
        button = view.findViewById( R.id.choose_image );
        title = view.findViewById( R.id.productTitle );
        price = view.findViewById( R.id.product_price );
        desc = view.findViewById( R.id.product_desc );
        discount = view.findViewById( R.id.product_price_discount );
        discountNote = view.findViewById( R.id.discount_note );
        layout=view.findViewById( R.id.discount );
        switchCompat=view.findViewById( R.id.switch_price_discount );

        requestMultiplePermissions();
        loadingbar = new ProgressDialog( getContext() );
        firebaseAuth=FirebaseAuth.getInstance();

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        Class5mageRef = FirebaseStorage.getInstance().getReference().child( "Items_Images" );
        
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


        com.rey.material.widget.FloatingActionButton fab = (com.rey.material.widget.FloatingActionButton) view.findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();

            }
        } );


        close.setOnClickListener( this );
        action.setOnClickListener( this );

        return view;
    }



    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getContext());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }
    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }

        if (requestCode == GALLERY && resultCode == RESULT_OK && data!=null ){
            imageUri = data.getData();
            imageView.setImageURI(imageUri);

        }
        else if (requestCode == CAMERA) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
            Toast.makeText(getActivity(), "Image Saved!", Toast.LENGTH_SHORT).show();
        }
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(getActivity(),
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    private void  requestMultiplePermissions(){
        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getActivity(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void CategoryDialog() {
        AlertDialog.Builder builder= new AlertDialog.Builder( getContext() );
        builder.setTitle( "Available Category" )
                .setItems( Constraints.ItemCategories, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        category = Constraints.ItemCategories[which];
                        spinner.setText( category );

                    }
                } ).show();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {

            case R.id.close_post:
                dismiss();
                break;

            case R.id.save_post:
                validateProductData();
                break;

        }

    }

    public interface Callback {

        void onActionClick(String name);

    }

    private void validateProductData() {

        Desc=desc.getText().toString().trim();
        Price = price.getText().toString().trim();
        category=spinner.getText().toString().trim();
        Title=title.getText().toString().trim();
        DiscountAvailable=switchCompat.isChecked();

        if(TextUtils.isEmpty(category)){
            Toast.makeText(getContext(), "Please Select Category...", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(TextUtils.isEmpty(Title)){
            Toast.makeText(getContext(), "Please Enter Product Name", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(TextUtils.isEmpty(Desc)){
            Toast.makeText(getContext(), "Please Enter Product Description", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(TextUtils.isEmpty( Price )){
            Toast.makeText(getContext(), "Please Enter Product Price", Toast.LENGTH_SHORT).show();
            return;
        }
        if (DiscountAvailable){
            Discount=discount.getText().toString().trim();
            DiscountNote=discountNote.getText().toString().trim();
            if(TextUtils.isEmpty( Discount )){
                Toast.makeText(getContext(), "Please Enter Product Discount", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else {
            Discount= "0";
            DiscountNote="";

        }
            storeProductInformation();

    }

    private void storeProductInformation() {
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


        if (imageUri == null) {
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("pid", ""+timestamp);
            productMap.put("date", ""+saveCurrentDate);
            productMap.put("time", ""+saveCurrentTime);
            productMap.put("title", ""+Title);
            productMap.put("image", "");
            productMap.put("price", ""+Price);
            productMap.put("priceDiscount", ""+Discount);
            productMap.put("priceDiscountNote", ""+DiscountNote);
            productMap.put("discountAvailable", ""+DiscountAvailable);
            productMap.put("category", ""+category);
            productMap.put("description", ""+Desc);
            productMap.put("uid", ""+firebaseAuth.getUid());

            DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
            reference.child(firebaseAuth.getUid()).child( "products" ).child( timestamp ).setValue(productMap).addOnSuccessListener( new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    loadingbar.dismiss();
                    Toast.makeText( getContext(), "Added Successfully", Toast.LENGTH_SHORT ).show();
                    clearData();

                }
            } ).addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    loadingbar.dismiss();
                    Toast.makeText( getActivity(), "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    dismiss();

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
                    Toast.makeText( getActivity(), "Error "+message, Toast.LENGTH_SHORT).show();
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
                            final HashMap<String, Object> productMap = new HashMap<>();
                            productMap.put("pid", ""+timestamp);
                            productMap.put("date", ""+saveCurrentDate);
                            productMap.put("time", ""+saveCurrentTime);
                            productMap.put("title", ""+Title);
                            productMap.put("image", ""+downloadImageUrl);
                            productMap.put("price", ""+Price);
                            productMap.put("priceDiscount", ""+Discount);
                            productMap.put("priceDiscountNote", ""+DiscountNote);
                            productMap.put("discountAvailable", ""+DiscountAvailable);
                            productMap.put("category", ""+category);
                            productMap.put("description", ""+Desc);
                            productMap.put("uid", ""+firebaseAuth.getUid());

                            final DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
                            reference.child(firebaseAuth.getUid()).child( "products" ).child( timestamp ).setValue(productMap).addOnSuccessListener( new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    loadingbar.dismiss();
                                    Toast.makeText( getContext(), "Added Successfully", Toast.LENGTH_SHORT ).show();
                                    clearData();


                                }
                            } ).addOnFailureListener( new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loadingbar.dismiss();
                                    Toast.makeText( getActivity(), "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    dismiss();

                                }
                            } );
                        }
                    } );

                }
            } );

        }

    }

    private void clearData() {

        title.setText( "" );
        spinner.setText( "" );
        desc.setText( "" );
        price.setText( "" );
        discount.setText( "" );
        discountNote.setText( "" );
        bitmap= null;
    }

}