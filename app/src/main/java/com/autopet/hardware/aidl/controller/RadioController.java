package com.autopet.hardware.aidl.controller;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.autopet.hardware.aidl.customview.volume.VolumeView.OnVolumeChangeListener;
import com.autopet.hardware.aidl.customview.volume.VolumeView.onMuteChangeListener;
import com.autopet.hardware.aidl.customview.volume.VolumeViewHelper;
import com.autopet.hardware.aidl.tboxinfo.RadioInfo;
import com.autopet.hardware.aidl.tboxinfo.TBoxInfo;
import com.autopet.hardware.aidl.util.AppManager;
import com.autopet.hardware.aidl.util.DataUtil;

public class RadioController extends TBoxController {
	private RadioInfo radioInfo;
	private Handler mHandler;
	private Context mContext;
	private DataUtil dataUtil;
	private VolumeViewHelper volumeViewHelper;
	private int tempFrq = FM_MIN_FREQUENCY;
	private boolean seekOrPreset;
	private boolean isInitOk;
	private static final int FM_MIN_FREQUENCY = 875;
	private static final int FM_MAX_FREQUENCY = 1080;// 100khz
	private static final int AM_MIN_FREQUENCY = 531;
	private static final int AM_MAX_FREQUENCY = 1602;// khz

	public int hfpStatu = 0;

	private String TAG = this.getClass().getName();
	private SearchState curSearchState, fmAutoSearchState, fmStepUpSearchState,
			fmStepDownSearchState, fmSeekUpSearchState, fmSeekDownSearchState,
			fmNoneSearchState, amAutoSearchState, amStepUpSearchState,
			amStepDownSearchState, amSeekUpSearchState, amSeekDownSearchState,
			amNoneSearchState;

	private TBOXState radioState, phoneState, mediaState, lastTBOXState,
			curTBOXState;// 收音机状态,电话状态,pad状态,上次状态,当前状态

