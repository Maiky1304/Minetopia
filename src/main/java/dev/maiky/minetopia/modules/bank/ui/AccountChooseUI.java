package dev.maiky.minetopia.modules.bank.ui;

import dev.maiky.minetopia.modules.bank.bank.Account;
import dev.maiky.minetopia.modules.bank.bank.Bank;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.BankManager;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Door: Maiky
 * Info: Minetopia - 04 Jun 2021
 * Package: dev.maiky.minetopia.modules.bank.ui
 */

public class AccountChooseUI extends Gui {

	private final Bank bank;
	private final List<Account> accountList = new ArrayList<>();

	public AccountChooseUI(Player player, Bank bank) {
		super(player, 3, "§3Kies een rekening:");

		this.bank = bank;

		if (bank == Bank.PERSONAL) return;

		BankManager manager = BankManager.with(DataModule.getInstance().getSqlHelper());
		accountList.addAll(manager.filterAndGet(bank, player.getUniqueId()));
	}

	@Override
	public void redraw() {
		if (bank == Bank.PERSONAL) {
			super.addItem(ItemStackBuilder.of(bank.icon).name(getPlayer().getName()).lore("§5(Privé rekening)").build(() -> account(null)));
		} else {
			for (Account account : this.accountList) {
				super.addItem(ItemStackBuilder.of(account.getBank().icon)
				.name(account.getCustomName()).lore("", "§5(ID: " + account.getId() + ")").build(() -> account(account)));
			}
		}
	}

	private void account(Account account) {
		BalanceManageUI balanceManageUI = new BalanceManageUI(getPlayer(), account);
		balanceManageUI.open();
	}

}
