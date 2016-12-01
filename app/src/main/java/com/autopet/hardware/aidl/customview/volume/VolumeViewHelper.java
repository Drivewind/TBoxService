package com.autopet.hardware.aidl.customview.volume;

import java.util.Date;

import com.autopet.hardware.aidl.customview.volume.VolumeView.OnVolumeChangeListener;
import com.autopet.hardware.aidl.customview.volume.VolumeView.onMuteChangeListener;

import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;

public class VolumeViewHelper {
	private Context mContext;
	private VolumeView volumeView;
	private boolean isShow;
	private final static int showTime = 3000;
	private static WindowManager wm;
	private static WindowManager.LayoutParams params;

	public VolumeViewHelper(Context context) {
		this.mContext = context;
		volumeView = new VolumeView(mContext);
		initWindowManager(context);
		mHandler = new Handler(mContext.getMainLooper()){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch(msg.what){
				case 0x3051:
					setViewVolume(msg.arg1);
					break;
				case 0x3052:
					setMute(msg.arg1);
					break;
					default:
						break;
				}				
			}
		};
	}
	

	public void setVolume(int volume) {
		Message msg = new Message();
		msg.what=0x3051;
		msg.arg1=volume;
		mHandler.sendMessage(msg);
	}
	
	public void setMute(boolean mute){
		Message msg = new Message();
		msg.what=0x3052;
		if(mute){
			msg.arg1=1;
		}else{
			msg.arg1=0;
		}
		mHandler.sendMessage(msg);
	}
	private void setViewVolume(int volume){
		if (isShow) {
			volumeView.setVolumeProgress(volume);
		} else {
			showVolumeView(volume);
		}
		mHandler.removeCallbacks(mRunnable);
		mHandler.postDelayed(mRunnable, showTime);
	}
	private void setMute(int flag){
		boolean mute = false;
		if(flag==1){
			mute = true;
		}
		if(isShow){
			setVolumeMute(mute);
		}else{
			showVolumeView(mute);
		}
		mHandler.removeCallbacks(mRunnable);
		mHandler.postDelayed(mRunnable, showTime);
	}
	
	public void setOnVolumeListener(OnVolumeChangeListener listener) {
		volumeView.setOnVolumeChangeListener(listener);
	}
	public void setOnMuteChangeListener(onMuteChangeListener listener){
		volumeView.setOnMuteChangeListener(listener);
	}

	private void showVolumeView(int volume) {
		wm.addView(volumeView, params);
		volumeView.setVolumeProgress(volume);
		isShow = true;

	}
	private void showVolumeView(boolean mute){
		wm.addView(volumeView, params);
		setVolumeMute(mute);
		isShow = true;
	}
	
	private void setVolumeMute(boolean mute){
		if(mute){
			volumeView.setVolumeMute();
		}else{
			volumeView.setVolumeUnMute();
		}
	}

	private void hideVolumeView() {
		wm.removeView(volumeView);
		isShow = false;
	}
	private void initWindowManager(Context context){
		wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		params = new WindowManager.LayoutParams();

		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

		params.format = PixelFormat.RGBA_8888;

		params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

		params.width = 300;
		params.height = 200;
		params.x = 0;
		params.y = 0;
	}

	private Handler mHandler ;
	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			hideVolumeView();
		}
	};

	
}
