package dev.maiky.minetopia.modules.items;

import org.bukkit.Material;

public interface MinetopiaWeapon {

	Material material();
	String name();
	String damagerMessage();
	String victimMessage();
	boolean damage();

}
