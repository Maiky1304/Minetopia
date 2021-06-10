package dev.maiky.minetopia.modules.colors.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.colors.gui.LevelColorUI;
import dev.maiky.minetopia.modules.colors.packs.LevelColor;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.util.Text;
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

@CommandAlias("levelcolor|levelkleur|levelkleuren|levelcolors")
@CommandPermission("minetopia.common.levelcolor")
public class LevelColorCommand extends BaseCommand {

	@HelpCommand
	public void onHelp(CommandSender sender) {
		Minetopia.showHelp(sender, this, getSubCommands());
	}

	@Default
	@Conditions("MTUser")
	@Subcommand("main")
	@Description("Open het levelkleur menu")
	public void onMain(Player player) {
		LevelColorUI ui = new LevelColorUI(player, 0);
		ui.open();
	}

	@CatchUnknown
	public void onUnknown(CommandSender sender) {
		sender.sendMessage("§cUnknown subcommand");
		this.onHelp(sender);
	}

	@Subcommand("add")
	@Syntax("<player> <kleur> [tijd]")
	@CommandPermission("minetopia.admin.levelcolor")
	@Description("Add een levelkleur aan een speler")
	@CommandCompletion("@players")
	public void onAdd(CommandSender sender, @Conditions("database") String target, LevelColor color, @Default("permanent") String time) {
		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		PlayerManager playerManager = PlayerManager.with(DataModule.getInstance().getSqlHelper());
		MinetopiaUser user = offlinePlayer.isOnline() ? PlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.retrieve(offlinePlayer.getUniqueId());

		if (user.getLevelColors().containsKey(color)) {
			throw new ConditionFailedException("Deze speler heeft deze kleur al in zijn/haar bezit!");
		}

		String string = null;
		boolean permanent = time.equals("permanent");
		Date expiry = null;
		if (!permanent){
			Response response = get(time);
			expiry = response.date;
			string = response.string;
		}

		user.getLevelColors().put(color, expiry == null ? "-" : String.valueOf(expiry.getTime()));
		if (!offlinePlayer.isOnline())
			playerManager.update(user);

		String message = "&6Success! You have added the levelcolor &c%s &6to the user &c%s&6.";
		String message2 = "§6Extra: §c%s";
		sender.sendMessage(String.format(Text.colors(message), color.itemName, offlinePlayer.getName()));
		if (string != null)
			sender.sendMessage(String.format(Text.colors(message2), string));
	}

	@Subcommand("remove")
	@Syntax("<player> <kleur>")
	@CommandPermission("minetopia.admin.levelcolor")
	@Description("Verwijder een levelkleur van een speler")
	@CommandCompletion("@players")
	public void onRemove(CommandSender sender, @Conditions("database") String target, LevelColor color) {
		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		PlayerManager playerManager = PlayerManager.with(DataModule.getInstance().getSqlHelper());
		MinetopiaUser user = offlinePlayer.isOnline() ? PlayerManager.getCache().get(offlinePlayer.getUniqueId())
				: playerManager.retrieve(offlinePlayer.getUniqueId());

		if (!user.getLevelColors().containsKey(color)) {
			throw new ConditionFailedException("Deze speler heeft deze kleur niet in zijn/haar bezit!");
		}

		user.getLevelColors().remove(color);
		if (!offlinePlayer.isOnline())
			playerManager.update(user);

		String message = "&6Success! You have removed the levelcolor &c%s &6from the user &c%s&6.";
		sender.sendMessage(String.format(Text.colors(message), color.itemName, offlinePlayer.getName()));
	}

	@Subcommand("list")
	@CommandPermission("minetopia.admin.levelcolor")
	@Description("Bekijk alle levelkleuren van een speler")
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
		String holder = " &c- &%s%s &c%s";

		sender.sendMessage(divider);
		for (LevelColor color : user.getLevelColors().keySet()) {
			sender.sendMessage(String.format(Text.colors(holder), color.getColor(), color.itemName, (
					user.getLevelColors().get(color).equals("-") ? "" :
							"T/m " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(Long.parseLong(user.getLevelColors().get(color))))
					)));
		}
		sender.sendMessage(divider);
	}

	@Override
	public void showSyntax(CommandIssuer issuer, RegisteredCommand<?> cmd) {
		issuer.sendMessage("§cGebruik: /" + this.getExecCommandLabel() + " " +
				cmd.getPrefSubCommand() + " " +
				cmd.getSyntaxText());
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
			throw new ConditionFailedException("Dit is geen geldig tijdformaat, gebruik bijvoorbeeld: 1, 1min, 1mo of 1y");
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
			throw new ConditionFailedException("Dit is geen geldig tijdformaat, gebruik bijvoorbeeld: 1, 1min, 1mo of 1y");
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
