package dev.maiky.minetopia.modules.guns;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.ConditionFailedException;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.MinetopiaModule;
import dev.maiky.minetopia.modules.data.DataModule;
import dev.maiky.minetopia.modules.data.managers.WeaponManager;
import dev.maiky.minetopia.modules.guns.commands.GunsCommand;
import dev.maiky.minetopia.modules.guns.listeners.DamageListener;
import dev.maiky.minetopia.modules.guns.listeners.ItemHeldListener;
import dev.maiky.minetopia.modules.guns.listeners.TriggerListener;
import dev.maiky.minetopia.modules.guns.models.interfaces.Model;
import dev.maiky.minetopia.util.Configuration;
import lombok.Getter;
import me.lucko.helper.cooldown.Cooldown;
import me.lucko.helper.cooldown.CooldownMap;
import me.lucko.helper.terminable.Terminable;
import me.lucko.helper.terminable.composite.CompositeClosingException;
import me.lucko.helper.terminable.composite.CompositeTerminable;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Door: Maiky
 * Info: Minetopia - 09 Jun 2021
 * Package: dev.maiky.minetopia.modules.guns
 */

public class GunsModule implements MinetopiaModule {

	private final CompositeTerminable composite = CompositeTerminable.create();

	private boolean enabled;

	private Configuration configuration;

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

		// Configuration
		this.configuration = new Configuration(Minetopia.getInstance(), "modules/guns.yml");
		this.configuration.load();

		// Register Guns
		this.registerGuns();

		// Register Events
		this.registerEvents();

		// Register Commands
		this.registerCommands();
	}

	private GunsCommand gunsCommand;

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
		this.gunsCommand = new GunsCommand(this);
		manager.registerCommand(gunsCommand);
		this.composite.bind((Terminable) () -> manager.unregisterCommand(gunsCommand));
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
		Set<String> keys = this.configuration.get().getKeys(false);
		Model[] models = new Model[keys.size()];

		int i = 0;
		for (String key : keys) {
			ConfigurationSection section = this.configuration.get().getConfigurationSection(key);
			Model model = new Model() {
				@Override
				public double bulletVelocity() {
					return section.getInt("velocity");
				}

				@Override
				public double bulletDamage() {
					return section.getDouble("damage");
				}

				@Override
				public long delayBetweenShots() {
					return section.getInt("delay");
				}

				@Override
				public boolean burst() {
					return section.getBoolean("burst");
				}

				@Override
				public String modelName() {
					return section.getString("mtcustom");
				}

				@Override
				public String customName() {
					return section.getString("name");
				}

				@Override
				public int defaultAmmo() {
					return section.getInt("default-ammo");
				}
			};

			models[i] = model;
			i++;
		}
		this.models.addAll(Arrays.asList(models));
		composite.bind((Terminable) () -> getModels().clear());
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
