package com.hhu.xst.function;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.hhu.xst.main.MainActivity;
import com.jereh.slidingdemo.R;
/**
 * 实现视频搜索
 * @author lenovo
 *
 */
public class SearchActivity extends Activity {
	private SearchView srv1;
	private ListView lv1;
	private ArrayAdapter<String> aadapter;
	private String[] names;
	private ArrayList<String> alist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		srv1 = (SearchView) findViewById(R.id.srv1);
		names = new String[] { "a中国d", "drrr中123ffa", "123u1y国iu", "r2qer",
				"qw3agt", "a3frgb", "rtyr" };
		

		srv1.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				//在该处实现查询视频 name [] 又数据库中查到的内容填充
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				if (newText.length() != 0) {
					setFilterText(newText);
				} else {
					clearTextFilter();
				}
				return false;
			}
		});
		lv1 = (ListView) findViewById(R.id.lv1);
		aadapter = new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.simple_expandable_list_item_1, names);

		lv1.setAdapter(aadapter);
		lv1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				SearchPlayerActivity.videoUrl = "http://video.cztv.com/video/rzx/201208/15/1345010952759.mp4";
				Intent intent = new Intent(SearchActivity.this,SearchPlayerActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	public void setFilterText(String filterText) {
		ArrayList<String> list = new ArrayList<String>();
		String[] tempStr;
		for (int i = 0; i < names.length; i++) {
			if (names[i].contains(filterText)) {
				list.add(names[i]);
			}
		}
		if (list.size() >= 0) {
			tempStr = new String[list.size()];
			int j = 0;
			for (String str : list) {
				tempStr[j++] = str;
			}
			aadapter = new ArrayAdapter<String>(getApplicationContext(),
					android.R.layout.simple_expandable_list_item_1, tempStr);
			lv1.setAdapter(aadapter);
		}
	}

	public void clearTextFilter() {
		aadapter = new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.simple_expandable_list_item_1, names);
		lv1.setAdapter(aadapter);
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent myIntent = new Intent();
            myIntent = new Intent(SearchActivity.this, MainActivity.class);
            startActivity(myIntent);
            this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
}
