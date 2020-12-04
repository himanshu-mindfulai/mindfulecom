package com.mindfulai.course.agora.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
//5fabcf50db0682c7a78b3eee
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.mindfulai.course.R;
import com.mindfulai.course.agora.EduApplication;
import com.mindfulai.course.agora.adapter.ClassVideoAdapter;
import com.mindfulai.course.agora.fragment.UserListFragment;
import com.mindfulai.course.agora.model.Room;
import com.mindfulai.course.agora.model.RoomEntry;
import com.mindfulai.course.agora.widget.ConfirmDialog;
import com.mindfulai.course.agora.widget.RtcVideoView;
import com.mindfulai.course.agora.widget.TitleView;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.agora.base.network.RetrofitManager;
import io.agora.education.api.EduCallback;
import io.agora.education.api.manager.listener.EduManagerEventListener;
import io.agora.education.api.message.EduActionMessage;
import io.agora.education.api.message.EduChatMsg;
import io.agora.education.api.message.EduMsg;
import io.agora.education.api.room.EduRoom;
import io.agora.education.api.room.data.EduRoomChangeType;
import io.agora.education.api.room.data.EduRoomInfo;
import io.agora.education.api.room.data.EduRoomState;
import io.agora.education.api.room.data.EduRoomStatus;
import io.agora.education.api.room.data.RoomCreateOptions;
import io.agora.education.api.room.data.RoomJoinOptions;
import io.agora.education.api.room.data.RoomMediaOptions;
import io.agora.education.api.room.data.RoomType;
import io.agora.education.api.room.listener.EduRoomEventListener;
import io.agora.education.api.statistics.ConnectionState;
import io.agora.education.api.statistics.NetworkQuality;
import io.agora.education.api.stream.data.EduStreamEvent;
import io.agora.education.api.stream.data.EduStreamInfo;
import io.agora.education.api.stream.data.EduStreamStateChangeType;
import io.agora.education.api.stream.data.LocalStreamInitOptions;
import io.agora.education.api.stream.data.VideoSourceType;
import io.agora.education.api.user.EduStudent;
import io.agora.education.api.user.EduUser;
import io.agora.education.api.user.data.EduUserEvent;
import io.agora.education.api.user.data.EduUserInfo;
import io.agora.education.api.user.data.EduUserRole;
import io.agora.education.api.user.data.EduUserStateChangeType;
import io.agora.education.api.user.listener.EduUserEventListener;
import io.agora.education.impl.room.EduRoomImpl;
import io.agora.education.impl.user.EduStudentImpl;
import io.agora.education.impl.user.data.EduLocalUserInfoImpl;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcChannel;
import io.agora.rte.RteEngineImpl;
import io.agora.rte.listener.RteAudioMixingListener;
import io.agora.rte.listener.RteMediaDeviceListener;
import io.agora.rte.listener.RteSpeakerReportListener;
import io.agora.rte.listener.RteStatisticsReportListener;

import static com.mindfulai.course.agora.EduApplication.getManager;

