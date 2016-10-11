package com.sunzhk.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 仿喵街视差列表
 * 只要看{@link BaseParallaxListViewAdapter}就可以了
 * 
 * @author sunzhk
 *
 */
public class SimpleParallaxListView extends ListView implements OnScrollListener {

	private static final int TAG = Integer.MIN_VALUE;
	private static final String TAG_BLANK_ITEM = "blankItem";
	private static final String TAG_SHELL_ITEM = "shellItem";
	
	private int firstItemHeight = -1;
	private ParallaxedView parallaxedView;
	private OnScrollListener listener = null;
	private View blankFooterView;
	private BaseParallaxListViewAdapter mAdapter;

	public SimpleParallaxListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public SimpleParallaxListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	protected void init(Context context, AttributeSet attrs) {
		super.setOnScrollListener(this);
		blankFooterView = new View(context);
		blankFooterView.setTag(TAG, TAG_BLANK_ITEM);
		addFooterView(blankFooterView);
		refreshBlankFooterView();
	}

	protected void refreshBlankFooterView() {
		if (blankFooterView.getLayoutParams() == null) {
			blankFooterView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					firstItemHeight <= 0 ? getHeight() : getHeight() - firstItemHeight));
		} else {
			blankFooterView.getLayoutParams().height = firstItemHeight <= 0 ? getHeight()
					: getHeight() - firstItemHeight;
		}
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		this.listener = l;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		// TODO Auto-generated method stub
		if (!(adapter instanceof BaseParallaxListViewAdapter)) {
			throw new ClassCastException("must use BaseParallaxListViewAdapter");
		}
		super.setAdapter(adapter);
		mAdapter = (BaseParallaxListViewAdapter) adapter;
	}

	@Override
	public View getChildAt(int index) {
		// TODO Auto-generated method stub
		return super.getChildAt(index);
	}
	/**
	 * 获取实际需要的item时用的方法
	 * @param index
	 * @return
	 */
	public View getRealChildAt(int index) {
		View child = getChildAt(0);
		if(child != null && child instanceof LinearLayout && child.getTag(TAG) != null && child.getTag(TAG).equals(TAG_SHELL_ITEM)){
			return ((LinearLayout) child).getChildAt(0);
		}
		return super.getChildAt(index);
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
		this.parallaxedView = new ParallaxedView(v);
	}

	protected void parallaxScroll() {
		circularParallax();
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

	private void setFilters(int top) {
		parallaxedView.setOffset((float) top / 1.9f);
		parallaxedView.animateNow();
	}

	private void fillParallaxedViews() {
		if (parallaxedView == null || parallaxedView.is(getChildAt(0)) == false) {
			if (parallaxedView != null) {
				resetFilters();
				parallaxedView.setView(getChildAt(0));
			} else {
				parallaxedView = new ParallaxedView(getChildAt(0));
			}
		}
	}

	private void resetFilters() {
		parallaxedView.setOffset(0);
		parallaxedView.animateNow();
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if ((getCount() - 1) * firstItemHeight <= getHeight()) {
			return;
		}

		setItemHeight();

		parallaxScroll();

		if (this.listener != null) {
			this.listener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (this.listener != null)
			this.listener.onScrollStateChanged(view, scrollState);
	}

	public int getFirstItemHeight() {
		return firstItemHeight;
	}

	public void setFirstItemHeight(int firstItemHeight) {
		this.firstItemHeight = firstItemHeight;
		refreshBlankFooterView();
	}

	protected void setItemHeight() {
		int childCount = getChildCount();

		for (int i = 0; i < childCount; i++) {
			View temp = getChildAt(i);
			if (temp == null) {
				continue;
			}
			if (getLastVisiblePosition() == getCount() - 1 && i == childCount - 1) {
				continue;
			}
			if (i == 0) {
				temp.getLayoutParams().height = firstItemHeight;
				temp.setLayoutParams(temp.getLayoutParams());
			} else if (i == 1) {
				temp.getLayoutParams().height = firstItemHeight / 2 + Math.abs(getChildAt(0).getTop() / 2);
				temp.setLayoutParams(temp.getLayoutParams());
			} else if (firstItemHeight > 0 && temp.getHeight() != (firstItemHeight / 2)) {
				temp.getLayoutParams().height = firstItemHeight / 2;
				temp.setLayoutParams(temp.getLayoutParams());
			}
		}
		if (mAdapter != null) {
			mAdapter.onScroll((float) Math.abs(getChildAt(0).getTop()) / (float) firstItemHeight,
					((LinearLayout) getChildAt(0)).getChildAt(0),
					(getChildAt(1).getTag(TAG) != null && getChildAt(1).getTag(TAG).equals(TAG_BLANK_ITEM)) ? null
							: ((LinearLayout) getChildAt(1)).getChildAt(0));

		}

	}

	/**
	 * 只要重写{@link #initView}和{@link #onScroll}就够了
	 * 
	 * @author sunzhk
	 *
	 */
	public static abstract class BaseParallaxListViewAdapter extends BaseAdapter {

		protected SimpleParallaxListView mListView;

		public BaseParallaxListViewAdapter(SimpleParallaxListView listView) {
			// TODO Auto-generated constructor stub
			mListView = listView;
		}

		/**
		 * 不需要再重写了
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 获取默认情况下item的高度
			if (mListView.getFirstItemHeight() < 0
					&& (mListView.getChildAt(0) != null ? mListView.getChildAt(0).getHeight() : -1) > 0) {
				mListView.setFirstItemHeight(mListView.getChildAt(0).getHeight());
			}
			// 在item外加壳
			View realChild;
			if (convertView == null) {
				convertView = new LinearLayout(mListView.getContext());
				convertView.setTag(TAG, TAG_SHELL_ITEM);
				realChild = initView(position, null, parent);
			} else {
				realChild = initView(position, ((LinearLayout) convertView).getChildAt(0), parent);
				((LinearLayout) convertView).removeAllViews();
			}
			((LinearLayout) convertView).addView(realChild);
			// 如果还没有获取到item的默认高度，则直接返回
			if (mListView.getFirstItemHeight() < 0) {
				return convertView;
			}
			// 设置实际item的高度
			if (realChild.getLayoutParams() == null) {
				realChild.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT, mListView.getFirstItemHeight()));
			} else {
				realChild.getLayoutParams().height = mListView.getFirstItemHeight();
			}

			convertView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, mListView.getFirstItemHeight()));

			return convertView;
		}

		/**
		 * 原来的getView现在用这个方法来实现
		 * 
		 * @param position
		 * @param convertView
		 * @param parent
		 * @return
		 */
		public abstract View initView(int position, View convertView, ViewGroup parent);

		/**
		 * 滚动的时候调用
		 * 
		 * @param percent
		 *            第一个item消失的百分比
		 * @param firstView
		 *            第一个item
		 * @param secondView
		 *            第二个item(可能为null)
		 */
		public abstract void onScroll(float percent, View firstView, View secondView);
	}

	class ParallaxedView {

		protected WeakReference<View> view;
		protected int lastOffset;
		protected List<Animation> animations;

		public ParallaxedView(View view) {
			this.lastOffset = 0;
			this.animations = new ArrayList<Animation>();
			this.view = new WeakReference<View>(view);

		}

		public boolean is(View v) {
			return (v != null && view != null && view.get() != null && view.get().equals(v));
		}

		@SuppressLint("NewApi")
		public void setOffset(float offset) {
			View view = this.view.get();
			if (view != null) {
				view.setTranslationY(offset);
			}
		}

		public void setAlpha(float alpha) {
			View view = this.view.get();
			if (view != null) {
				view.setAlpha(alpha);
			}
		}

		protected synchronized void addAnimation(Animation animation) {
			animations.add(animation);
		}

		protected void alphaPreICS(View view, float alpha) {
			addAnimation(new AlphaAnimation(alpha, alpha));
		}

		protected synchronized void animateNow() {
			View view = this.view.get();
			if (view != null) {
				AnimationSet set = new AnimationSet(true);
				for (Animation animation : animations)
					if (animation != null)
						set.addAnimation(animation);
				set.setDuration(0);
				set.setFillAfter(true);
				view.setAnimation(set);
				set.start();
				animations.clear();
			}
		}

		public void setView(View view) {
			this.view = new WeakReference<View>(view);
		}
	}

}
