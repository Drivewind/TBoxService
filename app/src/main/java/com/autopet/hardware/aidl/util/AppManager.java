package com.autopet.hardware.aidl.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Process;
import android.util.Log;

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 */
public class AppManager {

	private static Stack<Activity> activityStack;
	private static AppManager instance;

	private AppManager() {
	}

	/**
	 * 单一实例
	 */
	public static AppManager getAppManager() {
		if (instance == null) {
			instance = new AppManager();
		}
		return instance;
	}

	/**
	 * 添加Activity到堆栈
	 */
	public void getSize(String name) {
		System.out.println("---" + name + "--" + activityStack.size());
	}

	/**
	 * 添加Activity到堆栈
	 */
	public void addActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	/**
	 * 获取当前Activity（堆栈中最后一个压入的）
	 */
	public Activity currentActivity() {
		Activity activity = activityStack.lastElement();
		return activity;
	}

	/**
	 * 结束当前Activity（堆栈中最后一个压入的）
	 */
	public void finishActivity() {
		Activity activity = activityStack.lastElement();
		finishActivity(activity);
	}

	/**
	 * 结束指定的Activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 结束指定类名的Activity
	 */
	public void finishActivity(Class<?> cls) {
		Stack<Activity> activitys = new Stack<Activity>();
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				activitys.add(activity);
			}
		}

		for (Activity activity : activitys) {
			finishActivity(activity);
		}
	}

	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i)) {
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
	}

	/**
	 * 退出应用程序
	 */
	public void AppExit() {
		try {
			finishAllActivity();
			int pid = Process.myPid();
			Process.killProcess(pid);
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 获得version版本code
	public static int getVersionCode(Context context, String packageName) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(packageName, 0);
			int version = info.versionCode;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	// 获得version版本code
	public static String getVersionName(Context context, String packageName) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(packageName, 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "找不到版本name";
		}
	}

	// 获得app图标
	public static Drawable getAppIcon(Context context, String packageName) {
		try {
			PackageManager manager = context.getPackageManager();
			ApplicationInfo info = manager.getApplicationInfo(packageName, 0);
			return info.loadIcon(manager);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// 检查app是否安装
	public static boolean AppIsExist(Context context, String packageName) {
		List<PackageInfo> packs = context.getPackageManager()
				.getInstalledPackages(0);
		for (int i = 0; i < packs.size(); i++) {
			PackageInfo p = packs.get(i);
			if (p.versionName == null) {
				continue;
			}
			if (p.packageName.equalsIgnoreCase(packageName)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isServiceWork(Context context, String serviceName) {
		ActivityManager myAM = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> myList = myAM.getRunningServices(40);
		if (myList.size() <= 0) {
			return false;
		}
		for (int i = 0; i < myList.size(); i++) {
			String mName = myList.get(i).service.getClassName().toString();
			if (mName.equals(serviceName)) {
				return true;
			}
		}
		return false;
	}

	public static boolean launchActivity(Context context, String packageName) {
		if (packageName != null && !packageName.equals("")
				&& AppIsExist(context, packageName)) {
			Intent intent = context.getPackageManager()
					.getLaunchIntentForPackage(packageName);
			context.startActivity(intent);
			return true;
		}
		return false;
	}
}
