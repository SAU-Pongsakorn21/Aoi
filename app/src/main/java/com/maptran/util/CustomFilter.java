package com.maptran.util;

import android.widget.Filter;

import java.util.ArrayList;

/**
 * Created by KorPai on 19/4/2560.
 */

public class CustomFilter extends Filter {

    MyAdapter adapter;
    ArrayList<Place> filterList;

    public CustomFilter(ArrayList<Place> filterList, MyAdapter adapter)
    {
        this.adapter = adapter;
        this.filterList = filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint)
    {
        FilterResults results = new FilterResults();

        if(constraint != null && constraint.length() > 0)
        {
            constraint = constraint.toString().toUpperCase();

            ArrayList<Place> filteredPlace = new ArrayList<>();

            for(int i=0;i<filterList.size();i++)
            {
                if(filterList.get(i).getPlace_name().toUpperCase().contains(constraint))
                {
                    filteredPlace.add(filterList.get(i));
                }
            }

            results.count = filteredPlace.size();
            results.values = filteredPlace;
        }
        else
        {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results)
    {
        adapter.places = (ArrayList<Place>) results.values;

        adapter.notifyDataSetChanged();
    }
}
