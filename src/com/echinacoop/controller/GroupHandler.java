package com.echinacoop.controller;

import com.alibaba.fastjson.JSONObject;
import com.echinacoop.Startup;
import com.echinacoop.annotation.MessageHandler;
import com.echinacoop.annotation.Req;
import com.echinacoop.modal.WSClient;
import com.echinacoop.modal.WSData;

@MessageHandler("group")
public class GroupHandler extends BaseHandler {
	
	@Req("dissolved")
	public void dissolved(WSClient client, WSData wsData){
		JSONObject body = (JSONObject) wsData.getBody();
		if(null != body){
			String groupId = body.getString("groupId");
			Startup.mainWindow.getLeftPanel().getGroupPanel().removeGroup(groupId);
		}
	}
	
	@Req("update-info")
	public void updateGrouoInfo(WSClient client, WSData wsData){
		JSONObject body = (JSONObject) wsData.getBody();
		if(null != body){
			// {"msgType":"13","fromUid":"10000018","msgId":"80000000000","time":1496896144478,"group":{"groupName":"快带连接群","groupDesc":"王远成，胡兵ddd","groupOwner":"10000018","groupId":"10000276","groupImg":"images/group/g-002.jpg","groupLevel":0}}
			
			
		}
	}
	
}
