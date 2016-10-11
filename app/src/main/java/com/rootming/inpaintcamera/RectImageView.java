package com.rootming.inpaintcamera;

import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class RectImageView extends ImageView {
	public RectImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private Paint p;
	public float prevX = 0,prevY = 0,curX = 0,curY = 0,newprevX = 0,newprevY = 0,newcurX = 0,newcurY = 0;
	
	@Override  
    public void onDraw(Canvas canvas) {  
        super.onDraw(canvas);
        try {
			canvas.drawRect(prevX,prevY,curX,curY, p);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		p = new Paint();
        p.setColor(Color.RED);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(4);
		this.setOnTouchListener(new myTouchListener());
	}
	
	protected class myTouchListener implements OnTouchListener {
		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			//tv.setText("rawx="+String.valueOf(arg1.getRawX())+"rawy="+String.valueOf(arg1.getRawY())+"\n");  
            //tv.append("x="+String.valueOf(arg1.getX())+"y="+String.valueOf(arg1.getY())+"\n");  
            
            int action =arg1.getAction();  
            if(action==MotionEvent.ACTION_MOVE){  
            	curX = arg1.getX();
                curY = arg1.getY();
                invalidate();
            }  
            if(action==MotionEvent.ACTION_DOWN){  
                prevX = arg1.getX();
                prevY = arg1.getY();
                float[] xy = getPointerCoords((ImageView)arg0,arg1);
            	newprevX = xy[0];
            	newprevY = xy[1];
            }  
            if(action==MotionEvent.ACTION_UP){  
            	//curX = arg1.getX();
                //curY = arg1.getY();  
            	float[] xy = getPointerCoords((ImageView)arg0,arg1);
            	newcurX = xy[0];
            	newcurY = xy[1];
                invalidate();
//				Instrumentation inst = new Instrumentation();
//				inst.sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);

            }  
            if(action==MotionEvent.ACTION_CANCEL){  
                //System.out.println("cancel");  
            }
			return true;
		}  
	};
	
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
