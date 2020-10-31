package com.mindfulai.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.mindfulai.ministore.R;
import com.mindfulai.Utils.SPData;
import com.mindfulai.ui.CartFragment;

import java.util.ArrayList;

public class SearchCustomerAdapter extends RecyclerView.Adapter<SearchCustomerAdapter.ViewHolder> {
    private ArrayList<String> names;
    private Context context;
    private CartFragment cartFragment;

    public SearchCustomerAdapter(Context context, ArrayList<String> names, CartFragment cartFragment) {
        this.names = names;
        this.context = context;
        this.cartFragment=cartFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cutomer_list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.textViewName.setText(names.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Place Order");
                alertDialog.setMessage("Ready to place order for " + names.get(position));
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SPData.getAppPreferences().setUserName(names.get(position));
                        cartFragment.promptDialog();
                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.create().show();

            }
        });
    }

    public void filterList(ArrayList<String> filterdNames) {
        this.names = filterdNames;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        ViewHolder(View itemView) {
            super(itemView);
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
        }
    }
}
