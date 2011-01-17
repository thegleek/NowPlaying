package com.bukkit.thegleek.NowPlaying;

import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.Server;

public class AliasList {
	public static final Logger log = Logger.getLogger("Minecraft");
	public static HashMap<String, String> lastfmList;
	private Server server;
	public static Player player = null;
	public static Player dupPlayer = null;

	public AliasList(Server serv) {
		this.server = serv;
		cPlayerData.initialize();
		lastfmList = cPlayerData.getAliases();
	}

	public void addAlias(Player pPlayer, String lastFMname) {
		String msg = "";

		player = pPlayer;

		String inGameName = player.getName();
		dupPlayer = server.getPlayer(lastFMname);

		if (lastfmList.containsKey(inGameName)) {
			msg = "You already have a lastFM name in the database.";
			cChat.send(player, ChatColor.RED + msg);
			log.info(msg);
		} else if (lastfmList.containsValue(lastFMname)) {
			msg = lastFMname + " for '" + inGameName + "' already exists.";
			cChat.send(player, ChatColor.RED + msg);
			log.info(msg);
		} else {
			if (cPlayerData.addAlias(inGameName, lastFMname)) {
				msg = "Successfully added '" + lastFMname + "'";
				cChat.send(player, ChatColor.AQUA + msg);
				log.info(msg);
				lastfmList = cPlayerData.getAliases();// reload list from db
			} else {
				msg = "Failed to add your alias";
				cChat.send(player, ChatColor.RED + msg);
				log.warning(msg);
			}
		}
	}

	public void delAlias(Player pPlayer) {
		player = pPlayer;
		String inGameName = player.getName();
		String msg = "";

		if (lastfmList.containsKey(inGameName)) {
			if (cPlayerData.delAlias(inGameName)) {
				msg = "Successfully deleted your alias";
				cChat.send(player, ChatColor.AQUA + msg);
				log.info(msg);
				lastfmList = cPlayerData.getAliases();// reload list from db
			} else {
				msg = "Failed to delete your alias";
				cChat.send(player, ChatColor.RED + msg);
				log.warning(msg);
			}
		}
	}

	public int getSize() {
		int iSize = 0;

		try {
			iSize = lastfmList.size();
		} catch (Exception ex) {
			log.warning("[NowPlaying]: NowPlaying.db exists, but is empty.");
		}

		return iSize;
	}
}
