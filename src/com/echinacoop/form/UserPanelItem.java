package com.echinacoop.form;

import java.awt.Color;
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
import javax.swing.border.Border;

import com.alibaba.fastjson.JSONObject;
import com.echinacoop.controller.SocketService;
import com.echinacoop.utils.UserData;
import com.echinacoop.utils.Utils;
import com.yinsin.utils.CommonUtils;

public class UserPanelItem extends ComponentAbs {
	
	private int width = 230;
	private int height = 50;
	private int index = 0;
	private int mbStatus = 0;

	private JSONObject user = null;
	private JLabel headImgLabel = null;
	private JLabel nameLabel = null;
	private JLabel trueLabel = null;
	private JLabel infoLabel = null;
	private CButton addBtn = null;
	
	private BufferedImage image = null;
	
	public UserPanelItem(JSONObject user, int index) {
		this.user = user;
		this.index = index;
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
		this.repaint();
	}

	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1){ // 左键
			
		}
	}
	
	public void initCompoments() {
		this.setLayout(null);
		this.setSize(width, height);
		this.setLocation((index%3) * (width + 10), ((int)(index/3)) * (height + 10));

		Border lineBorder = BorderFactory.createLineBorder(new Color(225, 225, 205));
		this.setBorder(lineBorder);
		
		try {
			URL url = new URL(Utils.getHeadUrl(user.getString("headImg")));
			image = ImageIO.read(url);
			Image img = image.getScaledInstance(height - 4, height - 4, Image.SCALE_DEFAULT);
			headImgLabel = new JLabel(new ImageIcon(img));
			headImgLabel.setBounds(2, 2, height - 4, height - 4);
			this.add(headImgLabel);
		} catch (MalformedURLException e) {
		} catch (IOException e1) {
		}

		nameLabel = new JLabel(user.getString("nickName"));
		nameLabel.setBounds(58, 2, 160, 20);
		this.add(nameLabel);
		
		infoLabel = new JLabel("已为好友");
		infoLabel.setBounds(165, 25, 60, 20);
		this.add(infoLabel);
		
		addBtn = new CButton("加为好友", "/add_16px.png");
		addBtn.setBounds(140, 25, 85, 20);
		this.add(addBtn);
		
		if(UserData.user.getString("userId").equals(user.getString("userId"))){
			trueLabel = new JLabel("自己", JLabel.CENTER);
			trueLabel.setOpaque(true);
			trueLabel.setBounds(58, 25, 45, 23);
			trueLabel.setBackground(new Color(3, 184, 207));
			trueLabel.setForeground(Color.WHITE);
			this.add(trueLabel);
			
			infoLabel.setVisible(false);
			addBtn.setVisible(false);
		} else {
			trueLabel = new JLabel("未实名");
			trueLabel.setBounds(58, 25, 70, 25);
			this.add(trueLabel);
			if(CommonUtils.isNotEmpty(user.getString("trueName"))){
				trueLabel.setText("已实名");
				trueLabel.setForeground(Color.BLUE);
			}
			
			if(Utils.isMyFriend(user.getString("userId"))){
				infoLabel.setVisible(true);
				addBtn.setVisible(false);
			} else {
				infoLabel.setVisible(false);
				addBtn.setVisible(true);
			}
		}
		
		addBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				SocketService.addFriend(user.getString("userId"), "0");
				addBtn.setText("已申请");
				addBtn.setEnabled(false);
			}
			
		});
	}
	
	
	
}
