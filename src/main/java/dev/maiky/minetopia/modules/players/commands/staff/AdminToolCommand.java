package dev.maiky.minetopia.modules.players.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.players.ui.AdminToolUI;
import dev.maiky.minetopia.util.Message;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Door: Maiky
 * Info: Minetopia - 19 Jun 2021
 * Package: dev.maiky.minetopia.modules.players.commands.staff
 */

@CommandAlias("admintool")
@Description("For the admintool")
@CommandPermission("minetopia.moderation.admintool")
public class AdminToolCommand extends BaseCommand {


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

	@Subcommand("krijg")
	@Description("Krijg een admintool")
	@CommandPermission("minetopia.moderation.admintool.krijg")
	public void onGet(Player player) {
		if (player.getInventory().firstEmpty() == -1) throw new ConditionFailedException("Je hebt geen genoeg inventory ruimte!");

		final ItemStack adminTool = ItemStackBuilder.of(Material.NETHER_STAR)
				.name("&3&lAdmin&b&lTool").build();
		player.getInventory().addItem(adminTool);
	}

	@Subcommand("open")
	@Description("Open de admintool van iemand")
	@CommandPermission("minetopia.moderation.admin.open")
	public void onOpen(Player player, @Conditions("database") String target) {
		OfflinePlayer offlinePlayer;
		if (target.length() == 32)
			offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(target));
		else offlinePlayer = Bukkit.getOfflinePlayer(target);

		AdminToolUI ui = new AdminToolUI(player, offlinePlayer);
		ui.open();
		player.sendMessage("§6Je opent nu het admintool menu van §c" + offlinePlayer.getName() + "§6.");
	}

}
