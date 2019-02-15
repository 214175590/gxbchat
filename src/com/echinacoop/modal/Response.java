package com.echinacoop.modal;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * 统一的结果对象<br>
 * 统一写出到前端的数据包<br>
 * 出入参均以Map容器存储
 * 
 * @Time 2016-12-05 19:51
 * @GeneratedByCodeFactory
 */
public class Response {

	public static final String DATA_KEY = "data";

	private String code = "999999";

	private String message = "失败";

	private JSONObject rtn = new JSONObject();

	public String getCode() {
		return code;
	}

	public Response setCode(String code) {
		this.code = code;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public Response setMessage(String message) {
		this.message = message;
		return this;
	}

	public JSONObject getRtn() {
		return rtn;
	}

	public Response setRtn(JSONObject result) {
		this.rtn = result;
		return this;
	}

	public JSONObject getDataForRtn() {
		return this.rtn.getJSONObject(DATA_KEY);
	}

	/**
	 * 判断操作是否成功
	 * 
	 * @return true 成功，false 失败
	 */
	public boolean isSuccess() {
		return this.code != null && this.code.equals("000000");
	}

}