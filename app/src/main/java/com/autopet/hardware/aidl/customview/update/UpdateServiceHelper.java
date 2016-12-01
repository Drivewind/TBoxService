package com.autopet.hardware.aidl.customview.update;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.autopet.hardware.aidl.R;
import com.autopet.hardware.aidl.util.AppManager;
import com.autopet.hardware.aidl.util.FileUtils;
import com.autopet.hardware.aidl.util.HttpUtils;

public class UpdateServiceHelper implements OnProgressBarListener,
		OnClickListener {
	private Context mContext;
	private View dialogView;
	private NumberProgressBar mProgressBar;// 1 确认界面 2下载进度条
	private UpdateAsyncTask mAsyncTask;
	private Handler mHandler;
	private String apkDownloadPath = "";
	private static WindowManager wm;
	private static WindowManager.LayoutParams params;
	private LinearLayout confirmLayout, downloadLayout;
	private Button yesButton, noButton, hideButton, cancelButton;
	private TextView title;
	private String downloadUrl = "";
	private String apkName = "";

	public UpdateServiceHelper(Context context) {
		this.mContext = context;
		initUpdateView();
		mHandler = new Handler(mContext.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case 1:
					showProgressView();
					break;
				case 2:
					hideProgressView();
					break;
				case 3:
					mProgressBar.setProgress(msg.arg1);
					break;
				case 4:
					hideProgressView();
					installApk(apkDownloadPath);
					break;
				default:
					break;
				}
			}
		};

	}

	private void initUpdateView() {
		LayoutInflater mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		dialogView = (ViewGroup) mInflater.inflate(R.layout.window_update_view,
				null);
		mProgressBar = (NumberProgressBar) dialogView
				.findViewById(R.id.numberbar);
		title = (TextView) dialogView.findViewById(R.id.update_view_title);
		confirmLayout = (LinearLayout) dialogView
				.findViewById(R.id.update_view_confirm);
		downloadLayout = (LinearLayout) dialogView
				.findViewById(R.id.update_view_download);
		yesButton = (Button) dialogView
				.findViewById(R.id.update_view_btn_confirm);
		noButton = (Button) dialogView
				.findViewById(R.id.update_view_btn_cancel);
		hideButton = (Button) dialogView
				.findViewById(R.id.update_view_btn_hide);
		cancelButton = (Button) dialogView
				.findViewById(R.id.update_view_btn_canceldownload);
		yesButton.setOnClickListener(this);
		noButton.setOnClickListener(this);
		hideButton.setOnClickListener(this);
		cancelButton.setOnClickListener(this);
		wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		params = new WindowManager.LayoutParams();

		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

		params.format = PixelFormat.RGBA_8888;

		params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

		params.width = 566;
		params.height = 238;
		params.x = 0;
		params.y = 0;

	}

	private void showProgressView() {
		if (wm != null && dialogView != null) {
			wm.addView(dialogView, params);
		}
	}

	private void hideProgressView() {
		if (wm != null && dialogView != null)
			wm.removeView(dialogView);
	}



	public void checkServiceUpdate() {
		HttpUtils.doGetAsyn(HttpUtils.UPDATE_URL, 1, new HttpUtils.CallBack() {

			@Override
			public void onRequestComplete(String result, int code) {
				if (result != null && !result.equals("")) {
					parseUpdateInfo(result);
				}
			}
		});

	}

	protected boolean parseUpdateInfo(String result) {
		JSONObject json = null;
		String resultCode = null;
		JSONArray message = null;
		try {
			json = new JSONObject(result);
			if (json.has("message") && json.has("resultCode")) {
				resultCode = json.getString("resultCode");
				if (resultCode.equals("1")) {
					message = json.getJSONArray("message");
					for (int i = 0; i < message.length(); i++) {
						JSONObject jsonObject = message.getJSONObject(i);
						String packageName = jsonObject
								.getString("packageName");
						int versionCode = Integer.valueOf(jsonObject
								.getString("versionCode"));
						 if (packageName.equals("com.autopet.hardware.aidl")
						 && versionCode >AppManager.getVersionCode(mContext,
						 packageName)) {
							downloadUrl = jsonObject.getString("apkUrl");
							apkName = jsonObject.getString("appName") + "_"
									+ jsonObject.getString("version");
							mHandler.sendEmptyMessage(1);
							return true;
						}
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.e(this.getClass().getSimpleName(), e.toString());
		}
		return false;

	}


	class UpdateAsyncTask extends AsyncTask<String, String, Void> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(String... params) {
			int count;
			InputStream input = null;
			OutputStream output = null;

			try {
				URL url = new URL(params[0]);
				URLConnection conexion = url.openConnection();
				conexion.connect();
				int lenghtOfFile = conexion.getContentLength();

				input = new BufferedInputStream(url.openStream());
				apkDownloadPath = FileUtils.DOWNLOAD_PATH + params[1];
				output = new FileOutputStream(apkDownloadPath);
				byte data[] = new byte[1024];
				long total = 0;
				while ((count = input.read(data)) != -1) {
					if (isCancelled()) {
						break;
					}
					total += count;
					publishProgress("" + (int) ((total * 100) / lenghtOfFile));
					output.write(data, 0, count);
				}

			} catch (Exception e) {
				Log.e("error", e.getMessage().toString());
				System.out.println(e.getMessage().toString());
			} finally {
				if (output != null) {
					try {
						output.flush();
						output.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mHandler.sendEmptyMessage(4);
		}

		@Override
		protected void onProgressUpdate(String... values) {
			Message msg = new Message();
			msg.what = 3;
			msg.arg1 = Integer.valueOf(values[0]);
			mHandler.sendMessage(msg);
		}

	}

	@Override
	public void onProgressChange(int current, int max) {
		// TODO Auto-generated method stub

	}

	private void installApk(String path) {
		File apkfile = new File(path);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(i);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.update_view_btn_confirm:
			confirmLayout.setVisibility(View.INVISIBLE);
			downloadLayout.setVisibility(View.VISIBLE);
			title.setText("正在下载中...");
			if (downloadUrl != null && !downloadUrl.equals("")
					&& apkName != null && !apkName.equals("")) {
				mAsyncTask = new UpdateAsyncTask();
				mAsyncTask.execute(downloadUrl, apkName);
			}
			break;
		case R.id.update_view_btn_cancel:
			mHandler.sendEmptyMessage(2);
			break;
		case R.id.update_view_btn_hide:
			dialogView.setVisibility(View.INVISIBLE);
			break;
		case R.id.update_view_btn_canceldownload:
			mAsyncTask.cancel(true);
			mHandler.sendEmptyMessage(2);
			break;
		default:
			break;
		}

	}

}
