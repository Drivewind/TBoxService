package com.autopet.hardware.aidl.imxserial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;

import com.autopet.hardware.aidl.util.CommonUtil;
import com.autopet.hardware.aidl.util.FileUtils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class UartConnect {
	static final String TAG = "E3HWService";
	static final String TAG1 = "UartConnect";
	OnDataReceiver onDataReceiver = null;
	FileOutputStream mOutputStream;
	FileInputStream mInputStream;
	SerialPort sp;
	UartReadThread mUartReadThread;
	private static DispQueueThread queneThread;

	protected byte[] cmdbuffer = new byte[256];
	protected int mRxCMDcnt = 0;
	protected int FRAMELENGTH = 0;
	int i;

	void LOG(String a, String b) {
		Log.d(a, b);
	}

	public void close() {
		try {

			if (mUartReadThread != null) {
				mUartReadThread.interrupt();// refer to think in java 706
				mUartReadThread = null;
			}
			if (queneThread != null) {
				queneThread.interrupt();
				queneThread = null;
			}
		} catch (Exception e) {

		}

	}

	public UartConnect(String devicename, int baudrate, int flags, int len) {

		try {
			sp = new SerialPort(new File(devicename), baudrate, flags);
			mOutputStream = (FileOutputStream) sp.getOutputStream();
			mInputStream = (FileInputStream) sp.getInputStream();
			mUartReadThread = new UartReadThread();
			mUartReadThread.setName("UartReadThread");
			mUartReadThread.start();
			queneThread = new DispQueueThread();
			queneThread.setName("DispQueueThread");
			queneThread.start();
			FRAMELENGTH = len;

		} catch (SecurityException e) {

			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void SendData(int mLen, byte[] mData) {
		byte[] sData = new byte[mLen];
		for (int i = 0; i < mLen; i++) {
			sData[i] = mData[i];
		}
		try {
			mOutputStream.write(sData);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void SendData(String mStr) {
		try {
			mOutputStream.write(mStr.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	CommonUtil mCommonUtil = new CommonUtil();

	private class UartReadThread extends Thread {

		@Override
		public void run() {
			// super.run();
			LOG(TAG, "URAT receive routing start.");
			while (!isInterrupted()) {
				int size;
				try {
					if (mInputStream != null) {
						byte[] buffer = new byte[256];
						size = mInputStream.read(buffer);
						if (size > 0) {							
							ComBean ComRecData = new ComBean(buffer, size);
							if (queneThread != null) {
								queneThread.AddQueue(ComRecData);
							}
						}
						try {
							Thread.sleep(5);// 延时5ms
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else
						return;

				} catch (IOException e) {
					e.printStackTrace();
					LOG(TAG, "URAT received error.");
					return;
				}
			}
			LOG(TAG, "URAT receive routing quit.");
		}
	}

	void UartRcv(int size, byte[] rbuffer) {
		i = 0;

		if (size > 0) {
			while (i < size) {

				switch (mRxCMDcnt) {
				case 0:
					if (rbuffer[i] == 0x10) {
						cmdbuffer[mRxCMDcnt++] = rbuffer[i];
						// LOG(TAG, "CMDLINE BYTE 0, 0x10");
					} else {
						mRxCMDcnt = 0;
						// LOG(TAG,
						// "CMDLINE BYTE 0, NOT 0x10, cleared.");
					}

					break;
				case 1:
					if (rbuffer[i] == (byte) 0xfe) {
						cmdbuffer[mRxCMDcnt++] = rbuffer[i];
						// LOG(TAG, "CMDLINE BYTE 1, 0xfe");
					} else {
						mRxCMDcnt = 0;
						// LOG(TAG,
						// "CMDLINE BYTE 1, NOT 0xfe, cleared.");
					}
					break;

				default:

					cmdbuffer[mRxCMDcnt++] = rbuffer[i];
					if (mRxCMDcnt > 3) {
						if (mRxCMDcnt >= ((cmdbuffer[2]) + 5)) {

							// LOG(TAG, "CMDLINE BYTE LEN OK");
							if (onDataReceiver != null) {
								onDataReceiver.PutData(mRxCMDcnt, cmdbuffer);
							}
							mRxCMDcnt = 0;
						}
					}
					break;
				}

				i++;
			}
		}
	}

	private class DispQueueThread extends Thread {
		private ArrayBlockingQueue<ComBean> QueueList = new ArrayBlockingQueue<ComBean>(
				100);

		@Override
		public void run() {
			super.run();
			while (!isInterrupted()) {
				final ComBean ComData = QueueList.poll();
				if (ComData != null) {
					UartRcv(ComData.bRec.length, ComData.bRec);
				}
				try {
					Thread.sleep(5);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					Log.d(TAG, "Queue Thread Is Interrupted !!!");
				}
			}
		}

		public synchronized void AddQueue(ComBean ComData) {
			QueueList.add(ComData);
		}
	}

	public void setDataReceiver(OnDataReceiver onDataReceiver) {
		this.onDataReceiver = onDataReceiver;
	}

	public interface OnDataReceiver {
		public void PutData(int mLen, byte[] mData);
	}

}
