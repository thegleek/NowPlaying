package com.bukkit.thegleek.NowPlaying;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class cPlayerData {
	public static final Logger log = Logger.getLogger("Minecraft");
	public final static String DATABASE = "jdbc:sqlite:NowPlaying.db";

	public static void initialize() {
		if (!tableExists()) {
			createTable();
		}
	}

	public static HashMap<String, String> getAliases() {
		HashMap<String, String> ret = new HashMap<String, String>();
		Connection conn = null;
		Statement statement = null;
		ResultSet set = null;
		String msg = "";
		int iSize = 0;

		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(DATABASE);
			statement = conn.createStatement();
			set = statement.executeQuery("SELECT * FROM PlayerAlias");

			while (set.next()) {
				iSize++;
				int iIndex = set.getInt("id");
				String name = set.getString("name");
				String lastfmname = set.getString("lastfmname");
				iIndex++; // this incrementer doesn't do shit really for our
							// situation

				ret.put(name, lastfmname);
			}

			msg = "[NowPlaying]: " + iSize + " LastFM alias";

			if (iSize != 1)
				msg += "es";

			log.info(msg + " retrieved from NowPlaying.db");
		} catch (SQLException ex) {
			log.severe("[NowPlaying]: PlayerAlias Load Exception");
		} catch (ClassNotFoundException e) {
			log.severe("[NowPlaying]: Error loading org.sqlite.JDBC");
		} finally {
			try {
				if (statement != null)
					statement.close();
				if (set != null)
					set.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				log.severe("[NowPlaying]: Warp Load Exception (on close)");
			}
		}

		return ret;
	}

	private static boolean tableExists() {
		Connection conn = null;
		ResultSet rs = null;

		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(DATABASE);
			DatabaseMetaData dbm = conn.getMetaData();
			rs = dbm.getTables(null, null, "PlayerAlias", null);

			if (!rs.next())
				return false;

			return true;
		} catch (SQLException ex) {
			log.log(Level.SEVERE, "[NowPlaying]: Table Check Exception", ex);
			return false;
		} catch (ClassNotFoundException e) {
			log.severe("[NowPlaying]: Error loading org.sqlite.JDBC");
			return false;
		} finally {
			try {
				if (rs != null)
					rs.close();

				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				log.severe("[NowPlaying]: Table Check SQL Exception (on closing)");
			}
		}
	}

	private static void createTable() {
		Connection conn = null;
		Statement st = null;

		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(DATABASE);
			st = conn.createStatement();
			st.executeUpdate(cSQLQuery.CreateTable);
		} catch (SQLException e) {
			log.log(Level.SEVERE, "[NowPlaying]: Create Table Exception", e);
		} catch (ClassNotFoundException e) {
			log.severe("[NowPlaying]: Error loading org.sqlite.JDBC");
		} finally {
			try {
				if (conn != null)
					conn.close();
				if (st != null)
					st.close();
			} catch (SQLException e) {
				log.severe("[NowPlaying]: Could not create the table (on close)");
			}
		}
	}

	public static boolean addAlias(String sPlayer, String sAlias) {
		Connection conn = null;
		PreparedStatement ps = null;
		boolean bSuccess = false;

		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(DATABASE);

			ps = conn.prepareStatement(cSQLQuery.AddAlias);
			ps.setString(1, sPlayer);
			ps.setString(2, sAlias);
			ps.executeUpdate();
			bSuccess = true;
		} catch (SQLException ex) {
			log.log(Level.SEVERE, "[NowPlaying]: Alias Insert Exception", ex);
		} catch (ClassNotFoundException e) {
			log.severe("[NowPlaying]: Error loading org.sqlite.JDBC");
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				log.log(Level.SEVERE,
						"[NowPlaying]: Alias Insert Exception (on close)", ex);
				bSuccess = false;
			}
		}

		return bSuccess;
	}

	public static boolean delAlias(String sPlayer) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet set = null;
		boolean bSuccess = false;

		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(DATABASE);
			ps = conn.prepareStatement(cSQLQuery.DelAlias);
			ps.setString(1, sPlayer);
			ps.executeUpdate();
			bSuccess = true;
		} catch (SQLException ex) {
			log.log(Level.SEVERE, "[NowPlaying]: Alias Delete Exception", ex);
		} catch (ClassNotFoundException e) {
			log.severe("[NowPlaying]: Error loading org.sqlite.JDBC");
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (set != null) {
					set.close();
				}
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				log.log(Level.SEVERE,
						"[NowPlaying]: Alias Delete Exception (on close)", ex);
				bSuccess = false;
			}
		}

		return bSuccess;
	}
}
