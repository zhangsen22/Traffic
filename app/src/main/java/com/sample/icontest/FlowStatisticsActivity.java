package com.sample.icontest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.AttachPopupView;
import com.lxj.xpopup.interfaces.OnSelectListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FlowStatisticsActivity extends AppCompatActivity {

    private TextView tvStart, tvEnd, tvUsage;
    private EditText etPkg;
    private Button btnCommit;
    private LinearLayout llFlowDetails;
    private TextView tvFlowDetails;
    private LinearLayout llTrafficRanking;
    private LinearLayout llNetworkType;
    private LinearLayout llDateSeleted;
    private TextView tvTotal;
    private TextView tvDown;
    private TextView tvUpload;
    private TextView tvDateSeleted;
    private TextView tvNetworkType;


    private int uid = -1;

    private DataUsageTool.Usage startUsage;
    private DataUsageTool.Usage endUsage;

    private int networkType = 1;
    private int type_wifi =  ConnectivityManager.TYPE_WIFI;
    private int type_moble =  ConnectivityManager.TYPE_MOBILE;
    private Button btn;
    private long startTime=0,endTime=0;
    private FlowStatisticsAdapter flowStatisticsAdapter;
    private RecyclerView rvData;
    private List<MonthlyDataBean> monthList = new ArrayList<>();
    private RadioGroup radioGroup;
    private boolean hasStarted = false;
    private static int WIFI = 0, MOBILE = 1;
    private int currentDataType = 1;//????????????
    private ThreadPoolExecutor threadPool;
    public final static int ALL_APP_FLOW = 0;
    public final static int MONTH_FLOW = 1;
    private int FLOW_TYPE = 0;// 0??????  1 ??????  2 wifi
    private DataUsageTool.Usage tempUsage;


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case ALL_APP_FLOW:
                    tempUsage = (DataUsageTool.Usage) msg.obj;
                    updateFlow(tempUsage);
                    break;
                case MONTH_FLOW:
                    monthList = (List<MonthlyDataBean>) msg.obj;
                    if(flowStatisticsAdapter != null){
                        flowStatisticsAdapter.setCurrentDataType(currentDataType,monthList);
                    }
                    break;
            }
        }
    };


    private void updateFlow(DataUsageTool.Usage tempUsage){
        long totalData = FLOW_TYPE == 0?tempUsage.mobleTotalData+tempUsage.wifiTotalData: FLOW_TYPE == 1?tempUsage.mobleTotalData:tempUsage.wifiTotalData;
        long uploadData =  FLOW_TYPE == 0?tempUsage.mobleRxBytes+tempUsage.wifiRxBytes: FLOW_TYPE == 1?tempUsage.mobleRxBytes:tempUsage.wifiRxBytes;
        long downData =  FLOW_TYPE == 0?tempUsage.mobleTxBytes+tempUsage.wifiTxBytes: FLOW_TYPE == 1?tempUsage.mobleTxBytes:tempUsage.wifiTxBytes;
        tvTotal.setText(StringUtil.byteToMB(totalData));//?????????
        tvUpload.setText(StringUtil.byteToMB(uploadData));//??????
        tvDown.setText(StringUtil.byteToMB(downData));//??????
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        threadPool = new ThreadPoolExecutor(5, 6,
                1, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(128));
        threadPool.allowCoreThreadTimeOut(true);

        initView();

        onClickListener();

        networkType = NetworkCapabilities.TRANSPORT_WIFI;

        if (!Utils.isAccessGranted(this)) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        }

//        startTime =  TimeUtils.getSupportBeginDayofMonth();
//        endTime =  TimeUtils.getSupportBeginDayofNextMonth();

