package com.dyf.andriod_frontend.Setting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dyf.andriod_frontend.R;
import com.dyf.andriod_frontend.utils.HttpRequest;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    private String username;
    private String nickname;
    private String avatarUrl;
    private String slogan;
    private String phoneNumber;

    ImageView avatarView;
    TextView nicknameView;
    TextView usernameView ;
    TextView sloganView;
    TextView phoneNumberView ;

    public SettingsFragment() {
        // Required empty public constructor
    }


    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView title = getActivity().findViewById(R.id.title_text);
        title.setText("个人信息");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        avatarView = view.findViewById(R.id.settings_avatar_image_view);
        nicknameView = view.findViewById(R.id.settings_nickname_text_view);
        usernameView = view.findViewById(R.id.settings_username_text_view);
        sloganView = view.findViewById(R.id.settings_slogan_text_view);
        phoneNumberView = view.findViewById(R.id.settings_phone_number_text_view);




        Button image_icon = getActivity().findViewById(R.id.settings_modify_btn);
        image_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingsModifyActivity.class);
                startActivity(intent);
            }
        });

        Button passwordBtn = getActivity().findViewById(R.id.settings_password_modify_btn);
        passwordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsPasswordModifyActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sp = getActivity().getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);

        username = sp.getString("username", "获取失败");
        nickname = sp.getString("nickname", "获取失败");
        avatarUrl = HttpRequest.media_url+ sp.getString("avatarUrl", "R.string.test_user_avatar_url");
        slogan = sp.getString("slogan", "这个人很懒，什么都没有留下。");
        phoneNumber = sp.getString("phoneNumber", "");

        nicknameView.setText(nickname);
        usernameView.setText(username);
        Glide.with(this).load(avatarUrl).into(avatarView);
        phoneNumberView.setText(phoneNumber);
        sloganView.setText(slogan);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
}