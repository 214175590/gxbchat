package com.echinacoop.controller;

import java.io.File;
import java.util.List;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSONObject;
import com.echinacoop.consts.Constants;
import com.echinacoop.modal.PicMessage;
import com.echinacoop.modal.Response;
import com.echinacoop.modal.TextMessage;
import com.echinacoop.modal.WSData;
import com.echinacoop.socket.SocketEvent;
import com.echinacoop.socket.YSSocket;
import com.echinacoop.utils.AESUtils;
import com.echinacoop.utils.FileDelete;
import com.echinacoop.utils.JSONUtils;
import com.echinacoop.utils.UserData;
import com.yinsin.http.HttpRequest;
import com.yinsin.security.AES;
import com.yinsin.security.MD5;
import com.yinsin.utils.CommonUtils;
import com.yinsin.utils.SystemUtils;

public class SocketService {

	private static long msgId = 6900000000L;

	public static int serverPort = 8084;
	private static int net = 0; // 切换环境，0本机，1内网 ，2外网，3 sit环境内网
	public static String serverIp = "";
	public static String serverUrl = "";
	public static String imgServerUrl = "";
	
	static {
		if(net == 0){
			serverIp = "127.0.0.1";
			serverUrl = "http://127.0.0.1:8082/";
			imgServerUrl = "http://127.0.0.1:8080/chatfile/";
		} else if(net == 1){
			serverIp = "192.168.20.32";
			serverUrl = "http://192.168.20.32:8082/";
			imgServerUrl = "http://192.168.20.32:8083/chatfile/";
		} else if(net == 3){
			serverIp = "192.168.20.100";
			serverUrl = "http://192.168.20.100:8082";
			imgServerUrl = "http://192.168.20.100:8083/chatfile/";
		} else if(net == 2){
			serverIp = "202.105.145.245";
			serverUrl = "http://202.105.145.245:9082/";
			imgServerUrl = "http://202.105.145.245:9083/chatfile/";
		}
	}

	private static String LOGIN_URL = "m801/f80101";
	
	private static String LOAD_FRIEND = "m101/f10107";
	
	private static String LOAD_GROUP = "m103/f10306";
	
	private static String LOAD_RECENT = "m108/f10801";
	
	private static String LOAD_USER_CHAT_HIS = "m106/f10601";
	
	private static String LOAD_GROUP_CHAT_HIS = "m106/f10602";
	
	private static String UPLOAD_FILE = "m802/f80203";
	
	private static String REMOVE_FRIEND = "m102/f10205";
	
	private static String DISSOLUTION_GROUP = "m103/f10305";
	
	private static String EXIT_GROUP = "m105/f10505";
	
	private static String FIND_USER = "m101/f10108";
	
	private static String FIND_GROUP = "m103/f10310";
	
	/** 进入面对面群聊 */
	private static String FACING_JOIN_GROUP = "m103/f10309";
	
	/** 根据群组ID，加载群组信息及成员信息 */
	private static String GET_GROUP_INFO = "m103/f10308";
	
	/** 拉取好友组成群组 */
	private static String ATFRIEND_CREATE_GROUP = "m103/f10307";
	
	/** 修改用户头像 m101/f10112 */
	private static String UPDATE_USER_HEAD = "m101/f10112";
	
	/** 邀请好友加入群聊（群组已存在，邀请新成员）/踢出现有成员 m105/f10506 */
	private static String INVITATION_USER_JOIN_GROUP = "m105/f10506";
	
	/** 修改用户群昵称 m105/f10504 */
	private static String EDIT_GROUP_NICKNAME = "m105/f10504";
	
	/** 修改群组信息 m103/f10304 */
	private static String EDIT_GROUP_INFO = "m103/f10304";
	
	/** 同意某用户的好友请求 m102/f10203 */
	private static String ADD_FRIEND_AGREE = "m102/f10203";
	
	/** 同意入群 邀请/申请 m102/f10203 */
	private static String AGREE_JOIN_GROUP = "m105/f10503";
	
	/** 应用检测新版本 m109/f10902 */
	private static String CHECK_APP_VERSION = "m109/f10902";
	
	/** 修改好友备注名称 m102/f10207 */
	private static String EDIT_FRIEND_NICKNAME = "m102/f10207";
	
	private static JSONObject APP_INFO = new JSONObject();
	
