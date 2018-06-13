package com.example.sylviameow.exercisealarm.Adapters;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sylviameow.exercisealarm.Fragment.ExerciseAlarm;
import com.example.sylviameow.exercisealarm.Fragment.ForumFragment;
import com.example.sylviameow.exercisealarm.Fragment.MyState;
import com.example.sylviameow.exercisealarm.Fragment.TipsFragment;
import com.example.sylviameow.exercisealarm.Fragment.VideoFragment;
import com.example.sylviameow.exercisealarm.R;

public class TabFragment extends Fragment {
    private TextView textView;

    public static Fragment newInstance(int index){
//        MyState my_state = ;
//        ExerciseAlarm exercise_alarm = ();
//        VideoFragment video_fragment = ();
//        TipsFragment tips_fragment = ();
//        ForumFragment forum_fragment = ();

        Fragment fragment = new Fragment();

        switch (index){
            case 0:
                fragment = new MyState();
                break;
            case 1:
                fragment = new ExerciseAlarm();
                break;
            case 2:
                fragment = new VideoFragment();
                break;
            case 3:
                fragment = new TipsFragment();
                break;
            case 4:
                fragment = new ForumFragment();

        }

        return fragment;
    }


    /* Layout for tabs*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_tab_fragment, null);
        textView = view.findViewById(R.id.text);
        textView.setText(String.valueOf((char) getArguments().getInt("index")));
        return view;
    }
}
