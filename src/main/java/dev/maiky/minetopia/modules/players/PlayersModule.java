package dev.maiky.minetopia.modules.players;

import co.aikar.commands.ConditionFailedException;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaInventory;
import dev.maiky.minetopia.modules.players.classes.MinetopiaScoreboard;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.modules.players.commands.essential.*;
import dev.maiky.minetopia.modules.players.commands.staff.AdminToolCommand;
import dev.maiky.minetopia.modules.players.commands.staff.ModCommand;
import dev.maiky.minetopia.modules.players.listeners.AdminToolListener;
import dev.maiky.minetopia.modules.players.listeners.JoinListener;
import dev.maiky.minetopia.modules.players.listeners.QuitListener;
import dev.maiky.minetopia.modules.players.listeners.TrashbinListener;
import dev.maiky.minetopia.modules.players.listeners.impl.LoadingListener;
import dev.maiky.minetopia.modules.players.placeholders.Placeholders;
import dev.maiky.minetopia.modules.players.tasks.SaveTask;
import dev.maiky.minetopia.modules.players.tasks.TimeTask;
import dev.maiky.minetopia.util.Message;
import dev.maiky.minetopia.util.Options;
import lombok.Getter;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.bucket.Bucket;
import me.lucko.helper.bucket.factory.BucketFactory;
import me.lucko.helper.bucket.partitioning.PartitioningStrategies;
import me.lucko.helper.profiles.ProfileRepository;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Door: Maiky
 * Info: Minetopia - 21 May 2021
 * Package: dev.maiky.minetopia.modules.players
 */

public class PlayersModule implements MinetopiaModule {

	private final CompositeTerminable composite = CompositeTerminable.create();

	private boolean enabled;

	@Getter
	private static PlayersModule instance;

	public PlayersModule() {
		instance = this;
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

		// Register all events
		this.registerEvents();

		// Register all tasks
		this.registerTasks();

		// Register all commands
		this.registerCommands();

		// Initialize Scoreboard Variables
		this.initializeScoreboardVariables();

		// Initalize Player Variables
		this.initializePlayerVariables();

		// Initialize Placeholders
		this.initializePlaceholders();
	}

	@Getter
	private String cityName = "?", cityColor = "f";
	@Getter
	private List<String> layout = Arrays.asList("%empty%", "Stel dit in via de config!");

	private void initializeScoreboardVariables() {
		this.cityName = Options.SCOREBOARD_TITLE.asString().get();
		this.cityColor = Options.SCOREBOARD_FALLBACKCOLOR.asString().get();
		this.layout = Message.COMMON_SCOREBOARD_LINES.formatAsList();
	}

	@Getter
	private int shards, level, fitheid;
	@Getter
	private String city;

	private void initializePlayerVariables() {
		Minetopia minetopia = Minetopia.getPlugin(Minetopia.class);
		ConfigurationSection section = minetopia.getConfiguration().get().getConfigurationSection("settings.player.default");

		this.shards = section.getInt("shards");
		this.level = section.getInt("level");
		this.fitheid = section.getInt("fitness");
		this.city = section.getString("cityColor");
	}

	private void initializePlaceholders() {
		Placeholders nameColorPlaceholder = new Placeholders();
		nameColorPlaceholder.register();
	}

