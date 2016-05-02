package com.hhu.xst.function;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.widget.TextView;

import com.hhu.xst.main.MainActivity;
import com.hhu.xst.ui.NewsFragment;
import com.jereh.slidingdemo.R;
/**
 * 实现新闻浏览，仍需改进
 * @author lenovo
 *
 */
public class NewsActivity extends Activity {

	private String url = "http://121.42.203.75:8080/xst/news/view/12";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_news);
		WebView show = (WebView) findViewById(R.id.show);
		show.loadUrl(url );
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent myIntent = new Intent();
            myIntent = new Intent(NewsActivity.this, MainActivity.class);
            startActivity(myIntent);
            this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
