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
	 * ���ڻ��Ƶ��߳� 
	 */
	private Thread t;

	/**
	 * �̵߳Ŀ��ƿ���
	 */
	private boolean isRunning;
	 private Bitmap[] mImgsBitmap;
	  private Bitmap mBcgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg2);   //��������

    //����ͼ
   /*
  * �̿�ý���
    */
    private String[]mStrs = new String[]{"�������","IPAD","��ϲ����","IPHONE","��װһ��","��ϲ����"};
    private final int[] mAwardsImgs = new int[]{R.drawable.danfan, R.drawable.ipad, R.drawable.f040, R.drawable.iphone, R.drawable.meizi, R.drawable.f040};
    //�̿�Ľ���ͼƬ
    private int[] mColors = {0xFFFFC300, 0xFFF17E01,0xFFFFC300, 0xFFF17E01,0xFFFFC300, 0xFFF17E01};
   
    //�̿������
    private final int mItemCount = 6;
    //�ж��Ƿ�����ֹͣ��ť�ı�־
    private boolean isShouldEnd;
    //ת�̵�����λ��
    private int mCenter;
    //ֱ����paddingֵΪ׼������ȡleft��right��top��bottom�����õ���С�ģ�
    private int mPadding;
    
  
    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics());
    
    //�����̿�ķ�Χ
    private RectF mRange = new RectF();
    //�����̿��ֱ��
    private int mRadius;
    //�����̿顢�ı��Ļ���
    private Paint mArcPaint, mTextPaint;
    //�̿�������ٶ�(��ת��ÿ��mSpeed���õĽǶ��ػ�һ��,�����Ƶ�ʱ��������)
    private double mSpeed;
    //��ʼ�Ƕ�(����Ϊfloat����int����Ϊת�̴���ĳЩ�߼���ʹ��mStartAngle����С�������Ϊint��ʧȥ���ȶ�ָ������ʱ�ļ������Ӱ��)
    private volatile float mStartAngle = 0;//���ܻ�����������̣߳�ͬʱ����
    
	public LuckPan(Context context) {
		this(context,null);
		// TODO Auto-generated constructor stub
	}

	public LuckPan(Context context, AttributeSet attrs) {
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = Math.min(getMeasuredWidth(), getMeasuredHeight());

        mPadding = getPaddingLeft();
        //�뾶
        mRadius = width - mPadding * 2;
        //���ĵ�
        mCenter = width / 2;
        
        //������ó���ͬ
        setMeasuredDimension(width, width);
    }
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		//��ʼ��h�����̿�Ļ���
		mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);
        
        //��ʼ���ı�����
        mTextPaint = new Paint();
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setTextSize(mTextSize);//�������ִ�С
        //��ʼ���̿���Ƶķ�Χ��mRadius�Ѿ���ȥ��mPadding��
        mRange = new RectF(mPadding, mPadding, mRadius + mPadding, mRadius + mPadding);
	
      //��ʼ��ͼƬ
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
		//���Ͻ��л���
		while(isRunning)
		{
			long start = System.currentTimeMillis();  //��ȡʱ��
			draw();
			long end = System.currentTimeMillis();   //��ȡʱ��
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
            	//���Ʊ���
            	drawbg();
            	
            	//�����̿�
            	float tmpAngle = mStartAngle;   //������ʼ�Ƕ�
            	
            	float sweepAngle = 360/mItemCount;
            	 for (int i = 0; i < mItemCount; i++) 
            	 {
            		 mArcPaint.setColor(mColors[i]);   //ѡȡ��ɫ
            		 //���ֻ����̿�
            		 mCanvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint); 
            	     
            		 //�����ı�
            		 drawText(tmpAngle,sweepAngle,mStrs[i]);
            		 //����icon
            		 drawIcon(tmpAngle,mImgsBitmap[i]);
            		 
            	     tmpAngle+=sweepAngle;
            	     
            	 }
            	mStartAngle+=mSpeed;
                //��������ֹͣ��ť��ʹ��ת�̻���ֹͣ
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
   
	
	//��������
	public void luckyStart(int index)
	{
		//����ÿһ��Ƕ�
		float angle=360/mItemCount;
		
		//����ÿһ����н���Χ(��ǰindex)
		float from=270-(index+1)*angle ;
		float end =from+angle;
		
		//����ͣ������Ҫ��ת�ľ�������
		float targetFrom=4*360+from;
		float targetEnd=4*360+end;
		
		//v1->0 ��ÿ��-1 
		//��ʼ�ٶ�(v1+0)  ��ֹ�ٶ�(v1+1)   (v1+0)*(v1+1) /2=targetFrom
		
		float v1=(float) ((-1+Math.sqrt(1+8*targetFrom))/2);
		float v2=(float) ((-1+Math.sqrt(1+8*targetEnd))/2);
		
		mSpeed=v1+Math.random()*(v2-v1);   //��ת�ٶ�
		isShouldEnd=false;       //ֹͣ��ť״̬
	}
	
	//ֹͣ����
	public void luckyEnd()
	{
		mStartAngle=0;
		isShouldEnd=true;
	}
	
	//ת���Ƿ�����ת
	public boolean isStart(){
		
	  return mSpeed!=0;
		
	}
	
	//ֹͣ��ť�Ƿ���
	public boolean isShouldEnd()
	{
		
	  return isShouldEnd;
	}
	
	//����icon
	private void drawIcon(float tmpAngle, Bitmap bitmap) {
		//Լ��ͼƬ���Ϊֱ����1/8��
		int imgWidth =mRadius/8;
		
		float angle=(float) ((tmpAngle+360/mItemCount/2)*Math.PI/180);
		
		//ͼƬ���ĵ�����
		int x=(int) (mCenter+mRadius/2/2*Math.cos(angle));
		int y=(int) (mCenter+mRadius/2/2*Math.sin(angle));
		
		//ȷ��ͼƬλ��
		Rect rect=new Rect(x-imgWidth/2,y-imgWidth/2,x+imgWidth/2,y+imgWidth/2);
		
		mCanvas.drawBitmap(bitmap,null,rect,null);
		
		
	}

	//����ÿ���̿���ı�
	private void drawText(float tmpAngle, float sweepAngle, String string) {
		// TODO Auto-generated method stub
		Path path = new Path();
        path.addArc(mRange, tmpAngle, sweepAngle);
        //����ˮƽƫ�������־���
        float textWidth=mTextPaint.measureText(string);
        int hOffset = (int) (mRadius * Math.PI / mItemCount / 2 -textWidth/2);
        
        int vOffset=mRadius/2/6;  //��ֱƫ����
        
       mCanvas.drawTextOnPath(string, path, hOffset, vOffset, mTextPaint);
	}

	//���Ʊ���
	private void drawbg() {
		// TODO Auto-generated method stub
		//���Ʊ���
        mCanvas.drawColor(0xffffffff);
        mCanvas.drawBitmap(mBcgBitmap, null, new Rect(mPadding / 2, mPadding / 2, getMeasuredWidth() - mPadding / 2, getMeasuredWidth() - mPadding / 2), null);

		
	}
}

