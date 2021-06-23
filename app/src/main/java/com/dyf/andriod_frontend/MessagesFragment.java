package com.dyf.andriod_frontend;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.dyf.andriod_frontend.message.MessageA;
import com.dyf.andriod_frontend.message.MessageAdapter;
import com.dyf.andriod_frontend.utils.HttpRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import android.os.Handler;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessagesFragment extends Fragment {
    private MessageAdapter messageAdapter;
    private LinkedList<MessageA> data;
    private ListView listView;
    private static final int TAKE_PHOTO_REQUEST_TWO = 444;
    private static final int TAKE_PHOTO_REQUEST_ONE = 333;
    private static final int TAKE_PHOTO_REQUEST_THREE = 555;
    private Uri imageUri;
    private ImageView iv_image;
    private ImageView iv_VideoImage;
    private String tv_VideoPath;
    private String tv_VideoDuration;
    private String tv_VideoSize;
    private String tv_VideoTitle;
    private int chat_type;
    private MediaRecorderUtils mMediaRecorderUtils;
    public LocationClient mLocationClient;
    private String positionText;
    private MapView mapView;
    private BaiduMap baiduMap;
    private boolean isFirstLocate = true;
    private String talkToId;
    private String talkToName;
    private Handler handler_chats;
    private Handler handler_group_chats;
    private Handler handler_images;
    private Handler handler_audio;
    private Handler handler_video;
    public MainActivity mainActivity;
    private String filepath;
    private String filename;

    @BindView(R.id.bottomNavigationView)
    public BottomNavigationView bottomNavigationView;
    private MessagesMessageReceiver messagesMessageReceiver;

    private class MessagesMessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String message=intent.getStringExtra("message");
            try {
                JSONArray json_contact = new JSONArray(message);
//                Log.d("123", json_contact.toString());
                if(chat_type == 0) {
                    if (json_contact.getJSONObject(0).getString("messageType").equals("TEXT")) {
                        sendNotificationOfNewMessage(talkToName, json_contact.getJSONObject(0).getString("content"), 0);
                        if(json_contact.getJSONObject(0).getJSONObject("fromUser").getString("username").equals(talkToName)) {
                            data.add(new MessageA(talkToName, R.drawable.contacts_6, json_contact.getJSONObject(0).getString("content"), 0));
                            messageAdapter.notifyDataSetChanged();
                        }
                    }
                    else if (json_contact.getJSONObject(0).getString("messageType").equals("PHOTO")) {
                        sendNotificationOfNewMessage(talkToName, json_contact.getJSONObject(0).getString("content"), 1);
                        if(json_contact.getJSONObject(0).getJSONObject("fromUser").getString("username").equals(talkToName)) {
                            data.add(new MessageA(talkToName, R.drawable.contacts_6, 2, json_contact.getJSONObject(0).getString("content")));
                            messageAdapter.notifyDataSetChanged();
                        }
                    }
                    else if (json_contact.getJSONObject(0).getString("messageType").equals("VIDEO")) {
                        sendNotificationOfNewMessage(talkToName, json_contact.getJSONObject(0).getString("content"), 2);
                        if(json_contact.getJSONObject(0).getJSONObject("fromUser").getString("username").equals(talkToName)) {
                            data.add(new MessageA(talkToName, R.drawable.contacts_6, 3, json_contact.getJSONObject(0).getString("content")));
                            messageAdapter.notifyDataSetChanged();
                        }
                    }
                    else if (json_contact.getJSONObject(0).getString("messageType").equals("AUDIO")) {
                        sendNotificationOfNewMessage(talkToName, json_contact.getJSONObject(0).getString("content"), 3);
                        if(json_contact.getJSONObject(0).getJSONObject("fromUser").getString("username").equals(talkToName)) {
                            data.add(new MessageA(talkToName, R.drawable.contacts_6, 6, json_contact.getJSONObject(0).getString("content")));
                            messageAdapter.notifyDataSetChanged();
                        }
                    }
                }
                else if(chat_type == 1)
                {
                    SharedPreferences sp = mainActivity.getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
                    String username = sp.getString("username", "");
                    if(json_contact.getJSONObject(0).getString("messageType").equals("TEXT")) {
                        sendNotificationOfNewMessage(talkToName, json_contact.getJSONObject(0).getString("content"), 0);
                        if(json_contact.getJSONObject(0).getJSONObject("group").getString("id").equals(talkToId) && !json_contact.getJSONObject(0).getJSONObject("fromUserId").getString("username").equals(username))
                        {
                            data.add(new MessageA(json_contact.getJSONObject(0).getJSONObject("fromUserId").getString("username"), R.drawable.contacts_1, json_contact.getJSONObject(0).getString("content"), 0));
                            messageAdapter.notifyDataSetChanged();
                        }
                    }
                    else if(json_contact.getJSONObject(0).getString("messageType").equals("PHOTO")) {
                        sendNotificationOfNewMessage(talkToName, json_contact.getJSONObject(0).getString("content"), 1);
                        if(json_contact.getJSONObject(0).getJSONObject("group").getString("id").equals(talkToId) && !json_contact.getJSONObject(0).getJSONObject("fromUserId").getString("username").equals(username))
                        {
                            data.add(new MessageA(json_contact.getJSONObject(0).getJSONObject("fromUserId").getString("username"), R.drawable.contacts_1, 2, json_contact.getJSONObject(0).getString("content")));
                            messageAdapter.notifyDataSetChanged();
                        }
                    }
                    else if(json_contact.getJSONObject(0).getString("messageType").equals("VIDEO")) {
                        sendNotificationOfNewMessage(talkToName, json_contact.getJSONObject(0).getString("content"), 2);
                        if(json_contact.getJSONObject(0).getJSONObject("group").getString("id").equals(talkToId) && !json_contact.getJSONObject(0).getJSONObject("fromUserId").getString("username").equals(username))
                        {
                            data.add(new MessageA(json_contact.getJSONObject(0).getJSONObject("fromUserId").getString("username"), R.drawable.contacts_1, 4, json_contact.getJSONObject(0).getString("content")));
                            messageAdapter.notifyDataSetChanged();
                        }
                    }
                    else if(json_contact.getJSONObject(0).getString("messageType").equals("AUDIO")) {
                        sendNotificationOfNewMessage(talkToName, json_contact.getJSONObject(0).getString("content"), 3);
                        if(json_contact.getJSONObject(0).getJSONObject("group").getString("id").equals(talkToId) && !json_contact.getJSONObject(0).getJSONObject("fromUserId").getString("username").equals(username))
                        {
                            data.add(new MessageA(json_contact.getJSONObject(0).getJSONObject("fromUserId").getString("username"), R.drawable.contacts_1, 6, json_contact.getJSONObject(0).getString("content")));
                            messageAdapter.notifyDataSetChanged();
                        }
                    }
                }
//                id = new String(json_contact.getJSONObject(0).getJSONObject("sentUser").get("id").toString());
            } catch (JSONException e) {
            }
        }

        private void sendNotificationOfNewMessage(String name, String Content, int type) {
            Intent intent = new Intent(getContext(), NotificationActivity.class);
            PendingIntent pi = PendingIntent.getActivities(getContext(), 0, new Intent[]{intent}, 0);
            NotificationManager manager = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
            // 兼容  API 26，Android 8.0
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // 第三个参数表示通知的重要程度，默认则只在通知栏闪烁一下
                NotificationChannel notificationChannel = new NotificationChannel("AppTestNotificationId", "AppTestNotificationName", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setShowBadge(true);
                notificationChannel.enableVibration(true);
                // 注册通道，注册后除非卸载再安装否则不改变
                manager.createNotificationChannel(notificationChannel);
            }
            if(type==0) {
                Notification notification = new Notification.Builder(getContext())
                        .setContentTitle(name)
                        .setContentText(Content)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_play)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_play))
                        .setContentIntent(pi)
                        .setAutoCancel(true) // 取消通知
                        .setSound(Uri.fromFile(new File("/system/media/audio/notifications/Simple.ogg"))) // 通知铃声
                        //        .setSound(Uri.fromFile(new File("/system/media/audio/ringtones/Luna.ogg")))
                        .setVibrate(new long[]{0, 1000, 1000, 1000})
                        .setLights(Color.GREEN, 1000, 1000) // LED灯
                        .setChannelId("AppTestNotificationId")
                        //        .setLights(Color.GREEN, 1000, 1000)
                        .setDefaults(Notification.DEFAULT_ALL)
                        //        .setStyle(new NotificationCompat.BigTextStyle().bigText("Learn how to build notifications, send and sync data, and use voice actions. Get the official Android IDE and developer tools to build apps for Android."))
