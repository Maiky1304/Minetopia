package dev.maiky.minetopia.modules.notifications.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.notifications.notifications.Notification;
import dev.maiky.minetopia.modules.notifications.notifications.NotificationQueue;
import dev.maiky.minetopia.util.Message;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Door: Maiky
 * Info: Minetopia - 10 Jun 2021
 * Package: dev.maiky.minetopia.modules.notifications.command
 */

@CommandAlias("notification")
@CommandPermission("minetopia.admin.notification")
public class NotificationCommand extends BaseCommand {

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

	@Subcommand("test")
	@Syntax("<target> <duration> <message>")
	@CommandCompletion("@players @nothing @nothing")
	@Description("No description")
	public void onTest(Player player, OfflinePlayer target, int length, String[] message) {
		if (message.length == 0) {
			this.showSyntax(getCurrentCommandIssuer(), this.getDefaultRegisteredCommand());
			return;
		}

		if (!target.isOnline()) return;

		Player targetPlayer = target.getPlayer();
		StringBuilder builder = new StringBuilder();
		for (String s : message) builder.append(s).append(" ");

		NotificationQueue queue = NotificationQueue.getQueueCache().get(target.getUniqueId());
		Notification notification = new Notification(targetPlayer, builder.substring(0, builder.length()-1), length);
		queue.queue.add(notification);

		player.sendMessage("§6Succesfully sent this test message.");
	}

}
