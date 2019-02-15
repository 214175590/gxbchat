package com.echinacoop.modal;

import java.awt.Color;

import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.echinacoop.utils.YColor;

public class FontAttrib {

	/**
	 * 字体的属性类
	 */
	public static final int GENERAL = 0; // 常规

	public static final int BOLD = 1; // 粗体

	public static final int ITALIC = 2; // 斜体

	public static final int BOLD_ITALIC = 3; // 粗斜体
	
	public static final int UNDERLINE = 4; // 下划线
	
	public static final int BOLD_UNDERLINE = 5; // 粗体 + 下划线
	
	public static final int ITALIC_UNDERLINE = 6; // 斜体 + 下划线
	
	public static final int BOLD_ITALIC_UNDERLINE = 7; // 粗斜体 + 下划线

	private SimpleAttributeSet attrSet = new SimpleAttributeSet(); // 属性集

	private String text = "", name = "微软雅黑"; // 要输入的文本和字体名称

	private int style = 0, size = 12; // 样式和字号

	private Color color = Color.BLACK, backColor = null; // 文字颜色和背景颜色

	/**
	 * 一个空的构造（可当做换行使用）
	 */
	public FontAttrib() {
	}

	/**
	 * 返回属性集
	 *
	 * @return
	 */
	public SimpleAttributeSet getAttrSet() {
		if (name != null){
			StyleConstants.setFontFamily(attrSet, name);
		}
		if (style == FontAttrib.GENERAL) {
			StyleConstants.setBold(attrSet, false);
			StyleConstants.setItalic(attrSet, false);
			StyleConstants.setUnderline(attrSet, false);
		} else if (style == FontAttrib.BOLD) {
			StyleConstants.setBold(attrSet, true);
			StyleConstants.setItalic(attrSet, false);
			StyleConstants.setUnderline(attrSet, false);
		} else if (style == FontAttrib.ITALIC) {
			StyleConstants.setBold(attrSet, false);
			StyleConstants.setItalic(attrSet, true);
			StyleConstants.setUnderline(attrSet, false);
		} else if (style == FontAttrib.BOLD_ITALIC) {
			StyleConstants.setBold(attrSet, true);
			StyleConstants.setItalic(attrSet, true);
			StyleConstants.setUnderline(attrSet, false);
		} else if (style == FontAttrib.UNDERLINE) {
			StyleConstants.setBold(attrSet, false);
			StyleConstants.setItalic(attrSet, false);
			StyleConstants.setUnderline(attrSet, true);
		} else if (style == FontAttrib.BOLD_UNDERLINE) {
			StyleConstants.setBold(attrSet, true);
			StyleConstants.setItalic(attrSet, false);
			StyleConstants.setUnderline(attrSet, true);
		} else if (style == FontAttrib.ITALIC_UNDERLINE) {
			StyleConstants.setBold(attrSet, false);
			StyleConstants.setItalic(attrSet, true);
			StyleConstants.setUnderline(attrSet, true);
		} else if (style == FontAttrib.BOLD_ITALIC_UNDERLINE) {
			StyleConstants.setBold(attrSet, true);
			StyleConstants.setItalic(attrSet, true);
			StyleConstants.setUnderline(attrSet, true);
		}
		StyleConstants.setFontSize(attrSet, size);
		if (color != null){
			StyleConstants.setForeground(attrSet, color);
		}
		if (backColor != null){
			StyleConstants.setBackground(attrSet, backColor);
		}
		return attrSet;
	}

	/**
	 * 设置属性集
	 *
	 * @param attrSet
	 */
	public void setAttrSet(SimpleAttributeSet attrSet) {
		this.attrSet = attrSet;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getBackColor() {
		return backColor;
	}

	public void setBackColor(Color backColor) {
		this.backColor = backColor;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getStyle() {
		return style;
	}

	public void setStyle(int style) {
		this.style = style;
	}
	
	public void setFont(JSONObject fontJson){
		if(null != fontJson){
			if(fontJson.containsKey("face")){
				name = fontJson.getString("face");
			}
			if(fontJson.containsKey("size")){
				size = fontJson.getIntValue("size") * 3 + 8;
			}
			if(fontJson.containsKey("color")){
				String colorStr = fontJson.getString("color");
				color = YColor.init().getColorByRGB(colorStr);
			}
			if(fontJson.containsKey("style")){
				JSONArray styleJson = fontJson.getJSONArray("style");
				int i = styleJson.getInteger(0);
				int b = styleJson.getInteger(1);
				int u = styleJson.getInteger(2);
				String s = i + "" + b + "" + u;
				if(s.equals("000")){
					style = GENERAL;
				} else if(s.equals("001")){
					style = UNDERLINE;
				} else if(s.equals("010")){
					style = BOLD;
				} else if(s.equals("011")){
					style = BOLD_UNDERLINE;
				} else if(s.equals("100")){
					style = ITALIC;
				} else if(s.equals("101")){
					style = ITALIC_UNDERLINE;
				} else if(s.equals("110")){
					style = BOLD_ITALIC;
				} else if(s.equals("111")){
					style = BOLD_ITALIC_UNDERLINE;
				}
			}
		}
	}

}
