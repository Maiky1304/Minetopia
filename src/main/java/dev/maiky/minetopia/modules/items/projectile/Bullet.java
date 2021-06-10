package dev.maiky.minetopia.modules.items.projectile;

import dev.maiky.minetopia.modules.items.MinetopiaEntityColor;
import dev.maiky.minetopia.modules.items.MinetopiaProjectile;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;

/**
 * Door: Maiky
 * Info: Minetopia - 23 May 2021
 * Package: dev.maiky.minetopia.modules.weapons.projectile
 */

public class Bullet implements MinetopiaProjectile, MinetopiaEntityColor {

	@Override
	public EntityType material() {
		return EntityType.ARROW;
	}

	@Override
	public String damagerMessage() {
		return "&6Je hebt &c%s &6geraakt met een kogel.";
	}

	@Override
	public String victimMessage() {
		return "&6Je bent geraakt met een kogel door &c%s&6.";
	}

	@Override
	public ChatColor color() {
		return ChatColor.DARK_RED;
	}

	@Override
	public String[] exclusions() {
		return new String[]{
				"TASER_BULLET_PROJECTILE"
		};
	}

}
