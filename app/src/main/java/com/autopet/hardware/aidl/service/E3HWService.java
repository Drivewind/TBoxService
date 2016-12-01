package com.autopet.hardware.aidl.service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.autopet.hardware.aidl.IHWCallBack;
import com.autopet.hardware.aidl.IHWSendCmd;
import com.autopet.hardware.aidl.hal.PetMsgRadioHAL;
import com.autopet.hardware.aidl.customview.update.UpdateServiceHelper;
import com.autopet.hardware.aidl.util.HttpUtils;
import com.autopet.hardware.aidl.util.LogcatHelper;
import com.autopet.hardware.aidl.imxserial.UartConnect;

public class E3HWService extends Service {
	private static final String TAG = "E3HWService";
	private StartThread startThread = null;
	private static UartConnect uartConnect = null;// 同queneThread
	private LogcatHelper logcatHelper = null;
	private Handler mHandler;
	private ExecutorService linkToTBoxThreadPool;
	private SentRadioCommandThread sentRadioCommandThread = null;
	private TBoxMsgThreadManager  tBoxMsgThreadManager = null;
	private Runnable mRunnable = new Runnable() {

		@Override
		public void run() {
			radiohal.notifyPadConnection();
			radiohal.saveRadioInfoPref();
			mHandler.postDelayed(mRunnable, 5000);
		}

	};

	PetMsgRadioHAL radiohal;

	private void printf(String LogStr) {
		Log.v(TAG, LogStr);
	}

