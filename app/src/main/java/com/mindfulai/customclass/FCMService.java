package com.mindfulai.customclass;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mindfulai.Activites.MainActivity;
import com.mindfulai.Models.NotificationModel;
import com.mindfulai.Utils.GlobalEnum;
import com.mindfulai.ministore.R;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FCMService extends FirebaseMessagingService {
    public static String tag = null;
    String type;
    String Id = "fcm_default_channel";
    Intent intent;
    private ArrayList<NotificationModel> notificationModelArrayList;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        loadData(remoteMessage);

        tag = remoteMessage.getData().get("tag");
        type = remoteMessage.getData().get("type");
        intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        String imageUri = remoteMessage.getData().get("image");
        Bitmap bitmap = getBitmapfromUri(GlobalEnum.AMAZON_URL+imageUri);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, Id);
        if (bitmap != null) {
            builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).setBigContentTitle(remoteMessage.getData().get("title")).setSummaryText(remoteMessage.getData().get("body")));
        } else
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get("body")).setBigContentTitle(remoteMessage.getData().get("title")));

        builder.setFullScreenIntent(pendingIntent, true);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setVibrate(new long[]{10000, 10000, 10000, 10000, 10000});
        builder.setLights(Color.RED, 10000, 10000);
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(Id, "Default channel", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }
        manager.notify(0, builder.build());
    }

    private Bitmap getBitmapfromUri(String imageUri) {
        URL url;
        HttpURLConnection connection;
        InputStream input;
        Bitmap bitmap = null;
        try {
            url = new URL(imageUri);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", "getBitmapfromUri: " + e);
        }
        return bitmap;
    }
    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(notificationModelArrayList);
        editor.putString("notification list", json);
        editor.apply();
    }

    private void loadData(RemoteMessage remoteMessage) {
    SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
    Gson gson = new Gson();
    String json = sharedPreferences.getString("notification list", "");
    Type type = new TypeToken<ArrayList<NotificationModel>>() {}.getType();
    notificationModelArrayList = gson.fromJson(json, type);
    if (notificationModelArrayList == null) {
        notificationModelArrayList = new ArrayList<>();
     }
    Map<String ,String> hashMap = remoteMessage.getData();

    if(hashMap.get("image")!=null)
       notificationModelArrayList.add(new NotificationModel(hashMap.get("tag"),hashMap.get("type"),hashMap.get("image"),hashMap.get("title"),hashMap.get("title")));
   else
       notificationModelArrayList.add(new NotificationModel(hashMap.get("tag"),hashMap.get("type"),"",hashMap.get("title"),hashMap.get("title")));

        saveData();
    }
}