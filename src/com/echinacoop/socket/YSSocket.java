package com.echinacoop.socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;
import java.util.UUID;

import com.alibaba.fastjson.JSONObject;
import com.echinacoop.Startup;
import com.echinacoop.modal.WSClient;
import com.echinacoop.modal.WSData;
import com.echinacoop.utils.MessageUtils;
import com.yinsin.other.LogHelper;
import com.yinsin.utils.ByteUtils;

public class YSSocket {
	private static final LogHelper logger = LogHelper.getLogger(YSSocket.class);

	private Socket socket = null;
	private ISocketEvent event;
	private WSClient client;
	private Thread socketThread;

	public boolean register(String ip, int port, ISocketEvent event) {
		boolean isright = true;
		try {
			this.socket = new Socket(ip, port);

			this.event = event;
			client = new WSClient(this.socket);
			client.setSid(this.socket.getRemoteSocketAddress().toString());
			String uuid = UUID.randomUUID().toString();
			client.setSid(uuid.replaceAll("-", ""));

			socketThread = new Thread(new YSSocketThread(this));
			socketThread.start();

			this.event.onOpen(client);
		} catch (IOException e) {
			isright = false;
			logger.error("注册Socket服务失败：" + e.getMessage(), e);
			if (this.event != null) {
				this.event.onError(e);
			} else {
				Startup.loginForm.disabledLogin();
			}
		}
		return isright;
	}

	public void close() throws IOException {
		this.socket.close();
	}

	public ISocketEvent getSocketEvent() {
		return event;
	}

	public WSClient getClient() {
		return client;
	}

	public Socket getSocket() {
		return socket;
	}

	public Thread getSocketThread() {
		return socketThread;
	}

	public boolean sendMessage(WSData WSData) {
		boolean isok = true;
		try {
			client.sendMessage(WSData);
		} catch (Exception e) {
			isok = false;
			logger.error("发送消息时异常：" + e.getMessage(), e);
		}
		return isok;
	}

	class YSSocketThread implements Runnable {
		private Socket socket;
		private WSClient client;
		private ISocketEvent event;
		private InputStream is;
		private boolean cut = true;

		public YSSocketThread(YSSocket soc) {
			this.client = soc.getClient();
			this.socket = soc.getSocket();
			this.event = soc.getSocketEvent();
		}

		@Override
		public void run() {
			try {
				int i = -1, size = 62;
				if (!this.socket.isClosed()) {
					this.is = this.socket.getInputStream();
					byte[] byt = (byte[]) null;
					byte[] tempByt = null;
					boolean readFinished = true;
					String text = null;
					while (this.cut) {
						if (!readFinished) {
							try {
								text = new String(tempByt);
								JSONObject.parseObject(text);

								response(tempByt);
								tempByt = null;
								readFinished = true;
							} catch (Exception ex) {
							}
						}
						byt = new byte[size];
						// 堵塞 读取数据
						i = this.is.read(byt);
						if (i != -1) {
							if (i == size) {
								if (tempByt == null) {
									tempByt = byt;
								} else {
									tempByt = ByteUtils.joinByteArray(tempByt, byt);
								}
								readFinished = false;
							} else {
								if (tempByt == null) {
									tempByt = ByteUtils.getByte(byt, i);
								} else {
									tempByt = ByteUtils.joinByteArray(tempByt, ByteUtils.getByte(byt, i));
								}

								response(tempByt);
								tempByt = null;
								readFinished = true;
							}
						} else {
							this.event.onClose(client);
							this.cut = false;
						}
					}
				}
			} catch (IOException e) {
				this.event.onClose(client);
				this.cut = false;
			}

		}

		private void response(byte[] tempByt) {
			WSData pack = null;
			byte[] msgByt = null;
			String text = "";
			try {
				msgByt = ByteUtils.copyByteArray(tempByt);

				text = new String(msgByt);

				TcpPolicyThread.sendPolicyFile(this.socket, text);

				List<JSONObject> messages = MessageUtils.parseMessage(text);
				if(null != messages && messages.size() > 0){
					for (JSONObject json : messages) {
						pack = new WSData(json.getString("url"), json.getJSONObject("body"));
						
						if (this.event.filter(client, pack)) {
							this.event.onMessage(client, pack);
						}
					}
				}
			} catch (Exception e) {
				logger.error("接收消息异常：" + client.getUserId() + " =>" + text, e);
			}
			pack = null;
			msgByt = null;
		}
	}

	private class _YSSocket {

	}
}
