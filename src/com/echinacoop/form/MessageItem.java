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

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;

import com.alibaba.fastjson.JSONObject;
import com.echinacoop.Startup;
import com.echinacoop.consts.Constants;
import com.echinacoop.controller.SocketService;
import com.echinacoop.modal.ChatStatus;
import com.echinacoop.modal.ChatType;
import com.echinacoop.utils.GraphicsUtils;
import com.echinacoop.utils.UserData;
import com.echinacoop.utils.Utils;
import com.yinsin.utils.CommonUtils;

public class MessageItem extends ComponentAbs {
	private int width = 220;
	private int height = 40;
	private int index = 0;

	private JSONObject jsonObj = null;
	private JLabel headImgLabel = null;
	private JLabel nameLabel = null;
	private JLabel msgCountLabel = null;
	private int mbStatus = 0;
	private int msgCount = 0;
	private ChatType type;
	private ChatStatus status;
	private BufferedImage image = null;
	
	private JPopupMenu pop = null; // 弹出菜单
	private JMenuItem chatMenu = null; // 三个功能菜单

	public MessageItem(JSONObject jsonObj, ChatType type, int index) {
		this.jsonObj = jsonObj;
		this.type = type;
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

	private void initCompoments() {
		this.setLayout(null);
		this.setSize(width, height);
		this.setLocation(0, index * (height - 1));

		Border lineBorder = BorderFactory.createLineBorder(new Color(225, 225, 205));
		this.setBorder(lineBorder);

		try {
			URL url = null;
			if(this.type == ChatType.SINGLE_CHAT){
				url = new URL(Utils.getHeadUrl(jsonObj.getString("headImg")));
				image = ImageIO.read(url);
				
				if(jsonObj.getString("chatStatus").equals("online")){
					status = ChatStatus.ONLINE;
				} else if(jsonObj.getString("chatStatus").equals("offline")){
					status = ChatStatus.OFFLINE;
				}
				headImgLabel = new JLabel();
				flushStatus();
			} else if(this.type == ChatType.GROUP_CHAT){
				String headImg = jsonObj.getString("groupImg");
				ImageIcon img = null;
				if(CommonUtils.isBlank(headImg)){
					img = new ImageIcon(GraphicsUtils.getImg("/default_group.png"));
				} else {
					url = new URL(SocketService.serverUrl + headImg);
					img = new ImageIcon(url);
				}
				img.setImage(img.getImage().getScaledInstance(height - 4, height - 4, Image.SCALE_DEFAULT));
				headImgLabel = new JLabel(img);
			}
			headImgLabel.setBounds(2, 2, height - 4, height - 4);
			this.add(headImgLabel);
		} catch (MalformedURLException e) {
		} catch (IOException e1) {
		}
		
		if(this.type == ChatType.SINGLE_CHAT){
			nameLabel = new JLabel(jsonObj.getString("nickName"));
		} else if(this.type == ChatType.GROUP_CHAT){
			nameLabel = new JLabel(jsonObj.getString("groupName"));
		}
		nameLabel.setBounds(52, 10, width - 55, 20);
		this.add(nameLabel);
		
		msgCountLabel = new JLabel("0", JLabel.CENTER);
		msgCountLabel.setOpaque(true);
		msgCountLabel.setBackground(Color.RED);
		msgCountLabel.setForeground(Color.WHITE);
		msgCountLabel.setBounds(width - 42, 10, 40, 20);
		this.add(msgCountLabel);
		
		pop = new JPopupMenu();
		pop.add(chatMenu = new JMenuItem("发起聊天"));
		this.add(pop);
		
		chatMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startChat();
			}
		});
	}
	
	public void startChat(){
		// 打开聊天窗口
		if(this.type == ChatType.SINGLE_CHAT){
			UserItem item = UserData.USER_ITEM_MAP.get(jsonObj.getString("userId"));
			if(!item.isChat()){
				if(null != UserData.CURRENT_ITEM){
					UserData.CURRENT_ITEM.setChat(false);
					UserData.CURRENT_ITEM.resetBack();
				}
				UserData.CURRENT_ITEM = item;
				item.setChat(true);
				item.showCount(false);
				Startup.mainWindow.showChatPanel(jsonObj, type);
			}
		} else if(this.type == ChatType.GROUP_CHAT){
			GroupItem item = UserData.GROUP_ITEM_MAP.get(jsonObj.getString("groupId"));
			if(!item.isChat()){
				if(null != UserData.CURRENT_ITEM){
					UserData.CURRENT_ITEM.setChat(false);
					UserData.CURRENT_ITEM.resetBack();
				}
				UserData.CURRENT_ITEM = item;
				item.setChat(true);
				item.showCount(false);
				Startup.mainWindow.showChatPanel(jsonObj, type);
			}
		}
		
		Startup.hideMsgTiper();
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
		if(this.type == ChatType.SINGLE_CHAT){
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
	}
	
	
	public void setMsgCount(int count) {
		msgCountLabel.setText("" + count);
	}
	
	public void setStatus(String statusText){
		if(this.type == ChatType.SINGLE_CHAT){
			if(statusText.equals("online")){
				status = ChatStatus.ONLINE;
			} else if(statusText.equals("offline")){
				status = ChatStatus.OFFLINE;
			}
			jsonObj.put("chatStatus", statusText);
		}
	}
	
	public boolean hasNoReadMessage(){
		return msgCount > 0;
	}

	public void resetBack(){
		this.mbStatus = 0;
		this.repaint();
	}
	
	public void setIndex(int index){
		this.index = index;
		this.setLocation(0, this.index * (height - 1));
	}
	
}
