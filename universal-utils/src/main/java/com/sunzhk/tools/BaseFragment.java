package com.sunzhk.tools;

import android.app.Fragment;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sunzhk.widget.FractionTranslateLayout;

import java.lang.reflect.Method;

public abstract class BaseFragment extends Fragment {

	protected ImageLoader mImageLoader = ImageLoader.getInstance();
	
	protected View contentView;
	
	private FractionTranslateLayout topLayout;

	public BaseFragment(){}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		onCreateView();
		if(contentView instanceof FractionTranslateLayout){
			return contentView;
		}
		topLayout = new FractionTranslateLayout(getActivity());
		topLayout.addView(contentView);
		return topLayout;
	}
	
	public abstract void onCreateView();
	
	public void setContentView(View contentView){
		this.contentView = contentView;
	}
	
	public void setContentView(int layoutResID){
		if(contentView != null && contentView.isActivated() && contentView.getId() == layoutResID){
			return;
		}
		contentView = getActivity().getLayoutInflater().inflate(layoutResID, null);
	}
	/**
	 * 反射调用Fragment的getLayoutInflater方法--竟然没有崩溃。有机会得好好研究一下hide和重写的原理--某些情况下会发生返回null的情况，下回再看
	 * @return 获取父类的LayoutInflater
	 */
	public LayoutInflater getLayoutInflater() {
		LayoutInflater result = null;
		try {
			Class<Fragment> clazz = Fragment.class;
			//Class<BaseFragment> clazz = BaseFragment.class;
			Method method = clazz.getMethod("getLayoutInflater");
			result = (LayoutInflater) method.invoke(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		if(result == null){
//			result = getActivity().getLayoutInflater();
//		}
//		return result;
		return result == null ? getActivity().getLayoutInflater() : result;
	}
	
	public <T extends View> T findViewById(int id) {
		return (T) contentView.findViewById(id);
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event){
		return false;
	}
	
}
