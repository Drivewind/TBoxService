package com.autopet.hardware.aidl.hal;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.autopet.hardware.aidl.controller.CarInfoController;
import com.autopet.hardware.aidl.controller.GpsController;
import com.autopet.hardware.aidl.controller.RadioController;
import com.autopet.hardware.aidl.controller.RadioController.OnSystemStateChangeListener;
import com.autopet.hardware.aidl.controller.TBoxControllerFactory;
import com.autopet.hardware.aidl.tboxinfo.GPSInfo;
import com.autopet.hardware.aidl.util.AppManager;
import com.autopet.hardware.aidl.util.CommonUtil;
import com.autopet.hardware.aidl.util.FileUtils;
import com.autopet.hardware.aidl.util.progressmanager.ProcessManager;
import com.autopet.hardware.aidl.util.progressmanager.TaskInfo;

/*Johnson Zhang Modify by ZhongYong Chen
 * 接收主程序的收音机命令
 * 返回给主程序收音机
 * 使用Google GSON库来解析和重组JSON原语
 * https://code.google.com/p/google-gson/
 */
public class PetMsgRadioHAL {
	final static String TAG = "E3HWService";
	OnJsonOutput onJsonOutput = null;
	OnPadConnectListener onPadConnectListener = null;

	private int padConnect;// 与主机是否连接上 0未连接上 1连接上
	private long lastReceiveDataTime;// 单位milliseconds
	Handler radioHandler;
	int maxvol = 31;
	int lastband = 16;
	boolean ismute = false;
	boolean ispoweron = false;

	private Context mContext;
	private Handler mHandler;
	private RadioController radioController;
	private CarInfoController carInfoController;
	private GpsController gpsController;
	private TBoxControllerFactory factory;
	private CommonUtil mCommonUtil;

	public PetMsgRadioHAL(Context context) {
		mContext = context;
		mCommonUtil = new CommonUtil();
		initHandler();
		initTBoxController();
	}

