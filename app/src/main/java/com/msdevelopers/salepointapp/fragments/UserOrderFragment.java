package com.msdevelopers.salepointapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.msdevelopers.salepointapp.Adapter.AdapterOrderUser;
import com.msdevelopers.salepointapp.Adapter.AdapterShop;
import com.msdevelopers.salepointapp.R;
import com.msdevelopers.salepointapp.model.ModelOrderUser;
import com.msdevelopers.salepointapp.model.ModelShop;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserOrderFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private TextView NameTv,email_address,phone_no,tab_shops,tab_orders;
    private ImageButton ProfileEdit,logout;
    private RelativeLayout shopRel,ordersRel;
    private CircleImageView user_profile;
    private RecyclerView shopRecyclerView,OrderRecyclerView;

    private ArrayList<ModelShop> shopList;
    private AdapterShop adapterShop;

    private ArrayList<ModelOrderUser> orderUserList;
    private AdapterOrderUser adapterOrderUser;

    View view;
    public UserOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_order, container, false);

        OrderRecyclerView=view.findViewById( R.id.OrderRecyclerView );

        firebaseAuth=FirebaseAuth.getInstance();

        loadOrders();

        return view;
    }

    private void loadOrders() {
        orderUserList= new ArrayList<>(  );

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderUserList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    String uid= ""+ds.getRef().getKey();

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(uid).child( "Orders" );
                    reference.orderByChild( "OrderBy" ).equalTo( firebaseAuth.getUid() )
                            .addValueEventListener( new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()){
                                        for (DataSnapshot ds: snapshot.getChildren()){
                                            ModelOrderUser modelOrderUser = ds.getValue(ModelOrderUser.class);
                                            orderUserList.add( modelOrderUser );

                                        }
                                        adapterOrderUser = new AdapterOrderUser( getContext(),orderUserList );
                                        OrderRecyclerView.setAdapter( adapterOrderUser );
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            } );




                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        } );
    }
}