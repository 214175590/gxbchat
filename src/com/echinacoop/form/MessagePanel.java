package com.echinacoop.form;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.Border;

import com.alibaba.fastjson.JSONObject;
import com.echinacoop.Startup;
import com.echinacoop.consts.Constants;
import com.echinacoop.controller.SocketService;
import com.echinacoop.db.SqlHelper;
import com.echinacoop.modal.ChatType;
import com.echinacoop.modal.MessageStatus;
import com.echinacoop.modal.Response;
import com.echinacoop.utils.GraphicsUtils;
import com.echinacoop.utils.UserData;
import com.echinacoop.utils.Utils;
import com.yinsin.utils.CommonUtils;
import com.yinsin.utils.DateUtils;

@SuppressWarnings("serial")
public class MessagePanel extends BasePanel {
	
	private MessageStatus msgStatus;
	private int width = 0;
	private int height = 0;
	private int page = 1;
	private int size = 10;
	public Map<String, MessageItem> itemMap = new HashMap<String, MessageItem>();
	
	public MessagePanel(MessageStatus msgStatus, int width, int height) {
		this.msgStatus = msgStatus;
		this.width = width;
		this.height = height;
		this.setSize(this.width, this.height);
		this.setLayout(null);
		
		loadList();
	}
	
	public void loadList(){
		
		String status = "01";
		if(msgStatus == MessageStatus.NOREAD){
			status = "00";
		}
		List<Map<String, Object>> list = SqlHelper.loadMessageHistory(status, page, size);
		
		if(null != list){
			itemMap.clear();
			
			JSONObject jsonObj = null, data = null;
			Map<String, Object> dataMap = null;
			String url = "", msgType = "", userId = "", key = "", rowId = "";
			MessageItem item = null;
			int index = 0, tempIndex = -1;
			
			Map<String, List<String>> rowidMap = new HashMap<String, List<String>>();
			List<String> idList = null;
			for (int i = 0, k = list.size(); i < k; i++) {
				dataMap = list.get(i);
				tempIndex = -1;
				jsonObj = new JSONObject();
				rowId = CommonUtils.objectToString(dataMap.get("ROW_ID"), "");
				userId = CommonUtils.objectToString(dataMap.get("USER_ID"), "");
				url = CommonUtils.objectToString(dataMap.get("REQ_URL"), "");
				msgType = CommonUtils.objectToString(dataMap.get("MSG_TYPE"), "");
				
				if(url.equals("chat.friend") && msgType.equals("03")){ //好友请求
					
					data = JSONObject.parseObject((String) dataMap.get("MSG_CONTENT"));
					jsonObj.putAll(data.getJSONObject("fromUser"));
					jsonObj.put("time", data.getLongValue("time"));
					jsonObj.put("rowId", rowId);
					jsonObj.put("fromGroupId", data.get("fromGroupId"));
					jsonObj.put("operaState", dataMap.get("OPERA_STATE"));
					key = url + msgType + jsonObj.getString("userId");
					item = itemMap.get(key);
					idList = rowidMap.get(key);
					if(idList == null){
						idList = new ArrayList<String>();
						rowidMap.put(key, idList);
					}
					idList.add(rowId);
					
					if(item != null){
						if(msgStatus == MessageStatus.NOREAD){
							tempIndex = item.getIndex();
							this.remove(item);
						} else {
							continue;
						}
					}
					if(tempIndex == -1){
						item = new MessageItem(this, ChatType.SINGLE_CHAT, msgStatus, jsonObj, msgType, index++);
					} else {
						item = new MessageItem(this, ChatType.SINGLE_CHAT, msgStatus, jsonObj, msgType, tempIndex);
					}
					item.setIdList(idList);
					item.setItemId(key);
					itemMap.put(url + msgType + jsonObj.getString("userId"), item);
					
				} else if(url.equals("user.invitation-group") && msgType.equals("04")){ //邀请入群请求
					
					data = JSONObject.parseObject((String) dataMap.get("MSG_CONTENT"));
					jsonObj.putAll(data.getJSONObject("group"));
					jsonObj.put("time", data.getLongValue("time"));
					jsonObj.put("fromUid", data.get("fromUid"));
					jsonObj.put("fromUname", data.get("fromUname"));
					jsonObj.put("rowId", rowId);
					jsonObj.put("operaState", dataMap.get("OPERA_STATE"));
					
					key = url + msgType + jsonObj.getString("groupId");
					item = itemMap.get(key);
					idList = rowidMap.get(key);
					if(idList == null){
						idList = new ArrayList<String>();
						rowidMap.put(key, idList);
					}
					idList.add(rowId);
					
					if(item != null){
						if(msgStatus == MessageStatus.NOREAD){
							tempIndex = item.getIndex();
							this.remove(item);
						} else {
							continue;
						}
					}
					if(tempIndex == -1){
						item = new MessageItem(this, ChatType.GROUP_CHAT, msgStatus, jsonObj, msgType, index++);
					} else {
						item = new MessageItem(this, ChatType.GROUP_CHAT, msgStatus, jsonObj, msgType, tempIndex);
					}
					item.setIdList(idList);
					item.setItemId(key);
					itemMap.put(url + msgType + jsonObj.getString("groupId"), item);
				
				} else if(url.equals("chat.join-group") && msgType.equals("05")){ //入群申请请求
					
					data = JSONObject.parseObject((String) dataMap.get("MSG_CONTENT"));
					jsonObj.putAll(data.getJSONObject("fromUser"));
					jsonObj.put("time", data.getLongValue("time"));
					jsonObj.put("groupId", data.get("groupId"));
					jsonObj.put("groupName", data.get("groupName"));
					jsonObj.put("rowId", rowId);
					jsonObj.put("operaState", dataMap.get("OPERA_STATE"));
					
					key = msgType + jsonObj.getString("userId") + jsonObj.getString("groupId");
					item = itemMap.get(key);
					idList = rowidMap.get(key);
					if(idList == null){
						idList = new ArrayList<String>();
						rowidMap.put(key, idList);
					}
					idList.add(rowId);
					
					if(item != null){
						if(msgStatus == MessageStatus.NOREAD){
							tempIndex = item.getIndex();
							this.remove(item);
						} else {
							continue;
						}
					}
					if(tempIndex == -1){
						item = new MessageItem(this, ChatType.SINGLE_CHAT, msgStatus, jsonObj, msgType, index++);
					} else {
						item = new MessageItem(this, ChatType.SINGLE_CHAT, msgStatus, jsonObj, msgType, tempIndex);
					}
					item.setIdList(idList);
					item.setItemId(key);
					itemMap.put(msgType + jsonObj.getString("userId") + jsonObj.getString("groupId"), item);
					
				}
				
			}
			
			for(Entry<String, MessageItem> entry : itemMap.entrySet()){
				this.add(entry.getValue());
				entry.getValue().initCompoments();
			}
			this.height = itemMap.size() * 50 + 100;
			this.setPreferredSize(new Dimension(this.width, this.height));
			this.revalidate(); //告诉其他部件,我的宽高变了
			this.repaint();
		}
		
	}
	
