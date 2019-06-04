package com.example.lucky_pan;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MainActivity extends Activity {
	
	private LuckPan mLuckPan;
	private ImageView mStartBtn;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        

        
        mLuckPan = (LuckPan) findViewById(R.id.id_Luckpan);
        mStartBtn = (ImageView) findViewById(R.id.id_start_btn);
        
        mStartBtn.setOnClickListener(new OnClickListener()
        {
        	@Override
        	public void onClick(View v)
        	{
        		if(!mLuckPan.isStart())      //¿ªÊ¼³é½±
        		{
        			mLuckPan.luckyStart(1);
        			mStartBtn.setImageResource(R.drawable.stop);
        		}else
				{
					if(!mLuckPan.isShouldEnd())   //Í£Ö¹³é½±
					{
						mLuckPan.luckyEnd();
						mStartBtn.setImageResource(R.drawable.start);
					}
				}
        	}
    	
        });
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
