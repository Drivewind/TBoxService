package com.autopet.hardware.aidl.tboxinfo;

import org.json.JSONException;
import org.json.JSONStringer;

public class CarInfoNoneInstant extends TBoxInfo{
	/* 电池包剩余电量 0～25.5 kwh */
	private float batteryPackRemainEnergy;

	/* 电池包外侧总电压 0～500V */
	private float batteryPackAbroadTotalVoltage;

	/* 电池包内侧总电压 0～500V */
	private float batteryPackWithinTotalVoltage;

	/* 电池包总电流 -1000～1000A */
	private float batteryPackTotalCurrent;

	/* 充放电次数 0～65535 */
	private int chargeDischargeTime;
	
	/* 电池包最高单体电压 0～65534mV */
	private int batteryPackMostSingleVoltage;

	/* 电池包最高单体电压位置 1～255 */
	private int batteryPackMostSingleVoltagePosition;

	/* 电池包最低单体电压 0～65534mV */
	private int batteryPackLeastSingleVoltage;

	/* 电池包最低单体电压位置 1～255 */
	private int batteryPackLeastSingleVoltagePosition;

	/* 电池包充电剩余时间 0～1023min */
	private int batteryPackChargeRemainTime;

	/* OBC输入电压 0～265V */
	private int OBCInputVoltage;
	
	/* OBC输出电压 0～500V */
	private float OBCOutputVoltage;

	/* OBC输出电流 0～500A */
	private float OBCOutputCurrent;

	/* 电池包16个探针温度 -50～200℃ */
	private int batteryPackProbeTemp1;
	private int batteryPackProbeTemp2;
	private int batteryPackProbeTemp3;
	private int batteryPackProbeTemp4;
	private int batteryPackProbeTemp5;
	private int batteryPackProbeTemp6;
	private int batteryPackProbeTemp7;
	private int batteryPackProbeTemp8;
	private int batteryPackProbeTemp9;
	private int batteryPackProbeTemp10;
	private int batteryPackProbeTemp11;
	private int batteryPackProbeTemp12;
	private int batteryPackProbeTemp13;
	private int batteryPackProbeTemp14;
	private int batteryPackProbeTemp15;
	private int batteryPackProbeTemp16;
	
	
	public CarInfoNoneInstant() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CarInfoNoneInstant(float batteryPackRemainEnergy,
			float batteryPackAbroadTotalVoltage,
			float batteryPackWithinTotalVoltage, float batteryPackTotalCurrent,
			int chargeDischargeTime, int batteryPackMostSingleVoltage,
			int batteryPackMostSingleVoltagePosition,
			int batteryPackLeastSingleVoltage,
			int batteryPackLeastSingleVoltagePosition,
			int batteryPackChargeRemainTime, int oBCInputVoltage,
			float oBCOutputVoltage, float oBCOutputCurrent,
			int batteryPackProbeTemp1, int batteryPackProbeTemp2,
			int batteryPackProbeTemp3, int batteryPackProbeTemp4,
			int batteryPackProbeTemp5, int batteryPackProbeTemp6,
			int batteryPackProbeTemp7, int batteryPackProbeTemp8,
			int batteryPackProbeTemp9, int batteryPackProbeTemp10,
			int batteryPackProbeTemp11, int batteryPackProbeTemp12,
			int batteryPackProbeTemp13, int batteryPackProbeTemp14,
			int batteryPackProbeTemp15, int batteryPackProbeTemp16) {
		super();
		this.batteryPackRemainEnergy = batteryPackRemainEnergy;
		this.batteryPackAbroadTotalVoltage = batteryPackAbroadTotalVoltage;
		this.batteryPackWithinTotalVoltage = batteryPackWithinTotalVoltage;
		this.batteryPackTotalCurrent = batteryPackTotalCurrent;
		this.chargeDischargeTime = chargeDischargeTime;
		this.batteryPackMostSingleVoltage = batteryPackMostSingleVoltage;
		this.batteryPackMostSingleVoltagePosition = batteryPackMostSingleVoltagePosition;
		this.batteryPackLeastSingleVoltage = batteryPackLeastSingleVoltage;
		this.batteryPackLeastSingleVoltagePosition = batteryPackLeastSingleVoltagePosition;
		this.batteryPackChargeRemainTime = batteryPackChargeRemainTime;
		OBCInputVoltage = oBCInputVoltage;
		OBCOutputVoltage = oBCOutputVoltage;
		OBCOutputCurrent = oBCOutputCurrent;
		this.batteryPackProbeTemp1 = batteryPackProbeTemp1;
		this.batteryPackProbeTemp2 = batteryPackProbeTemp2;
		this.batteryPackProbeTemp3 = batteryPackProbeTemp3;
		this.batteryPackProbeTemp4 = batteryPackProbeTemp4;
		this.batteryPackProbeTemp5 = batteryPackProbeTemp5;
		this.batteryPackProbeTemp6 = batteryPackProbeTemp6;
		this.batteryPackProbeTemp7 = batteryPackProbeTemp7;
		this.batteryPackProbeTemp8 = batteryPackProbeTemp8;
		this.batteryPackProbeTemp9 = batteryPackProbeTemp9;
		this.batteryPackProbeTemp10 = batteryPackProbeTemp10;
		this.batteryPackProbeTemp11 = batteryPackProbeTemp11;
		this.batteryPackProbeTemp12 = batteryPackProbeTemp12;
		this.batteryPackProbeTemp13 = batteryPackProbeTemp13;
		this.batteryPackProbeTemp14 = batteryPackProbeTemp14;
		this.batteryPackProbeTemp15 = batteryPackProbeTemp15;
		this.batteryPackProbeTemp16 = batteryPackProbeTemp16;
	}

