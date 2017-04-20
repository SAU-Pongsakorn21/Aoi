package com.maptran.util;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import sau.comsci.com.aoi.R;

/**
 * Created by KorPai on 19/4/2560.
 */

public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

    ImageView img_place;
    TextView txt_name_place,txt_detail_place;

    ItemClickListener itemClickListener;

    public MyHolder(View itemView)
    {
        super(itemView);

        this.img_place = (ImageView) itemView.findViewById(R.id.img_cardview_thumbnail);
        this.txt_name_place = (TextView) itemView.findViewById(R.id.txt_card_title);
        this.txt_detail_place = (TextView) itemView.findViewById(R.id.txt_card_detail);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        this.itemClickListener.onItemClick(view,getLayoutPosition());
    }

    public void setItemClickListener(ItemClickListener ic)
    {
        this.itemClickListener=ic;
    }
}
