package dev.maiky.minetopia;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.RegisteredCommand;
import com.google.common.collect.SetMultimap;
import dev.maiky.minetopia.modules.bags.BagsModule;
import dev.maiky.minetopia.modules.bank.BankModule;
import dev.maiky.minetopia.modules.chat.ChatModule;
import dev.maiky.minetopia.modules.colors.ColorsModule;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.ddgitems.DDGItemsModule;
import dev.maiky.minetopia.modules.discord.DiscordModule;
import dev.maiky.minetopia.modules.districts.DistrictsModule;
import dev.maiky.minetopia.modules.guns.GunsModule;
import dev.maiky.minetopia.modules.items.ItemsModule;
import dev.maiky.minetopia.modules.levels.LevelsModule;
import dev.maiky.minetopia.modules.players.PlayersModule;
import dev.maiky.minetopia.modules.plots.PlotsModule;
import dev.maiky.minetopia.modules.prefixes.PrefixesModule;
import dev.maiky.minetopia.modules.security.SecurityModule;
import dev.maiky.minetopia.modules.transportation.TransportationModule;
import dev.maiky.minetopia.modules.upgrades.UpgradesModule;
import dev.maiky.minetopia.util.Configuration;
import lombok.Getter;
import me.lucko.helper.Services;
import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.plugin.ap.Plugin;
import me.lucko.helper.plugin.ap.PluginDependency;
import me.lucko.helper.profiles.ProfileRepository;
import me.lucko.helper.text3.Text;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.HashMap;

@Plugin(
		name = "Minetopia",
		version = "1.0.0",
		authors = {"Maiky1304"},
		depends = { @PluginDependency(value = "helper", soft = true),
				@PluginDependency(value = "helper-sql", soft = true),
				@PluginDependency(value = "helper-mongo", soft = true),
				@PluginDependency(value = "Citizens", soft = true),
				@PluginDependency(value = "Vault", soft = true) }
)
public final class Minetopia extends ExtendedJavaPlugin {

	// Here are all the loaded modules stored
	@Getter
	private final HashMap<String, MinetopiaModule> loadedModules = new HashMap<>();

	// Configurations
	@Getter
	private Configuration configuration, migrations;

	// Modules
	public PlayersModule playersModule;
	public DataModule dataModule;
	public ChatModule chatModule;
	public UpgradesModule upgradesModule;
	public ItemsModule itemsModule;
	public PlotsModule plotsModule;
	public LevelsModule levelsModule;
	public DistrictsModule districtsModule;
	public SecurityModule securityModule;
	public ColorsModule colorsModule;
	public PrefixesModule prefixesModule;
	public TransportationModule transportationModule;
	public DDGItemsModule ddgItemsModule;
	public BagsModule bagsModule;
	public BankModule bankModule;
	public GunsModule gunsModule;
	public DiscordModule discordModule;

	// Command Manager
	@Getter
	private BukkitCommandManager commandManager;

	// Profile Repository
	@Getter
	private ProfileRepository repository;

	// Economy
	@Getter
	private static Economy economy;

