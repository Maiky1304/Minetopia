package dev.maiky.minetopia.modules.bank.manager;

import dev.maiky.minetopia.modules.bank.bank.Bank;
import dev.maiky.minetopia.modules.bank.bank.Console;
import dev.maiky.minetopia.modules.bank.bank.PinRequest;
import dev.maiky.minetopia.util.Configuration;
import lombok.Getter;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Door: Maiky
 * Info: Minetopia - 20 Jun 2021
 * Package: dev.maiky.minetopia.modules.bank.manager
 */

public class PinManager {

	@Getter
	private final List<PinRequest> pinRequests = new ArrayList<>();

	private final Configuration configuration;

	public PinManager(Configuration configuration) {
		this.configuration = configuration;
	}

	public int incrementAndGet() {
		int lastId = this.configuration.get().getInt("last-id") + 1;
		this.configuration.get().set("last-id", lastId);
		this.configuration.save();
		return lastId;
	}

	public void insert(Console console) {
		this.configuration.get().set(String.valueOf(incrementAndGet()), console.section());
		this.configuration.save();
	}

	public void delete(Location location) {
		Console console = find(location);
		int id = findId(console);
		if (id == -1) return;

		this.configuration.get().set(String.valueOf(id), null);
		this.configuration.save();
	}

	public void delete(int id) {
		this.configuration.get().set(String.valueOf(id), null);
		this.configuration.save();
	}

	public void delete(Console console) {
		int id = findId(console);
		if (id == -1) return;

		this.configuration.get().set(String.valueOf(id), null);
		this.configuration.save();
	}

	public void saveConsole(Console console) {
		int id = findId(console);
		if (id == -1) return;

		this.configuration.get().set(String.valueOf(id), console.section());
		this.configuration.save();
	}

	public Console find(Location location) {
		for (Console console : list()) {
			if (console.getLocation().equals(location))
				return console;
		}
		return null;
	}

	public Console find(Bank bank, int id) {
		for (Console console : list()) {
			if (console.getAccountNumber() == id && console.getAccountType().equals(bank))
				return console;
		}

		return null;
	}

	public int findId(Console console) {
		for (int i : keyValueMap().keySet()) {
			if (keyValueMap().get(i).equals(console))
				return i;
		}
		return -1;
	}

	public HashMap<Integer, Console> keyValueMap() {
		HashMap<Integer, Console> consoleHashMap = new HashMap<>();
		for (String key : this.configuration.get().getKeys(false)) {
			if (key.equals("last-id")) continue;

			int id = Integer.parseInt(key);

			Console console = Console.ofSection(this.configuration.get().getConfigurationSection(key));
			consoleHashMap.put(id, console);
		}
		return consoleHashMap;
	}

	public List<Console> list() {
		List<Console> consoleList = new ArrayList<>();
		for (String key : this.configuration.get().getKeys(false)) {
			if (key.equals("last-id")) continue;

			Console console = Console.ofSection(this.configuration.get().getConfigurationSection(key));
			consoleList.add(console);
		}
		return consoleList;
	}

}
