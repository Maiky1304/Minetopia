package dev.maiky.minetopia.modules.bank.bank;

import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.mongo.MongoBankManager;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Door: Maiky
 * Info: Minetopia - 30 May 2021
 * Package: dev.maiky.minetopia.modules.bank.bank
 */

@Entity(value = "bank_accounts", noClassnameStored = true)
public class Account {

	@Id
	public ObjectId id;

	@Getter @Setter
	@Property("account_id")
	@Indexed(options = @IndexOptions(unique = true))
	public int accountId;

	@Getter
	public final HashMap<UUID, List<Permission>> permissions = new HashMap<>();

	@Getter @Setter
	@Property("display_name")
	public String customName = "???";

	@Getter @Setter
	@Property("creation_date")
	public long createdOn;

	@Getter @Setter
	public Bank bank;

	@Getter
	public double balance = 0;

	public Account() {}

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

	public void save() {
		MongoBankManager mongoBankManager = DataModule.getInstance().getBankManager();
		mongoBankManager.save(this);
	}

}
