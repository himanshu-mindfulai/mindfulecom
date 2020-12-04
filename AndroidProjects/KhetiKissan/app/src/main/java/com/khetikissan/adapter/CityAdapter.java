package com.khetikissan.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.khetikissan.R;
import com.khetikissan.activity.AddCityActivity;
import com.khetikissan.activity.AddCropActivity;
import com.khetikissan.model.CityModel;
import com.khetikissan.mysqli.AllQuery;
import com.khetikissan.utils.AppModule;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {
    private Context context;
    private ArrayList<CityModel> cityModelArrayList;
    private String sid;
    private String stateName;
    private int status;
    private String msg;
    private int selectedPosition;
    private ProgressDialog progressDialog;

    public CityAdapter(Context context, ArrayList<CityModel> cityModels,String sid,String stateName) {
        this.cityModelArrayList = cityModels;
        this.context = context;
        this.sid = sid;
        this.stateName = stateName;
        progressDialog = new ProgressDialog(context);
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.city_view, parent, false);
        return new CityViewHolder(view);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBindViewHolder(@NonNull CityViewHolder cityViewHolder, final int position) {
        if (!AppModule.showAdminApp()) {
            cityViewHolder.delete.setVisibility(View.GONE);
            cityViewHolder.edit.setVisibility(View.GONE);
        } else {
            cityViewHolder.delete.setVisibility(View.VISIBLE);
            cityViewHolder.edit.setVisibility(View.VISIBLE);
        }
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(30, 30, 30, 30);
        cityViewHolder.itemView.setLayoutParams(params);
        cityViewHolder.name.setText(cityModelArrayList.get(position).getName());
        cityViewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AddCityActivity)context).showDialogForInput(cityModelArrayList.get(position).getCid(),cityModelArrayList.get(position),position);
            }
        });
        cityViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete " + cityModelArrayList.get(position).getName());
                builder.setMessage("Are you sure want to delete " + cityModelArrayList.get(position).getName());
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        String id = cityModelArrayList.get(position).getCid();
                        selectedPosition = position;
                        progressDialog.setMessage("Please wait...");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        new DeleteCity().execute(AllQuery.CITY_DELETE+id);
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

        cityViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, AddCropActivity.class)
                        .putExtra("cid", cityModelArrayList.get(position).getCid())
                        .putExtra("sid",sid)
                        .putExtra("stateName",stateName)
                        .putExtra("cityName", cityModelArrayList.get(position).getName())
                );
            }
        });
    }
    @SuppressLint("StaticFieldLeak")
    class DeleteCity extends AsyncTask<String, Void, Integer> {

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
                msg = jsonObject.getJSONObject("Cities").getString("Message");
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
            Toast.makeText(context, ""+msg, Toast.LENGTH_SHORT).show();
            if(status==200){
                cityModelArrayList.remove(selectedPosition);
                notifyItemRemoved(selectedPosition);
            }
        }
    }

    @Override
    public int getItemCount() {
        return cityModelArrayList.size();
    }

    static class CityViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView noOfCrop;
        FloatingActionButton edit, delete;

        CityViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_view);
            noOfCrop = itemView.findViewById(R.id.no_of_crop_view);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);

        }
    }
}
