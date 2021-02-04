package com.msdevelopers.salepointapp.Filter;

import android.widget.Filter;


import com.msdevelopers.salepointapp.Adapter.AdapterProductUser;
import com.msdevelopers.salepointapp.Model.ModelProducts;

import java.util.ArrayList;

public class FilterProductsUser extends Filter {

    private AdapterProductUser adapter;
    private ArrayList<ModelProducts> filterList;

    public FilterProductsUser(AdapterProductUser adapter, ArrayList<ModelProducts> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results= new FilterResults();
        if (constraint != null && constraint.length()>0 ){
            constraint= constraint.toString().toUpperCase();

            ArrayList<ModelProducts> filteredModels= new ArrayList<>(  );
            for (int i=0; i<filterList.size(); i++){
                if (filterList.get( i ).getTitle().toUpperCase().contains( constraint )||
                        filterList.get( i ).getCategory() .toUpperCase().contains( constraint )){
                    filteredModels.add( filterList.get( i ) );

                }
            }
            results.count = filteredModels.size();
            results.values = filteredModels;
        }
        else{
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        adapter.productList=(ArrayList<ModelProducts>)results.values;
        adapter.notifyDataSetChanged();
    }
}


