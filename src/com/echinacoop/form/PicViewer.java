package com.echinacoop.form;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.filechooser.FileSystemView;

import com.echinacoop.Startup;
import com.echinacoop.utils.GraphicsUtils;
import com.yinsin.utils.FileUtils;

@SuppressWarnings("serial")
public class PicViewer extends JFrame {
	private PicViewer self;
	private int width = 0;
	private int height = 0;
	private File file;
	private BufferedImage image;
	private JLabel imageBox = null;
	private CButton closeBtn;
	private CButton fullBtn;
	private CButton saveBtn;

	private boolean isFullScreen = false;

	public PicViewer(File file) {
		this.self = this;
		this.file = file;
		this.setTitle("GXBChat 图片预览器");
		this.setIconImage(Startup.imageIcon.getImage()); 
		this.setUndecorated(true);
		this.setResizable(false);
		try {
			image = ImageIO.read(file);
			
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Dimension scmSize = toolkit.getScreenSize();
			width = image.getWidth();
			height = image.getHeight();
			if (width >= scmSize.width) {
				width = scmSize.width;
			} else if(width < 600){
				width = 600;
			}
			if (height >= scmSize.height) {
				height = scmSize.height;
			} else if(height < 400){
				height = 400;
			}
			this.setSize(width, height);
			this.setLocation(scmSize.width / 2 - (width / 2), scmSize.height / 2 - (height / 2));
			this.setVisible(true);
		
		} catch (IOException e) {
		}
	}

	public void showImage() {
		closeBtn = new CButton(new ImageIcon(GraphicsUtils.getImg("/close_24px.png")));
		closeBtn.setBounds(width - 27, 3, 24, 24);
		closeBtn.setToolTipText("关闭");
		this.add(closeBtn);

		// nofullscreen_24px.png
		fullBtn = new CButton(new ImageIcon(GraphicsUtils.getImg("/fullscreen_24px.png")));
		fullBtn.setBounds(width / 2 - 29, height - 34, 24, 24);
		fullBtn.setToolTipText("全屏显示");
		this.add(fullBtn);

		saveBtn = new CButton(new ImageIcon(GraphicsUtils.getImg("/save_24px.png")));
		saveBtn.setBounds(width / 2 + 5, height - 34, 24, 24);
		saveBtn.setToolTipText("保存图片到本地");
		this.add(saveBtn);

		imageBox = new JLabel(new ImageIcon(image));
		imageBox.setBounds(0, 0, image.getWidth(), image.getHeight());
		imageBox.setOpaque(true);
		imageBox.setBackground(new Color(239, 243, 249));
		this.add(imageBox);

		imageBox.addMouseListener(new CMouseListener());
		imageBox.addMouseMotionListener(new CMouseMotionAdapter());

		imageBox.setBorder(roverBorder);

		closeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				self.setVisible(false);
				self.dispose();
			}
		});

		fullBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				Dimension scmSize = toolkit.getScreenSize();

				if (isFullScreen) {
					isFullScreen = false;
					self.setBounds(scmSize.width / 2 - (width / 2), scmSize.height / 2 - (height / 2), width, height);
					self.setAlwaysOnTop(false);

					closeBtn.setBounds(width - 27, 3, 24, 24);
					fullBtn.setBounds(width / 2 - 29, height - 34, 24, 24);
					saveBtn.setBounds(width / 2 + 5, height - 34, 24, 24);

					imageBox.setBounds(0, 0, width, height);
				} else {
					isFullScreen = true;
					self.setBounds(0, 0, scmSize.width, scmSize.height);
					self.setAlwaysOnTop(true);

					closeBtn.setBounds(scmSize.width - 27, 3, 24, 24);
					fullBtn.setBounds(scmSize.width / 2 - 29, scmSize.height - 34, 24, 24);
					saveBtn.setBounds(scmSize.width / 2 + 5, scmSize.height - 34, 24, 24);

					imageBox.setBounds(0, 0, scmSize.width, scmSize.height);
				}
			}
		});

		saveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				FileSystemView fsv = FileSystemView.getFileSystemView(); // 得到系统视图对象
				File root = fsv.getHomeDirectory(); // 取桌面的路径
				JFileChooser chooser = new JFileChooser(root.getPath());
				chooser.setSelectedFile(new File(root.getPath() + "\\" + file.getName()));
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.setFileFilter(new CFileFilter());
				int result = chooser.showSaveDialog(null);
				if (result == JFileChooser.OPEN_DIALOG) {
					File target = new File(chooser.getCurrentDirectory() + "\\" + chooser.getSelectedFile().getName());
					FileUtils.copyFile(file, target);
				}
			}
		});
	}

	public static Border roverBorder = new Border() {
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			g.setColor(new Color(80, 80, 80));
			g.drawRect(x, y, width - 2, height - 2);
		}

		public Insets getBorderInsets(Component c) {
			return new Insets(2, 2, 2, 2);
		}

		public boolean isBorderOpaque() {
			return true;
		}
	};

	private static boolean isGragged = false;
	private int x = 0, y = 0;

	private class CMouseListener implements MouseListener {
		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			isGragged = true;
			x = e.getX();
			y = e.getY();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			isGragged = false;
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}
	}

	private class CMouseMotionAdapter extends MouseMotionAdapter {
		int left = 0, top = 0;

		@Override
		public void mouseDragged(MouseEvent e) {
			if (isGragged && !isFullScreen) {
				left = self.getLocation().x;
				top = self.getLocation().y;
				self.setLocation(left + e.getX() - x, top + e.getY() - y);
			}
		}
	}

}
