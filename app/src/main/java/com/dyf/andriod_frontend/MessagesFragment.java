package com.dyf.andriod_frontend;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.dyf.andriod_frontend.message.Message;
import com.dyf.andriod_frontend.message.MessageAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MessagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MessagesFragment extends Fragment {
    private MessageAdapter messageAdapter;
    private LinkedList<Message> data;
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
    private TextView positionText;
    private MapView mapView;
    private BaiduMap baiduMap;
    private boolean isFirstLocate = true;

    @BindView(R.id.bottomNavigationView)
    public BottomNavigationView bottomNavigationView;

    public MessagesFragment() {
        // Required empty public constructor
//        chat_type = type;
    }

    public void setChatType(int type) {
        this.chat_type = type;
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
            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append("纬度：").append(location.getLatitude()).append("\n");
            currentPosition.append("经线：").append(location.getLongitude()).append("\n");
            currentPosition.append("国家：").append(location.getCountry()).append("\n");
            currentPosition.append("省：").append(location.getProvince()).append("\n");
            currentPosition.append("市：").append(location.getCity()).append("\n");
            currentPosition.append("区：").append(location.getDistrict()).append("\n");
            currentPosition.append("街道：").append(location.getStreet()).append("\n");
            currentPosition.append("定位方式：");
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                currentPosition.append("GPS");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                currentPosition.append("网络");
            }
            positionText.setText(currentPosition);
            Log.d("GPS", String.valueOf(currentPosition));
            if (location.getLocType() == BDLocation.TypeGpsLocation
                    || location.getLocType() == BDLocation.TypeNetWorkLocation) {
                navigateTo(location);
            }
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
        mLocationClient = new LocationClient(getActivity().getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        SDKInitializer.initialize(getActivity().getApplicationContext());
//        baiduMap = mapView.getMap();
//        baiduMap.setMyLocationEnabled(true);
//        checkVersion();
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
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
        // 向ListView 添加数据，新建ChatAdapter，并向listView绑定该Adapter
        // 添加数据的样例代码如下:
        // data = new LinkedList<>();
        // data.add(new Chat(getString(R.string.nickname1), R.drawable.avatar1, getString(R.string.sentence1), "2021/01/01"));
        // data.add(new Chat(getString(R.string.nickname2), R.drawable.avatar2, getString(R.string.sentence2), "2021/01/01"));
        // TODO
        data = new LinkedList<>();
        TextView title_text = getActivity().findViewById(R.id.title_text);
        if(title_text.getText().toString().equals(getString(R.string.nickname1))) {
            data.add(new Message(getString(R.string.nickname6), R.drawable.contacts_6, "代码写不动", 1));
            data.add(new Message(getString(R.string.nickname6), R.drawable.contacts_6, "只好翻老照片刷朋友圈摸鱼", 1));
            data.add(new Message(getString(R.string.nickname1), R.drawable.contacts_1, "patpat", 0));
            data.add(new Message(getString(R.string.nickname1), R.drawable.contacts_1, "我这几天在写一个生信作业", 0));
            data.add(new Message(getString(R.string.nickname1), R.drawable.contacts_1, "老师让我们复现一篇cell的图", 0));
            data.add(new Message(getString(R.string.nickname1), R.drawable.contacts_1, "也是绝了", 0));
            data.add(new Message(getString(R.string.nickname6), R.drawable.contacts_6, "哇 复现论文", 1));
            data.add(new Message(getString(R.string.nickname6), R.drawable.contacts_6, "好难呀", 1));
            data.add(new Message(getString(R.string.nickname6), R.drawable.contacts_6, "就是把他的数据重新处理一遍吗", 1));
            data.add(new Message(getString(R.string.nickname6), R.drawable.contacts_6, "但论文怎么会写数据处理的过程", 1));
            data.add(new Message(getString(R.string.nickname1), R.drawable.contacts_1, "老师大致给了一个pipeline哈哈哈哈哈哈哈哈哈", 0));
            data.add(new Message(getString(R.string.nickname1), R.drawable.contacts_1, "但是还是很恶心", 0));
        }
        else if(title_text.getText().toString().equals(getString(R.string.nickname2))) {
            data.add(new Message(getString(R.string.nickname6), R.drawable.contacts_6, "开始吗", 1));
            data.add(new Message(getString(R.string.nickname2), R.drawable.contacts_2, "等一下", 0));
            data.add(new Message(getString(R.string.nickname6), R.drawable.contacts_6, "ok", 1));
            data.add(new Message(getString(R.string.nickname6), R.drawable.contacts_6, "要不我先做？", 1));
            data.add(new Message(getString(R.string.nickname6), R.drawable.contacts_6, "做完给你看我的答案", 1));
            data.add(new Message(getString(R.string.nickname2), R.drawable.contacts_2, "啊啊好的谢谢", 0));
            data.add(new Message(getString(R.string.nickname2), R.drawable.contacts_2, "不好意思有个表hr让我尽快填完", 0));
            data.add(new Message(getString(R.string.nickname2), R.drawable.contacts_2, "所以我现在在抓紧时间填", 0));
            data.add(new Message(getString(R.string.nickname6), R.drawable.contacts_6, "ok", 1));
            data.add(new Message(getString(R.string.nickname6), R.drawable.contacts_6, "EAADD ABABC", 1));
            data.add(new Message(getString(R.string.nickname6), R.drawable.contacts_6, "除了最后一个不太确定", 1));
            data.add(new Message(getString(R.string.nickname6), R.drawable.contacts_6, "其他应该没问题", 1));
            data.add(new Message(getString(R.string.nickname2), R.drawable.contacts_2, "啊谢谢！", 0));
            data.add(new Message(getString(R.string.nickname6), R.drawable.contacts_6, "hhh好滴", 1));
        }
        listView.setDivider(null);
        listView.setSelector(android.R.color.transparent);
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        messageAdapter = new MessageAdapter(data,context);
        listView.setAdapter(messageAdapter);
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
                    data.add(new Message(getString(R.string.nickname6), R.drawable.contacts_6, v.getText().toString(), 1));
                    messageAdapter.notifyDataSetChanged();
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
                recordAudio();
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
        titleBack_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                GroupInfoFragment groupInfoFragment = new GroupInfoFragment();
                groupInfoFragment.saveMessageInfo(title_text.getText().toString());
                transaction.replace(R.id.flFragment, groupInfoFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
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
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 222);
    }

    public void recordAudio() {
        if(mMediaRecorderUtils.isRecording() == false) {
            mMediaRecorderUtils.start();
        }
        else
        {
            mMediaRecorderUtils.stop();
            String audio_path = mMediaRecorderUtils.getPath();
            Log.d("path", audio_path);
            data.add(new Message(getString(R.string.nickname6), R.drawable.contacts_6, 7, audio_path));
            messageAdapter.notifyDataSetChanged();
        }
    }

    public void getLocationInfo() {
        requestLocation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
        mapView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
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
                    data.add(new Message(getString(R.string.nickname6), R.drawable.contacts_6, 3, imageUri));
                    messageAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 222:
                if (resultCode == 0) {
                    Toast.makeText(getActivity(), "取消从相册选择", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    Uri uri = data_intent.getData();
                    Log.d("MainActivity", "URL: " + uri);
                    ContentResolver cr = getActivity().getContentResolver();
                    Cursor cursor = cr.query(uri, null, null, null, null);
                    Log.d("MainActivity", "URL: " + uri);
                    cursor.moveToFirst();
                    String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                    Log.d("MainActivity", "filePath: " + filePath);
                    data.add(new Message(getString(R.string.nickname6), R.drawable.contacts_6, 5, filePath));
                    messageAdapter.notifyDataSetChanged();
//                    Log.d("TAG",uri.toString());
//                    ContentResolver cr = getActivity().getContentResolver();
//                    Cursor cursor = cr.query(uri, null, null, null, null);
//                    if (cursor != null) {
//                        if (cursor.moveToFirst()) {
//                            // 视频ID:MediaStore.Audio.Media._ID
//                            int videoId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
//                            // 视频名称：MediaStore.Audio.Media.TITLE
//                            String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
//                            // 视频路径：MediaStore.Audio.Media.DATA
//                            String videoPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
//                            // 视频时长：MediaStore.Audio.Media.DURATION
////                            int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
//                            // 视频大小：MediaStore.Audio.Media.SIZE
//                            long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
//
//                            // 视频缩略图路径：MediaStore.Images.Media.DATA
//                            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
//                            // 缩略图ID:MediaStore.Audio.Media._ID
//                            int imageId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
//                            // 方法一 Thumbnails 利用createVideoThumbnail 通过路径得到缩略图，保持为视频的默认比例
//                            // 第一个参数为 ContentResolver，第二个参数为视频缩略图ID， 第三个参数kind有两种为：MICRO_KIND和MINI_KIND 字面意思理解为微型和迷你两种缩略模式，前者分辨率更低一些。
////                            Bitmap bitmap1 = MediaStore.Video.Thumbnails.getThumbnail(cr, imageId, MediaStore.Video.Thumbnails.MICRO_KIND, null);
//
//                            // 方法二 ThumbnailUtils 利用createVideoThumbnail 通过路径得到缩略图，保持为视频的默认比例
//                            // 第一个参数为 视频/缩略图的位置，第二个依旧是分辨率相关的kind
//                            Bitmap bitmap1 = ThumbnailUtils.createVideoThumbnail(imagePath, MediaStore.Video.Thumbnails.MICRO_KIND);
//                            // 如果追求更好的话可以利用 ThumbnailUtils.extractThumbnail 把缩略图转化为的制定大小
////                        ThumbnailUtils.extractThumbnail(bitmap, width,height ,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
//                            tv_VideoPath = videoPath;
////                            tv_VideoDuration =  String.valueOf(duration);
//                            tv_VideoSize = String.valueOf(size);
//                            tv_VideoTitle = title;
////                            setText(tv_VideoPath, "path", videoPath);
////                            setText(tv_VideoDuration, "duration", String.valueOf(duration));
////                            setText(tv_VideoSize, "size", String.valueOf(size));
////                            setText(tv_VideoTitle, "title", title);
////                            iv_VideoImage.setImageBitmap(bitmap1);
//                            Log.d("TAG", tv_VideoPath);
//                        }
//                        cursor.close();
//                    }
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