//        monthList =  Utils.getMonthlyAndDailyData(this,Utils.THIS_MONTH_DAILY_DATA);
//        Log.e("test","monthList================:  "+monthList);
        QueryDataFlow.getMonthlyAndDailyData(handler,FlowStatisticsActivity.this,Utils.THIS_MONTH_DAILY_DATA,threadPool);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvData.setLayoutManager(linearLayoutManager);
        flowStatisticsAdapter = new FlowStatisticsAdapter(this, monthList);
        rvData.setAdapter(flowStatisticsAdapter);

        List<Long> dates = Utils.getDate(0);
        if(dates == null && dates.size() < 2){
            return;
        }
        QueryDataFlow.queryAllDataFlow(handler,FlowStatisticsActivity.this,dates.get(0),dates.get(1),threadPool);

    }


    private void onClickListener(){

        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy???MM???dd??? HH:mm:ss");// HH:mm:ss
//                Date date = DataUsageTool.getSupportBeginDayofMonth();
//                Log.e("date","????????????????????????"+simpleDateFormat.format(date));
//                time1.setText("Date????????????????????????"+simpleDateFormat.format(date))
                onClickCommit();
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AttachPopupView attachPopupView = new XPopup.Builder(Main2Activity.this)
//                        .hasShadowBg(false)
//                        .isViewMode(true)
//                        .isClickThrough(true)
//                        .atView(v)  // ?????????????????????View???????????????????????????????????????????????????
//                        .asAttachList(Utils.DATE_SELECETED,
//                                new int[]{R.mipmap.ic_launcher_round, R.mipmap.ic_launcher_round},
//                                new OnSelectListener() {
//                                    @Override
//                                    public void onSelect(int position, String text) {
//                                        List<Long> dates = Utils.getDate(position);
//                                        if(dates != null && dates.size()>1){
//                                            startTime =  dates.get(0);
//                                            endTime =  dates.get(1);
//                                        }
////                                        Toast.makeText(Main2Activity.this, "click " + text, Toast.LENGTH_LONG).show();
//                                        onClickCommit();
//                                      DataUsageTool.Usage usage = DataUsageTool.getUsageByUidFromSummaryTotal(Main2Activity.this,startTime,endTime);
//                                        Log.e("usage","usage========="+usage);
//                                    }
//                                }, 0, 0/*, Gravity.LEFT*/);
//                attachPopupView.show();
                test();
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
                if (flowStatisticsAdapter != null) {
                    flowStatisticsAdapter.setCurrentDataType(currentDataType,monthList);
                }
            }
        });

        llFlowDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupView.show(FlowStatisticsActivity.this, v, Utils.DATE_MONTHLY, new CallBackInterger() {
                    @Override
                    public void callMonthIndex(int index,String text) {
                        tvFlowDetails.setText(text);
                        QueryDataFlow.getMonthlyAndDailyData(handler,FlowStatisticsActivity.this,index,threadPool);
//                        monthList =  Utils.getMonthlyAndDailyData(FlowStatisticsActivity.this,index);
                    }
                });
            }
        });

        llTrafficRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FlowStatisticsActivity.this,TrafficRankingActivity.class));
            }
        });

        llNetworkType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupView.show(FlowStatisticsActivity.this, v, Utils.NETWORK_TYPE, new CallBackInterger() {
                    @Override
                    public void callMonthIndex(int index,String text) {
                        FLOW_TYPE = index;
                        tvNetworkType.setText(text);
                        if(tempUsage != null){
                            updateFlow(tempUsage);
                        }
                    }
                });
            }
        });

        llDateSeleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupView.show(FlowStatisticsActivity.this, v, Utils.DATE_SELECETED, new CallBackInterger() {
                    @Override
                    public void callMonthIndex(int position,String text) {
                        tvDateSeleted.setText(text);
                        List<Long> dates = Utils.getDate(position);
                        if(dates == null && dates.size() < 2){
                            return;
                        }
                        QueryDataFlow.queryAllDataFlow(handler,FlowStatisticsActivity.this,dates.get(0),dates.get(1),threadPool);
                    }
                });
            }
        });

    }

    private  int currentMonth = 0;
    private void test(){
//        startActivity(new Intent(this,TrafficRankingActivity.class));


//        long startTime = System.currentTimeMillis();
//        ArrayList<AppInfo> appInfos = Utils.getApplicationList(this);
//        Utils.getApplicationTrafficStatistics(this,appInfos,TimeUtils.LAST_MONTH_FIRST_DAY);
//        for (int i=0;i<appInfos.size();i++){
//            Log.e("test","appInfo:  "+appInfos.get(i).toString());
//        }
//        long end = System.currentTimeMillis() - startTime;
//        Log.e("test","end================:  "+end);














//        TimeUtils.getThePreviousSupportBeginDayofMonth();
//        TimeUtils.getSupportBeginDayofMonth();
//        TimeUtils.getSupportBeginDayofNextMonth();
//        TimeUtils.getTheDayBefore();
//        TimeUtils.getToDay();
//        TimeUtils.getTomorrow();










//        int total = TimeUtils.getPreviousMonthTotalDay(currentMonth);
//        total++;
//        Log.e("total","total=========="+total);
//        List<Long> dateList = new ArrayList<>();
//        for(int i=0;i<total;i++){
//            String date = TimeUtils.timestampConversionDate(TimeUtils.getEveryDayOfTheLastMonth(currentMonth,i)+"");
////            Log.e("total","????????????=========="+date);
//            dateList.add(TimeUtils.getEveryDayOfTheLastMonth(currentMonth,i));
//        }
//
//       int size = dateList.size()-1;
//        for(int i=0;i<size;i++){
//            MonthlyDataBean monthlyDataBean = new MonthlyDataBean();
//          DataUsageTool.Usage usage = DataUsageTool.getUsageByUidFromSummaryTotal(this,dateList.get(i),dateList.get(i+1));
//            String wifiTotalSize = StringUtil.getBytesString(usage.wifiTotalData);
//            String wifiUploadSize = StringUtil.getBytesString(usage.wifiRxBytes);//?????????????????????
//            String wifiDownSize = StringUtil.getBytesString(usage.wifiTxBytes);//?????????????????????
//
//            String mobileTotalSize = StringUtil.getBytesString(usage.mobleTotalData);
//            String mobileUploadSize = StringUtil.getBytesString(usage.mobleRxBytes);//?????????????????????
//            String mobileDownSize = StringUtil.getBytesString(usage.mobleTxBytes);//?????????????????????
//            String dates = TimeUtils.timestampConversionDate(dateList.get(i)+"");
//
//            monthlyDataBean.time = TimeUtils.timestampConversionDate(dateList.get(i)+"");//????????????
//
//            monthlyDataBean.mobileTotal = StringUtil.getBytesString(usage.mobleTotalData);//??? ????????????
//            monthlyDataBean.mobileDown =  StringUtil.getBytesString(usage.mobleTxBytes);// ?????? ?????? ??????
//            monthlyDataBean.mobileUpload =  StringUtil.getBytesString(usage.mobleRxBytes);//?????? ?????? ??????
//
//            monthlyDataBean.wifiTotal = StringUtil.getBytesString(usage.wifiTotalData);//??? wifi??????
//            monthlyDataBean.wifiDown = StringUtil.getBytesString(usage.wifiTxBytes);//    wifi ????????????
//            monthlyDataBean.wifiUpload =  StringUtil.getBytesString(usage.wifiRxBytes);// wifi ????????????
//
//            Log.e("total",
//             "\n\n?????????"+dates
//            +"\n\nWiFi ????????????"+wifiTotalSize
//            +"\nWiFi ????????????:"+wifiUploadSize
//            +"\nWiFi ????????????:"+wifiDownSize
//
//            +"\n\n??????????????????"+mobileTotalSize
//            +"\n?????? ????????????:"+mobileUploadSize
//            +"\n?????? ????????????:"+mobileDownSize
//            );
//        }






//        String startLastWeekTime = "",endLastWeekTime = "";
//       String date = TimeUtils.getLastWeekTime();
//       String[] dates = date.split(",");
//       if(dates.length>1){
//           startLastWeekTime = dates[0];
//           endLastWeekTime = dates[1];
//       }

//        String date = TimeUtils.getThisWeekTime();
//        String[] dates = date.split(",");
//        if(dates.length>1){
//            startLastWeekTime = dates[0];
//            endLastWeekTime = dates[1];
//        }
    }






    private void initView() {
        tvStart = findViewById(R.id.tv_start);
        tvEnd = findViewById(R.id.tv_end);
        tvUsage = findViewById(R.id.tv_usage);
        etPkg = findViewById((R.id.et_pkg));
        btnCommit = findViewById(R.id.btn_commit);
        etPkg.setText("com.speedtest.accurate");
        btn = findViewById(R.id.btn);
        rvData = findViewById(R.id.rv_data);
        radioGroup = findViewById(R.id.rg);
        llFlowDetails = findViewById(R.id.ll_flow_details);
        tvFlowDetails = findViewById(R.id.tv_flow_details);
        llTrafficRanking = findViewById(R.id.ll_traffic_ranking);
        llNetworkType = findViewById(R.id.ll_network_type);
        llDateSeleted = findViewById(R.id.ll_date_seleted);

        tvTotal = findViewById(R.id.tv_total);
        tvDown = findViewById(R.id.tv_down);
        tvUpload = findViewById(R.id.tv_upload);
        tvDateSeleted = findViewById(R.id.tv_date_seleted);
        tvNetworkType = findViewById(R.id.tv_network_type);
    }

    private void onClickCommit() {
        if (!hasStarted) {
            String pkgName = etPkg.getText().toString().trim();
            if (pkgName.isEmpty()) {
                Toast.makeText(this, "???????????????", Toast.LENGTH_SHORT).show();
                return;
            }
            uid = Utils.getUidByPackage(this, pkgName);
            if (uid == -1) {
                Toast.makeText(this, "????????????????????????", Toast.LENGTH_SHORT).show();
                return;
            }
            tvStart.setText("");
            tvEnd.setText("");
            tvUsage.setText("");
            btnCommit.setEnabled(false);
            btnCommit.setText("?????????...");
            catchUsageBytes();
        } else {
            btnCommit.setEnabled(false);
            btnCommit.setText("?????????...");
            catchUsageBytes();
        }
    }

    private void catchUsageBytes() {
        final boolean fromTotal = false;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // ????????????????????????
                if (fromTotal) {
                    tempUsage = DataUsageTool.getUsageByUidFromSummary(
                            FlowStatisticsActivity.this);
                } else {
                    tempUsage = DataUsageTool.getUsageBytesByUid(FlowStatisticsActivity.this,startTime, endTime, uid);
                    Log.e("tempUsage","tempUsage======"+tempUsage.toString());
                }
                // ?????????????????????????????????UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUsage();
                    }
                });
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    private void updateUsage() {
//        if (!hasStarted) {
//            hasStarted = true;
            startUsage = tempUsage;
//            btnCommit.setEnabled(true);
//            btnCommit.setText("??????");
            String total = "?????????????????????:"+StringUtil.getBytesString(startUsage.wifiRxBytes+startUsage.wifiTxBytes+startUsage.mobleRxBytes+startUsage.mobleTxBytes)+"\n";
            String wifi = "WIFI??????:"+StringUtil.getBytesString(startUsage.wifiRxBytes+startUsage.wifiTxBytes) +
           "      \n??????:"+StringUtil.getBytesString(startUsage.wifiRxBytes)+
                    "\n??????:"+StringUtil.getBytesString(startUsage.wifiTxBytes)
                    ;
            String mobile = "\n????????????:"+StringUtil.getBytesString(startUsage.mobleRxBytes+startUsage.mobleTxBytes)+
            "      \n??????:"+StringUtil.getBytesString(startUsage.mobleRxBytes)+
                    "      \n??????:"+StringUtil.getBytesString(startUsage.mobleTxBytes)
                    ;
            Log.e("hxl",total);
            Log.e("hxl",wifi);
            Log.e("hxl",mobile);
            tvStart.setText(total+"\n"+wifi+"\n"+mobile);
//        } else {
//            hasStarted = false;
//            endUsage = tempUsage;
//            btnCommit.setEnabled(true);
//            btnCommit.setText("??????");
//            tvEnd.setText(StringUtil.getBytesString(endUsage.rxBytes));
//            tvUsage.setText(StringUtil.getBytesString(endUsage.rxBytes - startUsage.rxBytes));
//        }
    }

}
