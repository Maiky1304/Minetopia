package dev.maiky.minetopia.modules.players.commands.essential;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.util.Numbers;
import dev.maiky.minetopia.util.Text;
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

@CommandAlias("shard|blackshard|shards|blackshards")
@CommandPermission("minetopia.common.shard")
public class ShardCommand extends BaseCommand {

	@HelpCommand
	public void onHelp(CommandSender sender) {
		Minetopia.showHelp(sender, this, getSubCommands());
	}

	@Default
	@Subcommand("main")
	@Description("View your own shard balance")
	public void on(@Conditions("MTUser") Player sender) {
		MinetopiaUser user = PlayerManager.getCache().get(sender.getUniqueId());
		String message = "&6Jij hebt &c%s &6BlackShards.";
		sender.sendMessage(Text.colors(String.format(message, Numbers.convert(Numbers.Type.SHARDS, user.getGrayshards()))));
	}

	@Subcommand("add")
	@Syntax("<player> <shards>")
	@Description("Add shards aan een speler")
	@CommandPermission("minetopia.moderation.shard.add")
	@CommandCompletion("@players")
	public void add(CommandSender sender, @Conditions("database") String target, double amount) {
		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		PlayerManager playerManager = PlayerManager.with(DataModule.getInstance().getSqlHelper());
		MinetopiaUser user = offlinePlayer.isOnline() ? PlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.retrieve(offlinePlayer.getUniqueId());
		user.setGrayshards(user.getGrayshards() + amount);
		if (!offlinePlayer.isOnline())
			playerManager.update(user);

		String message = "§6Success! Shards of §c%s §6were increased by §c%s§6 their balance is now §c%s&6.";
		sender.sendMessage(Text.colors(String.format(message, offlinePlayer.getName(), amount, user.getGrayshards())));
	}

	@CatchUnknown
	public void onUnknown(CommandSender sender) {
		sender.sendMessage("§cUnknown subcommand");
		this.onHelp(sender);
	}

	@Subcommand("remove")
	@Syntax("<player> <shards>")
	@Description("Verwijder shards van een speler")
	@CommandPermission("minetopia.moderation.shard.remove")
	@CommandCompletion("@players")
	public void remove(CommandSender sender, @Conditions("database") String target, double amount) {
		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		PlayerManager playerManager = PlayerManager.with(DataModule.getInstance().getSqlHelper());
		MinetopiaUser user = offlinePlayer.isOnline() ? PlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.retrieve(offlinePlayer.getUniqueId());
		user.setGrayshards(user.getGrayshards() - amount);
		if (!offlinePlayer.isOnline())
			playerManager.update(user);

		String message = "§6Success! Shards of §c%s §6were decreased by §c%s§6 their balance is now §c%s&6.";
		sender.sendMessage(Text.colors(String.format(message, offlinePlayer.getName(), amount, user.getGrayshards())));
	}

	@Subcommand("info")
	@Syntax("<player>")
	@Description("Bekijk de shards van een speler")
	@CommandCompletion("@players")
	@CommandPermission("minetopia.moderation.shard.other")
	public void info(CommandSender sender, @Conditions("database") String target) {
		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		PlayerManager playerManager = PlayerManager.with(DataModule.getInstance().getSqlHelper());
		MinetopiaUser user = offlinePlayer.isOnline() ? PlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.retrieve(offlinePlayer.getUniqueId());
		String message = "&6De speler &c%s &6heeft &c%s &6BlackShards.";
		sender.sendMessage(Text.colors(String.format(message, offlinePlayer.getName(), Numbers.convert(Numbers.Type.SHARDS, user.getGrayshards()))));
	}

	@Override
	public void showSyntax(CommandIssuer issuer, RegisteredCommand<?> cmd) {
		issuer.sendMessage("§cGebruik: /" + this.getExecCommandLabel() + " " +
				cmd.getPrefSubCommand() + " " +
				cmd.getSyntaxText());
	}

}
