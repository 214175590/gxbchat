package com.echinacoop.form;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.echinacoop.Startup;
import com.echinacoop.modal.MessageStatus;

public class MessageManager extends JFrame {
	private int width = 850;
	private int height = 550;
	private int headHeight = 45;
	private int borderWidth = 10;
	private JPanel mainPanel = new JPanel();
	private JTabbedPane tabPane = null;
	
	private MessagePanel noReadMsgPanel;
	private MessagePanel readedMsgPanel;
	private ChatHistoryPanel chatHisPanel;
	private SystemNoticePanel systemNoticePanel;
	
	private JScrollPane noReadMsgScrollPane = null;
	private JScrollPane readedMsgScrollPane = null;
	private JScrollPane chatHisScrollPane = null;
	private JScrollPane systemNoticeScrollPane = null;
	
	public MessageManager(){
		this.setSize(width, height);
		this.setIconImage(Startup.imageIcon.getImage()); 
		this.setTitle("消息管理器");
		this.setResizable(false);
		this.setLayout(null);
		
		mainPanel.setLayout(null);
		mainPanel.setBounds(0, 0, width, height);
		mainPanel.setBackground(Color.WHITE);
		this.add(mainPanel);
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension scmSize = toolkit.getScreenSize();
		this.setLocation(scmSize.width / 2 - (width / 2), scmSize.height / 2 - (height / 2));
		
		noReadMsgPanel = new MessagePanel(MessageStatus.NOREAD, width - borderWidth, height - headHeight);
		readedMsgPanel = new MessagePanel(MessageStatus.READED, width - borderWidth, height - headHeight);
		chatHisPanel = new ChatHistoryPanel(width - borderWidth, height - headHeight);
		systemNoticePanel = new SystemNoticePanel(width - borderWidth, height - headHeight);
		
		noReadMsgScrollPane = new JScrollPane(noReadMsgPanel);
		noReadMsgScrollPane.setBounds(0, 0, width - borderWidth, height - headHeight);
		noReadMsgScrollPane.setAutoscrolls(true);
		noReadMsgScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		noReadMsgScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		readedMsgScrollPane = new JScrollPane(readedMsgPanel);
		readedMsgScrollPane.setBounds(0, 0, width - borderWidth, height - headHeight);
		readedMsgScrollPane.setAutoscrolls(true);
		readedMsgScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		readedMsgScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		chatHisScrollPane = new JScrollPane(chatHisPanel);
		chatHisScrollPane.setBounds(0, 0, width - borderWidth, height - headHeight);
		chatHisScrollPane.setAutoscrolls(true);
		chatHisScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		chatHisScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		systemNoticeScrollPane = new JScrollPane(systemNoticePanel);
		systemNoticeScrollPane.setBounds(0, 0, width - borderWidth, height - headHeight);
		systemNoticeScrollPane.setAutoscrolls(true);
		systemNoticeScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		systemNoticeScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		tabPane = new JTabbedPane();
		tabPane.addTab("未读消息", null, noReadMsgScrollPane, "未读消息");
		tabPane.addTab("已读消息", null, readedMsgScrollPane, "已读消息");
		tabPane.addTab("聊天记录", null, chatHisScrollPane, "历史聊天记录");
		tabPane.addTab("系统通知", null, systemNoticeScrollPane, "系统通知");
		tabPane.setBounds(0, 0, width - 10, height - 32);
		tabPane.addChangeListener(new TabChangeListener(){
			
		});
		mainPanel.add(tabPane);
	}
	
	private class TabChangeListener implements ChangeListener {

		public TabChangeListener() {
		}

		public void stateChanged(ChangeEvent e) {
			JTabbedPane tab = (JTabbedPane) e.getSource();
			int index = tab.getSelectedIndex();
			if (index == 0) { // 
				noReadMsgPanel.reload();
			} else if (index == 1) { // 
				readedMsgPanel.reload();
			} else if (index == 2) { // 
			} else if (index == 2) { // 
			}
		}
	}
	
	
}
