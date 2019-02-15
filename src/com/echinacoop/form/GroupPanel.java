package com.echinacoop.form;

import java.awt.Dimension;
import java.text.MessageFormat;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.echinacoop.Startup;
import com.echinacoop.controller.SocketService;
import com.echinacoop.modal.Response;
import com.echinacoop.utils.UserData;

public class GroupPanel extends BasePanel {
	private int width = 260;
	private int height = 600;
	private boolean loadGroupListed = false;
	
	public GroupPanel(int width, int height){
		this.width = width;
		this.height = height;
		
		initCompoments();
	}
	
	private void initCompoments(){
    	this.setLayout(null);
    	
    	loadGroupList();
    }
	
	public void reloadGroupList(){
		loadGroupListed = false;
		loadGroupList();
	}
	
	public void loadGroupList(){
		if(!loadGroupListed){
			new Thread() {
				public void run() {
					Response res = SocketService.loadGroupList();
					if(res.isSuccess()){
						loadGroupListed = true;
						renderGroupList(res.getRtn().getJSONArray("data"));
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
	
	public void addNewGroup(String groupId){
		try {
			GroupItem item = UserData.GROUP_ITEM_MAP.get(groupId);
			if(null == item){
				Response res = SocketService.getGroupInfoByGroupId(groupId);
				if (res.isSuccess()) {
					JSONObject groupJson = res.getDataForRtn();
					int size = UserData.GROUP_ITEM_MAP.size();
					int heightValue = this.getHeight();
					item = new GroupItem(groupJson, size);
					this.add(item);
					UserData.GROUP_ITEM_MAP.put(groupJson.getString("groupId"), item);
					heightValue += item.getHeight();
					
					this.setPreferredSize(new Dimension(width - 10, heightValue));
					this.revalidate(); //告诉其他部件,我的宽高变了
					
					showTabTitle();
				}
			}
		} catch (Exception e) {
		}
	}
	
	public void addNewGroup(JSONObject groupJson){
		try {
			int size = UserData.GROUP_ITEM_MAP.size();
			int heightValue = this.getHeight();
			GroupItem item = new GroupItem(groupJson, size);
			this.add(item);
			UserData.GROUP_ITEM_MAP.put(groupJson.getString("groupId"), item);
			heightValue += item.getHeight();
			
			this.setPreferredSize(new Dimension(width - 10, heightValue));
			this.revalidate(); //告诉其他部件,我的宽高变了
		} catch (Exception e) {
		}
	}
	
	public void loadGroupInfo(String groupId){
		try {
			GroupItem item = UserData.GROUP_ITEM_MAP.get(groupId);
			if(null != item){
				item.loadGroupInfo();
			}
			/*Response res = SocketService.getGroupInfoByGroupId(groupId);
			if (res.isSuccess()) {
				JSONObject groupJson = res.getDataForRtn();
				if(null != item){
					item.setGroup(groupJson);
					item.showOnlineNum();
				}
			}*/			
		} catch (Exception e) {
		}
	}
	
	public void addUser(String groupId, JSONObject user){
		GroupItem item = UserData.GROUP_ITEM_MAP.get(groupId);
		if(null != item){
			JSONObject group = item.getGroup();
			if(group != null){
				JSONArray users = group.getJSONArray("users");
				if(null != users){
					users.add(user);
					item.showOnlineNum();
				}
			}
		}
	}
	
	public void removeUser(String groupId, String userId){
		GroupItem item = UserData.GROUP_ITEM_MAP.get(groupId);
		if(null != item){
			JSONObject group = item.getGroup();
			if(group != null){
				JSONArray users = group.getJSONArray("users");
				if(null != users){
					JSONObject user = null;
					for (int i = 0, k = users.size(); i < k; i++) {
						user = users.getJSONObject(i);
						if(null != user && user.getString("userId").equals(userId)){
							users.remove(i);
							break;
						}
					}
					item.showOnlineNum();
				}
			}
		}
	}
	
	private void renderGroupList(JSONArray groupList){
		if(null != groupList ){
			this.removeAll();
			JSONObject groupJson = null;
			GroupItem item = null;
			UserData.GROUP_ITEM_MAP.clear();
			int size = groupList.size();
			int heightValue = 10;
			for (int i = 0; i < size; i++) {
				groupJson = groupList.getJSONObject(i);
				item = new GroupItem(groupJson, i);
				this.add(item);
				UserData.GROUP_ITEM_MAP.put(groupJson.getString("groupId"), item);
				heightValue += item.getHeight();
			}
			
			this.setPreferredSize(new Dimension(width - 10, heightValue));
	    	this.revalidate(); //告诉其他部件,我的宽高变了
		}
	}
	
	public void changeUserState(String userId, String chatStatus){
		JSONObject group = null;
		JSONObject user = null;
		JSONArray users = null;
		for (Map.Entry<String, GroupItem> entry : UserData.GROUP_ITEM_MAP.entrySet()) {
			group = entry.getValue().getGroup();
			if(null != group){
				users = group.getJSONArray("users");
				if(users != null){
					for (int i = 0, k = users.size(); i < k; i++) {
						user = users.getJSONObject(i);
						if(user.getString("userId").equals(userId)){
							user.put("userState", chatStatus);
							entry.getValue().showOnlineNum();
							return;
						}
					}
				}
			}
		}
	}
	
	public void removeItem(GroupItem item){
		if(item.isChat()){
			Startup.mainWindow.hideChatPanel();
		}
		this.remove(item);
		UserData.GROUP_ITEM_MAP.remove(item.getGroup().getString("groupId"));
		int i = 0;
		for(Map.Entry<String, GroupItem> entry : UserData.GROUP_ITEM_MAP.entrySet()){
			entry.getValue().setIndex(i++);
		}
		this.repaint();
		showTabTitle();
	}
	
	public void removeGroup(String groupId){
		GroupItem item = UserData.GROUP_ITEM_MAP.get(groupId);
		if(item != null){
			removeItem(item);
		}
	}
	
	public void showTabTitle(){
		int size = UserData.GROUP_ITEM_MAP.size();
		Startup.mainWindow.getLeftPanel().setTabTitle(1, MessageFormat.format("({0})", size));
	}
	
	public void updateGroup(JSONObject group){
		GroupItem item = UserData.GROUP_ITEM_MAP.get(group.getString("groupId"));
		item.setGroup(group);
		item.updateGroup();
	}
}
