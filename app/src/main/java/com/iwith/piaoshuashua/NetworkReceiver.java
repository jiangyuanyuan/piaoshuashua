package com.iwith.piaoshuashua;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jiangyuanyuan on 31/3/18.
 */

public class NetworkReceiver extends BroadcastReceiver {
    String URL = "https://rong.36kr.com/api/mobi-investor/vote/aiVote?itemId=68619229&tagName=COMPANY";
    String COOKIE = "device-uid=2b4fd250-eb97-11e7-a302-f110328c294c; kr_stat_uuid=maX7Y25240700; download_animation=1; acw_tc=AQAAAJe6iGc2Uw0AUB/qAYoRyPipsVEh; sajssdk_2015_cross_new_user=1; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%221626b716e13a49-05d00832cfe019-3b60490d-2073600-1626b716e146fc%22%2C%22%24device_id%22%3A%221626b716e13a49-05d00832cfe019-3b60490d-2073600-1626b716e146fc%22%2C%22props%22%3A%7B%22%24latest_traffic_source_type%22%3A%22%E7%9B%B4%E6%8E%A5%E6%B5%81%E9%87%8F%22%2C%22%24latest_referrer%22%3A%22%22%2C%22%24latest_referrer_host%22%3A%22%22%2C%22%24latest_search_keyword%22%3A%22%E6%9C%AA%E5%8F%96%E5%88%B0%E5%80%BC_%E7%9B%B4%E6%8E%A5%E6%89%93%E5%BC%80%22%7D%7D";
    String REFERER = "https://rong.36kr.com/m/";

        @Override
        public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeInfo = manager.getActiveNetworkInfo();
            //如果无网络连接activeInfo为null

            //也可获取网络的类型
            if(activeInfo != null){ //网络连接
//                Toast.makeText(context, "测试：网络连接成功",0).show();
                requestVote();
            }else { //网络断开
//                Toast.makeText(context, "测试：网络断开",0).show();
            }
        }



    public String requestVote() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .build();
//        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .addHeader("Cookie", COOKIE)
                .addHeader("Referer", REFERER)
                .build();
        Log.e("----", "===开始==>>>" + request.toString());
        try {
            Response response = client.newCall(request).execute();

//            mHandler.sendEmptyMessageAtTime(1,random.nextInt(max)%(max-min+1) + min);

            if (response != null) {
                Log.e("----", "====1111=>>>"+response.body().string());
                return "OK";
            } else {
                Log.e("----", "====2222=>>>");
                return "不OK";
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("----", "====3333=>>>" + e.getLocalizedMessage());
            return "不OK";
        }
    }

}