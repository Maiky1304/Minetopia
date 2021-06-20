package dev.maiky.minetopia.modules.boosters.booster.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.boosters.booster.enums.BoosterType;
import dev.maiky.minetopia.modules.boosters.booster.manager.SystemBoosterManager;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
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
	public void onHelp(CommandSender sender) {
		Minetopia.showHelp(sender, this, getSubCommands());
	}

	@CatchUnknown
	public void onUnknown(CommandSender sender) {
		sender.sendMessage("§cUnknown subcommand");
		this.onHelp(sender);
	}

	@Override
	public void showSyntax(CommandIssuer issuer, RegisteredCommand<?> cmd) {
		issuer.sendMessage("§cGebruik: /" + this.getExecCommandLabel() + " " +
				cmd.getPrefSubCommand() + " " +
				cmd.getSyntaxText());
	}

	@Subcommand("add")
	@Syntax("<player> <type> <percentage>")
	@Description("Add a booster to a player")
	@CommandPermission("minetopia.moderation.add")
	@CommandCompletion("@players @boosterTypes")
	public void onAdd(Player player, @Conditions("database") String target , BoosterType type, @Conditions("limits:min=1,max=9999999") int percentage) {
		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		MinetopiaUser user = PlayerManager.getCache().get(offlinePlayer.getUniqueId());
		if (type == BoosterType.GRAYSHARD )
			user.setGrayshardBoost(user.getGrayshardBoost() + percentage);
		else user.setGoldshardBoost(user.getGoldshardBoost() + percentage);

		if (!offlinePlayer.isOnline())
			PlayerManager.with(DataModule.getInstance().getSqlHelper()).update(user);

		player.sendMessage("§6Succesvol de booster soort §c" + type.toString().toLowerCase() + " §6toegevoegd aan §c"
		+ offlinePlayer.getName() + "§6 van §c" + percentage + "%§6.");
	}

	@Subcommand("remove")
	@Syntax("<player> <type> <percentage>")
	@Description("Remove a booster to a player")
	@CommandPermission("minetopia.moderation.remove")
	@CommandCompletion("@players @boosterTypes")
	public void onRemove(Player player, @Conditions("database") String target , BoosterType type, @Conditions("limits:min=1,max=9999999") int percentage) {
		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		MinetopiaUser user = PlayerManager.getCache().get(offlinePlayer.getUniqueId());
		if (type == BoosterType.GRAYSHARD )
			user.setGrayshardBoost(user.getGrayshardBoost() - percentage);
		else user.setGoldshardBoost(user.getGoldshardBoost() - percentage);

		if (!offlinePlayer.isOnline())
			PlayerManager.with(DataModule.getInstance().getSqlHelper()).update(user);

		player.sendMessage("§6Succesvol de booster soort §c" + type.toString().toLowerCase() + " §6verwijderd van §c"
				+ offlinePlayer.getName() + "§6 van §c" + percentage + "%§6.");
	}



	@Subcommand("info")
	@Syntax("<type> [speler]")
	@Description("Bekijk een speler's beschikbare boostpercentage")
	@CommandPermission("minetopia.common.boost")
	@CommandCompletion("@boosterTypes @players")
	public void onInfo(Player player, BoosterType type, @Default("self") @Conditions("database") String target) {
		OfflinePlayer offlinePlayer;
		if (target.equals("self")) {
			offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
		} else {
			if (!player.hasPermission("minetopia.moderation.boost")) {
				offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
			} else {
				if (target.length() == 32)
					offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
				else offlinePlayer = Bukkit.getOfflinePlayer(target);
			}
		}

		MinetopiaUser user = PlayerManager.getCache().get(offlinePlayer.getUniqueId());
		int total = type == BoosterType.GRAYSHARD ? user.getGrayshardBoost() : user.getGoldshardBoost();

		player.sendMessage("§6De speler §c" + offlinePlayer.getName() + " §6heeft in totaal §c" + total + "% §6" + type.toString().toLowerCase() + "boost.");
	}

	@Subcommand("activate")
	@Syntax("<type> <percentage>")
	@Description("Activeer een booster")
	@CommandPermission("minetopia.common.booster.activate")
	@CommandCompletion("@boosterTypes")
	public void onActivate(Player player, BoosterType type, int percentage) {
		if (percentage <= 0) throw new ConditionFailedException("Gebruik een waarde van minimaal 1.");

		MinetopiaUser user = PlayerManager.getCache().get(player.getUniqueId());

		if (type == BoosterType.GRAYSHARD) {
			if (user.getGrayshardBoost() < percentage) throw new ConditionFailedException("Je hebt geen genoeg Grayshardboost hiervoor.");
			user.setGrayshardBoost(user.getGrayshardBoost() - percentage);
		} else {
			if (user.getGoldshardBoost() < percentage) throw new ConditionFailedException("Je hebt geen genoeg Goldshardboost hiervoor.");
			user.setGoldshardBoost(user.getGoldshardBoost() - percentage);
		}

		player.sendMessage("§6Je hebt een " + type.toString().toLowerCase() + "booster geactiveerd van §c" + percentage + "%§6.");

		SystemBoosterManager.update(player.getName(), type, SystemBoosterManager.get(type) + percentage);
		Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage("§6Er is een " + type.toString().toLowerCase() + "booster geactiveerd door §c" + player.getName() + " §6van" +
				" §c" + percentage + "%§6."));
	}

}
