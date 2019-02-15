package com.echinacoop.controller;

import com.alibaba.fastjson.JSONObject;
import com.yinsin.utils.CommonUtils;

public class BaseHandler {
	
	protected boolean isSuccess(JSONObject body) {
		String code = CommonUtils.excNullToString(body.getString("code"), "");
		return code.equals("000000");
	}
	
}