	@Override
	public IBinder onBind(Intent t) {
		initOnBind();
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		initOnUnBind();
		return true;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	public void onRebind(Intent intent) {
		initOnBind();
		super.onRebind(intent);
	}

	private void initOnBind() {
		if (mHandler == null) {
			mHandler = new Handler();
		}
		if (mRunnable != null) {
			mHandler.postDelayed(mRunnable, 5000);
		}
	}

	private void initOnUnBind() {
		if (mHandler == null) {
			mHandler = new Handler();
		}
		if (mRunnable != null) {
			mHandler.removeCallbacks(mRunnable);
		}

	}

	private void CallBackJson(String newStatus) {
		synchronized (E3HWService.class) {
			final int N = mCallbacks.beginBroadcast();
			try {
				for (int i = 0; i < N; i++) {
					mCallbacks.getBroadcastItem(i).updateStatus(newStatus);
				}
			} catch (RemoteException e) {
				// The RemoteCallbackList will take care of removing
				// the dead object for us.
				Log.e(TAG, "RemoteException is happend: " + e.getMessage());
			} finally {
				mCallbacks.finishBroadcast();
			}

		}
	}

	void CallBackMcu(byte[] McuStatus) {
		int mLen = McuStatus.length;
		byte[] cmd = new byte[mLen + 4];

		cmd[0] = 0x10;
		cmd[1] = (byte) 0xfe;
		cmd[2] = (byte) (mLen - 1);
		cmd[mLen + 3] = (byte) 0xff;

		for (int i = 0; i < mLen; i++)
			cmd[i + 3] = McuStatus[i];

		RadioCmd(mLen + 4, cmd);
	}

	private final IHWSendCmd.Stub mBinder = new IHWSendCmd.Stub() {

		public void sendCommand(String NewCommand) {
			if (!NewCommand.substring(2, 9).equals("TESTING")) {
				// Log.d(TAG, "CLIENT received:" + NewCommand);
				radiohal.InJson(NewCommand);
			}
		}

		public String getStatus(String StatusType) {
			if (!StatusType.substring(2, 9).equals("TESTING")) {
				// Log.d(TAG, "CLIENT received:" + StatusType);
				radiohal.InJson(StatusType);
			}
			return (StatusType);
		}

		public void registerCallback(IHWCallBack cb) {
			if (cb != null) {
				mCallbacks.register(cb);
			}
		}

		public void unregisterCallback(IHWCallBack cb) {
			if (cb != null) {
				mCallbacks.unregister(cb);
			}
		}
	};

	// private static 同queneThread + 对于onDestroy的处理
	private static final RemoteCallbackList<IHWCallBack> mCallbacks = new RemoteCallbackList<IHWCallBack>();

	@Override
	public void onCreate() {
		super.onCreate();
		printf("service on Created:");
		logcatHelper = LogcatHelper.getInstance(this);
		logcatHelper.start();
		radiohal = new PetMsgRadioHAL(this);
		radiohal.setOnJsonOutput(new PetMsgRadioHAL.OnJsonOutput() {
			@Override
			public void outputjson(String jsonstr) {
				CallBackJson(jsonstr);
			}

			public void outputMcuCmd(byte[] mcucmd) {
				CallBackMcu(mcucmd);
			}
		});
		radiohal.setOnPadConnectListener(new PetMsgRadioHAL.OnPadConnectListener() {

			@Override
			public void onPadConnect(boolean isConnected) {
				if (!isConnected) {
					uartConnect.close();
					startThread = null;
					try {
						Thread.sleep(300);
						startThread = new StartThread();
						linkToTBoxThreadPool.execute(startThread);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		linkToTBoxThreadPool = new ThreadPoolExecutor(0, 20, 0,
				TimeUnit.SECONDS, new SynchronousQueue<Runnable>());
		startThread = new StartThread();
		linkToTBoxThreadPool.execute(startThread);
		sentRadioCommandThread = new SentRadioCommandThread();
		sentRadioCommandThread.setName("SentRadioCommandThread");
		sentRadioCommandThread.start();
		tBoxMsgThreadManager = new TBoxMsgThreadManager();		
		tBoxMsgThreadManager.start();
		if (mHandler == null) {
			mHandler = new Handler();
		}
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				if (HttpUtils.isWifiConnected(getApplicationContext())) {
					new UpdateServiceHelper(getApplicationContext())
							.checkServiceUpdate();
				}
			}
		}, 6000);

	}

	@Override
	public void onDestroy() {
		printf("service on destroy");
		super.onDestroy();
		startService(new Intent(E3HWService.this, E3HWService.class));
		logcatHelper.stop();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		printf("service onStart");
	}

	// 发送Handshake Request
	private void RadioCmd_SentHandshakeRequest() {
		byte[] cmd = new byte[1];
		cmd[0] = 0x41;
		CallBackMcu(cmd);
	}

	private void RadioCmd(int mLen, byte[] mData) {
		mData[mLen - 1] = 0;
		for (int i = 2; i < mLen - 1; i++) {
			mData[mLen - 1] ^= mData[i];
		}
		sentRadioCommandThread.AddQueue(mData);
	}

	private void RadioData(int mLen, byte[] mData) {
		byte checksum = 0;
		for (int i = 2; i < mLen - 1; i++) {
			checksum ^= mData[i];
		}
		if (checksum == mData[mLen - 1]) {
			byte[] mcudata = new byte[mData[2] + 1];
			for (int i = 0; i < mcudata.length; i++) {
				mcudata[i] = mData[i + 3];
			}
			tBoxMsgThreadManager.addQueue(mcudata);
		}

	}

	// TODO 检测pad是否连接上，插入后初始化。。
	class StartThread extends Thread {
		@Override
		public void run() {
			uartConnect = new UartConnect("/dev/ttyMT3", 115200, 0, 8);
			UartConnect.OnDataReceiver rcvdata = new UartConnect.OnDataReceiver() {

				@Override
				public void PutData(int mLen, byte[] mData) {
					// All Uart Data process
					// 每次接受到数据就记录下接受时间
					radiohal.setLastReceiveDataTime(System.currentTimeMillis());
					RadioData(mLen, mData);
				}

			};
			uartConnect.setDataReceiver(rcvdata);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			RadioCmd_SentHandshakeRequest();
		}
	}

	class SentRadioCommandThread extends Thread {
		private ArrayBlockingQueue<byte[]> QueueList = new ArrayBlockingQueue<byte[]>(
				100);

		@Override
		public void run() {
			while (!isInterrupted()) {

				final byte[] radioCommand = QueueList.poll();
				if (radioCommand != null && radioCommand.length > 0) {
					try {
						if (uartConnect != null) {
							uartConnect.SendData(radioCommand.length,
									radioCommand);
							Thread.sleep(30);
						}
					} catch (Exception e) {
						e.printStackTrace();
						Log.e(TAG, "Radio USB not connect.");
						radiohal.notifyPadConnection(0);
					}
				}
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		public synchronized void AddQueue(byte[] command) {
			QueueList.add(command);
		}
	}

	class TBoxMsgThreadManager {
		TBoxMsgThread radioThread, otherThread;

		TBoxMsgThreadManager() {
			radioThread = new TBoxMsgThread();
			radioThread.setName("radioDataThread");			
			otherThread = new TBoxMsgThread();
			otherThread.setName("otherThread");			
		}
		private void start(){
			radioThread.start();
			otherThread.start();
		}
		private void stop(){
			radioThread.interrupt();
			otherThread.interrupt();
		}

		class TBoxMsgThread extends Thread {
			private ArrayBlockingQueue<byte[]> queueList = new ArrayBlockingQueue<byte[]>(
					100);

			@Override
			public void run() {
				while (!isInterrupted()) {
					try {
						final byte[] tboxMsg = queueList.poll();
						if (tboxMsg != null && tboxMsg.length > 0) {
							radiohal.InMcu(tboxMsg);
						}
						Thread.sleep(5);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			public synchronized void addQueue(byte[] tboxMsg) {
				queueList.add(tboxMsg);
			}
		}

		public synchronized void addQueue(byte[] tboxMsg) {
			if (tboxMsg[0] == 0x65) {
				radioThread.addQueue(tboxMsg);
			} else {
				otherThread.addQueue(tboxMsg);
			}
		}

	}

}
