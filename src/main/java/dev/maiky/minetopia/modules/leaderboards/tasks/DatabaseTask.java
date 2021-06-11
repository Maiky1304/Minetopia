package dev.maiky.minetopia.modules.leaderboards.tasks;

import me.lucko.helper.sql.Sql;

/**
 * Door: Maiky
 * Info: Minetopia - 11 Jun 2021
 * Package: dev.maiky.minetopia.modules.leaderboards.tasks
 */

public class DatabaseTask implements Runnable {

	private final Sql sql;

	public DatabaseTask(Sql sql) {
		this.sql = sql;
	}

	@Override
	public void run() {

	}

	public Sql getSql() {
		return sql;
	}

}
