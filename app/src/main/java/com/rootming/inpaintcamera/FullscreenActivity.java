package com.rootming.inpaintcamera;

import com.yanzi.camera.CameraInterface;
import com.yanzi.camera.CameraInterface.CamOpenOverCallback;
import com.yanzi.camera.preview.CameraSurfaceView;
import com.yanzi.util.DisplayUtil;
import com.yanzi.util.FileUtil;
import com.yanzi.util.TinyDB;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
//import com.rootming.inpaintcamera.FullscreenActivity.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends Activity implements CamOpenOverCallback{

    //定义按钮蛤的
    public static final String INTERVAL_TAG = "burstinterval";
    public static final String MAX_TAG = "burstmax";
    public static final String INTERVAL_DEFAULT = "1000";
    public static final String MAX_DEFAULT = "15";
    private static final String TAG = "yanzi";
    CameraSurfaceView surfaceView = null;
    ImageButton shutterBtn,moreBtn;
    TextView tv;
    float previewRate = -1f;
    Handler mHandler = new Handler();
    volatile int photocount = 0;
    volatile Boolean stopping = false;
    volatile Boolean capturing = false;
    int interval = 1000;
    int max = 15;

    //加载动态链接库
    static {
        System.loadLibrary("inpaint");
    }

    public native void StabImg(int len, String paths);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        Thread openThread = new Thread(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
                CameraInterface.getInstance().doOpenCamera(FullscreenActivity.this);
            }
        };
        openThread.start();
        setContentView(R.layout.activity_fullscreen);
        initUI();
        initViewParams();

        String sinterval = (String) TinyDB.GetValue(FullscreenActivity.this, INTERVAL_TAG);
        String smax = (String) TinyDB.GetValue(FullscreenActivity.this, MAX_TAG);
        if(sinterval.trim().isEmpty()) sinterval = INTERVAL_DEFAULT;
        if(smax.trim().isEmpty()) smax = MAX_DEFAULT;
        interval = Integer.valueOf(sinterval);
        max = Integer.valueOf(smax);

        shutterBtn.setOnTouchListener(new BtnListeners());
        moreBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FullscreenActivity.this, MoreActivity.class));
                finish();
            }
        });
        (new Thread(r)).start();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        stopping = true;
        CameraInterface.getInstance().doStopCamera();
    }



    @Override
    public void cameraHasOpened() {
        // TODO Auto-generated method stub
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SurfaceHolder holder = surfaceView.getSurfaceHolder();
        CameraInterface.getInstance().doStartPreview(holder, previewRate);
    }

    private void initUI() {
        surfaceView = (CameraSurfaceView)findViewById(R.id.surfaceView);
        shutterBtn = (ImageButton)findViewById(R.id.btn_shutter);
        moreBtn = (ImageButton)findViewById(R.id.btn_more);
        tv = (TextView)findViewById(R.id.textView);
    }
    private void initViewParams() {
        ViewGroup.LayoutParams params = surfaceView.getLayoutParams();
        Point p = DisplayUtil.getScreenMetrics(this);
        params.width = p.x;
        params.height = p.y;
        previewRate = DisplayUtil.getScreenRate(this);
        surfaceView.setLayoutParams(params);

        ViewGroup.LayoutParams p2 = shutterBtn.getLayoutParams();
        p2.width = DisplayUtil.dip2px(this, 80);
        p2.height = DisplayUtil.dip2px(this, 80);
        shutterBtn.setLayoutParams(p2);

    }

    private class BtnListeners implements View.OnTouchListener {

        private Runnable rc = new Runnable() {
            @Override
            public void run() {
                capturing = true;
            }
        };

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // TODO Auto-generated method stub
            switch(v.getId()){
                case R.id.btn_shutter:
                    if(event.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        if(capturing) return false;
                        CameraInterface.getInstance().doAutoFocus();
                        mHandler.postDelayed(rc, 1000);
                    }
                    else if(event.getAction() == MotionEvent.ACTION_UP)
                    {
                        mHandler.removeCallbacks(rc);
                        capturing = false;
                        if (photocount<3)
                        {
                            Toast.makeText(getApplicationContext(), "连拍照片过少",Toast.LENGTH_SHORT).show();
                            tv.setText("");
                        }
                        else
                        {
                            shutterBtn.setEnabled(false);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv.setText("正在转换图像");
                                        }
                                    });
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    FileUtil.transformPhotos(photocount,CameraInterface.previewWidth,CameraInterface.previewHeight);
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv.setText("正在消除抖动");
                                        }
                                    });
                                    String paths = "";
                                    String path = FileUtil.initPath();
                                    long dataTake = System.currentTimeMillis();
                                    final String outName = path + "/" + dataTake +".jpg";
                                    paths += path+"/tmp/1.jpg";
                                    for(int i=2;i<=photocount;i++)
                                    {
                                        paths += "*"+path+"/tmp/"+i+".jpg";
                                    }
                                    StabImg(photocount,paths);
                                    Log.e(TAG, "StabImg successful");
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent();
                                            intent.putExtra("photocount", photocount);
                                            intent.setClass(FullscreenActivity.this, SelectPerfectActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                }
                            }).start();
                        }
                    }
                    break;
                default:break;
            }
            return false;
        }

    }

    private Runnable r = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            while(true)
            {
                if(stopping) break;
                if(capturing)
                {
                    photocount++;
                    CameraInterface.getInstance().doTakePicture(photocount);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText(photocount+"/"+max);
                        }
                    });
                    if(photocount >= max)
                    {
                        capturing = false;
                    }
                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                else
                {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
    };

}
