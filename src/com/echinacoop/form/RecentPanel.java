package com.echinacoop.form;

import java.awt.Dimension;
import java.text.MessageFormat;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.echinacoop.Startup;
import com.echinacoop.controller.SocketService;
import com.echinacoop.modal.ChatType;
import com.echinacoop.modal.Response;
import com.echinacoop.utils.UserData;

public class RecentPanel extends BasePanel {
	private int width = 260;
	private int height = 600;

	private boolean loadRecentListed = false;

	public RecentPanel(int width, int height) {
		this.width = width;
		this.height = height;

		initCompoments();
	}

	private void initCompoments() {
		this.setLayout(null);

		loadRecentList();
	}

	public void loadRecentList() {
		if (!loadRecentListed) {
			new Thread() {
				public void run() {
					Response res = SocketService.loadRecentList();
					if (res.isSuccess()) {
						loadRecentListed = true;
						renderRecentList(res.getRtn().getJSONArray("data"));
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

	public void renderRecentList(JSONArray friendList) {

		if (null != friendList) {
			JSONObject recent = null, userJson = null, groupJson = null;
			RecentItem item = null;
			String type = "";
			int size = friendList.size();
			int heightValue = 10;
			for (int i = 0; i < size; i++) {
				recent = friendList.getJSONObject(i);
				type = recent.getString("contacterType");
				// 01 好友，02群组
				if (type.equals("01")) {
					userJson = new JSONObject();
					userJson.put("userId", recent.getString("contacterId"));
					userJson.put("nickName", recent.getString("contacterName"));
					userJson.put("headImg", recent.getString("contacterHead"));
					userJson.put("lastChat", recent.getString("lastMsg"));
					userJson.put("chatStatus", recent.getString("chatStatus"));
					item = new RecentItem(userJson, ChatType.SINGLE_CHAT, i);
					this.add(item);
					UserData.RECENT_ITEM_MAP.put("U" + userJson.getString("userId"), item);
				} else if (type.equals("02")) {
					groupJson = new JSONObject();
					groupJson.put("groupId", recent.getString("contacterId"));
					groupJson.put("groupName", recent.getString("contacterName"));
					groupJson.put("groupImg", recent.getString("contacterHead"));
					groupJson.put("lastChat", recent.getString("lastMsg"));
					item = new RecentItem(groupJson, ChatType.GROUP_CHAT, i);
					this.add(item);
					UserData.RECENT_ITEM_MAP.put("G" + groupJson.getString("groupId"), item);
				}
				heightValue += item.getHeight();
			}
			this.setPreferredSize(new Dimension(width - 10, heightValue));
			this.revalidate(); // 告诉其他部件,我的宽高变了
		}

	}

	public void removeUserItem(String userId) {
		RecentItem item = UserData.getRecentItem(userId, ChatType.SINGLE_CHAT);
		if (null != item) {
			if(item.isChat()){
				Startup.mainWindow.hideChatPanel();
			}
			this.remove(item);
			UserData.removeRecentItem(userId, ChatType.SINGLE_CHAT);
			int i = 0;
			for (Map.Entry<String, RecentItem> entry : UserData.RECENT_ITEM_MAP.entrySet()) {
				entry.getValue().setIndex(i++);
			}
			
			showTabTitle();
		}
	}

	public void removeGroupItem(String groupId) {
		RecentItem item = UserData.getRecentItem(groupId, ChatType.GROUP_CHAT);
		if (null != item) {
			if(item.isChat()){
				Startup.mainWindow.hideChatPanel();
			}
			this.remove(item);
			UserData.removeRecentItem(groupId, ChatType.GROUP_CHAT);
			int i = 0;
			for (Map.Entry<String, RecentItem> entry : UserData.RECENT_ITEM_MAP.entrySet()) {
				entry.getValue().setIndex(i++);
			}
			
			showTabTitle();
		}
	}
	
	public void showTabTitle(){
		int size = UserData.RECENT_ITEM_MAP.size();
		Startup.mainWindow.getLeftPanel().setTabTitle(2, MessageFormat.format("({0})", size));
	}

}
