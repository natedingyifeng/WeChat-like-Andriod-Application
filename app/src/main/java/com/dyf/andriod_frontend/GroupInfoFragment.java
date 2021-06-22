package com.dyf.andriod_frontend;

import android.content.Context;
import android.content.Intent;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.dyf.andriod_frontend.groupInfo.GroupInfoAdapter;
import com.dyf.andriod_frontend.groupInfo.groupInfo;
import com.dyf.andriod_frontend.groupMemberIcons.Icons;
import com.dyf.andriod_frontend.groupMemberIcons.IconsAdapter;
import com.dyf.andriod_frontend.message.Message;
import com.dyf.andriod_frontend.message.MessageAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.LinkedList;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GroupInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupInfoFragment extends Fragment {
    private IconsAdapter iconsAdapter;
    private GroupInfoAdapter groupInfoAdapter;
    private LinkedList<Icons> data_icons;
    private LinkedList<groupInfo> data_groupInfos;
    private RecyclerView recyclerView;
    private ListView listView;
    private int chat_type;
    private String message_name;

    @BindView(R.id.bottomNavigationView)
    public BottomNavigationView bottomNavigationView;

    public GroupInfoFragment() {
        // Required empty public constructor
    }

    public void saveMessageInfo(String name) {
        this.message_name = name;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupInfoFragment newInstance() {
        GroupInfoFragment fragment = new GroupInfoFragment();
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        bottomNavigationView = (MainActivity) getActivity().getMenu();
//        bottomNavigationView.animate().translationY(bottomNavigationView.getHeight());
//        getActivity().getWindow().setNavigationBarColor(Color.TRANSPARENT);
        TextView title = getActivity().findViewById(R.id.title_text);
        title.setText("聊天信息");
        listView = getView().findViewById(R.id.groupinfo_listview);
        recyclerView = getView().findViewById(R.id.member_icons);
        Button title_back = getActivity().findViewById(R.id.title_back);
        title_back.setVisibility(View.VISIBLE);
        Button title_back_2 = getActivity().findViewById(R.id.title_back2);
        title_back_2.setVisibility(View.INVISIBLE);
        Context context = getActivity();
        // 向ListView 添加数据，新建ChatAdapter，并向listView绑定该Adapter
        // 添加数据的样例代码如下:
        // data = new LinkedList<>();
        // data.add(new Chat(getString(R.string.nickname1), R.drawable.avatar1, getString(R.string.sentence1), "2021/01/01"));
        // data.add(new Chat(getString(R.string.nickname2), R.drawable.avatar2, getString(R.string.sentence2), "2021/01/01"));
        // TODO
        data_icons = new LinkedList<>();
        data_icons.add(new Icons(getString(R.string.nickname1), R.drawable.contacts_1));
        data_icons.add(new Icons(getString(R.string.nickname2), R.drawable.contacts_2));
        data_icons.add(new Icons(getString(R.string.nickname3), R.drawable.contacts_3));
        data_icons.add(new Icons(getString(R.string.nickname4), R.drawable.contacts_4));
        data_icons.add(new Icons(getString(R.string.nickname5), R.drawable.contacts_5));
        data_icons.add(new Icons(getString(R.string.nickname6), R.drawable.contacts_6));
        data_icons.add(new Icons(getString(R.string.nickname1), R.drawable.contacts_1));
        data_icons.add(new Icons(getString(R.string.nickname2), R.drawable.contacts_2));
        data_icons.add(new Icons(getString(R.string.nickname3), R.drawable.contacts_3));
        data_icons.add(new Icons(getString(R.string.nickname4), R.drawable.contacts_4));
        data_icons.add(new Icons(getString(R.string.nickname5), R.drawable.contacts_5));
        data_icons.add(new Icons(getString(R.string.nickname6), R.drawable.contacts_6));
        data_icons.add(new Icons(getString(R.string.nickname4), R.drawable.contacts_4));
        data_icons.add(new Icons(R.drawable.contacts_more, 1));
        iconsAdapter = new IconsAdapter(data_icons, context);
        recyclerView.setAdapter(iconsAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 5);
        recyclerView.setLayoutManager(layoutManager);

        data_groupInfos = new LinkedList<>();
        data_groupInfos.add(new groupInfo("群聊名称"));
        data_groupInfos.add(new groupInfo("群聊头像"));
        data_groupInfos.add(new groupInfo("查找聊天内容"));
        groupInfoAdapter = new GroupInfoAdapter(data_groupInfos, context);
        listView.setAdapter(groupInfoAdapter);
//        listView.setSelection(listView.getBottom());

        Button titleBack = (Button) getActivity().findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                title.setText(message_name);
                MessagesFragment messagesFragment = new MessagesFragment();
                transaction.replace(R.id.flFragment, messagesFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
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
        return inflater.inflate(R.layout.group_chat_info, container, false);
    }
}