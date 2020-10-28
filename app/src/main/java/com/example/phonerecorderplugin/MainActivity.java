package com.example.phonerecorderplugin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.BreakIterator;
import java.util.List;

import listener.RecorderFileListener;
import listener.UploadHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvStartService, tvDestroyService;
    private Button btnPlay, btnStop, btnStartService, btnStopService;
    private final String TAG = getClass().getSimpleName();
    public static final int CODE_READ_PHONE_STATE = 0;
    public static final int CODE_READ_CALL_LOG = 1;
    public static final int CODE_WRITE_EXTERNAL_STORAGE = 2;
    public static final int CODE_READ_EXTERNAL_STORAGE = 3;

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
        tvStartService = findViewById(R.id.tv_start_service);
        tvDestroyService = findViewById(R.id.tv_destroy_service);
        btnStartService = findViewById(R.id.btn_start_service);
        btnStopService = findViewById(R.id.btn_stop_service);
        btnPlay = findViewById(R.id.btn_play);
        btnStop = findViewById(R.id.btn_stop);
    }

    @Override
    protected void onStart() {
        super.onStart();
        UploadHelper.setRecorderFileListener(new RecorderFileListener() {
            @Override
            public void onUploadRecorder(File file) {
                Log.e(TAG, "新通话录音" + file.getAbsolutePath());
            }
        });
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_service:
                if (allPermissionsPass(this)){
                    startCallService();
                }else {
                    verifyPermissions(this);
                }
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

    private void startCallService() {
        Intent intent = new Intent(this, CallService.class);
        startService(intent);
    }

    private void stopCallService() {
        Intent intent = new Intent(this, CallService.class);
        stopService(intent);
    }

    private static String[] requestPermissions = {
            //电话状态
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            //API23以上不仅要在manifest中声明读写权限，还要动态申请
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    //动态申请权限,四个权限都允许则可以启动服务监听通话录音。
    public void verifyPermissions(Activity activity) {
            if (!allPermissionsPass(activity)) {
                ActivityCompat.requestPermissions(activity, requestPermissions, CODE_READ_PHONE_STATE);
            }
        }


    public boolean allPermissionsPass(Activity activity) {
        boolean isPass = false;
        for (String request : requestPermissions) {
            int permission = ActivityCompat.checkSelfPermission(activity, request);
            if (permission == PackageManager.PERMISSION_GRANTED) {
                isPass = true;
            } else {
                isPass = false;
                break;
            }
        }
        return isPass;

    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CODE_READ_PHONE_STATE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (allPermissionsPass(this)){
                        startCallService();
                    }else {verifyPermissions(this);}
                } else {
                    Toast.makeText(this,"请允许使用电话权限",Toast.LENGTH_LONG).show();
//                    String[] permissionsHint = this.getResources().getStringArray(R.array.permissions);
//                    openSettingActivity(this, permissionsHint[CODE_READ_PHONE_STATE]);
                }
                break;
            case CODE_READ_CALL_LOG:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //执行任务
                    if (allPermissionsPass(this)){
                        startCallService();
                    }else {verifyPermissions(this);}
                } else {
                    Toast.makeText(this,"请允许获取通话记录权限",Toast.LENGTH_LONG).show();
//                    String[] permissionsHint = this.getResources().getStringArray(R.array.permissions);
//                    openSettingActivity(this, permissionsHint[CODE_READ_CALL_LOG]);
                }
                break;
            case CODE_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //执行任务
                    if (allPermissionsPass(this)){
                        startCallService();
                    }else {verifyPermissions(this);}
                } else {
                    Toast.makeText(this,"请允许手机内存读取权限",Toast.LENGTH_LONG).show();
//                    String[] permissionsHint = this.getResources().getStringArray(R.array.permissions);
//                    openSettingActivity(this, permissionsHint[CODE_WRITE_EXTERNAL_STORAGE]);
                }
                break;
            case CODE_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //执行任务
                    if (allPermissionsPass(this)){
                        startCallService();
                    }else {verifyPermissions(this);}
                } else {
                    Toast.makeText(this,"请允许手机内存存储权限",Toast.LENGTH_LONG).show();
//                    String[] permissionsHint = this.getResources().getStringArray(R.array.permissions);
//                    openSettingActivity(this, permissionsHint[CODE_READ_EXTERNAL_STORAGE]);
                }
                break;
            default:
                break;
        }
    }

    private void openSettingActivity(final Activity activity, String message) {

        showMessageOKCancel(activity, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Log.d(TAG, "getPackageName(): " + activity.getPackageName());
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivity(intent);
            }
        });
    }

    private static void showMessageOKCancel(final Activity context, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("确定", okListener)
                .setNegativeButton("取消", null)
                .create()
                .show();

    }


}