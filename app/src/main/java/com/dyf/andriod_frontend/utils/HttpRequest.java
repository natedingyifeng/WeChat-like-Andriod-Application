package com.dyf.andriod_frontend.utils;


import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HttpRequest {
//    private static final String urlStr = "http://8.140.133.34:7421/";
    public static final String ip = "http://183.172.186.150";
    public static final String server_url = ip + ":7000/";
    public static final String media_url = ip + ":7001/media/";
    private static final MediaType JSON_Head = MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient client = new OkHttpClient();


    private static Request request;

    private static final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

    // 请求的Cookie处理
    private static CookieJar cookieJar= new CookieJar() {
        @Override
        public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
            cookieStore.put(httpUrl.host(), list);
        }

        @NotNull
        @Override
        public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
            List<Cookie> cookies = cookieStore.get(httpUrl.host());
            return cookies != null ? cookies : new ArrayList<Cookie>();
        }
    };

    /**
     * 发起异步get请求
     * @param url
     * @param callback
     */
    public static void sendOkHttpGetRequest(String url, okhttp3.Callback callback)
    {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().cookieJar(cookieJar).build();

        request = new Request.Builder().url(server_url + url).build();

        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 发起异步post请求
     * @param url
     * @param callback
     * @param params
     */
    public static void sendOkHttpPostRequest(String url, okhttp3.Callback callback, Map<String,String> params)
    {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().cookieJar(cookieJar).build();
        FormBody.Builder builder = new FormBody.Builder();
        for(String key:params.keySet())
        {
            builder.add(key, params.get(key));
        }
        RequestBody requestBody=builder.build();

        request = new Request.Builder().url(server_url + url).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static Response sendOkHttpPostRequest(String url, Map<String, String> params) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().cookieJar(cookieJar).build();
        FormBody.Builder builder = new FormBody.Builder();
        for(String key:params.keySet())
        {
            builder.add(key, params.get(key));
        }
        RequestBody requestBody=builder.build();

        request = new Request.Builder().url(server_url + url).post(requestBody).build();
        Response response =  okHttpClient.newCall(request).execute();
        return response;
    }

    public static void sendOkHttpPostRequest(String url, okhttp3.Callback callback, Map<String,String> params, Map<String, List<String>> listParams) throws JSONException {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().cookieJar(cookieJar).build();

//        MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
//        for(String key:params.keySet()){
//            requestBody.addFormDataPart(key, params.get(key));
//        }
//        for(String key: listParams.keySet()){
//            List<String> list = listParams.get(key);
//            for(String item : list) {
//                requestBody.addFormDataPart(key, item);
//            }
//        }

        JSONObject json = new JSONObject();
        for(String key:params.keySet()){
            json.put(key, params.get(key));
        }
        for(String key: listParams.keySet()){
            StringBuilder jsonString = new StringBuilder();
            jsonString.append("[");
            List<String> list = listParams.get(key);
            for(String item : list){
                jsonString.append("\"");
                jsonString.append(item);
                jsonString.append("\"");
                jsonString.append(",");
            }
            jsonString.deleteCharAt(jsonString.length()-1);
            jsonString.append("]");
            json.put(key, jsonString.toString());
        }

        RequestBody requestBody = RequestBody.create(json.toString(), JSON_Head);

//        FormBody.Builder builder = new FormBody.Builder();
//        for(String key:params.keySet())
//        {
//            builder.add(key, params.get(key));
//        }
//        for(String key : listParams.keySet()){
//            List<String> list = listParams.get(key);
//            for(int i = 0; i < list.size(); i++){
//                builder.add(key + "[" + i + "]", list.get(i));
//            }
//        }
//        RequestBody requestBody=builder.build();

        request = new Request.Builder().url(server_url + url).post(requestBody).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpUploadFile(String url, okhttp3.Callback callback, String filename) throws IOException {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().cookieJar(cookieJar).build();
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("fileUpload", filename.substring(filename.length()-5), RequestBody.create(MediaType.parse("multipart/form-data"), new File(filename)))
                .build();

        Request request = new Request.Builder()
                .header("Authorization", "Client-ID" + UUID.randomUUID())
                .url(server_url + url)
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(callback);
    }
}
