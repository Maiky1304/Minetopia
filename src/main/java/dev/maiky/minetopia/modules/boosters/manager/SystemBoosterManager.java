package dev.maiky.minetopia.modules.boosters.manager;

import com.google.gson.Gson;
import dev.maiky.minetopia.modules.boosters.enums.BoosterType;
import me.lucko.helper.sql.Sql;

import java.sql.ResultSet;

/**
 * Door: Maiky
 * Info: Minetopia - 17 Jun 2021
 * Package: dev.maiky.minetopia.modules.boosters.booster.manager
 */

public class SystemBoosterManager {

	private final Sql sql;
	private final Gson gson;

	public static SystemBoosterManager with(Sql sql) {
		return new SystemBoosterManager(sql);
	}

	private SystemBoosterManager(Sql sql) {
		this.sql = sql;
		this.gson = new Gson();
	}

	public void update(String owner, BoosterType type, int percentage) {
		if (exists(type)) {
			this.sql.execute("UPDATE `boosters` SET `last`=?,`expiry`=?,`percentage`=? WHERE `type`=?",
					preparedStatement ->
					{
						preparedStatement.setString(1, owner);
						preparedStatement.setLong(2, System.currentTimeMillis() + 3600000L);
						preparedStatement.setInt(3, percentage);
						preparedStatement.setString(4, type.toString());
					});
		} else {
			this.sql.execute("INSERT INTO `boosters`(`type`,`percentage`,`expiry`,`last`) VALUES(?,?,?,?)",
					preparedStatement ->
					{
						preparedStatement.setString(1, type.toString());
						preparedStatement.setInt(2, percentage);
						preparedStatement.setLong(3, System.currentTimeMillis() + 3600000L);
						preparedStatement.setString(4, owner);
					});
		}
	}

	public int get(BoosterType type) {
		if (!exists(type)) return 0;
		return this.sql.query("SELECT `percentage` FROM `boosters` WHERE `type`=?",
				preparedStatement -> preparedStatement.setString(1, type.toString()),
				resultSet ->
				{
					if (resultSet.next()) {
						return resultSet.getInt(1);
					}
					return 0;
				}).orElse(0);
	}

	public long getExpiry(BoosterType type) {
		if (!exists(type)) return 0L;
		return this.sql.query("SELECT `expiry` FROM `boosters` WHERE `type`=?",
				preparedStatement -> preparedStatement.setString(1, type.toString()),
				resultSet ->
				{
					if (resultSet.next()) {
						return resultSet.getLong(1);
					}

					return 0L;
				}).orElse(0L);
	}

	public String getLastUser(BoosterType type) {
		if (!exists(type)) return null;
		return this.sql.query("SELECT `last` FROM `boosters` WHERE `type`=?",
				preparedStatement -> preparedStatement.setString(1, type.toString()),
				resultSet ->
				{
					if (resultSet.next()) {
						return resultSet.getString(1);
					}

					return null;
				}).orElse(null);
	}

	private boolean exists(BoosterType type) {
		return sql.query("SELECT `type` FROM `boosters` WHERE `type`=?",
				preparedStatement -> preparedStatement.setString(1, type.toString()), ResultSet::next).orElse(false);
	}

}
