package com.example.phonerecorderplugin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telecom.Call;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.text.BreakIterator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvStartService, tvDestroyService;
    private Button btnPlay,btnStop,btnStartService,btnStopService;
    //手机品牌
    public final String HUAWEI="HUAWEI";
    public final String XIAOMI="XIAOMI";
    public final String OPPO="OPPO";
    public final String VIVO="VIVO";
    public final String SAMSUNG="SAMSUNG";
    public final String MEIZU="MEIZU";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initClickListener();
    }

    private void initClickListener() {
        btnPlay.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnStartService.setOnClickListener(this);
        btnStopService.setOnClickListener(this);
    }
    private void initView() {
        tvStartService =findViewById(R.id.tv_start_service);
        tvDestroyService =findViewById(R.id.tv_destroy_service);
        btnStartService=findViewById(R.id.btn_start_service);
        btnStopService=findViewById(R.id.btn_stop_service);
        btnPlay=findViewById(R.id.btn_play);
        btnStop=findViewById(R.id.btn_stop);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start_service:
                startCallService();
                break;
            case R.id.btn_stop_service:
                stopCallService();
                break;
            case R.id.btn_play:
                break;
            case R.id.btn_stop:
                break;
            default:
                break;
        }
    }
    private void startCallService(){
        Intent intent=new Intent(this,CallService.class);
        startService(intent);
    }
    private void stopCallService(){
        Intent intent=new Intent(this, CallService.class);
        stopService(intent);
    }

    /**
     * @return 手机品牌
     */
    public String getDeviceBrand(){
        return Build.BRAND;
    }

    /**
     * @return 通话录音的保存路径
     */
    public File getCallRecordPath(){
        //获取手机品牌
        String brand=Build.BRAND;
        //获取通话录音文件路径
        File mainFolder = Environment.getExternalStorageDirectory();
        String subFolder = "";
        switch (brand){
            case HUAWEI:
                subFolder="/Sounds/CallRecord";
                break;
            case XIAOMI:
                subFolder="/MIUI/sound_recorder/call_rec";
                break;
            case OPPO:
                subFolder="Music/Recordings/Call Recordings";
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
        File callRecordPath=new File(mainFolder,subFolder);
        return callRecordPath;
    }



}