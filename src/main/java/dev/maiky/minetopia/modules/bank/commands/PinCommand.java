package dev.maiky.minetopia.modules.bank.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.RegisteredCommand;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.bank.bank.Console;
import dev.maiky.minetopia.modules.bank.bank.PinRequest;
import dev.maiky.minetopia.modules.bank.manager.PinManager;
import dev.maiky.minetopia.util.Message;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Door: Maiky
 * Info: Minetopia - 20 Jun 2021
 * Package: dev.maiky.minetopia.modules.bank.commands
 */

@CommandAlias("pin")
@CommandPermission("minetopia.common.pin")
public class PinCommand extends BaseCommand {

	private final PinManager pinManager;

	public PinCommand(PinManager pinManager) {
		this.pinManager = pinManager;
	}

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

	public boolean hasRequest(Player player) {
		return this.pinManager.getPinRequests().stream().anyMatch(pinRequest ->
				(pinRequest.getFrom().equals(player) || pinRequest.getTo().equals(player)) && !pinRequest.isFinished());
	}

	@Subcommand("set")
	@Description("Start a pin request")
	@Syntax("<speler> <bedrag>")
	@CommandPermission("minetopia.common.pin.set")
	public void set(Player player, OfflinePlayer target, double amount) {
		if (hasRequest(player)) throw new ConditionFailedException(Message.BANKING_ERROR_PINCONSOLE_FINISHPINPAYMENT.raw());
		if (!target.isOnline()) throw new ConditionFailedException(Message.COMMON_ERROR_PLAYEROFFLINE.raw());
		if (amount < 0.01d) throw new ConditionFailedException(Message.BANKING_ERROR_PINCONSOLE_ATLEAST1EUROCENT.raw());

		PinRequest request = new PinRequest(null, player, target.getPlayer(), amount);
		this.pinManager.getPinRequests().add(request);

		player.sendMessage(Message.BANKING_REQUESTS_CREATED.raw());

		CompositeTerminable terminable = CompositeTerminable.create();
		Events.subscribe(PlayerInteractEvent.class)
				.filter(e -> e.getAction() == Action.RIGHT_CLICK_BLOCK)
				.filter(e -> e.getClickedBlock().getType() == Material.PURPUR_STAIRS)
				.filter(e -> pinManager.find(e.getClickedBlock().getLocation()) != null)
				.handler(e -> {
					Console console = pinManager.find(e.getClickedBlock().getLocation());
					request.setConsole(console);
					request.setLocation(e.getClickedBlock().getLocation());
					request.startRequest();

					try {
						terminable.close();
					} catch (CompositeClosingException compositeClosingException) {
						compositeClosingException.printStackTrace();
					}
				}).bindWith(terminable);
	}

}