	public float getBatteryPackRemainEnergy() {
		return batteryPackRemainEnergy;
	}

	public void setBatteryPackRemainEnergy(float batteryPackRemainEnergy) {
		this.batteryPackRemainEnergy = batteryPackRemainEnergy;
	}

	public float getBatteryPackAbroadTotalVoltage() {
		return batteryPackAbroadTotalVoltage;
	}

	public void setBatteryPackAbroadTotalVoltage(float batteryPackAbroadTotalVoltage) {
		this.batteryPackAbroadTotalVoltage = batteryPackAbroadTotalVoltage;
	}

	public float getBatteryPackWithinTotalVoltage() {
		return batteryPackWithinTotalVoltage;
	}

	public void setBatteryPackWithinTotalVoltage(float batteryPackWithinTotalVoltage) {
		this.batteryPackWithinTotalVoltage = batteryPackWithinTotalVoltage;
	}

	public float getBatteryPackTotalCurrent() {
		return batteryPackTotalCurrent;
	}

	public void setBatteryPackTotalCurrent(float batteryPackTotalCurrent) {
		this.batteryPackTotalCurrent = batteryPackTotalCurrent;
	}

	public int getChargeDischargeTime() {
		return chargeDischargeTime;
	}

	public void setChargeDischargeTime(int chargeDischargeTime) {
		this.chargeDischargeTime = chargeDischargeTime;
	}

	public int getBatteryPackMostSingleVoltage() {
		return batteryPackMostSingleVoltage;
	}

	public void setBatteryPackMostSingleVoltage(int batteryPackMostSingleVoltage) {
		this.batteryPackMostSingleVoltage = batteryPackMostSingleVoltage;
	}

	public int getBatteryPackMostSingleVoltagePosition() {
		return batteryPackMostSingleVoltagePosition;
	}

	public void setBatteryPackMostSingleVoltagePosition(
			int batteryPackMostSingleVoltagePosition) {
		this.batteryPackMostSingleVoltagePosition = batteryPackMostSingleVoltagePosition;
	}

	public int getBatteryPackLeastSingleVoltage() {
		return batteryPackLeastSingleVoltage;
	}

