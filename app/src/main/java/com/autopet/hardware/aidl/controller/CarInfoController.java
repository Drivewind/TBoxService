package com.autopet.hardware.aidl.controller;

import android.os.Handler;
import android.os.Message;

import com.autopet.hardware.aidl.tboxinfo.CarInfoInstant;
import com.autopet.hardware.aidl.tboxinfo.CarInfoNoneInstant;
import com.autopet.hardware.aidl.tboxinfo.CarInfoOnce;
import com.autopet.hardware.aidl.tboxinfo.CarInfoWarning;
import com.autopet.hardware.aidl.tboxinfo.TBoxInfo;
import com.autopet.hardware.aidl.util.CommonUtil;
import com.autopet.hardware.aidl.util.FileUtils;

public class CarInfoController extends TBoxController {
	private int iCurrentCarInfo;
	private static final int CARINFO_ONCE = 0x1001;
	private static final int CARINFO_WARNING = 0x1002;
	private static final int CARINFO_INSTANT = 0x1003;
	private static final int CARINFO_NONEINSTANT = 0x1004;
	private TBoxInfo tCurrentCarInfo;
	private CarInfoOnce mCarInfoOnce;
	private CarInfoWarning mCarInfoWarning;
	private CarInfoInstant mCarInfoInstant;
	private CarInfoNoneInstant mCarInfoNoneInstant;
	private String sCurrentCarInfo;
	private Handler mHandler;
	private CommonUtil mCommonUtil;

	public CarInfoController(Handler handler) {
		mHandler = handler;
		mCommonUtil = new CommonUtil();
	}

	@Override
	public void parseCmdInMcu(byte[] mcustatus) {
		switch (mcustatus[0]) {
		case 0x70:
			parseCarInfoWarningMcu(mcustatus);
			outCarInfoWarning();
			break;
		case 0x71:
			parseCarInfoInstantMcu(mcustatus);
			outCarInfoInstant();
			break;
		case 0x72:
			parseCarInfoNoneInstantMcu(mcustatus);
			outCarInfoNoneInstant();
			break;
		case 0x73:
			parseCarInfoOnceMcu(mcustatus);
			outCarInfoOnce();
			break;

		default:
			break;
		}

	}

	private void parseCarInfoWarningMcu(byte[] mcustatus) {
		if (mCarInfoWarning == null) {
			mCarInfoWarning = new CarInfoWarning();
		}
		int OBCCurrentState = (mcustatus[1] & 0x0f0) >>> 4;
		mCarInfoWarning.setOBCCurrentState(OBCCurrentState);
		int OBCState1 = mcustatus[1] & 0x0f;
		mCarInfoWarning.setOBCHardwareFault(OBCState1 / 8);
		mCarInfoWarning.setOBCInputOverVoltage(OBCState1 % 8 / 4);
		mCarInfoWarning.setOBCInputUnderVoltage(OBCState1 % 4 / 2);
		mCarInfoWarning.setOBCOutputOverVoltage(OBCState1 % 2);
		int OBCState2 = (mcustatus[2] & 0x0f0) >>> 4;
		mCarInfoWarning.setOBCOutputUnderVoltage(OBCState2 / 8);
		mCarInfoWarning.setOBCOutputOverCurrent(OBCState2 % 8 / 4);
		mCarInfoWarning.setOBCOverTemp(OBCState2 % 4 / 2);
		mCarInfoWarning.setFrontSideCollisionSign(OBCState2 % 2);
		int OBCState3 = mcustatus[2] & 0x0f;
		mCarInfoWarning.setBackCollisionSign(OBCState3 / 8);
		mCarInfoWarning.setDriverSafetyBelt(OBCState3 % 8 / 4);

//		StringBuffer warningStatus = new StringBuffer();
//		for (int i = 1; i < mcustatus.length; i++) {
//			warningStatus.append(
//					Integer.toHexString(mCommonUtil
//							.getUnsignedByte(mcustatus[i]))).append("-");
//
//		}
//		FileUtils.saveToFile(FileUtils.GW_LOG_SAVE_CARINFOWARNING + "_"
//				+ mCommonUtil.getCurDate() + ".txt", mCommonUtil.getCurTime2()
//				+ " " + warningStatus);
//		FileUtils.saveToFile(FileUtils.GW_LOG_SAVE_CARINFOWARNING + "_"
//				+ mCommonUtil.getCurDate() + ".txt", mCommonUtil.getCurTime2()
//				+ " " + mCarInfoWarning.toString());
	}

