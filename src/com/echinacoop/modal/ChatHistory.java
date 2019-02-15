package com.echinacoop.modal;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 此实体类为[代码工厂]自动生成
 * 
 * @Desc 模块方法表
 * @Time 2017-03-21 20:31
 * @GeneratedByCodeFactory
 */
@SuppressWarnings("serial")
public class ChatHistory implements java.io.Serializable {

	/** 主键ID */
	private String rowId;

	/** 接收者ID（用户ID） */
	private String toId;
	
	private String fromId;
	
	private String groupId;
	
	/** 请求Url */
	private String reqUrl;
	
	/** 消息类型 */
	private String msgType;

	/** 消息内容 */
	private String msgContent;

	/** 发送时间 */
	private Timestamp sendDate = new Timestamp(new Date().getTime());

	/** 阅读状态，01已阅，00未阅 */
	private String status = "00";
	
	/** 处理状态，01同意，02拒绝,03忽略 */
	private String operaState = "03";

	/**
	 * 获取 主键ID 的值
	 * 
	 * @return Long
	 */
	public String getRowId() {
		return rowId;
	}

	/**
	 * 设置主键ID 的值
	 * 
	 * @param Long
	 *            rowId
	 */
	public void setRowId(String rowId) {
		this.rowId = rowId;
	}

	public String getToId() {
		return toId;
	}

	public void setToId(String toId) {
		this.toId = toId;
	}

	public String getFromId() {
		return fromId;
	}

	public void setFromId(String fromId) {
		this.fromId = fromId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getReqUrl() {
		return reqUrl;
	}

	public void setReqUrl(String reqUrl) {
		this.reqUrl = reqUrl;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	/**
	 * 获取 消息内容 的值
	 * 
	 * @return String
	 */
	public String getMsgContent() {
		return msgContent;
	}

	/**
	 * 设置消息内容 的值
	 * 
	 * @param String
	 *            msgContent
	 */
	public void setMsgContent(String msgContent) {
		this.msgContent = msgContent;
	}

	/**
	 * 获取 发送时间 的值
	 * 
	 * @return Timestamp
	 */
	public Timestamp getSendDate() {
		return sendDate;
	}

	/**
	 * 设置发送时间 的值
	 * 
	 * @param Timestamp
	 *            sendDate
	 */
	public void setSendDate(Timestamp sendDate) {
		this.sendDate = sendDate;
	}

	/**
	 * 获取 阅读状态，01已阅，00未阅 的值
	 * 
	 * @return String
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * 设置阅读状态，01已阅，00未阅 的值
	 * 
	 * @param String
	 *            status
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	public String getOperaState() {
		return operaState;
	}

	public void setOperaState(String operaState) {
		this.operaState = operaState;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getClass().getName());
		sb.append("; rowId=" + (rowId == null ? "null" : rowId.toString()));
		sb.append("; fromId=" + (fromId == null ? "null" : fromId.toString()));
		sb.append("; toId=" + (toId == null ? "null" : toId.toString()));
		sb.append("; groupId=" + (groupId == null ? "null" : groupId.toString()));
		sb.append("; reqUrl=" + (reqUrl == null ? "null" : reqUrl.toString()));
		sb.append("; msgType=" + (msgType == null ? "null" : msgType.toString()));
		sb.append("; msgContent=" + (msgContent == null ? "null" : msgContent.toString()));
		sb.append("; sendDate=" + (sendDate == null ? "null" : sendDate.toString()));
		sb.append("; status=" + (status == null ? "null" : status.toString()));
		sb.append("; operaState=" + (operaState == null ? "null" : operaState.toString()));

		return sb.toString();
	}
}