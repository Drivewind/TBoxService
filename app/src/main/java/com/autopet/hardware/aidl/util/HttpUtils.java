package com.autopet.hardware.aidl.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;
import android.util.Log;

public class HttpUtils {
	private static final int TIMEOUT_IN_MILLIONS = 5000;
	public static final String UPDATE_URL = "http://vehicle.chexiang.com//vehicle/appVersion.action?cmd=listAppVersion";

	public interface CallBack {
		void onRequestComplete(String result, int code);
	}

	/**
	 * 判断网络是否连接
	 */
	public static boolean isConnected(Context context) {

		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (null != connectivity) {

			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (null != info && info.isConnected()) {
				if (info.getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断网络是否连接,是否为wifi
	 */
	public static boolean isWifiConnected(Context context) {

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager != null) {
			NetworkInfo wifiNetworkInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (wifiNetworkInfo.isConnected()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 异步的Get请求
	 */
	public static void doGetAsyn(final String urlStr, final int code,
			final CallBack callBack) {
		new Thread() {
			public void run() {
				try {
					String result = doGet(urlStr);
					if (callBack != null) {
						callBack.onRequestComplete(result, code);
					}
				} catch (Exception e) {
					e.printStackTrace();

				}

			};
		}.start();
	}

	/**
	 * 异步的Post请求
	 */
	public static void doPostAsyn(final String urlStr, final String params,
			final int code, final CallBack callBack) throws Exception {

		new Thread() {
			public void run() {
				try {
					String result = doPost(urlStr, params);
					if (callBack != null) {
						callBack.onRequestComplete(result, code);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();

	}

	/**
	 * 异步的Post请求
	 */
	public static void doPutAsyn(final String urlStr, final String params,
			final int code, final CallBack callBack) throws Exception {

		new Thread() {
			public void run() {
				try {
					String result = doPut(urlStr, params);

					if (callBack != null) {
						callBack.onRequestComplete(result, code);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();

	}

	/**
	 * Get请求，获得返回数据
	 */
	public static String doGet(String urlStr) {
		URL url = null;
		HttpURLConnection conn = null;
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
			conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
			conn.setRequestProperty("connection", "Keep-Alive");
			if (conn.getResponseCode() == 200) {
				is = conn.getInputStream();
				baos = new ByteArrayOutputStream();
				int len = -1;
				byte[] buf = new byte[128];

				while ((len = is.read(buf)) != -1) {
					baos.write(buf, 0, len);
				}
				baos.flush();
				return baos.toString();
			} else {
				throw new RuntimeException(" responseCode is not 200 ... ");
			}

		} catch (Exception e) {
			// 检测网络连接超时和网络连接异常
			if (e instanceof SocketTimeoutException) {
				return "SocketTimeoutException";
			} else if (e instanceof UnknownHostException) {
				return "UnknownHostException";
			}
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
				if (baos != null)
					baos.close();
				if (conn != null) {
					conn.disconnect();
				}
			} catch (IOException e) {
			}
		}
		return null;
	}

	/**
	 * 向指定 URL 发送POST方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 * @throws Exception
	 */
	public static String doPost(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		HttpURLConnection conn = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			conn = (HttpURLConnection) realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
			conn.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
			conn.setRequestMethod("POST");
			conn.setRequestProperty("charset", "utf-8");
			conn.setUseCaches(false);
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
			conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);

			if (param != null && !param.trim().equals("")) {
				// 获取URLConnection对象对应的输出流
				out = new PrintWriter(conn.getOutputStream());
				// 发送请求参数
				out.print(param);
				// flush输出流的缓冲
				out.flush();
			}

			Log.e("222", conn.getResponseCode() + "");
			if (conn.getResponseCode() != 200) {
				return "400";
			}
			// 定义BufferedReader输入流来读取URL的响应

			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			Log.e("doPost error", e.toString());
			e.printStackTrace();
			// 检测网络连接超时和网络连接异常
			if (e instanceof SocketTimeoutException) {
				return "SocketTimeoutException";
			} else if (e instanceof UnknownHostException) {
				return "UnknownHostException";
			} else if (e instanceof ConnectException) {
				return "UnknownHostException";
			}
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				if (conn != null) {
					conn.disconnect();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 向指定 URL 发送PUT方法的请求
	 * 
	 * @param url
	 *            发送请求的 URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return 所代表远程资源的响应结果
	 * @throws Exception
	 */
	public static String doPut(String url, String param) {
		PrintWriter out = null;
		BufferedReader in = null;
		HttpURLConnection conn = null;
		String result = "";
		Log.e("doPut", url + " " + param);
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			conn = (HttpURLConnection) realUrl.openConnection();
			// 设置通用的请求属性
			conn.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
			conn.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("charset", "utf-8");
			conn.setUseCaches(false);
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
			conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);

			if (param != null && !param.trim().equals("")) {
				// 获取URLConnection对象对应的输出流
				out = new PrintWriter(conn.getOutputStream());
				// 发送请求参数
				out.print(param);
				// flush输出流的缓冲
				out.flush();
			}
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(
					new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			Log.e("doPut err ", e.toString());
			e.printStackTrace();
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
				if (conn != null) {
					conn.disconnect();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

}
