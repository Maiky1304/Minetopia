package dev.maiky.minetopia.modules.players.commands.essential;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.mongo.MongoPlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.util.Message;
import dev.maiky.minetopia.util.Numbers;
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

@CommandAlias("goldshards|goldshard")
@CommandPermission("minetopia.common.shard")
public class GoldShardCommand extends BaseCommand {
	
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
	@Subcommand("main")
	@Description("View your own shard balance")
	@Conditions("MTUser")
	public void on(Player sender) {
		MinetopiaUser user = MongoPlayerManager.getCache().get(sender.getUniqueId());
		sender.sendMessage(Message.PLAYER_INFO_SHARDSINFOSELF.format(Numbers.convert(Numbers.Type.SHARDS,
				user.getGoldshards()), "Gold"));
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

		
		MinetopiaUser user = offlinePlayer.isOnline() ? MongoPlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.find(u -> u.getUuid().equals(offlinePlayer.getUniqueId())).findFirst().orElse(null);
		user.setGoldshards(user.getGoldshards() + amount);
		if (!offlinePlayer.isOnline())
			playerManager.save(user);

		sender.sendMessage(Message.PLAYER_SUCCESSFULLY_SHARDS_ADDED.format(Numbers.convert(Numbers.Type.SHARDS, amount), "Gold",
				offlinePlayer.getName(), Numbers.convert(Numbers.Type.SHARDS, user.getGoldshards())));
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

		
		MinetopiaUser user = offlinePlayer.isOnline() ? MongoPlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.find(u -> u.getUuid().equals(offlinePlayer.getUniqueId())).findFirst().orElse(null);
		user.setGoldshards(user.getGoldshards() - amount);
		if (user.getGoldshards() < 0)
			user.setGoldshards(0);
		if (!offlinePlayer.isOnline())
			playerManager.save(user);

		sender.sendMessage(Message.PLAYER_SUCCESSFULLY_SHARDS_REMOVED.format(Numbers.convert(Numbers.Type.SHARDS, amount), "Gold",
				offlinePlayer.getName(), Numbers.convert(Numbers.Type.SHARDS, user.getGoldshards())));
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

		
		MinetopiaUser user = offlinePlayer.isOnline() ? MongoPlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.find(u -> u.getUuid().equals(offlinePlayer.getUniqueId())).findFirst().orElse(null);
		sender.sendMessage(Message.PLAYER_INFO_SHARDSINFO.format(offlinePlayer.getName(), Numbers.convert(Numbers.Type.SHARDS, user.getGoldshards()), "Gold"));
	}
}
