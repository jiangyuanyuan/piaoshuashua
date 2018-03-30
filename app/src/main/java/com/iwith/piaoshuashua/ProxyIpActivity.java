package com.iwith.piaoshuashua;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProxyIpActivity extends AppCompatActivity {
    static Boolean sss =false;

    String ips ="http://www.66ip.cn/nmtq.php?getnum=1000&isp=0&anonymoustype=3&start=&ports=&export=&ipaddress=&area=1&proxytype=0&api=66ip";
    String URL = "https://rong.36kr.com/api/mobi-investor/vote/aiVote?itemId=68619229&tagName=COMPANY";
    String COOKIE = "device-uid=2b4fd250-eb97-11e7-a302-f110328c294c; kr_stat_uuid=maX7Y25240700; download_animation=1; acw_tc=AQAAAJe6iGc2Uw0AUB/qAYoRyPipsVEh; sajssdk_2015_cross_new_user=1; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%221626b716e13a49-05d00832cfe019-3b60490d-2073600-1626b716e146fc%22%2C%22%24device_id%22%3A%221626b716e13a49-05d00832cfe019-3b60490d-2073600-1626b716e146fc%22%2C%22props%22%3A%7B%22%24latest_traffic_source_type%22%3A%22%E7%9B%B4%E6%8E%A5%E6%B5%81%E9%87%8F%22%2C%22%24latest_referrer%22%3A%22%22%2C%22%24latest_referrer_host%22%3A%22%22%2C%22%24latest_search_keyword%22%3A%22%E6%9C%AA%E5%8F%96%E5%88%B0%E5%80%BC_%E7%9B%B4%E6%8E%A5%E6%89%93%E5%BC%80%22%7D%7D";
    String REFERER = "https://rong.36kr.com/m/";
    Random random =  new Random();
    TextView textView ;
    int max=20000;
    int min=10000;
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
                                final String text =requestVote();
                                long currentTime = System.currentTimeMillis();
                                final SimpleDateFormat formatter = new SimpleDateFormat("yyyy年-MM月dd日-HH时mm分ss秒");
                                final Date date = new Date(currentTime);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textView.setText(text+" _ ");
                                    }
                                });
                            }
                        });
                    }
                    break;
                case 2:
                    ThreadManager.getNormalPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            requestIPS();
                        }
                    });
                    break;
            }
        }
    } ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proxy_ip);
        textView =(TextView)findViewById(R.id.hintip);
        findViewById(R.id.startip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mHandler.sendEmptyMessageDelayed(2,2000);
//                sss =true;
//                Toast.makeText(ProxyIpActivity.this,"开始点击",Toast.LENGTH_SHORT).show();


//                toggleMobileData(MainActivity.this,true);
            }
        });
        findViewById(R.id.stopip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sss=false;
//                toggleMobileData(MainActivity.this,false);
                Toast.makeText(ProxyIpActivity.this,"结束点击",Toast.LENGTH_SHORT).show();
            }
        });
    }


    public String requestVote() {
        OkHttpClient client = new OkHttpClient.Builder()
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
    public String requestIPS() {
        OkHttpClient client = new OkHttpClient.Builder()
                .build();
//        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(ips)
                .build();
        Log.e("----", "=====0000>>>");
        Log.e("----", "===44444==>>>" + request.toString());

        try {
            Response response = client.newCall(request).execute();

            mHandler.sendEmptyMessageAtTime(1,random.nextInt(max)%(max-min+1) + min);

            if (response != null) {
                Log.e("----", "====1111=>>>"+response.body().string());
                return response.body().string();
            } else {
                Log.e("----", "====2222=>>>");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("----", "====3333=>>>" + e.getLocalizedMessage());
            return null;
        }
    }

}
