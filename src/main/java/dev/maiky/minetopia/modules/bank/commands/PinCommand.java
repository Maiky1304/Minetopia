package dev.maiky.minetopia.modules.bank.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.ConditionFailedException;
import co.aikar.commands.annotation.*;
import dev.maiky.minetopia.modules.bank.bank.Console;
import dev.maiky.minetopia.modules.bank.bank.PinRequest;
import dev.maiky.minetopia.modules.bank.manager.PinManager;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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

	public boolean hasRequest(Player player) {
		return this.pinManager.getPinRequests().stream().anyMatch(pinRequest ->
				(pinRequest.getFrom().equals(player) || pinRequest.getTo().equals(player)) && !pinRequest.isFinished());
	}

	@Subcommand("set")
	@Description("Start a pin request")
	@Syntax("<speler> <bedrag>")
	@CommandPermission("minetopia.common.pin.set")
	public void set(Player player, OfflinePlayer target, double amount) {
		if (hasRequest(player)) throw new ConditionFailedException("Maak eerst de huidige betaling af voordat je een nieuwe start!");
		if (!target.isOnline()) throw new ConditionFailedException("Deze speler is niet online!");
		if (amount < 0.01d) throw new ConditionFailedException("De betaling moet minimaal 1 euro cent zijn.");

		PinRequest request = new PinRequest(null, player, target.getPlayer(), amount);
		this.pinManager.getPinRequests().add(request);

		player.sendMessage("§6Je hebt een verzoek aangemaakt klik nu op een §cpinconsole §6om het verzoek te versturen.");

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
