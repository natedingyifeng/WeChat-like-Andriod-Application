package com.dyf.andriod_frontend;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import android.app.Service;

public class WebSocketService extends Service {

    public WebSocketService() {
    }

    private static int RE_TIME = 5;  // 发起重连的时间

    public static MyWebSocketClient socketClient = null;

    private static String SOCKET_URL = "ws://183.172.186.150:520/ws";
    private MyWebSocketClientBinder mBinder = new MyWebSocketClientBinder();
    private final static int GRAY_SERVICE_ID = 1001;
    //灰色保活
    public static class GrayInnerService extends Service {

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            startForeground(GRAY_SERVICE_ID, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
    PowerManager.WakeLock wakeLock;//锁屏唤醒
    //获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
    @SuppressLint("InvalidWakeLockTag")
    private void acquireWakeLock()
    {
        if (null == wakeLock)
        {
            PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK|PowerManager.ON_AFTER_RELEASE, "PostLocationService");
            if (null != wakeLock)
            {
                wakeLock.acquire();
            }
        }
    }

    public class MyWebSocketClientBinder extends Binder {
        public WebSocketService getService() {
            return WebSocketService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("INIT", "INIT1");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //初始化websocket
        Log.d("INIT", "INIT2");
        initSocket();
        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//开启心跳检测
        Log.d("INIT", "INIT3");

//        //设置service为前台服务，提高优先级
//        if (Build.VERSION.SDK_INT < 18) {
//            //Android4.3以下 ，隐藏Notification上的图标
//            startForeground(GRAY_SERVICE_ID, new Notification());
//        } else if(Build.VERSION.SDK_INT>18 && Build.VERSION.SDK_INT<25){
//            //Android4.3 - Android7.0，隐藏Notification上的图标
//            Intent innerIntent = new Intent(this, GrayInnerService.class);
//            startService(innerIntent);
//            startForeground(GRAY_SERVICE_ID, new Notification());
//        }else{
//            //Android7.0以上app启动后通知栏会出现一条"正在运行"的通知
//            startForeground(GRAY_SERVICE_ID, new Notification());
//        }
//
//        acquireWakeLock();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        closeConnect();
        super.onDestroy();
    }


    private void initSocket() {
        URI u = URI.create(SOCKET_URL);
        socketClient = new MyWebSocketClient(u) {

            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.d("Socket", "成功连接！");
            }

            @Override
            public void onMessage(String message) {
                Log.e("MyWebSocketService", "收到的消息：" + message);
//                    Message msg = new Message();
//                    msg.what = 1;
//                    msg.obj = message;
//                    MainActivity.msgHandler.sendMessage(msg);
                Intent intent = new Intent();
                intent.setAction("com.dyf.servicecallback.content");
                intent.putExtra("message", message);
                sendBroadcast(intent);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                socketClient = null;
//                   if (code != 1000)   // 1000为正常关闭，不是意外关闭
//                       WebSocketService.reconnect();
            }

            @Override
            public void onError(Exception ex) {
                socketClient = null;
            }
        };
//            socketClient.connect();
        connect();
    }

    private void connect() {
        new Thread() {
            @Override
            public void run() {
                try {
                    //connectBlocking多出一个等待操作，会先连接再发送，否则未连接发送会报错
                    socketClient.connectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    public boolean send(String msg) {
        if (socketClient != null) {
            Log.e("send msg", msg);
            socketClient.send(msg);
            return true;
        } else {
            return false;
        }
    }

    private void closeConnect() {
        try {
            if (null != socketClient) {
                socketClient.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            socketClient = null;
        }
    }

    private static final long HEART_BEAT_RATE = 10 * 1000;//每隔10秒进行一次对长连接的心跳检测
    private Handler mHandler = new Handler();
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            Log.e("JWebSocketClientService", "心跳包检测websocket连接状态");
            if (socketClient != null) {
                if (socketClient.isClosed()) {
                    reconnect();
                }
            } else {
                //如果client已为空，重新初始化连接
                socketClient = null;
                initSocket();
            }
            //每隔一定的时间，对长连接进行一次心跳检测
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };

    private void reconnect() {
        new Thread(() -> {
            try {
                while (socketClient == null || !socketClient.isOpen()) {
                    initSocket();
                    Thread.sleep(RE_TIME * 1000);
                    Log.e("socket", "服务器连接错误！" + RE_TIME + "秒后重连。");
                }
            } catch (Exception e) {

            }
        }).start();
    }
}
