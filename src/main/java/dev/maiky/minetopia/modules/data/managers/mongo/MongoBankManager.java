package dev.maiky.minetopia.modules.data.managers.mongo;

import dev.maiky.minetopia.modules.bank.bank.Account;
import dev.maiky.minetopia.modules.bank.bank.Bank;
import dev.maiky.minetopia.modules.data.managers.AIManager;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

public class MongoBankManager extends AIManager<Account> {

    public MongoBankManager() {
        super(Account.class);
    }

    public Account createAccount(Bank bank) {
        Account account = new Account();
        account.setAccountId(increment());
        account.setBank(bank);
        account.setCreatedOn(System.currentTimeMillis());

        super.save(account);

        return account;
    }

    public Account getAccount(Bank bank, int id) {
        return super.find(account -> account.getBank().equals(bank) && account.getAccountId() == id).findFirst()
                .orElse(null);
    }

}
