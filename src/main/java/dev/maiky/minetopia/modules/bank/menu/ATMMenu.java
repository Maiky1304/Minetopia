package dev.maiky.minetopia.modules.bank.menu;

import com.google.gson.GsonBuilder;
import dev.maiky.minetopia.Minetopia;
import dev.maiky.minetopia.util.Numbers;
import lombok.Getter;
import nl.cokemt.core.widgets.WidgetScreen;
import nl.cokemt.core.widgets.components.widgets.ButtonWidget;
import nl.cokemt.core.widgets.components.widgets.ImageWidget;
import nl.cokemt.core.widgets.components.widgets.LabelWidget;
import nl.cokemt.core.widgets.components.widgets.TextFieldWidget;
import nl.cokemt.core.widgets.json.ScreenInteraction;
import nl.cokemt.core.widgets.json.ScreenResponse;
import nl.cokemt.core.widgets.util.Anchor;
import nl.cokemt.core.widgets.util.EnumScreenAction;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

public class ATMMenu extends WidgetScreen {

    public ATMMenu() {
        // Base Anchor
        Anchor anchor = new Anchor(50, 50);

        // Background
        ImageWidget widget = new ImageWidget(1, anchor, -Math.floor((double)434 / 2), -Math.floor((double)325 / 2), 434, 325, Asset.BACKGROUND.url);
        addWidget(widget);

        // Button
        ButtonWidget button = new ButtonWidget(2, anchor, -25, 75, ".", 75, 75);
        addWidget(button);
    }

    @Override
    public void onEvent(ScreenResponse response) {
        if (response.isInteraction()) {
            int widgetId = ((ScreenInteraction) response).getWidgetId();

            // Fingerprint
            if (widgetId == 2) {
                update(response.getPlayer(), EnumScreenAction.CLOSE);

                BankMenu bankMenu = new BankMenu(response.getPlayer());
                bankMenu.open(response.getPlayer());
            }
        }
    }

    public static class BankMenu extends WidgetScreen {

        public BankMenu(Player player) {
            // Base Anchor
            Anchor anchor = new Anchor(50, 50);

            // Background
            ImageWidget background = new ImageWidget(0, anchor, -217, -163, 434, 325, Asset.OVERVIEW_BACKGROUND.url);
            addWidget(background);

            // Name
            LabelWidget name = new LabelWidget(1, anchor, -169, -65, ChatColor.GOLD + player.getName(), LabelWidget.Alignment.LEFT, 1.25);
            addWidget(name);

            // Balance
            LabelWidget balance = new LabelWidget(2, anchor, -169, -30, "§6§l" + Numbers.convert(Numbers.Type.MONEY, Minetopia.getEconomy().getBalance(player)), LabelWidget.Alignment.LEFT, 1.5);
            addWidget(balance);

            // Deposit Button
            ButtonWidget deposit = new ButtonWidget(3, anchor, 72, -100, "Storten", 115, 43);
            addWidget(deposit);

            // Withdraw Button
            ButtonWidget withdraw = new ButtonWidget(4, anchor, 72, -48, "Opnemen", 115, 43);
            addWidget(withdraw);

            // Deposit Button
            ButtonWidget transfer = new ButtonWidget(5, anchor, 72, 2, "Overmaken", 115, 43);
            addWidget(transfer);
        }

        @Override
        public void onEvent(ScreenResponse response) {
            if (response.isInteraction()) {
                int widgetId = ((ScreenInteraction) response).getWidgetId();

                switch (widgetId) {
                    case 3:
                        update(response.getPlayer(), EnumScreenAction.CLOSE);
                        new DepositMenu().open(response.getPlayer());
                        break;
                    case 4:
                        update(response.getPlayer(), EnumScreenAction.CLOSE);
                        new WithdrawMenu().open(response.getPlayer());
                        break;
                    case 5:
                        update(response.getPlayer(), EnumScreenAction.CLOSE);
                        new TransferMenu().open(response.getPlayer());
                        break;
                }
            }
        }

        public static class DepositMenu extends WidgetScreen {

