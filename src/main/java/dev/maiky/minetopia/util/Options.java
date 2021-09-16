package dev.maiky.minetopia.util;

import dev.maiky.minetopia.Minetopia;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.List;

public enum Options {

	MODULES_ADDONS(false, false, true),
	MODULES_BAGS(true),
	MODULES_BANK(true),
	MODULES_BOOSTERS(true),
	MODULES_CHAT(true),
	MODULES_COLORS(true),
	MODULES_DATA(true),
	MODULES_DDGITEMS(true),
	MODULES_DISCORD(false),
	MODULES_DISTRICTS(true),
	MODULES_GUNS(true),
	MODULES_ITEMS(true),
	MODULES_LEVELS(true),
	MODULES_NOTIFICATIONS(true),
	MODULES_PLAYERS(true),
	MODULES_PLOTS(true),
	MODULES_PREFIXES(true),
	MODULES_SCRIPTS(false, false, true),
	MODULES_SECURITY(true),
	MODULES_TRANSPORTATION(true),
	MODULES_UPGRADES(true),

	SCOREBOARD_TITLE("MINETOPIA"),
	SCOREBOARD_FALLBACKCOLOR("a"),

	DDGITEMS_AUTOUPDATE(true),
	DDGITEMS_CUSTOM_REPOSITORY("none"),

	PLAYER_DEFAULT_PREFIX("Burger"),
	PLAYER_DEFAULT_GRAYSHARDS(0.0),
	PLAYER_DEFAULT_GOLDSHARDS(0.0),
	PLAYER_DEFAULT_LEVEL(1),
	PLAYER_DEFAULT_CITYCOLOR("7"),
	PLAYER_DEFAULT_UPGRADES_POINTS(1),

	TIME_DAILYUPGRADETOKEN(true),
	TIME_SHARDPAYOUTENABLED(true),
	TIME_GRAYSHARDS_PER10MIN(1.0),
	TIME_GOLDSHARDS_PER10MIN(0.4),
	TIME_LOANENABLED(true),
	TIME_LOANINCREASEPERLVL(2500.0),

	PLOTS_MINIMUMPRIORITY(0),

	BAGS_DEFAULT_LORE("Officiële Minetopia Koffer"),
	BAGS_STACK(false),

	PORTALS_SIGNTAG("&f[&2T&aeleporter&f]"),

	POLICE_112COOLDOWN_LENGTH(10),
	POLICE_112COOLDOWN_TYPE("seconds"),

	SECURITY_DETECTOR_BLOCK(Material.WOOL, true),
	SECURITY_DETECTOR_CHECKFOR(15),
	SECURITY_DETECTOR_DETECTION(14),
	SECURITY_DETECTOR_BAGTOCHECK(4),
	SECURITY_DETECTOR_GOODTOGO(13),
	SECURITY_DETECTOR_SIGNTAG("[DETECTOR]");

	private final Object value;
	private final boolean mapAsString;
	private final boolean experimental;

	Options(Object defaultValue) {
		this.value = defaultValue;
		this.mapAsString = false;
		this.experimental = false;
	}

	Options(Object defaultValue, boolean mapAsString) {
		this.value = defaultValue;
		this.mapAsString = mapAsString;
		this.experimental = false;
	}

	Options(Object defaultValue, boolean mapAsString, boolean experimental) {
		this.value = defaultValue;
		this.mapAsString = mapAsString;
		this.experimental = experimental;
	}

	public Option<Boolean> asBoolean() {
		return new Option<>((Boolean) optionsCache.get(this));
	}

	public Option<Integer> asInt() {
		return new Option<>((Integer) optionsCache.get(this));
	}

	public Option<List<String>> asList() {
		return new Option<>((List<String>) optionsCache.get(this));
	}

	public Option<Double> asDouble() {
		return new Option<>((Double) optionsCache.get(this));
	}

	public Option<String> asString() {
		return new Option<>((String) optionsCache.get(this));
	}

	public Option<Material> asMaterial() { return new Option<>(Material.valueOf(String.valueOf(this.asString().get()))); }

	public Option<Location> asLocation() {
		return new Option<>((Location) optionsCache.get(this));
	}

	public Option<Float> asFloat() {
		return new Option<>((Float) optionsCache.get(this));
	}

	public Option<Byte> asByte() {
		return new Option<>((Byte) optionsCache.get(this));
	}

	public Option<Object> asObject() {
		return new Option<>((Object) optionsCache.get(this));
	}

	public Option<?> asRaw() {
		return new Option<>(optionsCache.get(this));
	}

	public static class Option<T> {
		T object;

		public Option(T object) {
			this.object = object;
		}

		public T get() {
			return object;
		}
	}

	private static HashMap<Options, Object> optionsCache = new HashMap<>();

	public static void loadAll() {
		optionsCache.clear();

		Configuration configuration = Minetopia.getInstance().getConfiguration();
		ConfigurationSection section = configuration.get().contains("settings") ?
				configuration.get().getConfigurationSection("settings") : configuration.get().createSection("settings");

		for (Options options : Options.values()) {
			String yamlPath = options.toString().toLowerCase().replaceAll("_", ".");
			final Object value = options.mapAsString ? options.value.toString() : options.value;

			if (section.contains(yamlPath)) {
				Object object = section.get(yamlPath);
				Class<?> clazz = object.getClass();

				if (clazz != options.value.getClass()) {
					if (!(clazz.equals(String.class) && options.mapAsString)) {
						Minetopia.getInstance().getLogger().warning("De waarde van de setting op " + yamlPath + " is " + clazz.toString() + " en moet " + options.value.getClass().toString() + " zijn!");
						continue;
					}
				}

				try {
					optionsCache.put(options, section.get(yamlPath));
				} catch (Exception exception) {
					section.set(yamlPath, value);
					optionsCache.put(options, options.value);
				}
			} else {
				if (options.experimental) {
					optionsCache.put(options, false);
					continue;
				}

				section.set(yamlPath, value);
				optionsCache.put(options, options.value);
			}
		}

		configuration.save();
	}

}
