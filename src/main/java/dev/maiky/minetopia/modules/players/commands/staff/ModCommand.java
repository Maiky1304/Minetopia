package dev.maiky.minetopia.modules.players.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.mongo.MongoPlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.util.Message;
import dev.maiky.minetopia.util.Text;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.UUID;

/**
 * Door: Maiky
 * Info: Minetopia - 21 May 2021
 * Package: dev.maiky.minetopia.modules.players.commands.staff
 */

@CommandAlias("mod|moderation")
@CommandPermission("minetopia.moderation")
public class ModCommand extends BaseCommand {

	private final MongoPlayerManager playerManager = DataModule.getInstance().getPlayerManager();

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

	@Subcommand("setlevel")
	@Syntax("<player> <level>")
	@Description("Verzet het level van een speler")
	@CommandCompletion("@players @range:1-100")
	public void setLevel(CommandSender sender, @Conditions("database") String target, @Conditions("limits:min=1,max=100") Integer level) {
		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		MinetopiaUser user = offlinePlayer.isOnline() ? MongoPlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.find(u -> u.getUuid().equals(offlinePlayer.getUniqueId())).findFirst().orElse(null);
		assert user != null;
		user.setLevel(level);
		if (!offlinePlayer.isOnline())
			playerManager.save(user);

		String message = "§6Success! Level of §c%s §6was set to §c%s§6.";
		sender.sendMessage(String.format(message, offlinePlayer.getName(), String.format("Level %s", level)));
	}

	@Subcommand("setcitycolor")
	@Syntax("<player> <color>")
	@Description("Verzet de citycolor van een speler")
	@CommandCompletion("@players @nothing")
	public void setCityColor(CommandSender sender, @Conditions("database") String target, String color) {
		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		
		MinetopiaUser user = offlinePlayer.isOnline() ? MongoPlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.find(u -> u.getUuid().equals(offlinePlayer.getUniqueId())).findFirst().orElse(null);
		assert user != null;
		user.setCityColor(Text.colors(color));
		if (!offlinePlayer.isOnline())
			playerManager.save(user);

		String message = "§6Success! Citycolor of §c%s §6was set to §c%s§6.";
		sender.sendMessage(String.format(message, offlinePlayer.getName(), "&" + color));
	}

}
