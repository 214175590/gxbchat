package com.echinacoop.form;

import java.awt.Color;
import java.awt.Image;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.text.StyledDocument;

import com.echinacoop.Startup;
import com.echinacoop.controller.SocketService;
import com.echinacoop.modal.Response;
import com.echinacoop.modal.UploadType;
import com.echinacoop.utils.GraphicsUtils;
import com.echinacoop.utils.UserData;
import com.yinsin.utils.CommonUtils;

/**
 * 文本编辑区域
 */
public class CTextPanel extends JTextPane implements MouseListener {

	private static final long serialVersionUID = -2308615404205560110L;
	private JPopupMenu pop = null; // 弹出菜单
	private JMenuItem copy = null, paste = null, pastepic = null, cut = null; // 三个功能菜单
	// private StyledDocument doc = null; // 非常重要插入文字样式就靠它了
	private Clipboard clipboard = null;
	private Transferable content = null;
	private StringBuffer sb = new StringBuffer();

	public CTextPanel() {
		super();
		init();
	}
	
	public CTextPanel(StyledDocument doc) {
		super(doc);
		init();
	}

	public CTextPanel(int width, int height) {
		super();
		init();
	}

	private void init() {
		try { // 使用Windows的界面风格
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.addMouseListener(this);
		this.setVisible(true);
		this.setEditable(true);
		this.setBackground(Color.white);
		pop = new JPopupMenu();
		// doc = this.getStyledDocument();
		pop.add(copy = new JMenuItem("复制"));
		// d = Toolkit.getDefaultToolkit().getScreenSize();
		// this.setSize(d);
		pop.add(paste = new JMenuItem("粘贴"));
		pop.add(pastepic = new JMenuItem("粘贴图片"));
		pop.add(cut = new JMenuItem("剪切"));
		copy.setAccelerator(KeyStroke.getKeyStroke('C', InputEvent.CTRL_MASK));
		paste.setAccelerator(KeyStroke.getKeyStroke('V', InputEvent.CTRL_MASK));

		cut.setAccelerator(KeyStroke.getKeyStroke('X', InputEvent.CTRL_MASK));

		copy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action(e);
			}
		});
		paste.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action(e);
			}
		});

		pastepic.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action(e);
			}
		});
		cut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				action(e);
			}
		});
		this.add(pop);
	}

	/**
	 * 菜单动作
	 * 
	 * @param e
	 */
	public void action(ActionEvent e) {
		clipboard = this.getToolkit().getSystemClipboard();
		content = clipboard.getContents(this);
		String str = e.getActionCommand();
		if (str.equals(copy.getText())) { // 复制
			this.copy();
		} else if (str.equals(paste.getText())) { // 粘贴
			this.paste();
		} else if (str.equals(cut.getText())) { // 剪切
			this.cut();
		} else if (str.equals(pastepic.getText())) {// 粘贴图片
			BufferedImage image = (BufferedImage) getClipboardImage();
			if (image != null) {
				try {
					String userId = UserData.user.getString("userId");
					String fileCode = "P" + userId + CommonUtils.getRandomNumber(8);
					String fileSuffix = ".jpg";
					File file = new File(fileCode + fileSuffix);
					ImageIO.write(image, "jpeg", file);
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
		}
	}

	// 获得图片地址
	public String getPicPath() {
		String path = "";
		if (sb != null) {
			path = sb.toString();
		}
		return path;
	}

	public JPopupMenu getPop() {
		return pop;
	}

	public void setPop(JPopupMenu pop) {
		this.pop = pop;
	}

	/**
	 * 剪切板中是否有文本数据可供粘贴
	 * 
	 * @return true为有文本数据
	 */
	public boolean isClipboardString() {
		boolean st = GraphicsUtils.getClipboardText() == null ? false : true;
		return st;
	}

	/**
	 * 剪切板中是否有图片数据可供粘贴
	 * 
	 * @return true为有文本数据
	 */
	public boolean isClipboardImage() {
		boolean pic = getClipboardImage() == null ? false : true;
		return pic;
	}

	/**
	 * 文本组件中是否具备复制的条件
	 * 
	 * @return true为具备
	 */
	public boolean isCanCopy() {
		boolean b = false;
		int start = this.getSelectionStart();
		int end = this.getSelectionEnd();
		if (start != end)
			b = true;
		return b;
	}

	public Image getClipboardImage() {
		clipboard = this.getToolkit().getSystemClipboard();
		// 获得当前系统剪切板内容到Transferable(这是一个Java提供的剪切板数据包装对象)
		content = clipboard.getContents(this);

		if (content.isDataFlavorSupported(DataFlavor.imageFlavor)) {
			try {
				return (Image) content.getTransferData(DataFlavor.imageFlavor);
			} catch (UnsupportedFlavorException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (content.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			try {
				List<?> files = (List<?>) content.getTransferData(DataFlavor.javaFileListFlavor);
				for (int i = 0; i < files.size();) {
					File f = (File) files.get(i);
					ImageIcon img = new ImageIcon(f.getPath());
					return img.getImage();
				}
			} catch (UnsupportedFlavorException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}

	public void mouseClicked(MouseEvent e) {

	}

	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			copy.setEnabled(isCanCopy());
			paste.setEnabled(isClipboardString());
			pastepic.setEnabled(isClipboardImage());
			cut.setEnabled(isCanCopy());
			pop.show(this, e.getX(), e.getY());
		}
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	/**
	 * 插入图片
	 * 
	 * @param icon
	 */
	private void insertIcon(File file) {
		// doc = this.getStyledDocument();
		this.setCaretPosition(this.getCaretPosition());
		this.insertIcon(new ImageIcon(file.getPath())); // 插入图片
		// insert(new FontAttrib()); // 这样做可以换行
	}

	private void insertIcon(Image image) {
		// doc = this.getStyledDocument();
		this.setCaretPosition(this.getCaretPosition());// doc.getLength()); //
														// 设置插入位置
		this.insertIcon(new ImageIcon(image)); // 插入图片
	}

	public static void main(String[] args) {
		new CTextPanel(400, 300);
	}
}
