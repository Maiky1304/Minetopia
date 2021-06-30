package dev.maiky.minetopia.modules.colors.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.colors.gui.ChatColorUI;
import dev.maiky.minetopia.modules.colors.packs.ChatColor;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.util.Message;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Door: Maiky
 * Info: Minetopia - 25 May 2021
 * Package: dev.maiky.minetopia.modules.colors.commands
 */

@CommandAlias("chatcolor|chatkleur|chatkleuren|chatcolors")
@CommandPermission("minetopia.common.chatcolor")
public class ChatColorCommand extends BaseCommand {

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
	@Description("Open het chatkleur menu")
	public void onMain(Player player) {
		ChatColorUI ui = new ChatColorUI(player, 0);
		ui.open();
	}

	@Subcommand("add")
	@Syntax("<player> <kleur> [tijd]")
	@CommandPermission("minetopia.admin.chatcolor")
	@Description("Add een chatkleur aan een speler")
	@CommandCompletion("@players")
	public void onAdd(CommandSender sender, @Conditions("database") String target, ChatColor color, @Default("permanent") String time) {
		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		PlayerManager playerManager = PlayerManager.with(DataModule.getInstance().getSqlHelper());
		MinetopiaUser user = offlinePlayer.isOnline() ? PlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.retrieve(offlinePlayer.getUniqueId());

		if (user.getChatColors().containsKey(color)) {
			throw new ConditionFailedException(Message.COLORS_ERROR_ALREADYOWNED.raw());
		}

		String string = null;
		boolean permanent = time.equals("permanent");
		Date expiry = null;
		if (!permanent){
			Response response = get(time);
			expiry = response.date;
			string = response.string;
		}

		user.getChatColors().put(color, expiry == null ? "-" : String.valueOf(expiry.getTime()));
		if (!offlinePlayer.isOnline())
			playerManager.update(user);

		if (string == null)
			sender.sendMessage(Message.COLORS_ADD_PERMANENT.format(color.itemName, offlinePlayer.getName()));
		else sender.sendMessage(Message.COLORS_ADD_TEMPORARY.format(color.itemName, offlinePlayer.getName(), string));
	}

	@Subcommand("remove")
	@Syntax("<player> <kleur>")
	@CommandPermission("minetopia.admin.chatcolor")
	@Description("Verwijder een chatkleur van een speler")
	@CommandCompletion("@players")
	public void onRemove(CommandSender sender, @Conditions("database") String target, ChatColor color) {
		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		PlayerManager playerManager = PlayerManager.with(DataModule.getInstance().getSqlHelper());
		MinetopiaUser user = offlinePlayer.isOnline() ? PlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.retrieve(offlinePlayer.getUniqueId());

		if (!user.getChatColors().containsKey(color)) {
			throw new ConditionFailedException(Message.COLORS_ERROR_NOTOWNED.raw());
		}

		user.getChatColors().remove(color);
		if (!offlinePlayer.isOnline())
			playerManager.update(user);

		sender.sendMessage(Message.COLORS_REMOVE.format(color.itemName, offlinePlayer.getName()));
	}

	@Subcommand("list")
	@CommandPermission("minetopia.admin.chatcolor")
	@Description("Bekijk alle chatkleuren van een speler")
	@CommandCompletion("@players")
	public void onList(CommandSender sender, @Conditions("database") String target) {
		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		PlayerManager playerManager = PlayerManager.with(DataModule.getInstance().getSqlHelper());
		MinetopiaUser user = offlinePlayer.isOnline() ? PlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.retrieve(offlinePlayer.getUniqueId());

		sender.sendMessage(Message.COLORS_LIST_HEADERFOOTER.raw());
		for (ChatColor color : user.getChatColors().keySet()) {
			sender.sendMessage(Message.COLORS_LIST_ENTRY.format(color.getColor(), color.itemName, (
					user.getChatColors().get(color).equals("-") ? "" :
							"T/m " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(Long.parseLong(user.getChatColors().get(color))))
			)));
		}
		sender.sendMessage(Message.COLORS_LIST_HEADERFOOTER.raw());
	}

	public Response get(String string) throws ConditionFailedException {
		int time;
		int timeUnit;
		StringBuilder timePart = new StringBuilder();
		StringBuilder idPart = new StringBuilder();

		for (char c : string.toCharArray()) {
			boolean b = Character.isDigit(c);
			if (b) {
				timePart.append(c);
			} else {
				idPart.append(c);
			}
		}

		try {
			time = Integer.parseInt(timePart.toString());
		} catch (NumberFormatException exception) {
			throw new ConditionFailedException(Message.COMMON_ERROR_INVALIDTIMEFORMAT.raw());
		}

		String timeString = "";
		if (idPart.toString().equalsIgnoreCase("d")) {
			timeUnit = 6;
			timeString = time + (time == 1 ? " dag" : " dagen");
		} else if (idPart.toString().equalsIgnoreCase("mo")) {
			timeUnit = 2;
			timeString = time + (time == 1 ? " maand" : " maanden");
		} else if (idPart.toString().equalsIgnoreCase("y")) {
			timeUnit = 1;
			timeString = time + (time == 1 ? " jaar" : " jaren");
		} else if (idPart.toString().equalsIgnoreCase("min")) {
			timeUnit = 12;
			timeString = time + (time == 1 ? " minuut" : " minuten");
		} else {
			throw new ConditionFailedException(Message.COMMON_ERROR_INVALIDTIMEFORMAT.raw());
		}

		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(timeUnit, time);
		date = calendar.getTime();
		return new Response(date, timeString);
	}

	static class Response {
		@Getter
		private final Date date;
		@Getter
		private final String string;

		public Response(Date date, String string) {
			this.date = date;
			this.string = string;
		}
	}

}
