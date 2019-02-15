package com.echinacoop.form;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Date;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yinsin.utils.DateUtils;

public class VersionDialog extends JDialog {
	private VersionDialog self;
	private int width = 600; 
	private int height = 450; 
	private String softUrl = null; 
	
	JScrollPane messageScrollPane = new JScrollPane();
	JTextArea info = new JTextArea();
	
	CButton closeBtn;
	CButton downBtn;
	
	public VersionDialog(JFrame parent, boolean modal, JSONArray jsonArr){
		super(parent, modal);
		this.self = this;
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension scmSize = toolkit.getScreenSize();
		this.setSize(width, height);
		this.setLocation(scmSize.width / 2 - (width / 2), scmSize.height / 2 - (height / 2));
		this.setLayout(null);
		this.setResizable(false);
		
		info.setBounds(0, 0, width - 10, height - 100);
		info.setEditable(false);
		
		messageScrollPane.setBounds(0, 0, width - 10, height - 100);
		messageScrollPane.getViewport().add(info);
		this.add(messageScrollPane);
		
		JSONObject json = null;
		StringBuffer msg = new StringBuffer();
		boolean ismust = false;
		String mandatory = "";
		for (int i = 0, k = jsonArr.size(); i < k; i++) {
			json = jsonArr.getJSONObject(i);
			msg.append("【版本号  】：" + json.getString("appVersion"));
			msg.append("\n【发布时间】：" + DateUtils.format(new Date(json.getLongValue("updateTime"))));
			msg.append("\n【更新内容】：" + json.getString("updateContent"));
			msg.append("\n\n");
			mandatory = json.getString("mandatory");
			if(mandatory.equals("01")){
				ismust = true;
			}
			if(null == softUrl){
				softUrl = json.getString("softUrl");
			}
		}
		info.setText(msg.toString());
		
		closeBtn = new CButton("下次再说", "/close_16px.png");
		closeBtn.setBounds(width / 2 - 85, height - 80, 80, 26);
		
		downBtn = new CButton("立即下载", "/download_16px.png");
		downBtn.setBounds(width / 2 + 5, height - 80, 80, 26);
		
		this.add(closeBtn);
		this.add(downBtn);
		
		if(ismust){
			closeBtn.setEnabled(false);
		}
		
		closeBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				self.setVisible(false);
				self.dispose();
			}
		});
		
		downBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Runtime.getRuntime().exec("cmd /c start " + softUrl);  
				} catch (IOException e1) {
				}
			}
		});
	}
	
	
}
