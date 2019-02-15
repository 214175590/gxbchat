package com.echinacoop.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.echinacoop.form.ComponentAbs;
import com.echinacoop.form.GroupItem;
import com.echinacoop.form.RecentItem;
import com.echinacoop.form.UserItem;
import com.echinacoop.modal.ChatType;
import com.echinacoop.modal.UploadType;
import com.echinacoop.modal.WSData;

public class UserData {
	
	public static JSONObject user = new JSONObject();
	
	public static JSONObject userToken = new JSONObject();
	
	public static Map<String, UserItem> USER_ITEM_MAP = new HashMap<String, UserItem>();
	
	public static Map<String, GroupItem> GROUP_ITEM_MAP = new HashMap<String, GroupItem>();
	
	public static Map<String, RecentItem> RECENT_ITEM_MAP = new HashMap<String, RecentItem>();
	
	public static Map<String, List<WSData>> USER_CHAT_RECORD = new HashMap<String, List<WSData>>();
	
	public static Map<String, List<WSData>> GROUP_CHAT_RECORD = new HashMap<String, List<WSData>>();
	
	public static Map<String, List<WSData>> RECENT_CHAT_RECORD = new HashMap<String, List<WSData>>();
	
	/** filecode - 后缀 */
	public static Map<String, String> PIC_SUFFIX = new HashMap<String, String>();
	
	/** filecode - UploadType */
	public static Map<String, UploadType> PIC_UPLOAD_TYPE = new HashMap<String, UploadType>();
	
	public static ComponentAbs CURRENT_ITEM = null;
	
	public static RecentItem getRecentItem(String id, ChatType type){
		RecentItem item = null;
		if(type == ChatType.SINGLE_CHAT){
			item = RECENT_ITEM_MAP.get("U" + id);
		} else if(type == ChatType.GROUP_CHAT){
			item = RECENT_ITEM_MAP.get("G" + id);
		}
		return item;
	}
	
	public static RecentItem removeRecentItem(String id, ChatType type){
		RecentItem item = null;
		if(type == ChatType.SINGLE_CHAT){
			item = RECENT_ITEM_MAP.remove("U" + id);
		} else if(type == ChatType.GROUP_CHAT){
			item = RECENT_ITEM_MAP.remove("G" + id);
		}
		return item;
	}
	
}
