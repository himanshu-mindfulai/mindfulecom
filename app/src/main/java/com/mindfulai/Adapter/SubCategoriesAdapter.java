package com.mindfulai.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.TextViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mindfulai.Activites.AllProductsActivity;
import com.mindfulai.Models.SubcategoryModel.Datum;
import com.mindfulai.Utils.CommonUtils;
import com.mindfulai.Utils.GlobalEnum;
import com.mindfulai.ministore.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SubCategoriesAdapter extends RecyclerView.Adapter<SubCategoriesAdapter.MyViewHolder> {


    private Context context;
    private List<Datum> categoryList;
    private int level;
    private static final String TAG = "CategoriesAdapter";

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView cat_name;
        CircleImageView circleImageView;

        MyViewHolder(View view) {
            super(view);
            cat_name = view.findViewById(R.id.sub_category_name);
            circleImageView = view.findViewById(R.id.sub_category_img);
        }
    }

    public SubCategoriesAdapter(Context context, List<Datum> categoryList, int level) {

        this.context = context;
        this.categoryList = categoryList;
        this.level = level;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subcategory_view, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        try {
            Log.i("SubCategory", "onBindViewHolder: " + CommonUtils.capitalizeWord(categoryList.get(position).getName()));
            //TextViewCompat.setAutoSizeTextTypeWithDefaults(holder.cat_name, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
            holder.cat_name.setText(CommonUtils.capitalizeWord(categoryList.get(position).getName()));
            if (categoryList.get(position).getImage() != null)
                Glide.with(context).load(GlobalEnum.AMAZON_URL + categoryList.get(position).getImage()).into(holder.circleImageView);
            else
                holder.circleImageView.setImageDrawable(context.getDrawable(R.drawable.noimage));
            Log.e(TAG, "level: " + level);
//            if (level > 1){
//                holder.circleImageView.setVisibility(View.GONE);
//                holder.cat_name.setBackground(context.getDrawable(R.drawable.rounded_border));
//            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, AllProductsActivity.class);
                    i.putExtra("level", level + 1);
                    i.putExtra("category_id", categoryList.get(position).getId());
                    i.putExtra("categoryName", categoryList.get(position).getName());
                    ((Activity) context).startActivityForResult(i, 10);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "onBindViewHolder: " + e);
        }

    }

    @Override
    public int getItemCount() {
        try {
            return categoryList.size();
        } catch (Exception e) {
            return 0;
        }
    }

}
