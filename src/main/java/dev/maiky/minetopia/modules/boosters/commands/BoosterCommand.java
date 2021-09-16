package dev.maiky.minetopia.modules.boosters.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.boosters.enums.BoosterType;
import dev.maiky.minetopia.modules.boosters.manager.SystemBoosterManager;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.mongo.MongoPlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.util.Message;
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

	private final SystemBoosterManager systemBoosterManager;
	private final MongoPlayerManager manager = DataModule.getInstance().getPlayerManager();

	public BoosterCommand() {
		this.systemBoosterManager = SystemBoosterManager.with(DataModule.getInstance().getSqlHelper());
	}

	@Default
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

		MinetopiaUser user = MongoPlayerManager.getCache().get(offlinePlayer.getUniqueId());
		if (type == BoosterType.GRAYSHARD )
			user.setGrayshardBoost(user.getGrayshardBoost() + percentage);
		else user.setGoldshardBoost(user.getGoldshardBoost() + percentage);

		if (!offlinePlayer.isOnline())
			manager.save(user);

		player.sendMessage(Message.BOOSTERS_ADD.format(type.toString().toLowerCase(), offlinePlayer.getName(),
				percentage));
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

		MinetopiaUser user = MongoPlayerManager.getCache().get(offlinePlayer.getUniqueId());
		if (type == BoosterType.GRAYSHARD )
			user.setGrayshardBoost(user.getGrayshardBoost() - percentage);
		else user.setGoldshardBoost(user.getGoldshardBoost() - percentage);

		if (!offlinePlayer.isOnline())
			manager.save(user);

		player.sendMessage(Message.BOOSTERS_REMOVE.format(type.toString().toLowerCase(), offlinePlayer.getName(), percentage));
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

		MinetopiaUser user = MongoPlayerManager.getCache().get(offlinePlayer.getUniqueId());
		int total = type == BoosterType.GRAYSHARD ? user.getGrayshardBoost() : user.getGoldshardBoost();

		player.sendMessage(Message.BOOSTERS_INFO.format(offlinePlayer.getName(), total, type.toString().toLowerCase()));
	}

	@Subcommand("activate")
	@Syntax("<type> <percentage>")
	@Description("Activeer een booster")
	@CommandPermission("minetopia.common.booster.activate")
	@CommandCompletion("@boosterTypes")
	public void onActivate(Player player, BoosterType type, int percentage) {
		if (percentage <= 0) throw new ConditionFailedException(Message.COMMON_ERROR_USEATLEAST.format(1));

		MinetopiaUser user = MongoPlayerManager.getCache().get(player.getUniqueId());

		if (type == BoosterType.GRAYSHARD) {
			if (user.getGrayshardBoost() < percentage) throw new ConditionFailedException(Message.BOOSTERS_ERROR_NOTENOUGHGRAYSHARD.raw());
			user.setGrayshardBoost(user.getGrayshardBoost() - percentage);
		} else {
			if (user.getGoldshardBoost() < percentage) throw new ConditionFailedException(Message.BOOSTERS_ERROR_NOTENOUGHGOLDSHARD.raw());
			user.setGoldshardBoost(user.getGoldshardBoost() - percentage);
		}

		player.sendMessage(Message.BOOSTERS_ACTIVATED_SELF.format(type.toString().toLowerCase(), percentage));

		systemBoosterManager.update(player.getName(), type, systemBoosterManager.get(type) + percentage);
		Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(Message.BOOSTERS_ACTIVATED_BROADCAST.format(type.toString().toLowerCase(),
				player.getName(), percentage)));
	}

}
