package com.fht.yuanzhuo.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.Toast;

import com.fht.yuanzhuo.Activity.MainActivity;
import com.fht.yuanzhuo.R;
import com.fht.yuanzhuo.UserDataApp;
import com.fht.yuanzhuo.Utils.CacheUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class SplashActivity extends Activity {
    public static final String START_MAIN = "start_main";
    private ConstraintLayout ll_splash;
    private Intent intent;

    private String status = new String();
    private String message = new String();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ll_splash = findViewById(R.id.ll_splash);
        intent = new Intent(SplashActivity.this, LoginActivity.class);

        AlphaAnimation aa = new AlphaAnimation(0,1);
//        aa.setDuration(500);
        aa.setFillAfter(true);

        AnimationSet set = new AnimationSet(false);
        set.addAnimation(aa);
        set.setDuration(2500);

        ll_splash.startAnimation(set);
        set.setAnimationListener(new MyAnimation());



    }

    class MyAnimation implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {
            boolean isStartMain = CacheUtils.getBoolea(SplashActivity.this,START_MAIN);
            if(isStartMain){
                String isLoginning = CacheUtils.getToken(SplashActivity.this);
                if(isLoginning.isEmpty()){
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }else {
                    String url = getString(R.string.baseurl)+"token/"+ isLoginning;
                    OkHttpClient okHttpClient = new OkHttpClient();
                    final Request request = new Request.Builder()
                            .url(url)
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
                                jsonCont = new JSONObject(jsonString);
                                status = jsonCont.getString("status");
                                message = jsonCont.getString("message");
                                if(status.equals("200")) {
                                   intent = new Intent(SplashActivity.this, MainActivity.class);
                                    UserDataApp userDataApp = (UserDataApp)getApplication();
                                    userDataApp.setYuanzhuoId(jsonCont.getJSONObject("data").getString("yuanzhuoId"));
                                    userDataApp.setImageurl(jsonCont.getJSONObject("data").getString("imageurl"));
//                                    userDataApp.setPhone(jsonCont.getJSONObject("data").getString("imageurl"));
                                    userDataApp.setUsername(jsonCont.getJSONObject("data").getString("nickaneme"));
                                    userDataApp.setUserToken(jsonCont.getJSONObject("data").getString("token"));
                                }else {
                                   CacheUtils.removeToken(SplashActivity.this);
                                   intent = new Intent(SplashActivity.this, LoginActivity.class);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }else {
                intent = new Intent(SplashActivity.this, GuideActivity.class);
            }

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if(message == null){
                message = "验证网络失败，请重新进入应用或者重新登录";
            }
            Toast.makeText(SplashActivity.this,message,Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
