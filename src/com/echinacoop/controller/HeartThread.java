package com.echinacoop.controller;

import com.yinsin.other.LogHelper;

public class HeartThread extends Thread {
	private static final LogHelper logger = LogHelper.getLogger(HeartThread.class);

	private int timer = 1000 * 15; // 15秒
	private boolean cut = true;
	
	@Override
	public void run() {
		
		while(cut){
			try {
				//logger.debug("发送心跳：" + DateUtils.format(new Date()));
				SocketService.sendHeart();
				
				Thread.sleep(timer);
			} catch (InterruptedException e) {
			}
		}
		
	}
	
	public void stopHeart(){
		cut = false;
	}

}
