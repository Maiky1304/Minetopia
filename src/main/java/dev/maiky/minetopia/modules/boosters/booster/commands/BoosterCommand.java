package dev.maiky.minetopia.modules.boosters.booster.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.boosters.booster.enums.BoosterType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Door: Maiky
 * Info: Minetopia - 07 Jun 2021
 * Package: dev.maiky.minetopia.modules.boosters.booster.commands
 */

@CommandAlias("booster")
@CommandPermission("minetopia.common.booster")
@Description("Command for the boosters")
public class BoosterCommand extends BaseCommand {

	@Default
	@HelpCommand
	@Subcommand("main")
	@Description("See all the commands in the plugin")
	public void main(Player player) {
		Minetopia.showHelp(player, this, getSubCommands());
	}

	@Subcommand("add")
	@Syntax("<player> <type> <percentage>")
	@Description("Add a booster to a player")
	@CommandPermission("minetopia.moderation.add")
	@CommandCompletion("@players @boosterTypes")
	public void onAdd(Player player, @Conditions("database") String target, BoosterType type, @Conditions("limits:min=1,max=9999999") int percentage) {
		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);


	}

}
