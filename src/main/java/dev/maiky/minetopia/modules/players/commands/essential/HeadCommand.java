package dev.maiky.minetopia.modules.players.commands.essential;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

/**
 * Door: Maiky
 * Info: Minetopia - 11 Jun 2021
 * Package: dev.maiky.minetopia.modules.players.commands.essential
 */

@CommandAlias("head")
@CommandPermission("minetopia.common.head")
public class HeadCommand extends BaseCommand {

	@HelpCommand
	public void onHelp(CommandSender sender) {
		Minetopia.showHelp(sender, this, getSubCommands());
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

	@Default
	@Subcommand("main")
	@Description("Put an item on your head")
	public void onMain(@Conditions("itemPossibleOnHead") Player player) {
		PlayerInventory inventory = player.getInventory();

		ItemStack currentHelmet = inventory.getHelmet();
		ItemStack mainHand = inventory.getItemInMainHand();

		if (currentHelmet != null) {
			ItemStack giveLater = currentHelmet.clone();
			inventory.setHelmet(mainHand);
			inventory.setItemInMainHand(giveLater);
		} else {
			inventory.setHelmet(mainHand);
			inventory.setItemInMainHand(null);
		}

		player.sendMessage("§3Je hebt dit item op je hoofd gezet.");
	}

}
