package com.mindfulai.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mindfulai.Activites.AllProductsActivity;
import com.mindfulai.Activites.ShowVendorByCategoryActivity;
import com.mindfulai.Models.categoryData.Datum;
import com.mindfulai.Utils.CommonUtils;
import com.mindfulai.Utils.GlobalEnum;
import com.mindfulai.Utils.SPData;
import com.mindfulai.ministore.R;

import java.util.List;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder> {


    private Context context;
    private List<Datum> categoryList;
    private FragmentManager fragmentManager;
    private String type;
    private static final String TAG = "CategoriesAdapter";

    static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView cat_image;
        TextView cat_name;
        CardView cardView;


        MyViewHolder(View view) {
            super(view);

            cat_image = view.findViewById(R.id.cat_image);
            cat_name = view.findViewById(R.id.cat_name);
            cardView = view.findViewById(R.id.card_categories);
        }
    }

    public CategoriesAdapter(Context context, List<Datum> categoryList, String type) {

        this.context = context;
        this.categoryList = categoryList;
        this.type = type;

    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (type.equals("grid"))
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.custom_categories_layout, parent, false);
        else
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.categories_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        try {
            holder.cat_name.setText(CommonUtils.capitalizeWord(categoryList.get(position).getName()));
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.noimage);
            requestOptions.error(R.drawable.noimage);
            Glide.with(context)
                    .applyDefaultRequestOptions(requestOptions)
                    .load(GlobalEnum.AMAZON_URL + categoryList.get(position).getImage())
                    .into(holder.cat_image);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!SPData.getShowVendor()) {
                        Intent i = new Intent(context, AllProductsActivity.class);
                        i.putExtra("category_id", categoryList.get(position).getId());
                        i.putExtra("categoryName", categoryList.get(position).getName());
                        ((Activity) context).startActivityForResult(i, 2);
                    } else {
                        Intent i = new Intent(context, ShowVendorByCategoryActivity.class);
                        i.putExtra("category_id", categoryList.get(position).getId());
                        i.putExtra("categoryName", categoryList.get(position).getName());
                        context.startActivity(i);
                    }
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
