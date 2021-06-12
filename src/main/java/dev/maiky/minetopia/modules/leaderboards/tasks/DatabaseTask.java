package dev.maiky.minetopia.modules.leaderboards.tasks;

import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUpgrades;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.modules.upgrades.upgrades.Upgrade;
import me.lucko.helper.sql.Sql;

import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

/**
 * Door: Maiky
 * Info: Minetopia - 11 Jun 2021
 * Package: dev.maiky.minetopia.modules.leaderboards.tasks
 */

public class DatabaseTask implements Runnable {

	private final Sql sql;
	private final PlayerManager manager;

	public DatabaseTask(Sql sql) {
		this.sql = sql;
		this.manager = PlayerManager.with(DataModule.getInstance().getSqlHelper());
	}

	@Override
	public void run() {
		List<MinetopiaUser> userList = manager.allUsers();
		System.out.println(userList);
		for (MinetopiaUser user : userList) {
			if (!exists(user.getUuid()))
				insert(user.getUuid(), user.getLevelPoints());
			else update(user.getUuid(), user.getLevelPoints());
		}
	}

	public LinkedHashMap<UUID, Integer> top3() {
		LinkedHashMap<UUID, Integer> linkedHashMap = new LinkedHashMap<>();
		return this.sql.query("SELECT * FROM `leaderboards` ORDER BY `leaderboards`.`points` DESC LIMIT 3",
				preparedStatement -> {},
				resultSet ->
				{
					while(resultSet.next()) {
						linkedHashMap.put(UUID.fromString(resultSet.getString("uuid")), resultSet.getInt("points"));
					}

					return linkedHashMap;
				}).orElse(linkedHashMap);
	}

	private void update(UUID uuid, int points) {
		this.sql.execute("UPDATE `leaderboards` SET `points`=? WHERE `uuid`=?",
				preparedStatement ->
				{
					preparedStatement.setInt(1, points);
					preparedStatement.setString(2, uuid.toString());
				});
	}

	private void insert(UUID uuid, int points) {
		this.sql.execute("INSERT INTO `leaderboards`(`uuid`, `points`) VALUES(?,?)",
				preparedStatement -> {
			preparedStatement.setString(1, uuid.toString());
			preparedStatement.setInt(2, points);
				});
	}

	private boolean exists(UUID uuid) {
		return this.sql.query("SELECT `uuid` FROM `leaderboards` WHERE `uuid`=?",
				preparedStatement -> preparedStatement.setString(1, uuid.toString()),
				ResultSet::next).orElse(false);
	}

	public Sql getSql() {
		return sql;
	}

}
