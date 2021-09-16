package dev.maiky.minetopia.modules.data.managers.mongo;

import dev.maiky.minetopia.modules.data.managers.Manager;
import dev.maiky.minetopia.modules.transportation.portal.ILocation;
import dev.maiky.minetopia.modules.transportation.portal.PortalData;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

public class MongoPortalManager extends Manager<PortalData> {

    public MongoPortalManager() {
        super(PortalData.class);
    }

    public void createPortal(String name, Location location, String server) {
        PortalData data = new PortalData();
        data.setName(name);
        data.setLocation(ILocation.from(location));
        data.setServer(server);

        super.save(data);
    }

    public HashMap<String, PortalData> getPortals() {
        return new HashMap<>(super.getDao().find().asList().stream().collect(Collectors.toMap(d -> d.name, d -> d)));
    }


}