public class SmallClassActivity extends AppCompatActivity implements
        RteAudioMixingListener, RteMediaDeviceListener, RteSpeakerReportListener, RteStatisticsReportListener , EduRoomEventListener, EduUserEventListener,
        EduManagerEventListener {

    protected RecyclerView rcv_videos;
    protected View layout_im;
    public RtcVideoView shareVideoAreaRtc;
    public AppCompatImageView imageViewCollapseExpand;
    private ClassVideoAdapter classVideoAdapter;
    private UserListFragment userListFragment;

    public static final String ROOMENTRY = "roomEntry";
    public static final int RESULT_CODE = 808;
    public static final String CODE = "code";
    public static final String REASON = "reason";
    protected TitleView title_view;
    public RoomEntry roomEntry;
    private volatile boolean isJoining = false, joinSuccess = false;
    private EduRoom mainEduRoom;
    private EduStreamInfo localCameraStream, localScreenStream;
    protected volatile boolean revRecordMsg = false;
    protected FrameLayout layout_share_video;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("TAG", "onCreate: small class activity" );
        setContentView(R.layout.activity_small_class);
        initView();
    }
    protected void initData() {
        getManager().setEduManagerEventListener(this);
        RoomCreateOptions createOptions = new RoomCreateOptions(roomEntry.getRoomUuid(),
                roomEntry.getRoomName(), roomEntry.getRoomType());
        mainEduRoom = buildEduRoom(createOptions, null);
        joinRoom(mainEduRoom, roomEntry.getUserName(), roomEntry.getUserUuid(), true, true, true,
                new EduCallback<EduStudent>() {
                    @Override
                    public void onSuccess(@Nullable EduStudent res) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(SmallClassActivity.this, "Join success", Toast.LENGTH_SHORT).show();
                                Log.e("TAG", "run: on success");
                                showFragmentWithJoinSuccess();
                            }
                        });
                    }

                    @Override
                    public void onFailure(int code, @Nullable String reason) {
                        joinFailed(code, reason);
                    }
                });
    }
    protected EduRoom buildEduRoom(RoomCreateOptions options, String parentRoomUuid) {
        int roomType = options.getRoomType();
        if (options.getRoomType() == RoomType.BREAKOUT_CLASS.getValue()) {
            roomType = TextUtils.isEmpty(parentRoomUuid) ? RoomType.LARGE_CLASS.getValue() :
                    RoomType.SMALL_CLASS.getValue();
        }
        options = new RoomCreateOptions(options.getRoomUuid(), options.getRoomName(), roomType);
        EduRoom room = EduApplication.buildEduRoom(options);
        room.setEventListener(SmallClassActivity.this);
        return room;
    }

    protected void joinRoom(final EduRoom eduRoom, String yourNameStr, String yourUuid, boolean autoSubscribe,
                            boolean autoPublish, final boolean needUserListener, final EduCallback<EduStudent> callback) {
        if (isJoining) {
            return;
        }
        isJoining = true;
        RoomJoinOptions options = new RoomJoinOptions(yourUuid, yourNameStr, EduUserRole.STUDENT,
                new RoomMediaOptions(autoSubscribe, autoPublish));
        eduRoom.joinClassroom(options, new EduCallback<EduStudent>() {
            @Override
            public void onSuccess(@Nullable EduStudent res) {
                progressDialog.cancel();
                Log.e("TAG", "onSuccess: on join room  "+getMainEduRoom().getLocalUser().getUserInfo().getUserToken() );
                RetrofitManager.instance().addHeader("token",
                        getMainEduRoom().getLocalUser().getUserInfo().getUserToken());
                joinSuccess = true;
                isJoining = false;
                if (needUserListener) {
                    eduRoom.getLocalUser().setEventListener(SmallClassActivity.this);
                }
                callback.onSuccess(res);
            }

            @Override
            public void onFailure(int code, @Nullable String reason) {
                Log.e("TAG", "onFailure: on join room "+reason);
                progressDialog.cancel();
                isJoining = false;
                callback.onFailure(code, reason);
            }
        });
    }

    protected void joinFailed(int code, String reason) {
        Toast.makeText(this, "join failed "+reason, Toast.LENGTH_SHORT).show();
        Log.e("TAG", "joinFailed: "+reason);
        Intent intent = getIntent().putExtra(CODE, code).putExtra(REASON, reason);
        setResult(RESULT_CODE, intent);
        finish();
    }

    protected void initView() {
        rcv_videos = findViewById(R.id.rcv_videos);
        layout_im = findViewById(R.id.layout_im);
        shareVideoAreaRtc = findViewById(R.id.share_video_area_rtc);
        imageViewCollapseExpand = findViewById(R.id.iv_float);
        title_view = findViewById(R.id.title_view);
        layout_share_video = findViewById(R.id.layout_share_video);
        roomEntry = getIntent().getParcelableExtra(ROOMENTRY);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Joining into the room...");
        progressDialog.show();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rcv_videos.setLayoutManager(layoutManager);
        rcv_videos.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if (parent.getChildAdapterPosition(view) > 0) {
                    outRect.left = getResources().getDimensionPixelSize(R.dimen.dp_2_5);
                }
            }
        });
        classVideoAdapter = new ClassVideoAdapter(SmallClassActivity.this);
        rcv_videos.setAdapter(classVideoAdapter);
        userListFragment = new UserListFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.layout_chat_room, userListFragment)
                .show(userListFragment)
                .commitNow();

        RteEngineImpl.INSTANCE.setMediaDeviceListener(this);
        RteEngineImpl.INSTANCE.setAudioMixingListener(this);
        RteEngineImpl.INSTANCE.setSpeakerReportListener(this);

        imageViewCollapseExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isSelected = view.isSelected();
                view.setSelected(!isSelected);
                layout_im.setVisibility(isSelected ? View.VISIBLE : View.GONE);
            }
        });
        initData();
    }
    public EduRoom getMainEduRoom() {
        return mainEduRoom;
    }

    public EduRoom getMyMediaRoom() {
        return mainEduRoom;
    }

    public EduUser getLocalUser() {
        if (getMainEduRoom() != null) {
            return getMainEduRoom().getLocalUser();
        }
        return null;
    }


    public EduStreamInfo getLocalCameraStream() {
        return localCameraStream;
    }

    protected List<EduStreamInfo> getCurFullStream() {
        return (getMyMediaRoom() != null) ? getMyMediaRoom().getFullStreamList() : null;
    }

    protected List<EduUserInfo> getCurFullUser() {
        return (getMyMediaRoom() != null) ? getMyMediaRoom().getFullUserList() : null;
    }
    @Override
    protected void onDestroy() {
        /**尝试主动释放TimeView中的handle*/
        if(title_view!=null)
            title_view.setTimeState(false, 0);
        /**退出activity之前释放eduRoom资源*/
        mainEduRoom = null;
        getManager().setEduManagerEventListener(null);
        getManager().release();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        showLeaveDialog();
    }

    public final void showLeaveDialog() {
        ConfirmDialog.normal(getString(R.string.confirm_leave_room_content), new ConfirmDialog.DialogClickListener() {
            @Override
            public void onClick(boolean confirm) {
                if (confirm) {
                    /**退出activity之前离开eduRoom*/
                    if (SmallClassActivity.this.getMainEduRoom() != null) {
                        SmallClassActivity.this.getMainEduRoom().leave();
                        SmallClassActivity.this.finish();
                    }
                }
            }
        }).show(getSupportFragmentManager(), null);
    }

    protected void showFragmentWithJoinSuccess() {
        title_view.setTitle(getMediaRoomName());
    }

    public final void muteLocalAudio(boolean isMute, EduStreamInfo eduStreamInfo) {
        if (localCameraStream != null) {
            switchLocalVideoAudio(getMyMediaRoom(), localCameraStream.getHasVideo(), !isMute, eduStreamInfo);
        }
    }

    public final void muteLocalVideo(boolean isMute, EduStreamInfo eduStreamInfo) {
        if (localCameraStream != null) {
            switchLocalVideoAudio(getMyMediaRoom(), !isMute, localCameraStream.getHasAudio(), eduStreamInfo);
        }
    }

    public final void muteRemoteAudio(boolean isMute, EduStreamInfo eduStreamInfo) {
        switchRemoteVideoAudio(eduStreamInfo.getHasVideo(), !isMute, eduStreamInfo);

    }

    public final void muteRemoteVideo(boolean isMute, EduStreamInfo eduStreamInfo) {
        switchRemoteVideoAudio(!isMute, eduStreamInfo.getHasAudio(), eduStreamInfo);
    }

    private void switchRemoteVideoAudio(boolean openVideo, boolean openAudio, EduStreamInfo eduStreamInfo) {
        EduLocalUserInfoImpl localUserInfo = new EduLocalUserInfoImpl(eduStreamInfo.getPublisher().getUserUuid(), eduStreamInfo.getPublisher().getUserName(), EduUserRole.STUDENT,
                true, null, new ArrayList<EduStreamEvent>(), System.currentTimeMillis());
        final EduStudentImpl localUser = new EduStudentImpl(localUserInfo);
        localUser.eduRoom = new EduRoomImpl(getMyMediaRoom().getRoomInfo(), getMyMediaRoom().getRoomStatus());
        List<EduStreamInfo> eduStreamInfoArrayList = localUser.eduRoom.getCurStreamList();
        eduStreamInfoArrayList.add(eduStreamInfo);
        ((EduUser) localUser).initOrUpdateLocalStream(new LocalStreamInitOptions(eduStreamInfo.getStreamUuid(),
                openVideo, openAudio), new EduCallback<EduStreamInfo>() {
            @Override
            public void onSuccess(@Nullable EduStreamInfo res) {
                localUser.muteStream(res, new EduCallback<Boolean>() {
                    @Override
                    public void onSuccess(@Nullable Boolean res) {
                    }

                    @Override
                    public void onFailure(int code, @Nullable String reason) {
                    }
                });
            }

            @Override
            public void onFailure(int code, @Nullable String reason) {
                Log.e("TAG", "onFailure: ");
            }
        });

    }

    private void switchLocalVideoAudio(final EduRoom room, boolean openVideo, boolean openAudio, EduStreamInfo eduStreamInfo) {
        if (localCameraStream != null) {
            room.getLocalUser().initOrUpdateLocalStream(new LocalStreamInitOptions(localCameraStream.getStreamUuid(),
                    openVideo, openAudio), new EduCallback<EduStreamInfo>() {
                @Override
                public void onSuccess(@Nullable EduStreamInfo res) {
                    room.getLocalUser().muteStream(res, new EduCallback<Boolean>() {
                        @Override
                        public void onSuccess(@Nullable Boolean res) {
                        }

                        @Override
                        public void onFailure(int code, @Nullable String reason) {
                        }
                    });
                }

                @Override
                public void onFailure(int code, @Nullable String reason) {
                }
            });
        }
    }


    private EduRoomInfo getMediaRoomInfo() {
        return getMyMediaRoom().getRoomInfo();
    }

    public final String getMediaRoomName() {
        return getMediaRoomInfo().getRoomName();
    }

    protected int getClassType() {
        return Room.Type.SMALL;
    }

    @Override
    public void onAudioMixingFinished() {
        Log.e("TAG", "onAudioMixingFinished");
    }

    @Override
    public void onAudioMixingStateChanged(int state, int errorCode) {
        Log.e("TAG", "onAudioMixingStateChanged->state:" + state + ",errorCode:" + errorCode);
    }

    @Override
    public void onAudioRouteChanged(int routing) {
        Log.e("TAG", "onAudioRouteChanged->routing:" + routing);
    }

    @Override
    public void onAudioVolumeIndicationOfLocalSpeaker(@Nullable IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume) {
        Log.e("TAG", "onAudioVolumeIndicationOfLocalSpeaker->totalVolume:" + totalVolume);
    }
    @Override
    public void onAudioVolumeIndicationOfRemoteSpeaker(@Nullable IRtcEngineEventHandler.AudioVolumeInfo[] speakers, int totalVolume) {
        Log.e("TAG", "onAudioVolumeIndicationOfRemoteSpeaker->totalVolume:" + totalVolume);
    }

    @Override
    public void onRtcStats(@Nullable RtcChannel channel, @Nullable IRtcEngineEventHandler.RtcStats stats) {
        Log.e("TAG", "onRtcStats->stats:" + stats.rxKBitRate);
    }

    @Override
    public void onVideoSizeChanged(@Nullable RtcChannel channel, int uid, int width, int height, int rotation) {
        Log.e("TAG", "onVideoSizeChanged->uid:" + uid + ",width:" + width + ",height:" + height + ",rotation:" + rotation);
    }
    public void renderStream(final EduRoom room, final EduStreamInfo eduStreamInfo, @Nullable final ViewGroup viewGroup) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                room.getLocalUser().setStreamView(eduStreamInfo,
                        room.getRoomInfo().getRoomUuid(), viewGroup);
            }
        });
    }

    @Override
    public void onRemoteUsersInitialized(@NotNull List<? extends EduUserInfo> users, @NotNull EduRoom classRoom) {

        RteEngineImpl.INSTANCE.setStatisticsReportListener(classRoom.getRoomInfo().getRoomUuid(), this);
        title_view.setTitle(String.format(Locale.getDefault(), "%s", getMediaRoomName()));
    }

    @Override
    public void onRemoteUsersJoined(@NotNull List<? extends EduUserInfo> users, @NotNull EduRoom classRoom) {
        EduRoomStatus roomStatus = getMyMediaRoom().getRoomStatus();
        title_view.setTimeState(roomStatus.getCourseState() == EduRoomState.START,
                System.currentTimeMillis() - roomStatus.getStartTime());
        title_view.setTitle(String.format(Locale.getDefault(), "%s", getMediaRoomName()));
    }

    @Override
    public void onRemoteUserLeft(@NotNull EduUserEvent userEvent, @NotNull EduRoom classRoom) {
        title_view.setTitle(String.format(Locale.getDefault(), "%s", getMediaRoomName()));
    }
    @Override
    public void onRemoteUserUpdated(@NotNull EduUserEvent userEvent, @NotNull EduUserStateChangeType type,
                                    @NotNull EduRoom classRoom) {
    }

    @Override
    public void onRoomMessageReceived(@NotNull EduMsg message, @NotNull EduRoom classRoom) {
    }

    @Override
    public void onUserMessageReceived(@NotNull EduMsg message) {
    }

    @Override
    public void onRoomChatMessageReceived(@NotNull EduChatMsg eduChatMsg, @NotNull EduRoom classRoom) {

    }
    @Override
    public void onUserChatMessageReceived(@NotNull EduChatMsg chatMsg) {

    }
    @Override
    public void onRemoteStreamsInitialized(@NotNull List<? extends EduStreamInfo> streams, @NotNull EduRoom classRoom) {
        Log.e("TAG", "onRemoteStreamsInitialized: small class" );
        for (final EduStreamInfo streamInfo : streams) {
            switch (streamInfo.getVideoSourceType()) {
                case SCREEN:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            layout_share_video.setVisibility(View.VISIBLE);
                            layout_share_video.removeAllViews();
                            SmallClassActivity.this.renderStream(SmallClassActivity.this.getMainEduRoom(), streamInfo, layout_share_video);
                        }
                    });
                    break;
                default:
                    break;
            }
        }
        userListFragment.setLocalUserUuid(classRoom.getLocalUser().getUserInfo().getUserUuid());
        userListFragment.setUserList(getCurFullStream());
        showVideoList(getCurFullStream());
    }
    @Override
    public void onRemoteStreamsAdded(@NotNull List<EduStreamEvent> streamEvents, @NotNull EduRoom classRoom) {
        Log.e("TAG", "onRemoteStreamsAdded: small class " );
        for (EduStreamEvent streamEvent : streamEvents) {
            final EduStreamInfo streamInfo = streamEvent.getModifiedStream();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    layout_share_video.setVisibility(View.VISIBLE);
                    layout_share_video.removeAllViews();
                    SmallClassActivity.this.renderStream(SmallClassActivity.this.getMainEduRoom(), streamInfo, layout_share_video);
                }
            });
        }
        boolean notify = false;
        for (EduStreamEvent streamEvent : streamEvents) {
            EduStreamInfo streamInfo = streamEvent.getModifiedStream();
            if (streamInfo.getVideoSourceType() == VideoSourceType.CAMERA) {
                notify = true;
            }
        }
        if (notify) {
            Log.e("TAG", "onRemoteStreamsAdded: " );
            showVideoList(getCurFullStream());
        }
        userListFragment.setUserList(getCurFullStream());
    }
    @Override
    public void onRemoteStreamUpdated(@NotNull EduStreamEvent streamEvent,
                                      @NotNull EduStreamStateChangeType type, @NotNull EduRoom classRoom) {
        final EduStreamInfo streamInfo = streamEvent.getModifiedStream();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                layout_share_video.setVisibility(View.VISIBLE);
                layout_share_video.removeAllViews();
                SmallClassActivity.this.renderStream(SmallClassActivity.this.getMainEduRoom(), streamInfo, layout_share_video);
            }
        });
        boolean notify = false;
        if (streamInfo.getVideoSourceType() == VideoSourceType.CAMERA) {
            notify = true;
        }
        if (notify) {
            Log.e("TAG", "onRemoteStreamUpdated: " );
            showVideoList(getCurFullStream());
        }
        userListFragment.setUserList(getCurFullStream());
    }
    @Override
    public void onRemoteStreamsRemoved(@NotNull List<EduStreamEvent> streamEvents, @NotNull EduRoom classRoom) {
        for (EduStreamEvent streamEvent : streamEvents) {
            final EduStreamInfo streamInfo = streamEvent.getModifiedStream();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    layout_share_video.setVisibility(View.GONE);
                    layout_share_video.removeAllViews();
                    SmallClassActivity.this.renderStream(SmallClassActivity.this.getMainEduRoom(), streamInfo, null);
                }
            });
        }
        boolean notify = false;
        for (EduStreamEvent streamEvent : streamEvents) {
            EduStreamInfo streamInfo = streamEvent.getModifiedStream();
            if (streamInfo.getVideoSourceType() == VideoSourceType.CAMERA) {
                notify = true;
            }
        }
        if (notify) {
            Log.e("TAG", "onRemoteStreamsRemoved: " );
            showVideoList(getCurFullStream());
        }
        userListFragment.setUserList(getCurFullStream());
    }
    @Override
    public void onRoomStatusChanged(@NotNull EduRoomChangeType event, @NotNull EduUserInfo operatorUser, @NotNull EduRoom classRoom) {
        Log.e("TAG", "onRoomStatusChanged: " );
        EduRoomStatus roomStatus = classRoom.getRoomStatus();
        if (event == EduRoomChangeType.CourseState) {
            title_view.setTimeState(roomStatus.getCourseState() == EduRoomState.START,
                    System.currentTimeMillis() - roomStatus.getStartTime());
        }
//        EduRoomStatus roomStatus = classRoom.getRoomStatus();
//        switch (event) {
//            case COURSE_STATE:
//                title_view.setTimeState(roomStatus.getCourseState() == EduRoomState.START,
//                        System.currentTimeMillis() - roomStatus.getStartTime());
//                break;
//            default:
//                break;
//        }

    }
    @Override
    public void onRoomPropertyChanged(@NotNull EduRoom classRoom, @Nullable Map<String, Object> cause) {
    }

    @Override
    public void onRemoteUserPropertyUpdated(@NotNull EduUserInfo userInfo, @NotNull EduRoom classRoom,
                                            @Nullable Map<String, Object> cause) {
    }

    @Override
    public void onNetworkQualityChanged(@NotNull NetworkQuality quality, @NotNull EduUserInfo user,
                                        @NotNull EduRoom classRoom) {
        title_view = findViewById(R.id.title_view);
        if(title_view!=null)
        title_view.setNetworkQuality(quality);
    }

    @Override
    public void onConnectionStateChanged(@NotNull ConnectionState state, @NotNull EduRoom classRoom) {

    }
    @Override
    public void onLocalUserUpdated(@NotNull EduUserEvent userEvent, @NotNull EduUserStateChangeType type) {
        showVideoList(getCurFullStream());
        userListFragment.updateLocalStream(getLocalCameraStream());
        userListFragment.setUserList(getCurFullStream());
    }

    @Override
    public void onLocalUserPropertyUpdated(@NotNull EduUserInfo userInfo, @Nullable Map<String, Object> cause) {
    }

    @Override
    public void onLocalStreamAdded(@NotNull EduStreamEvent streamEvent) {
        switch (streamEvent.getModifiedStream().getVideoSourceType()) {
            case CAMERA:
                localCameraStream = streamEvent.getModifiedStream();
                Log.e("TAG", "Camera");
                break;
            case SCREEN:
                localScreenStream = streamEvent.getModifiedStream();
                break;
            default:
                break;
        }
        showVideoList(getCurFullStream());
        userListFragment.updateLocalStream(getLocalCameraStream());
        userListFragment.setUserList(getCurFullStream());
    }

    @Override
    public void onLocalStreamUpdated(@NotNull EduStreamEvent streamEvent, @NotNull EduStreamStateChangeType type) {
        switch (streamEvent.getModifiedStream().getVideoSourceType()) {
            case CAMERA:
                localCameraStream = streamEvent.getModifiedStream();
                break;
            case SCREEN:
                localScreenStream = streamEvent.getModifiedStream();
                break;
            default:
                break;
        }
        showVideoList(getCurFullStream());
        userListFragment.updateLocalStream(getLocalCameraStream());
        userListFragment.setUserList(getCurFullStream());
    }

    @Override
    public void onLocalStreamRemoved(@NotNull EduStreamEvent streamEvent) {
        Log.e("TAG", "onLocalStreamRemoved: " );
        switch (streamEvent.getModifiedStream().getVideoSourceType()) {
            case CAMERA:
                localCameraStream = null;
                break;
            case SCREEN:
                localScreenStream = null;
                break;
            default:
                break;
        }
    }

    @Override
    public void onUserActionMessageReceived(@NotNull EduActionMessage actionMessage) {
        Log.e("TAG", "action->" + new Gson().toJson(actionMessage));
    }

    private void showVideoList(final List<EduStreamInfo> list) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < list.size(); i++) {
                    EduStreamInfo streamInfo = list.get(i);
                    if (streamInfo.getPublisher().getRole().equals(EduUserRole.TEACHER)) {
                        /*隐藏老师的占位布局*/
                        if (i != 0) {
                            Collections.swap(list, 0, i);
                        }
                        classVideoAdapter.setNewList(list);
                        return;
                    }
                }
                /*显示老师的占位布局*/
                classVideoAdapter.setNewList(list);
            }
        });
    }

}
