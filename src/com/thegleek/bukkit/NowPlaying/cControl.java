package com.thegleek.bukkit.NowPlaying;

import java.util.HashMap;

/**
 * @author thegleek
 * 
 */
public class cControl {
	public static HashMap<String, String> permissions = new HashMap<String, String>();

	/**
	 * @param controller
	 * @param groups
	 */
	public static void add(String controller, String groups) {
		permissions.put(controller, groups);
	}

	/**
	 * @param setting
	 * @return
	 */
	public static String getApiKey(String setting) {
		if (!permissions.containsKey(setting)) {
			return "";
		}

		String apiKey = (String) permissions.get(setting);

		return apiKey;
	}
}
