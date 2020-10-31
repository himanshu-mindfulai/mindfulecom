package com.mindfulai.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mindfulai.Models.CityName;
import com.mindfulai.ministore.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StateListViewAdapter extends BaseAdapter {

    // Declare Variables

    private LayoutInflater inflater;
    private List<CityName> cityNameList;
    private ArrayList<CityName> arraylist;

    public StateListViewAdapter(Context context, List<CityName> cityNameList) {
        this.cityNameList = cityNameList;
        inflater = LayoutInflater.from(context);
        this.arraylist = new ArrayList<CityName>();
        this.arraylist.addAll(cityNameList);
    }

    public class ViewHolder {
        TextView name;
    }

    @Override
    public int getCount() {
        return cityNameList.size();
    }

    @Override
    public CityName getItem(int position) {
        return cityNameList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.city_list_view_items, null);
            holder.name = view.findViewById(R.id.name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(cityNameList.get(position).getCityName());
        return view;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        cityNameList.clear();
        if (charText.length() == 0) {
            cityNameList.addAll(arraylist);
        } else {
            for (CityName wp : arraylist) {
                if (wp.getCityName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    cityNameList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
