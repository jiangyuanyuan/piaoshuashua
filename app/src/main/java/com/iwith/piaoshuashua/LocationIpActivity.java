package com.iwith.piaoshuashua;

import android.content.res.AssetManager;
import android.icu.util.TimeUnit;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LocationIpActivity extends AppCompatActivity {
    Map<String,String> ipsMap = new HashMap<>();
    Set<String> ipsSet = new HashSet<String>();
    String URL = "https://rong.36kr.com/api/mobi-investor/vote/aiVote?itemId=68619229&tagName=COMPANY";
    String COOKIE = "device-uid=2b4fd250-eb97-11e7-a302-f110328c294c; kr_stat_uuid=maX7Y25240700; download_animation=1; acw_tc=AQAAAJe6iGc2Uw0AUB/qAYoRyPipsVEh; sajssdk_2015_cross_new_user=1; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%221626b716e13a49-05d00832cfe019-3b60490d-2073600-1626b716e146fc%22%2C%22%24device_id%22%3A%221626b716e13a49-05d00832cfe019-3b60490d-2073600-1626b716e146fc%22%2C%22props%22%3A%7B%22%24latest_traffic_source_type%22%3A%22%E7%9B%B4%E6%8E%A5%E6%B5%81%E9%87%8F%22%2C%22%24latest_referrer%22%3A%22%22%2C%22%24latest_referrer_host%22%3A%22%22%2C%22%24latest_search_keyword%22%3A%22%E6%9C%AA%E5%8F%96%E5%88%B0%E5%80%BC_%E7%9B%B4%E6%8E%A5%E6%89%93%E5%BC%80%22%7D%7D";
    String REFERER = "https://rong.36kr.com/m/";
    static Boolean sss =false;
    Random random =  new Random();
    int max=4000;
    int min=2000;
    TextView textView ;
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    if (sss){
                        ThreadManager.getNormalPool().execute(new Runnable() {
                            @Override
                            public void run() {
                                loop:for (String str : ipsSet) {
                                    System.out.println(str);
                                    final String text =requestVote(str);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            textView.setText(text);
                                        }
                                    });
                                    if (!sss){
                                        break loop;
                                    }
                                    SystemClock.sleep(random.nextInt(max)%(max-min+1) + min);
                                }
                            }
                        });
                    }
                    break;
            }
        }
    } ;
//218.161.47.231:3128
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_ip);
        ThreadManager.getNormalPool().execute(new Runnable() {
            @Override
            public void run() {
                getipsSet();
//                getipsJsonSet();
            }
        });
        textView =(TextView)findViewById(R.id.hintlocation);
        findViewById(R.id.startlocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHandler.sendEmptyMessageDelayed(1,2000);
//                getipsSet();
                sss =true;
                Toast.makeText(LocationIpActivity.this,"开始点击",Toast.LENGTH_SHORT).show();
//                toggleMobileData(MainActivity.this,true);
            }
        });
        findViewById(R.id.stoplocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sss=false;
//                toggleMobileData(MainActivity.this,false);
                getipsSet();
                Toast.makeText(LocationIpActivity.this,"结束点击-----",Toast.LENGTH_SHORT).show();
            }
        });
    }




    public String requestVote(String str) {

        String[] ips = str.split(":");

        OkHttpClient client = new OkHttpClient.Builder()
                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ips[0], Integer.parseInt(ips[1]))))
                .connectTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(5,java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
                .build();
//        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URL)
                .addHeader("Cookie", COOKIE)
                .addHeader("Referer", REFERER)
                .build();
        Log.e("----", "=====0000>>>");
        Log.e("----", "===44444==>>>" + request.toString());

        try {
            Response response = client.newCall(request).execute();

            mHandler.sendEmptyMessageAtTime(1,random.nextInt(max)%(max-min+1) + min);

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

    private void getipsSet(){
        AssetManager assetManager = getAssets() ;
        try {
//            System.out.println("++++++++++++++++++++++++++++getipsSet");
            InputStream inputStream = assetManager.open( "ips.txt") ;
//            InputStream inputStream = assetManager.open( "ips_pipeline.json") ;
//            System.out.println("++++++++++++++++++++++++++++getipsSet");
            InputStreamReader isr = new InputStreamReader(inputStream, "utf-8");
            BufferedReader reader = new BufferedReader(isr);
            String lineTxt = null;
//            System.out.println("++++++++++++++++++++++++++++getipsSet");
            while ((lineTxt = reader.readLine()) != null) {
//                System.out.println(lineTxt);
                ipsSet.add(lineTxt.trim());
            }
            System.out.println("++++++++++++++++++++++++++++getipsSet        "+ipsSet.size());
            reader.close();
        } catch (IOException e) {
            System.out.println("++++++++++++++++++++++++++++");

            e.printStackTrace();
        }
    }
//    private void getipsJsonSet(){
//        AssetManager assetManager = getAssets() ;
//        try {
////            System.out.println("++++++++++++++++++++++++++++getipsSet");
////            InputStream inputStream = assetManager.open( "ips.txt") ;
//            InputStream inputStream = assetManager.open( "ips_pipeline.json") ;
////            System.out.println("++++++++++++++++++++++++++++getipsSet");
//            InputStreamReader isr = new InputStreamReader(inputStream, "utf-8");
//            BufferedReader reader = new BufferedReader(isr);
//            String lineTxt = null;
////            System.out.println("++++++++++++++++++++++++++++getipsSet");
//            while ((lineTxt = reader.readLine()) != null) {
//                System.out.println(lineTxt);
//                lineTxt = lineTxt.substring(0,lineTxt.length() - 1);
//                Gson gson = new Gson();
////                gson.fromJson()
////                JSONObject jsonObj = JSONObject.fromString(lineTxt);
////                String username = jsonObj.getString("username");
////                String password = jsonObj.optString("password");
//                ipsSet.add(lineTxt);
//            }
//            System.out.println("++++++++++++++++++++++++++++getipsSet        "+ipsSet.size());
//            reader.close();
//        } catch (IOException e) {
//            System.out.println("++++++++++++++++++++++++++++");
//
//            e.printStackTrace();
//        }
//    }

}
