package com.bukkit.thegleek.NowPlaying;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Last.FM plugin for Bukkit
 * 
 * @author thegleek
 */
public class NowPlaying extends JavaPlugin {
	public PluginDescriptionFile pdfFile = this.getDescription();
	public static final Logger log = Logger.getLogger("Minecraft");
	public static HashMap<String, String> players;
	public static cControl Watch = new cControl();
	public static cProperty Settings;
	public static String[] watching = { "apikey" };
	public static String[] defaults = { "YOUR_LASTFM_API_KEY_GOES_HERE" };

	private cPlayerListener playerListener = new cPlayerListener(this);
	private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
	private static String sPluginName = "NowPlaying";
	private static String sCodeName = "Bruderschaft";
	private static String sPluginDir = sPluginName + "/";
	private String sVersion;

	public NowPlaying(PluginLoader pluginLoader, Server instance,
			PluginDescriptionFile desc, File folder, File plugin,
			ClassLoader cLoader) {
		super(pluginLoader, instance, desc, folder, plugin, cLoader);

		registerEvents();

		this.setVersion("loaded");
		log.info(this.getVersion());
	}

	public String getVersion() {
		return this.sVersion;
	}

	public void setVersion(String sVersion) {
		this.sVersion = this.sVersionMessage(sVersion);
	}

	public void registerEvents() {
		PluginManager pm = getServer().getPluginManager();

		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener,
				Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_COMMAND, playerListener,
				Priority.Normal, this);
	}

	public void onEnable() {
		new File(sPluginDir).mkdir();
		Settings = new cProperty(sPluginDir + sPluginName + ".settings");

		cPlayerListener.setAliasList(new AliasList(getServer()));

		for (int x = 0; x < watching.length; x++)
			cControl.add(watching[x],
					Settings.getString(watching[x], defaults[x]));

		this.setVersion("enabled");
		log.info(this.getVersion());
	}

	public void onDisable() {
		this.setVersion("disabled");
		log.info(this.getVersion());
	}

	private String sVersionMessage(String msg) {
		sVersion = "[" + pdfFile.getName() + "]: v" + pdfFile.getVersion()
				+ " (" + sCodeName + ")";

		if (!msg.isEmpty()) {
			sVersion += " " + msg;
		}

		sVersion += ".";

		return sVersion;
	}

	public boolean isDebugging(final Player player) {
		if (debugees.containsKey(player)) {
			return debugees.get(player);
		} else {
			return false;
		}
	}

	public void setDebugging(final Player player, final boolean value) {
		debugees.put(player, value);
	}
}
