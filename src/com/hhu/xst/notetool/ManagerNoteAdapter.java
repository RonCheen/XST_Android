package com.hhu.xst.notetool;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jereh.slidingdemo.R;


public class ManagerNoteAdapter extends BaseAdapter{
	
	private Context context = null;
	private List<Note> list = null;
	
	public ManagerNoteAdapter(Context context, List<Note> list){
		this.context = context;
		this.list = list;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null) {
			vh = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.activity_nolist_manager_item, null);
			
			vh.time = (TextView)convertView.findViewById(R.id.time);
			vh.notitle = (TextView)convertView.findViewById(R.id.notitle);
			vh.cb = (CheckBox) convertView.findViewById(R.id.cb);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
		
		vh.time.setText(list.get(position).getCreated_at());
		vh.notitle.setText(list.get(position).getTitle());
		vh.cb.setOnClickListener(new CheckBoxOnCLick(position));
		convertView.setOnClickListener(new ItemOnClick(position));
		return convertView;
	}

	//item单击事件
	class ItemOnClick implements OnClickListener {
		int index = 0;
		public ItemOnClick(int index) {
			this.index = index;
		}
		public void onClick(View v) {
    		int id = list.get(index).getId();
			Intent intent = new Intent();
			intent.setClass(context, DetailNoteActivity.class);
			Bundle bl = new Bundle();
			bl.putString("id", id+"");
			bl.putString("mark", "2");
			intent.putExtras(bl);
			context.startActivity(intent);
    	}
	}
	
	//复选框单击事件
	class CheckBoxOnCLick implements OnClickListener {
		private int index = 0;
		public CheckBoxOnCLick(int index) {
			this.index = index;
		}
		public void onClick(View v) {
			Note note = list.get(index);
			if (ManagerNoteActivity.cbList.contains(note.getId()+"")) {
				ManagerNoteActivity.cbList.remove(note.getId()+"");
			} else {
				ManagerNoteActivity.cbList.add(note.getId()+"");
			}
		}
	}
	
	public final class ViewHolder {
		public TextView time;
		public TextView notitle;
		public CheckBox cb;
	}
}