	private void registerCommands() {
		final Minetopia minetopia = Minetopia.getPlugin(Minetopia.class);

		minetopia.getCommandManager().getCommandConditions().addCondition("MTUser", context -> {
			if (!context.getIssuer().isPlayer()) throw new ConditionFailedException(Message.COMMON_ERROR_NOCONSOLE.raw());

			Player player = context.getIssuer().getPlayer();
			UUID uuid = player.getUniqueId();

			if (!PlayerManager.getCache().containsKey(uuid)) throw new ConditionFailedException(Message.COMMON_ERROR_PLAYERDATANOTLOADED.raw());
		});

		minetopia.getCommandManager().getCommandConditions().addCondition(Integer.class, "limits", (c, exec, value) -> {
			if (value == null) {
				return;
			}
			if (c.hasConfig("min") && c.getConfigValue("min", 0) > value) {
				throw new ConditionFailedException(Message.COMMON_ERROR_INPUT_MIN.format(c.getConfigValue("min", 0)));
			}
			if (c.hasConfig("max") && c.getConfigValue("max", 0) < value) {
				throw new ConditionFailedException(Message.COMMON_ERROR_INPUT_MAX.format(c.getConfigValue("max", 0)));
			}
		});

		minetopia.getCommandManager().getCommandConditions().addCondition(OfflinePlayer.class, "online", (context, execContext, value) -> {
			if (!value.isOnline()) throw new ConditionFailedException(Message.COMMON_ERROR_PLAYEROFFLINE.raw());
		});

		minetopia.getCommandManager().getCommandConditions().addCondition(String.class, "database", (c, exec, value) -> {
			if (value.equals("self")) return;
			ProfileRepository repository = minetopia.getRepository();
			if (value.length() == 32 || value.length() == 28) {
				if (!repository.getProfile(UUID.fromString(value)).getName().isPresent())
					repository.lookupProfile(UUID.fromString(value));
				if (!repository.getProfile(UUID.fromString(value)).getName().isPresent()) throw new ConditionFailedException(Message.COMMON_ERROR_PLAYERINVALID.raw());
			} else {
				if (!repository.getProfile(value).isPresent())
					repository.lookupProfile(value);
				if (!repository.getProfile(value).isPresent()) throw new ConditionFailedException(Message.COMMON_ERROR_PLAYERINVALID.raw());
			}
		});

		List<Material> allowedMaterials = Arrays.asList(Material.BEDROCK,
				Material.SPONGE,
				Material.BEDROCK,
				Material.IRON_ORE,
				Material.COAL_ORE,
				Material.SPONGE,
				Material.LAPIS_ORE,
				Material.DIAMOND_ORE,
				Material.REDSTONE_ORE,
				Material.SOUL_SAND,
				Material.NETHERRACK,
				Material.NETHER_BRICK,
				Material.ENDER_STONE,
				Material.QUARTZ_ORE,
				Material.EMERALD_ORE,
				Material.PRISMARINE,
				Material.RED_SANDSTONE,
				Material.INK_SACK,
				Material.MAGMA_CREAM,
				Material.NETHER_BRICK,
				Material.NETHER_STALK,
				Material.RABBIT_HIDE,
				Material.PRISMARINE_SHARD,
				Material.CLAY_BALL,
				Material.PRISMARINE_CRYSTALS,
				Material.NETHER_BRICK,
				Material.GOLD_ORE,
				Material.CARROT_STICK,
				Material.SHEARS,
				Material.GLASS,
				Material.STAINED_GLASS);
		minetopia.getCommandManager().getCommandConditions().addCondition(Player.class, "itemPossibleOnHead", (context, execContext, value) -> {
			if (value.getInventory().getItemInMainHand() == null) throw new ConditionFailedException(Message.COMMON_ERROR_NOITEMINHAND.raw());
			if (!allowedMaterials.contains(value.getInventory().getItemInMainHand().getType())) throw new ConditionFailedException(Message.COMMON_ERROR_HEADIMPOSSIBLE.raw());
		});

		minetopia.getCommandManager().registerCommand(new ModCommand());
		minetopia.getCommandManager().registerCommand(new TimeCommand(), true);
		minetopia.getCommandManager().registerCommand(new GrayShardCommand());
		minetopia.getCommandManager().registerCommand(new GoldShardCommand());
		minetopia.getCommandManager().registerCommand(new HeadCommand(), true);
		minetopia.getCommandManager().registerCommand(new EmergencyCommand());
		minetopia.getCommandManager().registerCommand(new AdminToolCommand());
		minetopia.getCommandManager().registerCommand(new MinetopiaCommand());
	}

	private void registerTasks() {
		Bucket<Player> bucket = BucketFactory.newHashSetBucket(10, PartitioningStrategies.lowestSize());

		Events.subscribe(PlayerJoinEvent.class).handler(e -> bucket.add(e.getPlayer())).bindWith(composite);
		Events.subscribe(PlayerQuitEvent.class).handler(e -> bucket.remove(e.getPlayer())).bindWith(composite);

		PlayerManager playerManager = PlayerManager.with(DataModule.getInstance().getSqlHelper());

		Schedulers.sync().runRepeating(new SaveTask(bucket, playerManager), 20L, 20L).bindWith(composite);
		Schedulers.async().runRepeating(new TimeTask(bucket), 0L, 20L).bindWith(composite);
	}

	private void registerEvents() {
		PlayerManager playerManager = PlayerManager.with(DataModule.getInstance().getSqlHelper());

		this.composite.bindModule(new JoinListener(playerManager));
		this.composite.bindModule(new QuitListener(playerManager));
		this.composite.bindModule(new TrashbinListener());
		this.composite.bindModule(new AdminToolListener());
		this.composite.bindModule(new LoadingListener());
	}

	@Override
	public void disable() {
		this.enabled = false;

		// Emergency Save
		Minetopia.getInstance().getLogger().info("Bezig met het opslaan van " + Bukkit.getServer().getOnlinePlayers().size() + " wegens restart...");
		PlayerManager manager = PlayerManager.with(DataModule.getInstance().getSqlHelper());
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			MinetopiaUser user = PlayerManager.getCache().get(player.getUniqueId());
			if (user == null) continue;
			user.getMinetopiaData().setInventory(MinetopiaInventory.of(player.getInventory()));
			user.getMinetopiaData().setHp(player.getHealth());
			user.getMinetopiaData().setSaturation(player.getFoodLevel());
			user.getMinetopiaData().setBalance(Minetopia.getEconomy().getBalance(player));
			manager.update(user);

			MinetopiaScoreboard scoreboard = PlayerManager.getScoreboard().get(player.getUniqueId());
			scoreboard.update();
		}
		Minetopia.getInstance().getLogger().info("Succesvol " + Bukkit.getServer().getOnlinePlayers().size() + " spelers opgeslagen!");

		try {
			this.composite.close();
		} catch (CompositeClosingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return "Players";
	}
}