	@Override
	protected void enable() {
		// Line
		getLogger().info(Text.colorize("&3----------------------------------------------------------------------"));
		getLogger().info(" ");
		getLogger().info("                  §b§lMINETOPIA §3v" + getDescription().getVersion() + " §7by §bMaiky1304");
		getLogger().info(" ");
		getLogger().info(Text.colorize("&3----------------------------------------------------------------------"));

		// Check helper
		if (!getServer().getPluginManager().isPluginEnabled("helper")) {
			getLogger().warning("The plugin helper is not loaded install this plugin by downloading it at https://ci.lucko.me/job/helper/");
			this.setEnabled(false);
			return;
		}

		if (!setupEconomy() ) {
			getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			this.setEnabled(false);
			return;
		}

		if (!getServer().getPluginManager().isPluginEnabled("Citizens")) {
			getLogger().warning("The plugin Citizens is not loaded install the plugin by downloading it at https://dev.bukkit.org/projects/citizens/files/2456650/download");
			this.setEnabled(false);
			return;
		}

		// Initialize the configurations
		this.configuration = new Configuration(this, "config.yml");
		this.migrations = new Configuration(this, "database/migrations.yml");

		// Initialize Repository
		this.repository = Services.load(ProfileRepository.class);

		// Initialize the command manager
		this.commandManager = new BukkitCommandManager(this);

		// Load the configurations
		this.getConfiguration().load();
		this.getMigrations().load();

		// Verification
		//try {
		//			new Verification(this, this.getConfiguration().get().getString("license"));
		//		} catch (IOException exception) {
		//			this.setEnabled(false);
		//			return;
		//		}

		getLogger().info(Text.colorize("&3----------------------------------------------------------------------"));

		// Initialize the modules
		this.playersModule = new PlayersModule();
		this.dataModule = new DataModule(this.configuration);
		this.chatModule = new ChatModule();
		this.upgradesModule = new UpgradesModule();
		this.itemsModule = new ItemsModule();
		this.plotsModule = new PlotsModule();
		this.levelsModule = new LevelsModule();
		this.districtsModule = new DistrictsModule();
		this.securityModule = new SecurityModule();
		this.colorsModule = new ColorsModule();
		this.prefixesModule = new PrefixesModule();
		this.transportationModule = new TransportationModule();
		this.ddgItemsModule = new DDGItemsModule();
		this.bagsModule = new BagsModule();
		this.bankModule = new BankModule();
		this.gunsModule = new GunsModule();
		this.discordModule = new DiscordModule();

		// Load all the modules
		getLogger().info(" §b§lLOADING MODULES >>");
		this.loadModules(this.dataModule, this.playersModule, this.chatModule, this.upgradesModule, this.itemsModule,
				this.plotsModule, this.levelsModule, this.districtsModule, this.securityModule, this.colorsModule,
				this.prefixesModule, this.transportationModule, this.ddgItemsModule, this.bagsModule, this.bankModule,
				this.discordModule, this.gunsModule);

		// Line
		getLogger().info(Text.colorize("&3----------------------------------------------------------------------"));
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		economy = rsp.getProvider();
		return economy != null;
	}

	@Override
	protected void disable() {
		// Disable all modules
		getLogger().info(Text.colorize("&3----------------------------------------------------------------------"));
		for (MinetopiaModule module : this.loadedModules.values()) {
			this.getLogger().info(Text.colorize(String.format("     %s Module %s was succesfully unloaded!", Text.colorize("&c&l-&r"), Text.colorize("&b&l" + module.getName() + "&r"))));
		}
		getLogger().info(Text.colorize("&3----------------------------------------------------------------------"));

		this.loadedModules.values().forEach(MinetopiaModule::disable);
		this.loadedModules.clear();
	}

	/**
	 * Load a module
	 * @param module the instance of the module
	 */
	public void loadModule(MinetopiaModule module) {
		this.loadedModules.put(module.getName(), module);
		module.enable();
	}

	/**
	 * Load multiple modules at once
	 * @param modules the instances of the modules
	 */
	public void loadModules(MinetopiaModule... modules) {
		for (MinetopiaModule module : modules) {
			this.getLogger().info(Text.colorize(String.format("     %s Module %s has succesfully loaded!", Text.colorize("&a&l+&r"), Text.colorize("&b&l" + module.getName() + "&r"))));
		}

		getLogger().info(Text.colorize("&3----------------------------------------------------------------------"));

		for (MinetopiaModule module : modules) {
			this.loadModule(module);
		}
	}

	/**
	 * Reload all loaded modules
	 */
	public void reloadModules() {
		this.loadedModules.values().forEach(MinetopiaModule::reload);
	}

	/**
	 * Default Help
	 */
	public static void showHelp(CommandSender issuer, BaseCommand baseCommand, SetMultimap<String, RegisteredCommand> multimap) {
		issuer.sendMessage(String.format(dev.maiky.minetopia.util.Text.colors("&6/%s <subcommand> <arg>..."), baseCommand.getExecCommandLabel()));
		for (String subCommand : multimap.keys()) {
			RegisteredCommand rc = multimap.get(subCommand).iterator().next();
			if (rc.getHelpText() == null || rc.getHelpText().length()==1 || rc.getHelpText().length()==0)
				continue;
			if (rc.getRequiredPermissions().size() != 0) {
				boolean failed = false;
				for (Object s : rc.getRequiredPermissions()) {
					if (!issuer.hasPermission(s.toString())) {
						failed = true;
					}
				}
				if (failed) continue;
			}
			String message = "§a/%s §2%s §a%s§f- §a%s";
			issuer.sendMessage(String.format(dev.maiky.minetopia.util.Text.colors(message),
					baseCommand.getExecCommandLabel(),
					rc.getPrefSubCommand().equals(" ") ? "" : rc.getPrefSubCommand(),
					rc.getSyntaxText().equals(" ") ? "" : rc.getSyntaxText() + " ",
					rc.getHelpText().equals(" ") ? "" : rc.getHelpText()));
		}
	}

}