	public void setBatteryPackLeastSingleVoltage(int batteryPackLeastSingleVoltage) {
		this.batteryPackLeastSingleVoltage = batteryPackLeastSingleVoltage;
	}

	public int getBatteryPackLeastSingleVoltagePosition() {
		return batteryPackLeastSingleVoltagePosition;
	}

	public void setBatteryPackLeastSingleVoltagePosition(
			int batteryPackLeastSingleVoltagePosition) {
		this.batteryPackLeastSingleVoltagePosition = batteryPackLeastSingleVoltagePosition;
	}

	public int getBatteryPackChargeRemainTime() {
		return batteryPackChargeRemainTime;
	}

	public void setBatteryPackChargeRemainTime(int batteryPackChargeRemainTime) {
		this.batteryPackChargeRemainTime = batteryPackChargeRemainTime;
	}

	public int getOBCInputVoltage() {
		return OBCInputVoltage;
	}

	public void setOBCInputVoltage(int oBCInputVoltage) {
		OBCInputVoltage = oBCInputVoltage;
	}

	public float getOBCOutputVoltage() {
		return OBCOutputVoltage;
	}

	public void setOBCOutputVoltage(float oBCOutputVoltage) {
		OBCOutputVoltage = oBCOutputVoltage;
	}

	public float getOBCOutputCurrent() {
		return OBCOutputCurrent;
	}

	public void setOBCOutputCurrent(float oBCOutputCurrent) {
		OBCOutputCurrent = oBCOutputCurrent;
	}

	public int getBatteryPackProbeTemp1() {
		return batteryPackProbeTemp1;
	}

	public void setBatteryPackProbeTemp1(int batteryPackProbeTemp1) {
		this.batteryPackProbeTemp1 = batteryPackProbeTemp1;
	}

	public int getBatteryPackProbeTemp2() {
		return batteryPackProbeTemp2;
	}

	public void setBatteryPackProbeTemp2(int batteryPackProbeTemp2) {
		this.batteryPackProbeTemp2 = batteryPackProbeTemp2;
	}

	public int getBatteryPackProbeTemp3() {
		return batteryPackProbeTemp3;
	}

	public void setBatteryPackProbeTemp3(int batteryPackProbeTemp3) {
		this.batteryPackProbeTemp3 = batteryPackProbeTemp3;
	}

	public int getBatteryPackProbeTemp4() {
		return batteryPackProbeTemp4;
	}

	public void setBatteryPackProbeTemp4(int batteryPackProbeTemp4) {
		this.batteryPackProbeTemp4 = batteryPackProbeTemp4;
	}

	public int getBatteryPackProbeTemp5() {
		return batteryPackProbeTemp5;
	}

	public void setBatteryPackProbeTemp5(int batteryPackProbeTemp5) {
		this.batteryPackProbeTemp5 = batteryPackProbeTemp5;
	}

	public int getBatteryPackProbeTemp6() {
		return batteryPackProbeTemp6;
	}

	public void setBatteryPackProbeTemp6(int batteryPackProbeTemp6) {
		this.batteryPackProbeTemp6 = batteryPackProbeTemp6;
	}

	public int getBatteryPackProbeTemp7() {
		return batteryPackProbeTemp7;
	}

	public void setBatteryPackProbeTemp7(int batteryPackProbeTemp7) {
		this.batteryPackProbeTemp7 = batteryPackProbeTemp7;
	}

	public int getBatteryPackProbeTemp8() {
		return batteryPackProbeTemp8;
	}

	public void setBatteryPackProbeTemp8(int batteryPackProbeTemp8) {
		this.batteryPackProbeTemp8 = batteryPackProbeTemp8;
	}

	public int getBatteryPackProbeTemp9() {
		return batteryPackProbeTemp9;
	}

	public void setBatteryPackProbeTemp9(int batteryPackProbeTemp9) {
		this.batteryPackProbeTemp9 = batteryPackProbeTemp9;
	}

