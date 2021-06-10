package dev.maiky.minetopia.modules.data.managers;

import com.google.gson.Gson;
import dev.maiky.minetopia.modules.bank.bank.Account;
import dev.maiky.minetopia.modules.bank.bank.Bank;
import lombok.SneakyThrows;
import me.lucko.helper.sql.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Door: Maiky
 * Info: Minetopia - 30 May 2021
 * Package: dev.maiky.minetopia.modules.data.managers
 */

public class BankManager {

	private final Sql sql;
	private final Gson gson;

	public static BankManager with(Sql sql) {
		return new BankManager(sql);
	}

	private BankManager(Sql sql) {
		this.sql = sql;
		this.gson = new Gson();
	}

	public Account createAccount(Bank bank) {
		Account account = new Account(bank, -1);
		String json = this.gson.toJson(account);

		this.sql.execute(String.format("INSERT INTO `banking_%s`(`json`) VALUES(?)", bank.toString().toLowerCase()),
				preparedStatement -> preparedStatement.setString(1, json));
		return this.sql.query(String.format("SELECT max(id) FROM `banking_%s`", bank.toString().toLowerCase()), preparedStatement -> {
				},
				resultSet ->
				{
					if ( resultSet.next() ) {
						int id = resultSet.getInt(1);
						account.setId(id);
					}

					this.saveAccount(account);

					return account;
				}).orElse(account);
	}

	public List<Account> filterAndGet(Bank bank, UUID uuid) {
		List<Account> list = new ArrayList<>();
		for (Account account : this.allAccounts(bank)) {
			if ( account.getPermissions().containsKey(uuid) )
				list.add(account);
		}
		return list;
	}

	public Account filterAndGet(Bank bank, int id) {
		for (Account account : this.allAccounts(bank)) {
			if ( account.getId() == id )
				return account;
		}
		return null;
	}

	public Account getAccount(Bank bank, int id) {
		for (Account account : this.allAccounts(bank)) {
			if ( account.getId() == id )
				return account;
		}
		return null;
	}

	public void deleteAccount(Bank bank, int id) {
		this.sql.execute(String.format("DELETE FROM `banking_%s` WHERE `id`=?", bank.toString().toLowerCase()),
				preparedStatement -> preparedStatement.setInt(1, id));
	}

	public void deleteAccount(Account account) {
		this.deleteAccount(account.getBank(), account.getId());
	}

	@SneakyThrows
	public void saveAccount(Account account) {
		if (account.getId() < 0)
			throw new IllegalAccessException("Account ID cannot be lower than zero.");

		Bank bank = account.getBank();
		String json = this.gson.toJson(account);

		this.sql.execute(String.format("UPDATE `banking_%s` SET `json`=? WHERE `id`=?", bank.toString().toLowerCase()),
				preparedStatement ->
				{
					preparedStatement.setString(1, json);
					preparedStatement.setInt(2, account.getId());
				});
	}

	public void processAccountMaps(Account account) {
		List<UUID> remove = new ArrayList<>();
		for (UUID u : account.getPermissions().keySet()) {
			if (account.getPermissions().get(u).isEmpty())
				remove.add(u);
		}
		remove.forEach(account.getPermissions()::remove);
	}

	public List<Account> allAccounts(Bank bank) {
		List<Account> accounts = new ArrayList<>();

		return this.sql.query(String.format("SELECT `json` FROM `banking_%s` WHERE 1", bank.toString().toLowerCase()),
				preparedStatement -> {},
				resultSet ->
				{
					while(resultSet.next()) {
						String json = resultSet.getString("json");
						Account account = this.gson.fromJson(json, Account.class);
						accounts.add(account);
					}

					return accounts;
				}).orElse(accounts);

	}

}