	private void parseCarInfoOnceMcu(byte[] mcustatus) {
		if (mCarInfoOnce == null) {
			mCarInfoOnce = new CarInfoOnce();
		}
		int powerBatteryPackTotalAmount = (mcustatus[1] & 0x40) >>> 6;
		mCarInfoOnce
				.setPowerBatteryPackTotalAmount(powerBatteryPackTotalAmount);
		int singleBatteryTotalAmount = mcustatus[1] & 0x3f;
		mCarInfoOnce.setSingleBatteryTotalAmount(singleBatteryTotalAmount);
		int batteryTypeCode = mcustatus[2] & 0x0f;
		mCarInfoOnce.setBatteryTypeCode(batteryTypeCode);
		int batteryManufacturerCode = mCommonUtil.getUnsignedByte(mcustatus[3])
				* 16777216 + mCommonUtil.getUnsignedByte(mcustatus[4]) * 65536
				+ mCommonUtil.getUnsignedByte(mcustatus[5]) * 256
				+ mCommonUtil.getUnsignedByte(mcustatus[6]);
		mCarInfoOnce.setBatteryManufacturerCode(batteryManufacturerCode);
		int year = (mcustatus[7] >>> 1) & 0x7f;
		year += 2010;
		int month = (mcustatus[7] & 0x01) * 8 + (mcustatus[8] >>> 5) & 0x07;
		int date = mcustatus[8] & 0x1f;
		String batteryProductionDateTime = year + "/" + month + "/" + date;
		mCarInfoOnce.setBatteryProductionDateTime(batteryProductionDateTime);
		float batteryPackRatedEnerge = (mcustatus[9] & 0x3f) * 0.1f + 10;
		mCarInfoOnce.setBatteryPackRatedEnerge(batteryPackRatedEnerge);
//		StringBuffer onceStatus = new StringBuffer();
//		for (int i = 1; i < mcustatus.length; i++) {
//			onceStatus.append(
//					Integer.toHexString(mCommonUtil
//							.getUnsignedByte(mcustatus[i]))).append("-");
//
//		}
//		FileUtils.saveToFile(FileUtils.GW_LOG_SAVE_CARINFOONCE + "_"
//				+ mCommonUtil.getCurDate() + ".txt", mCommonUtil.getCurTime()
//				+ " " + onceStatus);
//		FileUtils.saveToFile(FileUtils.GW_LOG_SAVE_CARINFOONCE + "_"
//				+ mCommonUtil.getCurDate() + ".txt", mCommonUtil.getCurTime()
//				+ " " + mCarInfoOnce.toString());
	}

