package com.example.sylviameow.exercisealarm.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sylviameow.exercisealarm.Adapters.FragmentAdapter;
import com.example.sylviameow.exercisealarm.Activity.Login.LoginActivity;
import com.example.sylviameow.exercisealarm.R;
import com.example.sylviameow.exercisealarm.Adapters.TabFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String[] titles = new String[]{"我的狀態", "鍛鍊提醒", "視頻", "健康常識", "討論"};
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private FragmentAdapter adapter;
    //ViewPage选项卡页面集合
    private List<Fragment> mFragments = new ArrayList<>();;
    //Tab标题集合
    private List<String> mTitles;

    /** 图片数组 */
    private int[] mImgs=new int[]{R.drawable.icon_state, R.drawable.icon_alarm, R.drawable.icon_video,
            R.drawable.icon_tips, R.drawable.icon_forum};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Check sign in/up or not */
        if(!isLogin()){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        TabPageView();

        /*int id = getIntent().getIntExtra("id", 0);
        if(id == 0){
            TabPageView();
        }else{
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.tab_fragment, mFragments.get(id))
                    .addToBackStack(null)
                    .commit();
        }*/
    }


    protected void TabPageView(){
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.tablayout);

        mTitles = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            mTitles.add(titles[i]);
        }

        for (int i = 0; i < mTitles.size(); i++) {
            mFragments.add(TabFragment.newInstance(i));
        }

        adapter = new FragmentAdapter(getSupportFragmentManager(), mFragments, mTitles);
        mViewPager.setAdapter(adapter);//给ViewPager设置适配器
        mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来

        mTabLayout.setSelectedTabIndicatorHeight(0);
        for (int i = 0; i < mTitles.size(); i++) {
            //获得到对应位置的Tab
            TabLayout.Tab itemTab = mTabLayout.getTabAt(i);
            if (itemTab != null) {
                //设置自定义的标题
                itemTab.setCustomView(R.layout.item_tab);
                TextView textView = (TextView) itemTab.getCustomView().findViewById(R.id.tab_name);
                textView.setText(mTitles.get(i));
                textView.setTextSize(11);
                ImageView imageView= (ImageView) itemTab.getCustomView().findViewById(R.id.tab_img);
                imageView.setImageResource(mImgs[i]);

            }
        }
        mTabLayout.getTabAt(0).getCustomView().setSelected(true);
    }

    protected boolean isLogin(){
        SharedPreferences sharedPreferences = getSharedPreferences("login_information", MODE_APPEND);
        String state = sharedPreferences.getString("login_state", "default");

        return !state.equals("default") && state.equals("true");
    }

}

