package dev.maiky.minetopia.modules.ddgitems.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.ddgitems.items.classes.ItemType;
import dev.maiky.minetopia.modules.ddgitems.ui.ItemsUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Door: Maiky
 * Info: Minetopia - 26 May 2021
 * Package: dev.maiky.minetopia.modules.ddgitems.commands
 */

@CommandAlias("ddgitems")
@CommandPermission("minetopia.admin.ddgitems")
public class DDGItemsCommand extends BaseCommand {

	@Subcommand("main")
	@Syntax("<type>")
	@CommandCompletion("@itemTypes")
	@Default
	@Description("Open the DDG Items GUI")
	public void onMain(Player player, ItemType type) {
		ItemsUI itemsUI = new ItemsUI(player, type, 0);
		itemsUI.open();
	}

	@CatchUnknown
	public void onUnknown(CommandSender sender) {
		sender.sendMessage("§cUnknown subcommand");
		this.onHelp(sender);
	}

	@Override
	public void showSyntax(CommandIssuer issuer, RegisteredCommand<?> cmd) {
		issuer.sendMessage("§cGebruik: /" + this.getExecCommandLabel() + " " +
				cmd.getPrefSubCommand() + " " +
				cmd.getSyntaxText());
	}

	@HelpCommand
	public void onHelp(CommandSender sender) {
		Minetopia.showHelp(sender, this, getSubCommands());
	}

}
