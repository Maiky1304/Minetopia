package dev.maiky.minetopia.modules.ddgitems.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.ddgitems.items.classes.ItemType;
import dev.maiky.minetopia.modules.ddgitems.ui.ItemsUI;
import dev.maiky.minetopia.modules.ddgitems.ui.SelectUI;
import dev.maiky.minetopia.util.Message;
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
	@Description("Open the DDG Items GUI")
	public void onMain(Player player) {
		SelectUI ui = new SelectUI(player);
		ui.open();
	}

}
