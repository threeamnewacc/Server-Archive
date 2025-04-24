package me.devkevin.util;

import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public final class Constants {

    public static final ResponseEntity<JsonObject> PLAYER_NOT_FOUND;
    public static final ResponseEntity<JsonObject> INVALID_KEY;
    public static final ResponseEntity<JsonObject> ALREADY_HAVE_PREFIX;
    public static final ResponseEntity<JsonObject> ALREADY_HAVE_KIT;
    public static final ResponseEntity<JsonObject> ALREADY_HAVE_DISGUISE;
    public static final ResponseEntity<JsonObject> PREFIX_NOT_FOUND;
    public static final ResponseEntity<JsonObject> KIT_NOT_FOUND;
    public static final ResponseEntity<JsonObject> SUCCESS;

    public static final ResponseEntity<String> PLAYER_NOT_FOUND_STRING;
    public static final ResponseEntity<String> INVALID_KEY_STRING;
    public static final ResponseEntity<String> ALREADY_HAVE_PREFIX_STRING;
    public static final ResponseEntity<String> ALREADY_HAVE_KIT_STRING;
    public static final ResponseEntity<String> ALREADY_HAVE_DISGUISE_STRING;
    public static final ResponseEntity<String> PREFIX_NOT_FOUND_STRING;
    public static final ResponseEntity<String> KIT_NOT_FOUND_STRING;
    public static final ResponseEntity<String> SUCCESS_STRING;

    public static final String API_KEY = "czmxnvbDFJGfdf87634asdfjgcv";

    public static final int PRACTICE_SEASON = 1;

    static {
        JsonObject object = new JsonObject();
        object.addProperty("response", "player-not-found");
        PLAYER_NOT_FOUND = new ResponseEntity<>(object, HttpStatus.OK);

        object = new JsonObject();
        object.addProperty("response", "invalid-key");
        INVALID_KEY = new ResponseEntity<>(object, HttpStatus.FORBIDDEN);

        object = new JsonObject();
        object.addProperty("response", "already-have-prefix");
        ALREADY_HAVE_PREFIX = new ResponseEntity<>(object, HttpStatus.OK);

        object = new JsonObject();
        object.addProperty("response", "already-have-kit");
        ALREADY_HAVE_KIT = new ResponseEntity<>(object, HttpStatus.OK);

        object = new JsonObject();
        object.addProperty("response", "already-have-disguise");
        ALREADY_HAVE_DISGUISE = new ResponseEntity<>(object, HttpStatus.OK);

        object = new JsonObject();
        object.addProperty("response", "prefix-not-found");
        PREFIX_NOT_FOUND = new ResponseEntity<>(object, HttpStatus.OK);

        object = new JsonObject();
        object.addProperty("response", "prefix-not-found");
        KIT_NOT_FOUND = new ResponseEntity<>(object, HttpStatus.OK);

        object = new JsonObject();
        object.addProperty("response", "success");
        SUCCESS = new ResponseEntity<>(object, HttpStatus.OK);

        PLAYER_NOT_FOUND_STRING = new ResponseEntity<>(PLAYER_NOT_FOUND.getBody().toString(), HttpStatus.OK);
        INVALID_KEY_STRING = new ResponseEntity<>(INVALID_KEY.getBody().toString(), HttpStatus.FORBIDDEN);
        ALREADY_HAVE_PREFIX_STRING = new ResponseEntity<>(ALREADY_HAVE_PREFIX.getBody().toString(), HttpStatus.OK);
        ALREADY_HAVE_KIT_STRING = new ResponseEntity<>(ALREADY_HAVE_KIT.getBody().toString(), HttpStatus.OK);
        ALREADY_HAVE_DISGUISE_STRING = new ResponseEntity<>(ALREADY_HAVE_DISGUISE.getBody().toString(), HttpStatus.OK);
        PREFIX_NOT_FOUND_STRING = new ResponseEntity<>(PREFIX_NOT_FOUND.getBody().toString(), HttpStatus.OK);
        KIT_NOT_FOUND_STRING = new ResponseEntity<>(KIT_NOT_FOUND.getBody().toString(), HttpStatus.OK);
        SUCCESS_STRING = new ResponseEntity<>(SUCCESS.getBody().toString(), HttpStatus.OK);
    }

    public static boolean validServerKey(String key) {
        return key.equals(Constants.API_KEY);
    }
}
