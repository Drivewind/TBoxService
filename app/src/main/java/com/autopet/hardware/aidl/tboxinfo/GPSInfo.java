package com.autopet.hardware.aidl.tboxinfo;

import org.json.JSONException;
import org.json.JSONStringer;

public class GPSInfo extends TBoxInfo{
	/* 纬度 */
	private double latitude;
	/* 经度 */
	private double longitude;
	/* UTC Time in milliseconds since January 1, 1970. */
	private long UTCTime;
	/* Speed 单位m/s */
	private float speed; 
	/* 方位 */
	private float bearing;
	/* 高度  m*/
	private double altitude;
	/* 精确度 m*/
	private float accuracy;
	
	
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public long getUTCTime() {
		return UTCTime;
	}
	public void setUTCTime(long uTCTime) {
		UTCTime = uTCTime;
	}
	public float getSpeed() {
		return speed;
	}
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	public float getBearing() {
		return bearing;
	}
	public void setBearing(float bearing) {
		this.bearing = bearing;
	}
	public double getAltitude() {
		return altitude;
	}
	public void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	public float getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}
	public GPSInfo(double latitude, double longitude, long uTCTime,
			float speed, float bearing, double altitude, float accuracy) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		UTCTime = uTCTime;
		this.speed = speed;
		this.bearing = bearing;
		this.altitude = altitude;
		this.accuracy = accuracy;
	}
	public GPSInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toString() {
		return "latitude:"+latitude+" longitude:"+longitude+" UTCTime:"+UTCTime+" speed:"+speed+" bearing:"+bearing+" altitude:"+altitude+" accuracy"+accuracy;
	}
	@Override
	public String ToJson() {
		String ParaString = "";
		try {
			ParaString = new JSONStringer().object().key("latitude")
					.value(String.valueOf(latitude)).key("longitude")
					.value(String.valueOf(longitude)).key("UTCTime")
					.value(String.valueOf(UTCTime)).key("speed")
					.value(String.valueOf(speed)).key("altitude")
					.value(String.valueOf(altitude)).key("accuracy")
					.value(String.valueOf(accuracy)).key("bearing")
					.value(String.valueOf(bearing)).endObject()
					.toString();
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ParaString;
	}
	

}
