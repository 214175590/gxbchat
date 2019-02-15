package com.echinacoop;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.echinacoop.consts.Constants;
import com.echinacoop.controller.ChatHandler;
import com.echinacoop.controller.Config;
import com.echinacoop.controller.GroupHandler;
import com.echinacoop.controller.LoginHandler;
import com.echinacoop.controller.SocketService;
import com.echinacoop.db.CopyDBThread;
import com.echinacoop.form.LoginWindow;
import com.echinacoop.form.MainWindow;
import com.echinacoop.form.MsgTiper;
import com.echinacoop.form.VersionDialog;
import com.echinacoop.modal.Response;
import com.echinacoop.socket.HandleHelper;
import com.echinacoop.utils.UserData;
import com.yinsin.security.MD5;
import com.yinsin.utils.FileUtils;

public class Startup {

	private static int loginFormWidth = 450;
	private static int loginFormHeight = 320;
	private static int mainFormWidth = 1080;
	private static int mainFormHeight = 720;

	private static JFrame loginFrame = null;
	public static JFrame mainFrame = null;
	public static LoginWindow loginForm = null;
	public static MainWindow mainWindow = null;
	
	public static boolean connectionStatus = false;
	public static ImageIcon imageIcon = new ImageIcon(Startup.class.getResource("/logo.png"));
	
	private static SystemTray tray;
	private static TrayIcon trayIcon;
	private static boolean twinkle = false;
	private static MsgTiper msgTiper = null;

	public static void main(String[] args) {
		
		try {
			System.out.println(MD5.md5("15218701151" + MD5.md5("123456")));
			
			System.setProperty("JAVA_TOOL_OPTIONS", "-Dfile.encoding=UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 注册处理类
		try {
			HandleHelper.getInstance().registerHandle(LoginHandler.class, ChatHandler.class, GroupHandler.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			FileUtils.createDir(Config.APP_DIR);
			FileUtils.createDir(Constants.TEMP_DIR);
			Config.loadGlobalConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					System.setProperty("sun.java2d.noddraw", "true");
					BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.translucencySmallShadow;
					BeautyEyeLNFHelper.launchBeautyEyeLNF();
					UIManager.put("RootPane.setupButtonVisible", false);
				} catch (Exception e) {
				}
				loginFrame = new JFrame();
				loginFrame.setIconImage(imageIcon.getImage()); 
				loginFrame.setTitle("GXB Chat v" + Config.APP_VERSION);
				loginFrame.setResizable(false);
				loginFrame.setSize(loginFormWidth, loginFormHeight);
				loginFrame.setLayout(null);
				loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				loginForm = new LoginWindow(loginFormWidth, loginFormHeight);
				loginForm.setBounds(0, 0, loginFormWidth, loginFormHeight);

				loginFrame.add(loginForm);
				loginFrame.setVisible(true);

				Toolkit toolkit = Toolkit.getDefaultToolkit();
				Dimension scmSize = toolkit.getScreenSize();
				loginFrame.setLocation(scmSize.width / 2 - (loginFormWidth / 2), scmSize.height / 2 - (loginFormHeight / 2));
				loginForm.initInput();
				
				try {
					SocketService.connection();
				} catch (Exception e) {
					loginForm.disabledLogin();
				}
			}
		});
	}

