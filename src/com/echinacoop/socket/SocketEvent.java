package com.echinacoop.socket;

import com.echinacoop.Startup;
import com.echinacoop.controller.HeartThread;
import com.echinacoop.controller.SocketService;
import com.echinacoop.modal.HandlerMethod;
import com.echinacoop.modal.WSClient;
import com.echinacoop.modal.WSData;
import com.yinsin.other.LogHelper;

public class SocketEvent implements ISocketEvent {
	private static final LogHelper logger = LogHelper.getLogger(SocketEvent.class);
	private HeartThread heart = null;

	@Override
	public boolean filter(WSClient client, WSData wsData) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onMessage(WSClient client, WSData wsData) {
		logger.debug("收到消息：" + wsData.getUrl() + "=>" + wsData.getBody());
		boolean called = callOnMessage(client, wsData);
		if(!called){
			//
		}
	}

	@Override
	public void onClose(WSClient client) {
		logger.debug("连接关闭：" + client.getSid());
		Startup.connectionStatus = false;
		SocketService.socket = null;
		Startup.showLoginWindow();
		if(heart != null){
			heart.stopHeart();
			heart = null;
		}
	}

	@Override
	public void onError(Throwable e) {
		logger.debug("连接断开：" + e.getMessage());
		Startup.connectionStatus = false;
		SocketService.socket = null;
		if(heart != null){
			heart.stopHeart();
			heart = null;
		}
	}

	@Override
	public void onOpen(WSClient client) {
		logger.debug("连接成功：" + client.getSid());
		Startup.connectionStatus = true;
		Startup.loginForm.showMessage("服务器连接成功，请登录", null);
	}
	
	private boolean callOnMessage(WSClient client, WSData pack) {
		boolean called = false;
		String url = pack.getUrl();
		if (url != null) {
			url = url.replaceAll("/", ".");
		}
		HandlerMethod handMethod = HandleHelper.getHandlerMethod(url);
		if (null != handMethod) {
			try {
				handMethod.getMethod().invoke(handMethod.getInstance(), new Object[] { client, pack });
				called = true;
				if(url.equals("user.login")){
					if(heart == null){
						heart = new HeartThread();
						heart.start();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			logger.debug(url + " => 没有处理函数");
		}
		return called;
	}

}
