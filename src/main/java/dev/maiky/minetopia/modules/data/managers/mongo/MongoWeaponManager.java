package dev.maiky.minetopia.modules.data.managers.mongo;

import dev.maiky.minetopia.modules.data.managers.Manager;
import dev.maiky.minetopia.modules.guns.gun.Weapon;
import dev.maiky.minetopia.modules.guns.models.interfaces.Model;
import dev.maiky.minetopia.util.Text;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

public class MongoWeaponManager extends Manager<Weapon> {

    public MongoWeaponManager() {
        super(Weapon.class);
    }

    public Weapon createWeapon(Model model) {
        Weapon weapon = new Weapon();
        weapon.license = Text.randomString(12).toUpperCase();
        weapon.ammo = model.defaultAmmo();
        weapon.durability = model.defaultAmmo() * 3;
        weapon.modelName = model.modelName();

        super.save(weapon);

        return weapon;
    }

    public Weapon getWeaponByLicense(String license) {
        return super.find(weapon -> weapon.getLicense().equals(license)).findFirst().orElse(null);
    }


}
