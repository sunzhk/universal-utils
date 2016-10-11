package com.sunzhk.widget.parallaxlistview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.sunzhk.R;
import com.sunzhk.tools.BaseHolderAdapter;

import java.util.ArrayList;
import java.util.List;

public class ParallaxListView extends ListView implements OnScrollListener{
	
	private static final float DEFAULT_ALPHA_FACTOR = -1F;
	private static final float DEFAULT_PARALLAX_FACTOR = 1.9F;
	private static final boolean DEFAULT_IS_CIRCULAR = false;

	private static int firstItemHeight = 700;

	private float parallaxFactor = DEFAULT_PARALLAX_FACTOR;
	private float alphaFactor = DEFAULT_ALPHA_FACTOR;
	private ParallaxedView parallaxedView;
	private boolean isCircular;
	private OnScrollListener listener = null;
	private OnParallaxScrollListener mParallaxScrollListener;
	
	private boolean isScrolling = true;
	
	public ParallaxListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public ParallaxListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	protected void init(Context context, AttributeSet attrs) {
		TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.ParallaxScroll);
		this.parallaxFactor = typeArray.getFloat(R.styleable.ParallaxScroll_parallax_factor, DEFAULT_PARALLAX_FACTOR);
		this.alphaFactor = typeArray.getFloat(R.styleable.ParallaxScroll_alpha_factor, DEFAULT_ALPHA_FACTOR);
		this.isCircular = typeArray.getBoolean(R.styleable.ParallaxScroll_circular_parallax, DEFAULT_IS_CIRCULAR);
		typeArray.recycle();
		super.setOnScrollListener(this);
	}
	
	@Override
	public void setOnScrollListener(OnScrollListener l) {
		this.listener = l;
	}
	/**
	 * use BaseParallaxListViewAdapter
	 */
	@Override
	public void setAdapter(ListAdapter adapter) {
		// TODO Auto-generated method stub
		if(!(adapter instanceof BaseParallaxListViewAdapter)){
			throw new ClassCastException("must use BaseParallaxListViewAdapter");
		}
		super.setAdapter(adapter);
	}
	
	public void addParallaxedHeaderView(View v) {
		super.addHeaderView(v);
		addParallaxedView(v);
	}

	public void addParallaxedHeaderView(View v, Object data, boolean isSelectable) {
		super.addHeaderView(v, data, isSelectable);
		addParallaxedView(v);
	}

	protected void addParallaxedView(View v) {
		this.parallaxedView = new ListViewParallaxedItem(v);
	}

	protected void parallaxScroll() {
		if (isCircular)
			circularParallax();
		else
			headerParallax();
	}

	private void circularParallax() {
		if (getChildCount() > 0) {
			int top = -getChildAt(0).getTop();
			if (top >= 0) {
				fillParallaxedViews();
				setFilters(top);
			}
		}
	}

	private void headerParallax() {
		if (parallaxedView != null) {
			if (getChildCount() > 0) {
				int top = -getChildAt(0).getTop();
				if (top >= 0) {
					setFilters(top);
				}
			}
		}
	}

	private void setFilters(int top) {
		parallaxedView.setOffset((float)top / parallaxFactor);
		if (alphaFactor != DEFAULT_ALPHA_FACTOR) {
			float alpha = (top <= 0) ? 1 : (100 / ((float)top * alphaFactor));
			parallaxedView.setAlpha(alpha);
		}
		parallaxedView.animateNow();
	}

	private void fillParallaxedViews() {
		if (parallaxedView == null || parallaxedView.is(getChildAt(0)) == false) {
			if (parallaxedView != null) {
				resetFilters();
				parallaxedView.setView(getChildAt(0));
			} else {
				parallaxedView = new ListViewParallaxedItem(getChildAt(0));
			}
		}
	}

	private void resetFilters() {
		parallaxedView.setOffset(0);
		if (alphaFactor != DEFAULT_ALPHA_FACTOR)
			parallaxedView.setAlpha(1F);
		parallaxedView.animateNow();
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if(!isScrolling){
			return;
		}
		if((getCount()-1)*firstItemHeight <= getHeight()){
			return;
		}
		
		setItemHeight();
		
		parallaxScroll();
		
		if(mParallaxScrollListener != null && getChildCount() >=2){
			mParallaxScrollListener.onScroll(getChildAt(0), getChildAt(1), getChildAt(1).getTop()/firstItemHeight);
		}
		
		if (this.listener != null){
			this.listener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case SCROLL_STATE_FLING:
//			Log.e("scrollState", "SCROLL_STATE_FLING");
			isScrolling = true;
			break;
		case SCROLL_STATE_IDLE:
//			Log.e("scrollState", "SCROLL_STATE_IDLE");
			isScrolling = false;
			break;
		case SCROLL_STATE_TOUCH_SCROLL:
//			Log.e("scrollState", "SCROLL_STATE_TOUCH_SCROLL");
			isScrolling = true;
			break;

		default:
			break;
		}
		if (this.listener != null){
			this.listener.onScrollStateChanged(view, scrollState);
		}
	}

	public void setFirstItemHeight(int height){
		firstItemHeight = height;
	}

	public int getFirstItemHeight() {
		return firstItemHeight;
	}
	
	protected void setItemHeight(){
		int childCount = getChildCount();

		for(int i = 0;i<childCount;i++){
			View temp = getChildAt(i);
			if(temp == null){
				continue;
			}
			if(getLastVisiblePosition() == getCount() -1 && i == childCount-1){
				continue;
			}
			if(i == 0){
//				setViewLayoutParams(temp,LayoutParams.MATCH_PARENT, firstItemHeight);
				temp.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, firstItemHeight));
			}else if(i == 1){
//				setViewLayoutParams(temp, LayoutParams.MATCH_PARENT, firstItemHeight/2 + Math.abs(getChildAt(0).getTop()/2));
				temp.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, firstItemHeight/2 + Math.abs(getChildAt(0).getTop()/2)));
			}else{
				setViewLayoutParams(temp, LayoutParams.MATCH_PARENT, firstItemHeight/2);
//				temp.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, firstItemHeight/2));
			}
			if(getLastVisiblePosition() == getCount()-1){
//				setViewLayoutParams(getChildAt(getChildCount()-1), LayoutParams.MATCH_PARENT, getHeight()-firstItemHeight);
//				getChildAt(getChildCount()-1).setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, getHeight()-firstItemHeight));
			}
		}
	}
	
	private void setViewLayoutParams(View view, int w, int h){
		if((w > 0 && view.getWidth() == w) || (h > 0 && view.getHeight() == h)){
			return;
		}
		view.setLayoutParams(new LayoutParams(w, h));
	}
	
	public void setOnParallaxScrollListener(OnParallaxScrollListener mParallaxScrollListener) {
		this.mParallaxScrollListener = mParallaxScrollListener;
	}
	
	class ListViewParallaxedItem extends ParallaxedView {

		public ListViewParallaxedItem(View view) {
			super(view);
		}
		
		@Override
		protected void translatePreICS(View view, float offset) {
			addAnimation(new TranslateAnimation(0, 0, offset, offset));
		}
	}

