package com.echinacoop.form;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DefaultButtonModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.Border;

import com.echinacoop.utils.GraphicsUtils;

/**
 * �Զ���Button
 * 
 * @author Yisin
 * 
 */
public class CButton extends JButton {
	private static final long serialVersionUID = 1L;
	private static Color defBorderColor = new Color(230, 230, 230);
	private static Color roverBorderColor = new Color(180, 180, 220);
	private static Color pressBorderColor = new Color(200, 130, 130);
	
	private Object attr1;
	private Object attr2;

	public CButton() {
		init();
	}

	public CButton(String title) {
		super(title);
		init();
	}

	public CButton(ImageIcon ico) {
		super(null, ico);
		init();
	}

	public CButton(String title, ImageIcon ico) {
		super(title, ico);
		init();
	}

	public CButton(String title, String filePath) {
		ImageIcon ico = new ImageIcon(GraphicsUtils.getImg(filePath));
		// ico.setImage(ico.getImage().getScaledInstance(24, 24,
		// Image.SCALE_DEFAULT));
		setModel(new DefaultButtonModel());
		init(title, ico);
		this.setToolTipText(title);
		init();
	}

	private void init() {
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

			}

			public void mouseExited(MouseEvent e) {
				setExitedBorder();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				setPressBorder();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (isRolloverEnabled()) {
					setBorder(roverBorder);
				}
			}
		});
	}
	
	public void setPressBorder(){
		if (isRolloverEnabled()) {
			setBorder(pressBorder);
		}
	}
	
	public void setExitedBorder(){
		if (isRolloverEnabled()) {
			setBorder(defBorder);
		}
	}

	private static Border roverBorder = new Border() {

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
	
	private static Border pressBorder = new Border() {

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
	
	private static Border defBorder = new Border() {

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

	public Object getAttr1() {
		return attr1;
	}

	public void setAttr1(Object attr1) {
		this.attr1 = attr1;
	}

	public Object getAttr2() {
		return attr2;
	}

	public void setAttr2(Object attr2) {
		this.attr2 = attr2;
	}
	
}
