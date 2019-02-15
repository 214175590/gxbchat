package com.echinacoop.form;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.border.Border;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.echinacoop.Startup;
import com.echinacoop.consts.Constants;
import com.echinacoop.controller.SocketService;
import com.echinacoop.modal.ChatType;
import com.echinacoop.modal.Response;
import com.echinacoop.modal.WSData;
import com.echinacoop.utils.GraphicsUtils;
import com.echinacoop.utils.MessageUtils;
import com.echinacoop.utils.MusicPlayer;
import com.echinacoop.utils.UserData;
import com.yinsin.utils.CommonUtils;

public class GroupItem extends ComponentAbs {
	private int width = 270;
	private int height = 50;
	private int index = 0;

	private JSONObject group = null;
	private JLabel headImgLabel = null;
	private JLabel nameLabel = null;
	private JLabel lastMsgLabel = null;
	private JLabel onlineLabel = null;
	private JLabel msgCountLabel = null;
	private int mbStatus = 0;
	private boolean isChat = false;
	private int msgCount = 0;
	private GroupUsersDialog dialog;
	private InvitationGroupDialog invitationDialog;
	private EditGroupDialog groupDialog;
	
	private JPopupMenu pop = null; // 弹出菜单
	private JMenuItem chatMenu = null, removeMenu = null, exitMenu = null, 
			invitationMenu = null, updateNickMenu = null, editGroupMenu = null,
			userListMenu = null;

	public GroupItem(JSONObject group, int index) {
		this.group = group;
		this.index = index;
		initCompoments();
	}

	public void mouseEntered(MouseEvent e) {
		if(mbStatus != 2){
			mbStatus = 1;
		}
		this.repaint();
	}

	public void mouseExited(MouseEvent e) {
		if(mbStatus != 2){
			mbStatus = 0;
		}
		this.repaint();
	}
	
