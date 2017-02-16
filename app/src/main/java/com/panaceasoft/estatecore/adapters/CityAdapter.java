package com.panaceasoft.estatecore.adapters;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.panaceasoft.estatecore.Config;
import com.panaceasoft.estatecore.R;
import com.panaceasoft.estatecore.models.PCityData;
import com.panaceasoft.estatecore.utilities.BitmapTransform;
import com.panaceasoft.estatecore.utilities.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Panacea-Soft on 15/7/15.
 * Contact Email : teamps.is.cool@gmail.com
 */


public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder>  {
    private Activity activity;
    private int lastPosition = -1;
    private List<PCityData> pCityDataList;
    private Picasso p;
    public static class CityViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout cv;
        TextView cityName;
        ImageView cityPhoto;
        TextView cityDesc;


        CityViewHolder(View itemView) {
            super(itemView);
            cv = (RelativeLayout)itemView.findViewById(R.id.shop_cv);
            cityName = (TextView)itemView.findViewById(R.id.city_name);
            cityDesc = (TextView)itemView.findViewById(R.id.city_desc);
            cityPhoto = (ImageView)itemView.findViewById(R.id.city_photo);
            cityName.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));
            cityDesc.setTypeface(Utils.getTypeFace(Utils.Fonts.ROBOTO));
        }
    }

    public CityAdapter(Context context, List<PCityData> cities, Picasso p){
        this.activity = (Activity) context;
        this.pCityDataList = cities;
        this.p = p;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_row_container, parent, false);
        CityViewHolder svh = new CityViewHolder(v);
        return svh;
    }


    private int MAX_WIDTH = Utils.getScreenWidth();
    private int MAX_HEIGHT = MAX_WIDTH;
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public void onBindViewHolder(final CityViewHolder holder, int position) {
        final PCityData city = pCityDataList.get(position);
        holder.cityName.setText(city.name);
        holder.cityDesc.setText(city.description.substring(0, Math.min(city.description.length(), 150)) + "...");
        p.load(Config.APP_IMAGES_URL + city.cover_image_file)
                .transform(new BitmapTransform(MAX_WIDTH, MAX_HEIGHT))
                .placeholder(R.drawable.ps_icon)
                .into(holder.cityPhoto);
        setAnimation(holder.cv, position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


            }
        });

    }

    @Override
    public int getItemCount() {
        if(pCityDataList != null) {
            return pCityDataList.size();
        }
        return 0;
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(activity, R.anim.abc_slide_in_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }else{
            lastPosition = position;
        }
    }

}
