package com.example.phonerecorderplugin;

import android.Manifest;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.List;

import listener.RecorderFileListener;
import listener.UploadHelper;

/**
 * Created by fan.zhengxi on 2020/10/26
 * Describe:
 */
public class CallStateListener extends PhoneStateListener {
    private static boolean recordState = false;// 是否开启了服务
    private static CallStateListener instance;
    private Context context;
    //手机品牌
    public final String HUAWEI = "HUAWEI";
    public final String XIAOMI = "XIAOMI";
    public final String OPPO = "OPPO";
    public final String VIVO = "VIVO";
    public final String SAMSUNG = "SAMSUNG";
    public final String MEIZU = "MEIZU";
    public int callType=-1;//通话类型 0：来电 1：去电
    public long callDuration=0;//通话时长
    public long callDeviation=5*1000;//挂电话和录音文件生成时间的绝对误差设置为5s
    public float durationDeviation= (float) 0.3;//录音时长和通话时长的相对误差设置为30%
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
        long hangUpTime=0;//通话结束时间
        long pickUpTime=0;//开始接电话
        long ringUpTime=0;//开始打电话
        File recorderFile=null;
        if (recordState) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:// 空闲，（1）首次启动服务触发（2）去电-->挂断触发 （3）
                    Log.e("TAG1", "===============CALL_STATE_IDLE空闲==============");
                    hangUpTime=System.currentTimeMillis();
                    if (callType==0){
                        callDuration=hangUpTime-pickUpTime;
                         recorderFile=getRecorderFile(hangUpTime);
                         //上传录音文件
                        RecorderFileListener recorderFileListener=UploadHelper.getRecorderFileListener();
                        if (recorderFileListener!=null){
                            recorderFileListener.onUploadRecorder(recorderFile);
                        }
                    }else if (callType==1){
                        callDuration=hangUpTime-ringUpTime;
                        recorderFile=getRecorderFile(hangUpTime);
                        //上传录音文件
                        RecorderFileListener recorderFileListener=UploadHelper.getRecorderFileListener();
                        if (recorderFileListener!=null){
                            recorderFileListener.onUploadRecorder(recorderFile);
                        }
                    }
                    break;
                case TelephonyManager.CALL_STATE_RINGING:// 来电
                    Log.e("TAG2", "===============CALL_STATE_RINGING来电==============");
                    pickUpTime = System.currentTimeMillis();//获取系统时间
                    callType=0;
                    //记录开始录音时间点，可以区分来电和去电
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:// （1）去电触发，接通或未接通都粗发
                    Log.e("TAG3", "===============CALL_STATE_OFFHOOK接通==============");
                    ringUpTime = System.currentTimeMillis();//获取系统时间
                    callType=1;
                    //记录开始录音时间点
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * @return 通话录音的保存路径
     */
    public File getCallRecordPath() {
        //获取手机品牌
        String brand = Build.BRAND;
        //获取通话录音文件路径
        File mainFolder = Environment.getExternalStorageDirectory();
        String subFolder = "";
        switch (brand) {
            case HUAWEI:
                subFolder = "/Sounds/CallRecord";
                break;
            case XIAOMI:
                subFolder = "/MIUI/sound_recorder/call_rec";
                break;
            case OPPO:
                subFolder = "Music/Recordings/Call Recordings";
                break;
            case VIVO:
                break;
            case SAMSUNG:
                break;
            case MEIZU:
                break;
            default:
                break;
        }
        if (TextUtils.isEmpty(subFolder)){
            // TODO: 2020/10/28  使用后缀匹配法来匹配通话录音文件
            return null;
        }else {
            File callRecordPath = new File(mainFolder, subFolder);
            return callRecordPath;
        }

    }

    /**
     * 获取最新的通话录音文件
     * @param fileList 存放录文件的文件夹,非空
     * @return
     */
    public File getLastFile(File[] fileList) {
        File lastFile = null;
        long fileTime = 0;
        for (File file : fileList) {
            if (file.lastModified() > fileTime) {
                lastFile = file;
                fileTime = file.lastModified();
            }
        }
        return lastFile;
    }
    public File getRecorderFile(long hangUpTime){
        long duration=0;
        File folder=getCallRecordPath();
        if (folder!=null){
            File[] files=folder.listFiles();
            File mayRecorderFile=getLastFile(files);//目标录音文件
            //录音文件时长
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(mayRecorderFile.getAbsolutePath());
                mediaPlayer.prepare();
                duration = mediaPlayer.getDuration(); //时长
            } catch (IOException e) {
                e.printStackTrace();
            }
            //通话文件生成时间和通话结束时间间隔5s
            boolean isModified=Math.abs(hangUpTime-mayRecorderFile.lastModified())<callDeviation;
            boolean isDuration=(Math.abs(callDuration-duration)/callDuration)<durationDeviation;
            if (isModified && isDuration){
                return mayRecorderFile;
            }
        }
        return null;
    }

}
