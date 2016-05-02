package com.hhu.xst.ui;

import com.jereh.slidingdemo.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
/**
 *实现小组界面 
 * @author lenovo
 *
 */
public class XiaozuFragment extends Fragment{
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_xiaozu, null);
	//	TableRow tabr = (TableRow) view.findViewById(R.id.table1);
		return view;
	}

}
