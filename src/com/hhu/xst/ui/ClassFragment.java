package com.hhu.xst.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hhu.xst.video.VideoListActivity;
import com.jereh.slidingdemo.R;
/**
 * ��ʾ����APPʱ�γ��б�
 * @author lenovo
 *
 */
public class ClassFragment extends Fragment{
	ScrollView scroll;
	private View arg0;
	TextView text2,text1,text3,text4,text5;
   @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_ketang, null);
		scroll = (ScrollView) view.findViewById(R.id.scroll);
		onClick(view);
		return view;
	}
/**
 * �����Ӧר�ⰴť�����벻ͬר�ⲥ���б�ע����δʵ���ٵ��ʱ��ѯ��Ӧ��Ƶ��Ϣ�����б����
 * @param view
 */
   public void onClick(View view) {
	    text1 = (TextView) view.findViewById(R.id.lesson1);
	    text2 = (TextView) view.findViewById(R.id.lesson2);
	    text3 = (TextView) view.findViewById(R.id.lesson3);
	    text4 = (TextView) view.findViewById(R.id.lesson4);
	   // text5 = (TextView) view.findViewById(R.id.lesson5);
		text1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),VideoListActivity.class);
				startActivity(intent);
			}
		});
		text2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),VideoListActivity.class);
				startActivity(intent);
			}
		});

		text3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),VideoListActivity.class);
				startActivity(intent);
			}
		});
		text4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),VideoListActivity.class);
				startActivity(intent);
			}
		});
	 }
}
