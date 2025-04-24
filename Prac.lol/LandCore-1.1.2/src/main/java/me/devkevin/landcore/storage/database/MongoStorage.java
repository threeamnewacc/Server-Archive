package me.devkevin.landcore.storage.database;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.callback.DocumentCallback;
import lombok.Getter;
import org.bson.Document;

import java.util.Map;

@Getter
public class MongoStorage {
    @Getter private static MongoStorage instance;

    private final MongoDatabase database;
    private final MongoCollection<Document> disguiseCollection;
    private final MongoCollection<Document> factionsCollection;
    private final MongoCollection<Document> factionsPlayersCollection;

    private final boolean connected;

    public MongoStorage(LandCore plugin) {
        if (instance != null) {
            throw new RuntimeException("The mongo database has already been instantiated.");
        }

        instance = this;

        MongoClient mongoClient = MongoClients.create(
                LandCore.getInstance().getConfig().getString("mongodb.uri")
        );

        database = mongoClient.getDatabase("land_core");

        disguiseCollection = this.database.getCollection("disguises");
        factionsCollection = this.database.getCollection("factions");
        factionsPlayersCollection = this.database.getCollection("playersFactions");
        connected = true;
    }

    public void getOrCreateDocument(String collectionName, Object documentObject, DocumentCallback callback) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        Document document = new Document("_id", documentObject);

        try (MongoCursor<Document> cursor = collection.find(document).iterator()) {
            if (cursor.hasNext()) {
                callback.call(cursor.next(), true);
            } else {
                collection.insertOne(document);
                callback.call(document, false);
            }
        }
    }

    public MongoCursor<Document> getAllDocuments(String collectionName) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        return collection.find().iterator();
    }

    public Document getDocumentByFilter(String collectionName, String filter, Object documentObject) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        return collection.find(Filters.eq(filter, documentObject)).first();
    }

    public FindIterable<Document> getDocumentsByFilter(String collectionName, Object documentObject) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        return collection.find(Filters.eq("_id", documentObject));
    }

    public Document getDocument(String collectionName, Object documentObject) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        return collection.find(Filters.eq("_id", documentObject)).first();
    }

    public void updateDocument(String collectionName, Object documentObject, String key, Object newValue) {
        MongoCollection<Document> collection = database.getCollection(collectionName);
        collection.updateOne(Filters.eq(documentObject), Updates.set(key, newValue));
    }

    public void massUpdate(String collectionName, Object documentObject, Map<String, Object> updates) {
        MongoCollection<Document> collection = database.getCollection(collectionName);

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            collection.updateOne(Filters.eq(documentObject), Updates.set(entry.getKey(), entry.getValue()));
        }
    }
}
