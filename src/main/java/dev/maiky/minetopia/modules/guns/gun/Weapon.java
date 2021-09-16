package dev.maiky.minetopia.modules.guns.gun;

import lombok.Getter;
import lombok.Setter;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;

/**
 * Door: Maiky
 * Info: Minetopia - 09 Jun 2021
 * Package: dev.maiky.minetopia.modules.guns.gun
 */

@Entity(value = "weapons", noClassnameStored = true)
public class Weapon {

	@Getter
	public String license;

	@Getter @Setter
	public int ammo;

	@Getter @Setter
	public int durability;

	@Getter
	@Property("model_name")
	public String modelName;

	@Getter @Setter
	@Property("row_id")
	public int rowId;

	public Weapon() {
	}

}
