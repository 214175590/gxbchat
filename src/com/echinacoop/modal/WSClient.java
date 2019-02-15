package com.echinacoop.modal;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;

public class WSClient {

	private String userId;
	private String sid;
	private Long lastActiveTime = new Date().getTime();
	private Socket socket;

	public WSClient() {

	}

	public WSClient(Socket socket) {
		this.setSocket(socket);
	}

	public boolean sendMessage(WSData wsData) {
		boolean result = false;
		if(socket != null){
			OutputStream os = null;
			try {
				os = socket.getOutputStream();
				String body = JSONObject.toJSONString(wsData);
				os.write(body.toString().getBytes());
				os.flush();
			} catch (IOException e) {
			}			
		}
		return result;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public Long getLastActiveTime() {
		return lastActiveTime;
	}

	public void setLastActiveTime(Long lastActiveTime) {
		this.lastActiveTime = lastActiveTime;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

}
