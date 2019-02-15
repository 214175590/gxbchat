package com.echinacoop.form;

import java.awt.Color;

import javax.swing.JPanel;

import com.alibaba.fastjson.JSONObject;
import com.echinacoop.modal.ChatType;

public class MainWindow extends JPanel {
	
	private int width = 1080;
	private int height = 740;
	
	private int leftWidth = 300;
	private int headHieght = 70;
	
	private HeadPanel headPanel = new HeadPanel();
	private LeftPanel leftPanel = null;
	private BasePanel mainPanel = new BasePanel();
	
	private ChatPanel chatPanel = null;
	
	public MainWindow(int width, int height){
		this.width = width;
		this.height = height;
		initComponents();
	}
	
	private void initComponents() {
		this.setLayout(null);
		this.setBackground(new Color(240, 240, 240));
		
		headPanel.setBounds(0, 0, leftWidth, headHieght);
		this.add(headPanel);
		
		leftPanel = new LeftPanel(leftWidth, height - headHieght - 35);
		leftPanel.setLocation(0, headHieght);
		this.add(leftPanel);
		leftPanel.setVisible(true);
		
		// 
		
		mainPanel.setBounds(leftWidth, 0, width - leftWidth, height);
		mainPanel.setLayout(null);
		this.add(mainPanel);
		
		chatPanel = new ChatPanel(mainPanel.getWidth(), mainPanel.getHeight());
		chatPanel.setBounds(0, 0, mainPanel.getWidth(), mainPanel.getHeight());
		mainPanel.add(chatPanel);
		chatPanel.setVisible(true);
		
	}
	
	public void showChatPanel(JSONObject chatObj, ChatType chatType){
		chatPanel.setUserChat(chatObj, chatType);
		chatPanel.render();
		chatPanel.showChatPanel();
	}
	
	public void hideChatPanel(){
		chatPanel.clearChatPanel();
	}
	
	public ChatPanel getChatPanel(){
		return chatPanel;
	}
	
	public LeftPanel getLeftPanel(){
		return leftPanel;
	}
	
	public HeadPanel getHeadPanel(){
		return headPanel;
	}
	
}
