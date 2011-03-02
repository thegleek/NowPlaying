package com.thegleek.bukkit.NowPlaying;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author thegleek
 * 
 */
public final class cProperty {
	private static final Logger log = Logger.getLogger("Minecraft");
	private Properties properties;
	private String fileName;

	/**
	 * @param fileName
	 */
	public cProperty(String fileName) {
		this.fileName = fileName;
		this.properties = new Properties();
		File file = new File(fileName);

		if (file.exists()) {
			load();
		} else {
			save();
		}
	}

	/**
	 * 
	 */
	public void load() {
		try {
			this.properties.load(new FileInputStream(this.fileName));
		} catch (IOException ex) {
			log.log(Level.SEVERE, "Unable to load " + this.fileName, ex);
		}
	}

	/**
	 * 
	 */
	public void save() {
		try {
			this.properties.store(new FileOutputStream(this.fileName),
					"NowPlaying Properties File");
		} catch (IOException ex) {
			log.log(Level.SEVERE, "Unable to save " + this.fileName, ex);
		}
	}

	/**
	 * @return
	 */
	public Map<String, String> returnProperties() {
		String key = "";
		String value = "";
		Map<String, String> map = new HashMap<String, String>();

		for (Entry<Object, Object> obj : properties.entrySet()) {
			key = obj.getKey().toString();
			value = obj.getValue().toString();

			map.put(key, value);
		}

		return map;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> returnMap() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		BufferedReader reader = new BufferedReader(
				new FileReader(this.fileName));
		String line;

		while ((line = reader.readLine()) != null) {
			if ((line.trim().length() == 0) || (line.charAt(0) == '#')) {
				continue;
			}

			int delimPosition = line.indexOf('=');
			String key = line.substring(0, delimPosition).trim();
			String value = line.substring(delimPosition + 1).trim();
			map.put(key, value);
		}
		reader.close();

		return map;
	}

	/**
	 * @param key
	 */
	public void removeKey(String key) {
		this.properties.remove(key);
		save();
	}

	/**
	 * @param key
	 * @return
	 */
	public boolean keyExists(String key) {
		return this.properties.containsKey(key);
	}

	/**
	 * @param key
	 * @return
	 */
	public String getString(String key) {
		if (this.properties.containsKey(key)) {
			return this.properties.getProperty(key);
		}

		return "";
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 */
	public String getString(String key, String value) {
		if (this.properties.containsKey(key)) {
			return this.properties.getProperty(key);
		}
		setString(key, value);
		return value;
	}

	/**
	 * @param key
	 * @param value
	 */
	public void setString(String key, String value) {
		this.properties.setProperty(key, value);
		save();
	}

	/**
	 * @param key
	 * @return
	 */
	public int getInt(String key) {
		if (this.properties.containsKey(key)) {
			return Integer.parseInt(this.properties.getProperty(key));
		}

		return 0;
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 */
	public int getInt(String key, int value) {
		if (this.properties.containsKey(key)) {
			return Integer.parseInt(this.properties.getProperty(key));
		}

		setInt(key, value);
		return value;
	}

	/**
	 * @param key
	 * @param value
	 */
	public void setInt(String key, int value) {
		this.properties.setProperty(key, String.valueOf(value));
		save();
	}

	/**
	 * @param key
	 * @return
	 */
	public double getDouble(String key) {
		if (this.properties.containsKey(key)) {
			return Double.parseDouble(this.properties.getProperty(key));
		}

		return 0.0D;
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 */
	public double getDouble(String key, double value) {
		if (this.properties.containsKey(key)) {
			return Double.parseDouble(this.properties.getProperty(key));
		}

		setDouble(key, value);
		return value;
	}

	/**
	 * @param key
	 * @param value
	 */
	public void setDouble(String key, double value) {
		this.properties.setProperty(key, String.valueOf(value));
		save();
	}

	/**
	 * @param key
	 * @return
	 */
	public long getLong(String key) {
		if (this.properties.containsKey(key)) {
			return Long.parseLong(this.properties.getProperty(key));
		}

		return 0L;
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 */
	public long getLong(String key, long value) {
		if (this.properties.containsKey(key)) {
			return Long.parseLong(this.properties.getProperty(key));
		}

		setLong(key, value);
		return value;
	}

	/**
	 * @param key
	 * @param value
	 */
	public void setLong(String key, long value) {
		this.properties.setProperty(key, String.valueOf(value));
		save();
	}

	/**
	 * @param key
	 * @return
	 */
	public boolean getBoolean(String key) {
		if (this.properties.containsKey(key)) {
			return Boolean.parseBoolean(this.properties.getProperty(key));
		}

		return false;
	}

	/**
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean getBoolean(String key, boolean value) {
		if (this.properties.containsKey(key)) {
			return Boolean.parseBoolean(this.properties.getProperty(key));
		}

		setBoolean(key, value);
		return value;
	}

	/**
	 * @param key
	 * @param value
	 */
	public void setBoolean(String key, boolean value) {
		this.properties.setProperty(key, String.valueOf(value));
		save();
	}
}
