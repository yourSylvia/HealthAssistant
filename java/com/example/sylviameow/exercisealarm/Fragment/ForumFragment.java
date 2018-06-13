package com.example.sylviameow.exercisealarm.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sylviameow.exercisealarm.Activity.Forum.ConsultActivity;
import com.example.sylviameow.exercisealarm.Activity.Forum.MyQuestionActivity;
import com.example.sylviameow.exercisealarm.Activity.Forum.NormalQuestionActivity;
import com.example.sylviameow.exercisealarm.R;

public class ForumFragment extends Fragment {

    private LinearLayout my_ques_btn;
    private LinearLayout norm_ques_btn;
    private LinearLayout ask_ques_btn;
    private TextView my_ques;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum, null);

        initView(view);

        buttonEvent();

        return view;
    }


    protected int getNewMsg(){

        int num = 0;

        return num;

    }


    protected void buttonEvent(){

        my_ques_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i1 = new Intent(getActivity(), MyQuestionActivity.class);
                startActivity(i1);

            }
        });

        norm_ques_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent i2 = new Intent(getActivity(), NormalQuestionActivity.class);
                startActivity(i2);

            }

        });

        ask_ques_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i3 = new Intent(getActivity(), ConsultActivity.class);
                startActivity(i3);
            }

        });

    }


    protected void initView(View view){

        my_ques = view.findViewById(R.id.my_ques_id);
//        QBadgeView qBadgeView = new QBadgeView(getContext());
//        qBadgeView.bindTarget(my_ques)
//                .setBadgeNumber(getNewMsg())
//                .setBadgeGravity(Gravity.END);

        my_ques_btn = view.findViewById(R.id.my_answer_btn);
        norm_ques_btn = view.findViewById(R.id.normal_answer_btn);
        ask_ques_btn = view.findViewById(R.id.ask_ques_btn);

    }

}