	public void mouseReleased(MouseEvent e) {
		if(mbStatus != 2){
			mbStatus = 1;
		}
		if (e.getButton() == MouseEvent.BUTTON3) {
			boolean is = this.isOwner();
			removeMenu.setVisible(is);
			exitMenu.setVisible(!is);
			editGroupMenu.setVisible(is);
			pop.show(this, e.getX(), e.getY());
		}
		this.repaint();
	}

	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == 1){
			this.startChat();
		}
	}

	private void initCompoments() {
		this.setLayout(null);
		this.setSize(width, height);
		this.setLocation(0, index * (height - 1));

		Border lineBorder = BorderFactory.createLineBorder(new Color(225, 225, 205));
		this.setBorder(lineBorder);

		try {
			ImageIcon image = null;
			String headImg = group.getString("groupImg");
			if(CommonUtils.isBlank(headImg)){
				image = new ImageIcon(GraphicsUtils.getImg("/default_group.png"));
			} else {
				URL url = new URL(SocketService.serverUrl + headImg);
				image = new ImageIcon(url);
			}
			image.setImage(image.getImage().getScaledInstance(height - 4, height - 4, Image.SCALE_DEFAULT));
			headImgLabel = new JLabel(image);
			headImgLabel.setBounds(2, 2, height - 4, height - 4);
			this.add(headImgLabel);
		} catch (MalformedURLException e) {
		}

		nameLabel = new JLabel(group.getString("groupName"));
		nameLabel.setBounds(52, 2, width - 100, 22);
		this.add(nameLabel);
		
		onlineLabel = new JLabel("1/1", JLabel.RIGHT);
		onlineLabel.setBounds(width - 52, 12, 45, 20);
		this.add(onlineLabel);
		
		lastMsgLabel = new JLabel(CommonUtils.excNullToString(group.getString("groupDesc"), ""));
		lastMsgLabel.setBounds(52, 25, width - 55, 22);
		this.add(lastMsgLabel);

		msgCountLabel = new JLabel("0", JLabel.CENTER);
		msgCountLabel.setOpaque(true);
		msgCountLabel.setBackground(Color.RED);
		msgCountLabel.setForeground(Color.WHITE);
		msgCountLabel.setBounds(width - 42, 1, 40, 20);
		this.add(msgCountLabel);
		msgCountLabel.setVisible(false);

		pop = new JPopupMenu();
		pop.add(chatMenu = new JMenuItem("发起聊天"));
		pop.add(userListMenu = new JMenuItem("查看群成员"));
		pop.add(invitationMenu = new JMenuItem("邀请成员"));
		pop.add(updateNickMenu = new JMenuItem("修改群昵称"));
		pop.add(editGroupMenu = new JMenuItem("修改群资料"));
		pop.add(removeMenu = new JMenuItem("解散群组"));
		pop.add(exitMenu = new JMenuItem("退出群组"));
		this.add(pop);
		
		chatMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuAction(e);
			}
		});
		userListMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuAction(e);
			}
		});
		removeMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuAction(e);
			}
		});
		exitMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuAction(e);
			}
		});
		invitationMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuAction(e);
			}
		});
		updateNickMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuAction(e);
			}
		});
		editGroupMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuAction(e);
			}
		});
		
		this.showOnlineNum();
	}
	
	public void showLastChat(WSData wsData){
		JSONObject body = (JSONObject) wsData.getBody();
		if(body != null){
			JSONObject lastChat = body.getJSONObject("content");
			if(null != lastChat){
				String msg = MessageUtils.getLastChatMsg(lastChat);
				if(CommonUtils.isNotBlank(msg)){
					lastMsgLabel.setText(msg);
				}
			}
		}
	}
	
	public void showOnlineNum(){
		if(null != group){
			JSONArray users = group.getJSONArray("users");
			if(null != users){
				JSONObject user = null;
				int all = users.size();
				int online = 0;
				for (int i = 0; i < all; i++) {
					user = users.getJSONObject(i);
					if(user.getString("userState").equals("online") || user.getString("userState").equals("leave")){
						online++;
					}
				}
				onlineLabel.setText(online + "/" + all);
			}
		}
	}
	
	/**
	 * 菜单动作
	 * 
	 * @param e
	 */
	public void menuAction(ActionEvent e) {
		String str = e.getActionCommand();
		if (str.equals(chatMenu.getText())) { // 发起聊天
			this.startChat();
		} else if (str.equals(removeMenu.getText())) { // 解散群组
			int n = JOptionPane.showConfirmDialog(null, MessageFormat.format("您确定要解散群组【{0}】吗?", group.getString("groupName")), "提醒", JOptionPane.YES_NO_OPTION);
			//返回的是按钮的index  i=0或者1, 1否，0是
			if(n == 0){
				Response res = SocketService.dissolutionGroup(group.getString("groupId"));
				if(res.isSuccess()){
					UserData.GROUP_ITEM_MAP.remove(group.getString("userId"));
					Startup.mainWindow.getLeftPanel().getGroupPanel().removeItem(this);
					Startup.mainWindow.getLeftPanel().getRecentPanel().removeGroupItem(group.getString("groupId"));
				}
			}
		} else if (str.equals(exitMenu.getText())) { // 退出群组
			int n = JOptionPane.showConfirmDialog(null, MessageFormat.format("您确定要退出群组【{0}】吗?", group.getString("groupName")), "提醒", JOptionPane.YES_NO_OPTION);
			//返回的是按钮的index  i=0或者1, 1否，0是
			if(n == 0){
				Response res = SocketService.exitGroup(group.getString("groupId"));
				if(res.isSuccess()){
					UserData.GROUP_ITEM_MAP.remove(group.getString("userId"));
					Startup.mainWindow.getLeftPanel().getGroupPanel().removeItem(this);
					Startup.mainWindow.getLeftPanel().getRecentPanel().removeGroupItem(group.getString("groupId"));
				}
			}
		} else if (str.equals(invitationMenu.getText())) { // 邀请成员
			if(invitationDialog == null){
				invitationDialog = new InvitationGroupDialog(Startup.mainFrame, true);
				invitationDialog.setSize(760, 476);
				invitationDialog.init();
			}
			invitationDialog.setLocation(Startup.mainFrame.getX() + 310, Startup.mainFrame.getY() + 60);
			invitationDialog.render(group);
			invitationDialog.setVisible(true);
		} else if (str.equals(updateNickMenu.getText())) { // 修改昵称
			String nickName = UserData.user.getString("nickName");
			JSONObject user = getGroupUser(UserData.user.getString("userId"));
			if(null != user){
				nickName = user.getString("nickName");
			}
			String name = (String) JOptionPane.showInputDialog(null,"【" + group.getString("groupName") + "】\n", "修改您在群中的昵称", JOptionPane.PLAIN_MESSAGE, null, null, nickName);
			if(CommonUtils.isNotBlank(name) && !nickName.equals(name)){
				Response res = SocketService.editGroupNickname(group.getString("groupId"), name);
				if(res.isSuccess()){
					user.put("nickName", name);
				}
			}
		} else if (str.equals(editGroupMenu.getText())) { // 修改群资料
			if(groupDialog == null){
				groupDialog = new EditGroupDialog(Startup.mainFrame, true);
				groupDialog.setSize(760, 520);
				groupDialog.init();
			}
			groupDialog.setLocation(Startup.mainFrame.getX() + 310, Startup.mainFrame.getY() + 60);
			groupDialog.render(group);
			groupDialog.setVisible(true);
		} else if (str.equals(userListMenu.getText())) { // 查看群成员
			if(dialog != null){
				dialog.dispose();
			}
			dialog = new GroupUsersDialog(Startup.mainFrame, true);
			dialog.setSize(760, 476);
			dialog.init(group.getString("groupName"));
			dialog.setLocation(Startup.mainFrame.getX() + 310, Startup.mainFrame.getY() + 60);
			dialog.showUserList(this);
			dialog.setVisible(true);
		}
	}

	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		// 绘制边框
		if (mbStatus == 0) {
			g2.setPaint(new GradientPaint(0, this.getWidth(), Constants.BACK_COLOR_WHITE, 0, this.getHeight(), Constants.BACK_COLOR_WHITE));
			g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		} else if (mbStatus == 1) {// 鼠标进入
			g2.setPaint(new GradientPaint(0, this.getWidth(), Constants.ITEM_BACK_HOVER_COLOR, 0, this.getHeight(), Constants.ITEM_BACK_HOVER_COLOR));
			g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		} else if (mbStatus == 2) {// 鼠标按下
			g2.setPaint(new GradientPaint(0, this.getWidth(), Constants.ITEM_BACK_PRESS_COLOR, 0, this.getHeight(), Constants.ITEM_BACK_PRESS_COLOR));
			g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		}
		// 消除锯齿
		// UIUtils.setFractionalmetricsOn(g2);
	}
	
	public void startChat(){
		// 打开聊天窗口
		if(!this.isChat()){
			if(null != UserData.CURRENT_ITEM){
				UserData.CURRENT_ITEM.setChat(false);
				UserData.CURRENT_ITEM.resetBack();
			}
			UserData.CURRENT_ITEM = this;
			this.setChat(true);
			this.showCount(false);
			Startup.mainWindow.showChatPanel(group, ChatType.GROUP_CHAT);
		}
		mbStatus = 2;
		
		loadGroupInfo();
		
		Startup.trayStopTwinkle();
	}
	
	public JSONObject getGroup(){
		return this.group;
	}
	
	public JSONObject getGroupUser(String userId){
		JSONArray users = group.getJSONArray("users");
		JSONObject user = null;
		for (int i = 0, k = users.size(); i < k; i++) {
			user = users.getJSONObject(i);
			if(user.getString("userId").equals(userId)){
				return user;
			}
		}
		return null;
	}
	
	public void setGroup(JSONObject group){
		this.group = group;
	}
	
	public boolean isOwner(){
		String owner = CommonUtils.excNullToString(group.getString("groupOwner"), "");
		return UserData.user.getString("userId").equals(owner); 
	}

	public void showCount(boolean add) {
		if(add){
			msgCount++;		
			Startup.mainWindow.getLeftPanel().setTwinkle(1);
		} else {
			msgCount = 0;
			Startup.mainWindow.getLeftPanel().checkNoReadMessage(1);
		}
		msgCountLabel.setText("" + msgCount);
		msgCountLabel.setVisible(add);
		
		if(add){
			MusicPlayer player = new MusicPlayer("/music/msg.wav");
			player.start(); // 启动线程
			
			Startup.trayStartTwinkle();
			if(!Startup.mainFrame.isFocused()){
				Startup.mainFrame.requestFocus();
			}
		}
	}
	
	public boolean hasNoReadMessage(){
		return msgCount > 0;
	}
	
	public int getNoReadMessageCount(){
		return msgCount;
	}

	public boolean isChat() {
		return isChat;
	}

	public void setChat(boolean isChat) {
		this.isChat = isChat;
	}
	
	public void resetBack(){
		this.mbStatus = 0;
		this.repaint();
	}
	
	public void setIndex(int index){
		this.index = index;
		this.setLocation(0, this.index * (height - 1));
	}
	
	public void updateGroup(){
		try {
			ImageIcon image = null;
			String headImg = group.getString("groupImg");
			if(CommonUtils.isBlank(headImg)){
				image = new ImageIcon(GraphicsUtils.getImg("/default_group.png"));
			} else {
				URL url = new URL(SocketService.serverUrl + headImg);
				image = new ImageIcon(url);
			}
			image.setImage(image.getImage().getScaledInstance(height - 4, height - 4, Image.SCALE_DEFAULT));
			headImgLabel.setIcon(image);
		} catch (MalformedURLException e) {
		}
		nameLabel.setText(group.getString("groupName"));
		
	}
	
	public void loadGroupInfo(){
		try {
			String groupId = group.getString("groupId");
			Response res = SocketService.getGroupInfoByGroupId(groupId);
			if (res.isSuccess()) {
				group = res.getDataForRtn();
				showOnlineNum();
			}			
		} catch (Exception e) {
		}
	}
	
}
