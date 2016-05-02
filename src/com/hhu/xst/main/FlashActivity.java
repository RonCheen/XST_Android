package com.hhu.xst.main;


import java.util.Timer;
import java.util.TimerTask;

import com.hhu.xst.function.LoginActivity;
import com.jereh.slidingdemo.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.Window;
/**
 * SplashActivity
 * @author agnes
 *��������Ϊһ��������棬��������
 *ʵ�����м����������������
 */
public class FlashActivity extends Activity {
	static Timer timer = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_flash);
		
        
		//�ӳ�ʱ��
		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
						Intent intent = new Intent();
						intent.setClass(FlashActivity.this,LoginActivity.class);
						startActivity(intent);
						finish();
			}
		}, 1000);
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.gc();
	}
}
