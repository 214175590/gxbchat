package com.echinacoop.modal;

public class SingleChatMessage {
	private Object msgId;
	private String msgType; // 01;
	private String fromUid; // 4130328922,
	private String toUid; // 214175590,
	private long time; // 1441902439,
	private ChatMessage content;

	public Object getMsgId() {
		return msgId;
	}

	public void setMsgId(Object msgId) {
		this.msgId = msgId;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getFromUid() {
		return fromUid;
	}

	public void setFromUid(String fromUid) {
		this.fromUid = fromUid;
	}

	public String getToUid() {
		return toUid;
	}

	public void setToUid(String toUid) {
		this.toUid = toUid;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public ChatMessage getContent() {
		return content;
	}

	public void setContent(ChatMessage content) {
		this.content = content;
	}

}
