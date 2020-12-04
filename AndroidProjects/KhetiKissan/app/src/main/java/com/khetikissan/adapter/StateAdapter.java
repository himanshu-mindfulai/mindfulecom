package com.khetikissan.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.khetikissan.R;
import com.khetikissan.activity.AddCityActivity;
import com.khetikissan.fragment.HomeFragment;
import com.khetikissan.model.StateModel;
import com.khetikissan.mysqli.AllQuery;
import com.khetikissan.utils.AppModule;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.logging.LogRecord;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StateAdapter extends RecyclerView.Adapter<StateAdapter.StateViewHolder> {

    private ArrayList<StateModel> stateModelArrayList;
    private Context context;
    private ProgressDialog progressDialog;
    private int status;
    private String msg;
    private int selectedPosition;
    private HomeFragment homeFragment;

    public StateAdapter(Context context, ArrayList<StateModel> stringArrayList,HomeFragment homeFragment) {
        this.context = context;
        this.stateModelArrayList = stringArrayList;
        progressDialog = new ProgressDialog(context);
        this.homeFragment = homeFragment;
    }

    @NonNull
    @Override
    public StateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.state_view, parent, false);
        return new StateViewHolder(view);
    }

    @SuppressLint({"RestrictedApi", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull StateViewHolder stateViewHolder, final int position) {
        stateViewHolder.name.setText(stateModelArrayList.get(position).getName());
        Log.e("TAG", "onBindViewHolder: "+stateModelArrayList.get(position).getImage());
        if (stateModelArrayList.get(position).getImage() != null && !stateModelArrayList.get(position).getImage().isEmpty())
            Glide.with(context).load(stateModelArrayList.get(position).getImage()).placeholder(context.getResources().getDrawable(R.drawable.noimage)).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Log.e("TAG", "onLoadFailed: "+e);
                    return true;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    return false;
                }
            }).into(stateViewHolder.imageView);
        else
            stateViewHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.noimage));
        if (!AppModule.showAdminApp()) {
            stateViewHolder.floatingActionButtonDelete.setVisibility(View.GONE);
            stateViewHolder.floatingActionButtonEdit.setVisibility(View.GONE);
        } else {
            stateViewHolder.floatingActionButtonDelete.setVisibility(View.VISIBLE);
            stateViewHolder.floatingActionButtonEdit.setVisibility(View.VISIBLE);
        }
        stateViewHolder.floatingActionButtonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeFragment.showDialogForInput(stateModelArrayList.get(position),stateModelArrayList.get(position).getSid(),position);
            }
        });
        stateViewHolder.floatingActionButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Delete " + stateModelArrayList.get(position).getName());
                        builder.setMessage("Are you sure want to delete " + stateModelArrayList.get(position).getName());
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                final String id = stateModelArrayList.get(position).getSid();
                                selectedPosition = position;
                                progressDialog.setMessage("Please wait...");
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.show();
                                new DeleteState().execute(AllQuery.STATE_DELETE+id);
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

        stateViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = stateModelArrayList.get(position).getSid();
                String name = stateModelArrayList.get(position).getName();
                context.startActivity(new Intent(context, AddCityActivity.class).putExtra("sid", id)
                        .putExtra("stateName", name)
                );
            }
        });

    }

    @SuppressLint("StaticFieldLeak")
    class DeleteState extends AsyncTask<String, Void, Integer> {

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
                msg = jsonObject.getJSONObject("States").getString("Message");
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
                stateModelArrayList.remove(selectedPosition);
                notifyItemRemoved(selectedPosition);
            }
        }
    }
    @Override
    public int getItemCount() {
        return stateModelArrayList.size();
    }

    static class StateViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView imageView;
        FloatingActionButton floatingActionButtonEdit;
        FloatingActionButton floatingActionButtonDelete;

        StateViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name_view);
            floatingActionButtonDelete = itemView.findViewById(R.id.delete);
            floatingActionButtonEdit = itemView.findViewById(R.id.edit);
            imageView = itemView.findViewById(R.id.image);
        }
    }
}