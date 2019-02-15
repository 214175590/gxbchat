package com.echinacoop.modal;

import com.echinacoop.utils.Utils;

public class WSData {
	private String url;
	private Object body;
	private Object head;

	public WSData() {
		this.setHead(Utils.getClientInfo());
	}

	public WSData(String url, Object body) {
		this.setUrl(url);
		this.setHead(Utils.getClientInfo());
		this.setBody(body);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public Object getHead() {
		return head;
	}

	public void setHead(Object head) {
		this.head = head;
	}

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

}
