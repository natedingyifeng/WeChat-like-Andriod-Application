package com.dyf.andriod_frontend;

import android.os.Message;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;

public class WebSocket {

    private static int RE_TIME = 5;  // 发起重连的时间

    private static WebSocketClient socketClient = null;

    private static String SOCKET_URL = "ws://183.172.186.150:520/ws";

    public static void initSocket() {
        try {
            URI u = new URI(SOCKET_URL);
            socketClient = new WebSocketClient(u) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.d("Socket", "成功连接！");
                }

                @Override
                public void onMessage(String message) {
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = message;
                    MainActivity.msgHandler.sendMessage(msg);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    socketClient = null;
                    if (code != 1000)   // 1000为正常关闭，不是意外关闭
                        com.dyf.andriod_frontend.WebSocket.reconnect();
                }

                @Override
                public void onError(Exception ex) {
                    socketClient = null;
                }
            };
            socketClient.connect();
        } catch (Exception e) {

        }
    }

    public static void reconnect() {
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

    public static boolean send(JSONObject msg) {
        if (socketClient.isOpen()) {
            Log.e("send msg", msg.toString());
            socketClient.send(msg.toString());
            return true;
        } else {
            return false;
        }
    }
}
