package dev.maiky.minetopia.modules.levels.manager;

import dev.maiky.minetopia.modules.levels.plots.IPlot;
import me.lucko.helper.sql.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Door: Maiky
 * Info: Minetopia - 24 May 2021
 * Package: dev.maiky.minetopia.modules.levels.manager
 */

public class PlotManager {

	private final Sql sql;

	public static PlotManager with(Sql sql) {
		return new PlotManager(sql);
	}

	private PlotManager(Sql sql) {
		this.sql = sql;
	}

	public int getUserId(UUID uuid) {
		return this.sql.query("SELECT `id` FROM `worldguard_user` WHERE `uuid`=?",
				preparedStatement -> {
					preparedStatement.setString(1, uuid.toString());
				}, resultSet ->
				{
					if (resultSet.next()) {
						return resultSet.getInt("id");
					}
					return -1;
				}).orElse(-1);
	}

	public List<IPlot> getOwnedPlots(UUID uuid) {
		int userId = this.getUserId(uuid);
		List<IPlot> plots = new ArrayList<>();
		if (userId == -1) return plots;

		return this.sql.query("SELECT `region_id` FROM `worldguard_region_players` WHERE `user_id`=? AND `owner`=?",
				preparedStatement -> {
					preparedStatement.setInt(1, userId);
					preparedStatement.setInt(2, 1);
				}, resultSet -> {
					while (resultSet.next()) {
						plots.add(new IPlot(resultSet.getString("region_id")));
					}
					return plots;
				}).orElse(plots);
	}

}
