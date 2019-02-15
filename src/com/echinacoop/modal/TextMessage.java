package com.echinacoop.modal;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class TextMessage extends ChatMessage {

	private JSONObject font = new JSONObject();

	private JSONArray msg;
	
	public TextMessage(){
		
	}
	
	public TextMessage(String str){
		addMsg(str);
	}
	
	public TextMessage(String str, JSONObject font){
		addMsg(str);
		setFont(font);
	}

	public JSONObject getFont() {
		return font;
	}

	public void setFont(JSONObject font) {
		this.font = font;
	}

	public JSONArray getMsg() {
		return msg;
	}

	public void setMsg(JSONArray msg) {
		this.msg = msg;
	}

	public void addMsg(String str) {
		if (null == msg) {
			msg = new JSONArray();
		}
		JSONObject text = new JSONObject();
		text.put("text", str);
		msg.add(text);
	}

}
