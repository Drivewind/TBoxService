package com.autopet.hardware.aidl.tboxinfo;

import org.json.JSONException;
import org.json.JSONStringer;

public class CarInfoOnce extends TBoxInfo{
	/* 单体蓄电池总数 0～63 */
	private int singleBatteryTotalAmount;

	/* 动力蓄电池包总数 0～1 */
	private int powerBatteryPackTotalAmount;

	/* 电池类型代码 0～15 */
	private int batteryTypeCode;

	/* 电池生产商代码 */
	private int batteryManufacturerCode;

	/* 电池生产日期代码 2010/1/1～2137/12/31 */
	private String batteryProductionDateTime;

	/* 电池包额定能量 10～16.3kwh */
	private float batteryPackRatedEnerge;

	
	public CarInfoOnce() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CarInfoOnce(int singleBatteryTotalAmount,
			int powerBatteryPackTotalAmount, int batteryTypeCode,
			int batteryManufacturerCode, String batteryProductionDateTime,
			float batteryPackRatedEnerge) {
		super();
		this.singleBatteryTotalAmount = singleBatteryTotalAmount;
		this.powerBatteryPackTotalAmount = powerBatteryPackTotalAmount;
		this.batteryTypeCode = batteryTypeCode;
		this.batteryManufacturerCode = batteryManufacturerCode;
		this.batteryProductionDateTime = batteryProductionDateTime;
		this.batteryPackRatedEnerge = batteryPackRatedEnerge;
	}

	public int getSingleBatteryTotalAmount() {
		return singleBatteryTotalAmount;
	}

	public void setSingleBatteryTotalAmount(int singleBatteryTotalAmount) {
		this.singleBatteryTotalAmount = singleBatteryTotalAmount;
	}

	public int getPowerBatteryPackTotalAmount() {
		return powerBatteryPackTotalAmount;
	}

	public void setPowerBatteryPackTotalAmount(int powerBatteryPackTotalAmount) {
		this.powerBatteryPackTotalAmount = powerBatteryPackTotalAmount;
	}

	public int getBatteryTypeCode() {
		return batteryTypeCode;
	}

	public void setBatteryTypeCode(int batteryTypeCode) {
		this.batteryTypeCode = batteryTypeCode;
	}

	public int getBatteryManufacturerCode() {
		return batteryManufacturerCode;
	}

	public void setBatteryManufacturerCode(int batteryManufacturerCode) {
		this.batteryManufacturerCode = batteryManufacturerCode;
	}

	public String getBatteryProductionDateTime() {
		return batteryProductionDateTime;
	}

	public void setBatteryProductionDateTime(String batteryProductionDateTime) {
		this.batteryProductionDateTime = batteryProductionDateTime;
	}

	public float getBatteryPackRatedEnerge() {
		return batteryPackRatedEnerge;
	}

	public void setBatteryPackRatedEnerge(float batteryPackRatedEnerge) {
		this.batteryPackRatedEnerge = batteryPackRatedEnerge;
	}

	@Override
	public String toString() {
		String s = "单体蓄电池总数:"+singleBatteryTotalAmount+"--"+
								"动力蓄电池包总数"+powerBatteryPackTotalAmount+"--"+
								"电池类型代码"+batteryTypeCode+"--"+
								"电池生产商代码"+batteryManufacturerCode+"--"+
								"电池生产日期代码"+batteryProductionDateTime+"--"+
								"电池包额定能量"+batteryPackRatedEnerge+"--";
		return s;
	}

	@Override
	public String ToJson() {
		String ParaString = "";
		try {
			ParaString = new JSONStringer()
					.object()
					.key("singleBatteryTotalAmount")
					.value(String.valueOf(singleBatteryTotalAmount))
					.key("powerBatteryPackTotalAmount")
					.value(String.valueOf(powerBatteryPackTotalAmount))
					.key("batteryTypeCode")
					.value(String.valueOf(batteryTypeCode))
					.key("batteryManufacturerCode")
					.value(String.valueOf(batteryManufacturerCode))
					.key("batteryProductionDateTime")
					.value(batteryProductionDateTime)
					.key("batteryPackRatedEnerge")
					.value(String.valueOf(batteryPackRatedEnerge)).endObject()
					.toString();
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ParaString;
	}
}
