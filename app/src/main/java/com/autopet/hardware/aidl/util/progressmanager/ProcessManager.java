package com.autopet.hardware.aidl.util.progressmanager;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.autopet.hardware.aidl.R;
import com.autopet.hardware.aidl.util.progressmanager.models.AndroidAppProcess;
import com.autopet.hardware.aidl.util.progressmanager.models.AndroidProcess;

/**
 * Helper class to get a list of processes on Android.
 * 
 * <p>
 * <b>Note:</b> Every method in this class should <i>not</i> be executed on the
 * main thread.
 * </p>
 */
public class ProcessManager {

	private ProcessManager() {
		throw new AssertionError("no instances");
	}

	/**
	 * @return a list of <i>all</i> processes running on the device.
	 */
	public static List<AndroidProcess> getRunningProcesses() {
		List<AndroidProcess> processes = new ArrayList<AndroidProcess>();
		File[] files = new File("/proc").listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				int pid;
				try {
					pid = Integer.parseInt(file.getName());
				} catch (NumberFormatException e) {
					continue;
				}
				try {
					processes.add(new AndroidProcess(pid));
				} catch (IOException e) {
					// If you are running this from a third-party app, then
					// system apps will not be
					// readable on Android 5.0+ if SELinux is enforcing. You
					// will need root access or an
					// elevated SELinux context to read all files under /proc.
					// See: https://su.chainfire.eu/#selinux
				}
			}
		}
		return processes;
	}

	/**
	 * @return a list of all running app processes on the device.
	 */
	public static List<AndroidAppProcess> getRunningAppProcesses() {
		List<AndroidAppProcess> processes = new ArrayList<AndroidAppProcess>();
		File[] files = new File("/proc").listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				int pid;
				try {
					pid = Integer.parseInt(file.getName());
				} catch (NumberFormatException e) {
					continue;
				}
				try {
					processes.add(new AndroidAppProcess(pid));
				} catch (AndroidAppProcess.NotAndroidAppProcessException ignored) {
				} catch (IOException e) {
					// If you are running this from a third-party app, then
					// system apps will not be
					// readable on Android 5.0+ if SELinux is enforcing. You
					// will need root access or an
					// elevated SELinux context to read all files under /proc.
					// See: https://su.chainfire.eu/#selinux
				}
			}
		}
		return processes;
	}

	/**
	 * Get a list of user apps running in the foreground.
	 * 
	 * @param ctx
	 *            the application context
	 * @return a list of user apps that are in the foreground.
	 */
	public static List<AndroidAppProcess> getRunningForegroundApps(Context ctx) {
		List<AndroidAppProcess> processes = new ArrayList<AndroidAppProcess>();
		File[] files = new File("/proc").listFiles();
		PackageManager pm = ctx.getPackageManager();
		for (File file : files) {
			if (file.isDirectory()) {
				int pid;
				try {
					pid = Integer.parseInt(file.getName());
				} catch (NumberFormatException e) {
					continue;
				}
				try {
					AndroidAppProcess process = new AndroidAppProcess(pid);
					if (!process.foreground) {
						// Ignore processes not in the foreground
						continue;
					} else if (process.uid >= 1000 && process.uid <= 9999) {
						// First app user starts at 10000. Ignore system
						// processes.
						continue;
					} else if (process.name.contains(":")) {
						// Ignore processes that are not running in the default
						// app process.
						continue;
					} else if (pm.getLaunchIntentForPackage(process
							.getPackageName()) == null) {
						// Ignore processes that the user cannot launch.
						// TODO: remove this block?
						continue;
					}
					processes.add(process);
				} catch (AndroidAppProcess.NotAndroidAppProcessException ignored) {
				} catch (IOException e) {
					// If you are running this from a third-party app, then
					// system apps will not be
					// readable on Android 5.0+ if SELinux is enforcing. You
					// will need root access or an
					// elevated SELinux context to read all files under /proc.
					// See: https://su.chainfire.eu/#selinux
				}
			}
		}
		return processes;
	}

	/**
	 * 
	 * @return {@code true} if this process is in the foreground.
	 */
	public static boolean isMyProcessInTheForeground() {
		List<AndroidAppProcess> processes = getRunningAppProcesses();
		int myPid = android.os.Process.myPid();
		for (AndroidAppProcess process : processes) {
			if (process.pid == myPid && process.foreground) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns a list of application processes that are running on the device.
	 * 
	 * @return a list of RunningAppProcessInfo records, or null if there are no
	 *         running processes (it will not return an empty list). This list
	 *         ordering is not specified.
	 */
	public static List<RunningAppProcessInfo> getRunningAppProcessInfo(
			Context ctx) {
		if (Build.VERSION.SDK_INT >= 21) {
			List<AndroidAppProcess> runningAppProcesses = ProcessManager
					.getRunningAppProcesses();
			List<RunningAppProcessInfo> appProcessInfos = new ArrayList<RunningAppProcessInfo>();
			for (AndroidAppProcess process : runningAppProcesses) {
				RunningAppProcessInfo info = new RunningAppProcessInfo(
						process.name, process.pid, null);
				info.uid = process.uid;
				// TODO: Get more information about the process. pkgList,
				// importance, lru, etc.
				appProcessInfos.add(info);
			}
			return appProcessInfos;
		}
		ActivityManager am = (ActivityManager) ctx
				.getSystemService(Context.ACTIVITY_SERVICE);
		return am.getRunningAppProcesses();
	}

	/**
	 * Comparator to list processes with a lower oom_score_adj first.
	 * 
	 * If the /proc/[pid]/oom_score_adj is not readable, then processes are
	 * sorted by name.
	 */
	public static final class ProcessComparator implements
			Comparator<AndroidProcess> {

		@Override
		public int compare(AndroidProcess lhs, AndroidProcess rhs) {
			try {
				int oomScoreAdj1 = lhs.oom_score_adj();
				int oomScoreAdj2 = rhs.oom_score_adj();
				if (oomScoreAdj1 < oomScoreAdj2) {
					return -1;
				} else if (oomScoreAdj1 > oomScoreAdj2) {
					return 1;
				}
			} catch (IOException ignored) {
			}
			return lhs.name.compareToIgnoreCase(rhs.name);
		}
	}

	/**
	 * 获取系统运行的进程信息
	 * 
	 * @param context
	 * @return
	 */
	public static List<TaskInfo> getTaskInfos(Context context) {
		// 应用程序管理器
		ActivityManager am = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);

		// 应用程序包管理器
		PackageManager pm = context.getPackageManager();

		List<AndroidAppProcess> processInfos = ProcessManager
				.getRunningAppProcesses();

		List<TaskInfo> taskinfos = new ArrayList<TaskInfo>();
		// 遍历运行的程序,并且获取其中的信息
		for (AndroidAppProcess processInfo : processInfos) {
			TaskInfo taskinfo = new TaskInfo();
			// 应用程序的包名
			String packname = processInfo.name;
			taskinfo.setPackname(packname);
			// 湖区应用程序的内存 信息
			android.os.Debug.MemoryInfo[] memoryInfos = am
					.getProcessMemoryInfo(new int[] { processInfo.pid });
			long memsize = memoryInfos[0].getTotalPrivateDirty() * 1024L;
			taskinfo.setMemsize(memsize);
			try {
				// 获取应用程序信息
				ApplicationInfo applicationInfo = pm.getApplicationInfo(
						packname, 0);
				Drawable icon = applicationInfo.loadIcon(pm);
				taskinfo.setIcon(icon);
				String name = applicationInfo.loadLabel(pm).toString();
				taskinfo.setName(name);
				if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
					// 用户进程
					taskinfo.setUserTask(true);
				} else {
					// 系统进程
					taskinfo.setUserTask(false);
				}
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// 系统内核进程 没有名称
				taskinfo.setName(packname);
				Drawable icon = context.getResources().getDrawable(
						R.drawable.ic_launcher);
				taskinfo.setIcon(icon);
			}
			if (taskinfo != null) {
				taskinfos.add(taskinfo);
			}
		}
		return taskinfos;
	}

	/**
	 * 获取最近系统运行的进程包名
	 * 
	 * @param context
	 * @return
	 */
	public static String getRecentTaskInfo(Context context) {
		List<AndroidAppProcess> processInfos = ProcessManager
				.getRunningAppProcesses();
		// 遍历运行的程序,并且获取其中的信息
		for (int i = processInfos.size() - 1; i > 0; i--) {
			AndroidAppProcess processInfo = processInfos.get(i);
			String packname = processInfo.name;
			if (packname.equals("com.anyonavinfo.cpad.cpadfmradio")
					|| packname.equals("com.wedrive.welink.sgmw.navigation")
					|| packname.equals("com.anyonavinfo.bluetoothphone")
					|| packname.equals("com.autopet.hardware.aidl")
					|| packname.equals("com.tencent.mm")
					|| packname.equals("com.hpplay.happyplay.aw")
					|| packname.equals("com.svox.pico")
					|| packname.equals("com.example.testcamera")
					|| packname.startsWith("com.qihoo")
					|| packname.startsWith("com.android")
					|| packname.startsWith("logcat")
					|| packname.startsWith("android")
					|| packname.startsWith("com.mediatek")
					|| packname.contains("system")
					|| packname.contains("update") || packname.contains(":")
					|| packname.contains("service")
					|| packname.contains("inputmethod")
					|| packname.contains("com.btf")
					|| packname.contains("hotwords")) {
				continue;
			} else {
				return packname;
			}
		}
		return "";
	}

	/**
	 * 获取最近運行的app包名
	 * 
	 * @param context
	 * @return
	 */
	public static String getRecentAppInfo(Context context) {
		ArrayList<TaskInfo> list = (ArrayList<TaskInfo>) getTaskInfos(context);
		if (list != null && list.size() > 0) {
			for (int i = list.size() - 1; i >= 0; i--) {
				TaskInfo info = list.get(i);
				if (info.isUserTask()
						&& !info.getPackname().equals(
								"com.anyonavinfo.cpad.cpadfmradio")
						&& !info.getPackname().equals(
								"com.wedrive.welink.sgmw.navigation")
						&& !info.getPackname().equals(
								"com.anyonavinfo.bluetoothphone")) {
					return info.getPackname();
				}
			}
		}
		return null;
	}

}
