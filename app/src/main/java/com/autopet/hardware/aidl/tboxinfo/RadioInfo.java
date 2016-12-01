package com.autopet.hardware.aidl.tboxinfo;

import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONException;
import org.json.JSONStringer;

public class RadioInfo extends TBoxInfo {
	private int band = 1;//0 am,1 fm
	private int fmFrq = 875;//100khz
	private int amFrq = 531;//khz
	private int vol = 15;
	private int phoneVol = 20;
	private int mute;
	private ArrayList<Integer> radioFmStations;//treeset is should be used,because set has no repeat data and treeset is auto sorted itself.
	private ArrayList<Integer> radioAmStations;

	public RadioInfo() {
		super();
		initRadioInfo();
	}

	
	


	public int getBand() {
		return band;
	}


	public void setBand(int band) {
		this.band = band;
	}


	public int getFmFrq() {
		return fmFrq;
	}


	public void setFmFrq(int fmFrq) {
		this.fmFrq = fmFrq;
	}


	public int getAmFrq() {
		return amFrq;
	}


	public void setAmFrq(int amFrq) {
		this.amFrq = amFrq;
	}


	public int getVol() {
		return vol;
	}


	public void setVol(int vol) {		
		this.vol = vol;
	}


	public int getPhoneVol() {
		return phoneVol;
	}


	public void setPhoneVol(int phoneVol) {
		this.phoneVol = phoneVol;
	}


	public ArrayList<Integer> getRadioFmStations() {
		return radioFmStations;
	}


	public void setRadioFmStations(ArrayList<Integer> radioFmStations) {
		this.radioFmStations = radioFmStations;
	}


	public ArrayList<Integer> getRadioAmStations() {
		return radioAmStations;
	}


	public void setRadioAmStations(ArrayList<Integer> radioAmStations) {
		this.radioAmStations = radioAmStations;
	}


	private void initRadioInfo() {
		radioFmStations = new ArrayList<Integer>();
		Collections.addAll(radioFmStations, 923,974,991,1012,1029,1047,1059);
		
		radioAmStations = new ArrayList<Integer>();
		radioAmStations.add(1287);
	}

	@Override
	public String ToJson() {
		String paraString = "";
		try {
			paraString = new JSONStringer().object().key("FMFRQ")
					.value(Integer.toString(fmFrq)).key("AMFRQ")
					.value(Integer.toString(amFrq)).key("BAND")
					.value(Integer.toString(band)).key("VOL")
					.value(Integer.toString(vol)).key("PHONEVOL")
					.value(Integer.toString(phoneVol)).key("MAXVOL")
					.value("31").key("MUTE")
					.value(String.valueOf(mute)).endObject().toString();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return paraString;
	}

	public int getMute() {
		return mute;
	}





	public void setMute(int mute) {
		this.mute = mute;
	}





	public String StalionListToJson(String keyName,ArrayList<Integer> stationList) {
		String paraString = "";
		String stationsString = "";
		JSONStringer jStringer;
		try {
			jStringer = new JSONStringer().object();
			for (int i = 0; i < stationList.size(); i++) {
				jStringer = jStringer.key("station" + i).value(
						Integer.toString(stationList.get(i)));

			}
			stationsString = jStringer.endObject().toString();
			paraString = new JSONStringer().object().key(keyName)
					.value(stationsString).endObject().toString();
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return paraString;
	}
	public String EnforceStopAutoSearchToJson(){
		String paraString = "";
		String stopAutoSearchString = "";
		try {
			paraString =new JSONStringer().object().key("ENFORCESTOPAUTOSEARCH")
					.value(stopAutoSearchString).endObject().toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return paraString;
	}

}
