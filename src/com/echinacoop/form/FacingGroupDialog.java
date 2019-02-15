package com.echinacoop.form;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.echinacoop.Startup;
import com.echinacoop.controller.SocketService;
import com.echinacoop.modal.Response;
import com.echinacoop.utils.UserData;

public class FacingGroupDialog extends JDialog implements KeyListener, ActionListener {

	private FacingGroupDialog self;

	int width = 760;
	int height = 560;
	
	int index = 0;
	
	private boolean join = false;

	JScrollPane jsPane = null;
	JPanel userPanel = null;
	JLabel msgLabel = null;
	
	JLabel info = new JLabel("和您身边的朋友输入同一串数字快速加入群聊");
	JLabel label = new JLabel("这些朋友也将加入群聊");
	
	JLabel hr = new JLabel();
	
	JLabel line1 = new JLabel();
	JLabel line2 = new JLabel();
	JLabel line3 = new JLabel();
	JLabel line4 = new JLabel();
	JLabel line5 = new JLabel();
	JLabel line6 = new JLabel();
	
	JLabel num1 = new JLabel("*", JLabel.CENTER);
	JLabel num2 = new JLabel("*", JLabel.CENTER);
	JLabel num3 = new JLabel("*", JLabel.CENTER);
	JLabel num4 = new JLabel("*", JLabel.CENTER);
	JLabel num5 = new JLabel("*", JLabel.CENTER);
	JLabel num6 = new JLabel("*", JLabel.CENTER);

	List<UserPanelItem> itemList = new ArrayList<UserPanelItem>();
	List<JLabel> numList = new ArrayList<JLabel>();
	
	JButton resetBtn = new JButton("重置");
	JButton joinBtn = new JButton("立即加入群聊");

	public FacingGroupDialog(JFrame parent, boolean modal) {
		super(parent, modal);
		self = this;
	}

