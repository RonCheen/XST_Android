package com.hhu.xst.function;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.view.KeyEvent;

import com.hhu.xst.main.MainActivity;
import com.jereh.slidingdemo.R;
/**
 * 作为校视通介绍页面
 * @author lenovo
 *
 */
public class AboutXSTActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_xst);
	}
	/**
	 * 按返回键返回上级activity
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent myIntent = new Intent();
            myIntent = new Intent(AboutXSTActivity.this, MainActivity.class);
            startActivity(myIntent);
            this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
