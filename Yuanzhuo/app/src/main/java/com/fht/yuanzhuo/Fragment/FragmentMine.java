package com.fht.yuanzhuo.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fht.yuanzhuo.Activity.DetailActivity;
import com.fht.yuanzhuo.Activity.LoginActivity;
import com.fht.yuanzhuo.Activity.MainActivity;
import com.fht.yuanzhuo.Activity.SplashActivity;
import com.fht.yuanzhuo.R;
import com.fht.yuanzhuo.UserDataApp;
import com.fht.yuanzhuo.Utils.CacheUtils;
import com.leon.lib.settingview.LSettingItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.ContentValues.TAG;


public class FragmentMine extends Fragment{
    private LSettingItem update;
    private LSettingItem download;
    private LSettingItem adven;
    private LSettingItem about;
//    private LSettingItem idea;
//    private LSettingItem mic;
    private LSettingItem logout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.vminepage,container,false);
        update = view.findViewById(R.id.update);
        download = view.findViewById(R.id.download);
        adven = view.findViewById(R.id.adven);
        about =view.findViewById(R.id.about);
//        idea =view.findViewById(R.id.idea);
//        mic = view.findViewById(R.id.mic);
        logout= view.findViewById(R.id.logout);

        update.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                Toast.makeText(getActivity(),"编辑资料",Toast.LENGTH_SHORT).show();
            }
        });


        download.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                Toast.makeText(getActivity(),"我的下载",Toast.LENGTH_SHORT).show();
            }
        });

        adven.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                Toast.makeText(getActivity(),"提交建议",Toast.LENGTH_SHORT).show();
            }
        });

        about.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                Toast.makeText(getActivity(),"关于我们",Toast.LENGTH_SHORT).show();
            }
        });

//        idea.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
//            @Override
//            public void click(boolean isChecked) {
//                if(isChecked){
//                    Toast.makeText(getActivity(),"选中打开消息",Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        mic.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
//            @Override
//            public void click(boolean isChecked) {
//                if(isChecked){
//                    Toast.makeText(getActivity(),"选中打开语音",Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        logout.setmOnLSettingItemClick(new LSettingItem.OnLSettingItemClick() {
            @Override
            public void click(boolean isChecked) {
                CacheUtils.removeToken(getActivity());
                Toast.makeText(getActivity(),"退出成功",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }
}
