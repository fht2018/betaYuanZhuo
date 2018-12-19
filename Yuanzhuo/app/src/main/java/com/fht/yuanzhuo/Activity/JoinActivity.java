package com.fht.yuanzhuo.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fht.yuanzhuo.R;
import com.fht.yuanzhuo.UserDataApp;
import com.fht.yuanzhuo.Utils.CacheUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class JoinActivity extends AppCompatActivity {
    private String roomNum;
    private EditText roomNumCon;
    private String status = new String();
    private String message  = new String();
    @SuppressLint("HandlerLeak")
    private Handler uiHandler = new Handler() {
        // 覆写这个方法，接收并处理消息。
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 200:
                    Intent intent = new Intent(JoinActivity.this,WaitActivity.class);
                    intent.putExtra("roomNum",roomNum);
                    intent.putExtra("role","0");
                    startActivity(intent);
                    finish();
                    break;
                default:
                    Toast.makeText(JoinActivity.this,message,Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_join);
        Button confirmJoin=(Button)findViewById(R.id.confirmJoin);
        roomNumCon =findViewById(R.id.roomNum);

        confirmJoin.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                roomNum = roomNumCon.getText().toString().trim();
                if(roomNum.isEmpty()){
                    Toast.makeText(JoinActivity.this,"房间号不能为空",Toast.LENGTH_SHORT).show();
                }else {
                    String url = getString(R.string.baseurl)+ "userin?roomnum="+ roomNum;
                    OkHttpClient okHttpClient = new OkHttpClient();
                    final Request request = new Request.Builder()
                            .url(url)
                            .addHeader("X-USER-TOKEN",((UserDataApp)getApplication()).getUserToken())
                            .get()//默认就是GET请求，可以不写
                            .build();
                    Call call = okHttpClient.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d(TAG, "onFailure: ");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String jsonString = response.body().string();
                            Log.e("body",jsonString);

                            JSONObject jsonCont = null;
                            try {
                                Message msg = new Message();
                                jsonCont = new JSONObject(jsonString);
                                status = jsonCont.getString("status");
                                message = jsonCont.getString("message");
                                if(status.equals("200")) {
                                    msg.what = 200;
                                } else if(status.equals("1004")){
                                    msg.what = 200;
                                } else {
                                    msg.what = 0;
                                }
                                uiHandler.sendMessage(msg);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }});
                }
            }
        });

    }
}
