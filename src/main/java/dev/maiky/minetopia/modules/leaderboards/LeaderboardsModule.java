package dev.maiky.minetopia.modules.leaderboards;

import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.leaderboards.tasks.DatabaseTask;
import me.lucko.helper.Schedulers;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.bukkit.Bukkit;

import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * Door: Maiky
 * Info: Minetopia - 11 Jun 2021
 * Package: dev.maiky.minetopia.modules.leaderboards
 */

public class LeaderboardsModule implements MinetopiaModule {

	private boolean enabled;
	private final CompositeTerminable terminable = CompositeTerminable.create();

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	public void reload() {
		this.disable();
		this.enable();
	}

	@Override
	public void enable() {
		this.enabled = true;

		int[] ids = {1,3,2};

		// Register Tasks
		DatabaseTask task = new DatabaseTask(DataModule.getInstance().getSqlHelper());
		if ( Minetopia.getPlugin(Minetopia.class).getConfiguration().get().getBoolean("leaderboard-server"))
			Schedulers.async().runRepeating(task, 0, 1800);
		Schedulers.sync().runRepeating(task2 -> {
			LinkedHashMap<UUID, Integer> top3 = task.top3();

			int i = 0;
			for (UUID uuid : top3.keySet()) {
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "npc select " + ids[i]);
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "npc rename " + (String.format(
						"&c%s &8- &7%s", (i + 1), Bukkit.getOfflinePlayer(uuid).getName()
				)));
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "npc skin " + Bukkit.getOfflinePlayer(uuid).getName());
				i++;
			}
		}, 0, 1800);
	}

	@Override
	public void disable() {
		this.enabled = false;

		try {
			this.terminable.close();
		} catch (CompositeClosingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return "Leaderboards";
	}
}
