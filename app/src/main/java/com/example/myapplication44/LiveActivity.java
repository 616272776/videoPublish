package com.example.myapplication44;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.lake.librestreaming.core.listener.RESConnectionListener;
import me.lake.librestreaming.filter.hardvideofilter.BaseHardVideoFilter;
import me.lake.librestreaming.filter.hardvideofilter.HardVideoGroupFilter;
import me.lake.librestreaming.ws.StreamAVOption;
import me.lake.librestreaming.ws.StreamLiveCameraView;
import me.lake.librestreaming.ws.filter.hardfilter.GPUImageBeautyFilter;
import me.lake.librestreaming.ws.filter.hardfilter.WatermarkFilter;
import me.lake.librestreaming.ws.filter.hardfilter.extra.GPUImageCompatibleFilter;


public class LiveActivity extends AppCompatActivity {

    private static final String TAG = LiveActivity.class.getSimpleName();
    private StreamLiveCameraView mLiveCameraView;
    private StreamAVOption streamAVOption;
    private String rtmpUrl = "rtmp://192.168.3.30:1935/live/a";
    private LiveUI mLiveUI;
    private final String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        // 版本大于21时
        StatusBarUtils.setTranslucentStatus(this);

        // 权限
        List<String> unPermission = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                unPermission.add(permission);
            }
        }
        if (unPermission.size() > 0) {
            ActivityCompat.requestPermissions(this, unPermission.toArray(new String[]{}), 1);
        }

        // 初始化配置
        initLiveConfig();

        // 直播ui
        mLiveUI = new LiveUI(this,mLiveCameraView,rtmpUrl);
    }

    /**
     * 设置推流参数
     */
    public void initLiveConfig() {
        mLiveCameraView = (StreamLiveCameraView) findViewById(R.id.stream_previewView);
//        mLiveCameraView = new StreamLiveCameraView(this);
        //参数配置 start
        streamAVOption = new StreamAVOption();
        streamAVOption.cameraIndex=0;
        streamAVOption.streamUrl = rtmpUrl;
        //参数配置 end

        mLiveCameraView.init(this, streamAVOption);
        mLiveCameraView.addStreamStateListener(resConnectionListener);

        // 添加过滤使用
//        LinkedList<BaseHardVideoFilter> files = new LinkedList<>();

//        files.add(new GPUImageCompatibleFilter(new GPUImageBeautyFilter()));
        // 水印
//        files.add(new WatermarkFilter(BitmapFactory.decodeResource(getResources(),R.mipmap.live),new Rect(100,100,200,200)));
//        mLiveCameraView.setHardVideoFilter(new HardVideoGroupFilter(files));
    }

    // 直播监听
    RESConnectionListener resConnectionListener = new RESConnectionListener() {
        @Override
        public void onOpenConnectionResult(int result) {
            //result 0成功  1 失败
            Toast.makeText(LiveActivity.this,"打开推流连接 状态："+result+ " 推流地址："+rtmpUrl,Toast.LENGTH_LONG).show();
        }

        @Override
        public void onWriteError(int errno) {
            Toast.makeText(LiveActivity.this,"推流出错,请尝试重连",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCloseConnectionResult(int result) {
            //result 0成功  1 失败
            Toast.makeText(LiveActivity.this,"关闭推流连接 状态："+result,Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLiveCameraView.destroy();
    }
}
