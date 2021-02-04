 package com.msdevelopers.salepointapp.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.msdevelopers.salepointapp.R;

import java.util.List;


public class FavouritesFragment extends Fragment {

    private RecyclerView recyclerView;
    //adapter

    //database Ref
    private DatabaseReference databaseReference;
    //progress bar
    ProgressBar progressBar;
    //Query
    Query query;

    private View view;


    public FavouritesFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate( R.layout.fragment_favourites, container, false );
        this.getActivity().getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );
        recyclerView = view.findViewById( R.id.recyclerView );


        return view;

    }
}