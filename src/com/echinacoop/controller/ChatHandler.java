package com.echinacoop.controller;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.echinacoop.Startup;
import com.echinacoop.annotation.MessageHandler;
import com.echinacoop.annotation.Req;
import com.echinacoop.consts.Constants;
import com.echinacoop.db.SqlHelper;
import com.echinacoop.form.FacingGroupDialog;
import com.echinacoop.form.GroupItem;
import com.echinacoop.form.RecentItem;
import com.echinacoop.form.UserItem;
import com.echinacoop.modal.ChatHistory;
import com.echinacoop.modal.ChatType;
import com.echinacoop.modal.WSClient;
import com.echinacoop.modal.WSData;
import com.echinacoop.utils.UserData;
import com.yinsin.other.LogHelper;
import com.yinsin.utils.CommonUtils;

@MessageHandler("chat")
public class ChatHandler extends BaseHandler {
	private static final LogHelper logger = LogHelper.getLogger(ChatHandler.class);
	
	@Req("single-chat")
	public void singleChat(WSClient client, WSData wsData){
		JSONObject body = (JSONObject) wsData.getBody();
		String fromUid = body.getString("fromUid");
		boolean isMySend = false;
		if(null == fromUid){
			fromUid = body.getString("to");
			body.put("time", System.currentTimeMillis());
			isMySend = true;
		}
		// 增加到记录中
		List<WSData> datas = UserData.USER_CHAT_RECORD.get(fromUid);
		if(null == datas){
			datas = new ArrayList<WSData>();
			UserData.USER_CHAT_RECORD.put(fromUid, datas);
		}
		datas.add(wsData);
		
		ChatHistory his = new ChatHistory();
		his.setRowId(CommonUtils.getUUID());
		his.setReqUrl(wsData.getUrl());
		his.setGroupId("");
		his.setMsgContent(body.toJSONString());
		his.setStatus(Constants.MSG_READSTATE_YES);
		if(!body.containsKey("code")){
			his.setMsgType(body.getString("msgType"));
			his.setFromId(body.getString("fromUid"));
			his.setToId(UserData.user.getString("userId"));
		} else {
			his.setMsgType("01");
			his.setFromId(UserData.user.getString("userId"));
			his.setToId(body.getString("to"));
		}
		
		UserItem item = UserData.USER_ITEM_MAP.get(fromUid);
		RecentItem item2 = UserData.getRecentItem(fromUid, ChatType.SINGLE_CHAT);
		if(null != item){
			// 判断当前是否为聊天
			if(item.isChat()){
				// 显示聊天信息
				Startup.mainWindow.getChatPanel().showMessage(wsData, true);
			} else if(null != item2 && item2.isChat()){
				// 显示聊天信息
				Startup.mainWindow.getChatPanel().showMessage(wsData, true);
				item2.showLastChat(wsData);
			} else if(!isMySend){
				// 显示数字
				item.showCount(true);
			}
			item.showLastChat(wsData);
		} else if(null != item2){
			// 判断当前是否为聊天
			if(item2.isChat()){
				// 显示聊天信息
				Startup.mainWindow.getChatPanel().showMessage(wsData, true);
			} else {
				// 显示数字
				item2.showCount(true);
			}
			item2.showLastChat(wsData);
		}  // 陌生人的不显示
		
		//logger.debug(Startup.mainFrame.getState());
		SqlHelper.insertChatHistory(his);
		
	}
	
	
	@Req("group-chat")
	public void groupChat(WSClient client, WSData wsData){
		logger.debug("===>" + JSONObject.toJSONString(wsData));	
		JSONObject body = (JSONObject) wsData.getBody();
		String groupId = body.getString("groupId");
		boolean isMySend = false;
		if(groupId == null){
			groupId = body.getString("toGroup");
			body.put("time", System.currentTimeMillis());
			isMySend = true;
		}
		// 增加到记录中
		List<WSData> datas = UserData.GROUP_CHAT_RECORD.get(groupId);
		if(null == datas){
			datas = new ArrayList<WSData>();
			UserData.GROUP_CHAT_RECORD.put(groupId, datas);
		}
		datas.add(wsData);
		ChatHistory his = new ChatHistory();
		his.setRowId(CommonUtils.getUUID());
		his.setReqUrl(wsData.getUrl());
		his.setMsgContent(body.toJSONString());
		his.setStatus(Constants.MSG_READSTATE_YES);
		if(!body.containsKey("code")){
			his.setMsgType(body.getString("msgType"));
			his.setGroupId(body.getString("groupId"));
			his.setToId(UserData.user.getString("userId"));
			his.setFromId(body.getString("fromUid"));
		} else {
			his.setMsgType("02");
			his.setGroupId(body.getString("toGroup"));
			his.setToId("");
			his.setFromId(UserData.user.getString("userId"));
		}
		
		GroupItem item = UserData.GROUP_ITEM_MAP.get(groupId);
		RecentItem item2 = UserData.getRecentItem(groupId, ChatType.GROUP_CHAT);
		if(null != item){
			// 判断当前是否为聊天
			if(item.isChat()){
				// 显示聊天信息
				Startup.mainWindow.getChatPanel().showMessage(wsData, true);
			} else if(null != item2 && item2.isChat()){
				// 显示聊天信息
				Startup.mainWindow.getChatPanel().showMessage(wsData, true);
				item2.showLastChat(wsData);
			} else if(!isMySend){
				// 显示数字
				item.showCount(true);
			}
			item.showLastChat(wsData);
		} else if(null != item2){
			if(item2.isChat()){
				// 显示聊天信息
				Startup.mainWindow.getChatPanel().showMessage(wsData, true);
			} else {
				// 显示数字
				item2.showCount(true);
			}
			item2.showLastChat(wsData);
		}
		
		SqlHelper.insertChatHistory(his);
		
	}
	