	static {
		APP_INFO.put("APP_TYPE", "pc");
		APP_INFO.put("APP_SYSVER", SystemUtils.getOsVersion());
		APP_INFO.put("APP_VER", Config.APP_VERSION);
		//APP_INFO.put("APP_UDID", "c112ed059d3bc019833f9eb991cd9e594583381dd979b7a6f809f683efd70d82");
	}

	public SocketService() {
		
	}

	public static YSSocket socket = null;

	public static void connection() {
		if(socket == null){
			socket = new YSSocket();
			socket.register(serverIp, serverPort, new SocketEvent());
		}
	}
	
	public static Response sendPost(String uri, JSONObject jsonValue){
		HttpRequest request = HttpRequest.post(serverUrl + uri)
				.contentType(Constants.CONTENT_TYPE)
				.header("Authorization", UserData.userToken.getString("access_token"));
		for(Entry<String, Object> en : APP_INFO.entrySet()){
			request.header(en.getKey(), (String) en.getValue());
		}
		String body = request.send("jsonValue=" + CommonUtils.stringEncode(jsonValue.toJSONString())).body();
		return JSONUtils.toJavaObject(body, Response.class);
	}
	
	/** =================== Socket 请求 =================== **/

	public static WSData login(String userId, String userToken) {
		WSData wsData = new WSData();
		wsData.setUrl("user.login");
		JSONObject body = new JSONObject();
		body.put("msgId", msgId++);
		body.put("userId", userId);
		body.put("userToken", userToken);
		wsData.setBody(body);
		socket.sendMessage(wsData);
		return wsData;
	}

	public static void sendHeart() {
		WSData wsData = new WSData();
		wsData.setUrl("user.heart");
		JSONObject body = new JSONObject();
		body.put("sid", UserData.user.get("sid"));
		body.put("userId", UserData.user.getString("userId"));
		wsData.setBody(body);
		socket.sendMessage(wsData);
	}
	
	/** 加载离线消息，登录WS成功后调用 */
	public static void loadUserOfflineChatHisList(){
		WSData wsData = new WSData();
		wsData.setUrl("user.load-history-message");
		JSONObject body = new JSONObject();
		body.put("msgId", msgId++);
		body.put("sid", UserData.user.get("sid"));
		body.put("userId", UserData.user.getString("userId"));
		wsData.setBody(body);
		socket.sendMessage(wsData);
	}

	public static void sendSingleTextMessage(JSONObject user, String msg){
		WSData wsData = new WSData();
		wsData.setUrl("chat.single-chat");
		JSONObject body = new JSONObject();
		body.put("msgId", msgId++);
		body.put("sid", UserData.user.get("sid"));
		body.put("to", user.get("userId"));
		body.put("content", new TextMessage(msg, Config.getMessageFont()));
		wsData.setBody(body);
		socket.sendMessage(wsData);
	}
	
	public static void sendGroupTextMessage(JSONObject group, String msg){
		WSData wsData = new WSData();
		wsData.setUrl("chat.group-chat");
		JSONObject body = new JSONObject();
		body.put("msgId", msgId++);
		body.put("sid", UserData.user.get("sid"));
		body.put("toGroup", group.get("groupId"));
		body.put("content", new TextMessage(msg, Config.getMessageFont()));
		wsData.setBody(body);
		socket.sendMessage(wsData);
	}
	
	public static void sendSinglePicMessage(JSONObject user, String filePath, int width, int height){
		WSData wsData = new WSData();
		wsData.setUrl("chat.single-chat");
		JSONObject body = new JSONObject();
		body.put("msgId", msgId++);
		body.put("sid", UserData.user.get("sid"));
		body.put("to", user.get("userId"));
		body.put("content", new PicMessage(filePath, width, height));
		wsData.setBody(body);
		socket.sendMessage(wsData);
	}
	
	public static void sendGroupPicMessage(JSONObject group, String filePath, int width, int height){
		WSData wsData = new WSData();
		wsData.setUrl("chat.group-chat");
		JSONObject body = new JSONObject();
		body.put("msgId", msgId++);
		body.put("sid", UserData.user.get("sid"));
		body.put("toGroup", group.get("groupId"));
		body.put("content", new PicMessage(filePath, width, height));
		wsData.setBody(body);
		socket.sendMessage(wsData);
	}
	
