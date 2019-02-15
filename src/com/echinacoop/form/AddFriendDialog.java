package com.echinacoop.form;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.echinacoop.controller.SocketService;
import com.echinacoop.modal.Response;

public class AddFriendDialog extends JDialog {
	
	private AddFriendDialog self;
	
	int width = 760;
	int height = 560;
	
	JScrollPane jsPane = null;
	JPanel userPanel = null;
	JLabel infoLabel = null;
	
	CTextField inputText = null;
	
	List<UserPanelItem> itemList = new ArrayList<UserPanelItem>();
	
	public AddFriendDialog(JFrame parent, boolean modal){
		super(parent, modal);
		self = this;
	}
	
	public void init(){
		width = this.getWidth();
		height = this.getHeight();
		this.setLayout(null);
		this.setTitle("添加朋友");
		this.setResizable(false);
		
		inputText = new CTextField();
		inputText.setPlaceholder("输入 供销宝账号/手机号 ，按Enter键开始查找朋友");
		inputText.setBounds(20, 10, width - 55, 30);
		this.add(inputText);
		
		userPanel = new JPanel();
		jsPane = new JScrollPane(userPanel);
		jsPane.setAutoscrolls(true);
		jsPane.setBounds(10, 45, width - 35, height - 90);
		this.add(jsPane);
		userPanel.setLayout(null);
		
		infoLabel = new JLabel("没有查询到任何结果");
		
		inputText.addKeyListener(new KeyListener(){

			@Override
			public void keyTyped(KeyEvent e) {
				
			}

			@Override
			public void keyPressed(KeyEvent e) {
				
			}

			@Override
			public void keyReleased(KeyEvent e) {
				int code = e.getKeyCode();
				if(code == KeyEvent.VK_ENTER){ // 回车，开始查询
					String keyword = inputText.getText();
					if(keyword.length() == 0){
						clear(false);
					} else {
						Response res = SocketService.findUserByUserNameOrMobile(keyword);
						if(res.isSuccess()){
							JSONArray data = res.getRtn().getJSONArray("data");
							if(null != data && data.size() > 0){
								clear(false);
								JSONObject user = null;
								UserPanelItem item = null;
								for (int i = 0, k = data.size(); i < k; i++) {
									user = data.getJSONObject(i);
									item = new UserPanelItem(user, i);
									userPanel.add(item);
									itemList.add(item);
									item.initCompoments();
								}
								
								userPanel.repaint();
							} else {
								clear(true);
							}
						}
					}
					
				}
			}
			
		});
		
	}
	
	public void clear(boolean flag){
		for (UserPanelItem item : itemList) {
			userPanel.remove(item);
		}
		itemList.clear();
		if(flag){
			userPanel.add(infoLabel);
			infoLabel.setBounds(10, 5, 200, 20);
		} else {
			userPanel.remove(infoLabel);
		}
		userPanel.repaint();
	}
	
}
