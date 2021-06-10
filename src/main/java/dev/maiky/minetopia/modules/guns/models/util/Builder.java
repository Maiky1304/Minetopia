package dev.maiky.minetopia.modules.guns.models.util;

import dev.maiky.minetopia.modules.guns.models.interfaces.Model;
import dev.maiky.minetopia.util.Items;
import me.lucko.helper.item.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Door: Maiky
 * Info: Minetopia - 09 Jun 2021
 * Package: dev.maiky.minetopia.modules.guns.models.util
 */

public class Builder {

	public static Builder with(Model model) {
		return new Builder(model);
	}

	private final Model model;

	private Builder(Model model) {
		this.model = model;
	}

	private ItemStack itemStack = null;

	public Model getModel() {
		return model;
	}

	public Builder setLicense(String license) {
		itemStack = buildItem();
		itemStack = ItemStackBuilder.of(itemStack)
				.lore("", "Officiëel BlackMT Item, Season 1 Gun", "Officiëel BlackMT Gear", "Serie Nr. " + license)
				.breakable(false).build();
		itemStack = editNBT(itemStack, "license", license);
		return this;
	}

	public ItemStack buildAmmo() {
		return editNBT(ItemStackBuilder.of(Material.IRON_INGOT)
				.name(String.format("§8" + model.customName() + " %s", "Ammo"))
						.lore("", "Officiëel Black Item, Season 1 Ammo").breakable(false)
						.build(),
				"mtcustom", this.model.modelName().split("_")[0] + "_bullets");
	}

	public ItemStack buildItem() {
		if (this.itemStack != null)
			return itemStack;

		ItemStack itemStack = new ItemStack(Material.WOOD_HOE);
		itemStack = editNBT(itemStack, "mtcustom", this.model.modelName());
		itemStack = ItemStackBuilder.of(itemStack).name("&8" + this.model.customName()).build();
		return itemStack;
	}

	public ItemStack editNBT(ItemStack itemStack, String key, String value) {
		return Items.editNBT(itemStack, key, value);
	}

}
