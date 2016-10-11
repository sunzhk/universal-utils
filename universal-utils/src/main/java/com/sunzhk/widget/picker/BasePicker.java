package com.sunzhk.widget.picker;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.List;

public abstract class BasePicker<T> extends ListView {

	/**
	 * 最多显示的选项数量,默认五个
	 */
	private int optionNumber = 5;
	
	private List<T> mData;
	
	private View header,footer;
	
	private int itemHeight;
	
	public BasePicker(Context context, List<T> data) {
		super(context);
		// TODO Auto-generated constructor stub
		mData = data;
		header = new View(getContext());
		footer = new View(getContext());
		header.setBackgroundColor(0xff00ff00);
		footer.setBackgroundColor(0xff0000ff);
		addHeaderView(header);
		addFooterView(footer);
		setDividerHeight(2);
		GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xffffffff,0xff0000ff,0xffffffff});
		drawable.setCornerRadius(10);
		setDivider(drawable);
		setFooterDividersEnabled(true);
		setFooterDividersEnabled(true);
		setAdapter(new BasePickerAdapter());
	}
	
	/**
	 * 显示的行数- -请用奇数个，偶数的话好烦的，抛异常恶心你哦
	 * 
	 * @param number
	 */
	public void setOptionNumber(int number){
		if(number % 2 == 0){
			throw new  RuntimeException("- -显示行数请用奇数个，偶数的话好烦的");
		}
		optionNumber = number;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
//		int width = r - l;
		int height = b - t;
//		Toast.makeText(getContext(), "www"+width+"hhh"+height, 1).show();
		itemHeight = (height - getDividerHeight() * (optionNumber - 1)) / optionNumber;
		if(header.getLayoutParams() != null){
			header.getLayoutParams().height = 2 * itemHeight;
		}else{
			header.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 2 * itemHeight));
		}
		if(footer.getLayoutParams() != null){
			footer.getLayoutParams().height = 2 * itemHeight;
		}else{
			footer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 2 * itemHeight));
		}
		
	}
	
//	@Override
//	protected android.view.ViewGroup.LayoutParams generateDefaultLayoutParams() {
//		// TODO Auto-generated method stub
//		if(itemHeight <= 0){
//			return super.generateDefaultLayoutParams();
//		}
//		return new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight, 0);
//	}
	
//	@Override
//	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//		// TODO Auto-generated method stub
//		for(int i = firstVisibleItem;i < visibleItemCount;i++){
//			View temp = view.getChildAt(i);
//			if(temp == header || temp == footer){
//				continue;
//			}
//			temp.getLayoutParams().height = itemHeight;
//		}
//	}
	
	
	class BasePickerAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mData == null ? 0 : mData.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mData == null || mData.isEmpty() ? null : mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			convertView = BasePicker.this.getView(position, convertView, mData.get(position));
			if(convertView.getLayoutParams() != null){
				convertView.getLayoutParams().height = itemHeight;
			}else{
				convertView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, itemHeight));
			}
			convertView.setMinimumHeight(itemHeight);
			return BasePicker.this.getView(position, convertView, mData.get(position));
		}
		
	}
	/**
	 * 复用的item视图
	 * @param position
	 * @param convertView
	 * @return
	 */
	abstract public View getView(int position, View convertView, T data);

	
}