	private void initHandler() {
		mHandler = new Handler(Looper.getMainLooper()) {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case 1:
						OutJson(msg.getData().getString("TBOXINFO"));
						break;
					case 2:
						OutMcu(msg.getData().getByteArray("TBOXCMD"));
						break;
					case 3:
						// Toast.makeText(
						// mContext,
						// "自动搜台完成，共搜到"
						// + msg.getData().getInt("stationnumber")
						// + "个有效电台", Toast.LENGTH_SHORT).show();
						break;
					default:
						break;
				}

			}
		};
	}

	private void initTBoxController() {

		factory = TBoxControllerFactory.getControllerFactory();
		factory.setObjects(mHandler, mContext);
		radioController = (RadioController) factory
				.getController("RadioController");
		carInfoController = (CarInfoController) factory
				.getController("CarInfoController");
		gpsController = (GpsController) factory.getController("GpsController");

	}

	private String PadConnect_Info(int connect) {
		String CtrlString = "", ParaString = "";
		try {
			ParaString = new JSONStringer().object().key("isPadConnect")
					.value(String.valueOf(connect)).endObject().toString();
			CtrlString = new JSONStringer().object().key("ISPADCONNECT")
					.value(ParaString).endObject().toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return CtrlString;

	}

	private String PadReConnect_Info() {
		String CtrlString = "", ParaString = "";
		try {

			CtrlString = new JSONStringer().object().key("ISPADRECONNECT")
					.value(ParaString).endObject().toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return CtrlString;

	}

	// pad连接有变化就通知应用层
	public void notifyPadConnection() {
		if (!checkPadConnected()) {
			if (padConnect == 1) {
				padConnect = 0;
				updatePadConnection();
			}
		} else {
			if (padConnect == 0) {
				padConnect = 1;
				updatePadConnection();
			}
		}
	}

	public void notifyPadConnection(int connection) {
		if (padConnect != connection) {
			padConnect = connection;
			updatePadConnection();
		}
	}

	public void notifyPadReConnection() {
		updatePadReConnection();
	}

	public void setLastReceiveDataTime(long lastReceiveDataTime) {
		this.lastReceiveDataTime = lastReceiveDataTime;
	}

	// 将pad连接状态发送给应用层
	public synchronized void updatePadConnection() {
		OutJson(PadConnect_Info(padConnect));
	}

	// 将pad连接状态发送给应用层
	public synchronized void updatePadReConnection() {
		OutJson(PadReConnect_Info());
	}

	// 检测pad是否连接
	private boolean checkPadConnected() {
		int seconds = (int) ((System.currentTimeMillis() - lastReceiveDataTime) / 1000);
		boolean isConnected = seconds <= 10 ? true : false;
		outPadConnect(isConnected);
		return isConnected;
	}

	public boolean isPadConnected() {
		return padConnect == 1 ? true : false;
	}

	/* 接收主程序的传来的底层SOCKET数据 */
	public String InJson(String jsonstr) {
		String JsonID = "";
		JSONObject jsonparas;

		try {
			JSONObject jsonobject = new JSONObject(jsonstr);
			JsonID = jsonobject.names().getString(0);

			Log.d(TAG, "InJson KEY:" + jsonobject.names().getString(0));
			if (JsonID.equals("RADIOGET")) {
				radioController.outJsonToApp();
			} else if (JsonID.equals("RADIOPOWER")) {
				Log.d(TAG, "CMD:RADIOPOWER");

				radioController.outJsonToApp();
			} else if (JsonID.equals("RADIOSETBAND")) {
				jsonparas = new JSONObject(jsonobject.getString("RADIOSETBAND"));
				if (jsonparas.getString("BAND").equals("SSW")) {
					lastband = 1;
				} else if (jsonparas.getString("BAND").equals("SW")) {
					lastband = 2;
				} else if (jsonparas.getString("BAND").equals("LW")) {
					lastband = 4;
				} else if (jsonparas.getString("BAND").equals("MW")) {
					lastband = 8;
				} else if (jsonparas.getString("BAND").equals("FM")) {
					lastband = 16;
				}
				Log.d(TAG, "CMD:RADIOSETBAND:" + lastband);
				radioController.outJsonToApp();
			} else if (JsonID.equals("RADIOSETVOL")) {
				if (!radioController.isSearching()) {
					jsonparas = new JSONObject(
							jsonobject.getString("RADIOSETVOL"));
					int vol = Integer.parseInt(jsonparas.getString("VOL"));
					if (vol > maxvol)
						vol = maxvol;
					radioController.RadioCmd_AdjustVolume(vol);
				}
			} else if (JsonID.equals("RADIOGETFRQ")) {
				radioController.outJsonToApp();
			} else if (JsonID.equals("ChangeToAUX")) {
				radioController.changeToMediaMode();
			} else if (JsonID.equals("ChangeToRAD")) {
				radioController.changeToRadioMode();
			} else if (JsonID.equals("AUTOSEARCHSTATION")) {
				radioController.changeToAutoSearchState();
			} else if (JsonID.equals("STOPAUTOSEARCHSTATION")) {
				radioController.stopAutoSearchStation();
			} else if (JsonID.equals("STOPSEARCHSTATION")) {
				radioController.stopSeekStation();
			} else if (JsonID.equals("SEEKUP")) {
				radioController.changeToSeekUpSearchState();
			} else if (JsonID.equals("SEEKDOWN")) {
				radioController.changeToSeekDownSearchState();
			} else if (JsonID.equals("STEPUP")) {
				radioController.changeToStepUpSearchState();
			} else if (JsonID.equals("STEPDOWN")) {
				radioController.changeToStepDownSearchState();
			} else if (JsonID.equals("SEEKSTATION")) {
				int freq = Integer.valueOf(jsonobject.getString("SEEKSTATION"));
				radioController.seekStation(freq);
			} else if (JsonID.equals("SETMUTE")) {
				radioController.mute();
			} else if (JsonID.equals("SETUNMUTE")) {
				radioController.unMute();
			}
			else if (JsonID.equals("GETPADCONNECTION")) {
				updatePadConnection();
			} else if (JsonID.equals("GETCARINFOWARNING")) {
				carInfoController.outCarInfoWarning();
			} else if (JsonID.equals("GETCARINFOONCE")) {
				carInfoController.outCarInfoOnce();
			} else if (JsonID.equals("GETCARINFONONEINSTANT")) {
				carInfoController.outCarInfoNoneInstant();
			} else if (JsonID.equals("GETVOLUME")) {
				radioController.outJsonToApp();
			} else if (JsonID.equals("DIALPHONE")) {
				jsonparas = new JSONObject(jsonobject.getString("DIALPHONE"));
				String phoneNumber = jsonparas.getString("PHONENUMBER");
				RadioCmd_DialPhone(phoneNumber);
			} else if (JsonID.equals("HANGUPPHONE")) {
				jsonparas = new JSONObject(jsonobject.getString("HANGUPPHONE"));
				String phoneNumber = jsonparas.getString("PHONENUMBER");
				RadioCmd_HangupPhone(phoneNumber);
			} else if (JsonID.equals("INITRADIOINFO")) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				radioController.outJsonToApp();
				radioController.outStationLists();
				updatePadConnection();
				radioController.seekStation();
				radioController.changeToRadioMode();
				// radioController.RadioCmd_AdjustVolume(radioController
				// .getRadioInfo().getVol());

			} else if (JsonID.equals("PADRECONNECTED")) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				radioController.changeToRadioMode();
				radioController.seekStation();
			} else if (JsonID.equals("CHANGETOAMCHANNEL")) {
				radioController.changeToAmChannel();
			} else if (JsonID.equals("CHANGETOFMCHANNEL")) {
				radioController.changeToFmChannel();
			}else if (JsonID.equals("UPDATEHFPSTATU")) {
				jsonparas = new JSONObject(jsonobject.getString("UPDATEHFPSTATU"));
				String hfpStatu = jsonparas.getString("HFPSTATU");
				radioController.hfpStatu=Integer.valueOf(hfpStatu);
				Log.e("1111", "hfpstatu from bluetoothphone "+hfpStatu);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return ("");
	}

	// 拨打电话
	private void RadioCmd_DialPhone(String phoneNumber) {
		byte[] cmd = new byte[2 + phoneNumber.length()];
		cmd[0] = 0x40;
		cmd[1] = 0x01;
		for (int i = 0; i < phoneNumber.length(); i++) {
			cmd[i + 2] = (byte) (Integer.valueOf(
					phoneNumber.substring(i, i + 1)).intValue() + 0x30);
		}
		OutMcu(cmd);
	}

	// 挂断电话
	private void RadioCmd_HangupPhone(String phoneNumber) {
		byte[] cmd = new byte[2 + phoneNumber.length()];
		cmd[0] = 0x40;
		cmd[1] = 0x00;
		for (int i = 0; i < phoneNumber.length(); i++) {
			cmd[i + 2] = (byte) (Integer.valueOf(
					phoneNumber.substring(i, i + 1)).intValue() + 0x30);
		}
		OutMcu(cmd);
	}

	/* 接收主程序的传来的MCU Status数据 */
	public void InMcu(byte[] mcustatus) {
		switch (mcustatus[0]) {

			case 0x62:

			case 0x64:

			case 0x65:

			case 0x67:

				radioController.parseCmdInMcu(mcustatus);
				break;
			case 0x66:
				// GPS
				gpsController.parseCmdInMcu(mcustatus);
				break;

			case 0x68:
				// 接收handshake source key
				byte[] cmd = new byte[17];
				cmd[0] = 0x42;
				byte[] byte1 = { mcustatus[1], mcustatus[3], mcustatus[5],
						mcustatus[7] };
				int result1 = mCommonUtil.JSHash(byte1, 4);
				cmd[1] = (byte) ((result1 >> 24) & 0x00ff);
				cmd[2] = (byte) ((result1 >> 16) & 0x00ff);
				cmd[3] = (byte) ((result1 >> 8) & 0x00ff);
				cmd[4] = (byte) (result1 & 0x00ff);
				byte[] byte2 = { mcustatus[9], mcustatus[11], mcustatus[13],
						mcustatus[15] };
				int result2 = mCommonUtil.JSHash(byte2, 4);
				cmd[5] = (byte) ((result2 >> 24) & 0x00ff);
				cmd[6] = (byte) ((result2 >> 16) & 0x00ff);
				cmd[7] = (byte) ((result2 >> 8) & 0x00ff);
				cmd[8] = (byte) (result2 & 0x00ff);
				byte[] byte3 = { mcustatus[2], mcustatus[4], mcustatus[6],
						mcustatus[8] };
				int result3 = mCommonUtil.JSHash(byte3, 4);
				cmd[9] = (byte) ((result3 >> 24) & 0x00ff);
				cmd[10] = (byte) ((result3 >> 16) & 0x00ff);
				cmd[11] = (byte) ((result3 >> 8) & 0x00ff);
				cmd[12] = (byte) (result3 & 0x00ff);
				byte[] byte4 = { mcustatus[10], mcustatus[12], mcustatus[14],
						mcustatus[16] };
				int result4 = mCommonUtil.JSHash(byte4, 4);
				cmd[13] = (byte) ((result4 >> 24) & 0x00ff);
				cmd[14] = (byte) ((result4 >> 16) & 0x00ff);
				cmd[15] = (byte) ((result4 >> 8) & 0x00ff);
				cmd[16] = (byte) (result4 & 0x00ff);
				OutMcu(cmd);
				break;

			case 0x69:
				Log.e(TAG, "0x69 succsess");
				// 接收handshake ack
				if (mcustatus[1] == 1) {
					notifyPadConnection(1);
					outPadConnect(true);
					updatePadReConnection();
					radioController.RadioCmd_EnterAuxMode(3);// 初始化为media模式
					radioController.RadioCmd_AdjustVolume(radioController
							.getRadioInfo().getVol());
				} else if (mcustatus[1] == 0) {
					notifyPadConnection(0);
					outPadConnect(false);
				}
				break;

			case 0x70:
			case 0x71:
			case 0x72:
			case 0x73:
				carInfoController.parseCmdInMcu(mcustatus);
				break;

			default:
				break;
		}
	}



	int times = 0;
	Runnable setVolRunnable = new Runnable() {

		@Override
		public void run() {
			times++;
//			radioController.RadioCmd_AdjustVolume(radioController
//					.getRadioInfo().getVol());
//			radioController.seekStation();
			radioController.RadioCmd_EnterAuxMode(1);
			if (times >= 12) {
				times = 0;
				return;
			} else {
				mHandler.postDelayed(setVolRunnable, 10000);
			}

		}
	};

	public void saveRadioInfoPref() {
		radioController.saveRadioInfoPref();
	}

	/* 通过主程序来发送给底层SOCKET数据 */
	private void OutJson(String jsonstr) {
		if (this.onJsonOutput != null) {
			this.onJsonOutput.outputjson(jsonstr);
		} else {
			Log.e(TAG, "Error Json String, null! ");
		}
	}

	/* 通过主程序来发送给MCU CMD数据 */
	private void OutMcu(byte[] mcucmd) {
		if (this.onJsonOutput != null) {
			this.onJsonOutput.outputMcuCmd(mcucmd);
		} else {
			Log.e(TAG, "Error Json String, null! ");
		}
	}

	public void setOnJsonOutput(OnJsonOutput onJsonOutput) {
		this.onJsonOutput = onJsonOutput;
	}

	public interface OnJsonOutput {
		public void outputjson(String jsonstr);

		public void outputMcuCmd(byte[] mcucmd);
	}

	public interface OnPadConnectListener {
		public void onPadConnect(boolean isConnected);
	}

	public void setOnPadConnectListener(OnPadConnectListener listener) {
		this.onPadConnectListener = listener;
	}

	public void outPadConnect(boolean isConnected) {
		if (this.onPadConnectListener != null) {
			this.onPadConnectListener.onPadConnect(isConnected);
		}
		// 通知radio 如果正在搜台，则停止， 需解耦！！！
		if (!isConnected && radioController.isSearching())
			radioController.stopSeekStation();
	}

}
