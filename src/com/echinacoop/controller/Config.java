package com.echinacoop.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.alibaba.fastjson.JSONObject;
import com.echinacoop.utils.UserData;
import com.yinsin.utils.CommonUtils;
import com.yinsin.utils.SystemUtils;

public class Config {

	public static String APP_DIR = SystemUtils.getUserHome() + "\\gxbchat\\";
	
	public static String APP_NAME = "gxbchat";
	public static String APP_VERSION = "1.6";
	public static String APP_SERIAL = "201707131030";

	public static final String CONF_NAME = "config.json";

	public static JSONObject GLOBAL_CONFIG = new JSONObject();

	public static JSONObject USER_CONFIG = new JSONObject();

	private static Properties prop = null;
	
	public static void main(String[] args) {
		Config.initProperties();
	}

	public static void initProperties() {
		if(null == prop){
			try {
				prop = new Properties();
				// 读取属性文件a.properties
				InputStream in = new BufferedInputStream(Config.class.getResourceAsStream("/create_table_sql.properties"));
				prop.load(in); // /加载属性列表
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Properties getProperties(){
		initProperties();
		return prop;
	}

	public static void loadGlobalConfig() {
		String filepath = APP_DIR + CONF_NAME;
		File file = new File(filepath);
		if (file.exists()) {
			try {
				FileInputStream fis = new FileInputStream(file);
				byte[] byt = new byte[fis.available()];
				int size = fis.read(byt);
				String content = new String(byt, 0, size);
				if (CommonUtils.isNotBlank(content)) {
					JSONObject json = JSONObject.parseObject(content);
					GLOBAL_CONFIG = json;
				}
				fis.close();
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
		}
	}

	public static void saveGlobalConfig() {
		String filepath = APP_DIR + CONF_NAME;
		File file = new File(filepath);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			String content = JSONObject.toJSONString(GLOBAL_CONFIG);
			fos.write(content.getBytes());
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	public static void loadUserConfig(String userId) {
		String filepath = APP_DIR + userId + "-" + CONF_NAME;
		File file = new File(filepath);
		if (file.exists()) {
			try {
				FileInputStream fis = new FileInputStream(file);
				byte[] byt = new byte[fis.available()];
				int size = fis.read(byt);
				String content = new String(byt, 0, size);
				if (CommonUtils.isNotBlank(content)) {
					JSONObject json = JSONObject.parseObject(content);
					USER_CONFIG = json;
				}
				fis.close();
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
		}
	}

	public static void saveUserConfig(String userId) {
		String filepath = APP_DIR + userId + "-" + CONF_NAME;
		File file = new File(filepath);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			String content = JSONObject.toJSONString(USER_CONFIG);
			fos.write(content.getBytes());
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	public static JSONObject getMessageFont() {
		JSONObject json = USER_CONFIG.getJSONObject("MESSAGE_FONT");
		if (json == null) {
			json = new JSONObject();
		}
		return json;
	}

	public static void setMessageFont(JSONObject json) {
		USER_CONFIG.put("MESSAGE_FONT", json);
		saveUserConfig(UserData.user.getString("userId"));
	}

}
