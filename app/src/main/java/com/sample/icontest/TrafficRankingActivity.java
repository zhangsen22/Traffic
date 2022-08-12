package com.sample.icontest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.AttachPopupView;
import com.lxj.xpopup.interfaces.OnSelectListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

public class TrafficRankingActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RadioGroup radioGroup;
    private LinearLayout llDateSelected;
    private TextView tvDate;
    private static int WIFI = 0, MOBILE = 1;
    private int currentDataType = 1;//移动数据
    private RecyclerAdapter adapter;
    private ArrayList<AppInfo> appInfos = new ArrayList<>();
    private ArrayList<AppInfo> appInfosMobile = new ArrayList<>();
    private ArrayList<AppInfo> appInfosWiFi = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_ranking);

        initView();

        recyclerView = findViewById(R.id.rv_list);
        radioGroup = findViewById(R.id.rg);
        llDateSelected = findViewById(R.id.ll_date_selected);
        tvDate = findViewById(R.id.tv_date);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new RecyclerAdapter(this, appInfosMobile);
        recyclerView.setAdapter(adapter);


        appInfos = Utils.getApplicationList(this);
        List<Long> listDate = Utils.getDate(Utils.TO_DYA);//今日

        getDataAcquisitionBasedOnTime(listDate);

        llDateSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AttachPopupView attachPopupView = new XPopup.Builder(TrafficRankingActivity.this)
                        .popupWidth(v.getWidth())
                        .hasShadowBg(false)
                        .isViewMode(true)
                        .isClickThrough(true)
                        .atView(v)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                        .asAttachList(Utils.DATE_SELECETED,
//                                new int[]{R.mipmap.ic_launcher_round, R.mipmap.ic_launcher_round},
                                null,
                                new OnSelectListener() {
                                    @Override
                                    public void onSelect(int position, String text) {
                                        tvDate.setText(text);
                                        List<Long> dates = Utils.getDate(position);
                                        getDataAcquisitionBasedOnTime(dates);
                                    }
                                }, 0, 0/*, Gravity.LEFT*/);
                attachPopupView.show();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_wifi_traffic:
                        currentDataType = WIFI;
                        break;
                    case R.id.rb_data_flow:
                        currentDataType = MOBILE;
                        break;
                }
                if (adapter != null) {
                    adapter.setCurrentDataType(currentDataType,currentDataType==0?appInfosWiFi:appInfosMobile);
                }
            }
        });

    }

    /**
     * 根据时间段 获取使用流量
     * @param listDate
     */
    private void getDataAcquisitionBasedOnTime(List<Long> listDate){
        if(listDate == null || listDate.size()<1){
            return;
        }
        Utils.getApplicationTrafficStatistics(this, appInfos, listDate.get(0),listDate.get(1));
        listMobile(appInfos);

        appInfosMobile.clear();
        appInfosMobile.addAll(appInfos);

        listWiFi(appInfos);
        appInfosWiFi.clear();
        appInfosWiFi.addAll(appInfos);

        if (adapter != null) {
            adapter.setCurrentDataType(currentDataType,currentDataType==0?appInfosWiFi:appInfosMobile);
        }
    }


    public static void listMobile(List<AppInfo> listInAppxList) {
        Comparator<AppInfo> comparator = new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo details1, AppInfo details2) {
                //排序规则，按照价格由大到小顺序排列("<"),按照价格由小到大顺序排列(">"),
                if (details1.usage.mobleTotalData < details2.usage.mobleTotalData)
                    return 1;
                else {
                    return -1;
                }
            }
        };
        //这里就会自动根据规则进行排序
        Collections.sort(listInAppxList, comparator);
        Log.e("listInAppxList","listInAppxList======"+listInAppxList);
    }

    public static void listWiFi(List<AppInfo> listInAppxList) {
        Comparator<AppInfo> comparator = new Comparator<AppInfo>() {
            @Override
            public int compare(AppInfo details1, AppInfo details2) {
                //排序规则，按照价格由大到小顺序排列("<"),按照价格由小到大顺序排列(">"),
                if (details1.usage.wifiTotalData < details2.usage.wifiTotalData)
                    return 1;
                else {
                    return -1;
                }
            }
        };
        //这里就会自动根据规则进行排序
        Collections.sort(listInAppxList, comparator);
    }

    private void initView() {

    }

}