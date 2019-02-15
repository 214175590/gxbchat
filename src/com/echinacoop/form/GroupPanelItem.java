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
import com.echinacoop.utils.GraphicsUtils;
import com.yinsin.utils.CommonUtils;

public class GroupPanelItem extends ComponentAbs {
	
	private int width = 230;
	private int height = 50;
	private int index = 0;
	private int mbStatus = 0;

	private JSONObject group = null;
	private JLabel headImgLabel = null;
	private JLabel nameLabel = null;
	private JLabel numLabel = null;
	private JLabel infoLabel = null;
	private CButton addBtn = null;
	
	private BufferedImage image = null;
	
	public GroupPanelItem(JSONObject group, int index) {
		this.group = group;
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
		nameLabel.setBounds(55, 2, 160, 20);
		this.add(nameLabel);
		
		numLabel = new JLabel("成员：" + group.getIntValue("userCount") + "人", JLabel.CENTER);
		numLabel.setBounds(55, 25, 80, 20);
		numLabel.setOpaque(true);
		numLabel.setBackground(new Color(220, 220, 220));
		this.add(numLabel);

		infoLabel = new JLabel("已加入");
		infoLabel.setBounds(165, 25, 60, 20);
		this.add(infoLabel);
		
		addBtn = new CButton("申请入群", "/add_16px.png");
		addBtn.setBounds(140, 25, 85, 20);
		this.add(addBtn);
		
		if(group.getBooleanValue("isJoined")){
			infoLabel.setVisible(true);
			addBtn.setVisible(false);
		} else {
			infoLabel.setVisible(false);
			addBtn.setVisible(true);
		}
		
		addBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				SocketService.addGroup(group.getString("groupOwner"), group.getString("groupId"), group.getString("groupName"));
				addBtn.setText("已申请");
				addBtn.setEnabled(false);
			}
			
		});
	}
	
}
