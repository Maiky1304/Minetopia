package dev.maiky.minetopia.api.banking;

import dev.maiky.minetopia.modules.bank.ui.AccountChooseUI;
import dev.maiky.minetopia.modules.bank.ui.BalanceManageUI;
import dev.maiky.minetopia.modules.bank.ui.BankChooseUI;
import lombok.Getter;
import me.lucko.helper.menu.Gui;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

public class BankOpenEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();

    private boolean cancel;
    @Getter private final Player player;
    @Getter private final Block atm;
    @Getter private final Menu menu;

    public BankOpenEvent(Player player, Block atm, Menu menu) {
        this.player = player;
        this.atm = atm;
        this.menu = menu;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public enum Menu {

        MAIN(BankChooseUI.class), ACCOUNT_SELECTION(AccountChooseUI.class), OVERVIEW(BalanceManageUI.class);

        @Getter private final Class<? extends Gui> guiClass;

        Menu(Class<? extends Gui> guiClass) {
            this.guiClass = guiClass;
        }

    }

}