	public void reload(){
		for(Entry<String, MessageItem> entry : itemMap.entrySet()){
			this.remove(entry.getValue());
		}
		
		this.repaint();
		
		loadList();
	}
	
	
	class MessageItem extends ComponentAbs {
		private MessagePanel panel;
		private String itemId = "";
		private int width = 805;
		private int height = 50;
		public int index = 0;
		private int mbStatus = 0;
		private String msgType = "";
		
		private JSONObject jsonObj = null;
		
		private JLabel headImgLabel = null;
		private JLabel nameLabel = null;
		private JLabel operaLabel = null;
		private JLabel timeLabel = null;
		private JLabel msgLabel = null;
		private MessageStatus msgStatus;
		
		CButton agree = null;
		CButton refuse = null;
		CButton close = null;
		
		private ChatType type;
		private BufferedImage image = null;
		private List<String> idList = null;
		
		public MessageItem(MessagePanel panel, ChatType type, MessageStatus msgStatus, JSONObject jsonObj, String msgType, int index){
			this.panel = panel;
			this.type = type;
			this.msgStatus = msgStatus;
			this.jsonObj = jsonObj;
			this.msgType = msgType;
			this.index = index;
		}
		
		public void setItemId(String id){
			this.itemId = id;
		}
		
		public void setIdList(List<String> idList){
			this.idList = idList;
		}
		
