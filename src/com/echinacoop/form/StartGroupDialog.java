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

import com.alibaba.fastjson.JSONObject;
import com.echinacoop.Startup;
import com.echinacoop.controller.SocketService;
import com.echinacoop.modal.Response;
import com.echinacoop.utils.UserData;

public class StartGroupDialog extends JDialog {
	
	private StartGroupDialog self;
	
	int width = 760;
	int height = 560;
	
	JScrollPane mainSPane = null;
	JPanel mainPanel = null;
	
	JPanel out = null;
	JScrollPane jsPane = null;
	
	JLabel infoLabel = null;
	JLabel msgLabel = null;
	JButton startBtn = new JButton("开始群聊");
	
	List<UserCheckBox> itemList = new ArrayList<UserCheckBox>();
	Map<String, JPanel> panelMap = new HashMap<String, JPanel>();
	Map<String, Integer> panelItem = new HashMap<String, Integer>();
	
	public StartGroupDialog(JFrame parent, boolean modal){
		super(parent, modal);
		self = this;
	}
	
	public void init(){
		width = this.getWidth();
		height = this.getHeight();
		this.setLayout(null);
		this.setTitle("发起群聊");
		this.setResizable(false);
		
		infoLabel = new JLabel("选择好友，快速建立群聊");
		infoLabel.setBounds(10, 5, 200, 25);
		this.add(infoLabel);
		
		startBtn.setBounds(width - 140, 5, 100, 30);
		this.add(startBtn);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(null);
		
		mainSPane = new JScrollPane(mainPanel);
		mainSPane.setBounds(5, 40, width - 25, height - 80);
		this.add(mainSPane);
		mainPanel.setBounds(0, 0, mainSPane.getWidth(), mainSPane.getHeight());
		
		startBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				List<String> userList = getCheckedUserList();
				if(null != userList && userList.size() > 0){
					String groupName = getGroupName();
					Response res = SocketService.atFriendCreateGroup(groupName, userList);
					if(res.isSuccess()){
						JSONObject groupJson = res.getDataForRtn();
						Startup.mainWindow.getLeftPanel().getGroupPanel().addNewGroup(groupJson.getString("groupId"));
						self.dispose();
					}
				} else {
					JOptionPane.showMessageDialog(self, "请选择好友");
				}
			}
		});
		
		new Thread(){
			@Override
			public void run() {
				render();
			}
		}.start();
	}
	
	public void render(){
		JSONObject user = null;
		String groupId = null;
		JPanel uPanel = null;
		UserCheckBox box = null;
		int index = 0, i = 0;
		Integer count = 0;
		for(Map.Entry<String, UserItem> entry : UserData.USER_ITEM_MAP.entrySet()){
			try {
				user = entry.getValue().getUser();
				if (null != user) {
					groupId = user.getString("groupId");
					uPanel = panelMap.get(groupId);
					count = panelItem.get(groupId);
					if (null == uPanel) {
						out = new JPanel();
						out.setBorder(BorderFactory.createTitledBorder(user.getString("groupName")));
						out.setLayout(null);
						out.setBounds(10, 10 + (i * 370), mainPanel.getWidth() - 20, 360);
						i++;
						uPanel = new JPanel();
						uPanel.setLayout(null);
						panelMap.put(groupId, uPanel);
						panelItem.put(groupId, 0);
						count = 0;

						jsPane = new JScrollPane(uPanel);
						jsPane.setBounds(4, 20, out.getWidth() - 8, out.getHeight() - 23);
						jsPane.setAutoscrolls(true);
						jsPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
						jsPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
						out.add(jsPane);
						uPanel.setBounds(0, 0, jsPane.getWidth() - 10, jsPane.getHeight() - 2);

						mainPanel.add(out);
					}
					box = new UserCheckBox(user, index++);
					itemList.add(box);
					uPanel.add(box);
					count = count + 1;
					panelItem.put(groupId, count);
					box.initCompoments();

					uPanel.setPreferredSize(new Dimension(uPanel.getWidth() - 10, (count / 5 + 1) * box.getHeight() + 20));
					uPanel.revalidate(); //告诉其他部件,我的宽高变了
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
					}
				}
			} catch (Exception e) {
			}
		}
		self.repaint();
	}
	
	public List<String> getCheckedUserList(){
		List<String> list = new ArrayList<String>();
		for (UserCheckBox item : itemList) {
			if(item.isChecked()){
				list.add(item.getUser().getString("userId"));
			}
		}
		return list;
	}
	
	public String getGroupName(){
		String name = UserData.user.getString("nickName");
		int index = 0;
		for (UserCheckBox item : itemList) {
			if(index < 2){
				if(item.isChecked()){
					name +=	"、" + item.getUser().getString("nickName");
					index++;
				}
			}
		}
		return name;
	}
	
}
