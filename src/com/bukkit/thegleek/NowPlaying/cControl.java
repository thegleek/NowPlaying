package com.bukkit.thegleek.NowPlaying;

import java.util.HashMap;

public class cControl {
	public static HashMap<String, String> permissions = new HashMap<String, String>();

	public static void add(String controller, String groups) {
		permissions.put(controller, groups);
	}

	public static String getApiKey(String setting) {
		if (!permissions.containsKey(setting)) {
			return "";
		}

		String apiKey = (String) permissions.get(setting);

		return apiKey;
	}
}
