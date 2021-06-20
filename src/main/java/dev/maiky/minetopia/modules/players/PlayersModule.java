package dev.maiky.minetopia.modules.players;

import co.aikar.commands.ConditionFailedException;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.PlayerManager;
import dev.maiky.minetopia.modules.levels.manager.LevelCheck;
import dev.maiky.minetopia.modules.players.classes.MinetopiaScoreboard;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.modules.players.commands.essential.EmergencyCommand;
import dev.maiky.minetopia.modules.players.commands.essential.HeadCommand;
import dev.maiky.minetopia.modules.players.commands.essential.ShardCommand;
import dev.maiky.minetopia.modules.players.commands.essential.TimeCommand;
import dev.maiky.minetopia.modules.players.commands.staff.AdminToolCommand;
import dev.maiky.minetopia.modules.players.commands.staff.ModCommand;
import dev.maiky.minetopia.modules.players.placeholders.NameColorPlaceholder;
import dev.maiky.minetopia.modules.players.tasks.SaveTask;
import dev.maiky.minetopia.modules.players.tasks.TimeTask;
import dev.maiky.minetopia.modules.players.ui.AdminToolUI;
import dev.maiky.minetopia.modules.security.commands.BodySearchCommand;
import lombok.Getter;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.bucket.Bucket;
import me.lucko.helper.bucket.factory.BucketFactory;
import me.lucko.helper.bucket.partitioning.PartitioningStrategies;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.cooldown.CooldownMap;
import me.lucko.helper.profiles.ProfileRepository;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
		Minetopia minetopia = Minetopia.getPlugin(Minetopia.class);
		ConfigurationSection section = minetopia.getConfiguration().get().getConfigurationSection("settings.city");

		this.cityName = section.getString("title");
		this.cityColor = section.getString("color");
		this.layout = section.getStringList("layout");
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
		NameColorPlaceholder nameColorPlaceholder = new NameColorPlaceholder();
		nameColorPlaceholder.register();
	}

	private void registerCommands() {
		final Minetopia minetopia = Minetopia.getPlugin(Minetopia.class);

		minetopia.getCommandManager().getCommandConditions().addCondition("MTUser", context -> {
			if (!context.getIssuer().isPlayer()) throw new ConditionFailedException("Dit kan niet vanuit de console.");

			Player player = context.getIssuer().getPlayer();
			UUID uuid = player.getUniqueId();

			if (!PlayerManager.getCache().containsKey(uuid)) throw new ConditionFailedException("Je playerdata is nog niet ingeladen!");
		});

		minetopia.getCommandManager().getCommandConditions().addCondition(Integer.class, "limits", (c, exec, value) -> {
			if (value == null) {
				return;
			}
			if (c.hasConfig("min") && c.getConfigValue("min", 0) > value) {
				throw new ConditionFailedException("De invoer moet minimaal " + c.getConfigValue("min", 0) + " zijn.");
			}
			if (c.hasConfig("max") && c.getConfigValue("max", 0) < value) {
				throw new ConditionFailedException("De invoer mag maximaal " + c.getConfigValue("max", 0) + " zijn.");
			}
		});

		minetopia.getCommandManager().getCommandConditions().addCondition(OfflinePlayer.class, "online", (context, execContext, value) -> {
			if (!value.isOnline()) throw new ConditionFailedException("Deze speler is niet online!");
		});

		minetopia.getCommandManager().getCommandConditions().addCondition(String.class, "database", (c, exec, value) -> {
			if (value.equals("self")) return;
			ProfileRepository repository = minetopia.getRepository();
			if (value.length() == 32 || value.length() == 28) {
				if (!repository.getProfile(UUID.fromString(value)).getName().isPresent())
					repository.lookupProfile(UUID.fromString(value));
				if (!repository.getProfile(UUID.fromString(value)).getName().isPresent()) throw new ConditionFailedException("Deze speler bestaat niet in de database.");
			} else {
				if (!repository.getProfile(value).isPresent())
					repository.lookupProfile(value);
				if (!repository.getProfile(value).isPresent()) throw new ConditionFailedException("Deze speler bestaat niet in de database.");
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
			if (value.getInventory().getItemInMainHand() == null) throw new ConditionFailedException("Je hebt geen item in je hand!");
			if (!allowedMaterials.contains(value.getInventory().getItemInMainHand().getType())) throw new ConditionFailedException("Je kunt dit item niet op je hoofd zetten!");
		});

		minetopia.getCommandManager().registerCommand(new ModCommand());
		minetopia.getCommandManager().registerCommand(new TimeCommand(), true);
		minetopia.getCommandManager().registerCommand(new ShardCommand());
		minetopia.getCommandManager().registerCommand(new HeadCommand(), true);
		minetopia.getCommandManager().registerCommand(new EmergencyCommand());
		minetopia.getCommandManager().registerCommand(new AdminToolCommand());
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
		Events.subscribe(PlayerJoinEvent.class)
				.filter(e -> !playerManager.exists(e.getPlayer().getUniqueId()))
				.handler(e -> {
					Player player = e.getPlayer();
					MinetopiaUser user = new MinetopiaUser(player.getUniqueId(), player.getName());
					playerManager.create(user);
					LevelCheck check = new LevelCheck(user);
					int points = check.calculatePoints();
					user.setLevelPoints(points);
					PlayerManager.getCache().put(user.getUuid(), user);

					MinetopiaScoreboard minetopiaScoreboard = new MinetopiaScoreboard(player);
					minetopiaScoreboard.initialize();
					PlayerManager.getScoreboard().put(player.getUniqueId(), minetopiaScoreboard);
				}).bindWith(composite);
		Events.subscribe(PlayerJoinEvent.class)
				.filter(e -> playerManager.exists(e.getPlayer().getUniqueId()))
				.handler(e -> {
					Player player = e.getPlayer();
					MinetopiaUser user = playerManager.retrieve(player.getUniqueId());
					if (user == null) {
						player.kickPlayer("§cMinetopia: Oops! Something went wrong please contact a developer.");
						return;
					}
					LevelCheck check = new LevelCheck(user);
					int points = check.calculatePoints();
					user.setLevelPoints(points);
					PlayerManager.getCache().put(player.getUniqueId(), user);

					MinetopiaScoreboard minetopiaScoreboard = new MinetopiaScoreboard(player);
					minetopiaScoreboard.initialize();
					PlayerManager.getScoreboard().put(player.getUniqueId(), minetopiaScoreboard);
				}).bindWith(composite);
		Events.subscribe(PlayerQuitEvent.class)
				.filter(e -> PlayerManager.getCache().containsKey(e.getPlayer().getUniqueId()))
				.handler(e -> {
					playerManager.update(PlayerManager.getCache().get(e.getPlayer().getUniqueId()));
					PlayerManager.getCache().remove(e.getPlayer().getUniqueId());
				}).bindWith(composite);
		Events.subscribe(PlayerQuitEvent.class)
				.filter(e -> PlayerManager.getScoreboard().containsKey(e.getPlayer().getUniqueId()))
				.handler(e -> {
					PlayerManager.getScoreboard().get(e.getPlayer().getUniqueId()).getPlayerBoard().delete();
					PlayerManager.getScoreboard().remove(e.getPlayer().getUniqueId());
				}).bindWith(composite);
		Events.subscribe(PlayerInteractEvent.class)
				.filter(e -> !BodySearchCommand.getBeingSearched().containsKey(e.getPlayer().getUniqueId()))
				.filter(PlayerInteractEvent::hasBlock)
				.filter(e -> e.getClickedBlock().getType() == Material.DROPPER)
				.filter(e -> e.getAction() == Action.RIGHT_CLICK_BLOCK)
				.filter(e -> e.getPlayer().getGameMode() != GameMode.CREATIVE)
				.handler(e -> {
					e.setCancelled(true);
					e.getPlayer().openInventory(Bukkit.createInventory(null, 27, "§4Prullenbak"));
					e.getPlayer().sendMessage("§4PAS OP: §cAlles wat je hierin gooit wordt voor ALTIJD verwijderd!");
				}).bindWith(composite);
		CooldownMap<Player> cooldownMap = CooldownMap.create(Cooldown.of(500, TimeUnit.MILLISECONDS));
		Events.subscribe(PlayerInteractAtEntityEvent.class)
				.filter(e -> e.getHand().equals(EquipmentSlot.HAND))
				.filter(e -> e.getPlayer().getInventory().getItemInMainHand().getType() == Material.NETHER_STAR)
				.filter(e -> cooldownMap.test(e.getPlayer()))
				.handler(e -> {
					e.getPlayer().sendMessage("§6Je opent nu het admintool menu van §c" + e.getRightClicked().getName() + "§6.");
					AdminToolUI adminToolUI = new AdminToolUI(e.getPlayer(), Bukkit.getOfflinePlayer(e.getRightClicked().getUniqueId()));
					adminToolUI.open();
				}).bindWith(composite);
		Events.subscribe(PlayerInteractEvent.class)
				.filter(e -> e.getHand().equals(EquipmentSlot.HAND))
				.filter(PlayerInteractEvent::hasItem)
				.filter(e -> e.getItem().getType() == Material.NETHER_STAR)
				.filter(e -> e.getAction().toString().startsWith("RIGHT"))
				.filter(e -> cooldownMap.test(e.getPlayer()))
				.handler(e -> {
					e.getPlayer().sendMessage("§6Je opent nu het admintool menu van §c" + e.getPlayer().getName() + "§6.");
					AdminToolUI adminToolUI = new AdminToolUI(e.getPlayer(), Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId()));
					adminToolUI.open();
				}).bindWith(composite);
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
		return "Players";
	}
}
