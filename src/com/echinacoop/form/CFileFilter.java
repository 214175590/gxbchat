package com.echinacoop.form;

import java.io.File;

import javax.swing.filechooser.FileFilter;

public class CFileFilter extends FileFilter {

	@Override
	public boolean accept(File f) {
		if (f.isDirectory())
			return true;
		// 设置为选择以.class为后缀的文件
		return f.getName().endsWith(".jpg") || 
				f.getName().endsWith(".png") || 
				f.getName().endsWith(".gif") || 
				f.getName().endsWith(".bmp") || 
				f.getName().endsWith(".JPG") || 
				f.getName().endsWith(".PNG") || 
				f.getName().endsWith(".GIF") || 
				f.getName().endsWith(".BMP");
	}

	@Override
	public String getDescription() {
		return "*.jpg|*.png|*.gif|*.bmp";
	}

}
