package com.example.phonerecorderplugin;

import android.Manifest;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by fan.zhengxi on 2020/10/26
 * Describe:
 */
public class CallStateListener extends PhoneStateListener {
    private static boolean recordState = false;// 是否开启了服务
    private static CallStateListener instance;
    private Context context;

    private CallStateListener(Context context) {
        this.context = context;
    }

    public static CallStateListener getInstance(Context context) {
        if (instance == null) {
            synchronized (CallStateListener.class){
                if (instance==null){
                    instance = new CallStateListener(context);
                }
            }
        }
        return instance;
    }

    public void setRecordState(boolean state) {
        recordState = state;
    }

    /**
     * Callback invoked when device call state changes.
     */
    @Override
    public void onCallStateChanged(int state, String phoneNumber) {
        super.onCallStateChanged(state, phoneNumber);
        if (recordState) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:// 空闲，（1）首次启动服务触发（2）去电-->挂断触发 （3）
                    Log.e("TAG1", "===============CALL_STATE_IDLE空闲==============");

                    break;

                case TelephonyManager.CALL_STATE_RINGING:// 来电
                    Log.e("TAG2", "===============CALL_STATE_RINGING来电==============");
                    //记录开始录音时间点，可以区分来电和去电


                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:// （1）去电触发，接通或未接通都粗发
                    Log.e("TAG3", "===============CALL_STATE_OFFHOOK接通==============");
                    //记录开始录音时间点

                    break;
                default:
                    break;
            }
        }
    }
}
