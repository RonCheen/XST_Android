package com.hhu.xst.notetool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBUtil {
	public static SQLiteDatabase db;
	public static final String DB_NAME = "mynotepad"; // ���ݿ���

	// ���±�
	public static String TABLE_NAME_NOTE = "note";
	public static String CREATE_TABLE = "create table  if not exists "
			+ TABLE_NAME_NOTE + " (" + "id Integer Primary key,"
			+ "title NVARCHAR(100)," + "content TEXT," + "created_at TEXT)";

	// ��Ӽ���
	public static long addNote(Note note) {
		ContentValues cv = new ContentValues();
		cv.put("title", note.getTitle());
		cv.put("content", note.getContent());
		cv.put("created_at", note.getCreated_at());
		long id = db.insert(TABLE_NAME_NOTE, null, cv);
		return id;
	}

	// ��ȡ����һ������
	public static Note getLatsetNote() {
		Note note = new Note();
		Cursor cur = db.query(TABLE_NAME_NOTE, null, null, null, null, null,
				" created_at desc", "1");
		if (cur != null) {
			if (cur.moveToFirst()) {
				note.setId(cur.getInt(cur.getColumnIndex("id")));
				note.setTitle(cur.getString(cur.getColumnIndex("title")));
				note.setContent(cur.getString(cur.getColumnIndex("content")));
				note.setCreated_at(cur.getString(cur
						.getColumnIndex("created_at")));
			}
			cur.close(); // ��Ҫ���ǹر�����
		}
		return note;
	}

	// ��ȡ�������ļ���
	public static List<Note> getSevenNote() {
		List<Note> list = new ArrayList<Note>();
		Cursor cur = db.query(TABLE_NAME_NOTE, null, "created_at > ?",
				new String[] { getNTime(7) }, null, null, " created_at desc",
				null);
		if (cur != null) {
			if (cur.moveToFirst()) {
				do {
					Note note = new Note();
					note.setId(cur.getInt(cur.getColumnIndex("id")));
					note.setTitle(cur.getString(cur.getColumnIndex("title")));
					note.setContent(cur.getString(cur.getColumnIndex("content")));
					note.setCreated_at(cur.getString(cur
							.getColumnIndex("created_at")));
					list.add(note);
				} while (cur.moveToNext());
			}
			cur.close(); // ��Ҫ���ǹر�����
		}
		return list;
	}

	// ��ȡ���еļ���
	public static List<Note> getAllNote() {
		List<Note> list = new ArrayList<Note>();
		Cursor cur = db.query(TABLE_NAME_NOTE, null, null, null, null, null,
				" created_at desc", null);
		if (cur != null) {
			if (cur.moveToFirst()) {
				do {
					Note note = new Note();
					note.setId(cur.getInt(cur.getColumnIndex("id")));
					note.setTitle(cur.getString(cur.getColumnIndex("title")));
					note.setContent(cur.getString(cur.getColumnIndex("content")));
					note.setCreated_at(cur.getString(cur
							.getColumnIndex("created_at")));
					list.add(note);
				} while (cur.moveToNext());
			}
			cur.close(); // ��Ҫ���ǹر�����
		}
		return list;
	}

	// ɾ������
	public static void deleteNote(String id) {
		String sql = "delete from " + TABLE_NAME_NOTE + " where id=" + id;
		db.execSQL(sql);
	}

	// ��ȡIdһ������
	public static Note getNoteById(String id) {
		Note note = new Note();
		Cursor cur = db.query(TABLE_NAME_NOTE, null, "id=?", new String[]{id}, null, null, null, null);
		if (cur != null) {
			if (cur.moveToFirst()) {
				note.setId(cur.getInt(cur.getColumnIndex("id")));
				note.setTitle(cur.getString(cur.getColumnIndex("title")));
				note.setContent(cur.getString(cur.getColumnIndex("content")));
				note.setCreated_at(cur.getString(cur.getColumnIndex("created_at")));
			}
			cur.close(); // ��Ҫ���ǹر�����
		}
		return note;
	}

	// ��ȡ��ǰʱ����ǰn���ʱ��
	public static String getNTime(int n) {
		Calendar date = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		date.add(Calendar.DATE, -n);
		return sdf.format(date.getTime());
	}

	// ��ȡϵͳ��ǰʱ��
	public static String getNowTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());
		return formatter.format(curDate);
	}
}
