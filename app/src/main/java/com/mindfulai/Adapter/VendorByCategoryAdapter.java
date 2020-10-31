package com.mindfulai.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mindfulai.Activites.AllProductsActivity;
import com.mindfulai.Models.VendorChild;
import com.mindfulai.ministore.R;
import com.mindfulai.Utils.CommonUtils;
import com.mindfulai.Utils.GlobalEnum;

import java.util.List;

public class VendorByCategoryAdapter extends RecyclerView.Adapter<VendorByCategoryAdapter.MyViewHolder> {
    private Context context;
    private List<VendorChild> vendorChildList;
    private String catID;
    private String catName;

    public VendorByCategoryAdapter(Context context, List<VendorChild> vendorChildList, String catId, String catName) {
        this.context = context;
        this.vendorChildList = vendorChildList;
        this.catID = catId;
        this.catName = catName;
    }

    @NonNull
    @Override
    public VendorByCategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_vendor_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VendorByCategoryAdapter.MyViewHolder holder, final int position) {
        holder.product_name.setText(CommonUtils.capitalizeWord(vendorChildList.get(position).getFull_name()));
        if (vendorChildList.get(position).getProfile_picture() != null) {
            Glide.with(context).load(GlobalEnum.AMAZON_URL + vendorChildList.get(position).getProfile_picture()).into(holder.image);
        } else {
            Glide.with(context).load(context.getResources().getDrawable(R.drawable.noimage)).into(holder.image);
        }
        holder.vendor_address.setText(vendorChildList.get(position).getAddress());
       // holder.vendor_phoneno.setText(vendorChildList.get(position).getMobile_number());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AllProductsActivity.class);
                intent.putExtra("vendor_id", vendorChildList.get(position).get_id());
                intent.putExtra("category_id", catID);
                intent.putExtra("categoryName", catName);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vendorChildList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView product_name;
        TextView vendor_address;
      //  TextView vendor_phoneno;
        public ImageView image;

        MyViewHolder(View view) {
            super(view);
            product_name = view.findViewById(R.id.vendor_name);
            vendor_address = view.findViewById(R.id.vendor_address);
            image = view.findViewById(R.id.image);
         //   vendor_phoneno = view.findViewById(R.id.vendor_phoneno);

        }
    }

}
