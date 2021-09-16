package dev.maiky.minetopia.modules.bags.bag;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexed;

import java.util.HashMap;

/**
 * Door: Maiky
 * Info: Minetopia - 29 May 2021
 * Package: dev.maiky.minetopia.modules.bags.bag
 */

@Entity(value = "bags", noClassnameStored = true)
public class Bag {

	@Getter
	@Id
	public ObjectId id;

	@Getter @Setter
	@Indexed(options = @IndexOptions(unique = true))
	public int bagId;

	@Getter @Setter
	public String base64Contents;

	@Getter
	public final HashMap<String, String> history = new HashMap<>();

	@Getter @Setter
	public int rows;

	@Getter @Setter
	public BagType type;

	public Bag() {}

}
