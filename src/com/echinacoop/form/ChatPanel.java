package com.echinacoop.form;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.echinacoop.Startup;
import com.echinacoop.controller.SocketService;
import com.echinacoop.modal.ChatType;
import com.echinacoop.modal.FontAttrib;
import com.echinacoop.modal.ImageType;
import com.echinacoop.modal.Message;
import com.echinacoop.modal.MessageType;
import com.echinacoop.modal.Response;
import com.echinacoop.modal.WSData;
import com.echinacoop.utils.GraphicsUtils;
import com.echinacoop.utils.MessageUtils;
import com.echinacoop.utils.UserData;
import com.echinacoop.utils.Utils;
import com.yinsin.utils.CommonUtils;
import com.yinsin.utils.DateUtils;
import com.yinsin.utils.FileUtils;

public class ChatPanel extends JPanel implements ActionListener, KeyListener {

	private int width = 800;
	private int height = 740;
	private ChatType chatType = ChatType.SINGLE_CHAT;
	private JSONObject chatObj = new JSONObject();

	private JLabel userNameLabel = new JLabel("", JLabel.CENTER);

	JScrollPane messageScrollPane = new JScrollPane();
	JScrollPane inputScrollPane = new JScrollPane();
	StyleContext styleContext1 = new StyleContext();
	DefaultStyledDocument doc2 = new DefaultStyledDocument(styleContext1);
	CTextPanel inputText = new CTextPanel(doc2);
	JButton sendButton = new JButton();
	Style defaultStyle;
	Style userStyle;
	Style statusStyle;
	StyleContext styleContext = new StyleContext();
	DefaultStyledDocument doc = new DefaultStyledDocument(styleContext);
	JTextPane messageText = new JTextPane(doc);
	FontAttrib enterFont = new FontAttrib();
	
	FacingGroupDialog facingDialog = null;

	FaceDialog face = null;
	FontDialog fontDialog = null;
	
	private JPopupMenu pop = null; // 弹出菜单
	// 功能菜单
	private JMenuItem addFriendMenu = null, 
			findGroupMenu = null,
			startGroupChatMenu = null,
			faceingGroupMenu = null; 

	CButton fontBtn = new CButton(null, "/font_24px.png");
	CButton faceBtn = new CButton(null, "/face_24px.png");
	CButton picBtn = new CButton(null, "/image_24px.png");
	CButton plusBtn = new CButton(null, "/add_24px.png");

	public ChatPanel(int width, int height) {
		this.width = width;
		this.height = height;
		initCompoments();
	}

	public void setUserChat(JSONObject chatObj, ChatType chatType) {
		this.chatObj = chatObj;
		this.chatType = chatType;
		if (this.chatType == ChatType.SINGLE_CHAT) {
			userNameLabel.setText("好友【" + this.chatObj.getString("nickName") + "】");
		} else if (this.chatType == ChatType.GROUP_CHAT) {
			userNameLabel.setText("群组【" + this.chatObj.getString("groupName") + "】");
		}
	}

