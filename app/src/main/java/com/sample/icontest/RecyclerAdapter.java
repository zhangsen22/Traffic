package com.sample.icontest;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<AppInfo> mDatas;
    private int currentDataType = 1;//移动数据  0 wifi
    /*
       刷新数据列表跟类型
     */
    public void setCurrentDataType(int currentDataType,ArrayList<AppInfo> mDatas) {
        this.currentDataType = currentDataType;
        this.mDatas = mDatas;
        notifyDataSetChanged();
    }



    public RecyclerAdapter(Context context, ArrayList<AppInfo> datas) {
        mContext = context;
        mDatas = datas;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.item_traffic_ranking, parent, false);
        return new NormalHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NormalHolder normalHolder = (NormalHolder) holder;
        normalHolder.ivIcon.setImageDrawable(mDatas.get(position).appIcon);
        normalHolder.tvName.setText(mDatas.get(position).appName);
        Log.e("currentDataType","currentDataType========="+currentDataType);
        String flow = StringUtil.byte2MB(currentDataType == 0?
                        mDatas.get(position).usage.wifiTotalData
                :mDatas.get(position).usage.mobleTotalData
                );
        normalHolder.tvFlow.setText(flow);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public class NormalHolder extends RecyclerView.ViewHolder {

        public ImageView ivIcon;
        public TextView tvName;
        public TextView tvFlow;

        public NormalHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvName = itemView.findViewById(R.id.tv_name);
            tvFlow = itemView.findViewById(R.id.tv_flow);
        }

    }
}