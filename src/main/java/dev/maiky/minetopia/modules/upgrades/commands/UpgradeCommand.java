package dev.maiky.minetopia.modules.upgrades.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.mongo.MongoPlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUpgrades;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.modules.upgrades.ui.UpgradeUI;
import dev.maiky.minetopia.modules.upgrades.upgrades.Upgrade;
import dev.maiky.minetopia.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Door: Maiky
 * Info: Minetopia - 23 May 2021
 * Package: dev.maiky.minetopia.modules.upgrades.commands
 */

@CommandAlias("upgrade|upgrades")
@CommandPermission("minetopia.common.upgrades")
public class UpgradeCommand extends BaseCommand {

	private final MongoPlayerManager playerManager = DataModule.getInstance().getPlayerManager();

	@HelpCommand
	public void onHelp(CommandSender sender) {
		Minetopia.showHelp(sender, this, getSubCommands());
	}

	@CatchUnknown
	public void onUnknown(CommandSender sender) {
		sender.sendMessage(Message.COMMON_COMMAND_UNKNOWNSUBCOMMAND.raw());
		this.onHelp(sender);
	}

	@Override
	public void showSyntax(CommandIssuer issuer, RegisteredCommand<?> cmd) {
		issuer.sendMessage(Message.COMMON_COMMAND_SYNTAX.format(getExecCommandLabel(), cmd.getPrefSubCommand(), cmd.getSyntaxText()));
	}

	@Default
	@Conditions("MTUser")
	@Subcommand("main")
	@Description("Open the upgrades menu")
	public void onUpgrade(Player player) {
		UpgradeUI upgradeUI = new UpgradeUI(player);
		upgradeUI.open();
	}

	@Subcommand("info")
	@Syntax("<player>")
	@Description("View a specific player's upgrade information")
	@CommandPermission("minetopia.admin.upgrades")
	@CommandCompletion("@players")
	public void onInfo(CommandSender sender, @Conditions("database") String target) {
		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		
		MinetopiaUser user = offlinePlayer.isOnline() ? MongoPlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.find(u -> u.getUuid().equals(offlinePlayer.getUniqueId())).findFirst().orElse(null);
		MinetopiaUpgrades upgrades = user.getMinetopiaUpgrades();

		sender.sendMessage(Message.UPGRADES_INFO.format(offlinePlayer.getName(), upgrades.getPoints()));
	}

	@Subcommand("add")
	@Syntax("<player> <amount>")
	@Description("Add a specific amount of upgrades to a player")
	@CommandPermission("minetopia.admin.upgrades")
	@CommandCompletion("@players")
	public void onAdd(CommandSender sender, @Conditions("database") String target, int amount) {
		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		
		MinetopiaUser user = offlinePlayer.isOnline() ? MongoPlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.find(u -> u.getUuid().equals(offlinePlayer.getUniqueId())).findFirst().orElse(null);
		MinetopiaUpgrades upgrades = user.getMinetopiaUpgrades();
		upgrades.setPoints(upgrades.getPoints() + amount);
		if (!offlinePlayer.isOnline())
			playerManager.save(user);

		sender.sendMessage(Message.UPGRADES_SUCCESS_ADD.format(amount, offlinePlayer.getName(), upgrades.getPoints()));
	}

	@Subcommand("remove")
	@Syntax("<player> <amount>")
	@Description("Remove a specific amount of upgrades from a player")
	@CommandPermission("minetopia.admin.upgrades")
	@CommandCompletion("@players")
	public void onRemove(CommandSender sender, @Conditions("database") String target, int amount) {
		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		
		MinetopiaUser user = offlinePlayer.isOnline() ? MongoPlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.find(u -> u.getUuid().equals(offlinePlayer.getUniqueId())).findFirst().orElse(null);
		MinetopiaUpgrades upgrades = user.getMinetopiaUpgrades();
		upgrades.setPoints(upgrades.getPoints() - amount);
		if (!offlinePlayer.isOnline())
			playerManager.save(user);

		sender.sendMessage(Message.UPGRADES_SUCCESS_REMOVE.format(amount, offlinePlayer.getName(), upgrades.getPoints()));
	}

	@Subcommand("list")
	@Syntax("<player>")
	@Description("Bekijk alle upgrades van een specifieke speler")
	@CommandPermission("minetopia.admin.upgrades")
	@CommandCompletion("@players")
	public void onList(CommandSender sender, @Conditions("database") String target) {
		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		
		MinetopiaUser user = offlinePlayer.isOnline() ? MongoPlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.find(u -> u.getUuid().equals(offlinePlayer.getUniqueId())).findFirst().orElse(null);
		assert user != null;
		MinetopiaUpgrades upgrades = user.getMinetopiaUpgrades();

		sender.sendMessage(Message.UPGRADES_LIST_DIVIDER.raw());
		for (Upgrade upgrade : upgrades.getUpgrades().keySet()) {
			int level = upgrades.getUpgrades().get(upgrade);
			sender.sendMessage(Message.UPGRADES_LIST_ENTRY.format(upgrade.getLabel(), level));
		}
		sender.sendMessage(Message.UPGRADES_LIST_DIVIDER.raw());
	}


}
