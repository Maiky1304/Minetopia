package dev.maiky.minetopia.modules.plots.classes;

import dev.maiky.minetopia.modules.transportation.portal.ILocation;
import lombok.Getter;
import lombok.Setter;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

public class Selection {

    private @Getter @Setter ILocation pos1, pos2;

    public Selection(ILocation pos1, ILocation pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
    }

}
