package com.echinacoop.form;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import com.alibaba.fastjson.JSONObject;
import com.echinacoop.Startup;
import com.echinacoop.controller.SocketService;
import com.echinacoop.modal.Response;
import com.yinsin.utils.CommonUtils;

public class EditGroupDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private int width = 500;
	private int height = 320;
	private EditGroupDialog self;
	private JSONObject group;
	
	JScrollPane jsPane = null;
	JPanel imgPanel = null;
	
	JTextField nameText = new JTextField();
	JTextArea descText = new JTextArea();
	
	CButton saveBtn = new CButton("保存", "/save_16px.png");
	CButton cancelBtn = new CButton("取消", "/close_16px.png");
	Map<String, ImageBox> imgMap = new HashMap<String, ImageBox>();
	
	public EditGroupDialog(JFrame parent, boolean modal){
		super(parent, modal);
		self = this;
	}
	
	public void init(){
		this.width = this.getWidth();
		this.height = this.getHeight();
		
		this.setLayout(null);
		this.setResizable(false);
		this.setTitle("修改群资料");
		
		JLabel label = new JLabel("群名称：");
		label.setBounds(5, 2, 120, 21);
		this.add(label);
		
		nameText.setBounds(5, 26, width - 20, 30);
		this.add(nameText);
		
		JLabel label2 = new JLabel("群头像：");
		label2.setBounds(5, 70, 120, 21);
		this.add(label2);
		
		imgPanel = new JPanel();
		imgPanel.setLayout(null);
		jsPane = new JScrollPane(imgPanel);
		jsPane.setAutoscrolls(true);
		jsPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jsPane.setBounds(5, 95, this.width - 20, 180);
		this.add(jsPane);
		
		JLabel label3 = new JLabel("群说明：");
		label3.setBounds(5, 290, 120, 21);
		this.add(label3);
		
		descText.setBounds(5, 320, width - 20, 120);		
		this.add(descText);
		
		saveBtn.setBounds(width/2 - 90, 450, 80, 30);
		cancelBtn.setBounds(width/2 + 10, 450, 80, 30);
		
		this.add(saveBtn);
		this.add(cancelBtn);
		
		saveBtn.addActionListener(this);
		cancelBtn.addActionListener(this);
	}
	
	public void render(JSONObject group){
		this.group = group;
		imgPanel.removeAll();
		imgMap.clear();
		
		nameText.setText(group.getString("groupName"));
		descText.setText(group.getString("groupDesc"));
		
		ImageBox box = null;
		String path = "";
		
		path = "/default_group.png";
		if(!group.containsKey("groupImg") || CommonUtils.isBlank(group.getString("groupImg"))){
			group.put("groupImg", "images" + path);
		}
		box = new ImageBox(path, 0);
		box.initCompoments(this);
		box.setId("img-0");
		box.setImgPath("images" + path);
		if(box.getImgPath().equals(group.getString("groupImg"))){
			box.selected();
		}
		imgPanel.add(box);
		
		imgMap.put(box.getId(), box);
		for (int i = 1; i < 4; i++) {
			path = "/group/g-00" + i + ".jpg";
			box = new ImageBox(path, i);
			box.initCompoments(this);
			box.setId("img-" + i);
			box.setImgPath("images" + path);
			if(box.getImgPath().equals(group.getString("groupImg"))){
				box.selected();
			}
			imgPanel.add(box);
			
			imgMap.put(box.getId(), box);
		}
	}
	
	public void resetImage(String id){
		for (Entry<String, ImageBox> entry : imgMap.entrySet()) {
			if(!entry.getValue().getId().equals(id)){
				entry.getValue().unselected();
			}
		}
		imgPanel.repaint();
	}
	
	public String getHeadImg(){
		for (Entry<String, ImageBox> entry : imgMap.entrySet()) {
			if(entry.getValue().isSelected()){
				return entry.getValue().getImgPath();
			}
		}
		return  "images/default_group.png";
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object btn = e.getSource();
		if(btn == cancelBtn){
			this.setVisible(false);
		} else if(btn == saveBtn){
			String name = nameText.getText();
			String desc = descText.getText();
			String headImg = getHeadImg();
			if(CommonUtils.isBlank(name)){
				JOptionPane.showMessageDialog(this, "群组名称不能为空.", "警告", JOptionPane.WARNING_MESSAGE);
			} else {
				Response res = SocketService.editGroupInfo(group.getString("groupId"), name, headImg, desc);
				if(res.isSuccess()){
					group.put("groupName", name);
					group.put("groupImg", headImg);
					group.put("groupDesc", desc);
					Startup.mainWindow.getLeftPanel().getGroupPanel().updateGroup(group);
					this.setVisible(false);
				} else {
					JOptionPane.showMessageDialog(this, "失败：" + res.getMessage(), "提示", JOptionPane.WARNING_MESSAGE);
				}
			}
		}
	}
	
}