	public int getBatteryPackProbeTemp10() {
		return batteryPackProbeTemp10;
	}

	public void setBatteryPackProbeTemp10(int batteryPackProbeTemp10) {
		this.batteryPackProbeTemp10 = batteryPackProbeTemp10;
	}

	public int getBatteryPackProbeTemp11() {
		return batteryPackProbeTemp11;
	}

	public void setBatteryPackProbeTemp11(int batteryPackProbeTemp11) {
		this.batteryPackProbeTemp11 = batteryPackProbeTemp11;
	}

	public int getBatteryPackProbeTemp12() {
		return batteryPackProbeTemp12;
	}

	public void setBatteryPackProbeTemp12(int batteryPackProbeTemp12) {
		this.batteryPackProbeTemp12 = batteryPackProbeTemp12;
	}

	public int getBatteryPackProbeTemp13() {
		return batteryPackProbeTemp13;
	}

	public void setBatteryPackProbeTemp13(int batteryPackProbeTemp13) {
		this.batteryPackProbeTemp13 = batteryPackProbeTemp13;
	}

	public int getBatteryPackProbeTemp14() {
		return batteryPackProbeTemp14;
	}

	public void setBatteryPackProbeTemp14(int batteryPackProbeTemp14) {
		this.batteryPackProbeTemp14 = batteryPackProbeTemp14;
	}

	public int getBatteryPackProbeTemp15() {
		return batteryPackProbeTemp15;
	}

	public void setBatteryPackProbeTemp15(int batteryPackProbeTemp15) {
		this.batteryPackProbeTemp15 = batteryPackProbeTemp15;
	}

	public int getBatteryPackProbeTemp16() {
		return batteryPackProbeTemp16;
	}

	public void setBatteryPackProbeTemp16(int batteryPackProbeTemp16) {
		this.batteryPackProbeTemp16 = batteryPackProbeTemp16;
	}

	@Override
	public String toString() {
		String s = "电池包剩余电量:"+batteryPackRemainEnergy+"kwh--"+
								"电池包外侧总电压"+batteryPackAbroadTotalVoltage+"V--"+
								"电池包内侧总电压"+batteryPackWithinTotalVoltage+"V--"+
								"电池包总电流"+batteryPackTotalCurrent+"A--"+
								"充放电次数"+chargeDischargeTime+"次--"+
								"电池包最高单体电压"+batteryPackMostSingleVoltage+"mV--"+
								"电池包最高单体电压位置 "+batteryPackMostSingleVoltagePosition+"--"+
								"电池包最低单体电压"+batteryPackLeastSingleVoltage+"mV--"+
								"电池包最低单体电压位置"+batteryPackLeastSingleVoltagePosition+"--"+
								"电池包充电剩余时间"+batteryPackChargeRemainTime+"min--"+
								"OBC输入电压 "+OBCInputVoltage+"V--"+
								"OBC输出电压"+OBCOutputVoltage+"V--"+
								"OBC输出电流"+OBCOutputCurrent+"A--"+
										"电池包16个探针温度1"+batteryPackProbeTemp1+"℃--"+
										"电池包16个探针温度2"+batteryPackProbeTemp2+"℃--"+
										"电池包16个探针温度3"+batteryPackProbeTemp3+"℃--"+
										"电池包16个探针温度4"+batteryPackProbeTemp4+"℃--"+
										"电池包16个探针温度5"+batteryPackProbeTemp5+"℃--"+
										"电池包16个探针温度6"+batteryPackProbeTemp6+"℃--"+
										"电池包16个探针温度7"+batteryPackProbeTemp7+"℃--"+
										"电池包16个探针温度8"+batteryPackProbeTemp8+"℃--"+
										"电池包16个探针温度9"+batteryPackProbeTemp9+"℃--"+
										"电池包16个探针温度10"+batteryPackProbeTemp10+"℃--"+
										"电池包16个探针温度11"+batteryPackProbeTemp11+"℃--"+
										"电池包16个探针温度12"+batteryPackProbeTemp12+"℃--"+
										"电池包16个探针温度13"+batteryPackProbeTemp13+"℃--"+
										"电池包16个探针温度14"+batteryPackProbeTemp14+"℃--"+
										"电池包16个探针温度15"+batteryPackProbeTemp15+"℃--"+
										"电池包16个探针温度16"+batteryPackProbeTemp16+"℃--"
										;
		return s;
	}