		private void initCompoments() {
			this.setLayout(null);
			this.setSize(width, height);
			this.setLocation(0, index * (height - 1));

			Border lineBorder = BorderFactory.createLineBorder(new Color(225, 225, 205));
			this.setBorder(lineBorder);

			try {
				URL url = null;
				if(this.type == ChatType.SINGLE_CHAT){
					url = new URL(Utils.getHeadUrl(jsonObj.getString("headImg")));
					image = ImageIO.read(url);					
					
					headImgLabel = new JLabel();
					Image img = image.getScaledInstance(height - 4, height - 4, Image.SCALE_DEFAULT);
					headImgLabel.setIcon(new ImageIcon(img));
					
				} else if(this.type == ChatType.GROUP_CHAT){
					String headImg = jsonObj.getString("groupImg");
					ImageIcon img = null;
					if(CommonUtils.isBlank(headImg)){
						img = new ImageIcon(GraphicsUtils.getImg("/default_group.png"));
					} else {
						url = new URL(SocketService.serverUrl + headImg);
						img = new ImageIcon(url);
					}
					img.setImage(img.getImage().getScaledInstance(height - 4, height - 4, Image.SCALE_DEFAULT));
					headImgLabel = new JLabel(img);
				}
				headImgLabel.setBounds(2, 2, height - 4, height - 4);
				this.add(headImgLabel);
			} catch (MalformedURLException e) {
			} catch (IOException e1) {
			}
			
			if(this.type == ChatType.SINGLE_CHAT){
				nameLabel = new JLabel(jsonObj.getString("nickName"));
			} else if(this.type == ChatType.GROUP_CHAT){
				nameLabel = new JLabel(jsonObj.getString("groupName"));
			}
			nameLabel.setBounds(52, 2, 225, 22);
			this.add(nameLabel);
			
			// 好友请求
			if(msgType.equals(Constants.MSG_TYPE_ADD_FRIEND)){
				operaLabel = new JLabel("申请加您为好友，附加消息：" + CommonUtils.excNullToString(jsonObj.getString("additional"), "无"));
			} else if(msgType.equals(Constants.MSG_TYPE_INVITATION_GROUP)){//邀请入群
				operaLabel = new JLabel(MessageFormat.format("《{0}》邀请您加入群聊", jsonObj.getString("fromUname")));
			} else if(msgType.equals(Constants.MSG_TYPE_APPLY_GROUP)){//入群申请
				operaLabel = new JLabel(MessageFormat.format("申请加入群聊【{0}】", jsonObj.getString("groupName")));
			}
			operaLabel.setBounds(52, 25, 390, 22);
			this.add(operaLabel);
			
			timeLabel = new JLabel("时间：" + DateUtils.format(new Date(jsonObj.getLongValue("time"))));
			timeLabel.setBounds(width - 320, 2, 160, 22);
			this.add(timeLabel);
			
			msgLabel = new JLabel("", JLabel.CENTER);
			msgLabel.setBounds(width - 110, 16, 70, 22);
			this.add(msgLabel);
			msgLabel.setOpaque(true);
			
			if(msgStatus == MessageStatus.READED){
				msgLabel.setText("已" + Constants.getText("OPERA_RESULT", jsonObj.getString("operaState")));
				
				close = new CButton("", "/close_16px.png");
				close.setBounds(width - 35, 10, 30, 30);
				close.setToolTipText("点击删除此记录");
				this.add(close);
				
				close.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						if(null != idList){
							boolean bool = SqlHelper.deleteChatHistory(idList);
							if(bool){
								for(Entry<String, MessageItem> entry : panel.itemMap.entrySet()){
									if(entry.getValue().itemId.equals(itemId)){
										panel.remove(entry.getValue());
									} else if(entry.getValue().index > index){
										entry.getValue().index = entry.getValue().index - 1;
										entry.getValue().setLocation(0, entry.getValue().index * (height - 1));
									}
								}
								panel.repaint();
							}
						}
					}
				});
				
			} else {
				msgLabel.setVisible(false);
			}
			
			if(msgStatus == MessageStatus.NOREAD){
				agree = new CButton("同意", "/Ok_16px.png");
				agree.setBounds(width - 150, 10, 70, 30);
				refuse = new CButton("拒绝", "/close_16px.png");
				refuse.setBounds(width - 75, 10, 70, 30);
				
				this.add(agree);
				this.add(refuse);
				
				agree.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						agree.setVisible(false);
						refuse.setVisible(false);
						Response res = new Response();
						String groupId = "";
						if(msgType.equals(Constants.MSG_TYPE_ADD_FRIEND)){
							// 发送同意好友请求
							String friendUserId = jsonObj.getString("userId");
							String friendGroupId = jsonObj.getString("fromGroupId");
							res = SocketService.agreeFriendReq("0", friendUserId, friendGroupId);
						} else if(msgType.equals(Constants.MSG_TYPE_INVITATION_GROUP)){//邀请入群
							// 发送同意入群请求
							groupId = jsonObj.getString("groupId");
							res = SocketService.agreeJoinGroup(UserData.user.getString("userId"), groupId, null);
						} else if(msgType.equals(Constants.MSG_TYPE_APPLY_GROUP)){//入群申请
							// 发送同意入群申请
							groupId = jsonObj.getString("groupId");
							res = SocketService.agreeJoinGroup(jsonObj.getString("userId"), groupId, "apply");
						}
						if(res.isSuccess()){
							// 修改记录状态
							String rowId = jsonObj.getString("rowId");
							boolean result = SqlHelper.updateChatHistoryStatus(rowId, Constants.OPERA_RESULT_AGREE);
							if(result){
								msgLabel.setText("已同意");
								msgLabel.setVisible(true);
								if(msgType.equals(Constants.MSG_TYPE_INVITATION_GROUP)){//邀请入群
									// 新增群
									Startup.mainWindow.getLeftPanel().getGroupPanel().addNewGroup(groupId);
								}
							} else {
								agree.setVisible(true);
								refuse.setVisible(true);
							}
						} else {
							agree.setVisible(true);
							refuse.setVisible(true);
						}
						
						Startup.mainWindow.getHeadPanel().showMsgView();
					}
				});
				
				refuse.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						agree.setVisible(false);
						refuse.setVisible(false);
						String rowId = jsonObj.getString("rowId");
						boolean result = SqlHelper.updateChatHistoryStatus(rowId, Constants.OPERA_RESULT_REFUSE);
						if(result){
							msgLabel.setText("已拒绝");
							msgLabel.setVisible(true);
						} else {
							agree.setVisible(true);
							refuse.setVisible(true);
						}	
						
						Startup.mainWindow.getHeadPanel().showMsgView();
					}
				});
			}
		}
		
		public void mouseEntered(MouseEvent e) {
			if(mbStatus != 2){
				mbStatus = 1;
			}
			this.repaint();
		}

		public void mouseExited(MouseEvent e) {
			if(mbStatus != 2){
				mbStatus = 0;
			}
			this.repaint();
		}
		
		public void mouseReleased(MouseEvent e) {
			if(mbStatus != 2){
				mbStatus = 1;
			}
			this.repaint();
		}

		public void mouseClicked(MouseEvent e) {
			if(e.getButton() == 1){
				//
				
			}
		}
		
		public void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;

			// 绘制边框
			if (mbStatus == 0) {
				g2.setPaint(new GradientPaint(0, this.getWidth(), Constants.BACK_COLOR_WHITE, 0, this.getHeight(), Constants.BACK_COLOR_WHITE));
				g2.fillRect(0, 0, this.getWidth(), this.getHeight());
			} else if (mbStatus == 1) {// 鼠标进入
				g2.setPaint(new GradientPaint(0, this.getWidth(), Constants.ITEM_BACK_HOVER_COLOR, 0, this.getHeight(), Constants.ITEM_BACK_HOVER_COLOR));
				g2.fillRect(0, 0, this.getWidth(), this.getHeight());
			} else if (mbStatus == 2) {// 鼠标按下
				g2.setPaint(new GradientPaint(0, this.getWidth(), Constants.ITEM_BACK_PRESS_COLOR, 0, this.getHeight(), Constants.ITEM_BACK_PRESS_COLOR));
				g2.fillRect(0, 0, this.getWidth(), this.getHeight());
			}
			// 消除锯齿
			// UIUtils.setFractionalmetricsOn(g2);
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}
		
	}
	
	
}
