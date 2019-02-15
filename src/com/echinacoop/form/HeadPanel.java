package com.echinacoop.form;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.net.URL;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.UIManager;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import com.echinacoop.Startup;
import com.echinacoop.controller.SocketService;
import com.echinacoop.db.SqlHelper;
import com.echinacoop.modal.Response;
import com.echinacoop.utils.GraphicsUtils;
import com.echinacoop.utils.UserData;
import com.echinacoop.utils.Utils;
import com.yinsin.utils.CommonUtils;
import com.yinsin.utils.FileUtils;

public class HeadPanel extends BasePanel implements MouseListener {
	private int width = 260;
	private int height = 60;
	private CImageIcon headImg = null;
	private JLabel nameLabel = new JLabel();
	//private JLabel timeLabel = new JLabel();
	private CButton msgIcon = null;
	private boolean twinkle = false;
	
	public HeadPanel(){
		initCompoments();
	}
	
	private void initCompoments(){
		this.setLayout(null);
		
		try {
			URL url = new URL(Utils.getHeadUrl(UserData.user.getString("headImg")));
			headImg = new CImageIcon(url);
			if(headImg.getIconWidth() < 1){
				url = GraphicsUtils.class.getResource("/default-head.jpg");
				headImg = new CImageIcon(url);
			}
			headImg.setImage(headImg.getImage().getScaledInstance(height - 6, height - 6, Image.SCALE_DEFAULT));
			headImg.setIcon(headImg);
			headImg.setBounds(6, 5, height - 6, height - 6);
			this.add(headImg);
			
			headImg.addMouseListener(this);
		} catch (Exception e) {
		}
		
		nameLabel.setBounds(70, 5, width - 70, 20);
		nameLabel.setText(UserData.user.getString("nickName"));
		this.add(nameLabel);
		
		//timeLabel.setBounds(70, 32, width - 70, 25);
		//timeLabel.setText("登录时间：" + DateUtils.format(new Date()));
		//this.add(timeLabel);
		
		msgIcon = new CButton("（0）", "/message_chat_16px.png");
		msgIcon.setBounds(70, 35, 60, 23);
		this.add(msgIcon);
		
		msgIcon.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				/*EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							System.setProperty("sun.java2d.noddraw", "true");
							BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.translucencySmallShadow;
							BeautyEyeLNFHelper.launchBeautyEyeLNF();
							UIManager.put("RootPane.setupButtonVisible", false);
						} catch (Exception e) {
						}
						
					}
				});*/
				
				MessageManager m = new MessageManager();
				m.setVisible(true);
				
			}
		});
		
		showMsgView();
		
		new Thread(){
			public void run(){
				boolean flag = false;
				while(true){
					if(twinkle){
						if(flag){
							flag = false;
							msgIcon.setPressBorder();
							msgIcon.setForeground(Color.RED);
						} else {
							flag = true;
							msgIcon.setExitedBorder();
							msgIcon.setForeground(Color.BLACK);
						}
					} else {
						msgIcon.setExitedBorder();
						msgIcon.setForeground(Color.BLACK);
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
					}
				}
			}
		}.start();
	}
	
	public void showMsgView(){
		int count = SqlHelper.loadNoreadChatHistoryCount();
		msgIcon.setText("（" + count + "）");
		if(count > 0){
			msgIcon.setSize(60 + (String.valueOf(count).length() - 1) * 10, msgIcon.getHeight());
			// 开始闪烁
			twinkle = true;
		} else {
			// 停止闪烁
			twinkle = false;
		}
	}
	
	public void updateHead(String path){
		try {
			URL url = new URL(Utils.getHeadUrl( path));
			Image image = Toolkit.getDefaultToolkit().getImage(url);
			headImg.setImage(image.getScaledInstance(height - 6, height - 6, Image.SCALE_DEFAULT));
		} catch (Exception e) {
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Object obj = e.getSource();
		if(obj == headImg){
			JFileChooser jfc = new JFileChooser();
			jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			jfc.setFileFilter(new CFileFilter());
			jfc.showDialog(new JLabel(), "选择图片");
			File file = jfc.getSelectedFile();
			if (null != file && file.isFile()) {
				try {
					String userId = UserData.user.getString("userId");
					String fileCode = "P" + userId + CommonUtils.getRandomNumber(8);
					String fileSuffix = file.getName().substring(file.getName().lastIndexOf("."));
					File newFile = new File(fileCode + fileSuffix);
					FileUtils.copyFile(file, newFile);
					newFile = new File(newFile.getAbsolutePath());
					Response res = SocketService.uploadFile(newFile, true);
					if(res.isSuccess()){
						String filePath = res.getDataForRtn().getString("filePath");
						res = SocketService.updateUserHead(filePath);
						if(res.isSuccess()){
							Startup.mainWindow.getHeadPanel().updateHead(filePath);
						}
					}
				} catch (Exception ex) {
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
	
}
