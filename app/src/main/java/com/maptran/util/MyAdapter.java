package com.maptran.util;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.maptran.EditedActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import sau.comsci.com.aoi.R;
import sau.comsci.com.aoi.SharedPrefManager;
import sau.comsci.com.aoi.ShowExtraDetail;

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
    public void onBindViewHolder(final MyHolder holder, final int position)
    {
        holder.txt_name_place.setText(places.get(position).getPlace_name());
        holder.txt_detail_place.setText(places.get(position).getPlace_detail());
        String url = places.get(position).getPlace_image();

        final String user = places.get(position).getPlace_user();
        /*if(url.contains("["))
        {
            url = url.replace("[","");
        }

        if(url.contains("]"))
        {
            url = url.replace("]","");
        }*/

        Picasso.with(mCtx).load("http://argeosau.xyz/"+url).placeholder(R.drawable.loading).into(holder.img_place);
        Log.d("position","http://argeosau.xyz/"+url);

        /*ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(mCtx).build();
        ImageLoader.getInstance().init(imageLoaderConfiguration);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage("http://argeosau.xyz/"+url, holder.img_place);*/
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Intent intent = new Intent(v.getContext(), ShowExtraDetail.class);
                intent.putExtra("name_place",places.get(pos).getPlace_name());
                intent.putExtra("name_detail",places.get(pos).getPlace_detail());
                intent.putExtra("name_photo",places.get(pos).getPlace_image());
                intent.putExtra("page","page01");
                v.getContext().startActivity(intent);
            }
        });

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(holder.overflow,user,position);
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



    private void showPopupMenu(View view,String user,int position)
    {

        PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_cardview,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new MyMenuItemClickListener(view.getContext(),user,position));
        popupMenu.show();

    }
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        Context mContext;
        String user;
        int position;
        public MyMenuItemClickListener(Context context,String user,int position) {

            this.mContext = context;
            this.user = user;
            this.position = position;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_edit:
                    if(user.equals(SharedPrefManager.getInstance(mCtx.getApplicationContext()).getUsername()))
                    {
                        Intent intent = new Intent(mCtx.getApplicationContext(), EditedActivity.class);
                        intent.putExtra("title",places.get(position).getPlace_name());
                        intent.putExtra("detail",places.get(position).getPlace_detail());
                        intent.putExtra("photo",places.get(position).getPlace_image());
                        intent.putExtra("type",places.get(position).getPlace_type());
                        intent.putExtra("id",places.get(position).getId());
                        mContext.startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(mContext.getApplicationContext(),"คุณไม่สามารถแก้ไขได้...คุณไม่ใช่เจ้าของสถานที่นี้",Toast.LENGTH_SHORT).show();
                    }
                    return true;
                default:
            }
            return false;
        }
    }
}
