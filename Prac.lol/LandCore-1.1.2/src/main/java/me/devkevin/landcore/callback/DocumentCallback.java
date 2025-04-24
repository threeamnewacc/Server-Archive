package me.devkevin.landcore.callback;

import org.bson.Document;

public interface DocumentCallback {
    void call(Document document, boolean found);
}
