package dev.maiky.minetopia.modules.guns;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.ConditionFailedException;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.WeaponManager;
import dev.maiky.minetopia.modules.guns.commands.GunsCommand;
import dev.maiky.minetopia.modules.guns.gun.Weapon;
import dev.maiky.minetopia.modules.guns.models.firearms.*;
import dev.maiky.minetopia.modules.guns.models.interfaces.Burst;
import dev.maiky.minetopia.modules.guns.models.interfaces.Model;
import me.lucko.helper.Events;
import me.lucko.helper.Schedulers;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.cooldown.CooldownMap;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Door: Maiky
 * Info: Minetopia - 09 Jun 2021
 * Package: dev.maiky.minetopia.modules.guns
 */

public class GunsModule implements MinetopiaModule {

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

		// Register Guns
		this.registerGuns();

		// Register Events
		this.registerEvents();

		// Register Commands
		this.registerCommands();
	}

	private void registerCommands() {
		Minetopia minetopia = Minetopia.getPlugin(Minetopia.class);
		BukkitCommandManager manager = minetopia.getCommandManager();

		manager.getCommandCompletions().registerCompletion("models", context -> {
			List<String> strings = new ArrayList<>();
			this.models.forEach(m -> strings.add(m.modelName()));
			return strings;
		});
		manager.getCommandConditions().addCondition(String.class, "verifyModel", (context, execContext, value) -> {
			if ( this.models.stream().noneMatch(m -> m.modelName().equals(value)) ) throw new ConditionFailedException("There is no model with the name: " + value);
		});
		manager.registerCommand(new GunsCommand(this));
	}

	private void registerEvents() {
		WeaponManager weaponManager = WeaponManager.with(DataModule.getInstance().getSqlHelper());

		HashMap<Model, CooldownMap<String>> cooldowns = new HashMap<>();
		this.models.forEach(m -> cooldowns.put(m, CooldownMap.create(Cooldown.of(m.delayBetweenShots(), TimeUnit.MILLISECONDS))));

		Events.subscribe(PlayerInteractEvent.class)
				.filter(PlayerInteractEvent::hasItem)
				.filter(e -> e.getAction().toString().startsWith("RIGHT_CLICK"))
				.filter(e -> e.getItem().getType() == Material.WOOD_HOE)
				.filter(e -> CraftItemStack.asNMSCopy(e.getItem()).getTag() != null)
				.filter(e -> Objects.requireNonNull(CraftItemStack.asNMSCopy(e.getItem()).getTag()).hasKey("mtcustom"))
				.handler(e -> {
					Player player = e.getPlayer();

					NBTTagCompound nbtTagCompound = Objects.requireNonNull(CraftItemStack.asNMSCopy(e.getPlayer().getInventory().getItemInMainHand())
							.getTag());
					Model model = getModel(nbtTagCompound.getString("mtcustom"));
					String license = nbtTagCompound.getString("license");
					Weapon weapon = weaponManager.getWeaponByLicense(license);

					if (weapon.getAmmo() == 0) {
						// TODO: Maybe play sound?
						player.sendMessage("§cJe hebt geen ammo meer!");
						return;
					}

					CooldownMap<String> cooldownMap = cooldowns.get(model);
					if (!cooldownMap.test(license)) {
						return;
					}

					if (model.getClass().isAnnotationPresent(Burst.class)) {
						weapon.setAmmo(weapon.getAmmo() - 3);
						if (weapon.getAmmo() < 0)
							weapon.setAmmo(0);
					} else {
						weapon.setAmmo(weapon.getAmmo() - 1);
					}
					weapon.setDurability(weapon.getDurability() - 1);
					weaponManager.updateWeapon(weapon);

					player.sendMessage("§6Durability: §c" + weapon.getDurability());
					player.sendMessage("§6Ammo: §c" + weapon.getAmmo() + "§6/§c" + model.defaultAmmo());

					if (weapon.getDurability() == 0) {
						player.getInventory().setItemInMainHand(null);
						player.sendMessage("§cJe wapen is kapot gegaan!");
						player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 0.1f, 1f);
					}

					if (model.getClass().isAnnotationPresent(Burst.class)) {
						AtomicInteger i = new AtomicInteger();
						Schedulers.sync().runRepeating(task -> {
							if (i.get() == 3) {
								task.stop();
								task.close();
								return;
							}

							Snowball snowball = player.launchProjectile(Snowball.class, player.getLocation().getDirection().multiply(3.7D));

							// Modify data
							snowball.setShooter(player);
							snowball.setCustomName("minetopia_bullet:" + model.modelName());

							i.getAndIncrement();
						}, 0, 2);
					} else {
						Snowball snowball = player.launchProjectile(Snowball.class, player.getLocation().getDirection().multiply(3.7D));

						// Modify data
						snowball.setShooter(player);
						snowball.setCustomName("minetopia_bullet:" + model.modelName());
					}
				})
		.bindWith(composite);
		Events.subscribe(EntityDamageByEntityEvent.class)
				.filter(event -> event.getDamager() instanceof Snowball)
				.filter(event -> event.getDamager().getCustomName().startsWith("minetopia_bullet"))
				.filter(event -> event.getEntity() instanceof Player)
				.handler(event -> {
					Player gunman = (Player)((Snowball)event.getDamager()).getShooter();
					Player victim = (Player)event.getEntity();

					Model model = this.getModel(event.getDamager().getCustomName().split(":")[1]);
					double damage = model.bulletDamage();

					if ((victim.getHealth() - damage) < 0) {
						victim.setHealth(0d);
					} else {
						victim.setHealth(victim.getHealth() - damage);
					}

					gunman.sendMessage("§6Je hebt §c" + victim.getName() + " §6geraakt met een kogel.");
					victim.sendMessage("§6Je bent door §c" + gunman.getName() + " §6geraakt met een kogel.");
				})
		.bindWith(composite);
		Events.subscribe(PlayerItemHeldEvent.class)
				.filter(e -> e.getPlayer().getInventory().getItem(e.getNewSlot()) != null)
				.filter(e -> e.getPlayer().getInventory().getItem(e.getNewSlot()).getType() == Material.WOOD_HOE)
				.filter(e -> CraftItemStack.asNMSCopy(e.getPlayer().getInventory().getItem(e.getNewSlot())).getTag() != null)
				.filter(e -> Objects.requireNonNull(CraftItemStack.asNMSCopy(e.getPlayer().getInventory().getItem(e.getNewSlot())).getTag())
						.hasKey("mtcustom"))
				.handler(e -> {
					NBTTagCompound nbtTagCompound = Objects.requireNonNull(CraftItemStack.asNMSCopy(e.getPlayer().getInventory().getItemInMainHand())
							.getTag());
					Model model = getModel(nbtTagCompound.getString("mtcustom"));
					String license = nbtTagCompound.getString("license");
					Weapon weapon = weaponManager.getWeaponByLicense(license);
					e.getPlayer().sendMessage("§6Durability: §c" + weapon.getDurability());
					e.getPlayer().sendMessage("§6Ammo: §c" + weapon.getAmmo() + "§6/§c" + model.defaultAmmo());
				})
		.bindWith(composite);
	}

	public Model getModel(String key) {
		return this.models.stream()
				.filter(model -> model.modelName().equals(key))
				.findFirst().orElse(null);
	}

	private final List<Model> models = new ArrayList<>();

	private void registerGuns() {
		Model[] models = new Model[]{new DesertEagle(), new Glock(), new M16A4(),
		new Magnum(), new Walther()};
		this.models.addAll(Arrays.asList(models));
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
		return "Guns";
	}
}
