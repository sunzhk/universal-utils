package com.sunzhk.widget.picker;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class SimpleStringPicker extends BasePicker<String> {

	public SimpleStringPicker(Context context, List<String> data) {
		super(context, data);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, String data) {
		// TODO Auto-generated method stub
		if(convertView == null){
			convertView = new TextView(getContext());
		}
		((TextView) convertView).setText(data);
		((TextView) convertView).setTextSize(20);
		return convertView;
	}

}
