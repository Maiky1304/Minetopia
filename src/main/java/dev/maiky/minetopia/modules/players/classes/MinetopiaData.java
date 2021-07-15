package dev.maiky.minetopia.modules.players.classes;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

public class MinetopiaData {

    private @Getter @Setter MinetopiaInventory inventory;
    private @Getter @Setter double hp;
    private @Getter @Setter int saturation;
    private @Getter @Setter double balance;
    private final @Getter UUID uuid;

    public MinetopiaData(MinetopiaInventory inventory, int hp, int saturation, UUID uuid) {
        this.inventory = inventory;
        this.hp = hp;
        this.saturation = saturation;
        this.uuid = uuid;
    }

}
