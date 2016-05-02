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
	
	private Button add = null;//��Ӽ��°�ť
	private Button cancle = null;//����ҳ��ť
	private Button myn = null;//�ҵļ��°�ť
	private Button managern = null;//������°�ť
	private EditText title = null;//����
	private EditText content = null;//����
	
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_addnode);
		
		add = (Button) findViewById(R.id.add);
		add.setOnClickListener(new OnClickListener(){//��Ӽ���
			public void onClick(View v) {
				String titleStr = title.getText().toString();
				if ("".equals(titleStr.trim())) {//��ֹ�ո�
					Toast.makeText(AddNoteActivity.this, "���ⲻ��Ϊ��", Toast.LENGTH_SHORT).show();
					return;
				}
				String contentStr = content.getText().toString();
				if ("".equals(contentStr.trim())) {
					Toast.makeText(AddNoteActivity.this, "���ݲ���Ϊ��", Toast.LENGTH_SHORT).show();
					return;
				}
				
				Note note = new Note();
				note.setTitle(titleStr);
				note.setContent(contentStr);
				note.setCreated_at(DBUtil.getNowTime());
				long id = DBUtil.addNote(note);
				if (id > 0) {
					Toast.makeText(AddNoteActivity.this, "������ӳɹ�", Toast.LENGTH_SHORT).show();
					toHome();
				} else {
					Toast.makeText(AddNoteActivity.this, "������ʧ��", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		
		
		cancle = (Button) findViewById(R.id.cancle);
		cancle.setOnClickListener(new OnClickListener(){ //����ҳ
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
		//��ת����ҳ��
		Intent intent = new Intent();
		intent.setClass(AddNoteActivity.this, MyNoteActivity.class);
		AddNoteActivity.this.startActivity(intent);
		AddNoteActivity.this.finish();
	}
}
