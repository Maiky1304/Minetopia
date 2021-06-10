package dev.maiky.minetopia.modules.players.placeholders;

import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.players.PlayersModule;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Door: Maiky
 * Info: Minetopia - 06 Jun 2021
 * Package: dev.maiky.minetopia.modules.players.placeholders
 */

public class NameColorPlaceholder extends PlaceholderExpansion {

	@Override
	public @NotNull String getIdentifier() {
		return "minetopia";
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
		if (!params.equals("namecolor")) return null;
		if (!PlayerManager.getCache().containsKey(player.getUniqueId())) return "§7";

		MinetopiaUser user = PlayerManager.getCache().get(player.getUniqueId());
		return "§" + user.getCityColor();
	}
}
