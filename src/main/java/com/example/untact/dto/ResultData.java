package com.example.untact.dto;

import java.util.Map;

import com.example.untact.util.Util;

import lombok.Data;

@Data
public class ResultData {
	private String resultCode;
	private String msg;
	private Map<String, Object> body;
	
	public ResultData (String resultCode, String msg, Object... args) {
		this.resultCode = resultCode;
		this.msg = msg;
		this.body = Util.mapOf(args);
	}
	
	public boolean isSeccess() {
		return resultCode.startsWith("S-");
	}
	
	public boolean isFail() {
		return resultCode.startsWith("F-");
	}
}
