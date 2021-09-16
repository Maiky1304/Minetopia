package dev.maiky.minetopia.modules.prefixes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.mongo.MongoPlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.modules.prefixes.ui.PrefixUI;
import dev.maiky.minetopia.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Door: Maiky
 * Info: Minetopia - 25 May 2021
 * Package: dev.maiky.minetopia.modules.prefixes.commands
 */

@CommandAlias("prefix")
@CommandPermission("minetopia.common.prefix")
public class PrefixCommand extends BaseCommand {
	
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
	@Description("Open the prefix menu")
	public void onMain(Player player) {
		PrefixUI prefixUI = new PrefixUI(player, MongoPlayerManager.getCache().get(player.getUniqueId()));
		prefixUI.open();
	}

	@Subcommand("add")
	@Syntax("<player> <prefix>")
	@Description("Add a prefix to a player")
	@CommandPermission("minetopia.moderation.prefix")
	@CommandCompletion("@players")
	public void onAdd(CommandSender sender, @Conditions("database") String target, String... prefix) {
		if (prefix.length == 0) {
			this.showSyntax(getCurrentCommandIssuer(), getDefaultRegisteredCommand());
			return;
		}

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i != prefix.length; i++)
			builder.append(prefix[i]).append(" ");
		String output = builder.substring(0, builder.length()-1);

		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		
		MinetopiaUser user = offlinePlayer.isOnline() ? MongoPlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.find(u -> u.getUuid().equals(offlinePlayer.getUniqueId())).findFirst().orElse(null);

		if (user.getPrefixes().contains(output))
			throw new ConditionFailedException(Message.PREFIX_ERROR_ALREADYHASPREFIX.raw());

		user.getPrefixes().add(output);
		if (!offlinePlayer.isOnline())
			playerManager.save(user);

		sender.sendMessage(Message.PREFIX_SUCCESS_ADD.format(output, offlinePlayer.getName()));
	}

	@Subcommand("remove")
	@Syntax("<player> <prefix>")
	@Description("Remove a prefix from a player")
	@CommandPermission("minetopia.moderation.prefix")
	@CommandCompletion("@players")
	public void onRemove(CommandSender sender, @Conditions("database") String target, String... prefix) {
		if (prefix.length == 0) {
			this.showSyntax(getCurrentCommandIssuer(), getDefaultRegisteredCommand());
			return;
		}

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i != prefix.length; i++)
			builder.append(prefix[i]).append(" ");
		String output = builder.substring(0, builder.length()-1);

		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		
		MinetopiaUser user = offlinePlayer.isOnline() ? MongoPlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.find(u -> u.getUuid().equals(offlinePlayer.getUniqueId())).findFirst().orElse(null);

		if (!user.getPrefixes().contains(output))
			throw new ConditionFailedException(Message.PREFIX_ERROR_DOESNTOWNPREFIX.raw());

		user.getPrefixes().add(output);
		if (!offlinePlayer.isOnline())
			playerManager.save(user);

		sender.sendMessage(Message.PREFIX_SUCCESS_REMOVE.format(output, offlinePlayer.getName()));
	}

	@Subcommand("list")
	@Syntax("<player>")
	@Description("See all prefixes a player has")
	@CommandPermission("minetopia.moderation.prefix")
	@CommandCompletion("@players")
	public void onList(CommandSender sender, @Conditions("database") String target) {
		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		
		MinetopiaUser user = offlinePlayer.isOnline() ? MongoPlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.find(u -> u.getUuid().equals(offlinePlayer.getUniqueId())).findFirst().orElse(null);
		sender.sendMessage(Message.PREFIX_INFO_DIVIDER.raw());
		for (String prefix : user.getPrefixes()) {
			sender.sendMessage(Message.PREFIX_INFO_ENTRY.format(prefix, (user.getCurrentPrefix().equals(prefix) ?
					Message.PREFIX_INFO_CURRENT.raw() : "")));
		}
		sender.sendMessage(Message.PREFIX_INFO_DIVIDER.raw());
	}

	@Subcommand("clear")
	@Syntax("<player>")
	@Description("Clear all prefixes of a player")
	@CommandPermission("minetopia.moderation.prefix")
	@CommandCompletion("@players")
	public void onClear(CommandSender sender, @Conditions("database") String target) {
		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		
		MinetopiaUser user = offlinePlayer.isOnline() ? MongoPlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.find(u -> u.getUuid().equals(offlinePlayer.getUniqueId())).findFirst().orElse(null);

		user.getPrefixes().clear();
		String defaultPrefix = Minetopia.getInstance().getConfiguration().get().getString("player.default.prefix");
		user.getPrefixes().add(defaultPrefix);
		user.setCurrentPrefix(defaultPrefix);;

		if (!offlinePlayer.isOnline())
			playerManager.save(user);

		sender.sendMessage(Message.PREFIX_SUCCESS_CLEAR.format(offlinePlayer.getName()));
	}

}