//                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.contacts_6)))
                        .setPriority(Notification.PRIORITY_MAX)
                        .build();
                manager.notify(1, notification);
            }
            else if(type==1) {
                Notification notification = new Notification.Builder(getContext())
                        .setContentTitle(name)
                        .setContentText("[图片]")
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_play)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_play))
                        .setContentIntent(pi)
                        .setAutoCancel(true) // 取消通知
                        .setSound(Uri.fromFile(new File("/system/media/audio/notifications/Simple.ogg"))) // 通知铃声
                        //        .setSound(Uri.fromFile(new File("/system/media/audio/ringtones/Luna.ogg")))
                        .setVibrate(new long[]{0, 1000, 1000, 1000})
                        .setLights(Color.GREEN, 1000, 1000) // LED灯
                        .setChannelId("AppTestNotificationId")
                        //        .setLights(Color.GREEN, 1000, 1000)
                        .setDefaults(Notification.DEFAULT_ALL)
                        //        .setStyle(new NotificationCompat.BigTextStyle().bigText("Learn how to build notifications, send and sync data, and use voice actions. Get the official Android IDE and developer tools to build apps for Android."))
//                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.contacts_6)))
                        .setPriority(Notification.PRIORITY_MAX)
                        .build();
                manager.notify(1, notification);
            }
            else if(type==2) {
                Notification notification = new Notification.Builder(getContext())
                        .setContentTitle(name)
                        .setContentText("[视频]")
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_play)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_play))
                        .setContentIntent(pi)
                        .setAutoCancel(true) // 取消通知
                        .setSound(Uri.fromFile(new File("/system/media/audio/notifications/Simple.ogg"))) // 通知铃声
                        //        .setSound(Uri.fromFile(new File("/system/media/audio/ringtones/Luna.ogg")))
                        .setVibrate(new long[]{0, 1000, 1000, 1000})
                        .setLights(Color.GREEN, 1000, 1000) // LED灯
                        .setChannelId("AppTestNotificationId")
                        //        .setLights(Color.GREEN, 1000, 1000)
                        .setDefaults(Notification.DEFAULT_ALL)
                        //        .setStyle(new NotificationCompat.BigTextStyle().bigText("Learn how to build notifications, send and sync data, and use voice actions. Get the official Android IDE and developer tools to build apps for Android."))