            public DepositMenu() {
                // Base Anchor
                Anchor anchor = new Anchor(50, 50);

                // Background
                ImageWidget background = new ImageWidget(0, anchor, -217, -163, 434, 325, Asset.DEPOSIT_MENU.url);
                addWidget(background);

                // Back Button
                ButtonWidget back = new ButtonWidget(1, anchor, 72, -100, "Terug", 115, 43);
                addWidget(back);

                // Input Box
                TextFieldWidget inputBox = new TextFieldWidget(2, anchor, -173, 10, "", 350, 30, "€ 0,-", 32, true);
                addWidget(inputBox);

                // Confirm Button
                ButtonWidget confirm = new ButtonWidget(3, anchor, -180, 60, "Storten", 115, 43);
                addWidget(confirm);
            }

            @Override
            public void onEvent(ScreenResponse response) {
                if (response.isInteraction()) {
                    int widgetId = ((ScreenInteraction) response).getWidgetId();

                    switch(widgetId) {
                        case 1:
                            update(response.getPlayer(), EnumScreenAction.CLOSE);
                            new ATMMenu.BankMenu(response.getPlayer()).open(response.getPlayer());
                            break;
                        case 3:
                            System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(response.getObject()));
                            break;
                    }
                }
            }

        }

        public static class WithdrawMenu extends WidgetScreen {

            public WithdrawMenu() {
                // Base Anchor
                Anchor anchor = new Anchor(50, 50);

                // Background
                ImageWidget background = new ImageWidget(0, anchor, -217, -163, 434, 325, Asset.WITHDRAW_MENU.url);
                addWidget(background);

                // Back Button
                ButtonWidget back = new ButtonWidget(1, anchor, 72, -100, "Terug", 115, 43);
                addWidget(back);

                // Input Box
                TextFieldWidget inputBox = new TextFieldWidget(2, anchor, -173, 10, "", 350, 30, "€ 0,-", 32, true);
                addWidget(inputBox);
            }

            @Override
            public void onEvent(ScreenResponse response) {
                if (response.isInteraction()) {
                    int widgetId = ((ScreenInteraction) response).getWidgetId();

                    switch(widgetId) {
                        case 1:
                            update(response.getPlayer(), EnumScreenAction.CLOSE);
                            new ATMMenu.BankMenu(response.getPlayer()).open(response.getPlayer());
                            break;
                    }
                }
            }

        }

        public static class TransferMenu extends WidgetScreen {

            public TransferMenu() {
                // Base Anchor
                Anchor anchor = new Anchor(50, 50);

                // Background
                ImageWidget background = new ImageWidget(0, anchor, -217, -163, 434, 325, Asset.TRANSFER_MENU.url);
                addWidget(background);

                // Back Button
                ButtonWidget back = new ButtonWidget(1, anchor, 72, -100, "Terug", 115, 43);
                addWidget(back);
            }

            @Override
            public void onEvent(ScreenResponse response) {
                if (response.isInteraction()) {
                    int widgetId = ((ScreenInteraction) response).getWidgetId();

                    switch(widgetId) {
                        case 1:
                            update(response.getPlayer(), EnumScreenAction.CLOSE);
                            new ATMMenu.BankMenu(response.getPlayer()).open(response.getPlayer());
                            break;
                    }
                }
            }

        }

    }

    public enum Asset {

        TRANSFER_MENU("https://cdn.discordapp.com/attachments/726166328825741343/883114220600844368/unknown.png"),

        DEPOSIT_MENU("https://cdn.discordapp.com/attachments/726166328825741343/883113899912757248/unknown.png"),

        WITHDRAW_MENU("https://cdn.discordapp.com/attachments/726166328825741343/883113993634455602/unknown.png"),

        BACKGROUND("https://cdn.discordapp.com/attachments/726166328825741343/883079505034494014/unknown.png"),

        OVERVIEW_BACKGROUND("https://cdn.discordapp.com/attachments/726166328825741343/883297420321116231/unknown.png");

        @Getter private final String url;

        Asset(String url) {
            this.url = url;
        }

        @Override
        public String toString() {
            return url;
        }

    }

}
