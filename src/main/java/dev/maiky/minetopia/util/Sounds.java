package dev.maiky.minetopia.util;

import me.lucko.helper.Schedulers;
import me.lucko.helper.scheduler.Task;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * Door: Maiky
 * Info: Minetopia - 24 May 2021
 *
 * Package: dev.maiky.minetopia.util
 */

public class Sounds {

	public static void playSound(Player player, Sound sound, float pitch) {
		player.playSound(player.getLocation(), sound, 0.1f, pitch);
	}

	public static void playSoundRepeating(Player player, Sound sound, float pitch, int ticks, int repeats) {
		Task task = Schedulers.sync().runRepeating(() -> {
			player.playSound(player.getLocation(), sound, 0.1f, pitch);
		}, 0, ticks);
		Schedulers.sync().runLater(task::close, (long) repeats * ticks);
	}

	public static void playSound(Location location, Sound sound, float volume, float pitch) {
		location.getWorld().playSound(location, sound, volume, pitch);
	}

}
