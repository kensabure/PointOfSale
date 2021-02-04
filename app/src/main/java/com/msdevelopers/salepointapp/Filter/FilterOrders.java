package com.msdevelopers.salepointapp.Filter;

import android.widget.Filter;


import com.msdevelopers.salepointapp.Adapter.AdapterOrderShop;
import com.msdevelopers.salepointapp.model.ModelOrderUser;

import java.util.ArrayList;

public class FilterOrders extends Filter {

    private AdapterOrderShop adapter;
    private ArrayList<ModelOrderUser> filterList;

    public FilterOrders(AdapterOrderShop adapter, ArrayList<ModelOrderUser> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results= new FilterResults();
        if (constraint != null && constraint.length()>0 ){
            constraint= constraint.toString().toUpperCase();

            ArrayList<ModelOrderUser> filteredModels= new ArrayList<>(  );
            for (int i=0; i<filterList.size(); i++){
                if (filterList.get( i ).getOrderStatus().toUpperCase().contains( constraint )){
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

        adapter.modelOrderUseList=(ArrayList<ModelOrderUser>)results.values;
        adapter.notifyDataSetChanged();
    }
}


