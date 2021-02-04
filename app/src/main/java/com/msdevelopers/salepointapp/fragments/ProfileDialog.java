package com.msdevelopers.salepointapp.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.msdevelopers.salepointapp.R;
import com.msdevelopers.salepointapp.activities.SignInActivity;
import com.rey.material.widget.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileDialog extends DialogFragment implements View.OnClickListener {

    private Callback callback;
    private String Desc, price, category, saveCurrentDate, saveCurrentTime;

    private static final int galleryPick = 1;
    private final int PICK_IMAGE_REQUEST = 22;
    private final int CAMERA_REQUEST_CODE = 200;
    private final int STORAGE_REQUEST_CODE = 300;
    private final int IMAGE_PICK_GALLERY_CODE = 400;
    private final int IMAGE_PICK_CAMERA_CODE = 500;


    private ImageView imageView;

    // Uri indicates, where the image will be picked from
    private Uri image_uri;
    private Uri filePath;

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


    private ProgressDialog loadingbar;
    private TextView textView, textView1, textView2, textView3, textView4, Email, Date;
    private Button action, btn_Cancel;
    private ImageButton close;
    private ImageView ProfileImage;
    private LinearLayout layout;
    private FloatingActionButton btnSelect;
    private SwitchCompat switchCompat;


    public static ProfileDialog newInstance() {
        return new ProfileDialog();
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
        View view = inflater.inflate( R.layout.activity_profile, container, false );

        close = view.findViewById( R.id.close_post );
        action = view.findViewById( R.id.save_profile );
        btn_Cancel = view.findViewById( R.id.profile_cancel );
        imageView = view.findViewById( R.id.profile_image );
        btnSelect = view.findViewById( R.id.change_profile );
        textView1 = view.findViewById( R.id.profile_name );
        textView2 = view.findViewById( R.id.profile_phone );
        switchCompat = view.findViewById( R.id.shopOpenSwitch );
        Email = view.findViewById( R.id.email );
        Date = view.findViewById( R.id.date );
        layout = view.findViewById( R.id.linear_profile );
        loadingbar = new ProgressDialog( getContext() );
        firebaseAuth = FirebaseAuth.getInstance();

        checkUser();

        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference( "images" );
        databaseReference = FirebaseDatabase.getInstance().getReference( "Users" ).child( firebaseAuth.getUid() );

        // on pressing btnSelect SelectImage() is called
        btnSelect.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.setVisibility( View.VISIBLE );
                SelectImage();
            }
        } );
        btn_Cancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        } );

        getUserInfo();
        close.setOnClickListener( this );
        action.setOnClickListener( this );

        return view;
    }

    private void SelectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..."),
                PICK_IMAGE_REQUEST);
    }

    private void checkUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            startActivity( new Intent( getContext(), SignInActivity.class ) );

        } else {
            getUserInfo();
        }
    }

    private void pickFromGallery() {

        Intent intent = new Intent();
        intent.setType( "image/*" );
        intent.setAction( Intent.ACTION_GET_CONTENT );
        startActivityForResult(
                Intent.createChooser(
                        intent,
                        "Select Image from here..." ),
                IMAGE_PICK_GALLERY_CODE );
    }

    private void pickFromCamera() {

        ContentValues contentValues = new ContentValues();
        contentValues.put( MediaStore.Images.Media.TITLE, "Image Title" );
        contentValues.put( MediaStore.Images.Media.DESCRIPTION, "Image Description" );

        image_uri = getContext().getContentResolver().insert( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues );

        Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
        intent.putExtra( MediaStore.EXTRA_OUTPUT, image_uri );
        startActivityForResult( intent, IMAGE_PICK_CAMERA_CODE );
    }

    private void getUserInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference( "Users" );
        ref.orderByChild( "uid" ).equalTo( firebaseAuth.getUid() )
                .addValueEventListener( new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String name = "" + ds.child( "name" ).getValue();
                            String email = "" + ds.child( "email" ).getValue();
                            String phone = "" + ds.child( "phone" ).getValue();
                            String ProfileImage = "" + ds.child( "ProfileImage" ).getValue();
                            String online = "" + ds.child( "online" ).getValue();

                            textView1.setText( name );
                            textView2.setText( phone );
                            Email.setText( email );

                            if (online.equals( "true" )) {
                                switchCompat.setChecked( true );
                            } else {
                                switchCompat.setChecked( false );
                            }

                            try {
                                Picasso.get().load( ProfileImage ).placeholder( R.drawable.ic_account_circle_black_24dp ).into( imageView );
                                {

                                }
                            } catch (Exception e) {
                                imageView.setImageResource( R.drawable.ic_account_circle_black_24dp );
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                } );
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.close_post:
                dismiss();
                break;

            case R.id.save_profile:
                uploadImage();
                layout.setVisibility( View.GONE );
                break;

        }

    }

    private void uploadImage() {
        if (filePath != null) {

            // Code for showing progressDialog while uploading
            final ProgressDialog progressDialog
                    = new ProgressDialog( getContext() );
            progressDialog.setTitle( "Uploading..." );
            progressDialog.show();

            // Defining the child of storageReference
            final StorageReference ref
                    = storageReference
                    .child( firebaseAuth.getUid() + ".jpg" );
            uploadTask = ref.putFile( filePath );
            uploadTask.continueWithTask( new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return ref.getDownloadUrl();
                }
            } ).addOnCompleteListener( new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()) {
                        Uri downLoadUri = task.getResult();
                        myUri = downLoadUri.toString();
                        Map<String, Object> hashMap = new HashMap<>();
                        hashMap.put( "ProfileImage", myUri );
                        databaseReference.updateChildren( hashMap );
                        Toast.makeText( getActivity(), "Profile Updated Successfully", Toast.LENGTH_SHORT ).show();
                        progressDialog.dismiss();

                    } else {
                        progressDialog.dismiss();
                        Toast.makeText( getActivity(), "Error Occured", Toast.LENGTH_SHORT ).show();
                    }

                }
            } );
            // adding listeners on upload
            // or failure of image
            ref.putFile( filePath )
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast
                                            .makeText( getActivity(),
                                                    "Image Uploaded!!",
                                                    Toast.LENGTH_SHORT )
                                            .show();
                                }
                            } )

                    .addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast
                                    .makeText( getActivity(),
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT )
                                    .show();
                        }
                    } )
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int) progress + "%" );
                                }
                            } );
        }

    }

    public interface Callback {

        void onActionClick(String name);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case CAMERA_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText( getContext(), "Allow Camera Permission", Toast.LENGTH_SHORT ).show();
                    }
                }
            }

            case STORAGE_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText( getContext(), "Allow Storage Permission", Toast.LENGTH_SHORT ).show();
                    }
                }
            }
        }
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
    }

    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 Intent data) {

        super.onActivityResult( requestCode,
                resultCode,
                data );

        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getActivity().getContentResolver(),
                                filePath );
                imageView.setImageBitmap( bitmap );

            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }
}