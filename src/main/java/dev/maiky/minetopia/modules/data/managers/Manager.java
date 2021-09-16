package dev.maiky.minetopia.modules.data.managers;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;
import dev.maiky.minetopia.modules.data.DataModule;
import lombok.Getter;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.dao.BasicDAO;

import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * This project is owned by Maiky Perlee - © 2021
 */

public abstract class Manager <T> {

    @Getter private final Class<T> clazz;

    @Getter private final Morphia morphia;
    @Getter private final Datastore datastore;
    @Getter private final DAO<T> dao;

    @SuppressWarnings("unchecked")
    public Manager(Class<T> clazz) {
        this.clazz = clazz;

        MongoClient mongoClient = DataModule.getInstance().getMongoClient();
        this.morphia = new Morphia();

        this.datastore = this.morphia.createDatastore(mongoClient, "cokemt_minetopia");
        this.datastore.ensureIndexes();

        this.dao = new DAO<>(clazz, this.datastore);
        if (this.dao.find().asList().size() == 0)
            this.dao.getCollection().insert(new BasicDBObject("autoincrement", 0)
                    .append("auto", true));
    }

    public Stream<T> find(Predicate<T> predicate) {
        return dao.find().asList().stream().filter(predicate);
    }

    public Key<T> save(T object) {
        return dao.save(object);
    }

    public WriteResult delete(T object) {
        return dao.delete(object);
    }

    public static class DAO <T> extends BasicDAO<T, String> {

        public DAO(Class<T> entityClass, Datastore ds) {
            super(entityClass, ds);
        }

    }

}
