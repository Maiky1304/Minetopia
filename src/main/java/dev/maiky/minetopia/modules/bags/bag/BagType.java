package dev.maiky.minetopia.modules.bags.bag;

import dev.maiky.minetopia.util.Options;
import me.lucko.helper.item.ItemStackBuilder;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public enum BagType {

    YELLOW_BACKPACK(Material.CARROT_STICK, (short)20, "Rugzak"),
    RED_BACKPACK(Material.CARROT_STICK, (short)21, "Rugzak"),
    BROWN_BACKPACK(Material.CARROT_STICK, (short)22, "Rugzak"),
    PURPLE_BACKPACK(Material.CARROT_STICK, (short)23, "Rugzak"),
    GREEN_BACKPACK(Material.CARROT_STICK, (short)24, "Rugzak"),
    SUITCASE(Material.CARROT_STICK, (short)25, "Koffer"),
    CREEPER_BAG(Material.CARROT_STICK, (short)19, "Nektasje"),
    POTGROND_BACKPACK(Material.CARROT_STICK, (short)0, "Rugzak", "potgrond_backpack"),
    DUFFLEBAG(Material.CARROT_STICK, (short)0, "Dufflebag", "dufflebag");

    public Material material;
    public short durability;
    public String displayName,value;

    BagType(Material material, short durability, String displayName){
        this.material = material;
        this.durability = durability;
        this.displayName = displayName;
    }

    BagType(Material material, short durability, String displayName, String value){
        this.material = material;
        this.durability = durability;
        this.displayName = displayName;
        this.value = value;
    }

    public ItemStack create(int id) {
        ItemStack itemStack = ItemStackBuilder.of(this.material)
                .durability(this.durability)
                .name(this.displayName)
                .lore(Arrays.asList(Options.BAGS_DEFAULT_LORE.asString().get(), "ID: " + id)).build();
        net.minecraft.server.v1_12_R1.ItemStack nms = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tagCompound = nms.getTag() == null ? new NBTTagCompound() : nms.getTag();
        if (this.value != null)
            tagCompound.setString("mtcustom", this.value);
        if (id != -1)
            tagCompound.setInt("id", id);
        itemStack = CraftItemStack.asCraftMirror(nms);

        if (this.durability != (short)0) {
            ItemMeta meta = itemStack.getItemMeta();
            meta.setUnbreakable(true);
            itemStack.setItemMeta(meta);
        }

        return itemStack;
    }

    public static List<String> list() {
        List<String> list = new ArrayList<>();
        Iterator<BagType> bagTypeIterator = createIterator();
        while(bagTypeIterator.hasNext())
            list.add(bagTypeIterator.next().toString());
        return list;
    }

    public static Iterator<BagType> createIterator() {
        return new ArrayList<>(Arrays.asList(values())).iterator();
    }

}
