package com.mindfulai.course.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mindfulai.course.CustomClass.SPData;
import com.mindfulai.course.R;
import com.mindfulai.course.agora.activity.SmallClassActivity;
import com.mindfulai.course.agora.model.RoomEntry;
import com.mindfulai.course.agora.request.RoomCreateOptionsReq;
import com.mindfulai.course.agora.util.CommonService;
import com.mindfulai.course.pojo.ChapterModelData;

import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import io.agora.base.callback.ThrowableCallback;
import io.agora.base.network.BusinessException;
import io.agora.base.network.RetrofitManager;
import io.agora.education.api.BuildConfig;
import io.agora.education.api.EduCallback;
import io.agora.education.api.manager.EduManager;
import io.agora.education.api.manager.EduManagerOptions;
import io.agora.education.api.room.data.RoomCreateOptions;
import io.agora.education.api.room.data.RoomType;
import io.agora.education.api.statistics.AgoraError;
import io.agora.education.api.user.data.EduUserRole;
import static com.mindfulai.course.agora.EduApplication.getAppId;
import static com.mindfulai.course.agora.EduApplication.getCustomerCer;
import static com.mindfulai.course.agora.EduApplication.getCustomerId;
import static com.mindfulai.course.agora.EduApplication.setManager;

import io.agora.base.network.ResponseBody;

public class ChapterAdapterLive extends RecyclerView.Adapter<ChapterAdapterLive.BannerViewHolder> {
    private Context context;
    private List<ChapterModelData> chapterModelData;
    private ProgressDialog progressDialog;
    private SPData spData;

    public static final String API_BASE_URL = "https://api.agora.io";


    public ChapterAdapterLive(Context context, List<ChapterModelData> browseBannerModels) {
        this.context = context;
        this.chapterModelData = browseBannerModels;
        spData = new SPData(context);
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chapter_item_layout, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, final int position) {
        try {
            holder.tv_name.setText(chapterModelData.get(position).getName());
            SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            Date date = gmtToLocalDate(iso.parse(chapterModelData.get(position).getCreated_date()));
            String month = new SimpleDateFormat("dd MMMM hh:mm a").format(date);
            String day = new SimpleDateFormat("EEEE").format(date);
            holder.tv_link_date.setText(day+", "+month);
            holder.start_live.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    conigureEdManagerOptions(position);
                }
            });
        } catch (Exception e) {
            Log.e("TAG", "onBindViewHolder: " + e);
        }
    }
    private Date gmtToLocalDate(Date date) {
        String timeZone = Calendar.getInstance().getTimeZone().getID();
        return new Date(
                date.getTime() + TimeZone.getTimeZone(timeZone).getOffset(date.getTime())
        );
    }
    private void conigureEdManagerOptions(final int position) {
        String name = "";
        if (!spData.getName().isEmpty())
            name = spData.getName();
        else
            name = "Student " + spData.getMobileNumber();
        String userUuid = name + EduUserRole.STUDENT.getValue();
        EduManagerOptions options = new EduManagerOptions(context, getAppId(), userUuid, name);
        options.setCustomerId(getCustomerId());
        options.setCustomerCertificate(getCustomerCer());
        options.setLogFileDir(context.getCacheDir().getAbsolutePath());
        options.setTag(999);
        EduManager.init(options, new EduCallback<EduManager>() {
            @Override
            public void onSuccess(@Nullable EduManager res) {
                if (res != null) {
                    Log.e("TAG", "onSuccess: Edu manager");
                    setManager(res);
                    setRoom(position);
                }
            }

            @Override
            public void onFailure(int code, @Nullable String reason) {
                Log.e("TAG", "onFailure: " + reason);
                Toast.makeText(context, "" + reason, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setRoom(int position) {
        int roomType = RoomType.SMALL_CLASS.getValue();
        String roomNameStr = chapterModelData.get(position).getName();
        String roomUuid = roomNameStr + roomType;
        createRoom(roomNameStr, roomUuid, roomType);
    }

    private void createRoom(final String roomNameStr, final String roomUuid, final int roomType) {
        RoomCreateOptions options = new RoomCreateOptions(roomUuid, roomNameStr, roomType);
        RetrofitManager.instance().getService(BuildConfig.API_BASE_URL, CommonService.class)
                .createClassroom(getAppId(), options.getRoomUuid(),
                        RoomCreateOptionsReq.convertRoomCreateOptions(options))
                .enqueue(new RetrofitManager.Callback<>(0, new ThrowableCallback<ResponseBody<String>>() {
                    @Override
                    public void onSuccess(@Nullable ResponseBody<String> res) {
                        Toast.makeText(context, "Creating Room....", Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                        startRoom(roomNameStr, roomUuid, roomType);
                    }

                    @Override
                    public void onFailure(@Nullable Throwable throwable) {
                        progressDialog.cancel();
                        BusinessException error;

                        if (throwable instanceof BusinessException) {
                            error = (BusinessException) throwable;
                        } else {
                            error = new BusinessException(throwable.getMessage());
                        }
                        if (error.getCode() == AgoraError.ROOM_ALREADY_EXISTS.getValue()) {
                            Toast.makeText(context, "Room already exists", Toast.LENGTH_SHORT).show();
                            startRoom(roomNameStr, roomUuid, roomType);
                        } else {
                            Toast.makeText(context, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }));
    }

    private void startRoom(String roomNameStr, String roomUuid, int roomType) {
        String name = "";
        if (!spData.getName().isEmpty())
            name = spData.getName();
        else
            name = "Student " + spData.getMobileNumber();
        String userUuid = name + EduUserRole.STUDENT.getValue();
        Intent intent = new Intent();
        RoomEntry roomEntry = new RoomEntry(name, userUuid, roomNameStr, roomUuid, roomType);
        intent.putExtra(SmallClassActivity.ROOMENTRY, roomEntry);
        intent.setClass(context, SmallClassActivity.class);
        ((Activity) context).startActivityForResult(intent, 909);
    }

    @Override
    public int getItemCount() {
        return chapterModelData.size();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_name;
        private ImageView start_live;
        private TextView tv_link_date;

        BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            start_live = itemView.findViewById(R.id.start_live);
            tv_link_date = itemView.findViewById(R.id.tv_link_date);
        }
    }
}
