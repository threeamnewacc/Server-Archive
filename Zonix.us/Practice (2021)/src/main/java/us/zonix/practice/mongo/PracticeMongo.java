package us.zonix.practice.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.Collections;
import org.bson.Document;
import org.bukkit.configuration.file.FileConfiguration;
import us.zonix.practice.Practice;

public class PracticeMongo {
    private static PracticeMongo instance;
    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> players;

    public PracticeMongo() {
        if (instance != null) {
            throw new RuntimeException("The mongo database has already been instantiated.");
        } else {
            instance = this;
            FileConfiguration config = Practice.getInstance().getMainConfig().getConfiguration();
            if (config.contains("mongo.host")
                && config.contains("mongo.port")
                && config.contains("mongo.database")
                && config.contains("mongo.authentication.enabled")
                && config.contains("mongo.authentication.username")
                && config.contains("mongo.authentication.password")
                && config.contains("mongo.authentication.database")) {
                if (config.getBoolean("mongo.authentication.enabled")) {
                    MongoCredential credential = MongoCredential.createCredential(
                        config.getString("mongo.authentication.username"),
                        config.getString("mongo.authentication.database"),
                        config.getString("mongo.authentication.password").toCharArray()
                    );
                    this.client = new MongoClient(
                        new ServerAddress(config.getString("mongo.host"), config.getInt("mongo.port")), Collections.singletonList(credential)
                    );
                } else {
                    this.client = new MongoClient(new ServerAddress(config.getString("mongo.host"), config.getInt("mongo.port")));
                }

                this.database = this.client.getDatabase(config.getString("mongo.database"));
                this.players = this.database.getCollection("players");
            } else {
                throw new RuntimeException("Missing configuration option");
            }
        }
    }

    public MongoClient getClient() {
        return this.client;
    }

    public MongoDatabase getDatabase() {
        return this.database;
    }

    public MongoCollection<Document> getPlayers() {
        return this.players;
    }

    public static PracticeMongo getInstance() {
        return instance;
    }
}
