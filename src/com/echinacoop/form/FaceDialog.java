package com.echinacoop.form;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.alibaba.fastjson.JSONObject;
import com.echinacoop.Startup;
import com.echinacoop.utils.MessageUtils;

public class FaceDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private int width = 500;
	private int height = 320;
	private FaceDialog self;
	
	JScrollPane jsPane = null;
	JPanel facePanel = null;
	
	CButton closeBtn = null;
	
	public FaceDialog(JFrame parent, boolean modal){
		super(parent, modal);
		self = this;
	}
	
	public void init(){
		this.width = this.getWidth();
		this.height = this.getHeight();
		
		this.setLayout(null);
		this.setUndecorated(true);
		
		closeBtn = new CButton("关闭", "/close_16px.png");
		closeBtn.setBounds(this.getWidth() - 61, 1, 60, 24);
		this.add(closeBtn);
		
		JLabel label = new JLabel("表情选择框");
		label.setBounds(5, 2, 120, 21);
		this.add(label);
		
		facePanel = new JPanel();
		jsPane = new JScrollPane(facePanel);
		jsPane.setAutoscrolls(true);
		jsPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		jsPane.setBounds(0, 26, this.width, this.height - 26);
		this.add(jsPane);
		
		facePanel.setLayout(new GridLayout(0, 11));
		
		JSONObject faceStore = MessageUtils.getFaceStore();
		if(null != faceStore){
			Iterator<String> keys = faceStore.keySet().iterator();
			JSONObject faceJson = null;
			String key = null, path = null, name = null;
			CButton faceBtn = null;
			while(keys.hasNext()){
				key = keys.next();
				faceJson = faceStore.getJSONObject(key);
				path = faceJson.getString("path");
				name = path.replace("images", "");
				faceBtn = new CButton(null, name);
				faceBtn.setAttr1(name);
				faceBtn.setAttr2(faceJson.getString("name"));
				faceBtn.addActionListener(this);
				facePanel.add(faceBtn);
			}
		}
		
		closeBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				self.setVisible(false);
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		CButton faceBtn = (CButton) e.getSource();
		Startup.mainWindow.getChatPanel().addFace(faceBtn.getAttr1().toString(), faceBtn.getAttr2().toString());
		this.setVisible(false);
	}
	
}
