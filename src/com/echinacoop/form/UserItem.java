package com.echinacoop.form;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;

import com.alibaba.fastjson.JSONObject;
import com.echinacoop.Startup;
import com.echinacoop.consts.Constants;
import com.echinacoop.controller.SocketService;
import com.echinacoop.modal.ChatStatus;
import com.echinacoop.modal.ChatType;
import com.echinacoop.modal.Response;
import com.echinacoop.modal.WSData;
import com.echinacoop.utils.GraphicsUtils;
import com.echinacoop.utils.MessageUtils;
import com.echinacoop.utils.MusicPlayer;
import com.echinacoop.utils.UserData;
import com.echinacoop.utils.Utils;
import com.yinsin.utils.CommonUtils;

public class UserItem extends ComponentAbs {
	private int width = 270;
	private int height = 50;
	private int index = 0;

	private JSONObject user = null;
	private JLabel headImgLabel = null;
	private JLabel nameLabel = null;
	private JLabel lastMsgLabel = null;
	private JLabel msgCountLabel = null;
	private int mbStatus = 0;
	private boolean isChat = false;
	private int msgCount = 0;
	
	private JPopupMenu pop = null; // 弹出菜单
	private JMenuItem chatMenu = null, removeMenu = null, updNickMenu = null; 
	
	private ChatStatus status;
	private BufferedImage image = null;

	public UserItem(JSONObject user, int index) {
		this.user = user;
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
			pop.show(this, e.getX(), e.getY());
		}
		this.repaint();
	}

	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == 1){
			this.startChat();
		}
	}
	
	private void startChat(){
		// 打开聊天窗口
		if(!this.isChat()){
			if(null != UserData.CURRENT_ITEM){
				UserData.CURRENT_ITEM.setChat(false);
				UserData.CURRENT_ITEM.resetBack();
			}
			UserData.CURRENT_ITEM = this;
			this.setChat(true);
			this.showCount(false);
			Startup.mainWindow.showChatPanel(user, ChatType.SINGLE_CHAT);
		}
		mbStatus = 2;
		
		Startup.trayStopTwinkle();
	}

	private void initCompoments() {
		this.setLayout(null);
		this.setSize(width, height);
		this.setLocation(0, index * (height - 1));

		Border lineBorder = BorderFactory.createLineBorder(new Color(225, 225, 205));
		this.setBorder(lineBorder);

		try {
			URL url = null;
			try {
				url = new URL(Utils.getHeadUrl(user.getString("headImg")));
				image = ImageIO.read(url);
			} catch (Exception e) {
				url = GraphicsUtils.class.getResource("/default-head.jpg");
				image = ImageIO.read(url);
			}
			
			headImgLabel = new JLabel();
			headImgLabel.setBounds(2, 2, height - 4, height - 4);
			this.add(headImgLabel);
			
			if(user.getString("chatStatus").equals("online")){
				status = ChatStatus.ONLINE;
			} else if(user.getString("chatStatus").equals("offline")){
				status = ChatStatus.OFFLINE;
			}
			
			flushStatus();
		} catch (Exception e) {
		}

		nameLabel = new JLabel(CommonUtils.excNullToString(user.getString("friendNick"), user.getString("nickName")));
		nameLabel.setBounds(52, 2, width - 55, 22);
		this.add(nameLabel);

		String msg = MessageUtils.getLastChatMsg(user.getJSONObject("lastChat"));
		lastMsgLabel = new JLabel(msg);
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
		pop.add(updNickMenu = new JMenuItem("修改备注名称"));
		pop.add(removeMenu = new JMenuItem("删除好友"));
		this.add(pop);
		
		chatMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuAction(e);
			}
		});
		updNickMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuAction(e);
			}
		});
		removeMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuAction(e);
			}
		});
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
	
	/**
	 * 菜单动作
	 * 
	 * @param e
	 */
	public void menuAction(ActionEvent e) {
		String str = e.getActionCommand();
		if (str.equals(chatMenu.getText())) { // 发起聊天
			this.startChat();
		} else if (str.equals(removeMenu.getText())) { // 删除好友
			int n = JOptionPane.showConfirmDialog(null, MessageFormat.format("您确定要删除好友【{0}】吗?", user.getString("nickName")), "提醒", JOptionPane.YES_NO_OPTION);
			//返回的是按钮的index  i=0或者1, 1否，0是
			if(n == 0){
				Response res = SocketService.removeFriend(user.getString("userId"));
				if(res.isSuccess()){
					UserData.USER_ITEM_MAP.remove(user.getString("userId"));
					Startup.mainWindow.getLeftPanel().getFriendPanel().removeItem(this);
					Startup.mainWindow.getLeftPanel().getRecentPanel().removeUserItem(user.getString("userId"));
				}
			}
		} else if(str.equals(updNickMenu.getText())){			
			String nickName = CommonUtils.excNullToString(user.getString("friendNick"), user.getString("nickName"));
			String name = (String) JOptionPane.showInputDialog(null,"设置好友备注名称\n", "给好友设置备注名称", JOptionPane.PLAIN_MESSAGE, null, null, nickName);
			if(CommonUtils.isNotBlank(name) && !nickName.equals(name)){
				Response res = SocketService.editFriendNickname(user.getString("userId"), name);
				if(res.isSuccess()){
					user.put("friendNick", name);
					nameLabel.setText(name);
				}
			}			
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
	
	public void flushStatus(){
		if(this.status == ChatStatus.ONLINE){
			Image img = image.getScaledInstance(height - 4, height - 4, Image.SCALE_DEFAULT);
			headImgLabel.setIcon(new ImageIcon(img));
		} else if(this.status == ChatStatus.OFFLINE){
			BufferedImage buffImage = GraphicsUtils.getGrayPicture(image);
			Image img = buffImage.getScaledInstance(height - 4, height - 4, Image.SCALE_DEFAULT);
			headImgLabel.setIcon(new ImageIcon(img));
		}
		if(null != Startup.mainWindow){
			Startup.mainWindow.getLeftPanel().getFriendPanel().showTabTitle();
		}
	}
	
	public JSONObject getUser(){
		return this.user;
	}

	public void showCount(boolean add) {
		LeftPanel panel = Startup.mainWindow.getLeftPanel();
		if(add){
			msgCount++;	
			if(panel != null){
				panel.setTwinkle(0);
			}
		} else {
			msgCount = 0;
			if(panel != null){
				panel.checkNoReadMessage(0);
			}
		}
		msgCountLabel.setText("" + msgCount);
		msgCountLabel.setVisible(add);
		
		// 有新消息声音提醒，并闪动托盘图标
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
	
	public void setStatus(String statusText){
		if(statusText.equals("online")){
			status = ChatStatus.ONLINE;
		} else if(statusText.equals("offline")){
			status = ChatStatus.OFFLINE;
		}
		user.put("chatStatus", statusText);
	}

}
