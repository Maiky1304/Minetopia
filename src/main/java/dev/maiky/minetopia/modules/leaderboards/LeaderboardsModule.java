package dev.maiky.minetopia.modules.leaderboards;

import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.leaderboards.tasks.DatabaseTask;
import me.lucko.helper.Schedulers;
import me.lucko.helper.Services;
import me.lucko.helper.hologram.Hologram;
import me.lucko.helper.hologram.HologramFactory;
import me.lucko.helper.npc.Npc;
import me.lucko.helper.npc.NpcFactory;
import me.lucko.helper.serialize.Position;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;

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

	private NpcFactory factory;

	@Override
	public void enable() {
		this.enabled = true;

		// Register Factory
		this.factory = Services.load(NpcFactory.class);
		this.factory.bindWith(terminable);

		// NPC
		final HashMap<Integer, String> unicode = new HashMap<>();
		unicode.put(1, "❶");
		unicode.put(2, "❷");
		unicode.put(3, "❸");
		final HashMap<Integer, Location> locations = new HashMap<>();
		final World w = Bukkit.getWorlds().get(0);
		locations.put(1, new Location(w, -688.5, 53, 741.5, 0f, 0f));
		locations.put(2, new Location(w, -687.5, 52.5, 742.5, 0f, 0f));
		locations.put(3, new Location(w, -689.5, 52.0625, 742.5, 0f, 0f));

		final HashMap<Integer, Npc> cache = new HashMap<>();
		final HashMap<Integer, Hologram> cacheH = new HashMap<>();

		// Register Tasks
		DatabaseTask task = new DatabaseTask(DataModule.getInstance().getSqlHelper());
		if ( Minetopia.getPlugin(Minetopia.class).getConfiguration().get().getBoolean("leaderboard-server"))
			Schedulers.async().runRepeating(task, 0, 1800);
		Schedulers.sync().runRepeating(task2 -> {
			final LinkedHashMap<UUID, Integer> top3 = task.top3();

			int i = 1;
			for (UUID uuid : top3.keySet()) {

				String name = Bukkit.getOfflinePlayer(uuid).getName();

				if (cache.containsKey(i)) {
					Npc npc = cache.get(i);
					npc.setSkin(name);

					Hologram hologram = cacheH.get(i);
					hologram.updateLines(Collections.singletonList("§a" + unicode.get(i) + " §8- §7" + name));
				} else {
					final Npc npc = this.factory.spawnNpc(locations.get(i), "", name);
					npc.setShowNametag(false);

					final HologramFactory hologramFactory = Services.load(HologramFactory.class);
					List<String> lines = Collections.singletonList("§a" + unicode.get(i) + " §8- §7" + name);
					final Hologram hologram = hologramFactory.newHologram(Position.of(npc.getInitialSpawn().clone().add(0, 1.75, 0)), lines);
					hologram.bindWith(terminable);
					hologram.spawn();

					cache.put(i, npc);
					cacheH.put(i, hologram);
				}

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
