package com.dyf.andriod_frontend;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dyf.andriod_frontend.groupInfo.GroupInfoAdapter;
import com.dyf.andriod_frontend.groupInfo.groupInfo;
import com.dyf.andriod_frontend.groupMemberIcons.Icons;
import com.dyf.andriod_frontend.groupMemberIcons.IconsAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.LinkedList;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFriendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFriendFragment extends Fragment {
    private Button add_friend_button;
    private EditText add_friend_name;
    private TextView my_account;

    @BindView(R.id.bottomNavigationView)
    public BottomNavigationView bottomNavigationView;

    public AddFriendFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFriendFragment newInstance() {
        AddFriendFragment fragment = new AddFriendFragment();
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        bottomNavigationView = (MainActivity) getActivity().getMenu();
//        bottomNavigationView.animate().translationY(bottomNavigationView.getHeight());
//        getActivity().getWindow().setNavigationBarColor(Color.TRANSPARENT);
        TextView title = getActivity().findViewById(R.id.title_text);
        title.setText("添加好友");
        Button title_back = getActivity().findViewById(R.id.title_back);
        title_back.setVisibility(View.VISIBLE);
        Button title_back_2 = getActivity().findViewById(R.id.title_back2);
        title_back_2.setVisibility(View.INVISIBLE);
        Context context = getActivity();
        Button titleBack = (Button) getActivity().findViewById(R.id.title_back);
        titleBack.setOnClickListener(new View.OnClickListener() {
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
        add_friend_button = getActivity().findViewById(R.id.button_addfriend);
        add_friend_button.setOnClickListener(new View.OnClickListener() {
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
        add_friend_name = getActivity().findViewById(R.id.add_friend_edit);
        String new_friend_name = add_friend_name.getText().toString();
        my_account = getActivity().findViewById(R.id.my_account_text);
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
        return inflater.inflate(R.layout.add_friends_dialog, container, false);
    }
}