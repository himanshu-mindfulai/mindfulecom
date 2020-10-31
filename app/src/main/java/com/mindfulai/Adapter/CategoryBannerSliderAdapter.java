package com.mindfulai.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.mindfulai.Models.BannerInfoData.BannerInfo;
import com.mindfulai.Models.BannerInfoData.CategoryBannerData;
import com.mindfulai.Utils.GlobalEnum;

import java.util.List;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

/**
 * @author S.Shahini
 * @since 2/12/18
 */

public class CategoryBannerSliderAdapter extends SliderAdapter {


    private List<CategoryBannerData> bannerImages_List;
    private Context context;

    @Override
    public int getItemCount() {
        return bannerImages_List.size();
    }

    public CategoryBannerSliderAdapter(Context context, List<CategoryBannerData> bannerDataArrayList) {
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
