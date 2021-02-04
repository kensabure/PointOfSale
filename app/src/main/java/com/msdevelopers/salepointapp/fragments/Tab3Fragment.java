package com.msdevelopers.salepointapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.msdevelopers.salepointapp.R;
import com.msdevelopers.salepointapp.model.ModelOrderUser;


import java.util.ArrayList;

/**
 * Created by User on 2/28/2017.
 */

public class Tab3Fragment extends Fragment {
    private static final String TAG = "Tab3Fragment";

    private FirebaseAuth firebaseAuth;
    private RecyclerView OrderRecyclerView;
    private ArrayList<ModelOrderUser> orderUserList;
    private int currentList = 0;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.tab3_fragment,container,false);

        OrderRecyclerView= view.findViewById( R.id.OrderRecyclerView );
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefresh);
        firebaseAuth= FirebaseAuth.getInstance();

        loadOrders();

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(currentList == 2){
                    currentList = -1 ;
                }
                currentList = currentList + 1;

            }
        });


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