	/** 申请加好友 */
	public static void addFriend(String frieidId, String groupId){
		WSData wsData = new WSData();
		wsData.setUrl("chat.friend");
		JSONObject body = new JSONObject();
		JSONObject content = new JSONObject();
		content.put("gid", groupId);
		body.put("msgId", msgId++);
		body.put("sid", UserData.user.get("sid"));
		body.put("to", frieidId);
		body.put("content", content);
		wsData.setBody(body);
		socket.sendMessage(wsData);
	}
	
	/** 申请入群 */
	public static void addGroup(String userId, String groupId, String groupName){
		WSData wsData = new WSData();
		wsData.setUrl("chat.join-group");
		JSONObject body = new JSONObject();
		body.put("msgId", msgId++);
		body.put("sid", UserData.user.get("sid"));
		body.put("toUserId", userId);
		body.put("toGroupId", groupId);
		body.put("toGroupName", groupName);
		wsData.setBody(body);
		socket.sendMessage(wsData);
	}
	
	/** 面对面建群请求 —参与/退出 面对面建群 , type 1=参与， 0=退出 */
	public static void facingGroup(String pwd, String type){
		WSData wsData = new WSData();
		wsData.setUrl("chat.facing-group");
		JSONObject body = new JSONObject();
		body.put("msgId", msgId++);
		body.put("sid", UserData.user.get("sid"));
		body.put("pwd", pwd);
		body.put("type", type);
		wsData.setBody(body);
		socket.sendMessage(wsData);
	}
	
	/** =================== Http 请求 =================== **/
	
	public static Response httpLogin(String account, String password) {
		//String pwd = MD5.md5(account + MD5.md5(password));
		JSONObject jsonValue = new JSONObject();
		try {
			//String pwd = AESUtils.getInstance().encrypt(password);
			String pwd = AES.encrypt(password, account);
			jsonValue.put("uname", account);
			jsonValue.put("upass", pwd);
		} catch (Exception e) {
		}
		return sendPost(LOGIN_URL, jsonValue);
	}
	
	public static Response loadFriendList(){
		JSONObject jsonValue = new JSONObject();
		jsonValue.put("userId", UserData.user.getString("userId"));
		return sendPost(LOAD_FRIEND, jsonValue);
	}
	
	public static Response loadGroupList(){
		JSONObject jsonValue = new JSONObject();
		jsonValue.put("userId", UserData.user.getString("userId"));
		return sendPost(LOAD_GROUP, jsonValue);
	}
	
	public static Response loadRecentList(){
		JSONObject jsonValue = new JSONObject();
		jsonValue.put("userId", UserData.user.getString("userId"));
		return sendPost(LOAD_RECENT, jsonValue);
	}
	
	public static Response loadUserChatHisList(String userId){
		JSONObject jsonValue = new JSONObject();
		jsonValue.put("fromId", UserData.user.getString("userId"));
		jsonValue.put("userId", userId);
		jsonValue.put("_pageSize", 30);
		return sendPost(LOAD_USER_CHAT_HIS, jsonValue);
	}
	
	public static Response loadGroupChatHisList(String groupId){
		JSONObject jsonValue = new JSONObject();
		jsonValue.put("fromId", UserData.user.getString("userId"));
		jsonValue.put("groupId", groupId);
		jsonValue.put("_pageSize", 30);
		return sendPost(LOAD_GROUP_CHAT_HIS, jsonValue);
	}
	
	public static Response uploadFile(File file, boolean isDelete){
		HttpRequest request = HttpRequest.post(serverUrl + UPLOAD_FILE)
				.header("Authorization", UserData.userToken.getString("access_token"))
				.part("uploadFile", file.getName(), file);
		String body = request.body();
		Response res = JSONUtils.toJavaObject(body, Response.class);
		if(isDelete){
			new Thread(new FileDelete(file)).start();
		}
		return res;
	}
	
	public static Response removeFriend(String friendId){
		JSONObject jsonValue = new JSONObject();
		jsonValue.put("friendId", friendId);
		return sendPost(REMOVE_FRIEND, jsonValue);
	}
	
	/** 群主解散群组 /m103/f10305 */
	public static Response dissolutionGroup(String groupId){
		JSONObject jsonValue = new JSONObject();
		jsonValue.put("groupId", groupId);
		return sendPost(DISSOLUTION_GROUP, jsonValue);
	}
	
	/** 群成员退出群组 /m105/f10505 */
	public static Response exitGroup(String groupId){
		JSONObject jsonValue = new JSONObject();
		jsonValue.put("groupId", groupId);
		return sendPost(EXIT_GROUP, jsonValue);
	}
	