//	
//	public static abstract class BaseParallaxListViewAdapter<T> extends BaseAdapter{
//
//		protected List<T> mData;
//		protected ParallaxListView mListView;
//		
//		public BaseParallaxListViewAdapter(List<T> data, ParallaxListView listView) {
//			// TODO Auto-generated constructor stub
//			mData = data;
//			mListView = listView;
//		}
//		
//		@Override
//		public int getCount() {
//			// TODO Auto-generated method stub
//			return mData == null ? 0 : mData.size()+1;
//		}
//		
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			// TODO Auto-generated method stub
//			if(convertView != null){
//				if(position == getCount()-1){
//					if(mListView != null){
//						convertView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, mListView.getHeight()-firstItemHeight));
//					}
//				}else{
//					convertView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, firstItemHeight));
//				}
//			}
//			return convertView;
//		}
//	}
//	
	
	
	public static abstract class BaseParallaxListViewAdapter<T> extends BaseHolderAdapter<T>{

		protected List<T> mData;
		protected ParallaxListView mListView;
		
		private View blankView;
		
		public BaseParallaxListViewAdapter(Activity activity, int layout, ArrayList<T> data, ParallaxListView listView) {
			// TODO Auto-generated constructor stub
			super(activity, layout, data);
			mData = data;
			mListView = listView;
			blankView = new View(activity);
			blankView.setTag("blankItem");
			blankView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 1000));
		}
		
		public int getItemMaxHeight(){
			return mListView.getFirstItemHeight();
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mData == null ? 0 : mData.size()+1;
		}
		
		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position == mData.size() ? -1 : position;
		}
		
		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position == mData.size() ? null : mData.get(position);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(position == getCount()-1){
				blankView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, mListView.getHeight()-firstItemHeight));
				convertView = blankView;
				return convertView;
			}
			if (convertView == null || (convertView.getTag() != null && convertView.getTag().equals("blankItem"))){
				convertView = null;
				convertView = super.getView(position, convertView, parent);
				convertView.setTag(null);
			}
			
			if(convertView != null){
				convertView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, firstItemHeight));
				setData(position, convertView, mData.get(position));
			}
			
			return convertView;
		}
	}
	
	public interface OnParallaxScrollListener{
		
		void onScroll(View item1, View item2, double percent);
		
	}


	
	
	
}
