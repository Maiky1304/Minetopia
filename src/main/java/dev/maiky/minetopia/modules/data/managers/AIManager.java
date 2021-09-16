package dev.maiky.minetopia.modules.data.managers;

import com.mongodb.BasicDBObject;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

public class AIManager <T> extends Manager<T> {

    public AIManager(Class<T> clazz) {
        super(clazz);

        if (!super.getDao().getCollection().find().hasNext()) {
            super.getDao().getCollection().insert(new BasicDBObject("autoincrement", true).append("value", 0));
        }
    }

    public int increment() {
        BasicDBObject data = (BasicDBObject) super.getDao().getCollection().findOne(new BasicDBObject("autoincrement", true));

        int oldValue = data.getInt("value");
        int newValue = oldValue + 1;

        super.getDao().getCollection().update(data, new BasicDBObject("$set",
                new BasicDBObject("value", newValue)));

        return newValue;
    }

}