	private void initCompoments() {
		this.setLayout(null);
		this.setSize(width, height);

		userNameLabel.setBounds(0, 0, width - 10, 25);
		this.add(userNameLabel);

		messageScrollPane.setAutoscrolls(true);
		// messageScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		// messageScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		messageScrollPane.setBounds(0, 27, width - 10, 483);
		this.add(messageScrollPane);

		messageText.setEditable(false);
		messageText.setAutoscrolls(true);
		messageScrollPane.getViewport().add(messageText);

		sendButton.setText("发送");
		sendButton.setBorderPainted(false);
		sendButton.setMnemonic((int) 'S');
		sendButton.setBounds(width - 80, height - 85, 65, 40);
		this.add(sendButton);

		inputScrollPane.setAutoscrolls(true);
		inputScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		inputScrollPane.setBounds(0, 540, width - 80, height - 580);
		this.add(inputScrollPane);

		inputScrollPane.getViewport().add(inputText);
		inputText.setFont(new Font("Dialog", Font.PLAIN, 12));

		sendButton.addActionListener(this);
		// inputText.addKeyListener(this);

		defaultStyle = styleContext.addStyle("Default", null);
		StyleConstants.setForeground(defaultStyle, Color.black);
		StyleConstants.setFontFamily(defaultStyle, "Dialog");
		doc.setLogicalStyle(0, defaultStyle);

		userStyle = styleContext.addStyle(null, null);
		StyleConstants.setBold(userStyle, true);

		statusStyle = styleContext.addStyle(null, null);
		StyleConstants.setBold(statusStyle, true);
		StyleConstants.setForeground(statusStyle, Color.black);

		plusBtn.setBounds(width - 38, 0, 26, 26);
		this.add(plusBtn);
		
		fontBtn.setBounds(5, 510, 26, 26);
		this.add(fontBtn);

		faceBtn.setBounds(40, 510, 26, 26);
		this.add(faceBtn);

		picBtn.setBounds(75, 510, 26, 26);
		this.add(picBtn);

		plusBtn.addActionListener(this);
		fontBtn.addActionListener(this);
		faceBtn.addActionListener(this);
		picBtn.addActionListener(this);
		
		pop = new JPopupMenu();
		pop.add(addFriendMenu = new JMenuItem("添加朋友"));
		pop.add(findGroupMenu = new JMenuItem("查找群组"));
		pop.add(startGroupChatMenu = new JMenuItem("发起群聊"));
		pop.add(faceingGroupMenu = new JMenuItem("面对面建群"));
		this.add(pop);
		
		clearChatPanel();
		
		addFriendMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuAction(e);
			}
		});
		findGroupMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuAction(e);
			}
		});
		startGroupChatMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuAction(e);
			}
		});
		faceingGroupMenu.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuAction(e);
			}
		});

		inputText.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == 86 && e.isControlDown()) {
					BufferedImage img = (BufferedImage) inputText.getClipboardImage();
					if (img != null) {
						try {
							String userId = UserData.user.getString("userId");
							String fileCode = "P" + userId + CommonUtils.getRandomNumber(8);
							String fileSuffix = ".jpg";
							File file = new File(fileCode + fileSuffix);
							ImageIO.write(img, "jpeg", file);
							Response res = SocketService.uploadFile(file, true);
							if(res.isSuccess()){
								String fileSize = res.getDataForRtn().getString("fileSize");
								String filePath = res.getDataForRtn().getString("filePath");
								int[] size = {0, 0};
								if(null != fileSize && fileSize.indexOf(",") != -1){
									String[] sizeArr = fileSize.split(",");
									size[0] = CommonUtils.stringToInt(sizeArr[0]);
									size[1] = CommonUtils.stringToInt(sizeArr[1]);
								}
								Startup.mainWindow.getChatPanel().sendTextPic(filePath, size[0], size[1]);
							}
						} catch (IOException e1) {
						}
					}
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown()) {
					sendTextd();
				}
			}
		});
		
	}
	
	public void showChatPanel(){
		sendButton.setEnabled(true);
		inputText.setEditable(true);
		fontBtn.setEnabled(true);
		faceBtn.setEnabled(true);
		picBtn.setEnabled(true);
	}
	
	public void clearChatPanel(){
		sendButton.setEnabled(false);
		inputText.setEditable(false);
		fontBtn.setEnabled(false);
		faceBtn.setEnabled(false);
		picBtn.setEnabled(false);
		userNameLabel.setText("");
	}

	/** 渲染聊天界面 */
	public void render() {
		messageText.setText("");
		List<WSData> datas = null;
		if (chatType == ChatType.SINGLE_CHAT) {
			datas = UserData.USER_CHAT_RECORD.get(chatObj.getString("userId"));
			if (null == datas || datas.size() < 30) {
				new Thread(new LoadChatThread(chatType)).start();
			} else {
				renderChat(datas);
			}
		} else if (chatType == ChatType.GROUP_CHAT) {
			datas = UserData.GROUP_CHAT_RECORD.get(chatObj.getString("groupId"));
			if (null == datas || datas.size() < 30) {
				new Thread(new LoadChatThread(chatType)).start();
			} else {
				renderChat(datas);
			}
		}
	}

	public void renderChat(List<WSData> datas) {
		if (datas != null) {
			for (WSData wsData : datas) {
				showMessage(wsData, false);
				/*try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
				}*/
			}

			scrollChatPanel();
		}
	}
	
	private void scrollChatPanel(){
		try {
			Point p = new Point();
			/*JScrollBar sBar = messageScrollPane.getVerticalScrollBar();
			System.out.println(sBar.getHeight());
			System.out.println(messageText.getHeight());*/
			p.setLocation(0, messageText.getHeight());
			messageScrollPane.getViewport().setViewPosition(p);
			
			//messageText.setFocusable(true);
			inputText.setFocusable(true);
		} catch (Exception e) {
		}
	}

	/** 插入文本 */
	private void insertSendTitle(FontAttrib attrib, boolean isEnter) {
		try { // 插入文本
			doc.insertString(doc.getLength(), attrib.getText() + (isEnter ? "\n" : ""), attrib.getAttrSet());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/** 插入图片 */
	private void insertIcon(File file, boolean isEnter) {
		try {
			BufferedImage img = ImageIO.read(file);
			CImageIcon icon = new CImageIcon(img);
			icon.setType(ImageType.CHAT_PIC);
			icon.setObject(file.getName());
			messageText.setCaretPosition(doc.getLength()); // 设置插入位置
			//messageText.insertIcon(icon); // 插入图片
			messageText.insertComponent(icon);
			if (isEnter) {
				insertSendTitle(enterFont, true); // 这样做可以换行
			}
		} catch (Exception e) {
		}
	}
	
	/** 插入语音 */
	private void insertVoice(File file, boolean isEnter) {
		try {
			VoiceButton voice = new VoiceButton();
			voice.setVoice(file);
			messageText.setCaretPosition(doc.getLength()); // 设置插入位置
			messageText.insertComponent(voice);
			if (isEnter) {
				insertSendTitle(enterFont, true); // 这样做可以换行
			}
		} catch (Exception e) {
		}
	}

	private void insertFace(String facePath, boolean isEnter) {
		try {
			URL url = GraphicsUtils.class.getResource(facePath);
			CImageIcon face = new CImageIcon(url);
			face.setType(ImageType.CHAT_FACE);
			messageText.setCaretPosition(doc.getLength()); // 设置插入位置
			//messageText.insertIcon(face); // 插入图片
			messageText.insertComponent(face);
			if (isEnter) {
				insertSendTitle(enterFont, true); // 这样做可以换行
			}
		} catch (Exception e) {
		}
	}

	public void addFace(String facePath, String faceName) {
		try {
			URL url = GraphicsUtils.class.getResource(facePath);
			FaceIcon face = new FaceIcon(url);
			face.setFaceName(faceName);
			face.setFacePath(facePath);
			inputText.setCaretPosition(doc2.getLength()); // 设置插入位置
			inputText.insertIcon(face); // 插入图片
		} catch (Exception e) {
		}
	}

	public void showMessage(WSData wsData, boolean isscroll) {
		try {
			JSONObject body = (JSONObject) wsData.getBody();
			JSONObject user = null;
			// 说话者
			FontAttrib sendUser = new FontAttrib();
			sendUser.setColor(new Color(100, 100, 240));
			if (chatType == ChatType.SINGLE_CHAT) {
				if (body.containsKey("fromUid")) {
					user = chatObj;
				} else {
					user = UserData.user;
					sendUser.setColor(new Color(0, 120, 20));
				}
			} else if (chatType == ChatType.GROUP_CHAT) {
				if (body.containsKey("fromUid")) {
					user = getUserForGroup(body.getString("fromUid"));
				} else {
					user = UserData.user;
					sendUser.setColor(new Color(0, 120, 20));
				}
			}
			sendUser.setText(MessageFormat.format("【{0}】 {1} 说：", 
					Utils.getUserViewName(user.getString("userId"), user.getString("nickName")), 
					DateUtils.format(new Date(body.getLong("time")))));
			insertSendTitle(sendUser, true);

			List<Message> msgList = MessageUtils.formatMessage(body.getJSONObject("content"));
			if (null != msgList) {
				FontAttrib textFont = null;
				for (Message msg : msgList) {
					if (msg.getMessageType() == MessageType.TEXT) {
						textFont = msg.getMsg();
						insertSendTitle(textFont, false);
					} else if (msg.getMessageType() == MessageType.FACE) {
						try {
							textFont = msg.getMsg();
							insertFace(textFont.getText(), false);
						} catch (Exception e) {
						}
					} else if (msg.getMessageType() == MessageType.PIC) {
						try {
							textFont = msg.getMsg();
							insertIcon(Utils.getNetFile(textFont.getText()), true);
						} catch (Exception e) {
						}
					} else if (msg.getMessageType() == MessageType.VOICE) {
						try {
							textFont = msg.getMsg();
							insertVoice(Utils.getNetFile(textFont.getText()), true);
						} catch (Exception e) {
						}						
					}
				}
				insertSendTitle(enterFont, true);
			}
			
			if(isscroll){
				scrollChatPanel();
			}
			
			Startup.mainFrame.requestFocus();
			
			if(!Startup.mainFrame.isFocused()){
				Startup.mainFrame.requestFocus();
			}
		} catch (Exception e) {
		}
	}

	private JSONObject getUserForGroup(String userId) {
		JSONObject user = null, tempUser = null;
		JSONArray users = chatObj.getJSONArray("users");
		if (null != users) {
			for (int i = 0, k = users.size(); i < k; i++) {
				tempUser = users.getJSONObject(i);
				if (tempUser.getString("userId").equals(userId)) {
					user = tempUser;
					break;
				}
			}
		}
		return user;
	}

	public void sendTextd() {
		String text = inputText.getText();
		int size = text.length();
		if (size == 0) {
			return;
		}
		Icon icon = null;
		FaceIcon faceIcon = null;
		List<String> faceText = new ArrayList<String>();
		for (int i = 0; i < doc2.getRootElements()[0].getElement(0).getElementCount(); i++) {
			icon = StyleConstants.getIcon(doc2.getRootElements()[0].getElement(0).getElement(i).getAttributes());
			if (icon != null) {
				faceIcon = (FaceIcon) icon;
				faceText.add(faceIcon.getFaceName());
			}
		}
		String currText = "", message = "";
		int k = 0;
		for (int i = 0; i < size; i++) {
			try {
				currText = doc2.getText(i, 1);
				String targetName = doc2.getCharacterElement(i).getName();
				if (targetName.equals("icon")) {
					message += faceText.get(k++);
				} else if (targetName.equals("content")) {
					message += currText;
				} else if (targetName.equals("component")) {
				}
			} catch (BadLocationException e) {
			}
		}

		if (message != null && message.length() > 0) {
			if (chatType == ChatType.SINGLE_CHAT) {
				SocketService.sendSingleTextMessage(chatObj, message);
			} else if (chatType == ChatType.GROUP_CHAT) {
				SocketService.sendGroupTextMessage(chatObj, message);
			}
			inputText.setText("");
		}
	}

	public void sendTextPic(String filePath, int width, int height) {
		if (filePath != null && filePath.length() > 0) {
			if (chatType == ChatType.SINGLE_CHAT) {
				SocketService.sendSinglePicMessage(chatObj, filePath, width, height);
			} else if (chatType == ChatType.GROUP_CHAT) {
				SocketService.sendGroupPicMessage(chatObj, filePath, width, height);
			}
		}
	}

	public void actionPerformed(ActionEvent event) {
		Object object = event.getSource();
		if (object == sendButton) {
			sendTextd();
		} else if (object == faceBtn) {
			if (face == null) {
				face = new FaceDialog(Startup.mainFrame, true);
				face.setSize(width - 20, 480);
				face.init();
			}
			face.setLocation(Startup.mainFrame.getX() + 310, Startup.mainFrame.getY() + 60);
			face.setVisible(true);
		} else if (object == picBtn) {
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
						String fileSize = res.getDataForRtn().getString("fileSize");
						String filePath = res.getDataForRtn().getString("filePath");
						int[] size = {0, 0};
						if(null != fileSize && fileSize.indexOf(",") != -1){
							String[] sizeArr = fileSize.split(",");
							size[0] = CommonUtils.stringToInt(sizeArr[0]);
							size[1] = CommonUtils.stringToInt(sizeArr[1]);
						}
						Startup.mainWindow.getChatPanel().sendTextPic(filePath, size[0], size[1]);
					}
				} catch (Exception e) {
				}
			}
		} else if(object == fontBtn){
			if (fontDialog == null) {
				fontDialog = new FontDialog(Startup.mainFrame, true);
				fontDialog.setSize(width - 20, 60);
				fontDialog.init();
			}
			fontDialog.setLocation(Startup.mainFrame.getX() + 310, Startup.mainFrame.getY() + 480);
			fontDialog.render();
			fontDialog.setVisible(true);
		} else if(object == plusBtn){
			pop.show(this, plusBtn.getX() - 109 + plusBtn.getWidth(), plusBtn.getY() + plusBtn.getHeight() - 3);
		}
	}

	public void keyReleased(KeyEvent e) {

	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
	}
	
	/**
	 * 菜单动作
	 * 
	 * @param e
	 */
	public void menuAction(ActionEvent e) {
		String str = e.getActionCommand();
		if (str.equals(addFriendMenu.getText())) { // 添加朋友
			AddFriendDialog friendDialog = new AddFriendDialog(Startup.mainFrame, true);
			friendDialog.setSize(760, 560);
			friendDialog.setLocation(Startup.mainFrame.getX() + (Startup.mainFrame.getWidth() - 760)/2, 
					Startup.mainFrame.getY() + (Startup.mainFrame.getHeight() - 560)/2);
			friendDialog.init();
			friendDialog.setVisible(true);
		} else if (str.equals(findGroupMenu.getText())) { // 查找群组
			AddGroupDialog groupDialog = new AddGroupDialog(Startup.mainFrame, true);
			groupDialog.setSize(760, 560);
			groupDialog.setLocation(Startup.mainFrame.getX() + (Startup.mainFrame.getWidth() - 760)/2, 
					Startup.mainFrame.getY() + (Startup.mainFrame.getHeight() - 560)/2);
			groupDialog.init();
			groupDialog.setVisible(true);
		} else if (str.equals(startGroupChatMenu.getText())) { // 发起群聊
			StartGroupDialog groupDialog = new StartGroupDialog(Startup.mainFrame, true);
			groupDialog.setSize(760, 560);
			groupDialog.setLocation(Startup.mainFrame.getX() + (Startup.mainFrame.getWidth() - 760)/2, 
					Startup.mainFrame.getY() + (Startup.mainFrame.getHeight() - 560)/2);
			groupDialog.init();
			groupDialog.setVisible(true);
		} else if (str.equals(faceingGroupMenu.getText())) { // 面对面建群
			facingDialog = new FacingGroupDialog(Startup.mainFrame, true);
			facingDialog.setSize(760, 560);
			facingDialog.setLocation(Startup.mainFrame.getX() + (Startup.mainFrame.getWidth() - 760)/2, 
					Startup.mainFrame.getY() + (Startup.mainFrame.getHeight() - 560)/2);
			facingDialog.init();
			facingDialog.setVisible(true);
			facingDialog = null;
		}
	}
	
	public FacingGroupDialog getFacingDialog(){
		return facingDialog;
	}

	class LoadChatThread implements Runnable {
		private ChatType type;

		public LoadChatThread(ChatType type) {
			this.type = type;
		}

		@Override
		public void run() {
			List<WSData> datas = new ArrayList<WSData>();
			if (this.type == ChatType.SINGLE_CHAT) {
				Response res = SocketService.loadUserChatHisList(chatObj.getString("userId"));
				if (res.isSuccess()) {
					JSONObject dataJson = res.getRtn().getJSONObject("data");
					if (null != dataJson) {
						JSONArray content = dataJson.getJSONArray("content");
						if (content != null && content.size() > 0) {
							String msgContent = null;
							WSData wsData = null;
							JSONObject body = null;
							for (int i = (content.size() - 1); i >= 0; i--) {
								try {
									wsData = new WSData();
									msgContent = content.getJSONObject(i).getString("msgContent");
									wsData.setUrl("chat.single-chat");
									body = JSONObject.parseObject(msgContent);
									if (body.containsKey("fromUid") && body.getString("fromUid").equals(UserData.user.getString("userId"))) {
										body.remove("fromUid");
									}
									wsData.setBody(body);
									datas.add(wsData);
								} catch (Exception e) {
								}
							}
						}
					}
				}
			} else if (this.type == ChatType.GROUP_CHAT) {
				Response res = SocketService.loadGroupChatHisList(chatObj.getString("groupId"));
				if (res.isSuccess()) {
					JSONObject dataJson = res.getRtn().getJSONObject("data");
					if (null != dataJson) {
						JSONArray content = dataJson.getJSONArray("content");
						if (content != null && content.size() > 0) {
							datas = new ArrayList<WSData>();
							String msgContent = null;
							WSData wsData = null;
							JSONObject body = null;
							for (int i = (content.size() - 1); i >= 0; i--) {
								try {
									wsData = new WSData();
									msgContent = content.getJSONObject(i).getString("msgContent");
									wsData.setUrl("chat.group-chat");
									body = JSONObject.parseObject(msgContent);
									if (body.containsKey("fromUid") && body.getString("fromUid").equals(UserData.user.getString("userId"))) {
										body.remove("fromUid");
									}
									wsData.setBody(body);
									datas.add(wsData);
								} catch (Exception e) {
								}
							}
						}
					}
				}
			}
			renderChat(datas);
		}

	}

}
