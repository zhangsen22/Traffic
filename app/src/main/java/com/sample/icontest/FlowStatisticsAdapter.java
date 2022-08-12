package com.sample.icontest;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FlowStatisticsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<MonthlyDataBean> mDatas;
    private int currentDataType = 1;//移动数据  0 wifi
    /*
       刷新数据列表跟类型
     */
    public void setCurrentDataType(int currentDataType,List<MonthlyDataBean> mDatas) {
        this.currentDataType = currentDataType;
        this.mDatas = mDatas;
        notifyDataSetChanged();
    }

    public FlowStatisticsAdapter(Context context, List<MonthlyDataBean> datas) {
        mContext = context;
        mDatas = datas;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_flow_statistics, parent, false);
        return new NormalHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NormalHolder normalHolder = (NormalHolder) holder;
        MonthlyDataBean monthlyDataBean = mDatas.get(position);
        String time = monthlyDataBean.time;
        time = time.replace("00:00:00","");
        normalHolder.tvTime.setText(time);
       String total = currentDataType == 1?monthlyDataBean.mobileTotal:monthlyDataBean.wifiTotal;
       String upload = currentDataType == 1?monthlyDataBean.mobileUpload:monthlyDataBean.wifiUpload;
       String down = currentDataType == 1?monthlyDataBean.mobileDown:monthlyDataBean.wifiDown;
        total = total.replace("0 B","--");
        upload = upload.replace("0 B","--");
        down = down.replace("0 B","--");
        normalHolder.tvTotal.setText(total);
        normalHolder.tvUpload.setText(upload);
        normalHolder.tvDown.setText(down);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class NormalHolder extends RecyclerView.ViewHolder {

        public TextView tvTime;
        public TextView tvTotal;
        public TextView tvUpload;
        public TextView tvDown;

        public NormalHolder(View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvTotal = itemView.findViewById(R.id.tv_total_flow);
            tvUpload = itemView.findViewById(R.id.tv_upload_traffic);
            tvDown = itemView.findViewById(R.id.tv_download_traffic);
        }

    }
}