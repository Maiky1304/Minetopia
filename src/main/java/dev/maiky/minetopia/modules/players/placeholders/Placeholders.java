package dev.maiky.minetopia.modules.players.placeholders;

import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaTime;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * Door: Maiky
 * Info: Minetopia - 06 Jun 2021
 * Package: dev.maiky.minetopia.modules.players.placeholders
 */

public class Placeholders extends PlaceholderExpansion {

	@Override
	public @NotNull String getIdentifier() {
		return "minetopiacore";
	}

	@Override
	public @NotNull String getAuthor() {
		return "Maiky Perlee";
	}

	@Override
	public @NotNull String getVersion() {
		return "1.0.0";
	}

	@Override
	public String onRequest(OfflinePlayer player, @NotNull String params) {
		if (!PlayerManager.getCache().containsKey(player.getUniqueId())) return "???";

		MinetopiaUser user = PlayerManager.getCache().get(player.getUniqueId());
		if (params.equals("naamkleur")) {
			return "§" + user.getCityColor();
		} else if (params.equals("level")) {
			return String.valueOf(user.getLevel());
		} else if (params.equals("prefix")) {
			return String.valueOf(user.getCurrentPrefix());
		} else if (params.startsWith("time_")) {
			MinetopiaTime time = user.getTime();
			String type = params.split("_")[1];
			switch (type) {
				case "seconds":
					return String.valueOf(time.getSeconds());
				case "minutes":
					return String.valueOf(time.getMinutes());
				case "hours":
					return String.valueOf(time.getHours());
				case "days":
					return String.valueOf(time.getDays());
				default:
					return null;
			}
		}

		return null;
	}
}
