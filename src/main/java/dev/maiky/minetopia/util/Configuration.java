package dev.maiky.minetopia.util;

import me.lucko.helper.text3.Text;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Door: Maiky
 * Info: Minetopia - 21 May 2021
 * Package: dev.maiky.minetopia.util
 */

public class Configuration {

	private final File file;
	private YamlConfiguration configuration;
	private final String fileName;

	private final JavaPlugin plugin;

	public Configuration(JavaPlugin plugin, String fileName) {
		this.file = new File(plugin.getDataFolder() + "/" + fileName);
		this.fileName = fileName;
		this.plugin = plugin;
	}

	public void load() {
		// If file does not exist then let Bukkit create the default file if it exists in the jar file
		if (!this.file.exists()) {
			this.plugin.saveResource(this.fileName, true);
		}

		// Load configuration
		this.configuration = YamlConfiguration.loadConfiguration(this.file);
		this.configuration.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(this.plugin.getResource(this.fileName))));
		this.configuration.options().copyDefaults(true);
	}

	public YamlConfiguration get() {
		return this.configuration;
	}

	public void save() {
		// Try and save the configuration catch if an error occurs
		try {
			this.configuration.save(file);
		} catch (IOException exception) {
			Bukkit.getLogger().warning(Text.colorize(String.format("Something went wrong saving the configuration %s", this.file.toString())));
		}
	}

	public void reload() {
		// Try and reload the configuration catch if an error occurs
		try {
			this.configuration.load(this.file);
		} catch (IOException | InvalidConfigurationException exception) {
			Bukkit.getLogger().warning(Text.colorize(String.format("Something went wrong loading the the configuration %s", this.file.toString())));
		}
	}

}
