package com.bukkit.thegleek.NowPlaying;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.Server;

//import bsh.Interpreter;//TODO: REMOVE BEFORE DISTRIBUTION!

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

	// public Interpreter beanshell;// TODO: REMOVE BEFORE DISTRIBUTION!

	private cPlayerListener playerListener = new cPlayerListener(this);
	private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
	private static String sPluginName = "NowPlaying";
	private static String sCodeName = "CrystalCastles";
	private static String sPluginDir = sPluginName + "/";
	private String sVersion;
	private static NowPlaying staticThis = null;

	public NowPlaying(PluginLoader pluginLoader, Server instance,
			PluginDescriptionFile desc, File folder, File plugin,
			ClassLoader cLoader) {
		super(pluginLoader, instance, desc, folder, plugin, cLoader);

		// registerEvents();

		this.setVersion("loaded");
		log.info(this.getVersion());
	}

	public static NowPlaying getStatic() {
		return staticThis;
	}

	// TODO: REMOVE BEFORE DISTRIBUTION!
	// public void EnableDebug() {
	// enables the beanshell remote interpreter
	// beanshell = new Interpreter();

	// try {
	// beanshell.set("plugin", this); // Provide a reference to your app
	// beanshell.set("portnum", 1234);
	// beanshell.eval("setAccessibility(true)"); // turn off access
	// restrictions
	// beanshell.eval("server(portnum)");
	// } catch (Exception e) {
	// log.severe("Beanshell initialization failure");
	// }
	// }

	public String getVersion() {
		return this.sVersion;
	}

	public void setVersion(String sVersion) {
		this.sVersion = this.sVersionMessage(sVersion);
	}

	/*
	 * public void registerEvents() { PluginManager pm =
	 * getServer().getPluginManager();
	 * 
	 * pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal,
	 * this);
	 * 
	 * pm.registerEvent(Event.Type.PLAYER_COMMAND, playerListener,
	 * Priority.Normal, this);
	 * 
	 * }
	 */

	public void onEnable() {
		staticThis = this;

		this.playerListener = new cPlayerListener(this);
		PluginManager pm = getServer().getPluginManager();

		pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener,
				Event.Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener,
				Event.Priority.Monitor, this);
		pm.registerEvent(Event.Type.PLAYER_COMMAND, this.playerListener,
				Priority.Monitor, this);

		new File(sPluginDir).mkdir();
		Settings = new cProperty(sPluginDir + sPluginName + ".settings");

		cPlayerListener.setAliasList(new AliasList(getServer()));

		for (int x = 0; x < watching.length; x++)
			cControl.add(watching[x],
					Settings.getString(watching[x], defaults[x]));

		// EnableDebug();// TODO: REMOVE BEFORE DISTRIBUTION!

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

	private boolean isDebugging(final Player player) {
		if (debugees.containsKey(player)) {
			return debugees.get(player);
		} else {
			return false;
		}
	}

	private void setDebugging(final Player player, final boolean value) {
		debugees.put(player, value);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args) {
		boolean bRet = true;
		String[] trimmedArgs = args;
		String commandName = command.getName().toLowerCase();
		String key = cControl.getApiKey(watching[0]);

		log.info("[NowPlaying] command: " + command.getName());

		if (sender instanceof Player) {
			Player player = (Player) sender;

			cChat.save(player);

			if (commandName.equals("debug")) {
				setDebugging(player, !isDebugging(player));
			} else if (commandName.equals("nphelp")) {
				return playerListener.performNPHelp();
			} else if (commandName.equals("npver")) {
				cChat.send(ChatColor.GOLD + getVersion());
			} else if (commandName.equals("npsize")) {
				return playerListener.performNPSize();
			} else if (commandName.equals("npadd")) {
				return playerListener.performNPAdd(player, trimmedArgs);
			} else if (commandName.equals("npdel")) {
				return playerListener.performNPDel(player, trimmedArgs);
			} else if (commandName.equals("npcheck")) {
				return playerListener.performNPCheck(trimmedArgs);
			} else if (commandName.equals("npb")) {
				if (key == defaults[0]) {
					cChat.send(ChatColor.RED
							+ "The server admin has not set the APIKEY for this plugin to work!");
					bRet = false;
				} else {
					bRet = playerListener.performNP(player, trimmedArgs, key, true);
				}
			} else if (commandName.equals("np")) {
				if (key == defaults[0]) {
					cChat.send(ChatColor.RED
							+ "The server admin has not set the APIKEY for this plugin to work!");
					bRet = false;
				} else {
					bRet = playerListener.performNP(player, trimmedArgs, key, false);
				}
			} else {
				bRet = false;
			}
		}

		return bRet;
	}
}
