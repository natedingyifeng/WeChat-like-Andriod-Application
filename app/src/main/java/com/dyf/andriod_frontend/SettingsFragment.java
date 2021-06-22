package com.dyf.andriod_frontend;

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

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    private String username;
    private String nickname;

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
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ImageView avatarView = view.findViewById(R.id.settings_avatar_image_view);
        TextView nicknameView = view.findViewById(R.id.settings_nickname_text_view);
        TextView usernameView = view.findViewById(R.id.settings_username_text_view);
        TextView sloganView = view.findViewById(R.id.settings_slogan_text_view);
        TextView phoneNumberView = view.findViewById(R.id.settings_phone_number_text_view);

        Glide.with(this).load(getString(R.string.test_user_avatar_url)).into(avatarView);
        SharedPreferences sp = getActivity().getSharedPreferences(getString(R.string.store), Context.MODE_PRIVATE);
        username = sp.getString("username", "yihao_xu");
        nickname = sp.getString("nickname", "九月的南瓜");
        nicknameView.setText(nickname);
        usernameView.setText(username);

        phoneNumberView.setText(sp.getString("phoneNumber", "15808901623"));
        sloganView.setText(sp.getString("slogan", "为祖国健康工作五十年！"));

        Button image_icon = getActivity().findViewById(R.id.settings_modify_btn);
        image_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SettingsModifyActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }
}