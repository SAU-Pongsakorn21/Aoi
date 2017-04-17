package sau.comsci.com.aoi.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Random;

import sau.comsci.com.aoi.R;
import sau.comsci.com.aoi.ShowExtraDetail;


/**
 * Created by KorPai on 28/3/2560.
 */

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.MyViewHolder> {

    private Context mContext;
    private List<EndangeredItem> endangeredItems;
    private MyClickListener mCallback;


    public GridAdapter(Context mContext, List<EndangeredItem> endangeredItems)
    {
        this.mContext = mContext;
        this.endangeredItems = endangeredItems;
    }

    @Override
    public GridAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_main,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final GridAdapter.MyViewHolder viewHolder, int position)
    {
        setColorViewHolder(viewHolder,position);

        viewHolder.title.setText(endangeredItems.get(position).getTitle());
        viewHolder.detail.setText(endangeredItems.get(position).getDetail());

        String url = endangeredItems.get(position).getThumbnail();
        Glide.with(mContext).load(url.replace("http:","http://"))
                .placeholder(R.drawable.loading).into(viewHolder.thumbnail);

        viewHolder.overflow.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showPopupMenu(viewHolder.overflow);
            }
        });
    }

    private void setColorViewHolder(final GridAdapter.MyViewHolder viewHolder, int position)
    {
        Random rn = new Random();
        int n = position+1;
        int i = rn.nextInt() %n;
        int rdm = position - i;

        if(rdm == 0)
        {
            viewHolder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorCyan800));
        }
        else if(rdm == 1)
        {
            viewHolder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorGreen800));
        }
        else if(rdm== 3)
        {
            viewHolder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorAmber800));
        }
        else if(rdm== 4)
        {
            viewHolder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorOrange800));
        }
        else if(rdm== 5)
        {
            viewHolder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPink800));
        }
        else if(rdm== 6)
        {
            viewHolder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorRed800));
        }
        else if(rdm== 7)
        {
            viewHolder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorIndigo800));
        }
        else if(rdm== 8)
        {
            viewHolder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorTeal800));
        }
        else
        {
            viewHolder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.colorYellow800));
        }

        Log.d("position/2",""+rdm);
    }

    private void showPopupMenu(View view)
    {
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
    }
    @Override
    public int getItemCount()
    {
        return endangeredItems.size();
    }

    public void setOnItemClickListener(MyClickListener myClickListener)
    {
        this.mCallback = myClickListener;
    }

    public interface MyClickListener
    {
        public void onItemClick(int position, View v);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public TextView title,detail;
        public ImageView thumbnail, overflow;
        public CardView cardView;

        public MyViewHolder(View view)
        {
            super(view);
            title = (TextView) view.findViewById(R.id.txt_card_title);
            detail = (TextView) view.findViewById(R.id.txt_card_detail);
            thumbnail = (ImageView) view.findViewById(R.id.img_cardview_thumbnail);
            overflow = (ImageView) view.findViewById(R.id.img_cardview_overflow);
            cardView = (CardView) view.findViewById(R.id.card_view);

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View view)
        {

            int pos = getAdapterPosition();
            if(pos != RecyclerView.NO_POSITION)
            {
                EndangeredItem clickedDataItem = endangeredItems.get(pos);
                Intent intent = new Intent(mContext, ShowExtraDetail.class);

                mContext.startActivity(intent);
            }

            //mCallback.onItemClick(getAdapterPosition(),view);
        }
    }
}
