package dev.maiky.minetopia.modules.transportation.portal;

import lombok.Getter;
import lombok.Setter;
import me.lucko.helper.mongo.external.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Property;

/**
 * Door: Maiky
 * Info: Minetopia - 25 May 2021
 * Package: dev.maiky.minetopia.modules.transportation.portal
 */

@Entity(value = "portals", noClassnameStored = true)
public class PortalData {

	@Getter @Setter
	@Id
	public ObjectId id;

	@Getter @Setter
	public String name;

	@Getter @Setter
	@Embedded @Property("bukkit_location")
	public ILocation location;

	@Getter @Setter
	@Property("target_server")
	public String server;

	public PortalData() {}

}
