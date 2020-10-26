package com.example.phonerecorderplugin;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallService extends Service {
    private final String TAG=getClass().getSimpleName();
    private CallStateListener callStateListener;
    private TelephonyManager telephonyManager;
    public CallService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    /**
     * Called by the system when the service is first created.  Do not call this method directly.
     */
    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Called by the system every time a client explicitly starts the service by calling
     * {@link Context#startService}, providing the arguments it supplied and a
     * unique integer token representing the start request.  Do not call this method directly.
*/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "启动了。。。。。。。。。。。。。。。。。。。。。。。。。。。====------》》》》》》");
        if (telephonyManager == null) {
            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        }
        if (callStateListener == null) {
            callStateListener = CallStateListener.getInstance(this);
        }
        callStateListener.setRecordState(true);//服务启动了
        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);//注册监听器，监听电话状态
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * Called by the system to notify a Service that it is no longer used and is being removed.  The
     * service should clean up any resources it holds (threads, registered
     * receivers, etc) at this point.  Upon return, there will be no more calls
     * in to this Service object and it is effectively dead.  Do not call this method directly.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        callStateListener.setRecordState(false);//服务销毁了，不在监听电话状态
    }
}
