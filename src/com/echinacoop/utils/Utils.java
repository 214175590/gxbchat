package com.echinacoop.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import com.alibaba.fastjson.JSONObject;
import com.echinacoop.consts.Constants;
import com.echinacoop.controller.Config;
import com.echinacoop.controller.SocketService;
import com.echinacoop.form.UserItem;
import com.yinsin.utils.CommonUtils;
import com.yinsin.utils.FileUtils;
import com.yinsin.utils.SystemUtils;

public class Utils {

	public static File getNetFile(String fileurl) {
		String fileName = FileUtils.getFileNameToPath(fileurl);
		if(fileName.startsWith("/")){
			fileName = fileName.substring(1);
		}
		File file = new File(Constants.TEMP_DIR + fileName);
		if (!file.exists()) {
			try {
				download(fileurl, fileName, Constants.TEMP_DIR);
				file = new File(Constants.TEMP_DIR + fileName);
			} catch (Exception e) {
			}
		}
		return file;
	}
	
	public static  String getHeadUrl(String path){
		String url = "";
		if(path != null){
			if(path.startsWith("images/avatar")){
				url = SocketService.serverUrl + path;
			} else {
				url = SocketService.imgServerUrl + path;
			}
		}
		return url;
	}
	
	public static boolean isMyFriend(String userId){
		UserItem item = UserData.USER_ITEM_MAP.get(userId);
		return item != null;
	}

	public static void download(String urlString, String filename, String savePath) throws Exception {
		// 构造URL
		URL url = new URL(urlString);
		// 打开连接
		URLConnection con = url.openConnection();
		// 设置请求超时为5s
		con.setConnectTimeout(5 * 1000);
		// 输入流
		InputStream is = con.getInputStream();

		// 1K的数据缓冲
		byte[] bs = new byte[1024];
		// 读取到的数据长度
		int len;
		// 输出的文件流
		File sf = new File(savePath);
		if (!sf.exists()) {
			sf.mkdirs();
		}
		OutputStream os = new FileOutputStream(savePath + filename);
		// 开始读取
		while ((len = is.read(bs)) != -1) {
			os.write(bs, 0, len);
		}
		// 完毕，关闭所有链接
		os.close();
		is.close();
	}
	
	public static JSONObject getClientInfo(){
		JSONObject json = new JSONObject();
		json.put("sysVer", SystemUtils.getOsName());
		json.put("type", "pc");
		json.put("appVer", Config.APP_VERSION);
		return json;
	}
	
	public static String getUserViewName(String userId, String name){
		String viewName = null;
		UserItem item = UserData.USER_ITEM_MAP.get(userId);
		if(null != item){
			JSONObject user = item.getUser();
			if(null != user){
				viewName = CommonUtils.excNullToString(user.getString("friendNick"), user.getString("nickName"));
			}
		} else {
			viewName = name;
		}
		return viewName;
	}

}
