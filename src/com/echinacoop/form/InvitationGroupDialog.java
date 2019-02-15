package com.echinacoop.form;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.echinacoop.Startup;
import com.echinacoop.controller.SocketService;
import com.echinacoop.modal.Response;
import com.echinacoop.utils.JSONUtils;
import com.echinacoop.utils.UserData;

public class InvitationGroupDialog extends JDialog {
	
	private InvitationGroupDialog self;
	private JSONObject group;
	
	int width = 760;
	int height = 560;
	
	JScrollPane mainSPane = null;
	JPanel mainPanel = null;
	
	JPanel out = null;
	JScrollPane jsPane = null;
	
	JLabel infoLabel = null;
	JLabel msgLabel = null;
	JButton startBtn = new JButton("确定");
	
	List<UserCheckBox> itemList = new ArrayList<UserCheckBox>();
	Map<String, JPanel> panelMap = new HashMap<String, JPanel>();
	Map<String, Integer> panelItem = new HashMap<String, Integer>();
	
	public InvitationGroupDialog(JFrame parent, boolean modal){
		super(parent, modal);
		self = this;
	}
	
	public void init(){
		width = this.getWidth();
		height = this.getHeight();
		this.setLayout(null);
		this.setTitle("邀请、踢出成员");
		this.setResizable(false);
		
		infoLabel = new JLabel("邀请新成员加入，或者踢出旧成员，请在下方勾选或取消勾选好友");
		infoLabel.setBounds(10, 5, 300, 25);
		this.add(infoLabel);
		
		startBtn.setBounds(width - 140, 5, 100, 30);
		this.add(startBtn);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(null);
		
		mainSPane = new JScrollPane(mainPanel);
		mainSPane.setBounds(5, 40, width - 25, height - 80);
		this.add(mainSPane);
		mainPanel.setBounds(0, 0, mainSPane.getWidth() - 40, mainSPane.getHeight());
		
		startBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				List<String> addList = getAddUserList();
				List<String> delList = getDeleteUserList();
				if(addList.size() > 0 || delList.size() > 0){
					String groupId = self.group.getString("groupId");
					Response res = SocketService.invitationUserJoinGroup(groupId, addList, delList);
					if(res.isSuccess()){
						Startup.mainWindow.getLeftPanel().getGroupPanel().loadGroupInfo(groupId);
						self.dispose();
					}
				} else {
					//JOptionPane.showMessageDialog(self, "请选择好友");
					self.dispose();
				}
			}
		});
	}
	
	public void render(JSONObject group){
		this.group = group;
		mainPanel.removeAll();
		panelMap.clear();
		panelItem.clear();
		itemList.clear();
		
		new Thread(){
			public void run(){
				renderUI();
			}
		}.start();
	}
	
	private void renderUI(){
		JSONObject user = null;
		String groupId = null;
		JPanel uPanel = null;
		UserCheckBox box = null;
		int index = 0, i = 0;
		Integer count = 0;
		boolean has = false;
		JSONArray users = group.getJSONArray("users");
		List<JSONObject> userList = new ArrayList<JSONObject>();
		for (int j = 0; j < users.size(); j++) {
			user = users.getJSONObject(j);
			if (null != user) {
				user = JSONUtils.cloneJSONObject(user);
				user.put("groupId", "group-" + user.getString("groupId"));
				user.put("groupName", group.getString("groupName"));
				userList.add(user);
			}
		}
		for(Map.Entry<String, UserItem> entry : UserData.USER_ITEM_MAP.entrySet()){
			user = entry.getValue().getUser();
			userList.add(user);
		}
		Map<String, Integer> indexMap = new HashMap<String, Integer>();
		Map<String, String> userMap = new HashMap<String, String>();
		String userId = null;
		for (int j = 0, k = userList.size(); j < k; j++) {
			try {
				user = userList.get(j);
				userId = user.getString("userId");
				if(userMap.containsKey(userId)){
					continue;
				}
				userMap.put(userId, userId);
				has = exist(user);
				groupId = user.getString("groupId");
				uPanel = panelMap.get(groupId);
				count = panelItem.get(groupId);
				if(!indexMap.containsKey(groupId)){
					indexMap.put(groupId, 0);
				}
				if (null == uPanel) {
					out = new JPanel();
					out.setBorder(BorderFactory.createTitledBorder(user.getString("groupName")));
					out.setLayout(null);
					out.setBounds(5, 5 + (i * 310), mainPanel.getWidth() - 10, 300);
					i++;
					uPanel = new JPanel();
					uPanel.setLayout(null);
					panelMap.put(groupId, uPanel);
					panelItem.put(groupId, 0);
					count = 0;

					jsPane = new JScrollPane(uPanel);
					jsPane.setBounds(4, 20, out.getWidth() - 11, out.getHeight() - 23);
					jsPane.setAutoscrolls(true);
					jsPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
					jsPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
					out.add(jsPane);
					uPanel.setBounds(0, 0, jsPane.getWidth() - 10, jsPane.getHeight() - 2);

					mainPanel.add(out);
					
					mainPanel.setPreferredSize(new Dimension(mainSPane.getWidth() - 40, 20 + (i * 320)));
					mainPanel.revalidate();
				}
				index = indexMap.get(groupId);
				box = new UserCheckBox(user, index++);
				indexMap.put(groupId, index);
				box.setChecked(has);
				itemList.add(box);
				uPanel.add(box);
				count = count + 1;
				panelItem.put(groupId, count);
				box.initCompoments();

				uPanel.setPreferredSize(new Dimension(uPanel.getWidth(), (count / 5 + 1) * box.getHeight() + 20));
				uPanel.revalidate(); //告诉其他部件,我的宽高变了
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
				}
			} catch (Exception e) {
			}
		}
		self.repaint();
	}
	
	public List<String> getAddUserList(){
		List<String> list = new ArrayList<String>();
		for (UserCheckBox item : itemList) {
			if(item.isAdd()){
				list.add(item.getUser().getString("userId"));
			}
		}
		return list;
	}
	
	public List<String> getDeleteUserList(){
		List<String> list = new ArrayList<String>();
		for (UserCheckBox item : itemList) {
			if(item.iSDelete()){
				list.add(item.getUser().getString("userId"));
			}
		}
		return list;
	}
	
	private boolean exist(JSONObject user){
		boolean result = false;
		String userId = user.getString("userId");
		String uid = null;
		JSONArray users = group.getJSONArray("users");
		JSONObject userTemp = null;
		if(null != users){
			for (int i = 0, k = users.size(); i < k; i++) {
				userTemp = users.getJSONObject(i);
				uid = userTemp.getString("userId");
				if(userId.equals(uid)){
					result = true;
					break;
				}
			}
		}
		return result;
	}
}
