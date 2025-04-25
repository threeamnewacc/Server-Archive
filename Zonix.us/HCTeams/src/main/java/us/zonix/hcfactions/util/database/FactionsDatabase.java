package us.zonix.hcfactions.util.database;

import us.zonix.hcfactions.FactionsPlugin;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import us.zonix.hcfactions.FactionsPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FactionsDatabase {

    @Getter private MongoClient client;
    @Getter private MongoDatabase database;
    @Getter private MongoCollection profiles;
    @Getter private MongoCollection fights;
    @Getter private MongoCollection koths;
    @Getter private MongoCollection glowstone;
    @Getter private MongoCollection crates;
    @Getter private MongoCollection kits;
    @Getter private MongoCollection modes;

    public FactionsDatabase(FactionsPlugin main) {
        if (main.getMainConfig().getBoolean("DATABASE.MONGO.AUTHENTICATION.ENABLED")) {

            List<MongoCredential> credentials = new ArrayList<>();
            credentials.add(MongoCredential.createCredential(main.getMainConfig().getString("DATABASE.MONGO.AUTHENTICATION.USER"), main.getMainConfig().getString("DATABASE.MONGO.AUTHENTICATION.DATABASE"), main.getMainConfig().getString("DATABASE.MONGO.AUTHENTICATION.PASSWORD").toCharArray()));
            client = new MongoClient(new ServerAddress(main.getMainConfig().getString("DATABASE.MONGO.HOST"), main.getMainConfig().getInt("DATABASE.MONGO.PORT")), credentials);
        }
        else {
            client = new MongoClient(new ServerAddress(main.getMainConfig().getString("DATABASE.MONGO.HOST"), main.getMainConfig().getInt("DATABASE.MONGO.PORT")));
        }

        database = client.getDatabase(main.getMainConfig().getString("DATABASE.MONGO.DATABASE"));
        profiles = database.getCollection("profiles");
        fights = database.getCollection("fights");
        koths = database.getCollection("koths");
        glowstone = database.getCollection("glowstone");
        crates = database.getCollection("crates");
        kits = database.getCollection("kits");
        modes = database.getCollection("modes");
    }

}
