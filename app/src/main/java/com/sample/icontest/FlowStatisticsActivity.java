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
    private int currentDataType = 1;//移动数据
    private ThreadPoolExecutor threadPool;
    public final static int ALL_APP_FLOW = 0;
    public final static int MONTH_FLOW = 1;
    private int FLOW_TYPE = 0;// 0全部  1 移动  2 wifi
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
        tvTotal.setText(StringUtil.byteToMB(totalData));//总数据
        tvUpload.setText(StringUtil.byteToMB(uploadData));//上传
        tvDown.setText(StringUtil.byteToMB(downData));//下载
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
//                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss
//                Date date = DataUsageTool.getSupportBeginDayofMonth();
//                Log.e("date","本月第一天日期："+simpleDateFormat.format(date));
//                time1.setText("Date获取当前日期时间"+simpleDateFormat.format(date))
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
//                        .atView(v)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
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
////            Log.e("total","本月日期=========="+date);
//            dateList.add(TimeUtils.getEveryDayOfTheLastMonth(currentMonth,i));
//        }
//
//       int size = dateList.size()-1;
//        for(int i=0;i<size;i++){
//            MonthlyDataBean monthlyDataBean = new MonthlyDataBean();
//          DataUsageTool.Usage usage = DataUsageTool.getUsageByUidFromSummaryTotal(this,dateList.get(i),dateList.get(i+1));
//            String wifiTotalSize = StringUtil.getBytesString(usage.wifiTotalData);
//            String wifiUploadSize = StringUtil.getBytesString(usage.wifiRxBytes);//当天上传总流量
//            String wifiDownSize = StringUtil.getBytesString(usage.wifiTxBytes);//当天下载总流量
//
//            String mobileTotalSize = StringUtil.getBytesString(usage.mobleTotalData);
//            String mobileUploadSize = StringUtil.getBytesString(usage.mobleRxBytes);//当天上传总流量
//            String mobileDownSize = StringUtil.getBytesString(usage.mobleTxBytes);//当天下载总流量
//            String dates = TimeUtils.timestampConversionDate(dateList.get(i)+"");
//
//            monthlyDataBean.time = TimeUtils.timestampConversionDate(dateList.get(i)+"");//时间日期
//
//            monthlyDataBean.mobileTotal = StringUtil.getBytesString(usage.mobleTotalData);//总 移动数据
//            monthlyDataBean.mobileDown =  StringUtil.getBytesString(usage.mobleTxBytes);// 移动 下载 数据
//            monthlyDataBean.mobileUpload =  StringUtil.getBytesString(usage.mobleRxBytes);//移动 上传 数据
//
//            monthlyDataBean.wifiTotal = StringUtil.getBytesString(usage.wifiTotalData);//总 wifi数据
//            monthlyDataBean.wifiDown = StringUtil.getBytesString(usage.wifiTxBytes);//    wifi 下载数据
//            monthlyDataBean.wifiUpload =  StringUtil.getBytesString(usage.wifiRxBytes);// wifi 上传数据
//
//            Log.e("total",
//             "\n\n时间："+dates
//            +"\n\nWiFi 总流量："+wifiTotalSize
//            +"\nWiFi 上传流量:"+wifiUploadSize
//            +"\nWiFi 下载流量:"+wifiDownSize
//
//            +"\n\n移动总流量："+mobileTotalSize
//            +"\n移动 上传流量:"+mobileUploadSize
//            +"\n移动 下载流量:"+mobileDownSize
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
                Toast.makeText(this, "请输入包名", Toast.LENGTH_SHORT).show();
                return;
            }
            uid = Utils.getUidByPackage(this, pkgName);
            if (uid == -1) {
                Toast.makeText(this, "未发现应用或异常", Toast.LENGTH_SHORT).show();
                return;
            }
            tvStart.setText("");
            tvEnd.setText("");
            tvUsage.setText("");
            btnCommit.setEnabled(false);
            btnCommit.setText("等待中...");
            catchUsageBytes();
        } else {
            btnCommit.setEnabled(false);
            btnCommit.setText("等待中...");
            catchUsageBytes();
        }
    }

    private void catchUsageBytes() {
        final boolean fromTotal = false;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // 子线程中统计流量
                if (fromTotal) {
                    tempUsage = DataUsageTool.getUsageByUidFromSummary(
                            FlowStatisticsActivity.this);
                } else {
                    tempUsage = DataUsageTool.getUsageBytesByUid(FlowStatisticsActivity.this,startTime, endTime, uid);
                    Log.e("tempUsage","tempUsage======"+tempUsage.toString());
                }
                // 切换到主线程更新数据和UI
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
//            btnCommit.setText("结束");
            String total = "本月应用总流量:"+StringUtil.getBytesString(startUsage.wifiRxBytes+startUsage.wifiTxBytes+startUsage.mobleRxBytes+startUsage.mobleTxBytes)+"\n";
            String wifi = "WIFI流量:"+StringUtil.getBytesString(startUsage.wifiRxBytes+startUsage.wifiTxBytes) +
           "      \n下载:"+StringUtil.getBytesString(startUsage.wifiRxBytes)+
                    "\n上传:"+StringUtil.getBytesString(startUsage.wifiTxBytes)
                    ;
            String mobile = "\n移动流量:"+StringUtil.getBytesString(startUsage.mobleRxBytes+startUsage.mobleTxBytes)+
            "      \n下载:"+StringUtil.getBytesString(startUsage.mobleRxBytes)+
                    "      \n上传:"+StringUtil.getBytesString(startUsage.mobleTxBytes)
                    ;
            Log.e("hxl",total);
            Log.e("hxl",wifi);
            Log.e("hxl",mobile);
            tvStart.setText(total+"\n"+wifi+"\n"+mobile);
//        } else {
//            hasStarted = false;
//            endUsage = tempUsage;
//            btnCommit.setEnabled(true);
//            btnCommit.setText("开始");
//            tvEnd.setText(StringUtil.getBytesString(endUsage.rxBytes));
//            tvUsage.setText(StringUtil.getBytesString(endUsage.rxBytes - startUsage.rxBytes));
//        }
    }

}
