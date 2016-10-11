package com.rootming.inpaintcamera;


import com.yanzi.util.FileUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SelectPerfectActivity extends Activity implements OnPageChangeListener {
	
	private int photocount, selected = 0;
	/**
	 * ViewPager
	 */
	private ViewPager viewPager;
	
	/**
	 * 装点点的ImageView数组
	 */
	private ImageView[] tips;
	
	/**
	 * 装ImageView数组
	 */
	private ImageView[] mImageViews;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_perfect);
		ViewGroup group = (ViewGroup)findViewById(R.id.viewGroup);
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		
		photocount = getIntent().getIntExtra("photocount", 0);
		if(photocount < 3)
		{
			Toast.makeText(getApplicationContext(), "连拍照片过少",Toast.LENGTH_SHORT).show();
			startActivity(new Intent(SelectPerfectActivity.this,FullscreenActivity.class));
			finish();
		}
				
		//将点点加入到ViewGroup中
		tips = new ImageView[photocount];
		for(int i=0; i<tips.length; i++){
			ImageView imageView = new ImageView(this);
	    	imageView.setLayoutParams(new LayoutParams(10,10));
	    	tips[i] = imageView;
	    	if(i == 0){
	    		tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
	    	}else{
	    		tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
	    	}
	    	
	    	LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
	    	layoutParams.leftMargin = 5;
	    	layoutParams.rightMargin = 5;
	    	group.addView(imageView, layoutParams);
		}
		
		String path = FileUtil.initPath();
		
		//将图片装载到数组中
		mImageViews = new ImageView[photocount];
		for(int i=0; i<mImageViews.length; i++){
			ImageView imageView = new ImageView(this);
			mImageViews[i] = imageView;
			Bitmap bm = BitmapFactory.decodeFile(path+"/tmp/"+(i+1)+".jpg");
			imageView.setImageBitmap(bm);
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					SelectPerfectActivity.this.openOptionsMenu();
				}
			});
		}
		
		//设置Adapter
		viewPager.setAdapter(new MyAdapter());
		//设置监听，主要是设置点点的背景
		viewPager.setOnPageChangeListener(this);
		//设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
		viewPager.setCurrentItem((mImageViews.length) * 100);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("请选择主体部分最完美的一张照片\n点击菜单键结束");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new AlertDialog.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) finish();
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, Menu.FIRST + 1, 0, "确认");
		menu.add(Menu.NONE, Menu.FIRST + 2, 0, "取消");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId())// 得到被点击的item的itemId
		{
			case Menu.FIRST + 1: // 对应的ID就是在add方法中所设定的Id
				Intent intent = new Intent();
				intent.putExtra("photocount", photocount);
				intent.putExtra("photonum", selected + 1);
				intent.setClass(SelectPerfectActivity.this, RectActivity.class);
				//startActivityForResult(intent, 0);
				startActivity(intent);
				break;
			case Menu.FIRST + 2:
				startActivity(new Intent(SelectPerfectActivity.this,FullscreenActivity.class));
				finish();
				break;
		}
		return true;
	}
	
	/**
	 * 
	 * @author xiaanming
	 *
	 */
	public class MyAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			//((ViewPager)container).removeView(mImageViews[position % mImageViews.length]);
			
		}

		/**
		 * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
		 */
		@Override
		public Object instantiateItem(View container, int position) {
			try {
				((ViewPager)container).addView(mImageViews[position % mImageViews.length], 0);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return mImageViews[position % mImageViews.length];
		}
		
		
		
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int arg0) {
		selected = arg0 % mImageViews.length;
		setImageBackground(selected);
	}
	
	/**
	 * 设置选中的tip的背景
	 * @param selectItems
	 */
	private void setImageBackground(int selectItems){
		for(int i=0; i<tips.length; i++){
			if(i == selectItems){
				tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
			}else{
				tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
			}
		}
	}
}
