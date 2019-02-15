package com.echinacoop.form;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.echinacoop.Startup;

public class GroupUsersDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private int width = 500;
	private int height = 320;
	private GroupUsersDialog self;
	
	JScrollPane jsPane = null;
	JPanel userPanel = null;
	
	CButton closeBtn = null;
	
	public GroupUsersDialog(JFrame parent, boolean modal){
		super(parent, modal);
		self = this;
	}
	
	public void init(String groupName){
		this.width = this.getWidth();
		this.height = this.getHeight();
		
		this.setLayout(null);
		this.setUndecorated(true);
		this.setResizable(false);
		
		closeBtn = new CButton("关闭", "/close_16px.png");
		closeBtn.setBounds(this.getWidth() - 61, 1, 60, 24);
		this.add(closeBtn);
		
		JLabel label = new JLabel(groupName + " 成员");
		label.setBounds(5, 2, 120, 21);
		this.add(label);
		
		userPanel = new JPanel();
		userPanel.setLayout(null);
		jsPane = new JScrollPane(userPanel);
		jsPane.setAutoscrolls(true);
		jsPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jsPane.setBounds(0, 26, this.width, this.height - 26);
		this.add(jsPane);
		
		closeBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				self.setVisible(false);
			}
		});
	}
	
	public void showUserList(GroupItem item){
		try {
			new Thread(){
				public void run(){
					JSONObject group = item.getGroup();
					String groupOwner = group.getString("groupOwner");
					JSONArray users = group.getJSONArray("users");
					JSONObject user = null;
					UserItemBox box = null;
					for (int i = 0, k = users.size(); i < k; i++) {
						user = users.getJSONObject(i);
						box = new UserItemBox(user, i);
						if(user.getString("userId").equals(groupOwner)){
							box.isGroupOwner();
						}
						userPanel.add(box);
						box.initCompoments();

						userPanel.setPreferredSize(new Dimension(userPanel.getWidth() - 10, ((i + 1) / 5 + 1) * box.getHeight() + 20));
						userPanel.revalidate(); //告诉其他部件,我的宽高变了
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
						}
					}
				}
			}.start();
		} catch (Exception e) {
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		CButton faceBtn = (CButton) e.getSource();
		Startup.mainWindow.getChatPanel().addFace(faceBtn.getAttr1().toString(), faceBtn.getAttr2().toString());
		this.setVisible(false);
	}
	
}