//                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.contacts_6)))
                        .setPriority(Notification.PRIORITY_MAX)
                        .build();
                manager.notify(1, notification);
            }
            else if(type==3) {
                Notification notification = new Notification.Builder(getContext())
                        .setContentTitle(name)
                        .setContentText("[音频]")
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_play)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_play))
                        .setContentIntent(pi)
                        .setAutoCancel(true) // 取消通知
                        .setSound(Uri.fromFile(new File("/system/media/audio/notifications/Simple.ogg"))) // 通知铃声
                        //        .setSound(Uri.fromFile(new File("/system/media/audio/ringtones/Luna.ogg")))
                        .setVibrate(new long[]{0, 1000, 1000, 1000})
                        .setLights(Color.GREEN, 1000, 1000) // LED灯
                        .setChannelId("AppTestNotificationId")
                        //        .setLights(Color.GREEN, 1000, 1000)
                        .setDefaults(Notification.DEFAULT_ALL)
                        //        .setStyle(new NotificationCompat.BigTextStyle().bigText("Learn how to build notifications, send and sync data, and use voice actions. Get the official Android IDE and developer tools to build apps for Android."))
//                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.contacts_6)))
                        .setPriority(Notification.PRIORITY_MAX)
                        .build();
                manager.notify(1, notification);
            }
            else if(type==4) {
                Notification notification = new Notification.Builder(getContext())
                        .setContentTitle(name)
                        .setContentText("[位置]")
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_play)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_play))
                        .setContentIntent(pi)
                        .setAutoCancel(true) // 取消通知
                        .setSound(Uri.fromFile(new File("/system/media/audio/notifications/Simple.ogg"))) // 通知铃声
                        //        .setSound(Uri.fromFile(new File("/system/media/audio/ringtones/Luna.ogg")))
                        .setVibrate(new long[]{0, 1000, 1000, 1000})
                        .setLights(Color.GREEN, 1000, 1000) // LED灯
                        .setChannelId("AppTestNotificationId")
                        //        .setLights(Color.GREEN, 1000, 1000)
                        .setDefaults(Notification.DEFAULT_ALL)
                        //        .setStyle(new NotificationCompat.BigTextStyle().bigText("Learn how to build notifications, send and sync data, and use voice actions. Get the official Android IDE and developer tools to build apps for Android."))
