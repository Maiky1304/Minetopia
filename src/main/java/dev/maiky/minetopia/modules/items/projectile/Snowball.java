package dev.maiky.minetopia.modules.items.projectile;

import dev.maiky.minetopia.modules.items.MinetopiaProjectile;
import org.bukkit.entity.EntityType;

/**
 * Door: Maiky
 * Info: Minetopia - 23 May 2021
 * Package: dev.maiky.minetopia.modules.weapons.projectile
 */

public class Snowball implements MinetopiaProjectile {

	@Override
	public EntityType material() {
		return EntityType.SNOWBALL;
	}

	@Override
	public String damagerMessage() {
		return "&6Je hebt &c%s &6geraakt met een sneeuwbal.";
	}

	@Override
	public String victimMessage() {
		return "&6Je bent geraakt met een sneeuwbal door &c%s&6.";
	}

	@Override
	public String[] exclusions() {
		return new String[]{"minetopia_bullet"};
	}

}