	private void parseCarInfoInstantMcu(byte[] mcustatus) {
		if (mCarInfoInstant == null) {
			mCarInfoInstant = new CarInfoInstant();
		}
		int EMCurrentSpeed = (mcustatus[1] & 0x7f) * 256
				+ mCommonUtil.getUnsignedByte(mcustatus[2]) - 16384;
		mCarInfoInstant.setEMCurrentSpeed(EMCurrentSpeed);
		float EMCurrentOutputPower = ((mcustatus[3] & 0x07) * 256 + mCommonUtil
				.getUnsignedByte(mcustatus[4])) * 0.1f - 102;
		mCarInfoInstant.setEMCurrentOutputPower(EMCurrentOutputPower);
		int EMCurrentTemp = mCommonUtil.getUnsignedByte(mcustatus[5]) - 40;
		mCarInfoInstant.setEMCurrentTemp(EMCurrentTemp);
		float EMCurrentTorque = ((mcustatus[6] & 0x0f) * 256 + mCommonUtil
				.getUnsignedByte(mcustatus[7])) * 0.25f - 511;
		mCarInfoInstant.setEMCurrentTorque(EMCurrentTorque);
		int EMControllerBusVoltage = (mcustatus[8] & 0x03) * 256
				+ mCommonUtil.getUnsignedByte(mcustatus[9]);
		mCarInfoInstant.setEMControllerBusVoltage(EMControllerBusVoltage);
		int EMControllerBusCurrent = (mcustatus[10] & 0x07) * 256
				+ mCommonUtil.getUnsignedByte(mcustatus[11]) - 1023;
		mCarInfoInstant.setEMControllerBusCurrent(EMControllerBusCurrent);
		float carDriveMileage = (mCommonUtil.getUnsignedByte(mcustatus[12])
				* 16777216 + mCommonUtil.getUnsignedByte(mcustatus[13]) * 65536
				+ mCommonUtil.getUnsignedByte(mcustatus[14]) * 256 + mCommonUtil
				.getUnsignedByte(mcustatus[15])) * 0.015625f;
		mCarInfoInstant.setCarDriveMileage(carDriveMileage);
		int remainDriveMileage = mCommonUtil.getUnsignedByte(mcustatus[16]);
		mCarInfoInstant.setRemainDriveMileage(remainDriveMileage);
		float carDriveSpeed = ((mcustatus[17] & 0x7f) * 256 + mCommonUtil
				.getUnsignedByte(mcustatus[18])) * 0.015625f;
		mCarInfoInstant.setCarDriveSpeed(carDriveSpeed);

//		StringBuffer instantStatus = new StringBuffer();
//		for (int i = 1; i < mcustatus.length; i++) {
//			instantStatus.append(
//					Integer.toHexString(mCommonUtil
//							.getUnsignedByte(mcustatus[i]))).append("-");
//		}
//		FileUtils.saveToFile(FileUtils.GW_LOG_SAVE_CARINFOINSTANT + "_"
//				+ mCommonUtil.getCurDate() + ".txt", mCommonUtil.getCurTime()
//				+ " " + instantStatus);
//		FileUtils.saveToFile(FileUtils.GW_LOG_SAVE_CARINFOINSTANT + "_"
//				+ mCommonUtil.getCurDate() + ".txt", mCommonUtil.getCurTime()
//				+ " " + mCarInfoInstant.toString());
	}

