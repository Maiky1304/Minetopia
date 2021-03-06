package dev.maiky.minetopia.modules.items;

import co.aikar.commands.BukkitCommandManager;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.data.managers.mongo.MongoPlayerManager;
import dev.maiky.minetopia.modules.items.commands.CuffCommand;
import dev.maiky.minetopia.modules.items.drugs.Cocaine;
import dev.maiky.minetopia.modules.items.drugs.Weed;
import dev.maiky.minetopia.modules.items.other.Cooldown;
import dev.maiky.minetopia.modules.items.police.Handcuffs;
import dev.maiky.minetopia.modules.items.police.Radio;
import dev.maiky.minetopia.modules.items.police.Taser;
import dev.maiky.minetopia.modules.items.projectile.Bullet;
import dev.maiky.minetopia.modules.items.projectile.Snowball;
import dev.maiky.minetopia.modules.items.threads.RadioThread;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.util.Configuration;
import dev.maiky.minetopia.util.Message;
import dev.maiky.minetopia.util.Text;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

/**
 * Door: Maiky
 * Info: Minetopia - 23 May 2021
 * Package: dev.maiky.minetopia.modules.weapons
 */

public class ItemsModule implements MinetopiaModule {

	private CompositeTerminable composite = CompositeTerminable.create();

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

		// Weapons
		this.registerWeapons();

		// Interactables
		this.registerInteractables();

		// Projectiles
		this.registerProjectiles();

		// Events
		this.registerEvents();

		// Threads
		this.registerThreads();

