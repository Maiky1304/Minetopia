package dev.maiky.minetopia.modules.guns;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.ConditionFailedException;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.WeaponManager;
import dev.maiky.minetopia.modules.guns.commands.GunsCommand;
import dev.maiky.minetopia.modules.guns.gun.Weapon;
import dev.maiky.minetopia.modules.guns.listeners.DamageListener;
import dev.maiky.minetopia.modules.guns.listeners.ItemHeldListener;
import dev.maiky.minetopia.modules.guns.listeners.TriggerListener;
import dev.maiky.minetopia.modules.guns.models.firearms.*;
import dev.maiky.minetopia.modules.guns.models.interfaces.Burst;
import dev.maiky.minetopia.modules.guns.models.interfaces.Model;
import dev.maiky.minetopia.modules.guns.models.interfaces.Spread;
import dev.maiky.minetopia.modules.guns.models.util.Builder;
import dev.maiky.minetopia.modules.notifications.util.NotificationUtil;

import lombok.Getter;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

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
		instance = this;
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
		manager.getCommandConditions().addCondition(Player.class, "hasGun", (context, execContext, value) -> {
			ItemStack mainHand = value.getInventory().getItemInMainHand();
			String message = "You don't have a gun in your hand.";
			ConditionFailedException failed = new ConditionFailedException(message);
			if (mainHand.getType() != Material.WOOD_HOE)
				throw failed;
			if (CraftItemStack.asNMSCopy(mainHand).getTag() == null)
				throw failed;
			if (!Objects.requireNonNull(CraftItemStack.asNMSCopy(mainHand).getTag()).hasKey("mtcustom"))
				throw failed;
		});
		manager.registerCommand(new GunsCommand(this));
	}

	private void registerEvents() {
		WeaponManager weaponManager = WeaponManager.with(DataModule.getInstance().getSqlHelper());

		List<Player> reloading = new ArrayList<>();
		HashMap<Model, CooldownMap<String>> cooldowns = new HashMap<>();
		this.models.forEach(m -> cooldowns.put(m, CooldownMap.create(Cooldown.of(m.delayBetweenShots(), TimeUnit.MILLISECONDS))));

		this.composite.bindModule(new TriggerListener(weaponManager, reloading, cooldowns));
		this.composite.bindModule(new DamageListener());
		this.composite.bindModule(new ItemHeldListener(weaponManager));
	}

	@Getter
	private static GunsModule instance;

	public Model getModel(String key) {
		return this.models.stream()
				.filter(model -> model.modelName().equals(key))
				.findFirst().orElse(null);
	}

	private final List<Model> models = new ArrayList<>();

	public List<Model> getModels() {
		return models;
	}

	private void registerGuns() {
		Model[] models = new Model[]{new DesertEagle(), new Glock(), new M16A3(), new M16A4(),
		new Magnum(), new Walther(), new Shotgun(), new WaltherPP(), new AK47()};
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
