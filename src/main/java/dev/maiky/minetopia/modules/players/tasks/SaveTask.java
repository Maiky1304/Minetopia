package dev.maiky.minetopia.modules.players.tasks;

import dev.maiky.minetopia.modules.data.managers.PlayerManager;
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

	private Bucket<Player> bucket;
	private PlayerManager manager;

	public SaveTask(Bucket<Player> bucket, PlayerManager manager) {
		this.bucket = bucket;
		this.manager = manager;
	}

	@Override
	public void run() {
		BucketPartition<Player> part = bucket.asCycle().next();
		for (Player player : part) {
			MinetopiaUser user = PlayerManager.getCache().get(player.getUniqueId());
			manager.update(user);

			MinetopiaScoreboard scoreboard = PlayerManager.getScoreboard().get(player.getUniqueId());
			scoreboard.update();
		}
	}

}