		// Commands
		this.registerCommands();
	}

	private void registerCommands() {
		BukkitCommandManager manager = Minetopia.getInstance().getCommandManager();
		manager.registerCommand(new CuffCommand());
	}

	private void registerThreads() {
		RadioThread radioThread = new RadioThread(Minetopia.getPlugin(Minetopia.class).dataModule.getRedis());
		radioThread.start();
	}

	private final HashMap<Material, MinetopiaWeapon> weapons = new HashMap<>();
	private final HashMap<Material, MinetopiaInteractable> interactables = new HashMap<>();
	private final HashMap<EntityType, MinetopiaProjectile> projectiles = new HashMap<>();

	private void registerProjectiles() {
		MinetopiaProjectile[] projectiles = new MinetopiaProjectile[]{new Bullet(), new Snowball()};
		for (MinetopiaProjectile projectile : projectiles)
			this.projectiles.put(projectile.material(), projectile);
	}

	private void registerWeapons() {
		List<MinetopiaWeapon> weaponList = new ArrayList<>();
		Configuration configuration = new Configuration(Minetopia.getPlugin(Minetopia.class), "modules/pvpconfig.yml");
		configuration.load();
		for (String key : configuration.get().getKeys(false)) {
			Material material = Material.valueOf(configuration.get().getString(key + ".material"));
			String name = configuration.get().getString(key + ".name"),
			damagerMessage = configuration.get().getString(key + ".damagerMessage"),
			victimMessage = configuration.get().getString(key + ".victimMessage");
			boolean damage = configuration.get().getBoolean(key + ".damage");

			MinetopiaWeapon weapon;
			if (configuration.get().contains(key + ".cooldown")) {
				weapon = new MinetopiaWeapon() {
					@Override
					public Material material() {
						return material;
					}

					@Override
					public String name() {
						return name;
					}

					@Override
					public String damagerMessage() {
						return damagerMessage;
					}

					@Override
					public String victimMessage() {
						return victimMessage;
					}

					@Override
					public boolean damage() {
						return damage;
					}

					@Override
					public boolean cooldown() {
						return true;
					}

					@Override
					public int length() {
						return configuration.get().getInt(key + ".cooldown.length");
					}

					@Override
					public int unit() {
						return configuration.get().getInt(key + ".cooldown.unit");
					}

					@Override
					public String cooldownMessage() {
						return configuration.get().getString(key + ".cooldown.cooldownMessage");
					}
				};
				weaponList.add(weapon);
			} else {
				weapon = new MinetopiaWeapon() {
					@Override
					public Material material() {
						return material;
					}

					@Override
					public String name() {
						return name;
					}

					@Override
					public String damagerMessage() {
						return damagerMessage;
					}

					@Override
					public String victimMessage() {
						return victimMessage;
					}

					@Override
					public boolean damage() {
						return damage;
					}

					@Override
					public boolean cooldown() {
						return false;
					}

					@Override
					public int length() {
						return 0;
					}

					@Override
					public int unit() {
						return 0;
					}

					@Override
					public String cooldownMessage() {
						return null;
					}
				};
				weaponList.add(weapon);
			}
		}

		weaponList.forEach(minetopiaWeapon -> this.weapons.put(minetopiaWeapon.material(), minetopiaWeapon));
	}

	private void registerInteractables() {
		MinetopiaInteractable[] interactables = new MinetopiaInteractable[]{new Handcuffs(), new Radio(), new Weed(),
		new Cocaine()};
		for (MinetopiaInteractable interactable : interactables)
			this.interactables.put(interactable.material(), interactable);
	}

	private void registerEvents() {
		final HashMap<UUID, Cooldown> cooldowns = new HashMap<>();

		Events.subscribe(EntityDamageByEntityEvent.class)
				.filter(e -> e.getEntity() instanceof Player)
				.filter(e -> e.getDamager() instanceof Player)
				.handler(e -> {
					Player entity = (Player) e.getEntity();
					Player damager = (Player) e.getDamager();

					if (!MongoPlayerManager.getCache().containsKey(damager.getUniqueId())) {
						e.setCancelled(true);
						return;
					}

					MinetopiaUser user = MongoPlayerManager.getCache().get(damager.getUniqueId());

					String deny = Message.COMMON_ERROR_PVP.raw();

					if (damager.getInventory().getItemInMainHand() == null) {
						e.setCancelled(true);
						damager.sendMessage(Text.colors(deny));
						return;
					}

					if (!weapons.containsKey(damager.getInventory().getItemInMainHand().getType())) {
						e.setCancelled(true);
						damager.sendMessage(Text.colors(deny));
						return;
					}

					MinetopiaWeapon weapon = weapons.get(damager.getInventory().getItemInMainHand().getType());

					if (weapon.cooldown()) {
						if (cooldowns.containsKey(damager.getUniqueId())) {
							Cooldown cooldown = cooldowns.get(damager.getUniqueId());
							if (cooldown.getWeapon() != weapon) return;

							if (new Date().before(cooldown.getExpiry())) {
								e.setCancelled(true);
								damager.sendMessage(Text.colors(((Cooldownable) weapon).cooldownMessage()));
								return;
							}
						}

						cooldowns.put(damager.getUniqueId(), new Cooldown(user, weapon));
					}

					if (!weapon.damage()) {
						e.setDamage(0d);

						ItemStack[] armor = entity.getInventory().getArmorContents().clone();
						Schedulers.sync().runLater(() -> entity.getInventory().setArmorContents(armor), 1L);
					}

					entity.sendMessage(String.format(Text.colors(weapon.victimMessage()), damager.getName()));
					damager.sendMessage(String.format(Text.colors(weapon.damagerMessage()), entity.getName()));
				}).bindWith(composite);

		Events.subscribe(PlayerInteractAtEntityEvent.class)
				.filter(e -> e.getRightClicked() instanceof Player)
				.filter(e -> e.getPlayer().getInventory().getItemInMainHand() != null)
				.filter(e -> e.getHand() == EquipmentSlot.HAND)
				.handler(e -> {
					ItemStack itemStack = e.getPlayer().getInventory().getItemInMainHand();
					Material material = itemStack.getType();

					MinetopiaInteractable interactable = interactables.get(material);
					if (interactable == null) return;
					if (itemStack.getDurability() != interactable.durability()) return;
					if (!e.getPlayer().hasPermission(interactable.permission())) {
						e.getPlayer().sendMessage(Message.ITEMS_ERROR_NOPERMISSIONS.format(interactable.getClass().toString()));
						return;
					}

					e.setCancelled(true);
					interactable.event().execute(e);
				}).bindWith(composite);

		Events.subscribe(PlayerInteractEvent.class)
				.filter(PlayerInteractEvent::hasItem)
				.filter(e -> e.getPlayer().getInventory().getItemInMainHand() != null)
				.filter(e -> e.getHand() == EquipmentSlot.HAND)
				.handler(e -> {
					ItemStack itemStack = e.getPlayer().getInventory().getItemInMainHand();
					Material material = itemStack.getType();

					MinetopiaInteractable interactable = interactables.get(material);
					if (interactable == null) return;
					if (itemStack.getDurability() != interactable.durability()) return;
					if (!e.getPlayer().hasPermission(interactable.permission())) {
						return;
					}
					e.setCancelled(true);
					interactable.event().execute(e);
				}).bindWith(composite);

		Events.subscribe(ProjectileLaunchEvent.class)
				.filter(e -> this.projectiles.containsKey(e.getEntity().getType()))
				.filter(e -> this.projectiles.get(e.getEntity().getType()) instanceof MinetopiaEntityColor)
				.filter(e -> e.getEntity().getShooter() instanceof Player)
				.handler(e -> {
					MinetopiaEntityColor entityColor = (MinetopiaEntityColor) this.projectiles.get(e.getEntity().getType());
					e.getEntity().setGlowing(true);
					Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
					Team team = scoreboard.registerNewTeam("glow");
					team.setColor(entityColor.color());
					team.addEntry(e.getEntity().getUniqueId().toString());
				}).bindWith(composite);

		Events.subscribe(EntityDamageByEntityEvent.class)
				.filter(e -> this.projectiles.containsKey(e.getDamager().getType()))
				.filter(e -> e.getEntity() instanceof Player)
				.filter(e -> ((Projectile)e.getDamager()).getShooter() instanceof Player)
				.handler(e -> {
					Projectile projectile = ((Projectile) e.getDamager());
					Player shooter = (Player) projectile.getShooter();
					Player damaged = (Player) e.getEntity();

					MinetopiaProjectile minetopiaProjectile = this.projectiles.get(projectile.getType());
					if (minetopiaProjectile.exclusions().length != 0 && projectile.getCustomName() != null) {
						for (String string : minetopiaProjectile.exclusions()) {
							if (projectile.getCustomName().contains(string) || projectile.getCustomName().startsWith(string)) {
								return;
							}
						}
					}

					shooter.sendMessage(String.format(Text.colors(minetopiaProjectile.damagerMessage()), damaged.getName()));
					damaged.sendMessage(String.format(Text.colors(minetopiaProjectile.victimMessage()), shooter.getName()));
				}).bindWith(composite);

		Events.subscribe(ProjectileHitEvent.class)
				.filter(e -> e.getEntity() instanceof Arrow)
				.handler(e -> e.getEntity().remove()).bindWith(composite);

		Bukkit.getPluginManager().registerEvents(new Taser(), Minetopia.getPlugin(Minetopia.class));
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
		return "Weapons";
	}
}
