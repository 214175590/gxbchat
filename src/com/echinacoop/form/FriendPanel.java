package com.echinacoop.form;

import java.awt.Dimension;
import java.text.MessageFormat;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.echinacoop.Startup;
import com.echinacoop.controller.SocketService;
import com.echinacoop.modal.ChatStatus;
import com.echinacoop.modal.Response;
import com.echinacoop.utils.UserData;

public class FriendPanel extends BasePanel {

	private int width = 260;
	private int height = 600;
	private boolean loadFriendListed = false;

	public FriendPanel(int width, int height) {
		this.width = width;
		this.height = height;
		
		initCompoments();
	}

	private void initCompoments() {
		this.setLayout(null);
		
		loadFriendList();
	}

	public void loadFriendList() {
		if (!loadFriendListed) {
			new Thread() {
				public void run() {
					Response res = SocketService.loadFriendList();
					if (res.isSuccess()) {
						loadFriendListed = true;
						renderFriendList(res.getRtn().getJSONArray("data"));
					}
					while(Startup.mainWindow == null){
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
						}
					}
					showTabTitle();
					
					Startup.mainWindow.getLeftPanel().initedPanel();
				}
			}.start();
		}
	}

	public void renderFriendList(JSONArray friendList) {
		if (null != friendList) {
			JSONObject userJson = null;
			UserItem item = null;
			UserData.USER_ITEM_MAP.clear();
			int size = friendList.size();
			int heightValue = 10;
			for (int i = 0; i < size; i++) {
				userJson = friendList.getJSONObject(i);
				item = new UserItem(userJson, i);
				this.add(item);
				UserData.USER_ITEM_MAP.put(userJson.getString("userId"), item);
				heightValue += item.getHeight();
			}
			
			this.setPreferredSize(new Dimension(width - 10, heightValue));
	    	this.revalidate(); //告诉其他部件,我的宽高变了
		}
	}
	
	public void addFriend(JSONObject userJson){
		int size = UserData.USER_ITEM_MAP.size();
		int heightValue = this.getHeight();
		UserItem item = new UserItem(userJson, size);
		this.add(item);
		
		UserData.USER_ITEM_MAP.put(userJson.getString("userId"), item);
		heightValue += item.getHeight();
		this.setPreferredSize(new Dimension(width - 10, heightValue));
		this.revalidate(); //告诉其他部件,我的宽高变了
		
		showTabTitle();
	}
	
	public void removeItem(UserItem item){
		if(item.isChat()){
			Startup.mainWindow.hideChatPanel();
		}
		this.remove(item);
		UserData.USER_ITEM_MAP.remove(item.getUser().getString("userId"));
		int i = 0;
		for(Map.Entry<String, UserItem> entry : UserData.USER_ITEM_MAP.entrySet()){
			entry.getValue().setIndex(i++);
		}
		
		showTabTitle();
	}
	
	public void showTabTitle(){
		int all = UserData.USER_ITEM_MAP.size();
		int online = 0;
		JSONObject user = null;
		for (Map.Entry<String, UserItem> entry  : UserData.USER_ITEM_MAP.entrySet()) {
			user = entry.getValue().getUser();
			if(null != user){
				if(!user.getString("chatStatus").equals("offline")){
					online++;
				}
			}
		}
		Startup.mainWindow.getLeftPanel().setTabTitle(0, MessageFormat.format("({0}/{1})", online, all));
	}
	
}
