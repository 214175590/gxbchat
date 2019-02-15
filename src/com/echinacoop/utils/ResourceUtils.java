package com.echinacoop.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.echinacoop.modal.Message;
import com.echinacoop.modal.MessageType;

public class ResourceUtils {

	/** 读取资源文件内容 */
	public static String readResourceFile(String file) {
		String content = "";
		try {
			InputStream is = ResourceUtils.class.getClassLoader().getResourceAsStream(file);
			byte[] b = null;
			byte[] buf = new byte[1024];
			int num = -1;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((num = is.read(buf, 0, buf.length)) != -1) {
				baos.write(buf, 0, num);
			}
			b = baos.toByteArray();
			content = new String(b, "utf-8");
			baos.flush();
			baos.close();
			is.close();
		} catch (Exception e) {

		}
		return content;
	}
	
}
