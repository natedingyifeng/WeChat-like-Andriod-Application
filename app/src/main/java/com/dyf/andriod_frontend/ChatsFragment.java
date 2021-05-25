package com.dyf.andriod_frontend;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;

import com.dyf.andriod_frontend.chat.Chat;
import com.dyf.andriod_frontend.chat.ChatAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.ArrayList;

import butterknife.BindView;

public class ChatsFragment extends ListFragment {
    private ChatAdapter chatAdapter;
    private List<Chat> data;
    private ListView listView;

    @BindView(R.id.bottomNavigationView)
    public BottomNavigationView bottomNavigationView;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView title = getActivity().findViewById(R.id.title_text);
        title.setText("聊天");
        Button title_back = getActivity().findViewById(R.id.title_back);
        title_back.setVisibility(View.GONE);
        Button title_back_2 = getActivity().findViewById(R.id.title_back2);
        title_back_2.setVisibility(View.GONE);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        bottomNavigationView = activity.findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setVisibility(View.VISIBLE);
        Context context = getActivity();
        data = new ArrayList<Chat>();
        data.add(new Chat(getString(R.string.nickname1), R.drawable.contacts_1, "晚安", "2021/01/01"));
        data.add(new Chat(getString(R.string.nickname2), R.drawable.contacts_2, "hhh好滴", "2021/01/02"));
        data.add(new Chat(getString(R.string.nickname3), R.drawable.contacts_3, "OK", "2021/01/03"));
        data.add(new Chat(getString(R.string.nickname4), R.drawable.contacts_4, "你到了吗", "2021/01/04"));
        data.add(new Chat(getString(R.string.nickname5), R.drawable.contacts_5, "没事", "2021/01/05"));
        chatAdapter = new ChatAdapter(data,context);
        setListAdapter(chatAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chats, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment messagesFragment = new MessagesFragment();
        transaction.replace(R.id.flFragment, messagesFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        TextView title = getActivity().findViewById(R.id.title_text);
        title.setText(data.get(position).getNickname());
//        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) title.getLayoutParams();
//        lp.setMargins(0,0,180, 0);
//        title.setLayoutParams(lp);
        Log.d("position", String.valueOf(position));
    }

}