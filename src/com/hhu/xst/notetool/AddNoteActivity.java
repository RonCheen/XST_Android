package com.hhu.xst.notetool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hhu.xst.function.MyNoteActivity;
import com.jereh.slidingdemo.R;

public class AddNoteActivity extends Activity{
	
	private Button add = null;//添加记事按钮
	private Button cancle = null;//回主页按钮
	private Button myn = null;//我的记事按钮
	private Button managern = null;//管理记事按钮
	private EditText title = null;//标题
	private EditText content = null;//内容
	
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_addnode);
		
		add = (Button) findViewById(R.id.add);
		add.setOnClickListener(new OnClickListener(){//添加记事
			public void onClick(View v) {
				String titleStr = title.getText().toString();
				if ("".equals(titleStr.trim())) {//防止空格
					Toast.makeText(AddNoteActivity.this, "标题不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				String contentStr = content.getText().toString();
				if ("".equals(contentStr.trim())) {
					Toast.makeText(AddNoteActivity.this, "内容不能为空", Toast.LENGTH_SHORT).show();
					return;
				}
				
				Note note = new Note();
				note.setTitle(titleStr);
				note.setContent(contentStr);
				note.setCreated_at(DBUtil.getNowTime());
				long id = DBUtil.addNote(note);
				if (id > 0) {
					Toast.makeText(AddNoteActivity.this, "记事添加成功", Toast.LENGTH_SHORT).show();
					toHome();
				} else {
					Toast.makeText(AddNoteActivity.this, "记事添失败", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		
		
		cancle = (Button) findViewById(R.id.cancle);
		cancle.setOnClickListener(new OnClickListener(){ //回主页
			public void onClick(View v) {
				toHome();
			}
		});
		
		title = (EditText) findViewById(R.id.title);
		content = (EditText) findViewById(R.id.content);
		
		initFooter();
	}
	
	public void initFooter() {
		myn = (Button) findViewById(R.id.myn);
		myn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				toHome();
			}
		});
		managern = (Button) findViewById(R.id.managern);
		managern.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(AddNoteActivity.this, ManagerNoteActivity.class);
				AddNoteActivity.this.startActivity(intent);
				AddNoteActivity.this.finish();
			}
		});
	}
	
	public void toHome() {
		//跳转到主页面
		Intent intent = new Intent();
		intent.setClass(AddNoteActivity.this, MyNoteActivity.class);
		AddNoteActivity.this.startActivity(intent);
		AddNoteActivity.this.finish();
	}
}
