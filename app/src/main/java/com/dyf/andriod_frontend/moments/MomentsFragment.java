package com.dyf.andriod_frontend.moments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dyf.andriod_frontend.R;
import com.dyf.andriod_frontend.user.User;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MomentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MomentsFragment extends Fragment {

    private RecyclerView recyclerView;

    public MomentsFragment() {
        // Required empty public constructor
    }

    public static MomentsFragment newInstance(){
        MomentsFragment fragment = new MomentsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView title = getActivity().findViewById(R.id.title_text);
        title.setText(R.string.moments);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.moments_recycler_view);
        Context context = getActivity();

        User dyf = new User("dyf","丁一峰");
        User lq = new User("lq","李祁");
        User xyh = new User("xyh", "徐亦豪");

        ArrayList<User> likedUsers = new ArrayList<>();
        likedUsers.add(dyf);
        likedUsers.add(lq);
        likedUsers.add(xyh);

        LinkedList<MomentsComment> comments = new LinkedList<>();
        comments.add(new MomentsComment("1", "讲的不错！", lq, null));



        User TestUser = new User("fhdksjahk","Ifreet", "yihao_xu", "123456", "12345678900", "吃饱喝足，去……", "User", getString(R.string.test_user_avatar_url));
        LinkedList<Moment> data = new LinkedList<>();
        ArrayList<String> imagesUrl = new ArrayList();
        imagesUrl.add(getString(R.string.test_image_url_1));
        data.add(new Moment(getString(R.string.test_moment_content), new LinkedList<>(comments),
                getString(R.string.test_moment_time), getString(R.string.test_user_id), getString(R.string.test_moment_time), new ArrayList<>(likedUsers), TestUser,"TEXT", new ArrayList<String>(imagesUrl)));

        comments.add(new MomentsComment("1", "确实！", dyf, null));
        imagesUrl.add(getString(R.string.test_image_url_2));
        data.add(new Moment(getString(R.string.test_moment_content), new LinkedList<>(comments),
                getString(R.string.test_moment_time), getString(R.string.test_user_id), getString(R.string.test_moment_time), new ArrayList<>(likedUsers), TestUser,"TEXT", new ArrayList<String>(imagesUrl)));

        comments.add(new MomentsComment("1", "哇，非常厉害！",xyh, null));
        imagesUrl.add(getString(R.string.test_image_url_3));
        data.add(new Moment(getString(R.string.test_moment_content), new LinkedList<>(comments),
                getString(R.string.test_moment_time), getString(R.string.test_user_id), getString(R.string.test_moment_time), new ArrayList<>(likedUsers), TestUser,"TEXT", new ArrayList<String>(imagesUrl)));

        imagesUrl.add(getString(R.string.test_image_url_4));
        data.add(new Moment(getString(R.string.test_moment_content), new LinkedList<>(comments),
                getString(R.string.test_moment_time), getString(R.string.test_user_id), getString(R.string.test_moment_time), new ArrayList<>(likedUsers), TestUser,"TEXT", new ArrayList<String>(imagesUrl)));

        MomentsAdapter momentsAdapter = new MomentsAdapter(data, getContext());
        momentsAdapter.setHasStableIds(true);
        recyclerView.setAdapter(momentsAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_moments, container, false);
    }
}