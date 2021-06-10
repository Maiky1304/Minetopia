package dev.maiky.minetopia.modules.guns.models.util;

import dev.maiky.minetopia.modules.guns.models.interfaces.Model;
import me.lucko.helper.item.ItemStackBuilder;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
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
				.lore("", "&6License: &c" + license).build();
		itemStack = editNBT(itemStack, "license", license);
		return this;
	}

	public ItemStack buildItem() {
		if (this.itemStack != null)
			return itemStack;

		ItemStack itemStack = new ItemStack(Material.WOOD_HOE);
		itemStack = editNBT(itemStack, "mtcustom", this.model.modelName());
		return itemStack;
	}

	public ItemStack editNBT(ItemStack itemStack, String key, String value) {
		net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
		NBTTagCompound compound = nmsStack.getTag() == null ? new NBTTagCompound() : nmsStack.getTag();
		compound.setString(key, value);
		nmsStack.setTag(compound);
		itemStack = CraftItemStack.asCraftMirror(nmsStack);
		return itemStack;
	}

}
