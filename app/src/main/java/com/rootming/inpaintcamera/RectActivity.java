package com.rootming.inpaintcamera;

import java.io.File;

import com.rootming.inpaintcamera.RectImageView;
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupMenu;

public class RectActivity extends Activity {

    private Handler mHandler = new Handler();
    private int photocount, photonum;
    private RectImageView iv;
    private Bitmap orgBitmap;

    //rootming
    private PopupMenu popmenu;
    private Menu menu;

    static {
        System.loadLibrary("inpaint");
    }

    public native void ProcImg(int len, String paths, String savepath,int photonum, float prevX, float prevY, float curX, float curY);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rect);

        popmenu = new PopupMenu(this, findViewById(R.id.button));
        menu = popmenu.getMenu();
        menu.add(Menu.NONE, Menu.FIRST + 1, 0, "确认");
        menu.add(Menu.NONE, Menu.FIRST + 2, 0, "取消");
        popmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //super.onOptionsItemSelected(item);
                switch (item.getItemId())// 得到被点击的item的itemId
                {
                    case Menu.FIRST + 1: // 对应的ID就是在add方法中所设定的Id
                        final ProgressDialog MyDialog = ProgressDialog.show(RectActivity.this, "", "正在处理图片...", true, false);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String paths = "";
                                String path = FileUtil.initPath();
                                long dataTake = System.currentTimeMillis();
                                final String outName = path + "/" + dataTake + ".jpg";
                                paths += path + "/tmp/1.jpg";
                                for (int i = 2; i <= photocount; i++) {
                                    paths += "*" + path + "/tmp/" + i + ".jpg";
                                }
                                ProcImg(photocount, paths, outName, photonum, iv.newprevX, iv.newprevY, iv.newcurX, iv.newcurY);
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
                                        Intent intent = new Intent();
//                                        i.setDataAndType(Uri.fromFile(new File(outName)), "image/*");
//                                        i.setAction(Intent.ACTION_VIEW);
//                                        startActivity(i);
//                                        setResult(RESULT_OK, new Intent());
                                        intent.putExtra("filepath", outName);
                                        intent.setClass(RectActivity.this, FixActivity.class);
                                        startActivityForResult(intent, 0);

                                        finish();
                                    }
                                });
                            }
                        }).start();
                        break;
                    case Menu.FIRST + 2:
                        setResult(RESULT_CANCELED, new Intent());
                        finish();
                        break;
                }
                return true;
            }
        });


        photocount = getIntent().getIntExtra("photocount", 0);
        photonum = getIntent().getIntExtra("photonum", 1);

        iv = (RectImageView) findViewById(R.id.imageview_rect);

        String path = FileUtil.initPath();
        orgBitmap = BitmapFactory.decodeFile(path+"/tmp/"+photonum+".jpg");
        iv.setImageBitmap(orgBitmap);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("请在照片上拖动以用矩形选择主体部分\n按菜单键结束");
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
//		menu.add(Menu.NONE, Menu.FIRST + 2, 0, "取消");
//		return true;
//	}

//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		super.onOptionsItemSelected(item);
//		switch (item.getItemId())// 得到被点击的item的itemId
//		{
//			case Menu.FIRST + 1: // 对应的ID就是在add方法中所设定的Id
//				final ProgressDialog MyDialog = ProgressDialog.show(RectActivity.this, "", "正在处理图片...", true, false);
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//						String paths = "";
//						String path = FileUtil.initPath();
//						long dataTake = System.currentTimeMillis();
//						final String outName = path + "/" + dataTake + ".jpg";
//						paths += path + "/tmp/1.jpg";
//						for (int i = 2; i <= photocount; i++) {
//							paths += "*" + path + "/tmp/" + i + ".jpg";
//						}
//						ProcImg(photocount, paths, outName, photonum, iv.newprevX, iv.newprevY, iv.newcurX, iv.newcurY);
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
//								i.setDataAndType(Uri.fromFile(new File(outName)), "image/*");
//								i.setAction(Intent.ACTION_VIEW);
//								startActivity(i);
//								setResult(RESULT_OK, new Intent());
//								finish();
//							}
//						});
//					}
//				}).start();
//				break;
//			case Menu.FIRST + 2:
//				setResult(RESULT_CANCELED, new Intent());
//				finish();
//				break;
//		}
//		return true;
//	}
}
