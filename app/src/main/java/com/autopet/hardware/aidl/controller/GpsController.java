package com.autopet.hardware.aidl.controller;

import android.os.Handler;
import android.os.Message;

import com.autopet.hardware.aidl.tboxinfo.GPSInfo;
import com.autopet.hardware.aidl.util.CommonUtil;
import com.autopet.hardware.aidl.util.FileUtils;

public class GpsController extends TBoxController {
	private GPSInfo gpsInfo;
	private Handler mHandler;
	private CommonUtil mCommonUtil;

	public GpsController(Handler handler) {
		this.mHandler = handler;
		mCommonUtil = new CommonUtil();
	}

	@Override
	public void parseCmdInMcu(byte[] mcustatus) {
		if (mcustatus[0] == 0x66) {
			if (gpsInfo == null) {
				gpsInfo = new GPSInfo();
			}
			if((mcustatus[10]&0x01)==0){
				return;
			}
			// 纬度
			double latitude = 0;
			latitude += (mcustatus[1] & 0x0f0) / 16 * 10;
			latitude += (mcustatus[1] & 0x0f) / 1 * 1;
			latitude += (mcustatus[2] & 0x0f0) / 16 * 10 / 60.0;
			latitude += (mcustatus[2] & 0x0f) / 1 * 1 / 60.0;
			latitude += (mcustatus[3] & 0x0f0) / 16 * 0.1 / 60.0;
			latitude += (mcustatus[3] & 0x0f) / 1 * 0.01 / 60.0;
			latitude += (mcustatus[4] & 0x0f0) / 16 * 0.001 / 60.0;
			latitude += (mcustatus[4] & 0x0f) / 1 * 0.0001 / 60.0;
			int latiDirection = ((mcustatus[10] & 0x02) >>> 1) == 0 ? 1 : -1;
			latitude = latitude * latiDirection;
			gpsInfo.setLatitude(latitude);
			// 经度
			double longitude = 0;
			longitude += (mcustatus[5] & 0x0f0) / 16 * 100;
			longitude += (mcustatus[5] & 0x0f) / 1 * 10;
			longitude += (mcustatus[6] & 0x0f0) / 16 * 1;

			longitude += (mcustatus[6] & 0x0f) / 1 * 10 / 60.0;
			longitude += (mcustatus[7] & 0x0f0) / 16 * 1 / 60.0;
			longitude += (mcustatus[7] & 0x0f) / 1 * 0.1 / 60.0;
			longitude += (mcustatus[8] & 0x0f0) / 16 * 0.01 / 60.0;
			longitude += (mcustatus[8] & 0x0f) / 1 * 0.001 / 60.0;
			longitude += (mcustatus[9] & 0x0f0) / 16 * 0.0001 / 60.0;
			int longDirection = ((mcustatus[10] & 0x04) >>> 2) == 0 ? 1 : -1;
			longitude = longitude * longDirection;
			gpsInfo.setLongitude(longitude);

			// 日期时间
			StringBuilder sb = new StringBuilder();
			int UTCDate = 0;
			UTCDate += (mcustatus[11] & 0x0f0) / 16 * 100000;
			UTCDate += (mcustatus[11] & 0x0f) / 1 * 10000;// 日
			UTCDate += (mcustatus[12] & 0x0f0) / 16 * 1000;
			UTCDate += (mcustatus[12] & 0x0f) / 1 * 100;// 月
			UTCDate += (mcustatus[13] & 0x0f0) / 16 * 10;
			UTCDate += (mcustatus[13] & 0x0f) / 1 * 1;// 年
			int utcyear = UTCDate % 100 + 2000;
			int utcmonth = UTCDate % 10000 / 100;
			int utcday = UTCDate / 10000;
			sb.append(utcyear).append("/");
			if (utcmonth < 10) {
				sb.append("0");
			}
			sb.append(utcmonth).append("/");
			if (utcday < 10) {
				sb.append("0");
			}
			sb.append(utcday).append(" ");

			int UTCTime = 0;
			UTCTime += (mcustatus[14] & 0x0f0) / 16 * 100000;
			UTCTime += (mcustatus[14] & 0x0f) / 1 * 10000;// 小时
			UTCTime += (mcustatus[15] & 0x0f0) / 16 * 1000;
			UTCTime += (mcustatus[15] & 0x0f) / 1 * 100;// 分钟
			UTCTime += (mcustatus[16] & 0x0f0) / 16 * 10;
			UTCTime += (mcustatus[16] & 0x0f) / 1 * 1;// 秒数
			int utchour = UTCTime / 10000;
			int utcminute = UTCTime % 10000 / 100;
			int utcsecond = UTCTime % 100;
			if (utchour < 10) {
				sb.append("0");
			}
			sb.append(utchour).append(":");
			if (utcminute < 10) {
				sb.append("0");
			}
			sb.append(utcminute).append(":");
			if (utcsecond < 10) {
				sb.append("0");
			}
			sb.append(utcsecond);

			long milliseconds = 0;// 总毫秒数 UTC Time in milliseconds since January
									// 1, 1970
			milliseconds += (mcustatus[17] & 0x0f0) / 16 * 100;
			milliseconds += (mcustatus[17] & 0x0f) / 1 * 10;
			milliseconds += (mcustatus[18] & 0x0f0) / 16 * 1;// 毫秒数

			milliseconds += mCommonUtil.dateToLong(sb.toString());

			gpsInfo.setUTCTime(milliseconds);

			// 速度
			float speed = 0;
			speed += (mcustatus[19] & 0x0f0) / 16 * 100;
			speed += (mcustatus[19] & 0x0f) / 1 * 10;
			speed += (mcustatus[20] & 0x0f0) / 16 * 1;
			speed += (mcustatus[20] & 0x0f) / 1 * 0.1;
			speed = speed * 1850 / 3600;// 1海里=1.85公里=1850米 1小时=3600秒
			gpsInfo.setSpeed(speed);
			float bearing = 0;
			bearing += (mcustatus[21] & 0x0f0) / 16 * 100;
			bearing += (mcustatus[21] & 0x0f) / 1 * 10;
			bearing += (mcustatus[22] & 0x0f0) / 16 * 1;
			bearing += (mcustatus[22] & 0x0f) / 1 * 0.1;
			gpsInfo.setBearing(bearing);

			float accuracy = 0;
			accuracy += (mcustatus[23] & 0x0f) / 1 * 10;
			accuracy += (mcustatus[24] & 0x0f0) / 16 * 1;
			accuracy += (mcustatus[24] & 0x0f) / 1 * 0.1;
			gpsInfo.setAccuracy(accuracy);

			double altitude = 0;
			altitude += (mcustatus[25] & 0x0f) / 1 * 1000;
			altitude += (mcustatus[26] & 0x0f0) / 16 * 100;
			altitude += (mcustatus[26] & 0x0f) / 1 * 10;
			altitude += (mcustatus[27] & 0x0f0) / 16 * 1;
			altitude += (mcustatus[27] & 0x0f) / 1 * 0.1;
			int sign = (mcustatus[25] & 0x0f0) >>> 4;
			if (sign == 15) {
				altitude = -1 * altitude;
			} else {
				altitude += sign * 10000;
			}
			gpsInfo.setAltitude(altitude);
			outJsonToApp();
//			StringBuffer gpsStatus = new StringBuffer();
//			for (int i = 1; i < mcustatus.length; i++) {
//				gpsStatus.append(
//						Integer.toHexString(mCommonUtil
//								.getUnsignedByte(mcustatus[i]))).append("-");
//
//			}
//			FileUtils.saveToFile(
//					FileUtils.GW_LOG_SAVE_GPS + "_" + mCommonUtil.getCurDate()
//							+ ".txt", mCommonUtil.getCurTime() + " "
//							+ gpsStatus);
//			FileUtils.saveToFile(
//					FileUtils.GW_LOG_SAVE_GPS + "_" + mCommonUtil.getCurDate()
//							+ ".txt",
//					mCommonUtil.getCurTime() + " " + gpsInfo.toString());
		}

	}

	@Override
	public void outJsonToApp() {
		Message message = new Message();
		message.getData().putString("TBOXINFO",
				TBoxInfoToJson(gpsInfo, "GPSINFO_INSTANT"));
		message.what = 1;
		mHandler.sendMessage(message);

	}

	@Override
	public void outCmdToMcu(byte[] cmd) {
		// TODO Auto-generated method stub

	}

}
