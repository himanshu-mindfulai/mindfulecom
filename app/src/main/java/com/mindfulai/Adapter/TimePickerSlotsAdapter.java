package com.mindfulai.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.mindfulai.ministore.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TimePickerSlotsAdapter extends RecyclerView.Adapter<TimePickerSlotsAdapter.ViewHolder> {

    Context context;
    ArrayList<String> timeslots;
    TextView timeSlot;
    String dateString;
    AlertDialog alertDialog;

    public TimePickerSlotsAdapter(Context context, ArrayList<String> timeSlots, TextView timeSlot, String dateString, AlertDialog alertDialog){
        this.context = context;
        this.timeslots = timeSlots;
        this.timeSlot = timeSlot;
        this.dateString = dateString;
        this.alertDialog = alertDialog;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.time_picker_item_layout, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimePickerSlotsAdapter.ViewHolder holder, int position) {
        holder.tvTime.setText(timeslots.get(position));
        holder.tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeSlot.setText(dateString + timeslots.get(position) + " (Change)");
                alertDialog.cancel();
            }
        });
    }

    @Override
    public int getItemCount() {
        return timeslots.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTime = itemView.findViewById(R.id.tv_time_layout);
        }
    }
}
