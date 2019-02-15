package com.echinacoop.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.echinacoop.controller.Config;
import com.yinsin.other.LogHelper;
import com.yinsin.utils.CommonUtils;
import com.yinsin.utils.FileUtils;


public class CopyDBThread extends Thread {
	public static boolean DB_INITED = false;
	private final static LogHelper logger = LogHelper.getLogger(CopyDBThread.class);
	private String userId = "";
	public CopyDBThread(String userId){
		this.userId = userId;
	}
	
	public void run(){
		copydb(userId);
	}
	
	private void copydb(String userId){
		try {
			String dbver = CommonUtils.excNullToString(Config.GLOBAL_CONFIG.getString("dbver"), "");
			File dbfile = new File(Config.APP_DIR + userId + "-gxbchat.mv.db");
			if(!dbfile.exists() || !dbver.equals(Config.APP_VERSION)){
				InputStream is = CopyDBThread.class.getResourceAsStream("/gxbchat.mv.db");
				if (is != null) {
					FileOutputStream fos = new FileOutputStream(dbfile);
					byte[] b = new byte[1024];
					int size = is.read(b);
					while(size != -1){
						fos.write(b, 0, size);
						size = is.read(b);
					}
					is.close();
					fos.flush();
					fos.close();
					
					Config.GLOBAL_CONFIG.put("dbver", Config.APP_VERSION);
					Config.saveGlobalConfig();
				}
			}
			DBService.initDB(userId);
			logger.debug("数据库初始化完成");
		} catch (Exception e) {
			logger.error("初始化数据库异常：" + e.getMessage(), e);
		}
	}

	
}