	@Req("upload-file")
	public void uploadFileProcess(WSClient client, WSData wsData){
		JSONObject body = (JSONObject) wsData.getBody();
		if(null != body){
			int percent = body.getIntValue("percent");
			if(percent >= 100){
				/*String fileCode = body.getString("fileCode");
				String filePath = fileCode + UserData.PIC_SUFFIX.get(fileCode);
				UploadType type = UserData.PIC_UPLOAD_TYPE.get(fileCode);
				if(type == UploadType.CHAT_PIC){
					Startup.mainWindow.getChatPanel().sendTextPic(filePath);
				} else if(type == UploadType.USER_HEAD){
					Response res = SocketService.updateUserHead(filePath);
					if(res.isSuccess()){
						Startup.mainWindow.getHeadPanel().updateHead(filePath);
					}
				}
				UserData.PIC_SUFFIX.remove(fileCode);
				UserData.PIC_UPLOAD_TYPE.remove(fileCode);*/
			}
		}
		
	}
	
	/** 请求加好友消息 */
	@Req("friend")
	public void chatFriend(WSClient client, WSData wsData){
		JSONObject body = (JSONObject) wsData.getBody();
		if(null != body){
			if(!body.containsKey("code")){
				ChatHistory his = new ChatHistory();
				his.setRowId(CommonUtils.getUUID());
				his.setFromId(body.getJSONObject("fromUser").getString("userId"));
				his.setToId(UserData.user.getString("userId"));
				his.setGroupId("");
				his.setReqUrl(wsData.getUrl());
				his.setMsgType(body.getString("msgType"));
				his.setMsgContent(body.toJSONString());
				his.setStatus(Constants.MSG_READSTATE_NO);
				
				SqlHelper.insertChatHistory(his);
			}
		}
	}
	
	@Req("friendship")
	public void chatFriendship(WSClient client, WSData wsData){
		JSONObject body = (JSONObject) wsData.getBody();
		if(null != body){
			JSONObject user = new JSONObject();
			user.put("userId", body.get("friendUserId"));
			user.put("nickName", body.get("friendUserName"));
			user.put("headImg", body.get("friendUserHead"));
			user.put("groupId", body.get("friendGroupId"));
			user.put("chatStatus", body.get("friendChatStatus"));
			Startup.mainWindow.getLeftPanel().getFriendPanel().addFriend(user);
		}
		
	}
	
	@Req("facing-user")
	public void facingUser(WSClient client, WSData wsData){
		JSONObject body = (JSONObject) wsData.getBody();
		if(null != body){
			JSONArray users = body.getJSONArray("users");
			if(users != null){
				FacingGroupDialog dialog = Startup.mainWindow.getChatPanel().getFacingDialog();
				if(null != dialog){
					dialog.renderUserList(users);
				}
			}
		}
	}
	
	@Req("facing-group")
	public void facingGroup(WSClient client, WSData wsData){
		JSONObject body = (JSONObject) wsData.getBody();
		if(null != body){
		}
	}
	
	/** 申请入群消息 */
	@Req("join-group")
	public void joinGroup(WSClient client, WSData wsData){
		JSONObject body = (JSONObject) wsData.getBody();
		if(null != body){
			ChatHistory his = new ChatHistory();
			his.setRowId(CommonUtils.getUUID());
			his.setFromId(body.getJSONObject("fromUser").getString("userId"));
			his.setGroupId(body.getString("groupId"));
            his.setToId(UserData.user.getString("userId"));
            his.setReqUrl(wsData.getUrl());
            his.setMsgType(body.getString("msgType"));
            his.setMsgContent(body.toJSONString());
            his.setStatus(Constants.MSG_READSTATE_NO);
            
            SqlHelper.insertChatHistory(his);
		}
	}
}
