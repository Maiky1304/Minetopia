package dev.maiky.minetopia.modules.data.managers.mongo;

import dev.maiky.minetopia.modules.colors.packs.ChatColor;
import dev.maiky.minetopia.modules.colors.packs.LevelColor;
import dev.maiky.minetopia.modules.data.managers.Manager;
import dev.maiky.minetopia.modules.players.classes.*;
import dev.maiky.minetopia.util.Options;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.LinkedHashMap;
import java.util.UUID;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

public class MongoPlayerManager extends Manager<MinetopiaUser> {

    @Getter private static final LinkedHashMap<UUID, MinetopiaUser> cache = new LinkedHashMap<>();
    @Getter private static final LinkedHashMap<UUID, MinetopiaScoreboard> scoreboard = new LinkedHashMap<>();

    public MongoPlayerManager() {
        super(MinetopiaUser.class);
    }

    public MinetopiaUser create(UUID uuid, String name) {
        MinetopiaUser user = new MinetopiaUser();
        user.setUuid(uuid);

        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (!offlinePlayer.isOnline()) {
            user.setName(name);
        } else {
            user.setName(offlinePlayer.getName());
        }

        user.setLevel(Options.PLAYER_DEFAULT_LEVEL.asInt().get());
        user.setTime(new MinetopiaTime(0,0,0,0));
        user.setGrayshards(Options.PLAYER_DEFAULT_GRAYSHARDS.asDouble().get());
        user.setGoldshards(Options.PLAYER_DEFAULT_GOLDSHARDS.asDouble().get());
        user.setCityColor(Options.PLAYER_DEFAULT_CITYCOLOR.asString().get());
        user.setMinetopiaUpgrades(new MinetopiaUpgrades());
        user.setCurrentChatColor(ChatColor.CHATCOLOR_NORMAL_GRAY);
        user.setCurrentLevelColor(LevelColor.CHATCOLOR_NORMAL_AQUA);
        user.setCurrentPrefixColor(ChatColor.CHATCOLOR_NORMAL_GRAY);
        user.setCurrentPrefix(Options.PLAYER_DEFAULT_PREFIX.asString().get());
        user.getPrefixes().add(user.getCurrentPrefix());
        user.setScoreboard(true);
        user.setActionbar(true);
        user.setMinetopiaData(new MinetopiaData(offlinePlayer.isOnline() ? MinetopiaInventory.of(offlinePlayer.getPlayer().getInventory()) : MinetopiaInventory.empty(),
                20, 20, uuid));

        save(user);

        return user;
    }

}
