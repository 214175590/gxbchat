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
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.border.Border;

import com.alibaba.fastjson.JSONObject;
import com.echinacoop.consts.Constants;
import com.echinacoop.controller.SocketService;
import com.echinacoop.modal.ChatStatus;
import com.echinacoop.utils.GraphicsUtils;
import com.echinacoop.utils.Utils;

public class UserItemBox extends ComponentAbs {
	private int width = 100;
	private int height = 150;
	private int index = 0;
	private int mbStatus = 0;
	private boolean groupOwner = false;

	private JSONObject user = null;
	private JLabel headImgLabel = null;
	private JLabel nameLabel = null;
	private JLabel crownLabel = null;
	private JButton btn = null;

	private BufferedImage image = null;
	private ChatStatus status;

	public UserItemBox(JSONObject user, int index) {
		this.user = user;
		this.index = index;
	}

	public void mouseEntered(MouseEvent e) {
		if (mbStatus != 2) {
			mbStatus = 1;
		}
		this.repaint();
	}

	public void mouseExited(MouseEvent e) {
		if (mbStatus != 2) {
			mbStatus = 0;
		}
		this.repaint();
	}

	public void mouseReleased(MouseEvent e) {
		if (mbStatus != 2) {
			mbStatus = 1;
		}
		this.repaint();
	}

	public void mouseClicked(MouseEvent e) {
		
	}
	public void isGroupOwner() {
		groupOwner = true;
	}

	public void initCompoments() {
		this.setLayout(null);
		this.setSize(width, height);
		this.setLocation((index % 6) * (width + 10), ((int) (index / 6)) * (height + 10));

		Border lineBorder = BorderFactory.createLineBorder(new Color(225, 225, 205));
		this.setBorder(lineBorder);
		
		if(groupOwner){
			Image img = GraphicsUtils.getImg("/Crown_48px.png");
			//img = img.getScaledInstance(56, 40, Image.SCALE_DEFAULT);
			crownLabel = new JLabel(new ImageIcon(img));
			crownLabel.setBounds(26, -10, 48, 48);
			crownLabel.setToolTipText("群主大大");
			this.add(crownLabel);
		}

		try {
			URL url = new URL(Utils.getHeadUrl(user.getString("headImg")));
			image = ImageIO.read(url);
			if(user.getString("userState").equals("online")){
				status = ChatStatus.ONLINE;
			} else if(user.getString("userState").equals("offline")){
				status = ChatStatus.OFFLINE;
			}
			Image img = null;
			if(this.status == ChatStatus.ONLINE){
				img = image.getScaledInstance(width - 4, width - 4, Image.SCALE_DEFAULT);
			} else if(this.status == ChatStatus.OFFLINE){
				BufferedImage buffImage = GraphicsUtils.getGrayPicture(image);
				img = buffImage.getScaledInstance(width - 4, width - 4, Image.SCALE_DEFAULT);
			}
			headImgLabel = new JLabel(new ImageIcon(img));
			headImgLabel.setBounds(2, 2, width - 4, width - 4);
			this.add(headImgLabel);
		} catch (MalformedURLException e) {
		} catch (IOException e1) {
		}

		nameLabel = new JLabel(user.getString("nickName"), JLabel.CENTER);
		nameLabel.setBounds(1, 100, width - 2, 20);
		this.add(nameLabel);

		if(Utils.isMyFriend(user.getString("userId"))){
			btn = new CButton("已是好友", "/add_16px.png");
			btn.setEnabled(false);
		} else {
			btn = new CButton("加为好友", "/add_16px.png");
		}
		btn.setBounds(width/2 - 40, 122, 80, 25);
		this.add(btn);

		btn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				SocketService.addFriend(user.getString("userId"), "0");
				btn.setText("已申请");
				btn.setEnabled(false);
			}
		});
		
		this.repaint();
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

	public JSONObject getUser() {
		return user;
	}
}
