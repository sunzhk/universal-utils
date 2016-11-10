package com.sunzhk.tools;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import com.sunzhk.R;
import com.sunzhk.tools.utils.DoubleClickExit;
import com.sunzhk.tools.utils.PermissionUtils;
import com.sunzhk.tools.utils.SoftArrayList;

import java.lang.ref.SoftReference;

public class BaseActivity extends Activity {

	private int maxFragmentCount = 3;

	private SparseArray<SoftArrayList<Fragment>> mFragmentsMap = new SparseArray<SoftArrayList<Fragment>>();

	private SparseArray<SoftReference<Fragment>> currentFragmentsMap = new SparseArray<SoftReference<Fragment>>();

	private FragmentManager mFragmentManager;

	private boolean isNeedDoubleClickExit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (getApplication() instanceof BaseApplication) {
			BaseApplication.addActivity(this);
		}
		mFragmentManager = getFragmentManager();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (getApplication() instanceof BaseApplication) {
			BaseApplication.removeActivity(this);
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		if (getApplication() instanceof BaseApplication) {
			BaseApplication.removeActivity(this);
		}
		super.finish();
	}

	public <T extends View> T $(int id) {
		return (T) super.findViewById(id);
	}

	/**
	 * 沉浸式状态栏？改变状态栏的颜色。api21及更高有效
	 * @param color
	 */
	protected void setStatusBarColor(int color) {
		if (Build.VERSION.SDK_INT >= 21) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			getWindow().setStatusBarColor(color);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		boolean fragmentFlag = false;
		for (int i = 0; i < currentFragmentsMap.size(); i++) {
			Fragment temp = currentFragmentsMap.valueAt(i).get();
			if (temp instanceof BaseFragment) {
				fragmentFlag = ((BaseFragment) temp).onKeyDown(keyCode, event) | fragmentFlag;
			}
		}
		if (fragmentFlag) {
			return true;
		}

		if (keyCode == KeyEvent.KEYCODE_BACK && isNeedDoubleClickExit) {
			DoubleClickExit.clickExit(this);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void setMaxFragmentCount(int maxFragmentCount) {
		this.maxFragmentCount = maxFragmentCount;
	}

	protected void setIsNeedDoubleClickExit(boolean isNeedDoubleClickExit) {
		this.isNeedDoubleClickExit = isNeedDoubleClickExit;
	}

	protected <E extends Fragment> void loadFragment(int parent, E fragment) {
		loadFragment(parent, fragment, true);
	}

	protected <E extends Fragment> void loadFragment(int parent, E fragment, boolean needAnimations) {
		if (fragment == null || (currentFragmentsMap.get(parent) != null && fragment == currentFragmentsMap.get(parent).get())) {
			return;
		}
		Class<? extends Fragment> fragmentClass = fragment.getClass();

		SoftArrayList<Fragment> fragmentsList = mFragmentsMap.get(parent);

		if (fragmentsList == null) {
			fragmentsList = new SoftArrayList<Fragment>();
			mFragmentsMap.put(parent, fragmentsList);
		} else if (fragmentsList.size() > 0) {
			for (Fragment tempFragment : mFragmentsMap.get(parent)) {
				if (tempFragment.getClass() == fragmentClass) {
					mFragmentsMap.get(parent).remove(tempFragment);
					break;
				}
			}
		}

		fragmentsList.add(fragment);

		while (fragmentsList.size() > maxFragmentCount) {
			fragmentsList.remove(0);
		}
		showFragment(parent, fragment, needAnimations);
	}

	protected <E extends Fragment> void loadFragment(int parent, Class<E> fragmentClass) {
		this.loadFragment(parent, fragmentClass, false, null, true);
	}

	protected <E extends Fragment> void loadFragment(int parent, Class<E> fragmentClass, boolean needAnimations) {
		this.loadFragment(parent, fragmentClass, false, null, needAnimations);
	}

	protected <E extends Fragment> void loadFragment(int parent, Class<E> fragmentClass, boolean needReInstance, Bundle bundle) {
		this.loadFragment(parent, fragmentClass, needReInstance, bundle, true);
	}

	@SuppressWarnings({"unchecked"})
	protected <E extends Fragment> void loadFragment(int parent, Class<E> fragmentClass, boolean needReInstance, Bundle bundle, boolean needAnimations) {
//		if(parent <= 0 || fragmentClass == null || (!needReInstance && currentFragmentsMap.get(parent).get() != null && currentFragmentsMap.get(parent).get().getClass() == fragmentClass)){
		if (parent <= 0 || fragmentClass == null || (currentFragmentsMap.get(parent) != null && currentFragmentsMap.get(parent).get() != null && fragmentClass == currentFragmentsMap.get(parent).get().getClass())) {
			return;
		}

		E newFragment = null;

		SoftArrayList<Fragment> fragmentList = mFragmentsMap.get(parent);

		if (fragmentList == null) {
			fragmentList = new SoftArrayList<Fragment>();
			mFragmentsMap.put(parent, fragmentList);
		} else if (fragmentList.size() > 0) {
			for (Fragment tempFragment : fragmentList) {
				if (tempFragment.getClass() == fragmentClass) {
					newFragment = (E) tempFragment;
					fragmentList.remove(tempFragment);
					break;
				}
			}
		}

		if (needReInstance || newFragment == null) {
			try {
				newFragment = fragmentClass.newInstance();
				newFragment.setArguments(bundle);
				loadFragment(parent, newFragment, needAnimations);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}

		if (newFragment != null) {
			fragmentList.add(newFragment);
			showFragment(parent, newFragment, needAnimations);
		}
	}

	private <E extends Fragment> void showFragment(int parent, E fragment, boolean needAnimations) {
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		if (needAnimations) {
			transaction.setCustomAnimations(R.animator.fragment_slide_right_in,
					R.animator.fragment_slide_left_out,
					R.animator.fragment_slide_left_in,
					R.animator.fragment_slide_right_out);
		}
		SoftReference<Fragment> softReference = currentFragmentsMap.get(parent);
		Fragment current = softReference == null ? null : softReference.get();
		if (current != null) {
			transaction.hide(current);
		}
		if (fragment.isAdded()) {
			transaction.show(fragment);
		} else {
			transaction.add(parent, fragment);
		}

		onFragmentChange(transaction, current, fragment);
		transaction.commit();
		currentFragmentsMap.put(parent, new SoftReference<Fragment>(fragment));
	}

	public Fragment getCurrentFragment(int parent) {
		return currentFragmentsMap.get(parent) == null ? null : currentFragmentsMap.get(parent).get();
	}

	/**
	 * 清除所有列表中的Fragment除了正在显示的
	 * 未经过测试
	 */
	public void clearFragmentStack() {

		if (mFragmentsMap.size() == 0) {
			return;
		}

		FragmentTransaction transaction = mFragmentManager.beginTransaction();

		for (int i = 0; i < mFragmentsMap.size(); i++) {
			SoftArrayList<Fragment> fragmentsList = mFragmentsMap.valueAt(i);
			for (int j = fragmentsList.size() - 2; j >= 0; j--) {
				if (fragmentsList.get(j).isAdded()) {
					transaction.remove(fragmentsList.get(j));
				}
				fragmentsList.remove(j);
			}
		}

//		Iterator<Entry<Integer, SoftArrayList<Fragment>>> iterator = mFragmentsMap.entrySet().iterator();

//		while(iterator.hasNext()){
//			SoftArrayList<Fragment> fragmentsList = iterator.next().getValue();
//			for(int i = fragmentsList.size()-2 ; i >= 0 ; i--){
//				if(fragmentsList.get(i).isAdded()){
//					transaction.remove(fragmentsList.get(i));
//				}
//				fragmentsList.remove(i);
//			}
//		}

//		for(int i = mFragments.size()-2 ; i >= 0 ; i--){
//			if(mFragments.get(i).isAdded()){
//				transaction.remove(mFragments.get(i));
//			}
//			mFragments.remove(i);
//		}

		transaction.commit();
	}

	public void getPermission(PermissionUtils.DangerousPermissions permission, int requestCode) {
		PermissionUtils.getPermission(this, permission, permission.ordinal());
	}
	public void getPermission(String permission, int requestCode) {
		PermissionUtils.getPermission(this, permission, PermissionUtils.DangerousPermissions.valueOf(permission).ordinal());
	}

	protected void onFragmentChange(FragmentTransaction transaction, Fragment hide, Fragment show) {
	}

}