	public void init() {
		width = this.getWidth();
		height = this.getHeight();
		this.setLayout(null);
		this.setTitle("面对面建群");
		this.setResizable(false);

		userPanel = new JPanel();
		jsPane = new JScrollPane(userPanel);
		jsPane.setAutoscrolls(true);
		jsPane.setBounds(10, 140, width - 35, height - 225);
		this.add(jsPane);
		userPanel.setLayout(null);
		jsPane.setVisible(false);
		
		
		info.setBounds(40, 10, 500, 25);
		this.add(info);
		
		label.setBounds(10, 110, 500, 25);
		this.add(label);
		label.setVisible(false);
		
		resetBtn.setBounds(550, 55, 80, 30);
		this.add(resetBtn);
		resetBtn.setVisible(false);
		
		joinBtn.setBounds(this.getWidth()/2 - 75, this.getHeight() - 80, 150, 30);
		this.add(joinBtn);
		joinBtn.setVisible(false);
		
		msgLabel = new JLabel("");
		msgLabel.setBounds(10, this.getHeight() - 80, 200, 30);
		this.add(msgLabel);
		
		Color c = new Color(230, 230, 230);
		Font f = new Font("微软雅黑", 0, 25);
		
		hr.setOpaque(true);
		hr.setBounds(10, 105, width - 25, 1);
		hr.setBackground(c);
		this.add(hr);
		
		line1.setOpaque(true);
		line1.setBounds(40, 95, 45, 3);
		line1.setBackground(Color.GRAY);
		this.add(line1);
		
		line2.setOpaque(true);
		line2.setBounds(110, 95, 45, 3);
		line2.setBackground(Color.GRAY);
		this.add(line2);
		
		line3.setOpaque(true);
		line3.setBounds(180, 95, 45, 3);
		line3.setBackground(Color.GRAY);
		this.add(line3);
		
		line4.setOpaque(true);
		line4.setBounds(250, 95, 45, 3);
		line4.setBackground(Color.GRAY);
		this.add(line4);
		
		line5.setOpaque(true);
		line5.setBounds(320, 95, 45, 3);
		line5.setBackground(Color.GRAY);
		this.add(line5);
		
		line6.setOpaque(true);
		line6.setBounds(390, 95, 45, 3);
		line6.setBackground(Color.GRAY);
		this.add(line6);
		
		num1.setOpaque(true);
		num1.setBounds(40, 45, 45, 45);
		num1.setFont(f);
		num1.setBackground(c);
		this.add(num1);
		
		num2.setOpaque(true);
		num2.setBounds(110, 45, 45, 45);
		num2.setFont(f);
		num2.setBackground(c);
		this.add(num2);
		
		num3.setOpaque(true);
		num3.setBounds(180, 45, 45, 45);
		num3.setFont(f);
		num3.setBackground(c);
		this.add(num3);
		
		num4.setOpaque(true);
		num4.setBounds(250, 45, 45, 45);
		num4.setFont(f);
		num4.setBackground(c);
		this.add(num4);
		
		num5.setOpaque(true);
		num5.setBounds(320, 45, 45, 45);
		num5.setFont(f);
		num5.setBackground(c);
		this.add(num5);
		
		num6.setOpaque(true);
		num6.setBounds(390, 45, 45, 45);
		num6.setFont(f);
		num6.setBackground(c);
		this.add(num6);
		
		numList.add(num1);
		numList.add(num2);
		numList.add(num3);
		numList.add(num4);
		numList.add(num5);
		numList.add(num6);
		
		this.addKeyListener(this);
		resetBtn.addKeyListener(this);
		joinBtn.addKeyListener(this);
		
		resetBtn.addActionListener(this);
		joinBtn.addActionListener(this);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				if(isJoined()){
					SocketService.facingGroup(getPwd(), "0");
				}
				self.dispose();
			}
			public void windowLostFocus(WindowEvent e) {
			}
		});
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		int c = (int)(e.getKeyChar());
		if(code == KeyEvent.VK_BACK_SPACE){ // 退格键
			if(index < 6 && index > 0){
				index--;
				showNumber();
			}
		} else if(c >= 48 && c <= 57 && index < 6){
			index++;
			numList.get(index - 1).setText("" + e.getKeyChar());
			if(index == 6){ // 开始匹配
				join = true;
				SocketService.facingGroup(getPwd(), "1");
				label.setVisible(true);
				jsPane.setVisible(true);
				resetBtn.setVisible(true);
				joinBtn.setVisible(true);
			}
		}
	}
	
	public void renderUserList(JSONArray users){
		JSONObject user = null;
		UserPanelItem item = null;
		clearItem();
		int index = 1;
		for (int i = 0, k = users.size(); i < k; i++) {
			user = users.getJSONObject(i);
			if(UserData.user.getString("userId").equals(user.getString("userId"))){
				item = new UserPanelItem(user, 0);
			} else {
				item = new UserPanelItem(user, index++);
			}
			userPanel.add(item);
			itemList.add(item);
			item.initCompoments();
		}
		userPanel.repaint();
	}
	
	public void clearItem(){
		for (UserPanelItem item : itemList) {
			userPanel.remove(item);
		}
		itemList.clear();
		userPanel.repaint();
	}
	
	public void showNumber(){
		for(int i = index; i < 6; i++){
			numList.get(i).setText("*");
		}
	}
	
	public String getPwd(){
		String pwd = "";
		for(int i = 0; i < 6; i++){
			pwd += numList.get(i).getText();
		}
		return pwd;
	}
	
	public boolean isJoined(){
		return join;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(resetBtn == source){
			msgLabel.setText("");
			SocketService.facingGroup(getPwd(), "0");
			index = 0;
			showNumber();
			label.setVisible(false);
			jsPane.setVisible(false);
			resetBtn.setVisible(false);
			joinBtn.setVisible(false);
			this.setFocusable(true);
		} else if(joinBtn == source){
			String pwd = getPwd();
			if(pwd.length() == 6){
				Response res = SocketService.joinFacingGroup(pwd);
				if(res.isSuccess()){
					msgLabel.setText("");
					String groupId = res.getRtn().getString("data");
					Startup.mainWindow.getLeftPanel().getGroupPanel().addNewGroup(groupId);
					this.dispose();
				} else {
					msgLabel.setText("加入群聊失败：" + res.getMessage());
					msgLabel.setForeground(Color.RED);
				}
			}
		}
	}

}
