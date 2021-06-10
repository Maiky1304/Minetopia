package dev.maiky.minetopia.modules.data.managers;

import com.google.gson.Gson;
import dev.maiky.minetopia.modules.transportation.portal.PortalData;
import me.lucko.helper.sql.Sql;

import java.util.HashMap;

/**
 * Door: Maiky
 * Info: Minetopia - 25 May 2021
 * Package: dev.maiky.minetopia.modules.data.managers
 */

public class PortalManager {

	private final Sql sql;
	private final Gson gson;

	public static PortalManager with(Sql sql) {
		return new PortalManager(sql);
	}

	private PortalManager(Sql sql) {
		this.sql = sql;
		this.gson = new Gson();
	}

	public HashMap<String, PortalData> getPortals() {
		HashMap<String, PortalData> portalDataHashMap = new HashMap<>();

		return sql.query("SELECT `name`, `json` FROM `portals` WHERE 1",
				preparedStatement -> {},
				resultSet ->
				{
					while(resultSet.next())
						portalDataHashMap.put(resultSet.getString("name"),
								gson.fromJson(resultSet.getString("json"), PortalData.class));
					return portalDataHashMap;
				}).orElse(portalDataHashMap);
	}

	public void deletePortal(String name) {
		this.sql.execute("DELETE FROM `portals` WHERE `name`=?",
				preparedStatement ->
				{
					preparedStatement.setString(1, name);
				});
	}

	public void insertPortal(String name, PortalData data) {
		this.sql.execute("INSERT INTO `portals`(`name`, `json`) VALUES(?, ?)",
				preparedStatement ->
				{
					preparedStatement.setString(1, name);
					preparedStatement.setString(2, gson.toJson(data));
				});
	}

	public void updatePortal(String name, PortalData data) {
		this.sql.execute("UPDATE `portals` SET `json`=? WHERE `name`=?",
				preparedStatement ->
				{
					preparedStatement.setString(1, gson.toJson(data));
					preparedStatement.setString(2, name);
				});
	}

	public PortalData getPortalData(String name) {
		return this.sql.query("SELECT `json` FROM `portals` WHERE `name`=?",
				preparedStatement -> {
					preparedStatement.setString(1, name);
				}, resultSet ->
				{
					if (resultSet.next()) {
						return gson.fromJson(resultSet.getString("json"), PortalData.class);
					}
					return null;
				}).orElse(null);
	}

}
