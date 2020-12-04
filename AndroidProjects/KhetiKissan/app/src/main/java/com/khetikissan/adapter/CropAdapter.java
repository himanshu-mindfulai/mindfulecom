package com.khetikissan.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.khetikissan.R;
import com.khetikissan.activity.AddCityActivity;
import com.khetikissan.activity.AddCropActivity;
import com.khetikissan.model.CropModel;
import com.khetikissan.mysqli.AllQuery;
import com.khetikissan.utils.AppModule;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CropAdapter extends RecyclerView.Adapter<CropAdapter.CropViewHolder> {

    private Context context;
    private ArrayList<CropModel> cropModelArrayList;
    private int status;
    private String msg;
    private ProgressDialog progressDialog;
    private int selectedPosition;
    private boolean search;

    public CropAdapter(Context context, ArrayList<CropModel> cropModels, boolean search) {
        this.context = context;
        this.cropModelArrayList = cropModels;
        progressDialog = new ProgressDialog(context);
        this.search = search;
    }

    @NonNull
    @Override
    public CropViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.crop_view, parent, false);
        return new CropViewHolder(view);
    }

    @SuppressLint({"RestrictedApi", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull CropViewHolder cropViewHolder, final int position) {
        if (!AppModule.showAdminApp()) {
            cropViewHolder.edit.setVisibility(View.GONE);
            cropViewHolder.delete.setVisibility(View.GONE);
        } else {
            cropViewHolder.edit.setVisibility(View.VISIBLE);
            cropViewHolder.delete.setVisibility(View.VISIBLE);
        }
        cropViewHolder.rv.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(30, 30, 30, 30);
        cropViewHolder.rv.setLayoutParams(params);
        cropViewHolder.itemView.setLayoutParams(params);
        if (cropModelArrayList.get(position).getImage() != null && !cropModelArrayList.get(position).getImage().isEmpty())
            Glide.with(context).load(cropModelArrayList.get(position).getImage()).placeholder(context.getResources().getDrawable(R.drawable.noimage)).into(cropViewHolder.imageView);
        else
            cropViewHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.noimage));
        cropViewHolder.lastUpdate.setText("last update on " + cropModelArrayList.get(position).getDate());
        cropViewHolder.yesterdayPrice.setText("Yesterday price - " + "\u20B9" + cropModelArrayList.get(position).getYesterdayPrice());
        cropViewHolder.todayPrice.setText("Today price - " + "\u20B9" + cropModelArrayList.get(position).getTodayPrice());
        cropViewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AddCropActivity) context).showDialogForInput(cropModelArrayList.get(position).getCropid(), cropModelArrayList.get(position), position);

            }
        });
        if (search)
            cropViewHolder.name.setText(cropModelArrayList.get(position).getName() + "\n" + cropModelArrayList.get(position).getCityName() + " ,\n" + cropModelArrayList.get(position).getStateName());
        else
            cropViewHolder.name.setText(cropModelArrayList.get(position).getName());
        cropViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete " + cropModelArrayList.get(position).getName());
                builder.setMessage("Are you sure want to delete " + cropModelArrayList.get(position).getName());
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        String id = cropModelArrayList.get(position).getCropid();
                        selectedPosition = position;
                        progressDialog.setMessage("Please wait...");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        new DeleteCrop().execute(AllQuery.CROP_DELETE + id);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    class DeleteCrop extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json;charset=utf-8");
            JSONObject params = new JSONObject();
            try {
                RequestBody body = RequestBody.create(mediaType, params.toString());
                Request request = new Request.Builder()
                        .url(strings[0])
                        .post(body)
                        .addHeader("Content-Type", "application/json;charset=utf-8")
                        .build();
                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                JSONObject jsonObject = new JSONObject(jsonData);
                status = jsonObject.getInt("Status");
                msg = jsonObject.getJSONObject("Crops").getString("Message");
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("TAG", "doInBackground: " + e);
            }
            return status;
        }

        @Override
        protected void onPostExecute(Integer status) {
            super.onPostExecute(status);
            progressDialog.cancel();
            Toast.makeText(context, "" + msg, Toast.LENGTH_SHORT).show();
            if (status == 200) {
                cropModelArrayList.remove(selectedPosition);
                notifyItemRemoved(selectedPosition);
            }
        }
    }

    @Override
    public int getItemCount() {
        return cropModelArrayList.size();
    }

    static class CropViewHolder extends RecyclerView.ViewHolder {
        TextView name, todayPrice, yesterdayPrice, lastUpdate;
        ImageView imageView;
        RelativeLayout rv;
        FloatingActionButton edit, delete;

        CropViewHolder(@NonNull View itemView) {
            super(itemView);
            todayPrice = itemView.findViewById(R.id.today_price);
            name = itemView.findViewById(R.id.name);
            yesterdayPrice = itemView.findViewById(R.id.yesterday_price);
            lastUpdate = itemView.findViewById(R.id.last_update);
            imageView = itemView.findViewById(R.id.image);
            rv = itemView.findViewById(R.id.rv);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
