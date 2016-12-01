package com.autopet.hardware.aidl.tboxinfo;

import org.json.JSONException;
import org.json.JSONStringer;

public class CarInfoInstant extends TBoxInfo {
	/* 电机当前转速 -16384～16383rpm */
	private int EMCurrentSpeed;

	/* 电机当前输出功率 -102～101kw */
	private float EMCurrentOutputPower;

	/* 电机当前温度 -40～213℃ */
	private int EMCurrentTemp;

	/* 电机当前扭矩 -511～511Nm */
	private float EMCurrentTorque;

	/* 电机控制器母线电压 0～1021V */
	private int EMControllerBusVoltage;

	/* 电机控制器母线电流 -1023～1022A */
	private int EMControllerBusCurrent;

	/* 车辆行驶里程 0～67108863km */
	private float carDriveMileage;

	/* 剩余续驶里程 0～255km */
	private int remainDriveMileage;

	/* 车速 0～511km/h */
	private float carDriveSpeed;

	public CarInfoInstant() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CarInfoInstant(int eMCurrentSpeed, float eMCurrentOutputPower,
			int eMCurrentTemp, float eMCurrentTorque,
			int eMControllerBusVoltage, int eMControllerBusCurrent,
			float carDriveMileage, int remainDriveMileage, float carDriveSpeed) {
		super();
		EMCurrentSpeed = eMCurrentSpeed;
		EMCurrentOutputPower = eMCurrentOutputPower;
		EMCurrentTemp = eMCurrentTemp;
		EMCurrentTorque = eMCurrentTorque;
		EMControllerBusVoltage = eMControllerBusVoltage;
		EMControllerBusCurrent = eMControllerBusCurrent;
		this.carDriveMileage = carDriveMileage;
		this.remainDriveMileage = remainDriveMileage;
		this.carDriveSpeed = carDriveSpeed;
	}

	public int getEMCurrentSpeed() {
		return EMCurrentSpeed;
	}

	public void setEMCurrentSpeed(int eMCurrentSpeed) {
		EMCurrentSpeed = eMCurrentSpeed;
	}

	public float getEMCurrentOutputPower() {
		return EMCurrentOutputPower;
	}

	public void setEMCurrentOutputPower(float eMCurrentOutputPower) {
		EMCurrentOutputPower = eMCurrentOutputPower;
	}

	public int getEMCurrentTemp() {
		return EMCurrentTemp;
	}

	public void setEMCurrentTemp(int eMCurrentTemp) {
		EMCurrentTemp = eMCurrentTemp;
	}

	public float getEMCurrentTorque() {
		return EMCurrentTorque;
	}

	public void setEMCurrentTorque(float eMCurrentTorque) {
		EMCurrentTorque = eMCurrentTorque;
	}

	public int getEMControllerBusVoltage() {
		return EMControllerBusVoltage;
	}

	public void setEMControllerBusVoltage(int eMControllerBusVoltage) {
		EMControllerBusVoltage = eMControllerBusVoltage;
	}

	public int getEMControllerBusCurrent() {
		return EMControllerBusCurrent;
	}

	public void setEMControllerBusCurrent(int eMControllerBusCurrent) {
		EMControllerBusCurrent = eMControllerBusCurrent;
	}

	public float getCarDriveMileage() {
		return carDriveMileage;
	}

	public void setCarDriveMileage(float carDriveMileage) {
		this.carDriveMileage = carDriveMileage;
	}

	public int getRemainDriveMileage() {
		return remainDriveMileage;
	}

	public void setRemainDriveMileage(int remainDriveMileage) {
		this.remainDriveMileage = remainDriveMileage;
	}

	public float getCarDriveSpeed() {
		return carDriveSpeed;
	}

	public void setCarDriveSpeed(float carDriveSpeed) {
		this.carDriveSpeed = carDriveSpeed;
	}

	@Override
	public String toString() {
		String s = "电机当前转速:" + EMCurrentSpeed + "rpm--" + "电机当前输出功率"
				+ EMCurrentOutputPower + "kw--" + "电机当前温度" + EMCurrentTemp
				+ "℃--" + "电机当前扭矩" + EMCurrentTorque + "Nm--" + "电机控制器母线电压"
				+ EMControllerBusVoltage + "V--" + "电机控制器母线电流"
				+ EMControllerBusCurrent + "A--" + "车辆行驶里程" + carDriveMileage
				+ "km--" + "剩余续驶里程" + remainDriveMileage + "km--" + "车速"
				+ carDriveSpeed + "km/h--";
		return s;
	}

	@Override
	public String ToJson() {
		String ParaString = "";
		try {
			ParaString = new JSONStringer().object().key("EMCurrentSpeed")
					.value(String.valueOf(EMCurrentSpeed))
					.key("EMCurrentOutputPower")
					.value(String.valueOf(EMCurrentOutputPower))
					.key("EMCurrentTemp").value(String.valueOf(EMCurrentTemp))
					.key("EMCurrentTorque")
					.value(String.valueOf(EMCurrentTorque))
					.key("EMControllerBusVoltage")
					.value(String.valueOf(EMControllerBusVoltage))
					.key("EMControllerBusCurrent")
					.value(String.valueOf(EMControllerBusCurrent))
					.key("carDriveMileage")
					.value(String.valueOf(carDriveMileage))
					.key("remainDriveMileage")
					.value(String.valueOf(remainDriveMileage))
					.key("carDriveSpeed").value(String.valueOf(carDriveSpeed))
					.endObject().toString();

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ParaString;

	}

}
