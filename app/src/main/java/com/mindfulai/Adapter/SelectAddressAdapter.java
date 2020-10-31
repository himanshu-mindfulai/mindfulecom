package com.mindfulai.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mindfulai.Activites.AddAddressActivity;
import com.mindfulai.Models.UserDataAddress;
import com.mindfulai.ministore.R;

import java.util.List;

public class SelectAddressAdapter extends RecyclerView.Adapter<SelectAddressAdapter.ViewHolder> {

    private Context context;
    private List<UserDataAddress> userDataAddressList;
    private Activity activity;

    public SelectAddressAdapter(Context context, List<UserDataAddress> data, Activity activity){
        this.context = context;
        this.userDataAddressList = data;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.select_address_item_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.address_type.setText("Address " + (position + 1));
        if (userDataAddressList.get(position).getName() != null && userDataAddressList.get(position).getMobile_number() != null)
            holder.address_location.setText(userDataAddressList.get(position).getName() + "\n" + userDataAddressList.get(position).getAddressLine1() + "\n" + userDataAddressList.get(position).getAddressLine2() + ", " + userDataAddressList.get(position).getCity() + "\n" + userDataAddressList.get(position).getState() + ", " + userDataAddressList.get(position).getPincode() + "\n" + userDataAddressList.get(position).getMobile_number());
        else
            holder.address_location.setText(userDataAddressList.get(position).getAddressLine1() + "\n" + userDataAddressList.get(position).getAddressLine2() + ", " + userDataAddressList.get(position).getCity() + "\n" + userDataAddressList.get(position).getState() + ", " + userDataAddressList.get(position).getPincode());

        holder.address_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("address", userDataAddressList.get(position).getAddressLine1() + " " + userDataAddressList.get(position).getAddressLine2());
                activity.setResult(Activity.RESULT_OK, intent);
                activity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userDataAddressList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView address_location;
        TextView address_type;
        TextView address_select;

        public ViewHolder(@NonNull View view) {
            super(view);

            address_location = view.findViewById(R.id.complete_address);
            address_type = view.findViewById(R.id.address_type);
            address_select = view.findViewById(R.id.select);
        }
    }

}
