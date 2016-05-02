package com.hhu.xst.ui;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.hhu.xst.function.LoginActivity;
import com.hhu.xst.function.NewsActivity;
import com.hhu.xst.main.MainActivity;
import com.jereh.slidingdemo.R;

/**
 * @描述 在Fragment中要使用ListView，就要用ListFragment
 * */
@SuppressLint("SimpleDateFormat")
public class NewsFragment extends Fragment {

	private String[] names = new String[] { "RonCheen", "Bless Lee", "Mark" };
	private String[] descs = new String[3];
	private int[] ImagIds = new int[] { R.drawable.ic_launcher,
			R.drawable.ic_launcher, R.drawable.ic_launcher, };
	private ListView list;
	public static String title;

	/**
	 * @描述 在onCreateView中加载布局
	 * */
	@Override  
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  
            Bundle savedInstanceState) {  
        View view = inflater.inflate(R.layout.fragment_news, container,false);  
        list = (ListView) view.findViewById(android.R.id.list);  
        Tianchong();
       
        return view;  
    }
/**
 * 该方法用来为资讯填充内容
 */
	private void Tianchong() {
		// TODO Auto-generated method stub
		 SimpleDateFormat formatter = new SimpleDateFormat(
	    			"yyyy年MM月dd日    HH:mm:ss     ");
	    	Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
	    	descs[0] = formatter.format(curDate);
	    	SimpleDateFormat sDateFormat = new SimpleDateFormat(
	    			"yyyy-MM-dd    hh:mm:ss");
	    	descs[1] = sDateFormat.format(new java.util.Date());
	    	SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM");    
	    	descs[2] = sdf.format(new java.util.Date());    
	    	List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();
	    	for(int i = 0; i<names.length;i++){
	    		Map <String,Object> listItem = new HashMap<String, Object>();
	    		listItem.put("header", ImagIds[i]);
	    		listItem.put("personName", names[i]);
	    		listItem.put("desc",descs[i]);
	    		listItems.add(listItem);
	    	}
	    	/**
	    	 * 注：getActivity是一个值得记住的地方
	    	 */
	    	SimpleAdapter simple = new SimpleAdapter(getActivity(), listItems, R.layout.item_list, new String[]
					{"header","personName","desc"}, new int[]{R.id.header,R.id.name,R.id.desc});

	    	list.setAdapter(simple);
	    	list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
//					Toast toast=Toast.makeText(getActivity(),"这是第"+(position+1)+"条信息",Toast.LENGTH_SHORT);
//				    toast.show();
					/**
					 * title 用来向activity传递消息
					 */
				    title = "This is the news Page of " + String.valueOf(position) +" !";
				    Intent intent = new Intent(getActivity(),NewsActivity.class);
			//intent.setActivity(getActivity(), NewsActivity.class);
				    startActivity(intent);
				}
			});
	}
}