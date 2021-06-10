package dev.maiky.minetopia.modules.upgrades.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUpgrades;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.modules.upgrades.ui.UpgradeUI;
import dev.maiky.minetopia.util.Text;
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

	@Default
	@Conditions("MTUser")
	@Subcommand("main")
	@Description("Open the upgrades menu")
	public void onUpgrade(Player player) {
		UpgradeUI upgradeUI = new UpgradeUI(player);
		upgradeUI.open();
	}

	@CatchUnknown
	public void onUnknown(CommandSender sender) {
		sender.sendMessage("§cUnknown subcommand");
		this.onHelp(sender);
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

		PlayerManager playerManager = PlayerManager.with(DataModule.getInstance().getSqlHelper());
		MinetopiaUser user = offlinePlayer.isOnline() ? PlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.retrieve(offlinePlayer.getUniqueId());
		MinetopiaUpgrades upgrades = user.getMinetopiaUpgrades();
		String message = "§6The player §c%s §6has §c%s §6upgrades.";
		sender.sendMessage(String.format(Text.colors(message), offlinePlayer.getName(), upgrades.getPoints()));
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

		PlayerManager playerManager = PlayerManager.with(DataModule.getInstance().getSqlHelper());
		MinetopiaUser user = offlinePlayer.isOnline() ? PlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.retrieve(offlinePlayer.getUniqueId());
		MinetopiaUpgrades upgrades = user.getMinetopiaUpgrades();
		upgrades.setPoints(upgrades.getPoints() + amount);
		if (!offlinePlayer.isOnline())
			playerManager.update(user);

		String message = "§6Success! Upgrades of §c%s §6were increased by §c%s§6 their balance is now §c%s&6.";
		sender.sendMessage(String.format(Text.colors(message), offlinePlayer.getName(), amount, upgrades.getPoints()));
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

		PlayerManager playerManager = PlayerManager.with(DataModule.getInstance().getSqlHelper());
		MinetopiaUser user = offlinePlayer.isOnline() ? PlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.retrieve(offlinePlayer.getUniqueId());
		MinetopiaUpgrades upgrades = user.getMinetopiaUpgrades();
		upgrades.setPoints(upgrades.getPoints() - amount);
		if (!offlinePlayer.isOnline())
			playerManager.update(user);

		String message = "§6Success! Upgrades of §c%s §6were decreased by §c%s§6 their balance is now §c%s&6.";
		sender.sendMessage(String.format(Text.colors(message), offlinePlayer.getName(), amount, upgrades.getPoints()));
	}

	@Override
	public void showSyntax(CommandIssuer issuer, RegisteredCommand<?> cmd) {
		issuer.sendMessage("§cGebruik: /" + this.getExecCommandLabel() + " " +
				cmd.getPrefSubCommand() + " " +
				cmd.getSyntaxText());
	}

	@HelpCommand
	public void onHelp(CommandSender sender) {
		Minetopia.showHelp(sender, this, getSubCommands());
	}

}
