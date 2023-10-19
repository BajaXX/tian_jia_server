package com.bemore.api.util;

import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonUtil {

	public static Gson gson = buildGson();
	
	public static String build() {
		return build(0, "ok", null);
	}
	
	public static String build(Object result) {
		return build(0, "ok", result);
	}

	public static String build(int code, String message, Object result) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("code", code);
		map.put("message", message);
		if (result != null) {
			map.put("data", result);
		}
		return gson.toJson(map);
	}	
	
	private static Gson buildGson() {
		Gson gson = new GsonBuilder().serializeNulls().create();
		return gson;
	}
}
