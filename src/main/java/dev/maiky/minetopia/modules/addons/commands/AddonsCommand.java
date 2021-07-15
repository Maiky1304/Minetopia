package dev.maiky.minetopia.modules.addons.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.addons.AddonsModule;
import dev.maiky.minetopia.modules.addons.addon.Addon;
import dev.maiky.minetopia.util.Message;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * Door: Maiky
 * Info: Minetopia - 17 Jun 2021
 * Package: dev.maiky.minetopia.modules.addons.commands
 */

@CommandAlias("addons")
@Description("View all the loaded addons")
public class AddonsCommand extends BaseCommand {

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
		issuer.sendMessage("§cGebruik: /" + this.getExecCommandLabel() + " " +
				cmd.getPrefSubCommand() + " " +
				cmd.getSyntaxText());
	}

	@Default
	@Subcommand("main")
	@Description("See the loaded addons")
	public void onMain(CommandSender sender) {
		StringBuilder builder = new StringBuilder();
		List<Addon> addonList = new ArrayList<>(AddonsModule.getAddons());

		for (Addon addon : addonList) {
			ChatColor color = addon.isEnabled() ? ChatColor.GREEN : ChatColor.RED;
			builder.append(color).append(addon.getName()).append(ChatColor.RESET).append(Message.ADDONS_SEPERATOR.raw()).append(" ");
		}

		String raw = addonList.size() == 0 ? "" : builder.substring(0, builder.length()-2);
		sender.sendMessage(Message.ADDONS_VIEW.format(addonList.size(), raw));
	}

}
