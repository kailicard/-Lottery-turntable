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
	 * ���ڻ��Ƶ��߳� 
	 */
	private Thread t;

	/**
	 * �̵߳Ŀ��ƿ���
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
		
		//�ɻ�ý���
		setFocusable(true);
		setFocusableInTouchMode(true);
		//���ó���
		setKeepScreenOn(true);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//���ÿ�ʼ��־
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
		//���Ͻ��л���
		while(isRunning)
		{
			draw();
		}
		
	}
	private void draw() {
        /**
         * try-catch���пյ�ԭ��:
         * ��SurfaceView��������ʱ��������home����back��������ʹ��Surface���٣�
         * ����������֮���п����Ѿ�����÷���ִ����Ӧ���߼��ˣ������Ҫ��mCanvas�����пգ�
         * ���⣬����Surface�����٣������߳�ȴ������ô���ױ��رգ�����ִ��draw something�Ĳ�����
         * ��ʱ���п��ܻ��׳�ĳЩ�쳣
         */
        try {
            //�����õ�Canvas���ڻ���
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

