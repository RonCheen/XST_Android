package com.hhu.xst.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.hhu.xst.main.MainActivity;
import com.hhu.xst.notetool.AddNoteActivity;
import com.hhu.xst.notetool.DBUtil;
import com.hhu.xst.notetool.DetailNoteActivity;
import com.hhu.xst.notetool.ManagerNoteActivity;
import com.hhu.xst.notetool.Note;
import com.jereh.slidingdemo.R;
/**
 * 实现课堂笔记功能
 * @author lenovo
 *
 */
public class MyNoteActivity extends Activity{
	
	private Button myn = null;
	private Button addn = null;
	private Button managern = null;
	private Button bac;
 	private Button latest = null; //最新记事
	private Button all = null; //所有记事
	private ListView nolist = null;
	private TextView show = null; //提示语
	List<Note> list; 
	
	private List<Map<String, Object>> daList = new ArrayList<Map<String, Object>>();
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mynote);
		if (DBUtil.db == null) {
			DBUtil.db = MyNoteActivity.this.openOrCreateDatabase(DBUtil.DB_NAME, MODE_PRIVATE, null); //数据库私有
			DBUtil.db.execSQL(DBUtil.CREATE_TABLE); //创建表
		}
		
		show = (TextView) findViewById(R.id.show);
		list = DBUtil.getSevenNote(); //获取最近七天的记事
		latest = (Button) findViewById(R.id.latest);
		latest.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				
				initDate(list); //初始化数据集合
				SimpleAdapter adapter = new SimpleAdapter(MyNoteActivity.this, daList, R.layout.activity_nolist_item, new String[]{"time", "notitle"}, new int[]{R.id.time, R.id.notitle});
				nolist.setAdapter(adapter);
			}
		});
		all = (Button) findViewById(R.id.all);
		all.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				list = DBUtil.getAllNote();
				initDate(list);
				SimpleAdapter adapter = new SimpleAdapter(MyNoteActivity.this, daList, R.layout.activity_nolist_item, new String[]{"time", "notitle"}, new int[]{R.id.time, R.id.notitle});
				nolist.setAdapter(adapter);
			}
		});
		nolist = (ListView) findViewById(R.id.nolist);
		nolist.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				int id = list.get(arg2).getId();
				Intent intent = new Intent();
				intent.setClass(MyNoteActivity.this, DetailNoteActivity.class);
				Bundle bl = new Bundle();
				bl.putString("id", id+"");
				bl.putString("mark", "1");
				intent.putExtras(bl);
				MyNoteActivity.this.startActivity(intent);
				MyNoteActivity.this.finish();
			}
		});
		latest.performClick();
		initFooter();
	}
	
	//初始化ListView中的数据
	public void initDate(List<Note> list) {
		daList.clear();
		int len = list.size();
		if (len > 0) {
			show.setVisibility(View.GONE);
		} else {
			show.setVisibility(View.VISIBLE);
		}
		for (int i=0; i<len; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("time", list.get(i).getCreated_at());
			map.put("notitle", list.get(i).getTitle());
			daList.add(map);
		}
	}
	
	public void initFooter() {
		myn = (Button) findViewById(R.id.myn);
		myn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
			}
		});
		addn = (Button) findViewById(R.id.addn);
		addn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MyNoteActivity.this, AddNoteActivity.class);
				MyNoteActivity.this.startActivity(intent);
				MyNoteActivity.this.finish();
			}
		});
		managern = (Button) findViewById(R.id.managern);
		managern.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MyNoteActivity.this, ManagerNoteActivity.class);
				MyNoteActivity.this.startActivity(intent);
				MyNoteActivity.this.finish();
			}
		});
		bac = (Button) findViewById(R.id.backhome);
		bac.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(MyNoteActivity.this, MainActivity.class);
				MyNoteActivity.this.startActivity(intent);
				MyNoteActivity.this.finish();
			}
		});
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK){
			Intent myIntent = new Intent();
            myIntent = new Intent(MyNoteActivity.this, MainActivity.class);
            startActivity(myIntent);
            this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
