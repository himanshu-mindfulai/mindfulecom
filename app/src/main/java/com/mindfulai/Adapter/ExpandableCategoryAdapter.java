package com.mindfulai.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mindfulai.Activites.AllProductsActivity;
import com.mindfulai.Activites.ShowVendorByCategoryActivity;
import com.mindfulai.AppPrefrences.AppPreferences;
import com.mindfulai.Models.categoryData.Datum;
import com.mindfulai.Models.categoryData.Subcategory;
import com.mindfulai.Utils.SPData;
import com.mindfulai.customclass.ExpandableRecyclerView;
import com.mindfulai.ministore.R;

import java.util.List;


public class ExpandableCategoryAdapter extends ExpandableRecyclerView.Adapter<ExpandableCategoryAdapter.ChildViewHolder, ExpandableRecyclerView.SimpleGroupViewHolder, String, String> {

    private AppPreferences appPreferences;
    private Context context;
    private List<Datum> categoryList;
    private List<List<Subcategory>> subcategoryList;

    private static final String TAG = "ExpandableCategory";

    public ExpandableCategoryAdapter(Context context, List<Datum> categoryList, List<List<Subcategory>> subcategoryList) {
        this.context = context;
        this.categoryList = categoryList;
        this.subcategoryList = subcategoryList;
    }

    public ExpandableCategoryAdapter() {

    }


    @Override
    public int getGroupItemCount() {
        return categoryList.size() - 1;
    }

    @Override
    public int getChildItemCount(int group) {
        return subcategoryList.get(group).size();
    }

    @Override
    public String getGroupItem(int position) {
        return categoryList.get(position).getName();
    }

    @Override
    public String getChildItem(int group, int position) {
        return subcategoryList.get(group).get(position).getName();
    }

    @Override
    protected ExpandableRecyclerView.SimpleGroupViewHolder onCreateGroupViewHolder(ViewGroup parent) {
        return new ExpandableRecyclerView.SimpleGroupViewHolder(context);
    }

    @Override
    protected ChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_categories, parent, false);
        return new ChildViewHolder(view);
    }

    @Override
    public int getChildItemViewType(int group, int position) {
        return 1;
    }

    @Override
    public void onBindGroupViewHolder(final ExpandableRecyclerView.SimpleGroupViewHolder holder, int group) {
        super.onBindGroupViewHolder(holder, group);
        holder.setText(capitalizeWord(getGroupItem(group)));
    }

    private String capitalizeWord(String str) {
        String[] words = str.split("\\s");
        StringBuilder capitalizeWord = new StringBuilder();
        for (String w : words) {
            if (w.length() > 0) {
                String first = w.substring(0, 1);
                String afterfirst = w.substring(1);
                capitalizeWord.append(first.toUpperCase()).append(afterfirst).append(" ");
            }
        }
        return capitalizeWord.toString().trim();
    }

    @Override
    public void onBindChildViewHolder(ChildViewHolder holder, final int group, final int position) {
        try {
            super.onBindChildViewHolder(holder, group, position);
            holder.tv.setText(getChildItem(group, position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SPData.getShowVendor()) {
                        Intent intent = new Intent(context, ShowVendorByCategoryActivity.class);
                        intent.putExtra("category_id", subcategoryList.get(group).get(position).getId());
                        intent.putExtra("categoryName", subcategoryList.get(group).get(position).getName());
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent(context, AllProductsActivity.class);
                        intent.putExtra("category_id", subcategoryList.get(group).get(position).getId());
                        intent.putExtra("categoryName", subcategoryList.get(group).get(position).getName());
                        ((Activity) context).startActivity(intent);
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "onBindChildViewHolder: " + e);
        }
    }

    static class ChildViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv;

        ChildViewHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.sub_title_category_item);
        }
    }
}