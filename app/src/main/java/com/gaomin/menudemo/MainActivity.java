package com.gaomin.menudemo;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yyydjk.library.DropDownMenu;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final int REFRESH_COMPLETE = 0X110;
    private static final int REFRESH_LRARN = 0X111;

    @InjectView(R.id.dropDownMenu) DropDownMenu mDropDownMenu;
    @InjectView(R.id.id_swipe_ly) SwipeRefreshLayout mySwipeRefreshLayout;
    @InjectView(R.id.myListView) ListView myListView;
    private List<View> popupViews = new ArrayList<>();
    private View footerView;

    private GirdDropDownAdapter cityAdapter;
    private ListDropDownAdapter ageAdapter;
    private ConstellationAdapter constellationAdapter;

    private String headers[] = {"城市","年龄","星座"};
    private String citys[] = {"不限", "武汉", "北京", "上海", "成都", "广州", "深圳", "重庆", "天津", "西安", "南京", "杭州", "深圳", "重庆", "天津", "西安", "南京", "杭州"};
    private String ages[] = {"不限", "18岁以下", "18-22岁", "23-26岁", "27-35岁", "35岁以上"};
    private String constellations[] = {"不限", "白羊座", "金牛座", "双子座", "巨蟹座", "狮子座", "处女座", "天秤座", "天蝎座", "射手座", "摩羯座", "水瓶座", "双鱼座"};
    ArrayAdapter arrayAdapter;
    private List<String> mDatas;
    
    private int constellationPosition = 0;
    private int lastVisibleItem = 0;

    private MyHandler myHandler = new MyHandler(this);

    private  class MyHandler extends Handler {
        private WeakReference<Context> weakReference;  //用弱引用防止造成OOM
        public MyHandler(Context context) {
            weakReference = new WeakReference<>(context);
        }

        public void handleMessage(android.os.Message msg) {
            MainActivity activity = (MainActivity) weakReference.get();
            switch (msg.what) {
                case REFRESH_COMPLETE:
                    if (activity != null) {
                        activity.mDatas.addAll(Arrays.asList("下拉数据1", "下拉数据2", "下拉数据3"));
                        activity.arrayAdapter.notifyDataSetChanged();
                        activity.mySwipeRefreshLayout.setRefreshing(false);
                    }

                    break;
                case REFRESH_LRARN:
                    if (activity != null) {
                        activity.mDatas.addAll(Arrays.asList("上拉数据1", "上拉数据2", "上拉数据2"));
                        activity.arrayAdapter.notifyDataSetChanged();
                    }
                    break;
                default:break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        initView();
        initEvent();
    }
    private  void initEvent(){
        mySwipeRefreshLayout.setOnRefreshListener(this);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this,i+"",Toast.LENGTH_LONG).show();
            }
        });
    }
    private void initView() {
        footerView = View.inflate(MainActivity.this,R.layout.footer_layout,null);
        //init city menu
        final ListView cityView = new ListView(this);
        cityAdapter = new GirdDropDownAdapter(this, Arrays.asList(citys));
        cityView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView,int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0){
                    mySwipeRefreshLayout.setEnabled(true);
                }else{
                    mySwipeRefreshLayout.setEnabled(false);
                }
            }
        });
        cityView.setDividerHeight(0);
        cityView.setAdapter(cityAdapter);

        //init age menu
        final ListView ageView = new ListView(this);
        ageAdapter = new ListDropDownAdapter(this, Arrays.asList(ages));
        ageView.setDividerHeight(0);
        ageView.setAdapter(ageAdapter);

        //init constellation
        final View constellationView = getLayoutInflater().inflate(R.layout.custom_layout, null);
        GridView constellation = ButterKnife.findById(constellationView, R.id.constellation);
        constellationAdapter = new ConstellationAdapter(this, Arrays.asList(constellations));
        constellation.setAdapter(constellationAdapter);
        TextView ok = ButterKnife.findById(constellationView, R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDropDownMenu.setTabText(constellationPosition == 0 ? headers[2] : constellations[constellationPosition]);
                mDropDownMenu.closeMenu();
            }
        });

        //init popupViews
        popupViews.add(cityView);
        popupViews.add(ageView);
        popupViews.add(constellationView);

        //add item click event
        cityView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cityAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[0] : citys[position]);
                mDropDownMenu.closeMenu();
                Toast.makeText(MainActivity.this, ""+citys[position], Toast.LENGTH_SHORT).show();
            }
        });

        ageView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ageAdapter.setCheckItem(position);
                mDropDownMenu.setTabText(position == 0 ? headers[1] : ages[position]);
                mDropDownMenu.closeMenu();
                Toast.makeText(MainActivity.this, ""+ages[position], Toast.LENGTH_SHORT).show();
            }
        });

        constellation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                constellationAdapter.setCheckItem(position);
                constellationPosition = position;
            }
        });


        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MainActivity.this,""+i,Toast.LENGTH_LONG).show();
            }
        });

        myListView.addFooterView(footerView);

        mySwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light));

        myListView.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState)
            {
                if (lastVisibleItem == arrayAdapter.getCount()&&
                        scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    footerView.setVisibility(View.VISIBLE);

                    new Thread(new MyRunnable()).start();

                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                if (firstVisibleItem == 0){
                    mySwipeRefreshLayout.setEnabled(true);
                }else{
                    mySwipeRefreshLayout.setEnabled(false);
                }
                lastVisibleItem = firstVisibleItem + visibleItemCount- 1; //最后一条数据
            }
        });
        //init dropdownview
        ViewGroup parent1 = (ViewGroup) myListView.getParent();
        if(parent1 != null){
            parent1.removeView(myListView);
        }
        ViewGroup parent2 = (ViewGroup) mySwipeRefreshLayout.getParent();
        if(parent2 != null){
            parent2.removeView(myListView);
        }

        mDatas = new ArrayList();
        for (int i=0;i<5;i++){
            mDatas.add(i+"");
        }
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,mDatas);
        myListView.setAdapter(arrayAdapter);
        mDropDownMenu.setDropDownMenu(Arrays.asList(headers), popupViews, myListView);
    }
    class MyRunnable implements Runnable{
        @Override
        public void run() {
            SystemClock.sleep(2000);
            myHandler.sendEmptyMessage(REFRESH_LRARN);
        }
    }
    @Override
    public void onBackPressed() {
        //退出activity前关闭菜单
        if (mDropDownMenu.isShowing()) {
            mDropDownMenu.closeMenu();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRefresh() {
        myHandler.sendEmptyMessageDelayed(REFRESH_COMPLETE, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myHandler.removeCallbacksAndMessages(null);
    }
}
