package com.example.lucky_pan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class LuckPan extends SurfaceView implements Callback, Runnable {

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
	 private Bitmap[] mImgsBitmap;
	  private Bitmap mBcgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg2);   //背景设置

    //背景图
   /*
  * 盘块得奖项
    */
    private String[]mStrs = new String[]{"单反相机","IPAD","恭喜发财","IPHONE","服装一套","恭喜发财"};
    private final int[] mAwardsImgs = new int[]{R.drawable.danfan, R.drawable.ipad, R.drawable.f040, R.drawable.iphone, R.drawable.meizi, R.drawable.f040};
    //盘块的奖项图片
    private int[] mColors = {0xFFFFC300, 0xFFF17E01,0xFFFFC300, 0xFFF17E01,0xFFFFC300, 0xFFF17E01};
   
    //盘块的数量
    private final int mItemCount = 6;
    //判断是否点击了停止按钮的标志
    private boolean isShouldEnd;
    //转盘的中心位置
    private int mCenter;
    //直接以padding值为准（或者取left、right、top、bottom中设置的最小的）
    private int mPadding;
    
  
    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics());
    
    //整个盘块的范围
    private RectF mRange = new RectF();
    //整个盘快的直径
    private int mRadius;
    //绘制盘块、文本的画笔
    private Paint mArcPaint, mTextPaint;
    //盘块滚动的速度(即转盘每隔mSpeed设置的角度重绘一次,但绘制的时间间隔不变)
    private double mSpeed;
    //起始角度(设置为float而非int，因为转盘存在某些逻辑会使得mStartAngle带有小数，如果为int会失去精度对指定奖项时的计算产生影响)
    private volatile float mStartAngle = 0;//可能会存在于两个线程，同时更新
    
	public LuckPan(Context context) {
		this(context,null);
		// TODO Auto-generated constructor stub
	}

	public LuckPan(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mHolder = getHolder();
		
		mHolder.addCallback(this);
		
		//可获得焦点
		setFocusable(true);
		setFocusableInTouchMode(true);
		//设置常亮
		setKeepScreenOn(true);
	}
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = Math.min(getMeasuredWidth(), getMeasuredHeight());

        mPadding = getPaddingLeft();
        //半径
        mRadius = width - mPadding * 2;
        //中心点
        mCenter = width / 2;
        
        //宽高设置成相同
        setMeasuredDimension(width, width);
    }
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//初始化h绘制盘块的画笔
		mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);
        
        //初始化文本画笔
        mTextPaint = new Paint();
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setTextSize(mTextSize);//设置文字大小
        //初始化盘快绘制的范围（mRadius已经减去了mPadding）
        mRange = new RectF(mPadding, mPadding, mRadius + mPadding, mRadius + mPadding);
	
      //初始化图片
        mImgsBitmap = new Bitmap[mItemCount];
        
        for (int i = 0; i < mItemCount; i++)
            mImgsBitmap[i] = BitmapFactory.decodeResource(getResources(), mAwardsImgs[i]);
        
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
			long start = System.currentTimeMillis();  //获取时间
			draw();
			long end = System.currentTimeMillis();   //获取时间
		     if (end - start < 100) {
	                try {
	                    Thread.sleep(100 - (end - start));
	                } catch (InterruptedException e) {
	                    e.printStackTrace();
	                }
		    }
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
            	//绘制背景
            	drawbg();
            	
            	//绘制盘块
            	float tmpAngle = mStartAngle;   //保存起始角度
            	
            	float sweepAngle = 360/mItemCount;
            	 for (int i = 0; i < mItemCount; i++) 
            	 {
            		 mArcPaint.setColor(mColors[i]);   //选取颜色
            		 //均分绘制盘块
            		 mCanvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint); 
            	     
            		 //绘制文本
            		 drawText(tmpAngle,sweepAngle,mStrs[i]);
            		 //绘制icon
            		 drawIcon(tmpAngle,mImgsBitmap[i]);
            		 
            	     tmpAngle+=sweepAngle;
            	     
            	 }
            	mStartAngle+=mSpeed;
                //如果点击了停止按钮，使得转盘缓缓停止
                if (isShouldEnd)
                    mSpeed -= 1;
                if (mSpeed <= 0) {
                    mSpeed = 0;
                    isShouldEnd = false;
                }
            	
            }
        } catch (Exception e) {
        } finally {
            if (mCanvas != null)
                mHolder.unlockCanvasAndPost(mCanvas);
        }
    }
   
	
	//启动函数
	public void luckyStart(int index)
	{
		//计算每一项角度
		float angle=360/mItemCount;
		
		//计算每一项的中将范围(当前index)
		float from=270-(index+1)*angle ;
		float end =from+angle;
		
		//设置停下来需要旋转的距离区间
		float targetFrom=4*360+from;
		float targetEnd=4*360+end;
		
		//v1->0 且每次-1 
		//起始速度(v1+0)  终止速度(v1+1)   (v1+0)*(v1+1) /2=targetFrom
		
		float v1=(float) ((-1+Math.sqrt(1+8*targetFrom))/2);
		float v2=(float) ((-1+Math.sqrt(1+8*targetEnd))/2);
		
		mSpeed=v1+Math.random()*(v2-v1);   //旋转速度
		isShouldEnd=false;       //停止按钮状态
	}
	
	//停止函数
	public void luckyEnd()
	{
		mStartAngle=0;
		isShouldEnd=true;
	}
	
	//转盘是否在旋转
	public boolean isStart(){
		
	  return mSpeed!=0;
		
	}
	
	//停止按钮是否按下
	public boolean isShouldEnd()
	{
		
	  return isShouldEnd;
	}
	
	//绘制icon
	private void drawIcon(float tmpAngle, Bitmap bitmap) {
		//约束图片宽度为直径的1/8；
		int imgWidth =mRadius/8;
		
		float angle=(float) ((tmpAngle+360/mItemCount/2)*Math.PI/180);
		
		//图片中心点坐标
		int x=(int) (mCenter+mRadius/2/2*Math.cos(angle));
		int y=(int) (mCenter+mRadius/2/2*Math.sin(angle));
		
		//确定图片位置
		Rect rect=new Rect(x-imgWidth/2,y-imgWidth/2,x+imgWidth/2,y+imgWidth/2);
		
		mCanvas.drawBitmap(bitmap,null,rect,null);
		
		
	}

	//绘制每个盘块的文本
	private void drawText(float tmpAngle, float sweepAngle, String string) {
		// TODO Auto-generated method stub
		Path path = new Path();
        path.addArc(mRange, tmpAngle, sweepAngle);
        //利用水平偏移让文字居中
        float textWidth=mTextPaint.measureText(string);
        int hOffset = (int) (mRadius * Math.PI / mItemCount / 2 -textWidth/2);
        
        int vOffset=mRadius/2/6;  //垂直偏移量
        
       mCanvas.drawTextOnPath(string, path, hOffset, vOffset, mTextPaint);
	}

	//绘制背景
	private void drawbg() {
		// TODO Auto-generated method stub
		//绘制背景
        mCanvas.drawColor(0xffffffff);
        mCanvas.drawBitmap(mBcgBitmap, null, new Rect(mPadding / 2, mPadding / 2, getMeasuredWidth() - mPadding / 2, getMeasuredWidth() - mPadding / 2), null);

		
	}
}

