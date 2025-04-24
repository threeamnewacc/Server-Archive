package me.devkevin.practice.data;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import me.devkevin.practice.Practice;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright 22/05/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
@Getter
public class PracticeDatabase {

    @Getter private static PracticeDatabase instance;

    private MongoClient client;
    private MongoDatabase database;

    private MongoCollection<Document> profiles;
    private MongoCollection<Document> matchHistory;
    private MongoCollection<Document> votesCollection;
    private MongoCollection<Document> factions;

    public PracticeDatabase(Practice plugin) {
        if (instance != null) {
            throw new RuntimeException("The mongo database has already been instantiated.");
        }

        instance = this;
        if (plugin.getConfig().getBoolean("DATABASE.MONGO.AUTHENTICATION.ENABLED")) {

            List<MongoCredential> credentials = new ArrayList<>();
            credentials.add(MongoCredential.createCredential(
                    plugin.getConfig().getString("DATABASE.MONGO.AUTHENTICATION.USER"),
                    plugin.getConfig().getString("DATABASE.MONGO.AUTHENTICATION.DATABASE"),
                    plugin.getConfig().getString("DATABASE.MONGO.AUTHENTICATION.PASSWORD").toCharArray()));
            client = new MongoClient(new ServerAddress(
                    plugin.getConfig().getString("DATABASE.MONGO.HOST"),
                    plugin.getConfig().getInt("DATABASE.MONGO.PORT")),
                    credentials);
        } else {
            client = new MongoClient(new ServerAddress(
                    plugin.getConfig().getString("DATABASE.MONGO.HOST"),
                    plugin.getConfig().getInt("DATABASE.MONGO.PORT")));
        }

        database = client.getDatabase("Practice");
        profiles = database.getCollection("profiles");
        matchHistory = database.getCollection("matchHistory");
        votesCollection = database.getCollection("practice_votes");
        factions = database.getCollection("factions");
    }
}