	public RadioController(Handler handler, Context context) {
		radioInfo = new RadioInfo();
		mHandler = handler;
		mContext = context;
		dataUtil = new DataUtil(mContext);
		initRadioInfo();
		initTBOXState();
		initSearchState();
		volumeViewHelper = new VolumeViewHelper(mContext);
		volumeViewHelper.setOnVolumeListener(new OnVolumeChangeListener() {

			@Override
			public void onVolumeChange(int volume) {
				RadioCmd_AdjustVolume(volume);
			}
		});
		volumeViewHelper.setOnMuteChangeListener(new onMuteChangeListener() {

			@Override
			public void onMuteChange() {
				autoMute();
			}
		});
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				isInitOk = true;
			}
		}, 3000);
	}

	private void initRadioInfo() {
		int[] pref = dataUtil.readRadioInfoPref();
		radioInfo.setBand(pref[0]);
		radioInfo.setFmFrq(pref[1]);
		radioInfo.setAmFrq(pref[2]);
		radioInfo.setVol(pref[3]);
		radioInfo.setPhoneVol(pref[4]);
		radioInfo.setMute(0);
		ArrayList<Integer> fmlist = dataUtil.readRadioFmFrqPref();
		if (fmlist.size() > 0) {
			radioInfo.setRadioFmStations(fmlist);
		}
		ArrayList<Integer> amlist = dataUtil.readRadioAmFrqPref();
		if (fmlist.size() > 0) {
			radioInfo.setRadioAmStations(amlist);
		}
	}

	private void initSearchState() {
		fmAutoSearchState = new AutoSearchState(1);
		fmStepUpSearchState = new StepUpSearchState(1);
		fmStepDownSearchState = new StepDownSearchState(1);
		fmSeekUpSearchState = new SeekUpSearchState(1);
		fmSeekDownSearchState = new SeekDownSearchState(1);
		fmNoneSearchState = new NoneSearchState(1);

		amAutoSearchState = new AutoSearchState(0);
		amStepUpSearchState = new StepUpSearchState(0);
		amStepDownSearchState = new StepDownSearchState(0);
		amSeekUpSearchState = new SeekUpSearchState(0);
		amSeekDownSearchState = new SeekDownSearchState(0);
		amNoneSearchState = new NoneSearchState(0);
		curSearchState = fmNoneSearchState;
	}

	private void initTBOXState() {
		radioState = new RadioState();
		phoneState = new PhoneState();
		mediaState = new MediaState();
		curTBOXState = radioState;// 默认情况为radioState
		lastTBOXState = radioState;

	}

	public boolean isSeekOrPreset() {
		return seekOrPreset;
	}

	public void setSeekOrPreset(boolean seekOrPreset) {
		this.seekOrPreset = seekOrPreset;
	}

	@Override
	public void parseCmdInMcu(byte[] mcustatus) {
		if (mcustatus[0] == 0x62) {
			int mode = mcustatus[1];
			int vol = mcustatus[2] & 0x7f;
			int mute = (mcustatus[2] & 0x8f) >>> 7;
			TBOXState tempState;
			// Log.e("1111", "received vol= " + vol + ",received mode= " + mode
			// + ",received mute= " + mute);
			if (isInitOk) {
				if (vol != radioInfo.getVol() || vol == 0 || vol == 31) {
					volumeViewHelper.setVolume(vol);
				}
				if (radioInfo.getMute() != mute) {
					volumeViewHelper.setMute(mute == 1 ? true : false);
				}
				if (vol == 0 && radioInfo.getVol() > 0) {
					volumeViewHelper.setMute(true);
					radioInfo.setMute(1);
				} else if (vol > 0 && radioInfo.getVol() == 0) {
					volumeViewHelper.setMute(false);
					radioInfo.setMute(0);
				}
			}
			radioInfo.setMute(mute);
			switch (mode) {
				case 0x01:
					tempState = radioState;
					radioInfo.setVol(vol);
					break;
				case 0x02:
					tempState = phoneState;
					radioInfo.setPhoneVol(vol);
					break;
				case 0x03:
					tempState = mediaState;
					radioInfo.setVol(vol);
					break;
				default:
					tempState = mediaState;
					break;
			}

			if (!curTBOXState.equals(tempState)) {
				lastTBOXState = curTBOXState;
			}
			curTBOXState = tempState;
		} else if (mcustatus[0] == 0x65) {
			// frequency 此数据在发送preset和seek命令后会有结果返回
			int freq = 0;
			if (mcustatus[4] == 0x01) {
				freq += (mcustatus[1] & 0x0f0) / 16 * 1;
				freq += (mcustatus[2] & 0x0f) / 1 * 10;
				freq += (mcustatus[2] & 0x0f0) / 16 * 100;
				freq += (mcustatus[3] & 0x0f) / 1 * 1000;
				freq += (mcustatus[3] & 0x0f0) / 16 * 10000;
			} else if (mcustatus[4] == 0x00) {
				freq += mcustatus[2] & 0x0f;
				freq += (mcustatus[2] & 0x0f0) / 16 * 10;
				freq += (mcustatus[3] & 0x0f) / 1 * 100;
				freq += (mcustatus[3] & 0x0f0) / 16 * 1000;
			}
			// Log.e("1111", "search frequency : " + freq + "  stationStatu : "
			// + mcustatus[5]);
			if ((tempBand ^ mcustatus[4]) == 1 && isChangingChannel) {
				return;
			}
			int stationStatu = mcustatus[5];
			curSearchState.setFreq(freq);
			radioInfo.setBand(mcustatus[4]);
			curSearchState.handleFrequency(freq, stationStatu);
		} else if (mcustatus[0] == 0x64) {
			int action = mcustatus[1];
			int press = mcustatus[2];
			switch (action) {
				case 0x01:
					int vol1 = radioInfo.getVol() - 1;
					if (vol1 <= 0) {
						vol1 = 0;
					}
					RadioCmd_AdjustVolume(vol1);
					break;
				case 0x02:
					int vol2 = radioInfo.getVol() + 1;
					if (vol2 >= 31) {
						vol2 = 31;
					}
					RadioCmd_AdjustVolume(vol2);
					break;
				case 0x03:
					if (hfpStatu < 3) {
						if (press == 0x01) {
							curTBOXState.seekDown();
						} else if (press == 0x02) {
							curTBOXState.stepDown();
						}
					}
					break;
				case 0x04:
					if (hfpStatu < 3) {
						if (press == 0x01) {
							curTBOXState.seekUp();
						} else if (press == 0x02) {
							curTBOXState.stepUp();
						}
					}
					break;
				case 0x05:
					if (hfpStatu < 3) {
						curTBOXState.switchToOtherApp();
					}
					break;
				case 0x06:
					autoMute();
					break;

				default:
					break;
			}

			return;
		} else if (mcustatus[0] == 0x67) {
			// 接收Telephone状态
			int phoneStatu = mcustatus[1] & 0x7f;
			if (phoneStatu == 3) {
				if (curTBOXState.equals(phoneState)) {
					restoreToPreState();// 电话已断开连接，需恢复之前状态
				}
			} else if (phoneStatu == 1 || phoneStatu == 2) {
				if (!curTBOXState.equals(phoneState)) {
					changeToPhoneMode();// 电话正在连接状态，切换到电话状态
				}
			}
			return;
		}
		outJsonToApp();
	}

	@Override
	public void outJsonToApp() {
		Message message = new Message();
		message.getData().putString("TBOXINFO",
				TBoxInfoToJson(radioInfo, "RADIOINFO"));
		message.what = 1;
		mHandler.sendMessage(message);
	}

	@Override
	public void outCmdToMcu(byte[] cmd) {
		Message msg = new Message();
		msg.getData().putByteArray("TBOXCMD", cmd);
		msg.what = 2;
		mHandler.sendMessage(msg);
	}

	private void outStationList(String key, ArrayList<Integer> stationlist) {
		Message message = new Message();
		message.getData().putString("TBOXINFO",
				radioInfo.StalionListToJson(key, stationlist));
		message.what = 1;
		mHandler.sendMessage(message);
	}

	public void outStationList() {
		if (radioInfo.getBand() == 0) {
			((AutoSearchState) fmAutoSearchState).outStationJson();
		} else if (radioInfo.getBand() == 1) {
			((AutoSearchState) amAutoSearchState).outStationJson();
		}
	}

	public void outStationLists() {
		((AutoSearchState) fmAutoSearchState).outStationJson();
		((AutoSearchState) amAutoSearchState).outStationJson();
	}

	public void outEnforceStopAutoSearch() {
		Log.d(TAG, "enforce stop search station!");
		Message message = new Message();
		message.getData().putString("TBOXINFO",
				radioInfo.EnforceStopAutoSearchToJson());
		message.what = 1;
		mHandler.sendMessage(message);
	}

	public RadioInfo getRadioInfo() {
		if (radioInfo == null) {
			radioInfo = new RadioInfo();
		}
		return radioInfo;
	}

	// 搜台
	private void RadioCmd_Seek(int frq, int band) {
		int freq = frq;
		if (band == 0) {
			freq = frq * 10;
		}
		seekOrPreset = true;
		byte[] cmd = new byte[6];
		cmd[0] = 0x22;
		cmd[1] = 0x01;
		cmd[2] = (byte) (freq % 10 * 16);
		cmd[3] = (byte) (freq % 1000 / 100 * 16 + freq % 100 / 10);
		cmd[4] = (byte) (freq / 10000 * 16 + freq % 10000 / 1000);
		cmd[5] = (byte) band;
		outCmdToMcu(cmd);
	}

	// Radio Preset
	private void RadioCmd_Preset(int frq, int band) {
		int freq = frq;
		if (band == 0) {
			freq = frq * 10;
		}
		seekOrPreset = false;
		byte[] cmd = new byte[6];
		cmd[0] = 0x22;
		cmd[1] = 0x00;
		cmd[2] = (byte) (freq % 10 * 16);
		cmd[3] = (byte) (freq % 1000 / 100 * 16 + freq % 100 / 10);
		cmd[4] = (byte) (freq / 10000 * 16 + freq % 10000 / 1000);
		cmd[5] = (byte) band;
		outCmdToMcu(cmd);
	}

	// 调整音量
	public void RadioCmd_AdjustVolume(int volume) {
		byte[] cmd = new byte[4];
		cmd[0] = 0x27;
		cmd[1] = 0x01;
		cmd[2] = 0x00;
		cmd[3] = (byte) volume;
		outCmdToMcu(cmd);
	}

	// 静音
	public void RadioCmd_Mute() {
		byte[] cmd = new byte[4];
		cmd[0] = 0x27;
		cmd[1] = 0x01;
		cmd[2] = 0x01;
		cmd[3] = (byte) radioInfo.getVol();
		outCmdToMcu(cmd);
	}

	// 切换声道模式 0x01:radio 0x02:phone 0x03:media
	public void RadioCmd_EnterAuxMode(int aux) {
		// Log.e(TAG, "set mode "+aux);
		byte[] cmd = new byte[2];
		cmd[0] = 0x26;
		switch (aux) {
			case 1:
				cmd[1] = 0x01;
				break;
			case 2:
				cmd[1] = 0x02;
				break;
			case 3:
				cmd[1] = 0x03;
				break;
			default:
				cmd[1] = 0x03;
				break;
		}
		outCmdToMcu(cmd);
	}

	public void mute() {
		RadioCmd_Mute();
	}

	public void autoMute() {
		if (radioInfo.getVol() == 0) {
			RadioCmd_AdjustVolume(5);
			return;
		}
		if (radioInfo.getMute() == 1) {
			unMute();
		} else if (radioInfo.getMute() == 0) {
			RadioCmd_Mute();
		}
	}

	public void unMute() {
		RadioCmd_AdjustVolume(radioInfo.getVol());
	}

	public interface OnSystemStateChangeListener {
		public void onVolumeChange(int vol);

		public void onModeChange(int mode);
	}

	public void saveRadioInfoPref() {
		dataUtil.saveRadioInfoPref(radioInfo.getFmFrq(), radioInfo.getAmFrq(),
				radioInfo.getBand(), radioInfo.getVol(),
				radioInfo.getPhoneVol());
	}

	public void saveRadioFrqPref(int band) {
		if (band == 0) {
			dataUtil.saveRadioAmFrqPref(radioInfo.getRadioAmStations());
		} else if (band == 1) {
			dataUtil.saveRadioFmFrqPref(radioInfo.getRadioFmStations());
		}
	}

	/***************************************************************/
	/* Radio Search State */
	/***************************************************************/

	public class SearchState {
		protected int receivedFreq, receivedStationStatu;
		protected boolean isSearching = false;
		protected boolean isCurrentFreq = true;
		protected boolean isResent = false;
		protected final int TOTAL_THIS_TIME_OUT_COUNT = 60;// 本次超时重发，共60次，每次5ms，共300ms
		protected int currentTimeCount;
		protected final int TOTAL_TIME_OUT_COUNT = 120;// 超时重发，若本次freq连续搜索2次，仍无响应，则搜索下次频率
		protected final int minFrequency;
		protected final int maxFrequency;
		protected final int band;// 0 AM,1 FM
		protected final int stepSize;// 步进大小，fm 1*100KHZ ,am 9KHZ

		public SearchState(int band) {
			this.band = band;
			if (band == 1) {
				minFrequency = FM_MIN_FREQUENCY;
				maxFrequency = FM_MAX_FREQUENCY;
				stepSize = 1;
			} else if (band == 0) {
				minFrequency = AM_MIN_FREQUENCY;
				maxFrequency = AM_MAX_FREQUENCY;
				stepSize = 9;
			} else {
				throw new NullPointerException(
						"wrong band ! band must be 0 or 1");
			}
		}

		public void changeToAutoSearchState() {
			curSearchState.stopSeekStation();
			curSearchState.changeToAutoSearchState();
		}

		public void changeToSeekUpSearchState() {
			curSearchState.stopSeekStation();
			curSearchState.changeToSeekUpSearchState();
		}

		public void changeToSeekDownSearchState() {
			curSearchState.stopSeekStation();
			curSearchState.changeToSeekDownSearchState();
		}

		public void changeToStepUpSearchState() {
			curSearchState.stopSeekStation();
			curSearchState.changeToStepUpSearchState();
		}

		public void changeToStepDownSearchState() {
			curSearchState.stopSeekStation();
			curSearchState.changeToStepDownSearchState();
		}

		public void handleFrequency(int freq, int stationStatu) {
			stopSeekStation();
		}

		public void seekStation() {

		}

		public void stopSeekStation() {
			resetThisState();
			if (band == 0) {
				setSearchState(amNoneSearchState);
			} else if (band == 1) {
				setSearchState(fmNoneSearchState);
			}
		}

		protected void resetThisState() {
			isSearching = false;
			isResent = false;
			isCurrentFreq = true;
			currentTimeCount = 0;
			sleep(50);
			// sleep(100);
		}

		public void startSeekStation() {
			isSearching = true;
		}

		public void changeToAnotherBand() {
			stopSeekStation();
		}

		public int getFreq() {
			if (band == 1) {
				return radioInfo.getFmFrq();
			} else if (band == 0) {
				return radioInfo.getAmFrq();
			} else {
				throw new NullPointerException(
						"wrong band ! band must be 0 or 1");
			}
		}

		public void setFreq(int freq) {
			if (band == 1) {
				radioInfo.setFmFrq(freq);
			} else if (band == 0) {
				radioInfo.setAmFrq(freq);
			} else {
				throw new NullPointerException(
						"wrong band ! band must be 0 or 1");
			}
		}

		protected void sleep(int millSeconds) {
			try {
				Thread.sleep(millSeconds);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public int getBand() {
			return band;
		}

		protected void timeOutResent() {
			Log.d(TAG, "resent seek command !");
			currentTimeCount++;
			if (currentTimeCount == TOTAL_THIS_TIME_OUT_COUNT) {
				isResent = true;
				setResentFreq(isResent);
				seekStation();
			} else if (currentTimeCount == TOTAL_TIME_OUT_COUNT) {
				isResent = false;
				setResentFreq(isResent);
				seekStation();
			}
		}

		protected void setResentFreq(boolean isResent) {
			if (isResent) {
				Log.d(TAG, "resent seek current freq !");
			} else {
				Log.d(TAG, "continue seek next freq !");
			}
		}

	}

	public class AutoSearchState extends SearchState {

		public AutoSearchState(int band) {
			super(band);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void handleFrequency(int freq, int stationStatu) {
			if (freq == getFreq()) {
				receivedFreq = freq;
				receivedStationStatu = stationStatu;
				isCurrentFreq = true;
				isResent = false;
				currentTimeCount = 0;
			} else {
				isCurrentFreq = false;// 若收到错误的freq，直接屏蔽掉？需测试！！！
			}
		}

		@Override
		public void seekStation() {
			if (!isResent) {
				currentTimeCount = 0;
			}
			if (getFreq() >= maxFrequency) {
				Log.d(TAG, "search radio success");
				autoStopSearchStation();
			} else {
				RadioCmd_Seek(getFreq(), band);
			}
		}

		public void autoStopSearchStation() {
			setFreq(tempFrq);
			stopSeekStation();
			if (getRadioStations().size() > 0) {
				saveRadioFrqPref(band);
				outStationJson();
				RadioCmd_Preset(getRadioStations().get(0), band);
			} else {
				RadioCmd_Preset(getFreq(), band);
			}
		}

		@Override
		public void startSeekStation() {
			super.startSeekStation();
			new SeekThread().start();
		}

		@Override
		public void stopSeekStation() {
			outEnforceStopAutoSearch();
			super.stopSeekStation();
		}

		@Override
		public void changeToAutoSearchState() {
			Log.d(TAG, "isAlready autoSearching!!!");
		}

		class SeekThread extends Thread {
			@Override
			public void run() {
				tempFrq = getFreq();
				setFreq(minFrequency);
				getRadioStations().clear();
				RadioCmd_Seek(getFreq(), band);
				while (isSearching) {

					try {
						sleep(5);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if (isCurrentFreq) {
						if (seekOrPreset) {
							if (getFreq() == receivedFreq) {
								if (receivedStationStatu > 0) {
									RadioCmd_Preset(receivedFreq, band);
								} else {
									setFreq(getFreq() + stepSize);
									seekStation();
								}
							} else {
								timeOutResent();
							}
						} else {
							if (receivedStationStatu > 0) {
								if (!getRadioStations().contains(receivedFreq)) {
									getRadioStations().add(receivedFreq);
								}
								try {
									sleep(2000);
									setFreq(getFreq() + stepSize);
									seekStation();// 在当前电台停留2s，再往下继续搜台
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}

						}
					} else {
						timeOutResent();
					}
				}
			}
		}

		@Override
		protected void setResentFreq(boolean isResent) {
			super.setResentFreq(isResent);
			if (!isResent) {
				setFreq(getFreq() + stepSize);
			}
		}

		protected ArrayList<Integer> getRadioStations() {
			if (band == 1) {
				return radioInfo.getRadioFmStations();
			} else if (band == 0) {
				return radioInfo.getRadioAmStations();
			} else {
				throw new NullPointerException("band must be 0 or 1!");
			}
		}

		public void outStationJson() {
			if (band == 1) {
				if (radioInfo.getRadioFmStations().size() == 0) {
					if (dataUtil.readRadioFmFrqPref().size() > 0) {
						radioInfo.setRadioFmStations(dataUtil
								.readRadioFmFrqPref());
					} else {
						return;
					}
				}
				outStationList("FMSTATIONSLIST", radioInfo.getRadioFmStations());
			} else if (band == 0) {
				if (radioInfo.getRadioAmStations().size() == 0) {
					if (dataUtil.readRadioAmFrqPref().size() > 0) {
						radioInfo.setRadioAmStations(dataUtil
								.readRadioAmFrqPref());
					} else {
						return;
					}
				}
				outStationList("AMSTATIONSLIST", radioInfo.getRadioAmStations());
			} else {
				throw new NullPointerException("band must be 0 or 1!");
			}
		}
	}

	public class StepUpSearchState extends SearchState {

		public StepUpSearchState(int band) {
			super(band);
		}

		@Override
		public void seekStation() {
			if (getFreq() < maxFrequency) {
				setFreq(getFreq() + stepSize);
			} else if (getFreq() == maxFrequency) {
				setFreq(minFrequency);
			}
			RadioCmd_Preset(getFreq(), band);
			stopSeekStation();
		}

		@Override
		public void startSeekStation() {
			super.startSeekStation();
			seekStation();
		}

		@Override
		public void stopSeekStation() {
			super.stopSeekStation();
			outJsonToApp();
		}
	}

	public class StepDownSearchState extends SearchState {

		public StepDownSearchState(int band) {
			super(band);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void seekStation() {
			if (getFreq() > minFrequency) {
				setFreq(getFreq() - stepSize);
			} else if (getFreq() == minFrequency) {
				setFreq(maxFrequency);
			}
			RadioCmd_Preset(getFreq(), band);
			stopSeekStation();
		}

		@Override
		public void startSeekStation() {
			super.startSeekStation();
			seekStation();
		}

		@Override
		public void stopSeekStation() {
			super.stopSeekStation();
			outJsonToApp();
		}

	}

	public class SeekUpSearchState extends SearchState {

		public SeekUpSearchState(int band) {
			super(band);
			// TODO Auto-generated constructor stub
		}

		private boolean isStartSeeking = false;// 是否已开始搜索，防止从875 1080搜索会陷入死循环

		@Override
		public void handleFrequency(int freq, int stationStatu) {

			if (freq == getFreq()) {
				receivedFreq = freq;
				receivedStationStatu = stationStatu;
				isCurrentFreq = true;
				currentTimeCount = 0;
				isResent = false;
			} else {
				isCurrentFreq = false;
			}
		}

		@Override
		public void seekStation() {
			if (!isResent) {
				currentTimeCount = 0;
			}
			if (getFreq() < maxFrequency) {
				if ((getFreq() + stepSize) == tempFrq) {
					RadioCmd_Preset(tempFrq, band);
					setFreq(tempFrq);
					tempFrq = minFrequency;
					return;
				} else {
					if (tempFrq > minFrequency) {
						setFreq(getFreq() + stepSize);
					} else {
						if (isStartSeeking) {
							if (getFreq() == minFrequency) {
								RadioCmd_Preset(tempFrq, band);
								tempFrq = minFrequency;
								isStartSeeking = false;
								return;
							} else {
								setFreq(getFreq() + stepSize);
							}
						} else {
							isStartSeeking = true;
							setFreq(getFreq() + stepSize);
						}
					}
				}
			} else {
				setFreq(minFrequency);
			}
			RadioCmd_Seek(getFreq(), band);

		}

		@Override
		public void startSeekStation() {
			super.startSeekStation();
			isStartSeeking = false;
			new SeekThread().start();
		}

		@Override
		public void stopSeekStation() {
			isStartSeeking = false;
			super.stopSeekStation();
		}

		@Override
		public void changeToSeekUpSearchState() {
			Log.d(TAG, "isAlready SeekUpSearching!!!");
		}

		class SeekThread extends Thread {
			@Override
			public void run() {
				tempFrq = getFreq();
				seekStation();
				while (isSearching) {
					try {
						sleep(5);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if (isCurrentFreq) {
						if (seekOrPreset) {
							if (getFreq() == receivedFreq) {
								if (receivedStationStatu > 0) {
									RadioCmd_Preset(receivedFreq, band);
									stopSeekStation();
								} else {
									seekStation();
								}
							} else {
								timeOutResent();
							}
						} else {
							stopSeekStation();
						}
					} else {
						timeOutResent();
					}

				}
			}
		}

		@Override
		protected void setResentFreq(boolean isResent) {
			super.setResentFreq(isResent);
			if (isResent) {
				setFreq(getFreq() - stepSize);
			}
		}

	}

	public class SeekDownSearchState extends SearchState {

		public SeekDownSearchState(int band) {
			super(band);
			// TODO Auto-generated constructor stub
		}

		private boolean isStartSeeking = false;// 是否已开始搜索，防止从875 1080搜索会陷入死循环

		@Override
		public void handleFrequency(int freq, int stationStatu) {
			if (freq == getFreq()) {
				receivedFreq = freq;
				receivedStationStatu = stationStatu;
				isCurrentFreq = true;
				isResent = false;
				currentTimeCount = 0;
			} else {
				isCurrentFreq = false;
			}
		}

		@Override
		public void seekStation() {
			if (!isResent) {
				currentTimeCount = 0;
			}
			if (getFreq() > minFrequency) {
				if ((getFreq() - stepSize) == tempFrq) {
					RadioCmd_Preset(tempFrq, band);
					setFreq(tempFrq);
					tempFrq = minFrequency;
					return;
				} else {
					if (tempFrq < maxFrequency) {
						setFreq(getFreq() - stepSize);
					} else {
						if (isStartSeeking) {
							if (getFreq() == maxFrequency) {
								RadioCmd_Preset(maxFrequency, band);
								tempFrq = minFrequency;
								isStartSeeking = false;
								return;
							} else {
								setFreq(getFreq() - stepSize);
							}
						} else {
							isStartSeeking = true;
							setFreq(getFreq() - stepSize);
						}
					}
				}
			} else {
				setFreq(maxFrequency);
			}
			RadioCmd_Seek(getFreq(), band);
		}

		@Override
		public void startSeekStation() {
			super.startSeekStation();
			isStartSeeking = false;
			new SeekThread().start();
		}

		@Override
		public void stopSeekStation() {
			isStartSeeking = false;
			super.stopSeekStation();
		}

		@Override
		public void changeToSeekDownSearchState() {
			Log.d(TAG, "isAlready SeekDownSearching!!!");
		}

		class SeekThread extends Thread {
			@Override
			public void run() {
				tempFrq = getFreq();
				seekStation();
				while (isSearching) {

					try {
						sleep(5);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if (isCurrentFreq) {
						if (seekOrPreset) {
							if (getFreq() == receivedFreq) {
								if (receivedStationStatu > 0) {
									RadioCmd_Preset(receivedFreq, band);
									stopSeekStation();
								} else {
									seekStation();
								}
							} else {
								timeOutResent();
							}
						} else {
							stopSeekStation();

						}
					} else {
						timeOutResent();
					}

				}
			}
		}

		@Override
		protected void setResentFreq(boolean isResent) {
			super.setResentFreq(isResent);
			if (isResent) {
				setFreq(getFreq() + stepSize);
			}
		}
	}

	public class NoneSearchState extends SearchState {

		public NoneSearchState(int band) {
			super(band);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void changeToAutoSearchState() {
			sleep(150);
			if (band == 1) {
				setSearchState(fmAutoSearchState);
			} else if (band == 0) {
				setSearchState(amAutoSearchState);
			}
			curSearchState.startSeekStation();
		}

		@Override
		public void changeToSeekUpSearchState() {
			sleep(150);
			if (band == 1) {
				setSearchState(fmSeekUpSearchState);
			} else if (band == 0) {
				setSearchState(amSeekUpSearchState);
			}
			curSearchState.startSeekStation();
		}

		@Override
		public void changeToSeekDownSearchState() {
			sleep(150);
			if (band == 1) {
				setSearchState(fmSeekDownSearchState);
			} else if (band == 0) {
				setSearchState(amSeekDownSearchState);
			}
			curSearchState.startSeekStation();
		}

		@Override
		public void changeToStepUpSearchState() {
			sleep(50);
			if (band == 1) {
				setSearchState(fmStepUpSearchState);
			} else if (band == 0) {
				setSearchState(amStepUpSearchState);
			}
			curSearchState.startSeekStation();
		}

		@Override
		public void changeToStepDownSearchState() {
			sleep(50);
			if (band == 1) {
				setSearchState(fmStepDownSearchState);
			} else if (band == 0) {
				setSearchState(amStepDownSearchState);
			}
			curSearchState.startSeekStation();
		}

		@Override
		public void changeToAnotherBand() {
			sleep(2000);
			curSearchState.stopSeekStation();
			if (band == 1) {
				radioInfo.setBand(0);
				setSearchState(amNoneSearchState);
			} else if (band == 0) {
				radioInfo.setBand(1);
				setSearchState(fmNoneSearchState);
			}
			curSearchState.startSeekStation();
		}

		@Override
		public void seekStation() {
			sleep(20);// 必须加上睡眠延时且大于5s,当预置电台时,由于是从上个搜索状态切换过来,上个状态停止搜索后,isSearching变量改变时,seekThread线程中会有5ms的延时睡眠时间,因此若为在此加延时的话，preset电台会改变seekOrPreset值变量为false,导致进入seekThread的seekOrPreset分支,从而调用上个状态的stopSeekStation方法，从而导致curState会发生变化,影响后续事件！！
			RadioCmd_Preset(getFreq(), band);
		}

	}

	private void setSearchState(SearchState state) {
		curSearchState = state;
	}

	public void changeToAutoSearchState() {
		curSearchState.changeToAutoSearchState();
	}

	public void changeToSeekUpSearchState() {
		curSearchState.changeToSeekUpSearchState();
	}

	public void changeToSeekDownSearchState() {
		curSearchState.changeToSeekDownSearchState();
	}

	public void changeToStepUpSearchState() {
		curSearchState.changeToStepUpSearchState();
	}

	public void changeToStepDownSearchState() {
		curSearchState.changeToStepDownSearchState();
	}

	public void changeToAnotherBand() {
		curSearchState.changeToAnotherBand();
	}

	private boolean isChangingChannel;// 是否正在切换频道，若是，则屏蔽上个频道发过来的freq
	private int tempBand = 2;// 0 am 1 fm 2默认值

	public void changeToFmChannel() {
		isChangingChannel = true;
		tempBand = 1;
		curSearchState.stopSeekStation();
		setSearchState(fmNoneSearchState);
		curSearchState.seekStation();
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				isChangingChannel = false;// 1s后状态应该切换完成，故重置该状态，以免影响后续事件
				tempBand = 2;
			}
		}, 1000);
	}

	public void changeToAmChannel() {
		isChangingChannel = true;
		tempBand = 0;
		curSearchState.stopSeekStation();
		setSearchState(amNoneSearchState);
		curSearchState.seekStation();
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				isChangingChannel = false;
				tempBand = 2;
			}
		}, 1000);
	}

	public void stopSeekStation() {
		curSearchState.stopSeekStation();
	}

	public void startSeekStation() {
		curSearchState.startSeekStation();
	}

	public void stopAutoSearchStation() {
		if (radioInfo.getBand() == 1) {
			((AutoSearchState) fmAutoSearchState).autoStopSearchStation();
		} else if (radioInfo.getBand() == 0) {
			((AutoSearchState) amAutoSearchState).autoStopSearchStation();
		}
	}

	public boolean isSearching() {
		return curSearchState.isSearching;
	}

	public int getBand() {
		return radioInfo.getBand();
	}

	public void setBand(int band) {
		radioInfo.setBand(band);
	}

	public void seekStation(int freq) {
		stopSeekStation();
		curSearchState.setFreq(freq);
		curSearchState.seekStation();
	}

	public void seekStation() {
		if (curSearchState.band == 1) {
			fmNoneSearchState.seekStation();
		} else if (curSearchState.band == 0) {
			amNoneSearchState.seekStation();
		}
	}

	/***************************************************************/
	/* Radio Mode State */
	/***************************************************************/
	private static final String ACTION_NEXT_SONG = "com.autopet.hardware.aidl.nextsong";
	private static final String ACTION_PRE_SONG = "com.autopet.hardware.aidl.presong";

	private static final String PACKAGE_NAME_CPADRADIO = "com.anyonavinfo.cpad.cpadfmradio";
	private static final String PACKAGE_NAME_NAVIGATION = "com.wedrive.welink.sgmw.navigation";
	private static final String PACKAGE_NAME_BLUETOOTHPHONE = "com.anyonavinfo.bluetoothphone";
	private static String PACKAGE_NAME_CARMUSIC = "com.anyonavinfo.musicplayer";
	private static String[] PACKAGE_NAMES = { PACKAGE_NAME_CPADRADIO,
			PACKAGE_NAME_CARMUSIC, PACKAGE_NAME_NAVIGATION,
			PACKAGE_NAME_BLUETOOTHPHONE };
	private static int currentAppId = 0;

	public interface TBOXState {
		public void changeToRadioMode();

		public void changeToPhoneMode();

		public void changeToMediaMode();

		public void restoreToPreState();

		public void switchToOtherApp();

		public void seekUp();

		public void seekDown();

		public void stepUp();

		public void stepDown();
	}

	public class MediaState implements TBOXState {

		@Override
		public void changeToRadioMode() {
			RadioCmd_EnterAuxMode(1);
			Log.d(TAG, "MediaState had to change to RadioState");
		}

		@Override
		public void changeToPhoneMode() {
			RadioCmd_EnterAuxMode(2);
			RadioCmd_AdjustVolume(radioInfo.getPhoneVol());
			Log.d(TAG,
					"MediaState had to change to PhoneState,volume had to change to phoneVol!");

		}

		@Override
		public void changeToMediaMode() {
			Log.d(TAG, "MediaState doesn't need to change to MediaState");
			RadioCmd_EnterAuxMode(3);
		}

		@Override
		public void restoreToPreState() {
			if (lastTBOXState.equals(radioState)) {
				// changeToRadioMode();
			} else if (lastTBOXState.equals(mediaState)) {
				Log.d(TAG, "RadioState doesn't need to restore to MediaState");
			} else {
				Log.d(TAG, "RadioState doesn't need to restore to PhoneState");
			}
		}

		@Override
		public void switchToOtherApp() {
			switch (currentAppId) {
				case 1:
				case 2:
					if (!AppManager.launchActivity(mContext,
							PACKAGE_NAMES[++currentAppId])) {
						switchToOtherApp();
					}
					break;
				default:
					currentAppId = 0;
					if (AppManager.launchActivity(mContext,
							PACKAGE_NAMES[currentAppId])) {
						changeToRadioMode();
					}
					break;
			}

		}

		@Override
		public void seekUp() {
			Intent intent = new Intent();
			intent.setAction(ACTION_NEXT_SONG);
			mContext.sendBroadcast(intent);
		}

		@Override
		public void seekDown() {
			Intent intent = new Intent();
			intent.setAction(ACTION_PRE_SONG);
			mContext.sendBroadcast(intent);
		}

		@Override
		public void stepUp() {
			// TODO Auto-generated method stub

		}

		@Override
		public void stepDown() {
			// TODO Auto-generated method stub

		}

	}

	public class RadioState implements TBOXState {

		@Override
		public void changeToRadioMode() {
			Log.d(TAG, "RadioState doesn't need to change to RadioState");
			RadioCmd_EnterAuxMode(1);
		}

		@Override
		public void changeToPhoneMode() {
			RadioCmd_EnterAuxMode(2);
			RadioCmd_AdjustVolume(radioInfo.getPhoneVol());
			Log.d(TAG,
					"RadioState had to change to PhoneState,volume had to change to phoneVol!");

		}

		@Override
		public void changeToMediaMode() {
			RadioCmd_EnterAuxMode(3);
			Log.e(TAG, "RadioState had to change to MediaState");

		}

		@Override
		public void restoreToPreState() {
			if (lastTBOXState.equals(mediaState)) {
				// changeToMediaMode();
			} else if (lastTBOXState.equals(radioState)) {
				Log.d(TAG, "RadioState doesn't need to restore to RadioState");
			} else {
				Log.d(TAG, "RadioState doesn't need to restore to PhoneState");
			}

		}

		@Override
		public void switchToOtherApp() {
			changeToMediaMode();
			currentAppId = 0;
			if (AppManager.launchActivity(mContext,
					PACKAGE_NAMES[++currentAppId])) {
			} else if (AppManager.launchActivity(mContext,
					PACKAGE_NAMES[++currentAppId])) {
			} else if (AppManager.launchActivity(mContext,
					PACKAGE_NAMES[++currentAppId])) {
			} else {
				changeToRadioMode();
			}

		}

		@Override
		public void seekUp() {
			changeToSeekUpSearchState();
		}

		@Override
		public void seekDown() {
			changeToSeekDownSearchState();
		}

		@Override
		public void stepUp() {
			changeToStepUpSearchState();
		}

		@Override
		public void stepDown() {
			changeToStepDownSearchState();
		}

	}

	public class PhoneState implements TBOXState {

		@Override
		public void changeToRadioMode() {
			RadioCmd_EnterAuxMode(1);
			RadioCmd_AdjustVolume(radioInfo.getVol());
			Log.d(TAG,
					"PhoneState had to change to RadioState,volume had to change to RadioVol!");

		}

		@Override
		public void changeToPhoneMode() {
			Log.d(TAG, "PhoneState doesn't need to change to PhoneState");

		}

		@Override
		public void changeToMediaMode() {
			RadioCmd_EnterAuxMode(3);
			RadioCmd_AdjustVolume(radioInfo.getVol());
			Log.d(TAG,
					"PhoneState had to change to MediaState,volume had to change to RadioVol!");
		}

		@Override
		public void restoreToPreState() {
			if (lastTBOXState.equals(mediaState)) {
				changeToMediaMode();
				RadioCmd_AdjustVolume(radioInfo.getVol());
			} else if (lastTBOXState.equals(radioState)) {
				changeToRadioMode();
				RadioCmd_AdjustVolume(radioInfo.getVol());
			} else {
				Log.d(TAG, "PhoneState doesn't need to restore the PhoneState");
			}

		}

		@Override
		public void switchToOtherApp() {
			// TODO Auto-generated method stub

		}

		@Override
		public void seekUp() {
			Log.d(TAG, "TBOX is calling ! can't to seekUp !");

		}

		@Override
		public void seekDown() {
			Log.d(TAG, "TBOX is calling ! can't to seekDown !");

		}

		@Override
		public void stepUp() {
			// TODO Auto-generated method stub

		}

		@Override
		public void stepDown() {
			// TODO Auto-generated method stub

		}

	}

	public void setTBOXState(TBOXState state) {
		this.curTBOXState = state;
	}

	public void changeToRadioMode() {
		curTBOXState.changeToRadioMode();
	}

	public void changeToPhoneMode() {
		curTBOXState.changeToPhoneMode();
	}

	public void changeToMediaMode() {
		curTBOXState.changeToMediaMode();
	}

	public void restoreToPreState() {
		curTBOXState.restoreToPreState();
	}

	public TBOXState getCurTBOXState() {
		return curTBOXState;
	}

	public TBOXState getRadioState() {
		return radioState;
	}

	public void setRadioOriginVol() {
		int[] pref = dataUtil.readRadioInfoPref();
		RadioCmd_AdjustVolume(pref[3]);
	}

}
