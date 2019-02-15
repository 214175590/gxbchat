package com.echinacoop.consts;

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import com.yinsin.utils.SystemUtils;

public class Constants {
	
	public static final String TEMP_DIR = SystemUtils.getTmpdir() + "GXBChat\\";
	
	public final static Font LABEL_TEXT_FONT = new Font("微软雅黑", 0, 14);
	
	public final static String CONTENT_TYPE = "application/x-www-form-urlencoded;charset=utf-8";
	
	
	public final static Color BACK_COLOR_WHITE = new Color(255, 255, 255);
	
	public final static Color BACK_COLOR_GREEN = new Color(100, 250, 100);
	
	public final static Color BACK_COLOR_RED = new Color(240, 200, 200);
	
	public final static Color ITEM_BACK_HOVER_COLOR = new Color(225, 225, 225);
	
	public final static Color ITEM_BACK_PRESS_COLOR = new Color(193, 218, 247);
	
	
	/** 单聊 */
    public static String MSG_TYPE_SINGLE_CHAT                      = "01";
    /** 群聊  */                                                   
    public static String MSG_TYPE_GROUP_CHAT                       = "02";
    /** 好友请求 */                                                
    public static String MSG_TYPE_ADD_FRIEND                       = "03";
    /** 邀请入群 */                                                
    public static String MSG_TYPE_INVITATION_GROUP                 = "04";
    /** 入群申请 */                                                
    public static String MSG_TYPE_APPLY_GROUP                      = "05";
    /** 好友状态更改 */                                            
    public static String MSG_TYPE_CHANGE_STATE                     = "06";
    /** 群组被禁言、解禁 */                                        
    public static String MSG_TYPE_GROUP_SPEAK                      = "07";
    /** 群组成员被提升/取消管理员 */                               
    public static String MSG_TYPE_USER_SET_MANAGER                 = "08";
    /** 自己被提升/取消为群组管理员 */                             
    public static String MSG_TYPE_MY_SET_MANAGER                   = "09";
    /** 群组被解散 */                                              
    public static String MSG_TYPE_GROUP_DISSOLVED                  = "10";
    /** 被踢出群组 */                                              
    public static String MSG_TYPE_KICKET_GROUP                     = "11";
    /** 用户同意了入群邀请消息 */                                  
    public static String MSG_TYPE_USER_JOIN_GROUP                  = "12";
    /** 群资料被修改消息 */                                  
    public static String MSG_TYPE_USER_UPDATE_GROUP                = "13";
    /** 被好友拉入了群聊 */
    public static String MSG_TYPE_PULLED_GROUP                     = "14";
    /** 面对面建群输入了相同密码的用户消息 */
    public static String MSG_TYPE_FACING_PWD_USER                  = "15";
    /** 群主同意了入群申请，已入群 */
    public static String MSG_TYPE_JOIN_GROUP                  	   = "16";
    /** 用户退出群组 */
    public static String MSG_TYPE_USER_EXIT_GROUP                  = "17";
    /** 用户修改群昵称 */
    public static String MSG_TYPE_USER_EDIT_NICK                   = "18";
    /** 好友关系解除通知消息类型 */
    public static String MSG_TYPE_REMOVE_FRIENDSHIP                = "19";
    /** 对方同意了好友请求，互相成为了好友的通知消息类型 */
    public static String MSG_TYPE_FRIENDSHIP                       = "20";
    /** 系统消息  */                                               
    public static String MSG_TYPE_SYS_MSG                          = "99";
                                                                   
    /** 已阅*/                                                     
    public static String MSG_READSTATE_YES                         = "01";
    /** 未阅 */                                                    
    public static String MSG_READSTATE_NO                          = "00";
                                                                   
    /** 已成为好友通知 */                                          
    public static String REQ_URL_FRIENDSHIP                        = "chat.friendship";
                                                                   
    /** 私聊 */                                                    
    public static String REQ_URL_SINGLE_CHAT                       = "chat.single-chat";
                                                                   
    /** 群聊 */                                                    
    public static String REQ_URL_GROUP_CHAT                        = "chat.group-chat";
                                                                   
    /** 好友请求 */                                                
    public static String REQ_URL_ADD_FRIEND                        = "chat.friend";
                                                                   
    /** 文件上传进度 */                                            
    public static String REQ_URL_UPLOAD_FILE                       = "chat.upload-file";
                                                                   
    /** 邀请入群 */                                                
    public static String REQ_URL_INVITATION_GROUP                  = "user.invitation-group";
    
    /** 被好友拉入群聊 */
    public static String REQ_URL_PULLED_GROUP                      = "user.pulled-group";
    
    /** 被踢出群 */                                                
    public static String REQ_URL_KICKET_GROUP                      = "user.kicket-group";
                                                                   
    /** 用户进入群组的消息通知 */                                  
    public static String REQ_URL_USER_JOIN_GROUP                   = "user.join-group";
    
    /** 用户退出群组的消息通知 */                                  
    public static String REQ_URL_USER_EXIT_GROUP                   = "user.exit-group";
    
    /** 已加入群组通知消息 */                                  
    public static String REQ_URL_AGREE_JOIN_GROUP                  = "group.agree-join";
    
    /** 群组解散消息通知 */
    public static String REQ_URL_GROUP_DISSOLVED                   = "group.dissolved";
    
    /** 群组资料被修改通知 */
    public static String REQ_URL_UPDATE_GROUP                      = "group.update-info";
    
    /** 用户修改了群昵称的通知 */
    public static String REQ_URL_USER_EDIT_NICK                    = "group.user-nick";
    
    /** 匹配到面对面建群用户通知 */
    public static String REQ_URL_FACING_PWD_USER                   = "chat.facing-user";
    
    /** 用户会话超时通知 */
    public static String REQ_URL_LOGIN_TIMEOUT                     = "user.login-timeout";
    
    /** 好友关系解除通知 */
    public static String REQ_URL_REMOVE_FRIENDSHIP                 = "user.remove-friendship";
	
    /** 操作结果 - 01同意 */
    public final static String OPERA_RESULT_AGREE                  = "01";
    /** 操作结果 - 02拒绝 */
    public final static String OPERA_RESULT_REFUSE                 = "02";
    /** 操作结果 - 03忽略 */
    public final static String OPERA_RESULT_IGNORE                 = "03";
    
    private static Map<String, String> TEXT_MAP = new HashMap<String, String>();
    
    static {
    	TEXT_MAP.put("OPERA_RESULT_01", "同意");
    	TEXT_MAP.put("OPERA_RESULT_02", "拒绝");
    	TEXT_MAP.put("OPERA_RESULT_03", "忽略");
    }
    
    public static String getText(String key, String value){
    	return TEXT_MAP.get(key + "_" + value);
    }
    
}
