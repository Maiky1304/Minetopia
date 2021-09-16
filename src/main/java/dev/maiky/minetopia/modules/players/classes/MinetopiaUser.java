package dev.maiky.minetopia.modules.players.classes;

import dev.maiky.minetopia.modules.colors.packs.ChatColor;
import dev.maiky.minetopia.modules.colors.packs.LevelColor;
import dev.maiky.minetopia.modules.transportation.portal.LocalPortalData;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Door: Maiky
 * Info: Minetopia - 21 May 2021
 * Package: dev.maiky.minetopia.modules.players.classes
 */

@Entity(value = "users", noClassnameStored = true)
public class MinetopiaUser {

	@Id
	public ObjectId id;

	@Indexed(options = @IndexOptions(unique = true))
	@Getter @Setter
	public UUID uuid;

	@Getter @Setter
	public String name;

	@Getter @Setter
	public int level;

	@Getter @Setter
	public MinetopiaTime time;

	@Getter @Setter
	public double grayshards;

	@Getter @Setter
	public double goldshards;

	@Getter @Setter
	@Property("city_color")
	public String cityColor;

	@Getter @Setter
	@Property("upgrades")
	public MinetopiaUpgrades minetopiaUpgrades;

	@Getter @Setter
	@Property("level_points")
	public int levelPoints;

	@Getter @Setter
	@Property("police_chat_enabled")
	public boolean policeChat;

	@Getter @Setter
	@Property("current_prefix")
	public String currentPrefix;

	@Getter
	public final List<String> prefixes = new ArrayList<>();

	@Getter @Setter
	@Property("current_chatcolor")
	public ChatColor currentChatColor;

	@Getter
	@Property("chatcolors")
	public final HashMap<ChatColor, String> chatColors = new HashMap<>();

	@Getter @Setter
	@Property("current_levelcolor")
	public LevelColor currentLevelColor;

	@Getter
	@Property("levelcolors")
	public final HashMap<LevelColor, String> levelColors = new HashMap<>();

	@Getter @Setter
	@Property("current_prefixcolor")
	public ChatColor currentPrefixColor;

	@Getter
	@Property("prefixcolors")
	public final HashMap<ChatColor, String> prefixColors = new HashMap<>();
	@Getter @Setter
	public boolean scoreboard;

	@Getter @Setter
	public boolean actionbar;

	@Getter @Setter
	@Property("grayshard_boost")
	public int grayshardBoost;

	@Getter @Setter
	@Property("goldshard_boost")
	public int goldshardBoost;

	@Getter @Setter
	@Property("portal_data")
	public LocalPortalData portalData;

	@Getter @Setter
	@Property("minetopia_data")
	public MinetopiaData minetopiaData;

	@Getter @Setter
	@Property("cash_old") @Transient
	public double cash = 0;

	@Getter @Setter
	@Property("dirty_money")
	public double blackMoney = 0;

	public MinetopiaUser() {
	}

}
