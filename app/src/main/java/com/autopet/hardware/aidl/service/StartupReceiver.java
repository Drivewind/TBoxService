package com.autopet.hardware.aidl.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class StartupReceiver extends BroadcastReceiver {
	/* 瑕佹帴鏀剁殑intent婧�*/
	static final String ACTIONBoot = "android.intent.action.BOOT_COMPLETED";

	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTIONBoot)) {
			context.startService(new Intent(context, E3HWService.class));		
			Log.e("E3ProxyService", "Broadcast " + ACTIONBoot + " Received.");
		}
	}
}