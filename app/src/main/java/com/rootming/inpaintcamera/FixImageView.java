package com.rootming.inpaintcamera;

import com.yanzi.util.FileUtil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class FixImageView extends ImageView {
	public FixImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private Paint p;
	private float prevX = -1,prevY = -1,realprevX = -1,realprevY = -1;
	private Bitmap orgbmp,maskbmp,realmaskbmp;
	private Canvas c,realc;

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(maskbmp, 0, 0, p);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		maskbmp = Bitmap.createBitmap(bm.getWidth(),bm.getHeight(),bm.getConfig());
		c = new Canvas(maskbmp);
		realmaskbmp = Bitmap.createBitmap(bm.getWidth(),bm.getHeight(),bm.getConfig());
		realc = new Canvas(realmaskbmp);
		orgbmp = bm;
		super.setImageBitmap(bm);
		p = new Paint();
		p.setColor(Color.WHITE);
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(15);
		this.setOnTouchListener(new myTouchListener());
	}

	protected class myTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			//tv.setText("rawx="+String.valueOf(arg1.getRawX())+"rawy="+String.valueOf(arg1.getRawY())+"\n");  
			//tv.append("x="+String.valueOf(arg1.getX())+"y="+String.valueOf(arg1.getY())+"\n");

			int action =arg1.getAction();
			if(action==MotionEvent.ACTION_MOVE){
				float[] xy = getPointerCoords((ImageView)arg0,arg1);
				float realcurX = xy[0];
				float realcurY = xy[1];
				float curX = arg1.getX();
				float curY = arg1.getY();
				if( prevX < 0 || realprevX < 0)
				{
					prevX = curX;
					prevY = curY;
					realprevX = curX;
					realprevY = curY;
				}
				else
				{
					c.drawLine(prevX,prevY,curX,curY, p);
					realc.drawLine(realprevX,realprevY,realcurX,realcurY, p);
					invalidate();
					prevX = curX;
					prevY = curY;
					realprevX = curX;
					realprevY = curY;
				}
			}
			if(action==MotionEvent.ACTION_DOWN){
				float[] xy = getPointerCoords((ImageView)arg0,arg1);
				realprevX = xy[0];
				realprevY = xy[1];
				prevX = arg1.getX();
				prevY = arg1.getY();
			}
			if(action==MotionEvent.ACTION_UP){
				prevX = -1;
				prevY = -1;
				realprevX = -1;
				realprevY = -1;
			}
			if(action==MotionEvent.ACTION_CANCEL){
				//System.out.println("cancel");
			}
			return true;
		}
	};

	public void saveMask() {
		FileUtil.saveMask(realmaskbmp);
	}

	public void clear() {
		maskbmp = Bitmap.createBitmap(orgbmp.getWidth(),orgbmp.getHeight(),orgbmp.getConfig());
		c = new Canvas(maskbmp);
		realmaskbmp = Bitmap.createBitmap(orgbmp.getWidth(),orgbmp.getHeight(),orgbmp.getConfig());
		realc = new Canvas(realmaskbmp);
		invalidate();
	}

	final float[] getPointerCoords(ImageView view, MotionEvent e)
	{
		final int index = e.getActionIndex();
		final float[] coords = new float[] { e.getX(index), e.getY(index) };
		Matrix matrix = new Matrix();
		view.getImageMatrix().invert(matrix);
		matrix.postTranslate(view.getScrollX(), view.getScrollY());
		matrix.mapPoints(coords);
		return coords;
	}
}
