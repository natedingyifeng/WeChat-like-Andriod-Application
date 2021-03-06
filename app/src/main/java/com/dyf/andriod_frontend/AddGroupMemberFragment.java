package com.dyf.andriod_frontend;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.dyf.andriod_frontend.contact.Contact;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddGroupMemberFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddGroupMemberFragment extends Fragment {
    private LinearLayout menuLinerLayout;

    private ListView listView;
    private LinkedList<Contact> data;
    private List<String> addList = new ArrayList<String>();
    private CreateGroupContactsAdapter adapter;
    private Button button;
    private int total = 0;

    @BindView(R.id.bottomNavigationView)
    public BottomNavigationView bottomNavigationView;

    public AddGroupMemberFragment() {
        // Required empty public constructor
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
                    } else {
                        data.get(position).check_type = 1;
                        Bitmap bitmap = null;
                        bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        showCheckImage(bitmap, data.get(position));
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
                    } else {
                        data.get(position).check_type = 1;
                        Bitmap bitmap = null;
                        bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        showCheckImage(bitmap, data.get(position));
                        finalViewHolder.checkBox.setChecked(true);
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
    public static AddGroupMemberFragment newInstance() {
        AddGroupMemberFragment fragment = new AddGroupMemberFragment();
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        bottomNavigationView = (MainActivity) getActivity().getMenu();
//        bottomNavigationView.animate().translationY(bottomNavigationView.getHeight());
//        getActivity().getWindow().setNavigationBarColor(Color.TRANSPARENT);
        listView = (ListView) getActivity().findViewById(R.id.groupchat_add_contacts_listview);
        menuLinerLayout = (LinearLayout) getActivity().findViewById(R.id.linearLayoutMenu);
        Button title_back = getActivity().findViewById(R.id.title_back);
        title_back.setVisibility(View.VISIBLE);
        Button title_back_2 = getActivity().findViewById(R.id.title_back2);
        title_back_2.setVisibility(View.INVISIBLE);
        button = (Button) getActivity().findViewById(R.id.button_create_groupchat);
        Context context = getActivity();
        // ???ListView ?????????????????????ChatAdapter?????????listView?????????Adapter
        // ?????????????????????????????????:
        // data = new LinkedList<>();
        // data.add(new Chat(getString(R.string.nickname1), R.drawable.avatar1, getString(R.string.sentence1), "2021/01/01"));
        // data.add(new Chat(getString(R.string.nickname2), R.drawable.avatar2, getString(R.string.sentence2), "2021/01/01"));
        // TODO
        data = new LinkedList<>();
        data.add(new Contact(getString(R.string.nickname1), R.drawable.contacts_1, 1, null));
        data.add(new Contact(getString(R.string.nickname2), R.drawable.contacts_2, 1, null));
        data.add(new Contact(getString(R.string.nickname3), R.drawable.contacts_3, 1, null));
        data.add(new Contact(getString(R.string.nickname4), R.drawable.contacts_4, 1, null));
        data.add(new Contact(getString(R.string.nickname5), R.drawable.contacts_5, 1,null));
        adapter = new CreateGroupContactsAdapter(context, data);
        listView.setAdapter(adapter);
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
        // ??????TextView???LinearLayout
        // ????????????
        LinearLayout.LayoutParams menuLinerLayoutParames = new LinearLayout.LayoutParams(
                120, 120);
        View view = LayoutInflater.from(getActivity()).inflate(
                R.layout.header_item, null);
        ImageView images = (ImageView) view.findViewById(R.id.header_icon);
        menuLinerLayoutParames.setMargins(20, 20, 20, 20);

        // ??????id?????????????????????
        view.setTag(glufineid);
        if (bitmap != null) {
            images.setImageBitmap(bitmap);
        }

        menuLinerLayout.addView(view, menuLinerLayoutParames);
        button.setText("??????(" + total + ")");
        addList.add(glufineid.getNickname());
    }

    private void deleteImage(Contact glufineid) {
        View view = (View) menuLinerLayout.findViewWithTag(glufineid);

        menuLinerLayout.removeView(view);
        total--;
        button.setText("??????(" + total + ")");
        addList.remove(glufineid.getNickname());
        if (total < 1) {
            button.setText("??????");
        }
    }

}