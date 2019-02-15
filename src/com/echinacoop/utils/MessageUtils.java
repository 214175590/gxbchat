package com.echinacoop.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.echinacoop.controller.SocketService;
import com.echinacoop.modal.FontAttrib;
import com.echinacoop.modal.Message;
import com.echinacoop.modal.MessageType;

public class MessageUtils {

	private static JSONObject faceStore = null;

	private static String getFaceUrl(String key) {
		String url = "";
		if (null == faceStore) {
			String content = ResourceUtils.readResourceFile("facejson");
			faceStore = JSONObject.parseObject(content);
		}
		if (key != null) {
			JSONObject face = faceStore.getJSONObject(key);
			if (face != null) {
				url = face.getString("path").substring(6);
			}
		}
		return url;
	}

	public static JSONObject getFaceStore() {
		if (null == faceStore) {
			String content = ResourceUtils.readResourceFile("facejson");
			faceStore = JSONObject.parseObject(content);
		}
		return faceStore;
	}

	public static List<Message> formatMessage(JSONObject msgJson) {
		List<Message> result = new ArrayList<Message>();
		if (null != msgJson) {
			if (msgJson.containsKey("msg")) {
				try {
					JSONArray msg = msgJson.getJSONArray("msg");
					JSONObject fontJson = msgJson.getJSONObject("font");
					if (msg != null) {
						String text = "";
						String pattern = "\\[\\[_[OKN\\u4e00-\\u9fa5]+\\]\\]";
						Pattern r = Pattern.compile(pattern);
						Matcher m = null;
						Message message = null;
						String group = null;
						int start = 0, end = 0;
						FontAttrib fontAttr = new FontAttrib();
						for (int i = 0, k = msg.size(); i < k; i++) {
							text = msg.getJSONObject(i).getString("text");
							end = text.length();
							m = r.matcher(text);
							while (m.find()) {
								group = m.group();
								if ((start == 0 && m.start() != 0) || (start != 0 && m.start() > start)) {
									message = new Message();
									message.setMessageType(MessageType.TEXT);
									fontAttr = new FontAttrib();
									fontAttr.setText(text.substring(start, m.start()));
									fontAttr.setFont(fontJson);
									message.setMsg(fontAttr);
									result.add(message);
								}
								message = new Message();
								message.setMessageType(MessageType.FACE);
								fontAttr = new FontAttrib();
								fontAttr.setText(getFaceUrl(group));
								fontAttr.setFont(fontJson);
								message.setMsg(fontAttr);
								result.add(message);
								start = m.end();
							}
							if (start < end) {
								message = new Message();
								message.setMessageType(MessageType.TEXT);
								fontAttr = new FontAttrib();
								fontAttr.setText(text.substring(start, end));
								fontAttr.setFont(fontJson);
								message.setMsg(fontAttr);
								result.add(message);
							}
						}
						
						//fontJson
						
					}
				} catch (Exception e) {
				}
			} else if (msgJson.containsKey("pic")) {
				try {
					JSONObject pic = msgJson.getJSONObject("pic");
					if (pic != null) {
						FontAttrib fontAttr = new FontAttrib();
						String filePath = pic.getString("filePath");
						//String[] files = filePath.split("[.]");
						Message message = new Message();
						message.setMessageType(MessageType.PIC);
						//message.setMsg(SocketService.imgServerUrl + files[0] + "_large." + files[1]);
						fontAttr.setText(SocketService.imgServerUrl + filePath);
						message.setMsg(fontAttr);
						result.add(message);
					}
				} catch (Exception e) {
				}
			} else if (msgJson.containsKey("voice")) {
				try {
					JSONObject voice = msgJson.getJSONObject("voice");
					if (voice != null) {
						FontAttrib fontAttr = new FontAttrib();
						Message message = new Message();
						message.setMessageType(MessageType.VOICE);
						fontAttr.setText(SocketService.imgServerUrl + voice.getString("filePath"));
						message.setMsg(fontAttr);
						result.add(message);
					}
				} catch (Exception e) {
				}
			}
		}
		return result;
	}

	public static List<JSONObject> parseMessage(String text) {
		List<JSONObject> message = new ArrayList<JSONObject>();
		try {
			String pattern = "\\}[\\s]{0,}\\{";
			String place = "\\}#___________#\\{";
			String split = "#___________#";
			text = text.replaceAll(pattern, place);
			String[] texts = text.split(split);
			for (String string : texts) {
				try {
					message.add(JSONObject.parseObject(string));
				} catch (Exception e) {
				}
			}
		} catch (Exception e) {
		}
		return message;
	}
	
	public static String getLastChatMsg(JSONObject lastChat){
		List<Message> msgList = MessageUtils.formatMessage(lastChat);
		String msg = "";
		if (null != msgList) {
			for (int i = 0, k = msgList.size(); i < k; i++) {
				if (msgList.get(i).getMessageType() == MessageType.TEXT) {
					msg += msgList.get(i).getMsg().getText();
				} else if (msgList.get(i).getMessageType() == MessageType.FACE) {
					msg += "[表情]";
				} else if (msgList.get(i).getMessageType() == MessageType.PIC) {
					msg += "[图片]";
				} else if (msgList.get(i).getMessageType() == MessageType.VOICE) {
					msg += "[语音]";
				}
			}
		}
		return msg;
	}

}
