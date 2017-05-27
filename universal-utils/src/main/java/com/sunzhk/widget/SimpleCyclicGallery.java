package com.sunzhk.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sunzhk on 2017/5/27.
 */

public class SimpleCyclicGallery extends ViewPager {

	private final View[] mGalleryViews = new View[3];
	private BaseCyclicGalleryAdapter mAdapter;

	public SimpleCyclicGallery(Context context) {
		super(context);
		init();
	}

	public SimpleCyclicGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		super.setAdapter(mPagerAdapter);
	}

	private void initGalleryViews() {
		for (int i = mGalleryViews.length - 1; i >= 0; i--) {
			mGalleryViews[i] = mAdapter.createImageView();
		}
	}

	@Override
	@Deprecated
	public void setAdapter(PagerAdapter adapter) {
		throw new IllegalArgumentException("this method is deprecated,use SimpleCyclicGallery.setAdapter(BaseCyclicGalleryAdapter)");
	}

	public void setAdapter(BaseCyclicGalleryAdapter adapter) {
		mAdapter = adapter;
		initGalleryViews();
		mPagerAdapter.notifyDataSetChanged();
	}

	private PagerAdapter mPagerAdapter = new PagerAdapter() {

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View imageView = mGalleryViews[position % mGalleryViews.length];
			if (container.indexOfChild(imageView) < 0) {
				container.addView(imageView);
				imageView.getLayoutParams().width = ViewPager.LayoutParams.MATCH_PARENT;
				imageView.getLayoutParams().height = ViewPager.LayoutParams.MATCH_PARENT;
			}
			if (mAdapter != null) {
				mAdapter.showImage(position, imageView);
			}
			return imageView;
		}

		@Override
		public int getCount() {
			return mAdapter == null ? 0 : mAdapter.getCount();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	};

	public interface BaseCyclicGalleryAdapter<V extends View> {
		V createImageView();

		void showImage(int position, V imageView);

		int getCount();
	}
}