package com.autopet.hardware.aidl.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.content.Context;
import android.content.SharedPreferences;

public class DataUtil {
	private Context mContext;

	public DataUtil(Context context) {
		mContext = context;
	}

	public void saveRadioInfoPref(int fmFreq, int amFreq, int band, int vol,
			int phoneVol) {
		SharedPreferences radioInfo = mContext.getSharedPreferences(
				"radioInfo", 0);
		SharedPreferences.Editor editor = radioInfo.edit();
		editor.putInt("vol", vol);
		editor.putInt("fmfrq", fmFreq);
		editor.putInt("amfrq", amFreq);
		editor.putInt("band", band);
		editor.putInt("phonevol", phoneVol);
		editor.commit();
	}

	public int[] readRadioInfoPref() {
		SharedPreferences radioInfo = mContext.getSharedPreferences(
				"radioInfo", 0);
		int[] data = new int[5];
		data[0] = radioInfo.getInt("band", 1);
		data[1] = radioInfo.getInt("fmfrq", 875);
		data[2] = radioInfo.getInt("amfrq", 531);
		data[3] = radioInfo.getInt("vol", 15);
		data[4] = radioInfo.getInt("phonevol", 20);
		return data;
	}

	public void saveRadioFmFrqPref(ArrayList<Integer> frqlist) {
		SharedPreferences radioFrq = mContext.getSharedPreferences(
				"radioFmFrq", 0);
		SharedPreferences.Editor editor = radioFrq.edit();
		for (int i = 0; i < frqlist.size(); i++) {
			editor.putInt("" + frqlist.get(i), frqlist.get(i));
		}
		editor.commit();
	}

	public ArrayList<Integer> readRadioFmFrqPref() {
		SharedPreferences radioFrq = mContext.getSharedPreferences(
				"radioFmFrq", 0);
		ArrayList<Integer> frqlist = new ArrayList<Integer>();
		HashMap<String, Integer> map = (HashMap<String, Integer>) radioFrq
				.getAll();
//		for(Entry<String, Integer> i : map.entrySet()){
//			frqlist.add(i.getValue());
//		}
		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			frqlist.add(map.get(iterator.next()));
		}
		Collections.sort(frqlist);
		return frqlist;
	}

	public void saveRadioAmFrqPref(ArrayList<Integer> frqlist) {
		SharedPreferences radioFrq = mContext.getSharedPreferences(
				"radioAmFrq", 0);
		SharedPreferences.Editor editor = radioFrq.edit();
		for (int i = 0; i < frqlist.size(); i++) {
			editor.putInt("" + frqlist.get(i), frqlist.get(i));
		}
		editor.commit();
	}

	public ArrayList<Integer> readRadioAmFrqPref() {
		SharedPreferences radioFrq = mContext.getSharedPreferences(
				"radioAmFrq", 0);
		ArrayList<Integer> frqlist = new ArrayList<Integer>();
		HashMap<String, Integer> map = (HashMap<String, Integer>) radioFrq
				.getAll();
		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			frqlist.add(map.get(iterator.next()));
		}
		Collections.sort(frqlist);
		return frqlist;

	}
}
