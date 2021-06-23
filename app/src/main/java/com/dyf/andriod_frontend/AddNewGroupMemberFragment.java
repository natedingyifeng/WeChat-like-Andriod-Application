package com.dyf.andriod_frontend;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dyf.andriod_frontend.contact.Contact;
import com.dyf.andriod_frontend.utils.HttpRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddNewGroupMemberFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddNewGroupMemberFragment extends Fragment {
    private LinearLayout menuLinerLayout;

    private ListView listView;
    private LinkedList<Contact> data;
    private List<String> addList = new ArrayList<String>();
    private CreateGroupContactsAdapter adapter;
    private Button button;
    private int total = 0;
    private Handler handler;
    private Handler handler_create;
    private List<String> id_groups;
    private String group_id;
    private String userid;

    @BindView(R.id.bottomNavigationView)
    public BottomNavigationView bottomNavigationView;

    public AddNewGroupMemberFragment() {
        // Required empty public constructor
    }

    public void setInfo(String id) {
        this.group_id = id;
    }

    public class CreateGroupContactsAdapter extends BaseAdapter {
        private LinkedList<Contact> data;
        private LayoutInflater layoutInflater;
        private Context context;

        public CreateGroupContactsAdapter(Context context,LinkedList<Contact> list){
            this.context = context;
            this.data = list;
        }

        @Override
        public int getCount() {
            return this.data.size();
        }

        @Override
        public Contact getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        class ViewHolder {
            ImageView imageView;
            TextView textView;
            CheckBox checkBox;
            LinearLayout layout;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Contact contact = getItem(position);
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(R.layout.create_group_chat_item, null);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.create_group_member_avatar_icon);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.create_group_member_name);
                viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.create_group_member_checkBox);
                viewHolder.layout = (LinearLayout) convertView.findViewById(R.id.create_group_member_layout);
                convertView.setTag(viewHolder);
            }
            else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            final ImageView imageView = (ImageView) convertView.findViewById(R.id.create_group_member_avatar_icon);
            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.get(position).check_type == 1) {
                        data.get(position).check_type = 0;
                        deleteImage(data.get(position));

                        Iterator<String> it = id_groups.iterator();
                        while(it.hasNext()){
                            String x = it.next();
                            if(x.equals(data.get(position).getId())){
                                it.remove();
                            }
                        }
                    } else {
                        data.get(position).check_type = 1;
                        Bitmap bitmap = null;
                        bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        showCheckImage(bitmap, data.get(position));

                        id_groups.add(data.get(position).getId());
                    }
                }
            });
            final ViewHolder finalViewHolder = viewHolder;
            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.get(position).check_type == 1) {
                        data.get(position).check_type = 0;
                        deleteImage(data.get(position));
                        finalViewHolder.checkBox.setChecked(false);

                        Iterator<String> it = id_groups.iterator();
                        while(it.hasNext()){
                            String x = it.next();
                            if(x.equals(data.get(position).getId())){
                                it.remove();
                            }
                        }
                    } else {
                        data.get(position).check_type = 1;
                        Bitmap bitmap = null;
                        bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        showCheckImage(bitmap, data.get(position));
                        finalViewHolder.checkBox.setChecked(true);

                        id_groups.add(data.get(position).getId());
                    }
                }
            });
            if(data.get(position).check_type == 1){
                viewHolder.checkBox.setChecked(true);
            }else{
                viewHolder.checkBox.setChecked(false);
            }
            viewHolder.imageView.setImageResource(contact.getAvatarIcon());
            viewHolder.textView.setText(contact.getNickname());
            return convertView;
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddNewGroupMemberFragment newInstance() {
        AddNewGroupMemberFragment fragment = new AddNewGroupMemberFragment();
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        bottomNavigationView = (MainActivity) getActivity().getMenu();
//        bottomNavigationView.animate().translationY(bottomNavigationView.getHeight());
//        getActivity().getWindow().setNavigationBarColor(Color.TRANSPARENT);
        TextView title = getActivity().findViewById(R.id.title_text);
        title.setText("邀请联系人");
        id_groups = new LinkedList<>();
        listView = (ListView) getActivity().findViewById(R.id.groupchat_add_contacts_listview);
        menuLinerLayout = (LinearLayout) getActivity().findViewById(R.id.linearLayoutMenu);
        Button title_back = getActivity().findViewById(R.id.title_back);
        title_back.setVisibility(View.VISIBLE);
        title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                title.setText("通讯录");
                ContactsFragment contactsFragment = new ContactsFragment();
                transaction.replace(R.id.flFragment, contactsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        EditText group_name = getActivity().findViewById(R.id.group_name);
        group_name.setVisibility(View.INVISIBLE);
        Button title_back_2 = getActivity().findViewById(R.id.title_back2);
        title_back_2.setVisibility(View.INVISIBLE);
        button = (Button) getActivity().findViewById(R.id.button_create_groupchat);
        Context context = getActivity();
        SharedPreferences sp = getActivity().getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
        String username = sp.getString("username", "");
        data = new LinkedList<>();

        HashMap<String, String> params = new HashMap<>();
        params.put("keyword", username);

        HttpRequest.sendOkHttpPostRequest("user/search", new Callback() {
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
                        // 获取用户数据
                        JSONObject user = jsonObject.getJSONArray("users").getJSONObject(0);
                        userid = user.getString("id");
                        JSONArray friends =user.getJSONArray("contacts");
                        Log.d("len", friends.getJSONObject(0).getString("username")+"("+friends.getJSONObject(0).getString("nickname")+")");
                        for (int i = 0; i < friends.length(); i++)
                        {
                            data.add(new Contact(friends.getJSONObject(i).getString("username"), R.drawable.contacts_1, 0, friends.getJSONObject(i).getString("id")));
                        }
                        handler.sendEmptyMessage(1);
                    }else{
                        Looper.prepare();
                        Toast.makeText(getActivity().getApplicationContext(),"好友列表获取失败", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                        handler.sendEmptyMessage(1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(getActivity().getApplicationContext(),R.string.json_parse_error, Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }
        }, params);

        handler = new Handler(){
            @SuppressLint("HandlerLeak")
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                adapter = new CreateGroupContactsAdapter(context, data);
                listView.setAdapter(adapter);
            }
        };


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IdentityHashMap<String, String> params = new IdentityHashMap<>();
                params.put("groupChatID", group_id);
//                id_groups.add(userid);
                HashMap<String, List<String>> listParams = new HashMap<>();
                listParams.put("inviteMemberIds", id_groups);

                Log.d("list", id_groups.toString());

                try {
                    HttpRequest.sendOkHttpPostRequest("group/invite", new Callback() {
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
                                    Looper.prepare();
                                    Toast.makeText(getActivity().getApplicationContext(),"添加成功", Toast.LENGTH_SHORT).show();
                                    handler_create.sendEmptyMessage(1);
                                }else{
                                    Looper.prepare();
                                    Toast.makeText(getActivity().getApplicationContext(),"添加失败", Toast.LENGTH_SHORT).show();
                                    handler_create.sendEmptyMessage(1);
                                    Looper.loop();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Looper.prepare();
                                Toast.makeText(getActivity().getApplicationContext(),R.string.json_parse_error, Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                        }
                    }, params, listParams);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                handler_create = new Handler(){
                    @SuppressLint("HandlerLeak")
                    public void handleMessage(Message msg){
                        super.handleMessage(msg);
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        title.setText("通讯录");
                        ContactsFragment contactsFragment = new ContactsFragment();
                        transaction.replace(R.id.flFragment, contactsFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                };
            }
        });
//        listView.setSelection(listView.getBottom());
    }


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
        return inflater.inflate(R.layout.create_group_chat, container, false);
    }

    private void showCheckImage(Bitmap bitmap, Contact glufineid) {
        total++;
        // 包含TextView的LinearLayout
        // 参数设置
        LinearLayout.LayoutParams menuLinerLayoutParames = new LinearLayout.LayoutParams(
                120, 120);
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.header_item, null);
        ImageView images = (ImageView) view.findViewById(R.id.header_icon);
        menuLinerLayoutParames.setMargins(20, 20, 20, 20);

        // 设置id，方便后面删除
        view.setTag(glufineid);
        if (bitmap != null) {
            images.setImageBitmap(bitmap);
        }

        menuLinerLayout.addView(view, menuLinerLayoutParames);
        button.setText("邀请联系人(" + total + ")");
        addList.add(glufineid.getNickname());
    }

    private void deleteImage(Contact glufineid) {
        View view = (View) menuLinerLayout.findViewWithTag(glufineid);

        menuLinerLayout.removeView(view);
        total--;
        button.setText("邀请联系人(" + total + ")");
        addList.remove(glufineid.getNickname());
        if (total < 1) {
            button.setText("创建群聊");
        }
    }

}