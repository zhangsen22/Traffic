package com.sample.icontest;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.AttachPopupView;
import com.lxj.xpopup.interfaces.OnSelectListener;

import java.util.List;

class PopupView {

    public static void show(Context context, View v, String[] strList,CallBackInterger interger){
        AttachPopupView attachPopupView = new XPopup.Builder(context)
                .popupWidth(v.getWidth())
                .hasShadowBg(false)
                .isViewMode(true)
                .isClickThrough(true)
                .atView(v)  // 依附于所点击的View，内部会自动判断在上方或者下方显示
                .asAttachList(strList,
                        null,
                        new OnSelectListener() {
                            @Override
                            public void onSelect(int position, String text) {
                                interger.callMonthIndex(position,text);
                            }
                        }, 0, 0/*, Gravity.LEFT*/);
        attachPopupView.show();
    }
}
