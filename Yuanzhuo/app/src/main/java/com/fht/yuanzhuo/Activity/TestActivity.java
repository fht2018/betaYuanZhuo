package com.fht.yuanzhuo.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.fht.yuanzhuo.R;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {
    private AVLoadingIndicatorView avi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        String indicator=getIntent().getStringExtra("indicator");
        avi= (AVLoadingIndicatorView) findViewById(R.id.avi);
        avi.setIndicator(indicator);

        final EventManager eventManager = EventManagerFactory.create(this,"asr");

        final TextView info = findViewById(R.id.info);

        final com.baidu.speech.EventListener eventListener = new com.baidu.speech.EventListener() {
            @Override
            public void onEvent(String name, String params, byte [] data, int offset, int length){

                if(name == SpeechConstant.CALLBACK_EVENT_ASR_READY){
                    Log.i("TAG","start:"+params);
                    info.setText(name+"\n"+params);
                }
                if(name == SpeechConstant.CALLBACK_EVENT_ASR_BEGIN){
                    info.setText(name+"\n"+params);
                }
                if(name == SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL){
                    //TODO
                    try {
                        JSONObject jsonObject = new JSONObject(params);
                        if(jsonObject.getString("result_type").equals("final_result")){
                            Log.e("final_result字幕",jsonObject.getString("results_recognition"));
                        }
                        info.setText(jsonObject.getString("results_recognition"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        eventManager.registerListener(eventListener);
        final String json ="{\"accept-audio-volume\":false,\"vad.endpoint-timeout\":0}";
        Button start = findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventManager.send(SpeechConstant.ASR_START,json.toString(),null,0,0);
            }
        });

        Button stop = findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventManager.send(SpeechConstant.ASR_CANCEL, null, null, 0, 0);
                eventManager.send(SpeechConstant.ASR_STOP, null, null, 0, 0);
                eventManager.unregisterListener(eventListener);

            }
        });

    }

    public void hideClick(View view) {
        avi.hide();
        // or avi.smoothToHide();
    }

    public void showClick(View view) {
        avi.show();
        // or avi.smoothToShow();
    }

    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();

        for (String perm :permissions){
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
                //进入到这里代表没有权限.

            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()){
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // 此处为android 6.0以上动态授权的回调，用户自行实现。
    }
}