	private void parseCarInfoNoneInstantMcu(byte[] mcustatus) {
		if (mCarInfoNoneInstant == null) {
			mCarInfoNoneInstant = new CarInfoNoneInstant();
		}
		float batteryPackRemainEnergy = mCommonUtil
				.getUnsignedByte(mcustatus[1]) * 0.1f;
		mCarInfoNoneInstant.setBatteryPackRemainEnergy(batteryPackRemainEnergy);
		float batteryPackAbroadTotalVoltage = ((mcustatus[2] & 0x1f) * 256 + mCommonUtil
				.getUnsignedByte(mcustatus[3])) * 0.1f;
		mCarInfoNoneInstant
				.setBatteryPackAbroadTotalVoltage(batteryPackAbroadTotalVoltage);
		float batteryPackWithinTotalVoltage = ((mcustatus[4] & 0x1f) * 256 + mCommonUtil
				.getUnsignedByte(mcustatus[5])) * 0.1f;
		mCarInfoNoneInstant
				.setBatteryPackWithinTotalVoltage(batteryPackWithinTotalVoltage);
		float batteryPackTotalCurrent = ((mcustatus[6] & 0x7f) * 256 + mCommonUtil
				.getUnsignedByte(mcustatus[7])) * 0.1f - 1000;
		mCarInfoNoneInstant.setBatteryPackTotalCurrent(batteryPackTotalCurrent);
		int chargeDischargeTime = mCommonUtil.getUnsignedByte(mcustatus[8])
				* 256 + mCommonUtil.getUnsignedByte(mcustatus[9]);
		mCarInfoNoneInstant.setChargeDischargeTime(chargeDischargeTime);
		int batteryPackMostSingleVoltage = mCommonUtil
				.getUnsignedByte(mcustatus[10])
				* 256
				+ mCommonUtil.getUnsignedByte(mcustatus[11]);
		mCarInfoNoneInstant
				.setBatteryPackMostSingleVoltage(batteryPackMostSingleVoltage);
		int batteryPackMostSingleVoltagePosition = mCommonUtil
				.getUnsignedByte(mcustatus[12]);
		mCarInfoNoneInstant
				.setBatteryPackMostSingleVoltagePosition(batteryPackMostSingleVoltagePosition);
		int batteryPackLeastSingleVoltage = mCommonUtil
				.getUnsignedByte(mcustatus[13])
				* 256
				+ mCommonUtil.getUnsignedByte(mcustatus[14]);
		mCarInfoNoneInstant
				.setBatteryPackLeastSingleVoltage(batteryPackLeastSingleVoltage);
		int batteryPackLeastSingleVoltagePosition = mCommonUtil
				.getUnsignedByte(mcustatus[15]);
		mCarInfoNoneInstant
				.setBatteryPackLeastSingleVoltagePosition(batteryPackLeastSingleVoltagePosition);
		int batteryPackChargeRemainTime = (mcustatus[16] & 0x1f) * 256
				+ mCommonUtil.getUnsignedByte(mcustatus[17]);
		mCarInfoNoneInstant
				.setBatteryPackChargeRemainTime(batteryPackChargeRemainTime);
		int OBCInputVoltage = (mcustatus[18] & 0x01) * 256
				+ mCommonUtil.getUnsignedByte(mcustatus[19]);
		mCarInfoNoneInstant.setOBCInputVoltage(OBCInputVoltage);
		float OBCOutputVoltage = ((mcustatus[20] & 0x7f) * 256 + mCommonUtil
				.getUnsignedByte(mcustatus[21])) * 0.1f;
		mCarInfoNoneInstant.setOBCOutputVoltage(OBCOutputVoltage);
		float OBCOutputCurrent = ((mcustatus[22] & 0x7f) * 256 + mCommonUtil
				.getUnsignedByte(mcustatus[23])) * 0.1f;
		mCarInfoNoneInstant.setOBCOutputCurrent(OBCOutputCurrent);

		int batteryPackProbeTemp16 = mCommonUtil.getUnsignedByte(mcustatus[24]) - 50;
		mCarInfoNoneInstant.setBatteryPackProbeTemp16(batteryPackProbeTemp16);
		int batteryPackProbeTemp15 = mCommonUtil.getUnsignedByte(mcustatus[25]) - 50;
		mCarInfoNoneInstant.setBatteryPackProbeTemp15(batteryPackProbeTemp15);
		int batteryPackProbeTemp14 = mCommonUtil.getUnsignedByte(mcustatus[26]) - 50;
		mCarInfoNoneInstant.setBatteryPackProbeTemp14(batteryPackProbeTemp14);
		int batteryPackProbeTemp13 = mCommonUtil.getUnsignedByte(mcustatus[27]) - 50;
		mCarInfoNoneInstant.setBatteryPackProbeTemp13(batteryPackProbeTemp13);
		int batteryPackProbeTemp12 = mCommonUtil.getUnsignedByte(mcustatus[28]) - 50;
		mCarInfoNoneInstant.setBatteryPackProbeTemp12(batteryPackProbeTemp12);
		int batteryPackProbeTemp11 = mCommonUtil.getUnsignedByte(mcustatus[29]) - 50;
		mCarInfoNoneInstant.setBatteryPackProbeTemp11(batteryPackProbeTemp11);
		int batteryPackProbeTemp10 = mCommonUtil.getUnsignedByte(mcustatus[30]) - 50;
		mCarInfoNoneInstant.setBatteryPackProbeTemp10(batteryPackProbeTemp10);
		int batteryPackProbeTemp9 = mCommonUtil.getUnsignedByte(mcustatus[31]) - 50;
		mCarInfoNoneInstant.setBatteryPackProbeTemp9(batteryPackProbeTemp9);
		int batteryPackProbeTemp8 = mCommonUtil.getUnsignedByte(mcustatus[32]) - 50;
		mCarInfoNoneInstant.setBatteryPackProbeTemp8(batteryPackProbeTemp8);
		int batteryPackProbeTemp7 = mCommonUtil.getUnsignedByte(mcustatus[33]) - 50;
		mCarInfoNoneInstant.setBatteryPackProbeTemp7(batteryPackProbeTemp7);
		int batteryPackProbeTemp6 = mCommonUtil.getUnsignedByte(mcustatus[34]) - 50;
		mCarInfoNoneInstant.setBatteryPackProbeTemp6(batteryPackProbeTemp6);
		int batteryPackProbeTemp5 = mCommonUtil.getUnsignedByte(mcustatus[35]) - 50;
		mCarInfoNoneInstant.setBatteryPackProbeTemp5(batteryPackProbeTemp5);
		int batteryPackProbeTemp4 = mCommonUtil.getUnsignedByte(mcustatus[36]) - 50;
		mCarInfoNoneInstant.setBatteryPackProbeTemp4(batteryPackProbeTemp4);
		int batteryPackProbeTemp3 = mCommonUtil.getUnsignedByte(mcustatus[37]) - 50;
		mCarInfoNoneInstant.setBatteryPackProbeTemp3(batteryPackProbeTemp3);
		int batteryPackProbeTemp2 = mCommonUtil.getUnsignedByte(mcustatus[38]) - 50;
		mCarInfoNoneInstant.setBatteryPackProbeTemp2(batteryPackProbeTemp2);
		int batteryPackProbeTemp1 = mCommonUtil.getUnsignedByte(mcustatus[39]) - 50;
		mCarInfoNoneInstant.setBatteryPackProbeTemp1(batteryPackProbeTemp1);

//		StringBuffer noneInstantStatus = new StringBuffer();
//		for (int i = 1; i < mcustatus.length; i++) {
//			noneInstantStatus.append(
//					Integer.toHexString(mCommonUtil
//							.getUnsignedByte(mcustatus[i]))).append("-");
//		}
//		FileUtils.saveToFile(FileUtils.GW_LOG_SAVE_CARINFONONEINSTANT + "_"
//				+ mCommonUtil.getCurDate() + ".txt", mCommonUtil.getCurTime()
//				+ " " + noneInstantStatus);
//		FileUtils.saveToFile(FileUtils.GW_LOG_SAVE_CARINFONONEINSTANT + "_"
//				+ mCommonUtil.getCurDate() + ".txt", mCommonUtil.getCurTime()
//				+ " " + mCarInfoNoneInstant.toString());
	}

