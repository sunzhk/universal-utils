package com.sunzhk.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sunzhk.R;
import com.sunzhk.tools.BaseApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 自制Gallery
 * @author sunzhk
 *
 */
public class BaseGallery extends FrameLayout implements OnClickListener {

	private BaseGalleryListener baseGalleryListener;
	private ViewPager mViewPager;
	private LinearLayout mDotsLayout;
	/**
	 * 图片地址
	 */
	private List<String> mImageUrls = new ArrayList<String>();
	/**
	 * 
	 */
	protected ImageLoader mImageLoader = ImageLoader.getInstance();
	/**
	 * 用到的imageView列表
	 */
	private final List<ImageView> mImageViews = new ArrayList<ImageView>();
	
	private Timer mTimer = new Timer();
	
	public BaseGallery(Context context) {
		// TODO Auto-generated constructor stub
		super(context);
		initView();
	}

	public BaseGallery(Context context, AttributeSet attrs) {
		// TODO Auto-generated constructor stub
		super(context, attrs);
		initView();
	}
	
	public BaseGallery(Context context, AttributeSet attrs, int defStyleAttr) {
		// TODO Auto-generated constructor stub
		super(context, attrs, defStyleAttr);
		initView();
	}
	
	private void initView() {

		mViewPager = new ViewPager(getContext());
		mDotsLayout = new LinearLayout(getContext());
		
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		mViewPager.setLayoutParams(layoutParams);
		layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0, 0, 0, 2);
		mDotsLayout.setOrientation(LinearLayout.HORIZONTAL);
		layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
		mDotsLayout.setLayoutParams(layoutParams);
		mViewPager.setAdapter(new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				// TODO Auto-generated method stub
				return arg0 == arg1;
			}
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return mImageViews.size();
			}
			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				// TODO Auto-generated method stub
				
				ImageView imageView = mImageViews.get(position);
				
				mViewPager.addView(imageView);
				
				return imageView;
			}
			@Override
			public void destroyItem(ViewGroup container, int position, Object object) {
				// TODO Auto-generated method stub
				container.removeView(mImageViews.get(position));
			}
		});
		
		mViewPager.addOnPageChangeListener(mPageChangeListener);

		addView(mViewPager);
		addView(mDotsLayout);
	}

	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
		mTimer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				mViewPager.post(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						int index = mViewPager.getCurrentItem();
						if(mViewPager.getChildCount() != 0){
							mViewPager.setCurrentItem((index + 1) % mViewPager.getChildCount());
						}
					}
				});
			}
		}, 5000, 5000);
	}
	
	public void setImagesUrls(List<String> imageUrls){
		mImageUrls = imageUrls;
		showPictures();
	}
	
	public int getPosition(){
		return mViewPager.getCurrentItem();
	}
	
	private void showPictures(){
		if(mImageUrls == null || mImageUrls.isEmpty()){
			return;
		}
		ImageView imageView;
		LayoutParams ivParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(BaseApplication.getWindowWidth() / 55, BaseApplication.getWindowWidth() / 55);
		for(String url : mImageUrls){
			imageView = new ImageView(getContext());
			imageView.setLayoutParams(ivParams);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageView.setOnClickListener(this);
			mImageViews.add(imageView);
			mImageLoader.displayImage(url, imageView);
			
			ImageView dot = new ImageView(getContext());
			dot.setBackgroundResource(mDotsLayout.getChildCount() == 0 ? R.drawable.dot_red : R.drawable.dot_gray);
			dot.setAlpha(0.8f);
			mDotsLayout.addView(dot, params);
			
		}
		mViewPager.getAdapter().notifyDataSetChanged();
	}

	private OnPageChangeListener mPageChangeListener=new OnPageChangeListener(){
		@Override
		public void onPageSelected(int arg0){
			if (arg0 == 1) mDotsLayout.setVisibility(View.VISIBLE);
			for(int i = 0, length = mDotsLayout.getChildCount(); i < length; i++){
				mDotsLayout.getChildAt(i).setBackgroundResource(i == arg0 ? R.drawable.dot_red : R.drawable.dot_gray);
			}
			// mIsChangingPic=false;
		}
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2){
			// if (arg0 == 0){// 滑动结束，即切换完毕或者加载完毕
			// mIsChangingPic=false;
			// int position = mViewPager.getCurrentItem();
			// if (position == mViewPager.getAdapter().getCount() - 1)
			// mViewPager.setCurrentItem(0);
			// else if (position == 0)
			// mViewPager.setCurrentItem(mViewPager.getAdapter().getCount()
			// - 1);
			// return;
			// }
			// mIsChangingPic = arg0 == 2;
		}
		@Override
		public void onPageScrollStateChanged(int arg0){
		}
	};
	
	@Override
	public void setOnClickListener(OnClickListener l) {}

	public void setOnImagesClickListener(BaseGalleryListener l) {
		// TODO Auto-generated method stub
		baseGalleryListener = l;
	}
	public static interface BaseGalleryListener extends OnClickListener{
		
		@Override
		void onClick(View v);
		
		void onClick(View v, int position);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(baseGalleryListener != null){
			baseGalleryListener.onClick(v, getPosition());
		}
	}

}
