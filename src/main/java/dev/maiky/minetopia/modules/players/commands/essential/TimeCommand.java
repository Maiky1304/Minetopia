package dev.maiky.minetopia.modules.players.commands.essential;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaTime;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.util.Message;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Door: Maiky
 * Info: Minetopia - 22 May 2021
 * Package: dev.maiky.minetopia.modules.players.commands.essential
 */

@CommandAlias("time|tijd|speeltijd")
@CommandPermission("minetopia.common.time")
public class TimeCommand extends BaseCommand {

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
	@Subcommand("main")
	@Description("View your own online time")
	@Conditions("MTUser")
	public void onTime(Player player) {
		MinetopiaUser user = PlayerManager.getCache().get(player.getUniqueId());
		MinetopiaTime time = user.getTime();

		String string = "§c%s §6dagen, §c%s §6uren, §c%s §6minuten, §c%s §6seconden.";

		player.sendMessage(String.format(string, time.getDays(), time.getHours(), time.getMinutes(), time.getSeconds()));
	}

	@Deprecated
	@Subcommand("info")
	@Syntax("<player>")
	@Description("View other player's their online time")
	@CommandPermission("minetopia.moderation.time.other")
	@CommandCompletion("@players")
	public void onTimeOther(CommandSender sender, @Conditions("database") String player) {
		OfflinePlayer offlinePlayer;
		if (player.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(player));
		else offlinePlayer = Bukkit.getOfflinePlayer(player);

		PlayerManager playerManager = PlayerManager.with(DataModule.getInstance().getSqlHelper());
		MinetopiaUser user = offlinePlayer.isOnline() ? PlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.retrieve(offlinePlayer.getUniqueId());
		MinetopiaTime time = user.getTime();
		String string = "§6Time van §c%s§6: §c%s §6dagen, §c%s §6uren, §c%s §6minuten, §c%s §6seconden.";
		sender.sendMessage(String.format(string, offlinePlayer.getName(), time.getDays(), time.getHours(), time.getMinutes(), time.getSeconds()));
	}

}