	@Override
	public String ToJson() {
		String ParaString = "";
		try {
			ParaString = new JSONStringer()
					.object()
					.key("batteryPackRemainEnergy")
					.value(String.valueOf(batteryPackRemainEnergy))
					.key("batteryPackAbroadTotalVoltage")
					.value(String.valueOf(batteryPackAbroadTotalVoltage))
					.key("batteryPackWithinTotalVoltage")
					.value(String.valueOf(batteryPackWithinTotalVoltage))
					.key("batteryPackTotalCurrent")
					.value(String.valueOf(batteryPackTotalCurrent))
					.key("chargeDischargeTime")
					.value(String.valueOf(chargeDischargeTime))
					.key("batteryPackProbeTemp1")
					.value(String.valueOf(batteryPackProbeTemp1))
					.key("batteryPackProbeTemp2")
					.value(String.valueOf(batteryPackProbeTemp2))
					.key("batteryPackProbeTemp3")
					.value(String.valueOf(batteryPackProbeTemp3))
					.key("batteryPackProbeTemp4")
					.value(String.valueOf(batteryPackProbeTemp4))
					.key("batteryPackProbeTemp5")
					.value(String.valueOf(batteryPackProbeTemp5))
					.key("batteryPackProbeTemp6")
					.value(String.valueOf(batteryPackProbeTemp6))
					.key("batteryPackProbeTemp7")
					.value(String.valueOf(batteryPackProbeTemp7))
					.key("batteryPackProbeTemp8")
					.value(String.valueOf(batteryPackProbeTemp8))
					.key("batteryPackProbeTemp9")
					.value(String.valueOf(batteryPackProbeTemp9))
					.key("batteryPackProbeTemp10")
					.value(String.valueOf(batteryPackProbeTemp10))
					.key("batteryPackProbeTemp11")
					.value(String.valueOf(batteryPackProbeTemp11))
					.key("batteryPackProbeTemp12")
					.value(String.valueOf(batteryPackProbeTemp12))
					.key("batteryPackProbeTemp13")
					.value(String.valueOf(batteryPackProbeTemp13))
					.key("batteryPackProbeTemp14")
					.value(String.valueOf(batteryPackProbeTemp14))
					.key("batteryPackProbeTemp15")
					.value(String.valueOf(batteryPackProbeTemp15))
					.key("batteryPackProbeTemp16")
					.value(String.valueOf(batteryPackProbeTemp16))
					.key("batteryPackMostSingleVoltage")
					.value(String.valueOf(batteryPackMostSingleVoltage))
					.key("batteryPackMostSingleVoltagePosition")
					.value(String.valueOf(batteryPackMostSingleVoltagePosition))
					.key("batteryPackLeastSingleVoltage")
					.value(String.valueOf(batteryPackLeastSingleVoltage))
					.key("batteryPackLeastSingleVoltagePosition")
					.value(String.valueOf(batteryPackLeastSingleVoltagePosition))
					.key("batteryPackChargeRemainTime")
					.value(String.valueOf(batteryPackChargeRemainTime))
					.key("OBCInputVoltage")
					.value(String.valueOf(OBCInputVoltage)).key("OBCOutputVoltage")
					.value(String.valueOf(OBCOutputVoltage))
					.key("OBCOutputCurrent")
					.value(String.valueOf(OBCOutputCurrent))
					.endObject().toString();			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ParaString;
	}


}
