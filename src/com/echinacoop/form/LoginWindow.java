package com.echinacoop.form;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.alibaba.fastjson.JSONObject;
import com.echinacoop.consts.Constants;
import com.echinacoop.controller.Config;
import com.echinacoop.controller.SocketService;
import com.echinacoop.db.CopyDBThread;
import com.echinacoop.modal.Response;
import com.echinacoop.utils.UserData;
import com.yinsin.other.LogHelper;
import com.yinsin.security.AES;
import com.yinsin.utils.CommonUtils;

public class LoginWindow extends JPanel {
	private static final LogHelper logger = LogHelper.getLogger(LoginWindow.class);
	private int width = 450;
	private int height = 320;

	private JLabel accountLabel;
	private JLabel passwordLabel;
	private JLabel infoLabel;

	private JTextField accountText;
	private JPasswordField passwordText;

	private JButton submitBtn;

	public LoginWindow(int width, int height) {
		this.width = width;
		this.height = height;
		initComponents();
	}

	private void initComponents() {
		this.setLayout(null);
		this.setBackground(Color.WHITE);

		accountLabel = new JLabel("账 号：");
		accountLabel.setBounds(80, 30, 60, 30);
		accountLabel.setFont(Constants.LABEL_TEXT_FONT);
		this.add(accountLabel);

		accountText = new JTextField("");
		accountText.setBounds(150, 30, 200, 30);
		accountText.setFont(Constants.LABEL_TEXT_FONT);
		this.add(accountText);

		passwordLabel = new JLabel("密 码：");
		passwordLabel.setBounds(80, 80, 60, 30);
		passwordLabel.setFont(Constants.LABEL_TEXT_FONT);
		this.add(passwordLabel);

		passwordText = new JPasswordField("");
		passwordText.setBounds(150, 80, 200, 30);
		passwordText.setFont(Constants.LABEL_TEXT_FONT);
		this.add(passwordText);

		infoLabel = new JLabel("");
		infoLabel.setBounds(80, 130, 300, 30);
		infoLabel.setFont(Constants.LABEL_TEXT_FONT);
		this.add(infoLabel);

		submitBtn = new JButton("登录");
		submitBtn.setBounds(155, 220, 120, 30);
		submitBtn.setFont(Constants.LABEL_TEXT_FONT);
		this.add(submitBtn);

		submitBtn.addActionListener(new JActionListener());
		
		accountText.addKeyListener(new JKeyListener());
		passwordText.addKeyListener(new JKeyListener());
	}
	
	public void initInput(){
		String acc = Config.GLOBAL_CONFIG.getString("LAST_ACCOUNT");
		if(CommonUtils.isNotBlank(acc)){
			accountText.setText(acc);
			String pwd = Config.GLOBAL_CONFIG.getString("LAST_ACCOUNT_PWD");
			if(CommonUtils.isNotBlank(pwd)){
				passwordText.setText(AES.decrypt(pwd, acc));
			}
		}
	}
	
	private void saveLastAccount(String acc, String pwd){
		Config.GLOBAL_CONFIG.put("LAST_ACCOUNT", acc);
		Config.GLOBAL_CONFIG.put("LAST_ACCOUNT_PWD", AES.encrypt(pwd, acc));
		
		Config.saveGlobalConfig();
	}
	
	public void showMessage(String message, Color color){
		infoLabel.setText(message);
		infoLabel.setForeground(color == null ? Color.BLUE : color);
	}
	
	public void disabledLogin(){
		infoLabel.setText("服务器连接失败，请检查网络或联系管理员");
		infoLabel.setForeground(Color.RED);
		submitBtn.setEnabled(false);
	}

	class JActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String cmd = e.getActionCommand();
			if (cmd.equals("登录")) {
				String account = accountText.getText();
				String password = passwordText.getText();
				if (CommonUtils.isBlank(account)) {
					infoLabel.setText("请输入账号");
					infoLabel.setForeground(Color.RED);
					return;
				}
				if (CommonUtils.isBlank(password)) {
					infoLabel.setText("请输入密码");
					infoLabel.setForeground(Color.RED);
					return;
				}
				infoLabel.setText("");
				Response res = SocketService.httpLogin(account, password);
				if(res.isSuccess()){
					Map<String, Object> rtn = res.getRtn();
					logger.debug("登录http接口成功：" + rtn);
					JSONObject user = (JSONObject) rtn.get("user");
					JSONObject userToken = (JSONObject)user.getJSONObject("userToken");
					UserData.userToken = userToken;
					user.remove("userToken");
					UserData.user = user;
					
					new CopyDBThread(UserData.user.getString("userId")).start();
					
					SocketService.socket.getClient().setUserId(UserData.user.getString("userId"));
					SocketService.login(UserData.user.getString("userId"), UserData.userToken.getString("access_token"));
					infoLabel.setText("登录处理中，请稍候...");
					infoLabel.setForeground(Color.GREEN);
					// 保存最后登录账户
					saveLastAccount(account, password);
					// 加载用户配置
					Config.loadUserConfig(UserData.user.getString("userId"));
				} else {
					infoLabel.setText("登录失败：" + res.getMessage());
					infoLabel.setForeground(Color.RED);
				}
			}
		}

	}
	
	class JKeyListener implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
			
		}

		@Override
		public void keyPressed(KeyEvent e) {
			int code = e.getKeyCode();
			if(code == KeyEvent.VK_ENTER){
				submitBtn.doClick();
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			
		}
	}

}
