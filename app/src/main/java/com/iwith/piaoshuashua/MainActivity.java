package com.iwith.piaoshuashua;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class MainActivity extends AppCompatActivity {
    String URL = "https://rong.36kr.com/api/mobi-investor/vote/aiVote?itemId=68619229&tagName=COMPANY";
    String COOKIE = "device-uid=2b4fd250-eb97-11e7-a302-f110328c294c; kr_stat_uuid=maX7Y25240700; download_animation=1; acw_tc=AQAAAJe6iGc2Uw0AUB/qAYoRyPipsVEh; sajssdk_2015_cross_new_user=1; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%221626b716e13a49-05d00832cfe019-3b60490d-2073600-1626b716e146fc%22%2C%22%24device_id%22%3A%221626b716e13a49-05d00832cfe019-3b60490d-2073600-1626b716e146fc%22%2C%22props%22%3A%7B%22%24latest_traffic_source_type%22%3A%22%E7%9B%B4%E6%8E%A5%E6%B5%81%E9%87%8F%22%2C%22%24latest_referrer%22%3A%22%22%2C%22%24latest_referrer_host%22%3A%22%22%2C%22%24latest_search_keyword%22%3A%22%E6%9C%AA%E5%8F%96%E5%88%B0%E5%80%BC_%E7%9B%B4%E6%8E%A5%E6%89%93%E5%BC%80%22%7D%7D";
    String REFERER = "https://rong.36kr.com/m/";
    static Boolean sss =false;
    Random random =  new Random();
    int max=20000;
    int min=10000;
    TextView textView ;
    ContentResolver cr;
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
//                                toggleMobileData(MainActivity.this,false);
                                Log.e("RootCmd settings "," 0");
                                String string =exec("settings put global airplane_mode_on 1");
                                Log.e("RootCmd settings ",string);
                                SystemClock.sleep(1000);
                                string =exec("am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true");
                                Log.e("RootCmd  am broadcast",string);
                                SystemClock.sleep(8000);
//                                toggleMobileData(MainActivity.this,true);
                                exec("settings put global airplane_mode_on 0");
                                Log.e("RootCmd  am settings",string);
                                SystemClock.sleep(1000);
                                exec("am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false");
                                Log.e("RootCmd  am broadcast",string);
                                SystemClock.sleep(10000);
                                final String text =requestVote();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textView.setText(text+" _");
                                    }
                                });

                            }
                        });

                    }
                    break;
            }
        }
    } ;

    private String exec(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }
            reader.close();
            process.waitFor();
            return output.toString();
        } catch (IOException e) {
            Log.e("RootCmd  am settings"," 报错 ");
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            Log.e("RootCmd  am settings"," 报错");
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView =(TextView)findViewById(R.id.hint);
        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RootCmd.execRootCmd("adb shell");
                mHandler.sendEmptyMessageDelayed(1,2000);
                sss =true;
//                ThreadManager.getNormalPool().execute(new Runnable() {
//                    @Override
//                    public void run() {
////                        exec("adb root");
////                        SystemClock.sleep(1000);
//
//                    }
//                });
                Toast.makeText(MainActivity.this,"开始点击",Toast.LENGTH_SHORT).show();
//                toggleMobileData(MainActivity.this,true);
            }
        });
        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sss=false;
//                toggleMobileData(MainActivity.this,false);
                Toast.makeText(MainActivity.this,"结束点击",Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.shell).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Toast.makeText(MainActivity.this,"结束点击",Toast.LENGTH_SHORT).show();
            }
        });
        cr = getContentResolver();
    }

    /**
     * 设置手机网络
     */
    private void toggleMobileData(Context context, boolean enabled){

        if (!enabled){
            Settings.System.putString(cr,Settings.System.AIRPLANE_MODE_ON, "1");
            Intent intent = new Intent("android.intent.action.AIRPLANE_MODE");
            sendBroadcast(intent);
        }else {
            Settings.System.putString(cr,Settings.System.AIRPLANE_MODE_ON, "0");
            Intent intent = new Intent("android.intent.action.AIRPLANE_MODE");
            sendBroadcast(intent);
        }

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
}
