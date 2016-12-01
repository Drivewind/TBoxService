package com.autopet.hardware.aidl.tboxinfo;

import org.json.JSONException;
import org.json.JSONStringer;

public class CarInfoWarning extends TBoxInfo{
	/* OBC硬件故障 0=无故障，1=有故障 */
	private int OBCHardwareFault;

	/* OBC当前状态 1=充电准备，2=正常工作，3=降功率工作，4=故障，5=待机 */
	private int OBCCurrentState;

	/* OBC输入电压过压 0=正常，1=过压 */
	private int OBCInputOverVoltage;

	/* OBC输入电压欠压 0=正常，1=欠压 */
	private int OBCInputUnderVoltage;

	/* OBC输出电压过压 0=正常，1=过压 */
	private int OBCOutputOverVoltage;

	/* OBC输出电压欠压 0=正常，1=欠压 */
	private int OBCOutputUnderVoltage;

	/* OBC输出电流过流 0=正常，1=过流 */
	private int OBCOutputOverCurrent;

	/* OBC过温 0=正常，1=过温 */
	private int OBCOverTemp;

	/* 前碰、侧碰信号 0=未发生，1=发生 */
	private int frontSideCollisionSign;

	/* 后碰信号  0=未发生，1=发生*/
	private int backCollisionSign;

	/* 驾驶员安全带 0=未系，1=已系 */
	private int driverSafetyBelt ;

	
	public CarInfoWarning() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CarInfoWarning(int oBCHardwareFault, int oBCCurrentState,
			int oBCInputOverVoltage, int oBCInputUnderVoltage,
			int oBCOutputOverVoltage, int oBCOutputUnderVoltage,
			int oBCOutputOverCurrent, int oBCOverTemp,
			int frontSideCollisionSign, int backCollisionSign,
			int driverSafetyBelt) {
		super();
		OBCHardwareFault = oBCHardwareFault;
		OBCCurrentState = oBCCurrentState;
		OBCInputOverVoltage = oBCInputOverVoltage;
		OBCInputUnderVoltage = oBCInputUnderVoltage;
		OBCOutputOverVoltage = oBCOutputOverVoltage;
		OBCOutputUnderVoltage = oBCOutputUnderVoltage;
		OBCOutputOverCurrent = oBCOutputOverCurrent;
		OBCOverTemp = oBCOverTemp;
		this.frontSideCollisionSign = frontSideCollisionSign;
		this.backCollisionSign = backCollisionSign;
		this.driverSafetyBelt = driverSafetyBelt;
	}

	public int getOBCHardwareFault() {
		return OBCHardwareFault;
	}

	public void setOBCHardwareFault(int oBCHardwareFault) {
		OBCHardwareFault = oBCHardwareFault;
	}

	public int getOBCCurrentState() {
		return OBCCurrentState;
	}

	public void setOBCCurrentState(int oBCCurrentState) {
		OBCCurrentState = oBCCurrentState;
	}

	public int getOBCInputOverVoltage() {
		return OBCInputOverVoltage;
	}

	public void setOBCInputOverVoltage(int oBCInputOverVoltage) {
		OBCInputOverVoltage = oBCInputOverVoltage;
	}

	public int getOBCInputUnderVoltage() {
		return OBCInputUnderVoltage;
	}

	public void setOBCInputUnderVoltage(int oBCInputUnderVoltage) {
		OBCInputUnderVoltage = oBCInputUnderVoltage;
	}

	public int getOBCOutputOverVoltage() {
		return OBCOutputOverVoltage;
	}

	public void setOBCOutputOverVoltage(int oBCOutputOverVoltage) {
		OBCOutputOverVoltage = oBCOutputOverVoltage;
	}

	public int getOBCOutputUnderVoltage() {
		return OBCOutputUnderVoltage;
	}

	public void setOBCOutputUnderVoltage(int oBCOutputUnderVoltage) {
		OBCOutputUnderVoltage = oBCOutputUnderVoltage;
	}

	public int getOBCOutputOverCurrent() {
		return OBCOutputOverCurrent;
	}

	public void setOBCOutputOverCurrent(int oBCOutputOverCurrent) {
		OBCOutputOverCurrent = oBCOutputOverCurrent;
	}

	public int getOBCOverTemp() {
		return OBCOverTemp;
	}

	public void setOBCOverTemp(int oBCOverTemp) {
		OBCOverTemp = oBCOverTemp;
	}

	public int getFrontSideCollisionSign() {
		return frontSideCollisionSign;
	}

	public void setFrontSideCollisionSign(int frontSideCollisionSign) {
		this.frontSideCollisionSign = frontSideCollisionSign;
	}

	public int getBackCollisionSign() {
		return backCollisionSign;
	}

	public void setBackCollisionSign(int backCollisionSign) {
		this.backCollisionSign = backCollisionSign;
	}

	public int getDriverSafetyBelt() {
		return driverSafetyBelt;
	}

	public void setDriverSafetyBelt(int driverSafetyBelt) {
		this.driverSafetyBelt = driverSafetyBelt;
	}

	@Override
	public String toString() {
		String s = "OBC硬件故障:"+OBCHardwareFault+"--"+
								"OBC当前状态"+OBCCurrentState+"--"+
								"OBC输入电压过压"+OBCInputOverVoltage+"--"+
								"OBC输入电压欠压"+OBCInputUnderVoltage+"--"+
								"OBC输出电压过压"+OBCOutputOverVoltage+"--"+
								"OBC输出电压欠压"+OBCOutputUnderVoltage+"--"+
								"OBC输出电流过流"+OBCOutputOverCurrent+"--"+
								"OBC过温"+OBCOverTemp+"--"+
								"前碰、侧碰信号"+frontSideCollisionSign+"--"+
								"后碰信号"+backCollisionSign+"--"+
								"驾驶员安全带"+driverSafetyBelt+"--";
		return s;
	}

	@Override
	public String ToJson() {
		String ParaString = "";
		try {
			ParaString = new JSONStringer()
					.object()
					.key("OBCHardwareFault")
					.value(String.valueOf(OBCHardwareFault))
					.key("OBCCurrentState")
					.value(String.valueOf(OBCCurrentState))
					.key("OBCInputOverVoltage")
					.value(String.valueOf(OBCInputOverVoltage))
					.key("OBCInputUnderVoltage")
					.value(String.valueOf(OBCInputUnderVoltage))
					.key("OBCOutputOverVoltage")
					.value(String.valueOf(OBCOutputOverVoltage))
					.key("OBCOutputUnderVoltage")
					.value(String.valueOf(OBCOutputUnderVoltage))
					.key("OBCOutputOverCurrent")
					.value(String.valueOf(OBCOutputOverCurrent))
					.key("OBCOverTemp")
					.value(String.valueOf(OBCOverTemp))
					.key("frontSideCollisionSign")
					.value(String.valueOf(frontSideCollisionSign))
					.key("backCollisionSign")
					.value(String.valueOf(backCollisionSign))
					.key("driverSafetyBelt")
					.value(String.valueOf(driverSafetyBelt))
					.endObject().toString();
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ParaString;
		}

	
}
