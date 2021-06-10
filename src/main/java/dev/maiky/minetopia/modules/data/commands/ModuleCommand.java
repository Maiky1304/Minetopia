package dev.maiky.minetopia.modules.data.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.data.ui.ModuleUI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

/**
 * Door: Maiky
 * Info: Minetopia - 21 May 2021
 * Package: dev.maiky.minetopia.modules.data.commands
 */

@CommandAlias("module|modules")
@CommandPermission("minetopia.admin.modules")
@Description("View all modules")
public class ModuleCommand extends BaseCommand {

	@Default
	@Subcommand("main")
	@Description("Open the Module GUI")
	public void onExecute(Player player) {
		ModuleUI moduleUI = new ModuleUI(player);
		moduleUI.open();
	}

	@HelpCommand
	public void onHelp(CommandSender sender) {
		Minetopia.showHelp(sender, this, getSubCommands());
	}

	@Subcommand("reloadall")
	@Description("Reload all modules")
	public void onReloadAll(CommandSender sender) {
		Minetopia minetopia = Minetopia.getPlugin(Minetopia.class);
		long start = System.currentTimeMillis();

		sender.sendMessage("§3[Minetopia] §bReloading all modules...");
		minetopia.reloadModules();
		long end = System.currentTimeMillis();
		sender.sendMessage(String.format("§3[Minetopia] §bReloaded all modules took %.1fs", (double) TimeUnit.SECONDS.convert(((end-start)), TimeUnit.MILLISECONDS)));
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

}
