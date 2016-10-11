package com.rootming.inpaintcamera;

import com.rootming.inpaintcamera.R;
import com.yanzi.util.FileUtil;
import com.yanzi.util.TinyDB;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class MoreActivity extends Activity {

	private EditText tvInterval,tvMaxBurst;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more);
		
		Button fixBtn = (Button) findViewById(R.id.btn_image_fix);
		fixBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "请选择一张图片",Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();  
                /* 开启Pictures画面Type设定为image */  
                intent.setType("image/*");  
                /* 使用Intent.ACTION_GET_CONTENT这个Action */  
                intent.setAction(Intent.ACTION_GET_CONTENT);   
                /* 取得相片后返回本画面 */  
                startActivityForResult(intent, 1);  
			}
		});
		tvInterval = (EditText) findViewById(R.id.editTextInterval);
		tvMaxBurst = (EditText) findViewById(R.id.editTextMaxBurst);
		Button saveBtn = (Button) findViewById(R.id.btn_save);
		saveBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				TinyDB.StoreValue(MoreActivity.this, FullscreenActivity.INTERVAL_TAG, tvInterval.getText().toString());
				TinyDB.StoreValue(MoreActivity.this, FullscreenActivity.MAX_TAG, tvMaxBurst.getText().toString());
				Toast.makeText(getApplicationContext(), "设置保存成功",Toast.LENGTH_SHORT).show();
			}
		});
		ImageButton cameraBtn = (ImageButton) findViewById(R.id.btn_camera);
		cameraBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MoreActivity.this,FullscreenActivity.class));
				finish();
			}
		});
		
		String interval = (String) TinyDB.GetValue(MoreActivity.this, FullscreenActivity.INTERVAL_TAG);
		String max = (String) TinyDB.GetValue(MoreActivity.this, FullscreenActivity.MAX_TAG);
		if(interval.trim().isEmpty()) interval = FullscreenActivity.INTERVAL_DEFAULT;
		if(max.trim().isEmpty()) max = FullscreenActivity.MAX_DEFAULT;
		tvInterval.setText(interval);
		tvMaxBurst.setText(max);
	}
	
	@Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (resultCode == RESULT_OK) {  
            Uri uri = data.getData();  
            String path = FileUtil.getPath(this, uri);
            Log.e("uri", path);  
            Intent i = new Intent(this, FixActivity.class);
            i.putExtra("filepath", path);
            startActivity(i);
        }  
        super.onActivityResult(requestCode, resultCode, data);  
    }  
}
