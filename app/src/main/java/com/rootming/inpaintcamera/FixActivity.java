package com.rootming.inpaintcamera;

import java.io.File;


import com.yanzi.util.FileUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupMenu;

public class FixActivity extends Activity {

	private Handler mHandler = new Handler();
	private String photopath;
	private FixImageView iv;
	private Bitmap orgBitmap;

	//rootming
	private PopupMenu popmenu;
	private Menu menu;

	static {
    	System.loadLibrary("inpaint");
    }

    public native void FixImg(String orgfile, String maskfile, String outfile);
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		
		setContentView(R.layout.activity_fix);

		popmenu = new PopupMenu(this, findViewById(R.id.button));
		menu = popmenu.getMenu();
		menu.add(Menu.NONE, Menu.FIRST + 1, 0, "确认");
		menu.add(Menu.NONE, Menu.FIRST + 2, 0, "清除所有");
		menu.add(Menu.NONE, Menu.FIRST + 3, 0, "取消");
		popmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
           @Override
           public boolean onMenuItemClick(MenuItem item) {
               switch (item.getItemId())// 得到被点击的item的itemId
               {
                   case Menu.FIRST + 1: // 对应的ID就是在add方法中所设定的Id
                       final ProgressDialog MyDialog = ProgressDialog.show(FixActivity.this, "", "正在处理图片...", true, false);
                       iv.saveMask();
                       new Thread(new Runnable() {
                           @Override
                           public void run() {
                               String path = FileUtil.initPath();
                               long dataTake = System.currentTimeMillis();
                               final String outName = path + "/fix" + dataTake +".jpg";
                               FixImg(photopath,path+"/tmp/mask.jpg",outName);
                               try {
                                   Thread.sleep(1000);
                               } catch (InterruptedException e) {
                                   // TODO Auto-generated catch block
                                   e.printStackTrace();
                               }
                               mHandler.post(new Runnable() {
                                   @Override
                                   public void run() {
                                       MyDialog.cancel();
                                       Intent i = new Intent();
                                       i.setDataAndType(Uri.fromFile(new File(outName)),"image/*");
                                       i.setAction(Intent.ACTION_VIEW);
                                       startActivity(i);
                                       setResult(RESULT_OK, new Intent());
                                       finish();
                                   }
                               });
                           }
                       }).start();
                       break;
                   case Menu.FIRST + 2:
                       iv.clear();
                       break;
                   case Menu.FIRST + 3:
                       setResult(RESULT_CANCELED, new Intent());
                       finish();
                       break;
               }
               return true;
           }
		});

		photopath = getIntent().getStringExtra("filepath");
		
		iv = (FixImageView) findViewById(R.id.imageview_fix);
		
		orgBitmap = BitmapFactory.decodeFile(photopath);
		iv.setImageBitmap(orgBitmap);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("请在照片上涂抹需要消去的部分\n按菜单键结束");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

    public void popMenuShow(View v){
        popmenu.show();
    }
	
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		super.onCreateOptionsMenu(menu);
//		menu.add(Menu.NONE, Menu.FIRST + 1, 0, "确认");
//		menu.add(Menu.NONE, Menu.FIRST + 2, 0, "清除所有");
//		menu.add(Menu.NONE, Menu.FIRST + 3, 0, "取消");
//		return true;
//	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		super.onOptionsItemSelected(item);
//		switch (item.getItemId())// 得到被点击的item的itemId
//		{
//			case Menu.FIRST + 1: // 对应的ID就是在add方法中所设定的Id
//				final ProgressDialog MyDialog = ProgressDialog.show(FixActivity.this, "", "正在处理图片...", true, false);
//				iv.saveMask();
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//						String path = FileUtil.initPath();
//						long dataTake = System.currentTimeMillis();
//						final String outName = path + "/fix" + dataTake +".jpg";
//						FixImg(photopath,path+"/tmp/mask.jpg",outName);
//						try {
//							Thread.sleep(1000);
//						} catch (InterruptedException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						mHandler.post(new Runnable() {
//							@Override
//							public void run() {
//								MyDialog.cancel();
//								Intent i = new Intent();
//								i.setDataAndType(Uri.fromFile(new File(outName)),"image/*");
//								i.setAction(Intent.ACTION_VIEW);
//						        startActivity(i);
//								setResult(RESULT_OK, new Intent());
//								finish();
//							}
//						});
//					}
//				}).start();
//				break;
//			case Menu.FIRST + 2:
//				iv.clear();
//				break;
//			case Menu.FIRST + 3:
//				setResult(RESULT_CANCELED, new Intent());
//				finish();
//				break;
//		}
//		return true;
//	}
}
