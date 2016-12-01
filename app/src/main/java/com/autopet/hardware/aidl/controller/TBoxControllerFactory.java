package com.autopet.hardware.aidl.controller;

import android.content.Context;
import android.os.Handler;

public class TBoxControllerFactory {
	private static TBoxControllerFactory factory;
	private RadioController radioController ;
	private CarInfoController carInfoController;
	private GpsController gpsController;
	private Handler mHandler;
	private Context context;

	private TBoxControllerFactory() {

	}

	public static TBoxControllerFactory getControllerFactory() {
		if (factory == null) {
			synchronized (TBoxControllerFactory.class) {
				if (factory == null) {
					factory = new TBoxControllerFactory();
				}
			}
		}
		return factory;
	}

	public TBoxController getController(String className) {
		switch (className) {
		case "RadioController":
			if(radioController ==null){
				radioController = new RadioController(mHandler,context);
			}
			return radioController;
			
		case "CarInfoController":
			if(carInfoController ==null){
				carInfoController = new CarInfoController(mHandler);
			}
			return carInfoController;
			
		case "GpsController":
			if(gpsController==null){
				gpsController = new GpsController(mHandler);
			}
			return gpsController;
		default:
			break;
		}
		return null;
	}
	public void setObjects(Handler handler,Context context){
		mHandler = handler;
		this.context = context;
	}
}
