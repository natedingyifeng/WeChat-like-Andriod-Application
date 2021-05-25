package com.dyf.andriod_frontend;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;

import com.dyf.andriod_frontend.message.Message;
import com.dyf.andriod_frontend.message.MessageAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import androidx.appcompat.app.AppCompatActivity;

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

    @BindView(R.id.bottomNavigationView)
    public BottomNavigationView bottomNavigationView;

    public MessagesFragment() {
        // Required empty public constructor
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        bottomNavigationView = (MainActivity) getActivity().getMenu();
//        bottomNavigationView.animate().translationY(bottomNavigationView.getHeight());
//        getActivity().getWindow().setNavigationBarColor(Color.TRANSPARENT);
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
            data.add(new Message(getString(R.string.nickname1), R.drawable.contacts_1, "老师大致给了一个pipeline", 0));
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
//        listView.setSelection(listView.getBottom());
    }

    public void pickImageFromAlbum() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 111);
    }

    public void pickVideoFromAlbum() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 222);
    }

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
                    data.add(new Message(getString(R.string.nickname12), R.drawable.avatar12, 5, uri));
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