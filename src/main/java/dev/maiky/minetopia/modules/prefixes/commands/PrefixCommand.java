package dev.maiky.minetopia.modules.prefixes.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.modules.prefixes.ui.PrefixUI;
import dev.maiky.minetopia.util.Message;
import dev.maiky.minetopia.util.Text;
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
		PrefixUI prefixUI = new PrefixUI(player, PlayerManager.getCache().get(player.getUniqueId()));
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

		PlayerManager playerManager = PlayerManager.with(DataModule.getInstance().getSqlHelper());
		MinetopiaUser user = offlinePlayer.isOnline() ? PlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.retrieve(offlinePlayer.getUniqueId());

		if (user.getPrefixes().contains(output))
			throw new ConditionFailedException("This player already owns this prefix!");

		user.getPrefixes().add(output);
		if (!offlinePlayer.isOnline())
			playerManager.update(user);

		String message = "&6Success! You have added the prefix &c%s &6to the player &c%s&6.";
		sender.sendMessage(String.format(Text.colors(message), output, offlinePlayer.getName()));
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

		PlayerManager playerManager = PlayerManager.with(DataModule.getInstance().getSqlHelper());
		MinetopiaUser user = offlinePlayer.isOnline() ? PlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.retrieve(offlinePlayer.getUniqueId());

		if (!user.getPrefixes().contains(output))
			throw new ConditionFailedException("This player doesn't own this prefix!");

		user.getPrefixes().add(output);
		if (!offlinePlayer.isOnline())
			playerManager.update(user);

		String message = "&6Success! You have removed the prefix &c%s &6from the player &c%s&6.";
		sender.sendMessage(String.format(Text.colors(message), output, offlinePlayer.getName()));
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

		PlayerManager playerManager = PlayerManager.with(DataModule.getInstance().getSqlHelper());
		MinetopiaUser user = offlinePlayer.isOnline() ? PlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.retrieve(offlinePlayer.getUniqueId());

		String divider = "§6§m----------------------------------------------------";
		String holder = " &c- &6%s &c%s";

		sender.sendMessage(divider);
		for (String prefix : user.getPrefixes()) {
			sender.sendMessage(String.format(Text.colors(holder), (user.getCurrentPrefix().equals(prefix) ?
					"<-- Current" : prefix)));
		}
		sender.sendMessage(divider);
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

		PlayerManager playerManager = PlayerManager.with(DataModule.getInstance().getSqlHelper());
		MinetopiaUser user = offlinePlayer.isOnline() ? PlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.retrieve(offlinePlayer.getUniqueId());

		user.getPrefixes().clear();
		user.getPrefixes().add("Burger");
		user.setCurrentPrefix("Burger");;

		if (!offlinePlayer.isOnline())
			playerManager.update(user);

		String message = "&6Success! Cleared all of &c%s &6their prefixes.";
		sender.sendMessage(String.format(Text.colors(message), offlinePlayer.getName()));
	}

}
