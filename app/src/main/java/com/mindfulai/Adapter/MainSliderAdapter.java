package com.mindfulai.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.mindfulai.Activites.AllProductsActivity;
import com.mindfulai.Models.BannerInfoData.BannerInfo;
import com.mindfulai.Utils.GlobalEnum;

import java.util.List;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

/**
 * @author S.Shahini
 * @since 2/12/18
 */

public class MainSliderAdapter extends SliderAdapter {


    private List<BannerInfo> bannerImages_List;
    private Context context;

    @Override
    public int getItemCount() {
        return bannerImages_List.size();
    }

    public MainSliderAdapter(Context context, List<BannerInfo> bannerDataArrayList) {
        this.context = context;
        this.bannerImages_List = bannerDataArrayList;
    }

    @Override
    public void onBindImageSlide(final int position, ImageSlideViewHolder viewHolder) {

        if (bannerImages_List.size() > 0) {
           viewHolder.bindImageSlide(GlobalEnum.AMAZON_URL + bannerImages_List.get(position).getImage());
            viewHolder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    if (bannerImages_List.get(position).getTarget() != null) {
//                        if (bannerImages_List.get(position).getType().equals("product_category")) {
//                            Intent i = new Intent(context, AllProductsActivity.class);
//                            i.putExtra("category_id", bannerImages_List.get(position).getTarget().getId());
//                            i.putExtra("categoryName", bannerImages_List.get(position).getTarget().getFullName());
//                            ((Activity) context).startActivityForResult(i, 2);
//                        }
//                    }
                }
            });
        }

//        }
    }

}
