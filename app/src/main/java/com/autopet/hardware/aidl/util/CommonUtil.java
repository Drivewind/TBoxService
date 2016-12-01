package com.autopet.hardware.aidl.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CommonUtil {
	public long dateToLong(String in) {
		SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
		Date date;
		try {
			date = format.parse(in);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal.getTimeInMillis();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;

	}

	public String getCurDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(new Date());
	}

	public String getCurTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(new Date());
	}
	public String getCurTime2() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		return dateFormat.format(new Date());
	}

	// 将data字节型数据转换为0~255 (0xFF 即BYTE)。
	public int getUnsignedByte(byte data) {
		return data & 0x0FF;
	}

	public int JSHash(byte[] bt, int len) {
		int hash = 1315423911;
		int temp = 0;
		int i = 0;
		for (i = 0; i < len; i++) {
			temp = bt[i] & 0x0ff;
			hash ^= ((hash << 5) + temp + (hash >> 2));
		}
		return hash;

	}

}