//                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(), R.drawable.contacts_6)))
                        .setPriority(Notification.PRIORITY_MAX)
                        .build();
                manager.notify(1, notification);
            }
        }
    }

    private void doRegisterReceiver() {
        messagesMessageReceiver = new MessagesMessageReceiver();
        IntentFilter filter = new IntentFilter("com.dyf.servicecallback.content");
        getActivity().registerReceiver(messagesMessageReceiver, filter);
    }

    public MessagesFragment() {
        // Required empty public constructor
//        chat_type = type;
    }

    public void setInfo(int type, String id, String name) {
        this.chat_type = type;
        this.talkToId = new String(id);
        this.talkToName = new String(name);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessagesFragment newInstance() {
        com.dyf.andriod_frontend.MessagesFragment fragment = new com.dyf.andriod_frontend.MessagesFragment();
        return fragment;
    }

    /**
     * 权限申请
     */
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE};
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), permissions, 200);
                    return;
                }
            }
        }
    }

    public class MyLocationListener extends BDAbstractLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            JSONObject positionJson = new JSONObject();
            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append("纬度：").append(location.getLatitude()).append("\n");
            currentPosition.append("经线：").append(location.getLongitude()).append("\n");
            currentPosition.append("国家：").append(location.getCountry()).append("\n");
            currentPosition.append("省：").append(location.getProvince()).append("\n");
            currentPosition.append("市：").append(location.getCity()).append("\n");
            currentPosition.append("区：").append(location.getDistrict()).append("\n");
            currentPosition.append("街道：").append(location.getStreet()).append("\n");
            currentPosition.append("定位方式：");
            try {
                positionJson.put("latitude", location.getLatitude());
                positionJson.put("longitude", location.getLongitude());
                positionJson.put("place", location.getCountry()+" "+location.getProvince()+" "+location.getCity()+" "+location.getDistrict());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                currentPosition.append("GPS");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                currentPosition.append("网络");
            }
            positionText = positionJson.toString();
