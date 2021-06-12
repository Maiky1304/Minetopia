package dev.maiky.minetopia.modules.data.managers;

import com.google.gson.Gson;
import dev.maiky.minetopia.modules.players.classes.MinetopiaScoreboard;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import lombok.Getter;
import me.lucko.helper.sql.Sql;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Door: Maiky
 * Info: Minetopia - 21 May 2021
 * Package: dev.maiky.minetopia.modules.data.managers
 */

public class PlayerManager {

	@Getter
	private final static HashMap<UUID, MinetopiaUser> cache = new HashMap<>();
	@Getter
	private final static HashMap<UUID, MinetopiaScoreboard> scoreboard = new HashMap<>();

	private final Sql sql;
	private final Gson gson;

	public static PlayerManager with(Sql sql) {
		return new PlayerManager(sql);
	}

	private PlayerManager(Sql sql) {
		this.sql = sql;
		this.gson = new Gson();
	}

	public void update(MinetopiaUser user) {
		final String json = gson.toJson(user);
		final String statement = "UPDATE `users` SET `json`=? WHERE `uuid`=?";

		this.sql.execute(statement, preparedStatement -> {
			preparedStatement.setString(1, json);
			preparedStatement.setString(2, user.getUuid().toString());
		});
	}

	public void delete(MinetopiaUser user) {
		final String statement = "DELETE FROM `users` WHERE `uuid`=?";

		this.sql.execute(statement, preparedStatement -> {
			preparedStatement.setString(1, user.getUuid().toString());
		});
	}

	public MinetopiaUser retrieve(UUID uuid) {
		final String statement = "SELECT `json` FROM `users` WHERE `uuid`=?";

		return this.sql.query(statement, preparedStatement -> {
			preparedStatement.setString(1, uuid.toString());
		}, resultSet -> {
			if (resultSet.next()) {
				String json = resultSet.getString("json");
				return gson.fromJson(json, MinetopiaUser.class);
			}
			return null;
		}).orElse(null);
	}

	public void create(MinetopiaUser user) {
		final String jsonUser = this.toJson(user);
		final String statement = "INSERT INTO `users`(`uuid`, `json`) VALUES(?,?);";

		this.sql.execute(statement, preparedStatement -> {
			preparedStatement.setString(1, user.getUuid().toString());
			preparedStatement.setString(2, jsonUser);
		});
	}

	public boolean exists(UUID uuid) {
		return this.sql.query("SELECT `uuid` FROM `users` WHERE `uuid`=?",
				preparedStatement -> preparedStatement.setString(1, uuid.toString()),
				ResultSet::next).orElse(false);
	}

	public List<MinetopiaUser> allUsers() {
		List<MinetopiaUser> users = new ArrayList<>();
		return this.sql.query("SELECT `uuid` FROM `users` WHERE 1", preparedStatement -> {},
				resultSet ->
				{
					while(resultSet.next()) {
						users.add(retrieve(UUID.fromString(resultSet.getString("uuid"))));
					}

					return users;
				}).orElse(users);
	}

	public String toJson(Object object) {
		return this.gson.toJson(object);
	}

	public Sql getSql() {
		return sql;
	}
}
