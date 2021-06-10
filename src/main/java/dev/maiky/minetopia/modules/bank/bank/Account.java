package dev.maiky.minetopia.modules.bank.bank;

import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.BankManager;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Door: Maiky
 * Info: Minetopia - 30 May 2021
 * Package: dev.maiky.minetopia.modules.bank.bank
 */

public class Account {

	@Getter @Setter
	private int id;
	@Getter
	private final HashMap<UUID, List<Permission>> permissions = new HashMap<>();
	@Getter @Setter
	private String customName = "???";
	@Getter
	private final long createdOn;
	@Getter
	private final Bank bank;
	@Getter
	private double balance = 0;

	public Account(Bank bank, int id) {
		this.id = id;
		this.bank = bank;
		this.createdOn = System.currentTimeMillis();
	}

	public void deposit(double d) {
		this.balance += d;
		this.save();
	}

	public boolean withdraw(double d) {
		if ((this.balance - d) < 0)
			return false;
		this.balance -= d;
		this.save();
		return true;
	}

	private void save() {
		BankManager.with(DataModule.getInstance().getSqlHelper()).saveAccount(this);
	}

}
