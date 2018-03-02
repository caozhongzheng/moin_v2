/**
 * 
 */
package com.moinapp.wuliao.commons.ui;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.LinkedList;

/**
 * 可以使用Tab标签来切换fragment的工具
 * 
 */
public class TabFragmentManager {
	public static interface IFragment {
		public android.support.v4.app.Fragment getFragment();

		public View getTabView(LayoutInflater inflater);
	}

	public static View setFragmentsForActivity(FragmentActivity activity,
			String[] fragmentClasses) {
		return setFragmentsForActivity(activity,
				fragmentClassNamesToObjs(fragmentClasses));
	}

	public static View setFragmentsForActivity(FragmentActivity activity,
			IFragment[] fragments) {
		LayoutInflater inflater = LayoutInflater.from(activity);
		LinearLayout contentView = new LinearLayout(activity);
		LinearLayout fragment_tab_layout = setFragmentsForActivity(activity,
				contentView);
		int fragContentId = activity.getClass().getName().hashCode();
		if (fragContentId < 0) {
			fragContentId = ~fragContentId;
		}
		_setFragmentsForActivity(activity.getSupportFragmentManager(),
				inflater, fragment_tab_layout, fragments, fragContentId);
		return contentView;
	}

	private static LinearLayout setFragmentsForActivity(Activity activity,
			LinearLayout contentView) {

		{
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			contentView.setOrientation(LinearLayout.VERTICAL);
			contentView.setLayoutParams(params);
		}

		FrameLayout fragment_content = new FrameLayout(activity);
		{
			int fragContentId = activity.getClass().getName().hashCode();
			if (fragContentId < 0) {
				fragContentId = ~fragContentId;
			}
			fragment_content.setId(fragContentId);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, 0);
			params.weight = 1;
			contentView.addView(fragment_content, params);
		}
		LinearLayout fragment_tab_layout = new LinearLayout(activity);

		DisplayMetrics metric = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
		// int width = metric.widthPixels; // 屏幕宽度（像素）
		// int height = metric.heightPixels; // 屏幕高度（像素）
		float density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）
		// int densityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
		{
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					(int) (60 * density));
			fragment_tab_layout.setOrientation(LinearLayout.HORIZONTAL);
			fragment_tab_layout.setBackgroundColor(Color.BLACK);
			contentView.addView(fragment_tab_layout, params);
		}
		// activity.setContentView(contentView, contentView.getLayoutParams());
		return fragment_tab_layout;
	}

	private static IFragment[] fragmentClassNamesToObjs(String[] fragmentClasses) {
		LinkedList<IFragment> list = new LinkedList<IFragment>();
		for (int i = 0; i < fragmentClasses.length; i++) {
			String fragmentClass = fragmentClasses[i];
			try {
				IFragment ifrag = (IFragment) Class.forName(fragmentClass)
						.newInstance();
				list.add(ifrag);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			} catch (ClassCastException e) {
				throw e;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		int size = list.size();
		if (size == 0) {
			return new IFragment[] {};
		}
		IFragment[] fragments = new IFragment[size];
		list.toArray(fragments);
		return fragments;
	}

	public static View setFragmentsForActivity(FragmentActivity activity,
			int layoutResId, int fragmentId, int tablayoutResId,
			String[] fragmentClasses) {

		return setFragmentsForActivity(activity, layoutResId, fragmentId,
				tablayoutResId, fragmentClassNamesToObjs(fragmentClasses));
	}

	public static View setFragmentsForActivity(FragmentActivity activity,
			int layoutResId, int fragmentId, int tablayoutResId,
			final IFragment[] fragments) {
		LayoutInflater inflater = LayoutInflater.from(activity);
		View lyt = inflater.inflate(layoutResId, null);
		LinearLayout tablyt = (LinearLayout) lyt.findViewById(tablayoutResId);
		_setFragmentsForActivity(activity.getSupportFragmentManager(),
				inflater, tablyt, fragments, fragmentId);
		return lyt;
	}

	private static void _setFragmentsForActivity(
			final FragmentManager fragmentManager, LayoutInflater inflater,
			LinearLayout tablyt, final IFragment[] fragments,
			final int fragmentLayoutId) {
		// 开启一个Fragment事务
		final FragmentTransaction transaction = fragmentManager
				.beginTransaction();
		for (final IFragment fragThis : fragments) {
			final Fragment fragment = fragThis.getFragment();
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.weight = 1;
			View tabview = fragThis.getTabView(inflater);
			tablyt.addView(tabview, params);
			// 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
			transaction.add(fragmentLayoutId, fragment);
			transaction.hide(fragment);
			tabview.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					FragmentTransaction transaction = fragmentManager
							.beginTransaction();
					{
						int n = fragmentManager.getBackStackEntryCount();
						while (n-- > 0) {
							fragmentManager.popBackStackImmediate();
						}
					}
					for (final IFragment ifrag : fragments) {
						boolean show = ifrag == fragThis;
						if (show) {
							transaction.show(ifrag.getFragment());
						} else {
							transaction.hide(ifrag.getFragment());
						}
					}
					transaction.commit();
				}
			});
		}
		// select the first tab
		transaction.show(fragments[0].getFragment());
		// clearSelection
		transaction.commit();
	}

	public static boolean popBackStack(FragmentActivity activity) {
		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		if (fragmentManager.getBackStackEntryCount() > 0) {
			fragmentManager.popBackStack();
			return true;
		}
		return false;
	}
}
