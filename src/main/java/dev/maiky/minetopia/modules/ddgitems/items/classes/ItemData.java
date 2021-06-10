package dev.maiky.minetopia.modules.ddgitems.items.classes;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.lucko.helper.item.ItemStackBuilder;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemData {

    private JsonObject jsonObject;

    public Material material;
    public ItemType type;
    public String name;
    public List<String> lore;
    public ItemNBT nbt;
    public int durability;
    public String armorColor;
    public List<ItemData> customItems = new ArrayList<>();

    public Object[] createItem() {
        ItemStack itemStack = ItemStackBuilder.of(this.material).amount(1).durability(this.durability)
                .name(this.name).lore(this.lore).build();
        if (nbt != null) {
            net.minecraft.server.v1_12_R1.ItemStack nbtStack = CraftItemStack.asNMSCopy(itemStack);
            NBTTagCompound tagCompound = nbtStack.getTag();
            tagCompound.setString(nbt.base, nbt.value);
            nbtStack.setTag(tagCompound);
            itemStack = CraftItemStack.asCraftMirror(nbtStack);
        }
        if (armorColor != null) {
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
            leatherArmorMeta.setColor(hex2Rgb(this.armorColor));
            itemStack.setItemMeta(leatherArmorMeta);
        }
        if (durability != 0) {
            ItemMeta meta = itemStack.getItemMeta();
            meta.setUnbreakable(true);
            itemStack.setItemMeta(meta);
        }

        return new Object[]{itemStack, this.customItems};
    }

    private Color hex2Rgb(String colorStr) {
        return Color.fromRGB(
                Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
                Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
                Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
    }

    public ItemData(JsonObject jsonObject) {
        /* Object */
        this.jsonObject = jsonObject;

        JsonElement materialElement = jsonObject.get("material");
        if (materialElement != null) {
            material = Material.valueOf(materialElement.getAsString().toUpperCase());
        }

        JsonElement typeElement = jsonObject.get("type");
        if (typeElement != null) {
            type = ItemType.valueOf(typeElement.getAsString().toUpperCase());
        }

        JsonElement nameElement = jsonObject.get("name");
        if (nameElement != null) {
            name = nameElement.getAsString();
        }

        JsonElement loreElement = jsonObject.get("lore");
        if (loreElement != null) {
            Type listType = new TypeToken<List<String>>() {}.getType();
            lore = new Gson().fromJson(loreElement, listType);
            if (this.type == ItemType.ITEM) {
                lore.add("Officiëel BlackMT Item");
            } else {
                lore.add("Officiële BlackMT Kleding");
            }
        }

        if (lore == null) {
            if (this.type == ItemType.ITEM) {
                lore = Collections.singletonList("Officiëel BlackMT Item");
            } else {
                lore = Collections.singletonList("Officiële BlackMT Kleding");
            }
        }

        JsonElement nbtElement = jsonObject.get("nbt");
        if (nbtElement != null) {
            JsonObject nbtObject = nbtElement.getAsJsonObject();
            String base = nbtObject.get("base").getAsString();
            String value = nbtObject.get("value").getAsString();
            nbt = new ItemNBT(base, value);
        }

        JsonElement durabilityElement = jsonObject.get("durability");
        if (durabilityElement != null) {
            durability = durabilityElement.getAsInt();
        }

        JsonElement armorColorElement = jsonObject.get("armorColor");
        if (armorColorElement != null) {
            armorColor = armorColorElement.getAsString();
        }

        JsonElement customItemsElement = jsonObject.get("customitems");
        if (customItemsElement != null) {
            JsonArray customItemsArray = customItemsElement.getAsJsonArray();
            for (JsonElement element : customItemsArray) {
                if (element instanceof JsonObject) {
                    customItems.add(new ItemData(element.getAsJsonObject()));
                }
            }
        }

        if (this.type == ItemType.REGULAR_ARMOR_SET) {
            String type = this.material.toString().split("_")[0].toUpperCase();
            List<JsonObject> objects = new ArrayList<>();
            for (ArmorType type2 : ArmorType.values()) {
                JsonObject json = new JsonObject();
                json.addProperty("material", type + "_" + type2.toString());
                json.addProperty("type", ItemType.SINGLE_ARMOR_PIECE.toString());
                json.addProperty("name", this.name.substring(0, this.name.length()-10) + type2.toString().toLowerCase());
                objects.add(json);
            }
            for (JsonObject object : objects) {
                customItems.add(new ItemData(object));
            }
        }
    }

    @Override
    public String toString() {
        return "ItemData{" +
                "jsonObject=" + jsonObject +
                ", material=" + material +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", lore=" + lore +
                ", nbt=" + nbt +
                ", durability=" + durability +
                ", armorColor='" + armorColor + '\'' +
                ", customItems=" + customItems +
                '}';
    }

    enum ArmorType {
        HELMET,LEGGINGS,BOOTS;
    }

}
