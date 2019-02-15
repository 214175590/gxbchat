package com.echinacoop.utils;

import java.io.File;
import java.util.List;

public class FileDelete implements Runnable {
	private File file;
	private String filePath;
	private List<File> fileList;

	public FileDelete(File file) {
		this.file = file;
	}

	public FileDelete(String filePath) {
		this.filePath = filePath;
	}

	public FileDelete(List<File> fileList) {
		this.fileList = fileList;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(1000);
			if (file != null) {
				file.delete();
			} else if (null != filePath) {
				file = new File(filePath);
				file.delete();
			} else if (null != fileList) {
				for (File file : fileList) {
					file.delete();
				}
			}
		} catch (Exception e) {
		}
	}

}
