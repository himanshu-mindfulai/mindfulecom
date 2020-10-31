package com.mindfulai.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.mindfulai.Activites.AllProductsActivity;
import com.mindfulai.Models.categoryData.Datum;
import com.mindfulai.Models.categoryData.Subcategory;
import com.mindfulai.Utils.CommonUtils;
import com.mindfulai.ministore.R;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<Datum> listDataHeader;
    private HashMap<Datum, List<Subcategory>> listDataChild;

    public ExpandableListAdapter(Context context, List<Datum> listDataHeader,
                                 HashMap<Datum, List<Subcategory>> listChildData) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listDataChild = listChildData;
    }

    @Override
    public Subcategory getChild(int groupPosition, int childPosititon) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = getChild(groupPosition, childPosition).getName();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.nav_sub_category_item, null);
        }

        TextView txtListChild = convertView
                .findViewById(R.id.item);

        txtListChild.setText(CommonUtils.capitalizeWord(childText));
        txtListChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, AllProductsActivity.class);
                i.putExtra("category_id", getChild(groupPosition, childPosition).getId());
                i.putExtra("categoryName", getChild(groupPosition, childPosition).getName());
                ((Activity) context).startActivityForResult(i, 2);
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        if (this.listDataChild.get(this.listDataHeader.get(groupPosition)) == null)
            return 0;
        else
            return this.listDataChild.get(this.listDataHeader.get(groupPosition))
                    .size();
    }

    @Override
    public Datum getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();

    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = getGroup(groupPosition).getName();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.nav_category_header, null);
        }

        TextView lblListHeader = convertView.findViewById(R.id.header);
        lblListHeader.setText(CommonUtils.capitalizeWord(headerTitle));
        lblListHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, AllProductsActivity.class);
                i.putExtra("category_id", getGroup(groupPosition).getId());
                i.putExtra("categoryName", getGroup(groupPosition).getName());
                ((Activity) context).startActivityForResult(i, 2);
            }
        });
        ImageView listHeaderArrow = (ImageView) convertView.findViewById(R.id.left_menu_list_header_arrow);


//        Drawable drawable = isExpanded ? context.getDrawable(R.drawable.ic_baseline_keyboard_arrow_up_24) : context.getDrawable(R.drawable.ic_baseline_keyboard_arrow_down_24);
//        listHeaderArrow.setImageDrawable(drawable);
        listHeaderArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isExpanded) ((ExpandableListView) parent).collapseGroup(groupPosition);
                else ((ExpandableListView) parent).expandGroup(groupPosition, true);
            }
        });
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
