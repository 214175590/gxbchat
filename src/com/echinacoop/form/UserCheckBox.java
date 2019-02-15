package com.echinacoop.form;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.alibaba.fastjson.JSONObject;
import com.echinacoop.consts.Constants;
import com.echinacoop.utils.UserData;
import com.echinacoop.utils.Utils;

public class UserCheckBox extends ComponentAbs {
	private int width = 100;
	private int height = 145;
	private int index = 0;
	private int mbStatus = 0;
	private int type = 0;

	private JSONObject user = null;
	private JLabel headImgLabel = null;
	private JLabel nameLabel = null;
	private JCheckBox box = null;
	private boolean defCheck = false;

	private BufferedImage image = null;

	public UserCheckBox(JSONObject user, int index) {
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
		if (e.getButton() == MouseEvent.BUTTON1) { // 左键
			if(box.isEnabled()){
				if(isChecked()){
					box.setSelected(false);
				} else {
					box.setSelected(true);
				}
			}
		}
	}

	public void initCompoments() {
		this.setLayout(null);
		this.setSize(width, height);
		this.setLocation((index % 6) * (width + 10), ((int) (index / 6)) * (height + 10));

		Border lineBorder = BorderFactory.createLineBorder(new Color(225, 225, 205));
		this.setBorder(lineBorder);

		try {
			URL url = new URL(Utils.getHeadUrl(user.getString("headImg")));
			image = ImageIO.read(url);
			Image img = image.getScaledInstance(width - 4, width - 4, Image.SCALE_DEFAULT);
			headImgLabel = new JLabel(new ImageIcon(img));
			headImgLabel.setBounds(2, 2, width - 4, width - 4);
			this.add(headImgLabel);
		} catch (MalformedURLException e) {
		} catch (IOException e1) {
		}

		nameLabel = new JLabel(user.getString("nickName"), JLabel.CENTER);
		nameLabel.setBounds(1, 100, width - 2, 20);
		this.add(nameLabel);

		box = new JCheckBox();
		box.setBounds(width - 27, 122, 25, 20);
		box.setSelected(defCheck);
		this.add(box);
		box.setOpaque(false);
		if(this.user.getString("userId").equals(UserData.user.getString("userId"))){
			box.setEnabled(false);
		}

		box.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(defCheck && !isChecked()){
					type = 2;
				} else if(!defCheck && isChecked()){
					type = 1;
				} else {
					type = 0;
				}
			}
		});
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;

		// 绘制边框
		if (mbStatus == 0) {
			if(type == 1){ // 新增加
				g2.setPaint(new GradientPaint(0, this.getWidth(), Constants.BACK_COLOR_GREEN, 0, this.getHeight(), Constants.BACK_COLOR_GREEN));
			} else if(type == 2){ // 旧的删掉
				g2.setPaint(new GradientPaint(0, this.getWidth(), Constants.BACK_COLOR_RED, 0, this.getHeight(), Constants.BACK_COLOR_RED));
			} else {
				g2.setPaint(new GradientPaint(0, this.getWidth(), Constants.BACK_COLOR_WHITE, 0, this.getHeight(), Constants.BACK_COLOR_WHITE));
			}
		} else if (mbStatus == 1) {// 鼠标进入
			g2.setPaint(new GradientPaint(0, this.getWidth(), Constants.ITEM_BACK_HOVER_COLOR, 0, this.getHeight(), Constants.ITEM_BACK_HOVER_COLOR));
		} else if (mbStatus == 2) {// 鼠标按下
			g2.setPaint(new GradientPaint(0, this.getWidth(), Constants.ITEM_BACK_PRESS_COLOR, 0, this.getHeight(), Constants.ITEM_BACK_PRESS_COLOR));
		}
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		// 消除锯齿
		// UIUtils.setFractionalmetricsOn(g2);
	}
	
	public void setType(int type){
		this.type = type;
	}
	
	public void setChecked(boolean c){
		this.defCheck = c;
	}

	public boolean isChecked() {
		return box.isSelected();
	}
	
	public boolean isAdd() {
		return !defCheck && isChecked();
	}
	
	public boolean iSDelete() {
		return defCheck && !isChecked();
	}
	
	public JSONObject getUser() {
		return user;
	}
}
