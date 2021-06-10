package dev.maiky.minetopia.modules.items;

import org.bukkit.entity.EntityType;

public interface MinetopiaProjectile {

	EntityType material();
	String damagerMessage();
	String victimMessage();
	String[] exclusions();

}
