package dev.maiky.minetopia.modules.players.gui;

import dev.maiky.minetopia.modules.data.managers.mongo.MongoPlayerManager;
import dev.maiky.minetopia.modules.players.classes.MinetopiaUser;
import dev.maiky.minetopia.util.Numbers;
import dev.maiky.minetopia.util.Skull;
import me.lucko.helper.item.ItemStackBuilder;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Iterator;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

public class WalletGUI extends Gui {

    public WalletGUI(Player player) {
        super(player, 1, "Portemonnee");
    }

    private final MenuScheme glass = new MenuScheme().mask("101110011");

    private final MenuScheme icons = new MenuScheme().mask("010001100");

    @Override
    public void redraw() {
        // Variables
        MinetopiaUser user = MongoPlayerManager.getCache().get(getPlayer().getUniqueId());
        double cashOnPerson = user.getCash();
        double blackMoney = user.getBlackMoney();

        // Apply Glass Scheme
        MenuPopulator glassPopulator = glass.newPopulator(this);
        while(glassPopulator.hasSpace()) glassPopulator.accept(ItemStackBuilder.of(Material.STAINED_GLASS_PANE)
                .name(" ").durability(15).buildItem().build());

        // icons
        Item[] items = {
                ItemStackBuilder.of(Skull.getPlayerSkull(getPlayer().getName())).name("&b" + getPlayer().getName())
                        .buildItem().build(),
                ItemStackBuilder.of(Material.IRON_PICKAXE).name("&aContant Geld").durability(48).breakable(false)
                        .lore("", "&7Je hebt §2" + Numbers.convert(Numbers.Type.MONEY, cashOnPerson) + " §7op zak.").buildItem().build(),
                ItemStackBuilder.of(Material.IRON_PICKAXE).name("&cZwart Geld").durability(47).breakable(false)
                        .lore("","&7Je hebt §4" + Numbers.convert(Numbers.Type.MONEY, blackMoney) + " §7zwartgeld op zak.")
                        .buildItem().build()
        };
        Iterator<Item> iterator = Arrays.stream(items).iterator();
        MenuPopulator populator = icons.newPopulator(this);
        while(populator.hasSpace() && iterator.hasNext()) populator.accept(iterator.next());
    }

    @Override
    public void open() {
        super.open();
    }

}
