package com.dyf.andriod_frontend;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dyf.andriod_frontend.contact.Contact;
import com.dyf.andriod_frontend.contact.ContactAdapter;

import org.java_websocket.client.WebSocketClient;

import java.util.LinkedList;

public class ContactsFragment extends ListFragment {
    private ListView listView;
    private LinkedList<Contact> contacts;

    public ContactsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ContactsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static com.dyf.andriod_frontend.ContactsFragment newInstance() {
        com.dyf.andriod_frontend.ContactsFragment fragment = new com.dyf.andriod_frontend.ContactsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView title = getActivity().findViewById(R.id.title_text);
        title.setText(R.string.contacts);
        Context context = getActivity();
        contacts = new LinkedList<>();
        contacts.add(new Contact("添加朋友", R.drawable.add_friends, 1));
        contacts.add(new Contact("发起群聊", R.drawable.group_chat, 2));
        contacts.add(new Contact(getString(R.string.nickname1), R.drawable.contacts_1, 0));
        contacts.add(new Contact(getString(R.string.nickname2), R.drawable.contacts_2, 0));
        contacts.add(new Contact(getString(R.string.nickname3), R.drawable.contacts_3, 0));
        contacts.add(new Contact(getString(R.string.nickname4), R.drawable.contacts_4, 0));
        contacts.add(new Contact(getString(R.string.nickname5), R.drawable.contacts_5, 0));
        setListAdapter(new ContactAdapter(contacts, context));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if(contacts.get(position).getType() == 0)
        {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            MessagesFragment messagesFragment = new MessagesFragment();
            messagesFragment.setChatType(0);
            transaction.replace(R.id.flFragment, messagesFragment);
            transaction.addToBackStack(null);
            transaction.commit();
            TextView title = getActivity().findViewById(R.id.title_text);
            title.setText(contacts.get(position).getNickname());
        }
        else if(contacts.get(position).getType() == 2)
        {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            CreateGroupMemberFragment creategroupFragment = new CreateGroupMemberFragment();
            transaction.replace(R.id.flFragment, creategroupFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        Log.d("position", String.valueOf(position));
    }
}