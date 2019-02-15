package com.echinacoop.form;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.HashMap;
import java.util.Map;

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
public class CheckBoxButton extends JButton {
	private static final long serialVersionUID = 1L;
	private static Color defBorderColor = new Color(230, 230, 230);
	private static Color roverBorderColor = new Color(180, 180, 220);
	private static Color pressBorderColor = new Color(245, 117, 39);
	
	private Object attr1;
	private Object attr2;
	
	private int status = 0;

	public CheckBoxButton() {
		init();
	}

	public CheckBoxButton(String title) {
		super(title);
		init();
	}

	public CheckBoxButton(ImageIcon ico) {
		super(null, ico);
		init();
	}

	public CheckBoxButton(String title, ImageIcon ico) {
		super(title, ico);
		init();
	}

	public CheckBoxButton(String title, String filePath) {
		ImageIcon ico = new ImageIcon(GraphicsUtils.getImg(filePath));
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
		
		Font oldFont = this.getFont();
		if(this.getText() == "I"){
			Font font = new Font(oldFont.getFontName(), Font.ITALIC, oldFont.getSize());
			this.setFont(font);
		} else if(this.getText() == "B"){
			Font font = new Font(oldFont.getFontName(), Font.BOLD, oldFont.getSize());
			this.setFont(font);
		} else if(this.getText() == "U"){
			Map<Attribute, Object> map = new HashMap<Attribute, Object>();  
			map.put(TextAttribute.FONT, this.getFont());//原字体  
			map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);//增加的属性 
			this.setFont(Font.getFont(map));
		}

		this.addMouseListener(new MouseListener() {

			public void mouseEntered(MouseEvent e) {
				if (isRolloverEnabled() && status == 0) {
					setBorder(roverBorder);
				}
			}

			public void mouseClicked(MouseEvent e) {

			}

			public void mouseExited(MouseEvent e) {
				if (isRolloverEnabled() && status == 0) {
					setBorder(defBorder);
					setForeground(Color.BLACK);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (isRolloverEnabled()) {
					setBorder(pressBorder);
					setForeground(pressBorderColor);
					if(status == 0){
						status = 1;
					} else if(status == 1){
						status = 0;
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (isRolloverEnabled() && status == 0) {
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
		if(status == 0){
			setBorder(defBorder);
			setForeground(Color.BLACK);
		} else if(status == 1){
			setBorder(pressBorder);
			setForeground(pressBorderColor);
		}
	}
	
}