	public static void showMainWindow() {
		if (null == mainFrame) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					mainFrame = new JFrame();
					mainFrame.setIconImage(imageIcon.getImage()); 
					mainFrame.setTitle(MessageFormat.format("GXB Chat v{0} - {1}", Config.APP_VERSION, UserData.user.getString("nickName")));
					mainFrame.setResizable(false);
					mainFrame.setSize(mainFormWidth, mainFormHeight);
					mainFrame.setLayout(null);
					mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

					Startup.mainWindow = new MainWindow(mainFormWidth, mainFormHeight);
					Startup.mainWindow.setBounds(0, 0, mainFormWidth, mainFormHeight);

					mainFrame.add(Startup.mainWindow);
					mainFrame.setVisible(true);

					Toolkit toolkit = Toolkit.getDefaultToolkit();
					Dimension scmSize = toolkit.getScreenSize();
					mainFrame.setLocation(scmSize.width / 2 - (mainFormWidth / 2), scmSize.height / 2 - (mainFormHeight / 2));

					mainFrame.setLocationRelativeTo(null);
					mainFrame.addWindowListener(new WindowAdapter() {
						public void windowClosing(WindowEvent e) {
							if(Startup.connectionStatus){
								mainFrame.setVisible(false);								
							} else {
								showLoginWindow();
							}
						}
						public void windowLostFocus(WindowEvent e) {
							//mainFrame.setVisible(true);
						}
						public void windowDeactivated(WindowEvent e) {
							//mainFrame.setVisible(true);
			            }
					});
					
					initTray();
					
					Response res = SocketService.checkAppVersion();
					if(res.isSuccess()){
						JSONArray data = res.getRtn().getJSONArray("data");
						if(null != data && data.size() > 0){
							// 有新版本
							VersionDialog dialog = new VersionDialog(Startup.mainFrame, true, data);
							dialog.setVisible(true);
						}
					}
				}
			});
		} else {
			mainFrame.setVisible(true);
		}
	}

	public static void hideLoginWindow() {
		loginFrame.setVisible(false);
	}

	public static void showLoginWindow() {
		loginFrame.setVisible(true);
		loginForm.initInput();
		if(mainFrame != null){
			mainFrame.dispose();
			mainFrame = null;
		}
		try {
			SocketService.connection();
		} catch (Exception e) {
			loginForm.disabledLogin();
		}
	}
	
	public static void initTray(){
		// 将托盘图标添加到系统的托盘实例中
		try {
			PopupMenu pop = new PopupMenu(); 
			MenuItem exit = new MenuItem("Exit");
			pop.add(exit);
			tray = SystemTray.getSystemTray();
			trayIcon = new TrayIcon(imageIcon.getImage(),"供销通\n版权所有：供销e家", pop);
			trayIcon.setImageAutoSize(true);
			tray.add(trayIcon);
			
			trayIcon.addMouseListener(new MouseListener(){
				@Override
				public void mouseClicked(MouseEvent e) {
					if(e.getButton() == MouseEvent.BUTTON1){
						mainFrame.setVisible(true);
						mainFrame.setAlwaysOnTop(true);
						mainFrame.setAlwaysOnTop(false);
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
			});;
			
			trayIcon.addMouseMotionListener(new MouseMotionListener() {  
                @Override  
                public void mouseMoved(MouseEvent e) {  
                    if(msgTiper == null){
                    	msgTiper = new MsgTiper();
                    } else {
                    	int count = msgTiper.loadMessage();
                		if(count > 0 && twinkle){
                			Dimension sd = Toolkit.getDefaultToolkit().getScreenSize();
                			Insets si = Toolkit.getDefaultToolkit().getScreenInsets(mainFrame.getGraphicsConfiguration());
                			//x, y为坐标定位
                			int x = (e.getX() + msgTiper.getWidth()/2) > sd.width ? 
                					sd.width - msgTiper.getWidth() - 3 : 
                						(e.getX() - msgTiper.getWidth()/2); 
        					int y = sd.height - si.bottom - msgTiper.getHeight();
                			msgTiper.setVisible(true);
                			msgTiper.setLocation(x, y);
                			msgTiper.setAlwaysOnTop(true);
                		}
                		
                		trayIcon.setToolTip(MessageFormat.format("欢迎使用供销通\n当前用户：{0}\n未读消息：{1}\n版权所有：供销e家", UserData.user.getString("nickName"), count));
                    }
                }
  
                @Override  
                public void mouseDragged(MouseEvent e) {  
                }  
            }); 
			
			exit.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					System.exit(-1);
				}
			});
			
		} catch (Exception e1) {
		}
	}
	
	public static void hideMsgTiper(){
		if(msgTiper != null){
			msgTiper.setVisible(false);
			int count = msgTiper.loadMessage();
    		if(count == 0){
    			trayStopTwinkle();
    		}
        }
	}
	
	public static void trayStartTwinkle(){
		if(null != trayIcon && !twinkle){
			twinkle = true;
			new Thread(){
				public void run(){
					boolean flag = true; 
					Toolkit tk = Toolkit.getDefaultToolkit();  
					while(twinkle){
						if(flag){
							trayIcon.setImage(tk.createImage(""));
							flag = false;
						} else {
							trayIcon.setImage(imageIcon.getImage());
							flag = true;
						}
						try {
							Thread.sleep(300);
						} catch (InterruptedException e) {
						}
					}
					trayIcon.setImage(imageIcon.getImage());
				}
			}.start();
		}
	}
	
	public static void trayStopTwinkle(){
		twinkle = false;
	}

}
