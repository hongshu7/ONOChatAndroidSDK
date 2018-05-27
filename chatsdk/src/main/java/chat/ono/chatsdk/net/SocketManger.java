package chat.ono.chatsdk.net;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;

import chat.ono.chatsdk.core.Packet;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


//TODO: cache handshake data
public class SocketManger implements Runnable, Callback {
	
	protected boolean connected;  //标记是否已连接
	protected boolean sending;  //标记是否运行发送线程
	protected boolean closed;   //标记是否手动关闭，如关闭便不再自动重连了
	protected Socket socket;
	protected SocketCallback callback;

	private String host;
	private int port = 0;
	private Handler handler;

	private int failTimes;
	private Timer timer;

    private TimerTask timerTask;

	private BlockingQueue<Packet> sendQueue;

	private static SocketManger instance;

	public static SocketManger getInstance() {
		if (instance == null) {
			instance = new SocketManger();
		}
		return instance;
	}

	public void setCallback(SocketCallback callback) {
		this.callback = callback;
	}

	public SocketManger() {
		handler = new Handler(this);
		Log.v("IM", "instance");
		sendQueue = new LinkedBlockingQueue<Packet>();
		timer = new Timer();

	}

	/**
	 * 连接
	 *
	 * @param host 服务器地址
	 * @param port 端口号
	 */
	public void setup(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void connect() {
		if (this.connected) {
			return;
		}
		this.closed = false;
		new Thread(this).start(); // 启动进程
	}

	public boolean isConnect() {
		return connected;
	}

	/**
	 * 关闭连接
	 */
	public void disconnect() {
		if (!this.connected) {
			return;
		}
		Log.v("IM", "disconnect...");
		this.closed = true;
		this.connected = false;
		if (this.sending) {
            Packet packet = new Packet();
            packet.setType(Packet.TYPE_KICK);
			sendQueue.add(packet);
		}
		try {
			socket.close();
		} catch (IOException e) {
			//e.printStackTrace();
		}
		Log.v("IM", "disconnected");
	}

	public void send(Packet packet) {
		sendQueue.add(packet);
	}

	private void handshake() {
		String str = "{\"sys\":{\"type\":\"android\",\"version\":\"1.0\",\"protocol\":\"protobuf\"}}";
        Packet packet = new Packet();
        packet.setType(Packet.TYPE_HANDSHAKE);
        packet.setStrings(str);
        sendSync(packet);
	}

	public void sendSync(Packet packet) {
		if (!this.connected) {
			return;
		}
        //Log.v("IM", "send packet");
		this.send(packet.encode());
	}


	private void send(byte[] data) {
		try {
			OutputStream os = socket.getOutputStream();
			os.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {

		// 连接
		SocketAddress addr = new InetSocketAddress(host, port);
		try {
			Log.v("IM", "connect to " + host + ":" + port + " ...");
			Log.v("IM", "connect...");
			socket = new Socket();
			socket.connect(addr, 2000); //最长不超过2秒，否则超时
			Log.v("IM", "connected.");

			this.connected = true;
			this.failTimes = 0;

			//握手
			handshake();

			startSendThread();

			InputStream is = socket.getInputStream();
			DataInputStream dis = new DataInputStream(is);
			while (socket.isConnected() && this.connected) {

				Packet packet = new Packet();
				packet.decode(dis);

				this.triggerCallBack(packet);
			}
		} catch (IOException e) {
			if (this.closed) {
				return;
			}
			this.connected = false;
			this.failTimes++;
			Log.v("IM", "fail times:" + this.failTimes);
            //Log.v("IM", "fail reason:" + e.getMessage());
			if (this.sending) {
                Packet packet = new Packet();
                packet.setType(Packet.TYPE_KICK);
				sendQueue.add(packet);
			}
			//重新连接
			try {
				int sleepTime = 1;
				if (this.failTimes == 1) {
					sleepTime = 3;
				} else if (this.failTimes == 2) {
					sleepTime = 5;
				} else if (this.failTimes == 3) {
					sleepTime = 10;
				} else if (this.failTimes == 4) {
					sleepTime = 15;
				} else if (this.failTimes == 5) {
					sleepTime = 20;
				} else if (this.failTimes == 6) {
					sleepTime = 25;
				} else if (this.failTimes == 7) {
					sleepTime = 30;
				} else if (this.failTimes == 8) {
					sleepTime = 60;
				} else if (this.failTimes == 9) {
					sleepTime = 120;
				} else if (this.failTimes == 10) {
					sleepTime = 300;
				} else if (this.failTimes == 11) {
					sleepTime = 600;
				} else {
					sleepTime = 1200;
				}
				Thread.sleep(sleepTime*1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			this.connect();
		}

	}

    public void startHeartbeat(int heartbeat) {

        heartbeat = heartbeat * 1000;
        Log.v("IM", "heartbeat with period:"+heartbeat);
        timerTask = new TimerTask() {
            @Override
            public void run() {
                //心跳
                //Log.v("IM", "heartbeat");
                Packet packet = new Packet();
                packet.setType(Packet.TYPE_HEARTBEAT);
                sendQueue.add(packet);
            }
        };
        timer.scheduleAtFixedRate(timerTask, heartbeat, heartbeat);
    }

	private void startSendThread() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				sending = true;
				Packet packet = null;
				// 开始发心跳（30秒一次）

				try {
					while (true) {
						packet = sendQueue.take();
						if (packet.getType() == Packet.TYPE_KICK) {
							break;
						}
						sendSync(packet);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				sending = false;
				timerTask.cancel();
			}
		}).start();

	}

	public void triggerCallBack(Packet packet) {
		if (callback != null) {
			Message msg = new Message();
			msg.obj = packet;
			handler.sendMessage(msg);
		}
	}



	@Override
	public boolean handleMessage(Message msg) {
		//转到主线程去处理消息
		Packet response = (Packet)msg.obj;
		if (callback != null) {
			callback.handleMessage(response);
		}
		return false;
	}

}
