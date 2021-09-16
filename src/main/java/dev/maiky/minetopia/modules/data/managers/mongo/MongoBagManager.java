package dev.maiky.minetopia.modules.data.managers.mongo;

import dev.maiky.minetopia.modules.bags.bag.Bag;
import dev.maiky.minetopia.modules.bags.bag.BagType;
import dev.maiky.minetopia.modules.data.managers.AIManager;
import dev.maiky.minetopia.util.SerializationUtils;
import org.bukkit.inventory.ItemStack;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

public class MongoBagManager extends AIManager<Bag> {

    public MongoBagManager() {
        super(Bag.class);
    }

    public Bag createBag(BagType type, int rows) {
        Bag bag = new Bag();
        bag.setBagId(increment());
        bag.setBase64Contents(SerializationUtils.itemStackArrayToBase64(new ItemStack[0]));
        bag.setRows(rows);
        bag.setType(type);

        super.save(bag);

        return bag;
    }

}
