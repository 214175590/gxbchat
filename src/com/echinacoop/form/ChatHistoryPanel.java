package com.echinacoop.form;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.sql.Date;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyleContext;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.echinacoop.db.SqlHelper;
import com.echinacoop.modal.FontAttrib;
import com.echinacoop.modal.ImageType;
import com.echinacoop.modal.Message;
import com.echinacoop.modal.MessageType;
import com.echinacoop.modal.User;
import com.echinacoop.modal.WSData;
import com.echinacoop.utils.GraphicsUtils;
import com.echinacoop.utils.MessageUtils;
import com.echinacoop.utils.UserData;
import com.echinacoop.utils.Utils;
import com.yinsin.utils.DateUtils;

public class ChatHistoryPanel extends BasePanel {
	
	private static JTree tree = null;
	private DefaultMutableTreeNode rootNode;
	private JScrollPane treeScrollPane;
	private JScrollPane messageScrollPane;
	
	private JSONObject chatObj = new JSONObject();
	
	StyleContext styleContext = new StyleContext();
	DefaultStyledDocument doc = new DefaultStyledDocument(styleContext);
	JTextPane messageText = new JTextPane(doc);
	FontAttrib enterFont = new FontAttrib();

	public ChatHistoryPanel(int width, int height) {
		this.setSize(width, height);
		this.setLayout(null);
		
		rootNode = new DefaultMutableTreeNode(new User("联系人", ""));
		
		tree = new JTree(rootNode);
		
		User user = null;
		DefaultMutableTreeNode groupNode = null;
		DefaultMutableTreeNode userNode = null;
		Map<String, DefaultMutableTreeNode> tempNodes = new HashMap<String, DefaultMutableTreeNode>();
		JSONObject userJson = null;
		for(Entry<String, UserItem> entry : UserData.USER_ITEM_MAP.entrySet()){
			userJson = entry.getValue().getUser();
			user = new User();
			user.setType(1);
			user.setName(userJson.getString("nickName"));
			user.setUserId(userJson.getString("userId"));
			
			if(tempNodes.containsKey(userJson.getString("groupId"))){
				groupNode = tempNodes.get(userJson.getString("groupId"));
			} else {
				groupNode = new DefaultMutableTreeNode(new User(userJson.getString("groupName"), userJson.getString("groupId")));
				rootNode.add(groupNode);
				tempNodes.put(userJson.getString("groupId"), groupNode);
			}
			userNode = new DefaultMutableTreeNode(user);
			
			groupNode.add(userNode);
		}
		
		JSONObject groupJson = null;
		groupNode = new DefaultMutableTreeNode(new User("我的群组", ""));
		for(Entry<String, GroupItem> entry : UserData.GROUP_ITEM_MAP.entrySet()){
			groupJson = entry.getValue().getGroup();
			
			user = new User();
			user.setType(2);
			user.setName(groupJson.getString("groupName"));
			user.setUserId(groupJson.getString("groupId"));
			userNode = new DefaultMutableTreeNode(user);
					
			groupNode.add(userNode);
		}
		rootNode.add(groupNode);
		
		treeScrollPane = new JScrollPane();
		treeScrollPane.getViewport().add(tree);
		treeScrollPane.setBounds(2, 2, 200, height - 44);
		this.add(treeScrollPane);
		
		tree.expandPath(new TreePath(rootNode));
		
		messageText.setEditable(false);
		messageScrollPane = new JScrollPane();
		messageScrollPane.getViewport().add(messageText);
		messageScrollPane.setBounds(205, 2, width - 210, height - 44);
		this.add(messageScrollPane);
		
		// 添加选择事件
		tree.addTreeSelectionListener(new JTreeSelectionListener());
	}
	
	public void loadSingleChatHistory(String friendId){
		List<Map<String, Object>> msgList = SqlHelper.loadSingleChatHistory(friendId, "02", 1, 30);
		List<WSData> datas = new ArrayList<WSData>();
		Map<String, Object> dataMap = null;
		WSData wsData = null;
		if(null != msgList){
			for (int i = 0, k = msgList.size(); i < k; i++) {
				dataMap = msgList.get(i);
				wsData = new WSData();
				wsData.setUrl((String) dataMap.get("REQ_URL"));
				wsData.setBody(JSONObject.parseObject((String) dataMap.get("MSG_CONTENT")));
				datas.add(wsData);
			}
		}
		renderChat(datas);
	}
	
	public void loadGroupChatHistory(String groupId){
		List<Map<String, Object>> msgList = SqlHelper.loadGroupChatHistory(groupId, "02", 1, 30);
		List<WSData> datas = new ArrayList<WSData>();
		Map<String, Object> dataMap = null;
		WSData wsData = null;
		if(null != msgList){
			for (int i = 0, k = msgList.size(); i < k; i++) {
				dataMap = msgList.get(i);
				wsData = new WSData();
				wsData.setUrl((String) dataMap.get("REQ_URL"));
				wsData.setBody(JSONObject.parseObject((String) dataMap.get("MSG_CONTENT")));
				datas.add(wsData);
			}
		}
		renderChat(datas);
	}
	
	public void renderChat(List<WSData> datas) {
		messageText.setText("");
		
		if (datas != null) {
			for (WSData wsData : datas) {
				showMessage(wsData, false);
			}

			scrollChatPanel();
		}
	}
	
	public void showMessage(WSData wsData, boolean isscroll) {
		try {
			JSONObject body = (JSONObject) wsData.getBody();
			JSONObject user = null;
			// 说话者
			FontAttrib sendUser = new FontAttrib();
			sendUser.setColor(new Color(100, 100, 240));
			if (wsData.getUrl().equals("chat.single-chat")) {
				if (body.containsKey("fromUid")) {
					user = chatObj;
				} else {
					user = UserData.user;
					sendUser.setColor(new Color(0, 120, 20));
				}
			} else if (wsData.getUrl().equals("chat.group-chat")) {
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
	
	private void scrollChatPanel(){
		try {
			Point p = new Point();
			/*JScrollBar sBar = messageScrollPane.getVerticalScrollBar();
			System.out.println(sBar.getHeight());
			System.out.println(messageText.getHeight());*/
			p.setLocation(0, messageText.getHeight());
			messageScrollPane.getViewport().setViewPosition(p);
			
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
	
	
	class JTreeSelectionListener implements TreeSelectionListener {
		public void valueChanged(TreeSelectionEvent e) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			if (node == null)
				return;
			Object object = node.getUserObject();
			if (object != null) { // node.isLeaf() //node.isRoot()
				User user = (User) object;
				if (user != null) {
					int level = node.getLevel();
					if (level == 0) { // 最顶层
												
					} else if (level == 1) { // 分组层
						
					} else if (level == 2) { // 用户层
						user = (User) node.getUserObject();
						System.out.println(node.getUserObject().toString());
						System.out.println(user.getUserId());
						if(user.getType() == 1){
							UserItem item = UserData.USER_ITEM_MAP.get(user.getUserId());
							if(null != item){
								chatObj = item.getUser(); 
								loadSingleChatHistory(user.getUserId());
							}
						} else if(user.getType() == 2){
							GroupItem item = UserData.GROUP_ITEM_MAP.get(user.getUserId());
							if(null != item){
								chatObj = item.getGroup();
								loadGroupChatHistory(user.getUserId());
							}
						}
						
					}
				}
			}
		}
	}
	
	
}