//            Log.d("GPS", String.valueOf(currentPosition));
//            if (location.getLocType() == BDLocation.TypeGpsLocation
//                    || location.getLocType() == BDLocation.TypeNetWorkLocation) {
//                navigateTo(location);
//            }
        }

    }

    private void navigateTo(BDLocation location) {
        if (isFirstLocate) {
            Toast.makeText(getActivity(), "nav to " + location.getAddrStr(), Toast.LENGTH_SHORT).show();
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.
                Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
    }

    private void requestLocation() {
        initLocation();
        mLocationClient.start();
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        mLocationClient.setLocOption(option);
    }

//    @SuppressLint("CheckResult")
//    private void checkVersion() {
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
//            RxPermissions rxPermissions = new RxPermissions(getActivity());
//            rxPermissions.request(Manifest.permission.ACCESS_COARSE_LOCATION,
//                    Manifest.permission.ACCESS_FINE_LOCATION,
//                    Manifest.permission.READ_PHONE_STATE,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                    .subscribe(granted -> {
//                        if (granted) {//申请成功
//                            //发起连续定位请求
//                            initLocation();// 定位初始化
//                        } else {//申请失败
//                            Toast.makeText(getActivity(),"权限未开启",Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        }else {
//            initLocation();// 定位初始化
//        }
//    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        bottomNavigationView = (MainActivity) getActivity().getMenu();
//        bottomNavigationView.animate().translationY(bottomNavigationView.getHeight());
//        getActivity().getWindow().setNavigationBarColor(Color.TRANSPARENT);
        mainActivity = (MainActivity ) getActivity();
        doRegisterReceiver();
        mLocationClient = new LocationClient(getActivity().getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getActivity().getApplicationContext());
////        baiduMap = mapView.getMap();
////        baiduMap.setMyLocationEnabled(true);
////        checkVersion();
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
//        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            permissionList.add(Manifest.permission.READ_PHONE_STATE);
//        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(getActivity(), permissions, 1);
        }
        else {
            requestLocation();
        }


        mMediaRecorderUtils = new MediaRecorderUtils.Builder(getActivity())
                .setAudioSource(MediaRecorder.AudioSource.MIC)//麦克
                .setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB)//AMR
                .setOutputFormat(MediaRecorder.OutputFormat.AMR_WB)//AMR
                .setDecibelSpace(500)//获取分贝的间隔
                .build();
        mMediaRecorderUtils.setMaximum(60);
        listView = getView().findViewById(R.id.message_listview);
        Context context = getActivity();
        Button title_back = getActivity().findViewById(R.id.title_back);
        title_back.setVisibility(View.VISIBLE);
        Button title_back_2 = getActivity().findViewById(R.id.title_back2);
        title_back_2.setVisibility(View.VISIBLE);
        listView.setDivider(null);
        listView.setSelector(android.R.color.transparent);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        data = new LinkedList<>();
        TextView title_text = getActivity().findViewById(R.id.title_text);

        if(chat_type == 0) {

            HashMap<String, String> params = new HashMap<>();
            params.put("talkToUserId", talkToId);

            HttpRequest.sendOkHttpPostRequest("chat/get", new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Looper.prepare();
                    Toast.makeText(getActivity().getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String resStr = response.body().string();
                    if (resStr.charAt(resStr.length()-1) != '}'){
                        resStr = resStr + "}";
                    }
                    Log.e("response", resStr);
                    try {
                        JSONObject jsonObject = new JSONObject(resStr);
                        if (jsonObject.getBoolean("success")) {
                            JSONArray messages = jsonObject.getJSONArray("messages");
                            for (int j = 0; j < messages.length(); j++) {
                                int k = 0;
                                if(messages.getJSONObject(j).getString("messageType").equals("TEXT")) {
                                    if (messages.getJSONObject(j).getJSONObject("fromUser").getString("username").equals(talkToName)) {
                                        k = 0;
                                    } else {
                                        k = 1;
                                    }
                                    data.add(new MessageA(messages.getJSONObject(j).getJSONObject("fromUser").getString("username"), R.drawable.contacts_6, messages.getJSONObject(j).getString("content"), k));
                                }
                                else if(messages.getJSONObject(j).getString("messageType").equals("PHOTO")) {
                                    if (messages.getJSONObject(j).getJSONObject("fromUser").getString("username").equals(talkToName)) {
                                        k = 2;
                                    } else {
                                        k = 3;
                                    }
                                    data.add(new MessageA(messages.getJSONObject(j).getJSONObject("fromUser").getString("username"), R.drawable.contacts_6, k, messages.getJSONObject(j).getString("content")));
                                }
                                else if(messages.getJSONObject(j).getString("messageType").equals("VIDEO")) {
                                    if (messages.getJSONObject(j).getJSONObject("fromUser").getString("username").equals(talkToName)) {
                                        k = 4;
                                    } else {
                                        k = 5;
                                    }
                                    data.add(new MessageA(messages.getJSONObject(j).getJSONObject("fromUser").getString("username"), R.drawable.contacts_6, k, messages.getJSONObject(j).getString("content")));
                                }
                                else if(messages.getJSONObject(j).getString("messageType").equals("AUDIO")) {
                                    if (messages.getJSONObject(j).getJSONObject("fromUser").getString("username").equals(talkToName)) {
                                        k = 6;
                                    } else {
                                        k = 7;
                                    }
                                    data.add(new MessageA(messages.getJSONObject(j).getJSONObject("fromUser").getString("username"), R.drawable.contacts_6, k, messages.getJSONObject(j).getString("content")));
                                }
                            }
                            handler_chats.sendEmptyMessage(1);
                        } else {
                            Looper.prepare();
                            Toast.makeText(getActivity().getApplicationContext(), "好友消息获取失败", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            handler_chats.sendEmptyMessage(1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Looper.prepare();
                        Toast.makeText(getActivity().getApplicationContext(), R.string.json_parse_error, Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }
            }, params);

            handler_chats = new Handler() {
                @SuppressLint("HandlerLeak")
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    messageAdapter = new MessageAdapter(data, context);
                    listView.setAdapter(messageAdapter);
                }
            };
        }

        else if(chat_type == 1)
        {
            HashMap<String, String> params = new HashMap<>();

            HttpRequest.sendOkHttpPostRequest("group/get", new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Looper.prepare();
                    Toast.makeText(getActivity().getApplicationContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String resStr = response.body().string();
                    Log.e("response", resStr);
                    try {
                        JSONObject jsonObject = new JSONObject(resStr);
                        if (jsonObject.getBoolean("success")) {
                            JSONArray groups = jsonObject.getJSONArray("groups");
                            int index=0;
                            for(int i=0;i<groups.length();i++) {
                                if(groups.getJSONObject(i).getString("id").equals(talkToId))
                                {
                                    index = i;
                                }
                            }
                            try {
                                SharedPreferences sp = mainActivity.getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
                                String username = sp.getString("username", "");
                                JSONArray groupMessages = groups.getJSONObject(index).getJSONArray("groupMessages");
                                for(int j=0;j<groupMessages.length();j++)
                                {
                                    int k = 0;
                                    if(groupMessages.getJSONObject(j).getString("messageType").equals("TEXT")) {
                                        if (groupMessages.getJSONObject(j).getJSONObject("sendUser").getString("username").equals(username)) {
                                            k = 1;
                                        } else {
                                            k = 0;
                                        }
                                        data.add(new MessageA(groupMessages.getJSONObject(j).getJSONObject("sendUser").getString("username"), R.drawable.contacts_6, groupMessages.getJSONObject(j).getString("content"), k));
                                    }
                                    else if(groupMessages.getJSONObject(j).getString("messageType").equals("PHOTO")) {
                                        if (groupMessages.getJSONObject(j).getJSONObject("sendUser").getString("username").equals(talkToName)) {
                                            k = 2;
                                        } else {
                                            k = 3;
                                        }
                                        data.add(new MessageA(groupMessages.getJSONObject(j).getJSONObject("sendUser").getString("username"), R.drawable.contacts_6, k, groupMessages.getJSONObject(j).getString("content")));
                                    }
                                    else if(groupMessages.getJSONObject(j).getString("messageType").equals("VIDEO")) {
                                        if (groupMessages.getJSONObject(j).getJSONObject("sendUser").getString("username").equals(talkToName)) {
                                            k = 4;
                                        } else {
                                            k = 5;
                                        }
                                        data.add(new MessageA(groupMessages.getJSONObject(j).getJSONObject("sendUser").getString("username"), R.drawable.contacts_6, k, groupMessages.getJSONObject(j).getString("content")));
                                    }
                                    else if(groupMessages.getJSONObject(j).getString("messageType").equals("AUDIO")) {
                                        if (groupMessages.getJSONObject(j).getJSONObject("sendUser").getString("username").equals(talkToName)) {
                                            k = 6;
                                        } else {
                                            k = 7;
                                        }
                                        data.add(new MessageA(groupMessages.getJSONObject(j).getJSONObject("sendUser").getString("username"), R.drawable.contacts_6, k, groupMessages.getJSONObject(j).getString("content")));
                                    }
                                }
                            }
                            catch (JSONException e)
                            {

                            }
                            handler_group_chats.sendEmptyMessage(1);
                        } else {
                            Looper.prepare();
                            Toast.makeText(getActivity().getApplicationContext(), "群聊消息获取失败", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            handler_group_chats.sendEmptyMessage(1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Looper.prepare();
                        Toast.makeText(getActivity().getApplicationContext(), R.string.json_parse_error, Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }
                }
            }, params);

            handler_group_chats = new Handler() {
                @SuppressLint("HandlerLeak")
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    messageAdapter = new MessageAdapter(data, context);
                    listView.setAdapter(messageAdapter);
                }
            };
        }


        EditText edit_text = getActivity().findViewById(R.id.editTextTextPersonName);
        edit_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEND){
                    InputMethodManager imm = (InputMethodManager) v
                            .getContext().getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(
                                v.getApplicationWindowToken(), 0);
                    }
                    mainActivity = (MainActivity ) getActivity();
                    if(chat_type == 0) {
                        JSONObject ws_msg_send = new JSONObject();
                        try {
                            ws_msg_send.put("bizType", "SEND_TEXT");
                            ws_msg_send.put("content", v.getText().toString());
                            ws_msg_send.put("targetUserId", talkToId);
                            ws_msg_send.put("messageType", "TEXT");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences sp = mainActivity.getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
                        String username = sp.getString("username", "");
                        mainActivity.sendMsg(ws_msg_send.toString());
                        data.add(new MessageA(username, R.drawable.contacts_6, v.getText().toString(), 1));
                        messageAdapter.notifyDataSetChanged();
                    }
                    else if(chat_type == 1)
                    {
                        JSONObject ws_msg_send = new JSONObject();
                        try {
                            ws_msg_send.put("bizType", "GROUP_SEND_TEXT");
                            ws_msg_send.put("content", v.getText().toString());
                            ws_msg_send.put("groupId", talkToId);
                            ws_msg_send.put("messageType", "TEXT");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SharedPreferences sp = mainActivity.getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
                        String username = sp.getString("username", "");
                        mainActivity.sendMsg(ws_msg_send.toString());
                        data.add(new MessageA(username, R.drawable.contacts_6, v.getText().toString(), 1));
                        messageAdapter.notifyDataSetChanged();
                    }
                    edit_text.setText("");
                    return true;
                }
                return false;
            }
        });
        Button image_icon = getActivity().findViewById(R.id.image_icon);
        image_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImageFromAlbum();
            }
        });
        Button video_icon = getActivity().findViewById(R.id.video_icon);
        video_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickVideoFromAlbum();
            }
        });
        Button voice_icon = getActivity().findViewById(R.id.voice_icon);
        voice_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    recordAudio();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Button location_icon = getActivity().findViewById(R.id.location_icon);
        location_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocationInfo();
            }
        });
        Button titleBack = (Button) getActivity().findViewById(R.id.title_back);
        Button titleBack_2 = (Button) getActivity().findViewById(R.id.title_back2);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                ChatsFragment chatsFragment = new ChatsFragment();
                transaction.replace(R.id.flFragment, chatsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        if(chat_type == 1) {
            titleBack_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    GroupInfoFragment groupInfoFragment = new GroupInfoFragment();
                    groupInfoFragment.saveMessageInfo(chat_type, talkToName, talkToId);
                    transaction.replace(R.id.flFragment, groupInfoFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });
        }
        else if(chat_type == 0) {
            titleBack_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),ChatHistoryActivity.class);
                    intent.putExtra("talkToId",talkToId);
                    intent.putExtra("talkToName",talkToName);
                    startActivity(intent);
                }
            });
        }
