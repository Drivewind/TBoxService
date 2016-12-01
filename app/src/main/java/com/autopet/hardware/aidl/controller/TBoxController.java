package com.autopet.hardware.aidl.controller;

import org.json.JSONException;
import org.json.JSONStringer;

import com.autopet.hardware.aidl.tboxinfo.TBoxInfo;

/**
 * @author chenzhongyong 2016-6-3 收音机命令控制类
 */
public abstract class TBoxController {

	public abstract void parseCmdInMcu(byte[] cmd);

	public abstract void outJsonToApp();

	public abstract void outCmdToMcu(byte[] cmd);

	public String TBoxInfoToJson(TBoxInfo tboxInfo, String jsonId) {
		String jString = "";
		try {
			jString = new JSONStringer().object().key(jsonId)
					.value(tboxInfo.ToJson()).endObject().toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jString;
	}

}
