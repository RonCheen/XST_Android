package com.hhu.xst.notetool;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hhu.xst.function.MyNoteActivity;
import com.jereh.slidingdemo.R;

public class ManagerNoteActivity extends Activity{
	private Button myn = null;
	private Button addn = null;
	private Button managern = null;
	
	private Button delete = null; //删除记事
	private ListView nolist = null;
	private TextView showtime = null; //当前时间
	private TextView show = null; //提示语
	
	public static List<String> cbList = new ArrayList<String>();
	List<Note> list = DBUtil.getAllNote(); //获取所有的记事
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_managernode);
		
		showtime = (TextView) findViewById(R.id.showtime);
		showtime.setText(DBUtil.getNowTime());
		
		
		delete = (Button) findViewById(R.id.delete);
		delete.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				for (int i=0; i<cbList.size(); i++) {
					DBUtil.deleteNote(cbList.get(i)); 
				}
				cbList.clear();
				Toast.makeText(ManagerNoteActivity.this, "删除成功!", Toast.LENGTH_SHORT).show();
				list = DBUtil.getAllNote();
				if (list.size() > 0) {
					show.setVisibility(View.GONE);
				} else {
					show.setVisibility(View.VISIBLE);
				}
				nolist.setAdapter(new ManagerNoteAdapter(ManagerNoteActivity.this, list));
			}
		});
		show = (TextView) findViewById(R.id.show);
		nolist = (ListView) findViewById(R.id.nolist);
		
		if (list.size() > 0) {
			show.setVisibility(View.GONE);
		} else {
			show.setVisibility(View.VISIBLE);
		}
		nolist.setAdapter(new ManagerNoteAdapter(ManagerNoteActivity.this, list));
		initFooter();
	}
	
	public void initFooter() {
		myn = (Button) findViewById(R.id.myn);
		myn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ManagerNoteActivity.this, MyNoteActivity.class);
				ManagerNoteActivity.this.startActivity(intent);
				ManagerNoteActivity.this.finish();
			}
		});
		addn = (Button) findViewById(R.id.addn);
		addn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ManagerNoteActivity.this, AddNoteActivity.class);
				ManagerNoteActivity.this.startActivity(intent);
				ManagerNoteActivity.this.finish();
			}
		});
		managern = (Button) findViewById(R.id.managern);
		managern.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
			}
		});
	}
}
