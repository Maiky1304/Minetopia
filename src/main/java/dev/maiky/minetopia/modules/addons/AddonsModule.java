package dev.maiky.minetopia.modules.addons;

import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.addons.addon.Addon;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Door: Maiky
 * Info: Minetopia - 13 Jun 2021
 * Package: dev.maiky.minetopia.modules.addons
 */

public class AddonsModule implements MinetopiaModule {

	private List<Addon> addons = new ArrayList<>();

	private final CompositeTerminable composite = CompositeTerminable.create();
	private boolean enabled;

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	public void reload() {
		this.disable();
		this.enable();
	}

	@Override
	public void enable() {
		this.enabled = true;

		Minetopia minetopia = Minetopia.getPlugin(Minetopia.class);
		File addons = new File(minetopia.getDataFolder() + "/addons");
		if (!addons.exists())
			addons.mkdir();
		File[] files = addons.listFiles();
		for (File file : files) {
			if (!file.toString().endsWith(".jar")) continue;
			try {
				JarFile jarFile = new JarFile(file);
				Enumeration<JarEntry> entries = jarFile.entries();

				String mainClass = null, addonName = null;

				while(entries.hasMoreElements()) {
					JarEntry element = entries.nextElement();
					if (element.getName().equalsIgnoreCase("addon.yml")) {
						BufferedReader reader = new BufferedReader(new InputStreamReader(jarFile.getInputStream(element)));
						YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(reader);
						if (!yamlConfiguration.contains("main")) {
							Bukkit.getLogger().warning("The addon " + yamlConfiguration.getString("name") + " misses the main class key in the addon.yml");
							reader.close();
							break;
						}

						mainClass = yamlConfiguration.getString("main");
						addonName = yamlConfiguration.getString("name");
						reader.close();
						break;
					}
				}

				if (mainClass == null)
					continue;

				jarFile.close();

				ClassLoader loader = URLClassLoader.newInstance(new URL[]{ file.toURI().toURL() }, minetopia.getClass().getClassLoader());
				Class<?> clazz = Class.forName(mainClass, true, loader);
				for (Class<?> subclazz : clazz.getClasses()) {
					Class.forName(subclazz.getName(), true, loader);
				}

				Class<? extends Addon> typeClass = clazz.asSubclass(Addon.class);
				Addon addon = typeClass.newInstance();
				addon.enable();
				this.addons.add(addon);
				Bukkit.getLogger().info("Succesfully loaded the addon " + addonName);
			} catch (IOException | ClassNotFoundException | IllegalAccessException | InstantiationException exception) {
				exception.printStackTrace();
			}
		}
	}


	@Override
	public void disable() {
		this.enabled = false;

		this.addons.forEach(Addon::disable);

		try {
			this.composite.close();
		} catch (CompositeClosingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return "Addons";
	}
}
