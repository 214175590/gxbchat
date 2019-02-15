package com.echinacoop.controller;

import java.awt.Color;

import com.alibaba.fastjson.JSONObject;
import com.echinacoop.Startup;
import com.echinacoop.annotation.MessageHandler;
import com.echinacoop.annotation.Req;
import com.echinacoop.consts.Constants;
import com.echinacoop.db.SqlHelper;
import com.echinacoop.form.RecentItem;
import com.echinacoop.form.UserItem;
import com.echinacoop.modal.ChatHistory;
import com.echinacoop.modal.WSClient;
import com.echinacoop.modal.WSData;
import com.echinacoop.utils.UserData;
import com.yinsin.utils.CommonUtils;

@MessageHandler("user")
public class LoginHandler extends BaseHandler {
	
	@Req("login")
	public void login(WSClient client, WSData wsData){
		
		JSONObject body = (JSONObject) wsData.getBody();
		if(isSuccess(body)){
			UserData.user.put("sid", body.getJSONObject("data").getString("sid"));
			//Startup.loginForm.showMessage("登录成功，" + UserData.user.getString("nickName"), null);
			Startup.hideLoginWindow();
			Startup.showMainWindow();
		} else {
			Startup.loginForm.showMessage("登录失败：" + body.getString("msg"), Color.RED);
		}
	}
	
	@Req("change-state")
	public void changeState(WSClient client, WSData wsData){
		JSONObject body = (JSONObject) wsData.getBody();
		if(body != null){
			String userId = body.getString("fromUid");
			String chatStatus = body.getString("userStatus");
			UserItem item = UserData.USER_ITEM_MAP.get(userId);
			if(null != item){
				item.setStatus(chatStatus);
				item.flushStatus();
			}
			RecentItem reItem = UserData.RECENT_ITEM_MAP.get("U" + userId);
			if(null != reItem){
				reItem.setStatus(chatStatus);
				reItem.flushStatus();
			}
			Startup.mainWindow.getLeftPanel().getGroupPanel().changeUserState(userId, chatStatus);
		}
	}
	
	@Req("join-group")
	public void joinGroup(WSClient client, WSData wsData){
		JSONObject body = (JSONObject) wsData.getBody();
		if(body != null){
			String groupId = body.getString("groupId");
			JSONObject user = body.getJSONObject("user");
			if(null != groupId && null != user){
				Startup.mainWindow.getLeftPanel().getGroupPanel().addUser(groupId, user);
			}
		}
	}
	
	@Req("exit-group")
	public void exitGroup(WSClient client, WSData wsData){
		JSONObject body = (JSONObject) wsData.getBody();
		if(body != null){
			String groupId = body.getString("groupId");
			String userId = body.getString("userId");
			if(null != groupId && null != userId){
				Startup.mainWindow.getLeftPanel().getGroupPanel().removeUser(groupId, userId);
			}
		}
	}
	
	@Req("pulled-group")
	public void pulledGroup(WSClient client, WSData wsData){
		JSONObject body = (JSONObject) wsData.getBody();
		if(body != null){
			JSONObject group = body.getJSONObject("group");
			if(null != group ){
				Startup.mainWindow.getLeftPanel().getGroupPanel().addNewGroup(group.getString("groupId"));
			}
		}
	}
	
	/** 被邀请入群消息 */
	@Req("invitation-group")
	public void invitationGroup(WSClient client, WSData wsData){
		JSONObject body = (JSONObject) wsData.getBody();
		if(body != null){
			ChatHistory his = new ChatHistory();
			his.setRowId(CommonUtils.getUUID());
			his.setFromId(body.getString("fromUid"));
			his.setGroupId(body.getJSONObject("group").getString("groupId"));
            his.setToId(UserData.user.getString("userId"));
            his.setReqUrl(wsData.getUrl());
            his.setMsgType(body.getString("msgType"));
            his.setMsgContent(body.toJSONString());
            his.setStatus(Constants.MSG_READSTATE_NO);
            
            SqlHelper.insertChatHistory(his);
		}
	}
	
}
