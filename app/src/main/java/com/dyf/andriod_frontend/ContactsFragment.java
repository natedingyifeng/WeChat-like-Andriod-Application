package com.dyf.andriod_frontend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dyf.andriod_frontend.contact.Contact;
import com.dyf.andriod_frontend.contact.ContactAdapter;

import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link com.example.homework2.ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment {
    private RecyclerView recyclerView;

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

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.contacts_recylerview);
        // 添加数据，为recyclerView绑定Adapter、LayoutManager
        // 添加数据的样例代码如下:
        // LinkedList<Contact> contacts = new LinkedList<>();
        // contacts.add(new Contact(getString(R.string.nickname1), R.drawable.avatar1));
        // contacts.add(new Contact(getString(R.string.nickname2), R.drawable.avatar2));
        // TODO
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        LinkedList<Contact> contacts = new LinkedList<>();
        contacts.add(new Contact("陈诗瑶", R.drawable.contacts_1));
        contacts.add(new Contact("董诗琪", R.drawable.contacts_2));
        contacts.add(new Contact("孙宇红", R.drawable.contacts_3));
        contacts.add(new Contact("陈凌霄", R.drawable.contacts_4));
        contacts.add(new Contact("郭建敏", R.drawable.contacts_5));
        recyclerView.setAdapter(new ContactAdapter(contacts));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contacts, container, false);
    }
}