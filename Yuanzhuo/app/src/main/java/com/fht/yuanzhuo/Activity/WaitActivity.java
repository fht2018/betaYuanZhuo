package com.fht.yuanzhuo.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.icu.lang.UCharacter;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fht.yuanzhuo.R;
import com.fht.yuanzhuo.UserDataApp;
import com.fht.yuanzhuo.Userclass.History;
import com.fht.yuanzhuo.Utils.FileUtils;
import com.zego.zegoaudioroom.ZegoAudioAVEngineDelegate;
import com.zego.zegoaudioroom.ZegoAudioDeviceEventDelegate;
import com.zego.zegoaudioroom.ZegoAudioLiveEvent;
import com.zego.zegoaudioroom.ZegoAudioLiveEventDelegate;
import com.zego.zegoaudioroom.ZegoAudioLivePlayerDelegate;
import com.zego.zegoaudioroom.ZegoAudioLivePublisherDelegate;
import com.zego.zegoaudioroom.ZegoAudioLiveRecordDelegate;
import com.zego.zegoaudioroom.ZegoAudioPrepDelegate2;
import com.zego.zegoaudioroom.ZegoAudioPrepareDelegate;
import com.zego.zegoaudioroom.ZegoAudioRoom;
import com.zego.zegoaudioroom.ZegoAudioRoomDelegate;
import com.zego.zegoaudioroom.ZegoAudioStream;
import com.zego.zegoaudioroom.ZegoAudioStreamType;
import com.zego.zegoaudioroom.ZegoAuxData;
import com.zego.zegoaudioroom.ZegoLoginAudioRoomCallback;
import com.zego.zegoaudioroom.callback.ZegoRoomMessageDelegate;
import com.zego.zegoliveroom.constants.ZegoIM;
import com.zego.zegoliveroom.entity.ZegoAudioFrame;
import com.zego.zegoliveroom.entity.ZegoBigRoomMessage;
import com.zego.zegoliveroom.entity.ZegoConversationMessage;
import com.zego.zegoliveroom.entity.ZegoRoomMessage;
import com.zego.zegoliveroom.entity.ZegoStreamQuality;
import com.zego.zegoliveroom.entity.ZegoUserState;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class WaitActivity extends AppCompatActivity {
    private List<String> filename = new ArrayList<>();
    private List<String> fileurl = new ArrayList<>();
    private List<String> filenickname = new ArrayList<>();

    private MyListAdapter adapter;
    private String roomNum;
    private String role;
    private ListView listfile;
    private String status = new String();
    private String message  = new String();
    private static final String TAG = "ChooseFile";
    private static final int FILE_SELECT_CODE = 0;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    @SuppressLint("HandlerLeak")
    private Handler uiHandler = new Handler() {
        // 覆写这个方法，接收并处理消息。
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 200:
                    adapter.notifyDataSetChanged();
                    break;
                default:
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
        setContentView(R.layout.activity_wait);

        ((UserDataApp)getApplication()).reInitZegoSDK();

        Intent intent=getIntent();
        roomNum  = intent.getStringExtra("roomNum");
        role     = intent.getStringExtra("role");

        String url = getString(R.string.baseurl)+ "filelist?roomnum="+roomNum;
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
                        for(int j = 0;j<jsonCont.getJSONObject("data").length();j++){
                            String url = jsonCont.getJSONObject("data").getJSONObject(String.valueOf(j)).getString("imageurl");
                            fileurl.add(url);
                            filename.add(url.substring(url.lastIndexOf('/') + 1));
                            filenickname.add(jsonCont.getJSONObject("data").getJSONObject(String.valueOf(j)).getString("nickaneme"));
                        }
                        msg.what = 200;
                    }else {
                        msg.what = 0;
                    }
                    uiHandler.sendMessage(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }});
        adapter  = new MyListAdapter();
        listfile = findViewById(R.id.filelist);
        listfile.setAdapter(adapter);

        Button invite=findViewById(R.id.invite);
        invite.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WaitActivity.this,TestActivity.class);
                startActivity(intent);
            }
        });

        Button uploadFiles=findViewById(R.id.uploadFiles);
        uploadFiles.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try {
                    checkPermission();
                    startActivityForResult( Intent.createChooser(intent, "选择上传文件"), FILE_SELECT_CODE);
                } catch (android.content.ActivityNotFoundException ex) {
                    // Potentially direct the user to the Market with a Dialog
                    Toast.makeText(WaitActivity.this, "请先安装文件管理器.", Toast.LENGTH_SHORT).show();
                }
//                startActivityForResult(intent, 1);
            }
        });



        Button getIn=findViewById(R.id.getIn);
        getIn.setOnClickListener( new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WaitActivity.this,ChatRoomActivity.class);
                intent.putExtra("roomNum",roomNum);
                intent.putExtra("role",role);
                startActivity(intent);
                finish();
            }
        });

    }

    private void checkPermission() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);

        } else {
//            Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
            Log.e("消息", "checkPermission: 已经授权！");
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    Log.d(TAG, "File Uri: " + uri.toString());
                    String path = null;
                    try {
                        path = FileUtils.getPath(this, uri);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "File Path: " + path);


                    final File file = new File(path);
                    OkHttpClient okHttpClient = new OkHttpClient();
                    RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"),file);
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("roomnum",roomNum)
                            .addFormDataPart("file",file.getName(),fileBody)
                            .build();
                    Request request = new Request.Builder()
                            .addHeader("X-USER-TOKEN",((UserDataApp)getApplication()).getUserToken())
                            .url(getString(R.string.baseurl)+"/file")
                            .post(requestBody)
                            .build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e(TAG, "failure upload!");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String jsonString = response.body().string();
                            Log.e("body",jsonString);
                        }
                    });
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private class MyListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return filename.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(WaitActivity.this);
            String Fname = filename.get(position);
            String Fnickname = filenickname.get(position);
            tv.setText(Fnickname+":"+ Fname);

            tv.setTextSize(20);
            tv.setGravity(Gravity.CENTER);
            return tv;
        }
    }
}

