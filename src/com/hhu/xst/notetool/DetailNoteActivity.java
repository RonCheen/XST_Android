package com.hhu.xst.notetool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.hhu.xst.function.MyNoteActivity;
import com.jereh.slidingdemo.R;

public class DetailNoteActivity extends Activity {

	private TextView title = null;
	private TextView showtime = null;
	private TextView content = null;

	private String mark = "1";
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_detail_note);

		Intent intent = getIntent();
		String id = intent.getExtras().getString("id");
		mark = intent.getExtras().getString("mark");
		Note note = DBUtil.getNoteById(id);

		title = (TextView) findViewById(R.id.title);
		title.setText(note.getTitle());
		showtime = (TextView) findViewById(R.id.showtime);
		showtime.setText(note.getCreated_at());
		content = (TextView) findViewById(R.id.content);
		content.setText(note.getContent());
	}

	// ¼àÌý·µ»Ø¼ü
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent();
			if (mark.equals("1")) {
				intent.setClass(DetailNoteActivity.this, MyNoteActivity.class);
			} else {
				intent.setClass(DetailNoteActivity.this, ManagerNoteActivity.class);
			}
			DetailNoteActivity.this.startActivity(intent);
			DetailNoteActivity.this.finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
