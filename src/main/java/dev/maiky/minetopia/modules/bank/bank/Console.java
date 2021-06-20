package dev.maiky.minetopia.modules.bank.bank;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;

import java.util.Objects;

/**
 * Door: Maiky
 * Info: Minetopia - 20 Jun 2021
 * Package: dev.maiky.minetopia.modules.bank.bank
 */

public class Console {

	public static Console ofSection(ConfigurationSection section) {
		Location location = (Location) section.get("location");
		int accountNumber = section.getInt("accountNumber");
		Bank bank = Bank.valueOf(section.getString("accountType"));
		return new Console(location, accountNumber, bank);
	}

	@Getter
	private final Location location;
	@Getter @Setter
	private int accountNumber;
	@Getter @Setter
	private Bank accountType;

	public Console(Location location, int accountNumber, Bank accountType) {
		this.location = location;
		this.accountNumber = accountNumber;
		this.accountType = accountType;
	}

	public ConfigurationSection section() {
		ConfigurationSection configurationSection = new MemoryConfiguration();
		configurationSection.set("location", this.location);
		configurationSection.set("accountNumber", this.accountNumber);
		configurationSection.set("accountType", this.accountType.toString());
		return configurationSection;
	}

	@Override
	public boolean equals(Object o) {
		if ( this == o ) return true;
		if ( o == null || getClass() != o.getClass() ) return false;
		Console console = (Console) o;
		return accountNumber == console.accountNumber && Objects.equals(location, console.location) && accountType == console.accountType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(location, accountNumber, accountType);
	}
}
