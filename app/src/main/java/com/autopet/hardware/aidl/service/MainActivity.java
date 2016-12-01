package com.autopet.hardware.aidl.service;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.startService(new Intent(this, E3HWService.class));
		finish();
	}
}
