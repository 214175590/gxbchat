package com.echinacoop.db;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.echinacoop.Startup;
import com.echinacoop.modal.ChatHistory;
import com.echinacoop.utils.UserData;
import com.yinsin.other.LogHelper;
import com.yinsin.utils.CollectionUtils;
import com.yinsin.utils.CommonUtils;

public class SqlHelper {
	private static final LogHelper logger = LogHelper.getLogger(SqlHelper.class);

	public static int loadNoreadChatHistoryCount() {
		int size = 0;
		String sql = "select count(row_id) size from chat_history where req_url in('chat.friend', 'chat.join-group') and to_id = ? and status = '00'";
		List<Object> params = new ArrayList<Object>();
		params.add(UserData.user.getString("userId"));
		List<Map<String, Object>> list = DBService.query(sql, params);
		if(list != null && list.size() > 0){
			size = CommonUtils.objectToInt(list.get(0).get("SIZE"));
		}
		return size;
	}
	
	public static List<Map<String, Object>> loadMessageHistory(String status, int page, int size) {
		String sql = "select * from chat_history where req_url in('chat.friend', 'chat.join-group') and to_id = ? and status = ? order by send_date desc limit ?, ?";
		List<Object> params = new ArrayList<Object>();
		params.add(UserData.user.getString("userId"));
		params.add(status);
		params.add((page - 1) * size);
		params.add(size);
		List<Map<String, Object>> list = DBService.query(sql, params);
		return list;
	}
	
	public static List<Map<String, Object>> loadSingleChatHistory(String friendId, String status, int page, int size) {
		StringBuilder sql = new StringBuilder("select * from chat_history where req_url = 'chat.single-chat' ");
		sql.append(" and (from_id = ? or to_id = ?) ")
		.append(" order by send_date asc limit ?, ?");
		List<Object> params = new ArrayList<Object>();
		params.add(friendId);
		params.add(friendId);
		params.add((page - 1) * size);
		params.add(size);
		List<Map<String, Object>> list = DBService.query(sql.toString(), params);
		return list;
	}
	
	public static List<Map<String, Object>> loadGroupChatHistory(String groupId, String status, int page, int size) {
		String sql = "select * from chat_history where req_url = 'chat.group-chat' and group_id = ? order by send_date asc limit ?, ?";
		List<Object> params = new ArrayList<Object>();
		params.add(groupId);
		params.add((page - 1) * size);
		params.add(size);
		List<Map<String, Object>> list = DBService.query(sql, params);
		return list;
	}

	public static void insertChatHistory(ChatHistory chat) {
		try {
			String sql = "insert into chat_history(row_id, from_id, to_id, group_id, req_url, msg_type, msg_content, send_date, status) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
			List<Object> params = new ArrayList<Object>();
			params.add(chat.getRowId());
			params.add(chat.getFromId());
			params.add(chat.getToId());
			params.add(chat.getGroupId());
			params.add(chat.getReqUrl());
			params.add(chat.getMsgType());
			params.add(chat.getMsgContent());
			params.add(chat.getSendDate());
			params.add(chat.getStatus());
			boolean result = DBService.execute(sql, params);
			logger.debug("保存消息" + (result ? "成功" : "失败"));
			logger.debug(chat.toString());
			
			Startup.mainWindow.getHeadPanel().showMsgView();
		} catch (Exception e) {
			logger.error("保存消息时异常：" + e.getMessage(), e);
		}
	}

	public static boolean updateChatHistoryStatus(String rowId, String operaState) {
		boolean result = false;
		try {
			String sql = "update chat_history set opera_state = ?, status = '01' where row_id = ?";
			List<Object> params = new ArrayList<Object>();
			params.add(operaState);
			params.add(rowId);
			result = DBService.execute(sql, params);
			logger.debug("修改消息状态" + (result ? "成功" : "失败"));
			logger.debug(rowId + " - " + operaState);
		} catch (Exception e) {
			logger.error("保存消息时异常：" + e.getMessage(), e);
		}
		return result;
	}
	
	public static boolean deleteChatHistory(List<String> idList) {
		boolean result = false;
		try {
			String idStr = CollectionUtils.listJoinToSql(idList);
			System.out.println(idStr);
			String sql = "delete from chat_history where row_id in (" + idStr + ")";
			result = DBService.execute(sql);
			logger.debug("删除消息" + (result ? "成功" : "失败"));
		} catch (Exception e) {
			logger.error("删除消息时异常：" + e.getMessage(), e);
		}
		return result;
	}

}
