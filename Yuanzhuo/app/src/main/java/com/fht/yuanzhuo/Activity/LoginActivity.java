package com.fht.yuanzhuo.Activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TimeUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.fht.yuanzhuo.R;
import com.fht.yuanzhuo.UserDataApp;
import com.fht.yuanzhuo.Utils.CacheUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

import static android.content.ContentValues.TAG;

public class LoginActivity extends Activity {
    private Button login;
    private Button to_register;
    private EditText phone;
    private EditText password;

    private String status = new String();
    private String Token  = new String();
    @SuppressLint("HandlerLeak")
    private Handler uiHandler = new Handler() {
        // 覆写这个方法，接收并处理消息。
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 200:
                    CacheUtils.setToken(LoginActivity.this,Token);
                    Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    break;
                case 1001:
                    Toast.makeText(LoginActivity.this,"账号或者密码不能为空",Toast.LENGTH_SHORT).show();
                    break;
                case 1002:
                    Toast.makeText(LoginActivity.this,"该邮箱已经未注册，请重新登录",Toast.LENGTH_SHORT).show();
                    break;
                case 1003:
                    Toast.makeText(LoginActivity.this,"账号或者密码出错",Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(LoginActivity.this,"请求失败",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        login=findViewById(R.id.login);
        to_register = findViewById(R.id.to_registered);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneContent = phone.getText().toString();
                String passContent = password.getText().toString();
                String url = getString(R.string.baseurl)+"user";
                OkHttpClient okHttpClient = new OkHttpClient();
                RequestBody requestBody = new FormBody.Builder()
                        .add("phonenum", phoneContent)
                        .add("password", passContent)
                        .build();
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();

                okHttpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.d(TAG, response.protocol() + " " + response.code() + " " + response.message());
                        Headers headers = response.headers();
//                        for (int i = 0; i < headers.size(); i++) {
//                            Log.d(TAG, headers.name(i) + ":" + headers.value(i));
//                        }
                        String jsonString = response.body().string();
                        JSONObject jsonCont = null;
                        try {
                            Message msg = new Message();
                            jsonCont = new JSONObject(jsonString);
                            status = jsonCont.getString("status");
                            if(status.equals("200")) {
                                msg.what = 200;
                                Token = jsonCont.getJSONObject("data").getString("token");
                                UserDataApp userDataApp = (UserDataApp)getApplication();
                                userDataApp.setYuanzhuoId(jsonCont.getJSONObject("data").getString("yuanzhuoId"));
                                userDataApp.setImageurl(jsonCont.getJSONObject("data").getString("imageurl"));
//                                    userDataApp.setPhone(jsonCont.getJSONObject("data").getString("imageurl"));
                                userDataApp.setUsername(jsonCont.getJSONObject("data").getString("nickaneme"));
                                userDataApp.setUserToken(jsonCont.getJSONObject("data").getString("token"));
                            }else if(status.equals("1001")) {
                                msg.what = 1001;
                            }else if(status.equals("1002")){
                                msg.what = 1002;
                            }else if(status.equals("1003")){
                                msg.what = 1003;
                            }else {
                                msg.what = 0;
                            }
                            uiHandler.sendMessage(msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


            }
        });
        to_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

}
