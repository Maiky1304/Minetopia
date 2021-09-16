package dev.maiky.minetopia.modules.data;

import co.aikar.commands.BukkitCommandManager;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.data.commands.ModuleCommand;
import dev.maiky.minetopia.modules.data.enums.StorageType;
import dev.maiky.minetopia.modules.data.managers.mongo.*;
import dev.maiky.minetopia.util.Configuration;
import lombok.Getter;
import me.lucko.helper.Services;
import me.lucko.helper.redis.Redis;
import me.lucko.helper.redis.RedisCredentials;
import me.lucko.helper.redis.RedisProvider;
import me.lucko.helper.sql.DatabaseCredentials;
import me.lucko.helper.sql.Sql;
import me.lucko.helper.sql.SqlProvider;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import me.lucko.helper.text3.Text;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * Door: Maiky
 * Info: Minetopia - 21 May 2021
 * Package: dev.maiky.minetopia.modules.data
 */

public class DataModule implements MinetopiaModule {

	private CompositeTerminable composite = CompositeTerminable.create();

	@Getter
	private static DataModule instance;

	private boolean enabled;
	private final Configuration dataConfiguration;

	private Sql sqlHelper;
	private MongoClient mongoClient;
	private Redis redis;

	@Getter private MongoBagManager bagManager;
	@Getter private MongoBankManager bankManager;
	@Getter private MongoPlayerManager playerManager;
	@Getter private MongoPortalManager portalManager;
	@Getter private MongoWeaponManager weaponManager;

	public DataModule(Configuration dataConfiguration) {
		instance = this;

		this.dataConfiguration = dataConfiguration;
	}

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

		String rawStorageType = this.dataConfiguration.get().getString("storage.type");
		StorageType type;
		try {
			type = StorageType.valueOf(rawStorageType.toUpperCase());
		} catch (IllegalArgumentException exception) {
			Bukkit.getLogger().warning(Text.colorize("! - Invalid storage type provided in config.yml"));
			return;
		}

		// Check if dependecy is in the server
		if (!Bukkit.getPluginManager().isPluginEnabled(type.getAssociatedPlugin())) {
			Bukkit.getLogger().warning(Text.colorize(String.format("! - The plugin %s is not loaded so the plugin will be disabled.", type.getAssociatedPlugin())));
			Bukkit.getPluginManager().disablePlugin(Minetopia.getPlugin(Minetopia.class));
			return;
		}

		this.initializeStorage(type);
		this.registerCommands();
	}

	/**
	 * Register the commands of the module
	 */
	private void registerCommands() {
		final BukkitCommandManager manager = Minetopia.getPlugin(Minetopia.class).getCommandManager();
		manager.registerCommand(new ModuleCommand());
	}

	/**
	 * Start the processes required for the provided storage type
	 * @param type storage type
	 */
	private void initializeStorage(StorageType type) {
		Minetopia minetopia = Minetopia.getPlugin(Minetopia.class);
		YamlConfiguration configuration = minetopia.getConfiguration().get();
		YamlConfiguration migration = minetopia.getMigrations().get();
		ConfigurationSection section = configuration.getConfigurationSection("storage.auth");
		ConfigurationSection redisSection = configuration.getConfigurationSection("storage.redis");
		final String host = section.getString("host"),
		database = section.getString("database"), username = section.getString("username"),
		password = section.getString("password");
		final int port = section.getInt("port");

		if (type.equals(StorageType.MYSQL)) {
			SqlProvider provider = Services.load(SqlProvider.class);
			this.sqlHelper = provider.getSql(DatabaseCredentials.of(host, port,
					database, username, password));
			this.sqlHelper.bindWith(composite);
			for (String key : migration.getKeys(false)) {
				String value = migration.getString(key);
				this.sqlHelper.execute(value);
			}
		} else if (type.equals(StorageType.MONGODB)) {
			this.mongoClient = new MongoClient(new ServerAddress("localhost", 27017));
			this.bagManager = new MongoBagManager();
			this.bankManager = new MongoBankManager();
			this.playerManager = new MongoPlayerManager();
			this.portalManager = new MongoPortalManager();
			this.weaponManager = new MongoWeaponManager();
		}

		RedisProvider redisProvider = Services.load(RedisProvider.class);
		this.redis = redisProvider.getRedis(RedisCredentials.of(redisSection.getString("address"),
				redisSection.getInt("port"), redisSection.getString("password")));
	}

	/**
	 * Get the instance of the mongo client
	 * @return the mongo helper
	 */
	public MongoClient getMongoClient() {
		return mongoClient;
	}

	/**
	 * Get the instance of the sql helper
	 * @return the sql helper
	 */
	public Sql getSqlHelper() {
		return sqlHelper;
	}

	/**
	 * Get the instance of the redis helper
	 * @return the redis helper
	 */
	public Redis getRedis() {
		return redis;
	}

	@Override
	public void disable() {
		this.enabled = false;

		try {
			this.composite.close();
		} catch (CompositeClosingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return "Data";
	}
}
