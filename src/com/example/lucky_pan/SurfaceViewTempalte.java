package com.example.lucky_pan;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class SurfaceViewTempalte extends SurfaceView implements Callback, Runnable {

	private SurfaceHolder mHolder;
	private Canvas mCanvas;
	/**
	 * 用于绘制的线程 
	 */
	private Thread t;

	/**
	 * 线程的控制开关
	 */
	private boolean isRunning;
	
	public SurfaceViewTempalte(Context context) {
		this(context,null);
		// TODO Auto-generated constructor stub
	}

	public SurfaceViewTempalte(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mHolder = getHolder();
		
		mHolder.addCallback(this);
		
		//可获得焦点
		setFocusable(true);
		setFocusableInTouchMode(true);
		//设置常量
		setKeepScreenOn(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//设置开始标志
		isRunning = true;
		
		t = new Thread(this);
		t.start();
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isRunning = false;
		
	}

	@Override
	public void run() {
		//不断进行绘制
		while(isRunning)
		{
			draw();
		}
		
	}
	private void draw() {
        /**
         * try-catch与判空的原因:
         * 当SurfaceView在主界面时，如果点击home或者back键，都会使得Surface销毁，
         * 但是在销毁之后，有可能已经进入该方法执行相应的逻辑了，因此需要对mCanvas进行判空，
         * 另外，由于Surface被销毁，但是线程却不是那么容易被关闭，继续执行draw something的操作，
         * 此时就有可能会抛出某些异常
         */
        try {
            //首先拿到Canvas用于绘制
            mCanvas = mHolder.lockCanvas();
            if (mCanvas != null) {
                //TODO draw something
            }
        } catch (Exception e) {
        } finally {
            if (mCanvas != null)
                mHolder.unlockCanvasAndPost(mCanvas);
        }
    }
}

