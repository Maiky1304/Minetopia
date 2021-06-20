package dev.maiky.minetopia.modules.bank.bank;

import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.modules.bank.BankModule;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.BankManager;
import dev.maiky.minetopia.util.Numbers;
import dev.maiky.minetopia.util.Text;
import javafx.util.Callback;
import lombok.Getter;
import lombok.Setter;
import me.lucko.helper.Events;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Door: Maiky
 * Info: Minetopia - 20 Jun 2021
 * Package: dev.maiky.minetopia.modules.bank.bank
 */

public class PinRequest {

	private CompositeTerminable terminable = CompositeTerminable.create();

	@Getter @Setter
	private Player from, to;
	@Getter @Setter
	private double amount;
	@Getter @Setter
	private boolean finished = false;

	@Getter @Setter
	private State state = null;

	@Getter @Setter
	private Location location = null;

	@Getter
	private final UUID paymentIdentifier;

	@Getter @Setter
	private Console console;

	public PinRequest(Console console, Player from, Player to, double amount) {
		this.paymentIdentifier = UUID.randomUUID();
		this.console = console;
		this.from = from;
		this.to = to;
		this.amount = amount;
	}

	public void startRequest() {
		try {
			Events.subscribe(PlayerQuitEvent.class)
					.filter(e -> e.getPlayer().equals(from) || e.getPlayer().equals(to))
					.handler(e -> this.endRequest(e.getPlayer().equals(from) ? Reason.SHOPKEEPER_LEFT : Reason.CLIENT_LEFT)).bindWith(terminable);

			String converted = Numbers.convert(Numbers.Type.MONEY, this.amount);

			from.sendMessage(Text.colors("&6Je hebt het betaalproces met &c" + to.getName() + " &6gestart, met een bedrag van &c" + converted + "&6."));
			to.sendMessage(Text.colors("&6Je hebt een pinverzoek ontvangen van &c" + from.getName() + " &6met een bedrag van &c" + converted + "&6."));

			this.state = State.CLICK;

			Events.subscribe(PlayerInteractEvent.class)
					.filter(e -> state == State.CLICK)
					.filter(e -> e.getAction() == Action.RIGHT_CLICK_BLOCK)
					.filter(e -> e.getPlayer().equals(to))
					.filter(e -> e.getClickedBlock().getLocation().equals(location))
					.filter(e -> e.getClickedBlock().getType() == Material.PURPUR_STAIRS)
					.filter(e -> e.getPlayer().getInventory().getItemInMainHand() != null)
					.filter(e -> e.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR)
					.filter(BankModule::isBankCard)
					.handler(e -> {
						to.sendMessage(Text.colors("&6Typ &caccept &6in de chat om de betaling te voltooien!"));
						state = State.CHAT;
					}).bindWith(terminable);

			Events.subscribe(AsyncPlayerChatEvent.class)
					.filter(e -> state == State.CHAT)
					.filter(e -> e.getPlayer().equals(to))
					.expireAfter(1)
					.handler(e -> {
						e.setCancelled(true);

						String message = e.getMessage();
						if (!message.equalsIgnoreCase("accept")) {
							this.endRequest(Reason.CANCELLED);
							return;
						}

						Economy economy = Minetopia.getEconomy();
						if (!economy.has(to, amount)) {
							this.endRequest(Reason.INSUFFICIENT_BALANCE);
							return;
						}

						EconomyResponse response = economy.withdrawPlayer(to, amount);
						if (response.transactionSuccess()) {
							BankManager bankManager = BankManager.with(DataModule.getInstance().getSqlHelper());
							Account shopAccount = bankManager.getAccount(this.console.getAccountType(), this.console.getAccountNumber());
							shopAccount.deposit(this.amount);
							bankManager.saveAccount(shopAccount);

							this.endRequest(Reason.SUCCESS);
							return;
						}

						Bukkit.getLogger().warning("Meld dit bij Maiky#0001");
						Bukkit.getLogger().warning(response.errorMessage);
						this.endRequest(Reason.ERROR);
					}).bindWith(terminable);

		} catch (Exception exception) {
			exception.printStackTrace();
			this.endRequest(Reason.ERROR);
		}
	}

	public void endRequest(Reason reason) {
		if (reason == Reason.SUCCESS) {
			String converted = Numbers.convert(Numbers.Type.MONEY, this.amount);
			from.sendMessage(Text.colors("&6De betaling met &c" + to.getName() + " &6van &c" + converted + " &6is voltooid."));
			to.sendMessage(Text.colors("&6Je hebt de betaling met &c" + from.getName() + " &6van &c" + converted + " &6voltooid."));
		} else if (reason == Reason.INSUFFICIENT_BALANCE) {
			from.sendMessage(Text.colors("&cDe betaling is mislukt omdat de klant onvoldoende banksaldo heeft."));
			to.sendMessage(Text.colors("&cJe hebt onvoldoende saldo het betaalproces is geannuleerd."));
		} else if (reason == Reason.CLIENT_LEFT) {
			from.sendMessage(Text.colors("&6De klant heeft de server &cverlaten &6tijdens het betaalproces daarom is de betaling geannuleerd."));
		} else if (reason == Reason.SHOPKEEPER_LEFT) {
			to.sendMessage(Text.colors("&6De winkelier heeft de server &cverlaten &6tijdens het betaalproces daarom is de betaling geannuleerd."));
		} else if (reason == Reason.CANCELLED) {
			to.sendMessage(Text.colors("&6Je hebt het pinverzoek succesvol geannuleerd."));
			from.sendMessage(Text.colors("&6De klant &c" + to.getName() + " &6heeft het pinverzoek geweigerd."));
		} else {
			from.sendMessage(Text.colors("&cDe betaling is wegens een onbekende reden geannuleerd, neem contact op met Maiky#0001"));
			to.sendMessage(Text.colors("&cDe betaling is wegens een onbekende reden geannuleerd, neem contact op met Maiky#0001"));
		}

		this.finished = true;

		try {
			terminable.close();
		} catch (CompositeClosingException e) {
			e.printStackTrace();
		}
	}

	enum State {
		CHAT, CLICK;
	}

	enum Reason {
		SUCCESS,CANCELLED,INSUFFICIENT_BALANCE,CLIENT_LEFT,SHOPKEEPER_LEFT,ERROR;
	}

}
