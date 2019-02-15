package com.echinacoop.form;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.echinacoop.Startup;
import com.echinacoop.controller.Config;
import com.echinacoop.utils.YColor;

public class FontDialog extends JDialog implements ActionListener, MouseListener {
	private static final long serialVersionUID = 1L;

	private int width = 500;
	private int height = 320;
	private FontDialog self;

	JComboBox<Object> fontBox = null;
	JLabel colorBtn = null;
	JComboBox<Object> sizeBox = null;

	CheckBoxButton iBtn = null;
	CheckBoxButton bBtn = null;
	CheckBoxButton uBtn = null;
	CButton closeBtn = null;
	
	int[] size = { 9, 8, 7, 6, 5, 4, 3, 2 };

	public FontDialog(JFrame parent, boolean modal) {
		super(parent, modal);
		self = this;
	}

	public void init() {
		this.width = this.getWidth();
		this.height = this.getHeight();

		this.setLayout(null);
		this.setUndecorated(true);

		closeBtn = new CButton("保存", "/save_16px.png");
		closeBtn.setBounds(this.getWidth() - 65, 10, 60, 30);
		this.add(closeBtn);

		JLabel label = new JLabel("字体：");
		label.setBounds(10, 10, 40, 30);
		this.add(label);

		String[] fontArr = { "System", "宋体", "楷体_GB2312", "隶书", "幼圆", "黑体", "微软雅黑", "华文彩云", "华文新魏", "方正舒体" };
		fontBox = new JComboBox<Object>(fontArr);
		fontBox.setBounds(50, 10, 150, 30);
		this.add(fontBox);
		fontBox.setSelectedIndex(6);

		JLabel label2 = new JLabel("颜色：");
		label2.setBounds(210, 10, 40, 30);
		this.add(label2);

		colorBtn = new JLabel();
		colorBtn.setBackground(Color.BLACK);
		colorBtn.setBounds(250, 13, 30, 24);
		colorBtn.setOpaque(true);
		this.add(colorBtn);

		JLabel label3 = new JLabel("大小：");
		label3.setBounds(290, 10, 40, 30);
		this.add(label3);
		
		String[] sizeArr = { "二号", "小二", "三号", "小三", "四号", "小四", "五号", "小五" };
		sizeBox = new JComboBox<Object>(sizeArr);
		sizeBox.setBounds(330, 10, 100, 30);
		this.add(sizeBox);
		sizeBox.setSelectedIndex(6);
		
		JLabel label4 = new JLabel("样式：");
		label4.setBounds(440, 10, 40, 30);
		this.add(label4);
		
		iBtn = new CheckBoxButton("I");
		bBtn = new CheckBoxButton("B");
		uBtn = new CheckBoxButton("U");
		iBtn.setBounds(480, 10, 25, 30);
		bBtn.setBounds(505, 10, 25, 30);
		uBtn.setBounds(530, 10, 25, 30);
		this.add(iBtn);
		this.add(bBtn);
		this.add(uBtn);
		
		colorBtn.addMouseListener(this);

		closeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String font = fontBox.getSelectedItem().toString();
				int index = sizeBox.getSelectedIndex();
				int fontSize = size[index];
				Color color = colorBtn.getBackground();
				String colorStr = YColor.init().getRGB(color);
				JSONObject fontJson = new JSONObject();
				List<Integer> style = new ArrayList<Integer>();
				style.add(iBtn.getStatus());
				style.add(bBtn.getStatus());
				style.add(uBtn.getStatus());
				fontJson.put("size", fontSize);
				fontJson.put("face", font);
				fontJson.put("color", colorStr);
				fontJson.put("style", style);
				Config.setMessageFont(fontJson);
				self.setVisible(false);
			}
		});
	}

	public void render() {
		JSONObject fontJSon = Config.getMessageFont();
		if(fontJSon.containsKey("face")){
			String fontName = fontJSon.getString("face");
			fontBox.setSelectedItem(fontName);
		}
		if(fontJSon.containsKey("size")){
			int fontSize = fontJSon.getIntValue("size");
			for (int i = 0; i < size.length; i++) {
				if(size[i] == fontSize){
					sizeBox.setSelectedIndex(i);
					break;
				}
			}
		}
		if(fontJSon.containsKey("color")){
			String colorStr = fontJSon.getString("color");
			Color color = YColor.init().getColorByRGB(colorStr);
			colorBtn.setBackground(color);
		}
		if(fontJSon.containsKey("style")){
			JSONArray style = fontJSon.getJSONArray("style");
			int i = style.getInteger(0);
			int b = style.getInteger(1);
			int u = style.getInteger(2);
			iBtn.setStatus(i);
			bBtn.setStatus(b);
			uBtn.setStatus(u);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		CButton faceBtn = (CButton) e.getSource();
		Startup.mainWindow.getChatPanel().addFace(faceBtn.getAttr1().toString(), faceBtn.getAttr2().toString());
		this.setVisible(false);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Object obj = e.getSource();
		if (obj == colorBtn) {
			Color color = JColorChooser.showDialog(self, "选择字体颜色", Color.yellow);
			if (color != null) {
				colorBtn.setBackground(color);
			}
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

}
