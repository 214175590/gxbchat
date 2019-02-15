package com.echinacoop.modal;

import com.alibaba.fastjson.JSONObject;

public class PicMessage extends ChatMessage {

	private JSONObject font = new JSONObject();

	private JSONObject pic = new JSONObject();
	
	public PicMessage(){
		
	}
	
	public PicMessage(String filePath, int width, int height){
		addPicMsg(filePath, width, height);
	}

	public JSONObject getFont() {
		return font;
	}

	public void setFont(JSONObject font) {
		this.font = font;
	}

	public JSONObject getPic() {
		return pic;
	}

	public void setPic(JSONObject pic) {
		this.pic = pic;
	}

	public void addPicMsg(String filePath, int width, int height) {
		if (null == pic) {
			pic = new JSONObject();
		}
		pic.put("filePath", filePath);
		pic.put("width", width);
		pic.put("height", height);
	}

}