//        listView.setSelection(listView.getBottom());
    }

    public void pickImageFromAlbum() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 111);
    }

    public void pickVideoFromAlbum() {
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_PICK);
//        intent.setData(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, 222);
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 222);
    }

    private void getPicture(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 111);
    }

    private void uploadImage(String path) throws IOException {
        HttpRequest.sendOkHttpUploadFile("file/upload", new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(getActivity().getApplicationContext(),R.string.network_error, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resStr = response.body().string();
                Log.e("response", resStr);
                try {
                    JSONObject jsonObject = new JSONObject(resStr);
                    if (jsonObject.getBoolean("success")){
                        filename = jsonObject.getString("fileName");
                        Looper.prepare();
                        Toast.makeText(getActivity().getApplicationContext(), "图片"+"上传成功", Toast.LENGTH_SHORT).show();
                    }else{
                        Looper.prepare();
                        Toast.makeText(getActivity().getApplicationContext(),"图片错误", Toast.LENGTH_SHORT).show();
                    }
                    handler_images.sendEmptyMessage(1);
                    Looper.loop();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(getActivity().getApplicationContext(),R.string.json_parse_error, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

            }
        }, path);
    }

    private void uploadAudio(String path) throws IOException {
        HttpRequest.sendOkHttpUploadFile("file/upload", new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(getActivity().getApplicationContext(),R.string.network_error, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resStr = response.body().string();
                Log.e("response", resStr);
                try {
                    JSONObject jsonObject = new JSONObject(resStr);
                    if (jsonObject.getBoolean("success")){
                        filename = jsonObject.getString("fileName");
                        Looper.prepare();
                        Toast.makeText(getActivity().getApplicationContext(), "音频"+"上传成功", Toast.LENGTH_SHORT).show();
                    }else{
                        Looper.prepare();
                        Toast.makeText(getActivity().getApplicationContext(),"音频错误", Toast.LENGTH_SHORT).show();
                    }
                    handler_audio.sendEmptyMessage(1);
                    Looper.loop();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(getActivity().getApplicationContext(),R.string.json_parse_error, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

            }
        }, path);
        handler_audio = new Handler() {
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(chat_type == 0) {
                    JSONObject ws_msg_send = new JSONObject();
                    try {
                        ws_msg_send.put("bizType", "SEND_TEXT");
                        ws_msg_send.put("content", HttpRequest.media_url + filename);
                        ws_msg_send.put("targetUserId", talkToId);
                        ws_msg_send.put("messageType", "AUDIO");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    SharedPreferences sp = mainActivity.getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
                    String username = sp.getString("username", "");
                    mainActivity.sendMsg(ws_msg_send.toString());
                    data.add(new MessageA(username, R.drawable.contacts_6, 7, HttpRequest.media_url + filename));
                    messageAdapter.notifyDataSetChanged();
                }
                else if(chat_type == 1) {
                    JSONObject ws_msg_send = new JSONObject();
                    try {
                        ws_msg_send.put("bizType", "GROUP_SEND_TEXT");
                        ws_msg_send.put("content", HttpRequest.media_url + filename);
                        ws_msg_send.put("groupId", talkToId);
                        ws_msg_send.put("messageType", "AUDIO");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    SharedPreferences sp = mainActivity.getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
                    String username = sp.getString("username", "");
                    mainActivity.sendMsg(ws_msg_send.toString());
                    data.add(new MessageA(username, R.drawable.contacts_6, 7, HttpRequest.media_url + filename));
                    messageAdapter.notifyDataSetChanged();
                }
            }
        };
    }

    private void uploadVideo(String path) throws IOException {
        HttpRequest.sendOkHttpUploadFile("file/upload", new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Looper.prepare();
                Toast.makeText(getActivity().getApplicationContext(),R.string.network_error, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String resStr = response.body().string();
                Log.e("response", resStr);
                try {
                    JSONObject jsonObject = new JSONObject(resStr);
                    if (jsonObject.getBoolean("success")){
                        filename = jsonObject.getString("fileName");
                        Looper.prepare();
                        Toast.makeText(getActivity().getApplicationContext(), "音频"+"上传成功", Toast.LENGTH_SHORT).show();
                    }else{
                        Looper.prepare();
                        Toast.makeText(getActivity().getApplicationContext(),"音频错误", Toast.LENGTH_SHORT).show();
                    }
                    handler_video.sendEmptyMessage(1);
                    Looper.loop();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(getActivity().getApplicationContext(),R.string.json_parse_error, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

            }
        }, path);
        handler_video = new Handler() {
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(chat_type == 0) {
                    JSONObject ws_msg_send = new JSONObject();
                    try {
                        ws_msg_send.put("bizType", "SEND_TEXT");
                        ws_msg_send.put("content", HttpRequest.media_url + filename);
                        ws_msg_send.put("targetUserId", talkToId);
                        ws_msg_send.put("messageType", "VIDEO");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    SharedPreferences sp = mainActivity.getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
                    String username = sp.getString("username", "");
                    mainActivity.sendMsg(ws_msg_send.toString());
                    data.add(new MessageA(username, R.drawable.contacts_6, 5, HttpRequest.media_url + filename));
                    messageAdapter.notifyDataSetChanged();
                }
                else if(chat_type == 1)
                {
                    JSONObject ws_msg_send = new JSONObject();
                    try {
                        ws_msg_send.put("bizType", "GROUP_SEND_TEXT");
                        ws_msg_send.put("content", HttpRequest.media_url + filename);
                        ws_msg_send.put("groupId", talkToId);
                        ws_msg_send.put("messageType", "VIDEO");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    SharedPreferences sp = mainActivity.getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
                    String username = sp.getString("username", "");
                    mainActivity.sendMsg(ws_msg_send.toString());
                    data.add(new MessageA(username, R.drawable.contacts_6, 5, HttpRequest.media_url + filename));
                    messageAdapter.notifyDataSetChanged();
                }
            }
        };
    }

    public void recordAudio() throws IOException {
        if(mMediaRecorderUtils.isRecording() == false) {
            mMediaRecorderUtils.start();
        }
        else
        {
            mMediaRecorderUtils.stop();
            String audio_path = mMediaRecorderUtils.getPath();
            Log.d("path", audio_path);
            uploadAudio(audio_path);
        }
    }

    public void getLocationInfo() {
//        JSONObject ws_msg_send = new JSONObject();
//        try {
//            ws_msg_send.put("bizType", "SEND_TEXT");
//            ws_msg_send.put("content", positionText);
//            ws_msg_send.put("targetUserId", talkToId);
//            ws_msg_send.put("messageType", "LOCATION");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        SharedPreferences sp = mainActivity.getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
        String username = sp.getString("username", "");
//        mainActivity.sendMsg(ws_msg_send.toString());
        data.add(new MessageA(username, R.drawable.contacts_1, 9, positionText));
        messageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mLocationClient.stop();
//        mapView.onDestroy();
//        baiduMap.setMyLocationEnabled(false);
        mMediaRecorderUtils.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data_intent) {
        super.onActivityResult(requestCode, resultCode, data_intent);
        switch (requestCode) {
            case 111:
                if (resultCode == 0) {
                    Toast.makeText(getActivity(), "取消从相册选择", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    Uri imageUri = data_intent.getData();
                    Log.d("TAG", imageUri.toString());
//                    iv_image.setImageURI(imageUri);
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(imageUri,filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    filepath = cursor.getString(columnIndex);
                    cursor.close();
                    uploadImage(filepath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler_images = new Handler() {
                    @SuppressLint("HandlerLeak")
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if(chat_type == 0) {
                            JSONObject ws_msg_send = new JSONObject();
                            try {
                                ws_msg_send.put("bizType", "SEND_TEXT");
                                ws_msg_send.put("content", HttpRequest.media_url + filename);
                                ws_msg_send.put("targetUserId", talkToId);
                                ws_msg_send.put("messageType", "PHOTO");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            SharedPreferences sp = mainActivity.getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
                            String username = sp.getString("username", "");
                            mainActivity.sendMsg(ws_msg_send.toString());
                            data.add(new MessageA(username, R.drawable.contacts_6, 3, HttpRequest.media_url + filename));
                            messageAdapter.notifyDataSetChanged();
                        }
                        else if(chat_type == 1) {
                            JSONObject ws_msg_send = new JSONObject();
                            try {
                                ws_msg_send.put("bizType", "GROUP_SEND_TEXT");
                                ws_msg_send.put("content", HttpRequest.media_url + filename);
                                ws_msg_send.put("groupId", talkToId);
                                ws_msg_send.put("messageType", "PHOTO");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            SharedPreferences sp = mainActivity.getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
                            String username = sp.getString("username", "");
                            mainActivity.sendMsg(ws_msg_send.toString());
                            data.add(new MessageA(username, R.drawable.contacts_6, 3, HttpRequest.media_url + filename));
                            messageAdapter.notifyDataSetChanged();
                        }
                    }
                };
                break;
            case 222:
                if (resultCode == 0) {
                    Toast.makeText(getActivity(), "取消从相册选择", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    Uri uri = data_intent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(uri,filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    filepath = cursor.getString(columnIndex);
                    cursor.close();
                    uploadVideo(filepath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }


//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        menu.setGroupVisible(R.menu.menu_main,false);
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        bottomNavigationView = activity.findViewById(R.id.bottomNavigationView);
//        bottomNavigationView.inflateMenu(R.menu.chat_editor);
        bottomNavigationView.setVisibility(View.GONE);
//        setHasOptionsMenu(false);
//        ButterKnife.bind(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.chat_interface, container, false);
    }
}