	@Override
	public void outJsonToApp() {
		switch (iCurrentCarInfo) {
		case CARINFO_ONCE:
			sCurrentCarInfo = "CARINFO_ONCE";
			tCurrentCarInfo = mCarInfoOnce;
			break;
		case CARINFO_WARNING:
			sCurrentCarInfo = "CARINFO_WARNING";
			tCurrentCarInfo = mCarInfoWarning;
			break;
		case CARINFO_INSTANT:
			sCurrentCarInfo = "CARINFO_INSTANT";
			tCurrentCarInfo = mCarInfoInstant;
			break;
		case CARINFO_NONEINSTANT:
			sCurrentCarInfo = "CARINFO_NONEINSTANT";
			tCurrentCarInfo = mCarInfoNoneInstant;
			break;
		default:
			break;
		}
		Message message = new Message();
		message.getData().putString("TBOXINFO",
				TBoxInfoToJson(tCurrentCarInfo, sCurrentCarInfo));
		message.what = 1;
		mHandler.sendMessage(message);

	}

	@Override
	public void outCmdToMcu(byte[] cmd) {
		// TODO Auto-generated method stub

	}

	public void outCarInfoWarning() {
		if (mCarInfoWarning != null) {
			iCurrentCarInfo = CARINFO_WARNING;
			outJsonToApp();
		}
	}

	public void outCarInfoOnce() {
		if (mCarInfoOnce != null) {
			iCurrentCarInfo = CARINFO_ONCE;
			outJsonToApp();
		}
	}

	public void outCarInfoInstant() {
		if (mCarInfoInstant != null) {
			iCurrentCarInfo = CARINFO_INSTANT;
			outJsonToApp();
		}
	}

	public void outCarInfoNoneInstant() {
		if (mCarInfoNoneInstant != null) {
			iCurrentCarInfo = CARINFO_NONEINSTANT;
			outJsonToApp();
		}
	}

}
