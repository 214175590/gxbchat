package com.echinacoop.form;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.Border;

import com.echinacoop.utils.GraphicsUtils;

public class VoiceButton extends JButton {
	private static final long serialVersionUID = 1L;
	private static Color defBorderColor = new Color(230, 230, 230);
	private static Color roverBorderColor = new Color(180, 180, 220);
	private static Color pressBorderColor = new Color(200, 130, 130);

	private File voice;

	public VoiceButton() {
		init();
	}

	private void init() {
		this.setIcon(new ImageIcon(GraphicsUtils.getImg("/Sound_16px.png")));
		this.setText(" ");
		this.setPreferredSize(new Dimension(100, 35));
		this.setOpaque(false);
		this.setBorder(defBorder);
		this.setContentAreaFilled(false);
		this.setFocusPainted(false);
		this.setRolloverEnabled(true);

		this.addMouseListener(new MouseListener() {

			public void mouseEntered(MouseEvent e) {
				if (isRolloverEnabled()) {
					setBorder(roverBorder);
				}
			}

			public void mouseClicked(MouseEvent e) {
				// 播放语音
				/*try {
					String name = CommonUtils.getUUID() + ".wav";
					File newFile = new File("g:/" + name);
					decodeAAC(voice.getAbsolutePath(), "g:/" + name);
					
					new TestMusic(newFile).play();
				} catch (Exception ex) {
					ex.printStackTrace();
				}*/
				setText("请到手机上查看");
			}

			public void mouseExited(MouseEvent e) {
				if (isRolloverEnabled()) {
					setBorder(defBorder);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (isRolloverEnabled()) {
					setBorder(pressBorder);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (isRolloverEnabled()) {
					setBorder(roverBorder);
				}
			}
		});
	}

	public static Border roverBorder = new Border() {

		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			g.setColor(roverBorderColor);
			g.drawRect(x, y, width - 1, height - 1);
		}

		public Insets getBorderInsets(Component c) {
			return new Insets(1, 1, 1, 1);
		}

		public boolean isBorderOpaque() {
			return true;
		}
	};

	public static Border pressBorder = new Border() {

		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			g.setColor(pressBorderColor);
			g.drawRect(x, y, width - 1, height - 1);
		}

		public Insets getBorderInsets(Component c) {
			return new Insets(1, 1, 1, 1);
		}

		public boolean isBorderOpaque() {
			return true;
		}
	};

	public static Border defBorder = new Border() {

		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			g.setColor(defBorderColor);
			g.drawRect(x, y, width - 1, height - 1);
		}

		public Insets getBorderInsets(Component c) {
			return new Insets(1, 1, 1, 1);
		}

		public boolean isBorderOpaque() {
			return true;
		}
	};

	public File getVoice() {
		return voice;
	}

	public void setVoice(File voice) {
		this.voice = voice;
	}

}
