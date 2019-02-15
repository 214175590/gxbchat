package com.echinacoop.form;

import java.awt.Color;
import java.util.Map;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.echinacoop.controller.SocketService;
import com.echinacoop.utils.UserData;

@SuppressWarnings("serial")
public class LeftPanel extends BasePanel {
	private int width = 260;
	private int height = 600;
	private int inited = 0;

	private JTabbedPane tabPane = null;
	private FriendPanel friendPanel;
	private GroupPanel groupPanel;
	private RecentPanel recentPanel;
	
	JScrollPane friendScrollPane = null;
	JScrollPane groupScrollPane = null;
	JScrollPane recentScrollPane = null;
	
	private TwinkleThread[] threads = new TwinkleThread[3];
	
	public LeftPanel(int width, int height) {
		this.width = width;
		this.height = height;
		initCompoments();
	}

	private void initCompoments() {
		this.setSize(width, height);
		this.setLayout(null);
		
		tabPane = new JTabbedPane();

		friendPanel = new FriendPanel(width, height - 50);
		groupPanel = new GroupPanel(width, height);
		recentPanel = new RecentPanel(width, height);
		
		friendScrollPane = new JScrollPane(friendPanel);
		friendScrollPane.setBounds(0, 0, width, height);
		friendScrollPane.setAutoscrolls(true);
		friendScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		friendScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		groupScrollPane = new JScrollPane(groupPanel);
		groupScrollPane.setBounds(0, 0, width, height);
		groupScrollPane.setAutoscrolls(true);
		groupScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		groupScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		
		recentScrollPane = new JScrollPane(recentPanel);
		recentScrollPane.setBounds(0, 0, width, height);
		recentScrollPane.setAutoscrolls(true);
		recentScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		recentScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		tabPane.addTab("好友", null, friendScrollPane, "我的好友列表");
		tabPane.addTab("群组", null, groupScrollPane, "我的群组列表");
		tabPane.addTab("最近", null, recentScrollPane, "最近联系人");

		tabPane.setBounds(0, 0, width, height);

		tabPane.addChangeListener(new TabChangeListener());

		this.add(tabPane);
	}

	private class TabChangeListener implements ChangeListener {

		public TabChangeListener() {
		}

		public void stateChanged(ChangeEvent e) {
			JTabbedPane tab = (JTabbedPane) e.getSource();
			int index = tab.getSelectedIndex();
			if (index == 0) { // 好友
			} else if (index == 1) { // 群组
			} else if (index == 2) { // 最近
			}
		}
	}
	
	public void setTwinkle(int index){
		int selectedIndex = tabPane.getSelectedIndex();
		if(selectedIndex != index){
			startTwinkle(index);
		}
	}
	
	public void checkNoReadMessage(int index){
		int count = 0;
		if(index == 0){
			for (Map.Entry<String, UserItem> entry : UserData.USER_ITEM_MAP.entrySet()) {
				if(entry.getValue().hasNoReadMessage()){
					count++;
					break;
				}
			}
		} else if(index == 1){
			for (Map.Entry<String, GroupItem> entry : UserData.GROUP_ITEM_MAP.entrySet()) {
				if(entry.getValue().hasNoReadMessage()){
					count++;
					break;
				}
			}
		} else if(index == 2){
			for (Map.Entry<String, RecentItem> entry : UserData.RECENT_ITEM_MAP.entrySet()) {
				if(entry.getValue().hasNoReadMessage()){
					count++;
					break;
				}
			}
		}
		if(count == 0){
			stopTwinkle(index);
		}
	}
	
	public void startTwinkle(int index){
		if(threads[index] == null){
			threads[index] = new TwinkleThread();
			threads[index].start();
		}
		threads[index].startThread(index);
	}
	
	public void stopTwinkle(int index){
		if(threads[index] != null){
			threads[index].stopThread(index);
		}
	}
	
	public void setTabTitle(int index, String name){
		if(index == 0){ // 好友
			tabPane.setTitleAt(index, "好友" + name);
		} else if(index == 1){ // 群组
			tabPane.setTitleAt(index, "群组" + name);
		} else if(index == 2){ // 最近
			tabPane.setTitleAt(index, "最近" + name);
		}
	}
	
	public FriendPanel getFriendPanel(){
		return friendPanel;
	}
	
	public GroupPanel getGroupPanel(){
		return groupPanel;
	}
	
	public RecentPanel getRecentPanel(){
		return recentPanel;
	}
	
	class TwinkleThread extends Thread {
		private boolean flag = true;
		private boolean cut = false;
		private int index = 0;
		@Override
		public void run() {
			try {
				// 1
				while(true){
					// 2
					while(cut){
						try {
							Thread.sleep(300);
							if(flag){
								flag = false;
								tabPane.setForegroundAt(index, Color.WHITE);
							} else {
								flag = true;
								tabPane.setForegroundAt(index, Color.RED);
							}
						} catch (InterruptedException e) {
						}
					}
					tabPane.setForegroundAt(index, Color.BLACK);
						Thread.sleep(100);
				}
			} catch (InterruptedException e) {
			}
		}
		
		public void startThread(int index){
			this.index = index;
			if(!cut){
				flag = true;
				cut = true;
			}
		}
		
		public void stopThread(int index){
			flag = true;
			cut = false;
		}
	};
	
	public void initedPanel(){
		inited++;
		if(inited >= 3){
			// 初始化数据
			SocketService.loadUserOfflineChatHisList();
		}
	}
	
}
