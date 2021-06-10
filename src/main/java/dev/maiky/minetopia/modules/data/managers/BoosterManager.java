package dev.maiky.minetopia.modules.data.managers;

import com.google.gson.Gson;
import dev.maiky.minetopia.modules.boosters.booster.Booster;
import me.lucko.helper.sql.Sql;

import java.util.LinkedHashMap;

/**
 * Door: Maiky
 * Info: Minetopia - 07 Jun 2021
 * Package: dev.maiky.minetopia.modules.data.managers
 */

public class BoosterManager {

	private final Sql sql;
	private final Gson gson;

	public static BoosterManager with(Sql sql) {
		return new BoosterManager(sql);
	}

	private BoosterManager(Sql sql) {
		this.sql = sql;
		this.gson = new Gson();
	}

	public void insertBooster(Booster booster) {
		String json = this.gson.toJson(booster);

		this.sql.execute("INSERT INTO `boosters`(`json`) VALUES(?)",
				preparedStatement -> preparedStatement.setString(1, json));
	}

	public void removeBooster(int id) {
		this.sql.execute("DELETE FROM `boosters` WHERE `id`=?", preparedStatement -> preparedStatement.setInt(1, id));
	}

	public LinkedHashMap<Integer, Booster> all() {
		LinkedHashMap<Integer, Booster> linkedHashMap = new LinkedHashMap<>();

		return this.sql.query("SELECT * FROM `boosters` WHERE 1", preparedStatement -> {},
				resultSet ->
				{
					while(resultSet.next()) {
						String json = resultSet.getString("json");
						Booster booster = this.gson.fromJson(json, Booster.class);

						linkedHashMap.put(resultSet.getInt("id"), booster);
					}

					return linkedHashMap;
				}).orElse(linkedHashMap);
	}

}