	/** 根据供销宝账号、手机号码，查找用户 m101/f10108 */
	public static Response findUserByUserNameOrMobile(String keyword){
		JSONObject jsonValue = new JSONObject();
		jsonValue.put("keyword", keyword);
		return sendPost(FIND_USER, jsonValue);
	}
	
	/** 根据群组名称模糊查找群组 m103/f10310 */
	public static Response findGroupByGroupName(String keyword){
		JSONObject jsonValue = new JSONObject();
		jsonValue.put("keyword", keyword);
		return sendPost(FIND_GROUP, jsonValue);
	}
	
	/** 面对面建群，进入群聊请求 m103/f10309 */
	public static Response joinFacingGroup(String pwd){
		JSONObject jsonValue = new JSONObject();
		jsonValue.put("pwd", pwd);
		return sendPost(FACING_JOIN_GROUP, jsonValue);
	}
	
	/** 根据群组ID加载群组信息 */
	public static Response getGroupInfoByGroupId(String groupId){
		JSONObject jsonValue = new JSONObject();
		jsonValue.put("groupId", groupId);
		return sendPost(GET_GROUP_INFO, jsonValue);
	}
	
	/** 拉取好友组成群组 */
	public static Response atFriendCreateGroup(String groupName, List<String> userList){
		JSONObject jsonValue = new JSONObject();
		jsonValue.put("groupName", groupName);
		jsonValue.put("userList", userList);
		return sendPost(ATFRIEND_CREATE_GROUP, jsonValue);
	}
	
	/** 修改用户头像 */
	public static Response updateUserHead(String headImg){
		JSONObject jsonValue = new JSONObject();
		jsonValue.put("headImg", headImg);
		return sendPost(UPDATE_USER_HEAD, jsonValue);
	}
	
	/** 拉取好友组成群组 */
	public static Response invitationUserJoinGroup(String groupId, List<String> addList, List<String> delList){
		JSONObject jsonValue = new JSONObject();
		jsonValue.put("groupId", groupId);
		jsonValue.put("addList", addList);
		jsonValue.put("delList", delList);
		return sendPost(INVITATION_USER_JOIN_GROUP, jsonValue);
	}
	
	/** 修改自己再群中的昵称 */
	public static Response editGroupNickname(String groupId, String nickName){
		JSONObject jsonValue = new JSONObject();
		jsonValue.put("groupId", groupId);
		jsonValue.put("nickName", nickName);
		return sendPost(EDIT_GROUP_NICKNAME, jsonValue);
	}
	
	/** 修改群组信息 */
	public static Response editGroupInfo(String groupId, String groupName, String groupImg, String groupDesc){
		JSONObject jsonValue = new JSONObject();
		jsonValue.put("groupId", groupId);
		jsonValue.put("groupName", groupName);
		jsonValue.put("groupImg", groupImg);
		jsonValue.put("groupDesc", groupDesc);
		return sendPost(EDIT_GROUP_INFO, jsonValue);
	}
	
	/** 同意好友请求 */
	public static Response agreeFriendReq(String groupId, String friendUserId, String friendGroupId){
		JSONObject jsonValue = new JSONObject();
		jsonValue.put("groupId", groupId);
		jsonValue.put("friendUserId", friendUserId);
		jsonValue.put("friendGroupId", friendGroupId);
		return sendPost(ADD_FRIEND_AGREE, jsonValue);
	}
	
	/** 同意入群请求 */
	public static Response agreeJoinGroup(String userId, String groupId, String apply){
		JSONObject jsonValue = new JSONObject();
		jsonValue.put("userId", userId);
		jsonValue.put("groupId", groupId);
		if(null != apply){
			jsonValue.put("apply", apply);
		}
		return sendPost(AGREE_JOIN_GROUP, jsonValue);
	}
	
	/** 同意入群请求 */
	public static Response checkAppVersion(){
		JSONObject jsonValue = new JSONObject();
		jsonValue.put("appid", Config.APP_NAME);
		jsonValue.put("serial", Config.APP_SERIAL);
		return sendPost(CHECK_APP_VERSION, jsonValue);
	}
	
	/** 修改好友备注名称 */
	public static Response editFriendNickname(String userId, String nickName){
		JSONObject jsonValue = new JSONObject();
		jsonValue.put("friendUserId", userId);
		jsonValue.put("nickName", nickName);
		return sendPost(EDIT_FRIEND_NICKNAME, jsonValue);
	}
	
}
