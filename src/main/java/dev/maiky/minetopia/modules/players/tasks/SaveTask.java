package dev.maiky.minetopia.modules.players.tasks;

import dev.maiky.minetopia.modules.data.managers.mongo.MongoPlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaScoreboard;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import me.lucko.helper.bucket.Bucket;
import me.lucko.helper.bucket.BucketPartition;
import org.bukkit.entity.Player;

/**
 * Door: Maiky
 * Info: Minetopia - 22 May 2021
 * Package: dev.maiky.minetopia.modules.players.tasks
 */

public class SaveTask implements Runnable {

	private final Bucket<Player> bucket;
	private final MongoPlayerManager manager;

	public SaveTask(Bucket<Player> bucket, MongoPlayerManager manager) {
		this.bucket = bucket;
		this.manager = manager;
	}

	@Override
	public void run() {
		BucketPartition<Player> part = bucket.asCycle().next();
		for (Player player : part) {
			MinetopiaUser user = MongoPlayerManager.getCache().get(player.getUniqueId());
			if (user == null) continue;
			manager.save(user);

			MinetopiaScoreboard scoreboard = MongoPlayerManager.getScoreboard().get(player.getUniqueId());
			scoreboard.update();
		}
	}

}
