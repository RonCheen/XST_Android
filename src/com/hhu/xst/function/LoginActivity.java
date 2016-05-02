package com.hhu.xst.function;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hhu.xst.connectutil.LandServer;
import com.hhu.xst.main.MainActivity;
import com.jereh.slidingdemo.R;

/**
 * 注册界面，主要逻辑尚未实现
 * 
 * @author lenovo
 * 
 */
public class LoginActivity extends Activity {

	private Button btlogin;
	private EditText userNameText;
	private EditText passwdText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		// tsj:该activity应该是登录activity，暂时作为连接后端服务器的测试部分
		//start
		System.out.println("come on !!!!!!");
		btlogin = (Button) findViewById(R.id.bnLogin);
		userNameText = (EditText) findViewById(R.id.userNameText);
		passwdText = (EditText) findViewById(R.id.passwdText);
		btlogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				System.out.println("点击事件！！！");
				String username = userNameText.getText().toString();
				String password = passwdText.getText().toString();
				if (username.isEmpty()) {
					Toast.makeText(getApplicationContext(), "用户名不能为空！", 8000);
				} else if (password.isEmpty()) {
					Toast.makeText(getApplicationContext(), "密码不能为空！", 8000);
				} else {
					final Handler handler = new Handler() {
						public void handleMessage(Message msg) {
							super.handleMessage(msg);
							String str = (String) msg.obj;
							System.out.println("服务器返回值："+str);
							if ("true".equals(str)) {
								Toast.makeText(getApplicationContext(), "登录成功",
										8000).show();
								Intent intent = new Intent(LoginActivity.this,
										MainActivity.class);
								startActivity(intent);
							} else {
								Toast.makeText(getApplicationContext(), "登录失败",
										8000).show();
							}
						}
					};
					new Thread(new Runnable() {
						@Override
						public void run() {
							LandServer server = new LandServer();
							String result = server.doPost(userNameText
									.getText().toString(), passwdText.getText()
									.toString());
							Message msg = new Message();
							msg.obj = result;
							handler.sendMessage(msg);

						}
					}).start();
				}
			}
		});
	}

	//end
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			Intent myIntent = new Intent();
//			myIntent = new Intent(LoginActivity.this, MainActivity.class);
//			startActivity(myIntent);
//			this.finish();
//		}
//		return super.onKeyDown(keyCode, event);
//	}
}
