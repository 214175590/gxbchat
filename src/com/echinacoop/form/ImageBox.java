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
import com.echinacoop.utils.GraphicsUtils;
import com.echinacoop.utils.UserData;
import com.echinacoop.utils.Utils;

public class ImageBox extends ComponentAbs {
	private int width = 100;
	private int height = 100;
	private int index = 0;
	private int mbStatus = 0;
	private boolean press = false;
	private String id = "";
	private String path = "";
	private String imgPath = "";

	private JLabel imgLabel = null;
	private EditGroupDialog dialog;

	public ImageBox(String path, int index) {
		this.path = path;
		this.imgPath = path;
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
			if(!press){
				press = true;
				dialog.resetImage(id);
			}
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}
	
	public void selected() {
		press = true;
	}
	
	public void unselected() {
		press = false;
	}
	
	public boolean isSelected() {
		return press;
	}

	public void initCompoments(EditGroupDialog dialog) {
		this.dialog = dialog;
		this.setLayout(null);
		this.setSize(width, height);
		this.setLocation((index % 6) * (width + 10), ((int) (index / 6)) * (height + 10));

		Border lineBorder = BorderFactory.createLineBorder(new Color(225, 225, 205));
		this.setBorder(lineBorder);

		try {
			ImageIcon ico = new ImageIcon(GraphicsUtils.getImg(path));
			Image img = ico.getImage().getScaledInstance(width - 8, width - 8, Image.SCALE_DEFAULT);
			imgLabel = new JLabel(new ImageIcon(img));
			imgLabel.setBounds(4, 4, width - 8, width - 8);
			this.add(imgLabel);
		} catch (Exception e1) {
		}
		
		
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		
		if(press){
			g2.setPaint(new GradientPaint(0, this.getWidth(), Constants.BACK_COLOR_GREEN, 0, this.getHeight(), Constants.BACK_COLOR_GREEN));
		} else if (mbStatus == 0) {
			g2.setPaint(new GradientPaint(0, this.getWidth(), Constants.BACK_COLOR_WHITE, 0, this.getHeight(), Constants.BACK_COLOR_WHITE));
		} else if (mbStatus == 1) {// 鼠标进入
			g2.setPaint(new GradientPaint(0, this.getWidth(), Constants.ITEM_BACK_HOVER_COLOR, 0, this.getHeight(), Constants.ITEM_BACK_HOVER_COLOR));
		} else if (mbStatus == 2) {// 鼠标按下
			g2.setPaint(new GradientPaint(0, this.getWidth(), Constants.ITEM_BACK_PRESS_COLOR, 0, this.getHeight(), Constants.ITEM_BACK_PRESS_COLOR));
		}
		g2.fillRect(0, 0, this.getWidth(), this.getHeight());
		// 消除锯齿
		// UIUtils.setFractionalmetricsOn(g2);
	}

}
