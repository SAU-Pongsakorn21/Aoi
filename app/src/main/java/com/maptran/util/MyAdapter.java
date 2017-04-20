package com.maptran.util;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import sau.comsci.com.aoi.R;

/**
 * Created by KorPai on 19/4/2560.
 */

public class MyAdapter extends RecyclerView.Adapter<MyHolder> implements Filterable {

    Context mCtx;

    ArrayList<Place> places,filterList;
    CustomFilter filter;


    public MyAdapter(Context context, ArrayList<Place> places)
    {
        this.mCtx = context;
        this.places = places;
        this.filterList = places;
    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_main,null);

        MyHolder holder = new MyHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position)
    {
        holder.txt_name_place.setText(places.get(position).getPlace_name());
        holder.txt_detail_place.setText(places.get(position).getPlace_detail());

        String url = places.get(position).getPlace_image();

        /*if(url.contains("["))
        {
            url = url.replace("[","");
        }

        if(url.contains("]"))
        {
            url = url.replace("]","");
        }*/

        Picasso.with(mCtx).load("http://argeosau.xyz/"+url).placeholder(R.drawable.loading).into(holder.img_place);
        Log.d("position",url);
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return places.size();
    }

    @Override
    public Filter getFilter()
    {
        if(filter == null)
        {
            filter = new CustomFilter(filterList,this);
        }
        return filter;
    }

}
