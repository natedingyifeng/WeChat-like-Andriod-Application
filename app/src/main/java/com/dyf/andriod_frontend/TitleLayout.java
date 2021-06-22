package com.dyf.andriod_frontend;

import android.app.Activity;
import androidx.fragment.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
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
import androidx.fragment.app.ListFragment;

import com.dyf.andriod_frontend.chat.Chat;
import com.dyf.andriod_frontend.chat.ChatAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.ArrayList;

import butterknife.BindView;

public class TitleLayout extends LinearLayout {
    private int chat_type;
    public void setChat_type(int type) {
        this.chat_type = type;
    }
    @BindView(R.id.bottomNavigationView)
    public BottomNavigationView bottomNavigationView;
    public TitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.title_bar, this);
    }
}
