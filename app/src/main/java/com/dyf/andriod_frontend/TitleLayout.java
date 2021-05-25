package com.dyf.andriod_frontend;

import android.app.Activity;
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
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.ListFragment;

import com.dyf.andriod_frontend.chat.Chat;
import com.dyf.andriod_frontend.chat.ChatAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.ArrayList;

import butterknife.BindView;

public class TitleLayout extends LinearLayout {
    @BindView(R.id.bottomNavigationView)
    public BottomNavigationView bottomNavigationView;
    public TitleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.title_bar, this);
        Button titleBack = (Button) findViewById(R.id.title_back);
        titleBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = ((Activity) getContext());
                if (activity.getFragmentManager().getBackStackEntryCount() > 0 ){
                    activity.getFragmentManager().popBackStack();
                } else {
                    activity.onBackPressed();
                }
                TextView title = activity.findViewById(R.id.title_text);
                title.setText("Instant Message");
                titleBack.setVisibility(GONE);
                Button title_back_2 = activity.findViewById(R.id.title_back2);
                title_back_2.setVisibility(GONE);
                bottomNavigationView = activity.findViewById(R.id.bottomNavigationView);
                bottomNavigationView.setVisibility(View.VISIBLE);
//                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) title.getLayoutParams();
//                lp.setMargins(0,0,0, 0);
//                title.setLayoutParams(lp);
            }
        });
    }
}
