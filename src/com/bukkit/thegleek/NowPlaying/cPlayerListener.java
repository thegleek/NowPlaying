package com.bukkit.thegleek.NowPlaying;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;
import de.umass.lastfm.*;

/**
 * Handle events for all Player related events
 * 
 * @author thegleek
 */
public class cPlayerListener extends PlayerListener {
	private static final Logger log = Logger.getLogger("Minecraft");
	public static NowPlaying plugin;
	public NowPlaying np;
	public static AliasList aliasList;
	private ArrayList<String> aTextOutput = new ArrayList<String>();
	public static String sMaxString = "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"; // len=44

	public cPlayerListener(NowPlaying instance) {
		plugin = instance;
		this.np = plugin;
	}

	public static void setAliasList(AliasList aList) {
		aliasList = aList;
	}

	@Override
	public void onPlayerCommand(PlayerChatEvent event) {
		String[] sCommand = event.getMessage().split(" ");

		Player player = event.getPlayer();
		// World world = player.getWorld();
		cChat.save(player);

		if ((!event.isCancelled()) && sCommand[0].equalsIgnoreCase("/pos")) {
			if (sCommand.length == 1) {
				Location location = player.getLocation();
				player.sendMessage("You are currently at " + location.getX()
						+ "," + location.getY() + "," + location.getZ()
						+ " with " + location.getYaw() + " yaw and "
						+ location.getPitch() + " pitch");
			} else if (sCommand.length == 4) {
				try {
					double x = Double.parseDouble(sCommand[1]);
					double y = Double.parseDouble(sCommand[2]);
					double z = Double.parseDouble(sCommand[3]);
					player.teleportTo(new Location(player.getWorld(), x, y, z));
				} catch (NumberFormatException ex) {
					player.sendMessage("Given location is invalid");
				}
			} else {
				player.sendMessage("Usage: '/pos' to get current position, or '/pos x y z' to teleport to x,y,z");
			}
			event.setCancelled(true);
		}

		if (sCommand[0].equalsIgnoreCase("/help")) {
			String sCmd = ChatColor.WHITE + "/np (player)" + ChatColor.GOLD
					+ " - " + ChatColor.YELLOW
					+ "Shows what you/player is listening to";
			cChat.send(sCmd);

			sCmd = ChatColor.WHITE + "/npver" + ChatColor.GOLD + " - "
					+ ChatColor.YELLOW
					+ "Displays the NowPlaying plugin version";
			cChat.send(sCmd);

			sCmd = ChatColor.WHITE + "/npadd [lastFM name]" + ChatColor.GOLD
					+ " - " + ChatColor.YELLOW
					+ "Adds your lastFM alias to your nick";
			cChat.send(sCmd);

			sCmd = ChatColor.WHITE + "/npdel" + ChatColor.GOLD + " - "
					+ ChatColor.YELLOW + "Deletes your lastFM alias";
			cChat.send(sCmd);

			sCmd = ChatColor.WHITE + "/npcheck (player/alias)" + ChatColor.GOLD
					+ " - " + ChatColor.YELLOW
					+ "Check if entry exists in the db";
			cChat.send(sCmd);

			event.setCancelled(true);
		}

		if ((!event.isCancelled()) && sCommand[0].equalsIgnoreCase("/debug")) {
			plugin.setDebugging(player, !plugin.isDebugging(player));
			event.setCancelled(true);
		}

		if ((!event.isCancelled()) && sCommand[0].equalsIgnoreCase("/npver")) {
			cChat.send(ChatColor.GOLD + this.np.getVersion());
			event.setCancelled(true);
		}

		if ((!event.isCancelled()) && sCommand[0].equalsIgnoreCase("/npsize")) {
			int iAliasSize = aliasList.getSize();
			cChat.send(ChatColor.AQUA + "Alias records in db: " + iAliasSize);
			event.setCancelled(true);
		}

		if ((!event.isCancelled()) && sCommand[0].equalsIgnoreCase("/npadd")) {
			if (sCommand.length > 1) {
				String sLongName = "";
				int x = 0;

				for (String s : sCommand) {
					if (x > 0)
						sLongName += s + " ";

					x++;
				}
				aliasList.addAlias(player, sLongName.trim());
			}
			event.setCancelled(true);
		}

		if ((!event.isCancelled()) && sCommand[0].equalsIgnoreCase("/npdel")) {
			aliasList.delAlias(player);
			event.setCancelled(true);
		}

		if ((!event.isCancelled()) && sCommand[0].equalsIgnoreCase("/npcheck")) {
			if (sCommand.length == 1) {
				Map<String, String> m = AliasList.lastfmList;
				Iterator<Map.Entry<String, String>> it = m.entrySet()
						.iterator();

				while (it.hasNext()) {
					Map.Entry<String, String> pairs = it.next();

					log.info(pairs.getKey() + ":" + pairs.getValue());
				}

			} else if (sCommand.length == 2) {
				String sFound = findValueInStack(sCommand[1],
						AliasList.lastfmList);

				if (sFound != null) {
					cChat.send(ChatColor.AQUA
							+ "LastFM alias exists in the db...");
				}

				if (AliasList.lastfmList.containsKey(sCommand[1])) {
					cChat.send(ChatColor.AQUA + "Player exists in the db...");
				}
			}
			event.setCancelled(true);
		}

		if ((!event.isCancelled()) && sCommand[0].equalsIgnoreCase("/np")) {
			String key = cControl.getApiKey(NowPlaying.watching[0]);

			if (key == NowPlaying.defaults[0]) {
				cChat.send(ChatColor.RED
						+ "The server admin has not set the APIKEY for this plugin to work.");
				event.setCancelled(true);
			}

			if (sCommand.length == 1) {
				String sFound = findKeyInStack(player.getName(),
						AliasList.lastfmList);

				if (sFound == null) {
					sFound = player.getName();
				}

				aTextOutput = getRecentTrack(key, sFound, player.getName());

				for (String s : this.aTextOutput) {
					cChat.send(s);
				}
			} else if (sCommand.length == 2) {
				String sFound = findKeyInStack(sCommand[1],
						AliasList.lastfmList);

				if (sFound == null) {
					sFound = sCommand[1];
				}

				aTextOutput = getRecentTrack(key, sFound, sCommand[1]);

				for (String s : this.aTextOutput) {
					cChat.send(s);
				}
			}
			event.setCancelled(true);
		}
	}

	@Override
	public void onPlayerJoin(PlayerEvent event) {
		Player player = event.getPlayer();

		String location = (int) player.getLocation().getX() + "x, "
				+ (int) player.getLocation().getY() + "y, "
				+ (int) player.getLocation().getZ() + "z";
		String ip = player.getAddress().getAddress().getHostAddress();

		log.info(player.getName() + " (" + ip + ") joined the server @ "
				+ location);
	}

	@Override
	public void onPlayerQuit(PlayerEvent event) {
		Player player = event.getPlayer();
		log.info(player.getName() + " left the server...");
	}

	public static ArrayList<String> getRecentTrack(String apiKey, String user,
			String alias) {
		String output = "";
		String preOutput = "";
		String currentInfo = "";
		String sDisplayName = "";
		ArrayList<String> aOutput = new ArrayList<String>();

		if (user.equalsIgnoreCase(alias)) {
			sDisplayName = ChatColor.DARK_AQUA + user;
		} else {
			sDisplayName = ChatColor.AQUA + alias;
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
			output = "last.fm api did not work: " + ex.getMessage();
			log.warning(output);
		}

		return aOutput;
	}

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
