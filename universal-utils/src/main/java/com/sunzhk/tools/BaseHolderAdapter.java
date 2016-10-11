package com.sunzhk.tools;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.sunzhk.tools.utils.Util;

import java.util.ArrayList;

/**
 * 创建item的时候会遍历item并将id >= 0的子控件放入item的tag
 * @author sunzhk
 *
 * @param <T>
 */
public abstract class BaseHolderAdapter<T> extends BaseAdapter {

	private int defalutLayout;
	private ArrayList<T> mDataList;
	
	private Activity parentActivity;
	
	private LayoutInflater layoutInflater;
	
	public BaseHolderAdapter(Activity activity, int layout, ArrayList<T> data) {
		// TODO Auto-generated constructor stub
		parentActivity = activity;
		defalutLayout = layout;
		mDataList = data;
		layoutInflater = activity.getLayoutInflater();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mDataList == null ? 0 : mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mDataList == null ? null : mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView == null){
			convertView = layoutInflater.inflate(defalutLayout, null);
			if(convertView instanceof ViewGroup){
				ArrayList<View> childs = new ArrayList<View>();
				Util.getAllChild((ViewGroup) convertView, childs);
				for(View view : childs){
					if(view.getId() >= 0){
						convertView.setTag(view.getId(), view);
					}
				}
			}
			initViewSet(position, convertView);
		}
		if(position < mDataList.size()){
			setData(position, convertView, mDataList.get(position));
		}
		return convertView;
	}
	
	public void setItemLayout(int layout){
		defalutLayout = layout;
		notifyDataSetChanged();
	}
	
	public int getItemLayout(){
		return defalutLayout;
	}

	public Activity getParentActivity() {
		return parentActivity;
	}
	
	protected abstract void initViewSet(int position, View convertView);
	/**
	 * 请使用convertView.getTag(R.id.XXXXXXX)来获得子控件
	 * @param position
	 * @param convertView
	 * @param data
	 */
	protected abstract void setData(int position, View convertView, T data);

}
