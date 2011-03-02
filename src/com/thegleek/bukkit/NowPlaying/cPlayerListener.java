package com.thegleek.bukkit.NowPlaying;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.umass.lastfm.*;

public class cPlayerListener implements CommandExecutor {
	private static final Logger log = Logger.getLogger("Minecraft");

	public NowPlaying plugin;
	public static NowPlaying StaticNP;
	public static AliasList aliasList;
	private ArrayList<String> aTextOutput = new ArrayList<String>();
	public static String sMaxString = "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"; // len=44

	/**
	 * @param instance
	 */
	public cPlayerListener(NowPlaying instance) {
		this.plugin = instance;
		StaticNP = instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender
	 * , org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	public boolean onCommand(CommandSender sender, Command command,
			String commandLabel, String[] args) {
		boolean bRet = false;
		String[] trimmedArgs = args;
		String commandName = command.getName().toLowerCase();
		String key = cControl.getApiKey(NowPlaying.watching[0]);

		log.info("[NowPlaying] command: " + command.getName());

		if (sender instanceof Player) {
			Player player = (Player) sender;

			cChat.save(player);

			if (commandName.equals("nphelp")) {
				return performNPHelp();
			} else if (commandName.equals("npver")) {
				cChat.send(ChatColor.GOLD + plugin.getVersion());
				bRet = true;
			} else if (commandName.equals("npsize")) {
				return performNPSize();
			} else if (commandName.equals("npadd")) {
				return performNPAdd(player, trimmedArgs);
			} else if (commandName.equals("npdel")) {
				return performNPDel(player, trimmedArgs);
			} else if (commandName.equals("npcheck")) {
				return performNPCheck(trimmedArgs);
			} else if (commandName.equals("npb")) {
				if (key == NowPlaying.defaults[0]) {
					cChat.send(ChatColor.DARK_RED
							+ "The server admin has not set the APIKEY yet!");
					bRet = true;
				} else {
					return performNP(player, trimmedArgs, key, true);
				}
			} else if (commandName.equals("np")) {
				if (key == NowPlaying.defaults[0]) {
					cChat.send(ChatColor.DARK_RED
							+ "The server admin has not set the APIKEY yet!");
					bRet = true;
				} else {
					return performNP(player, trimmedArgs, key, false);
				}
			}
		}

		return bRet;
	}

	/**
	 * @param aList
	 */
	public static void setAliasList(AliasList aList) {
		aliasList = aList;
	}

	/**
	 * @return
	 */
	public boolean performNPSize() {
		int iAliasSize = aliasList.getSize();
		cChat.send(ChatColor.AQUA + "Alias records in db: " + iAliasSize);
		return true;
	}

	/**
	 * @param player
	 * @param split
	 * @return
	 */
	public boolean performNPAdd(Player player, String[] split) {
		if (split.length > 0) {
			String sLongName = "";
			int x = 0;

			for (String s : split) {
				if (x > 0) {
					sLongName += s + " ";
				} else {
					sLongName = s;
				}

				x++;
			}
			aliasList.addAlias(player, sLongName.trim());
		}

		return true;
	}

	/**
	 * @param player
	 * @param split
	 * @return
	 */
	public boolean performNPDel(Player player, String[] split) {
		aliasList.delAlias(player);

		return true;
	}

	/**
	 * @param split
	 * @return
	 */
	public boolean performNPCheck(String[] split) {
		if (split.length == 0) {
			Map<String, String> m = AliasList.lastfmList;
			Iterator<Map.Entry<String, String>> it = m.entrySet().iterator();

			while (it.hasNext()) {
				Map.Entry<String, String> pairs = it.next();

				log.info("[NowPlaying] " + pairs.getKey() + ":"
						+ pairs.getValue());
			}
		} else if (split.length == 1) {
			String sFound = findValueInStack(split[0], AliasList.lastfmList);

			if (sFound != null) {
				cChat.send(ChatColor.AQUA + "LastFM alias exists in the db...");
			}

			if (AliasList.lastfmList.containsKey(split[0])) {
				cChat.send(ChatColor.AQUA + "Player exists in the db...");
			}
		}

		return true;
	}

	/**
	 * @return
	 */
	public boolean performNPHelp() {
		String sCmd = ChatColor.WHITE + "/np (player)" + ChatColor.GOLD + " - "
				+ ChatColor.YELLOW + "Shows what you/player is listening to";
		cChat.send(sCmd);

		sCmd = ChatColor.WHITE + "/npb (player)" + ChatColor.GOLD + " - "
				+ ChatColor.YELLOW
				+ "Broadcasts what you/player is listening to";
		cChat.send(sCmd);

		sCmd = ChatColor.WHITE + "/nphelp" + ChatColor.GOLD + " - "
				+ ChatColor.YELLOW + "Help for the NowPlaying commands";
		cChat.send(sCmd);

		sCmd = ChatColor.WHITE + "/npver" + ChatColor.GOLD + " - "
				+ ChatColor.YELLOW + "Displays the NowPlaying plugin version";
		cChat.send(sCmd);

		sCmd = ChatColor.WHITE + "/npadd [lastFM name]" + ChatColor.GOLD
				+ " - " + ChatColor.YELLOW
				+ "Adds your lastFM alias to your nick";
		cChat.send(sCmd);

		sCmd = ChatColor.WHITE + "/npdel" + ChatColor.GOLD + " - "
				+ ChatColor.YELLOW + "Deletes your lastFM alias";
		cChat.send(sCmd);

		sCmd = ChatColor.WHITE + "/npcheck (player/alias)" + ChatColor.GOLD
				+ " - " + ChatColor.YELLOW + "Check if entry exists in the db";
		cChat.send(sCmd);

		return true;
	}

	/**
	 * @param player
	 * @param split
	 * @param key
	 * @param bBroadcast
	 * @return
	 */
	public boolean performNP(Player player, String[] split, String key,
			Boolean bBroadcast) {
		if (split.length == 0) {
			String sFound = findKeyInStack(player.getName(),
					AliasList.lastfmList);

			if (sFound == null) {
				sFound = player.getName();
			}

			aTextOutput = getRecentTrack(key, sFound, player.getName(),
					bBroadcast);

			for (String s : this.aTextOutput) {
				if (bBroadcast) {
					cChat.broadcast(s);
				} else {
					cChat.send(s);
				}
			}
		} else if (split.length == 1) {
			String sFound = findKeyInStack(split[0], AliasList.lastfmList);

			if (sFound == null) {
				sFound = split[0];
			}

			aTextOutput = getRecentTrack(key, sFound, split[0], bBroadcast);

			for (String s : this.aTextOutput) {
				if (bBroadcast) {
					cChat.broadcast(s);
				} else {
					cChat.send(s);
				}
			}
		}

		return true;
	}

	/**
	 * @param apiKey
	 * @param user
	 * @param alias
	 * @param bBroadcast
	 * @return
	 */
	public static ArrayList<String> getRecentTrack(String apiKey, String user,
			String alias, Boolean bBroadcast) {
		String preOutput = "";
		String currentInfo = "";
		String sDisplayName = "";
		ArrayList<String> aOutput = new ArrayList<String>();

		if (bBroadcast) {
			if (user.equalsIgnoreCase(alias)) {
				sDisplayName = ChatColor.GOLD + user;
			} else {
				sDisplayName = ChatColor.YELLOW + alias;
			}
		} else {
			if (user.equalsIgnoreCase(alias)) {
				sDisplayName = ChatColor.DARK_AQUA + user;
			} else {
				sDisplayName = ChatColor.AQUA + alias;
			}
		}

		try {
			PaginatedResult<Track> nowPlaying = User.getRecentTracks(user, 1,
					apiKey, 1);

			// log.info(nowPlaying.toString());

			Collection<Track> tracks = nowPlaying.getPageResults();

			// log.info(tracks.toString());

			for (Track track : tracks) {
				String trackName = track.getName();
				String trackArtist = track.getArtist();
				String trackAlbum = track.getAlbum();
				boolean bNowPlaying = track.isNowPlaying();
				Date tracklastPlayed = track.getPlayedWhen();

				if (bNowPlaying) {
					currentInfo = sDisplayName + ChatColor.BLUE
							+ " is currently listening to: ";

					preOutput = trackName + " - " + trackArtist;

					if (!trackAlbum.isEmpty()) {
						preOutput += " (" + trackAlbum + ") ";
					}

					aOutput = ParseString(currentInfo, preOutput,
							ChatColor.BLUE);
				} else {
					currentInfo = sDisplayName + ChatColor.DARK_GRAY
							+ " last played: ";

					preOutput = trackName + " - " + trackArtist;

					if (!trackAlbum.isEmpty()) {
						preOutput += " (" + trackAlbum + ") ";
					}

					aOutput = ParseString(currentInfo, preOutput,
							ChatColor.DARK_GRAY);
				}

				if (!bNowPlaying) {
					aOutput.add(ChatColor.DARK_GRAY + "at "
							+ tracklastPlayed.toString());
				}

				break;// seems to always be 2 tracks in the queue, so break out
						// of the loop the first time
			}
		} catch (Exception ex) {
			log.warning("[NowPlaying] last.fm api failed: " + ex.toString());
		}

		return aOutput;
	}

	/**
	 * @param sLeft
	 * @param sRight
	 * @param cColor
	 * @return
	 */
	private static ArrayList<String> ParseString(String sLeft, String sRight,
			ChatColor cColor) {
		String Ltemp = "";
		String Rtemp = "";
		ArrayList<String> aString = new ArrayList<String>();
		ArrayList<String> outArray = new ArrayList<String>();
		int maxLen = cChat.getStringWidth(sMaxString);
		int right = cChat.getStringWidth(sRight);
		int Ldiff = (maxLen - cChat.getStringWidth(sLeft));

		if (right > Ldiff) {
			int iDiff = (right - Ldiff);
			aString = cChat.aSubstring(sRight, Ldiff, cColor);
			Ltemp = aString.get(0);
			Rtemp = aString.get(1);
			int rlen = Rtemp.length();

			outArray.add(sLeft + Ltemp);

			if (iDiff > maxLen) {
				aString = cChat.aSubstring(Rtemp, maxLen, cColor);
				Ltemp = aString.get(0);
				Rtemp = aString.get(1);
				rlen = Rtemp.length();

				outArray.add(Ltemp);
				if (rlen > 0) {
					outArray.add(Rtemp);
				}
			} else if (rlen > 0) {
				outArray.add(Rtemp);
			}
		} else {
			outArray.add(sLeft + sRight);
		}

		return outArray;
	}

	/**
	 * @param needle
	 * @param haystack
	 * @return
	 */
	private String findValueInStack(String needle, Map<String, String> haystack) {
		Iterator<Map.Entry<String, String>> it = haystack.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();

			if (entry.getValue().equals(needle)) {
				return entry.getKey();
			}
		}

		return null;
	}

	/**
	 * @param needle
	 * @param haystack
	 * @return
	 */
	private String findKeyInStack(String needle, Map<String, String> haystack) {
		Iterator<Map.Entry<String, String>> it = haystack.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();

			if (entry.getKey().equals(needle)) {
				return entry.getValue();
			}
		}

		return null;
	}
}
