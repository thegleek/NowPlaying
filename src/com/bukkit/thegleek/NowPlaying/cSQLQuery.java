package com.bukkit.thegleek.NowPlaying;

import java.util.Formatter;
import java.util.Locale;

public class cSQLQuery {
	protected final static String CreateTable = ""
			+ "CREATE TABLE `PlayerAlias` (" + "`id` INTEGER PRIMARY KEY,"
			+ "`name` varchar(32) NOT NULL DEFAULT 'Player',"
			+ "`lastfmname` varchar(64) NOT NULL DEFAULT 'Empty');";

	protected final static String AddAlias = "INSERT INTO PlayerAlias (id, name, lastfmname) VALUES (null,?,?)";

	protected final static String DelAlias = "DELETE FROM PlayerAlias WHERE name = ?";

	public static void FormatTesting() {
		StringBuilder sb = new StringBuilder();
		// Send all output to the Appendable object sb
		Formatter formatter = new Formatter(sb, Locale.US);

		// Explicit argument indices may be used to re-order output.
		formatter.format("%4$2s %3$2s %2$2s %1$2s", "a", "b", "c", "d");
		// -> " d  c  b  a"
	}
}
