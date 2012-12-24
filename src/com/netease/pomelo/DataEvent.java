package com.netease.pomelo;

import java.util.EventObject;

import org.json.JSONObject;

/**
 * Data event of broadcast message.
 * 
 */
public class DataEvent extends EventObject {

	private JSONObject message;

	public JSONObject getMessage() {
		return message;
	}

	public void setMessage(JSONObject message) {
		this.message = message;
	}

	public DataEvent(Object source, JSONObject message) {
		super(source);
		this.message = message;
	}

}
