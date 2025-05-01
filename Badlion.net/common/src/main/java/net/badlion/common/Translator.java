package net.badlion.common;

import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Translator {

    private static String API_KEY = "";
    private static String PORT = "9000";
    private static Map<String, Language> languages = new HashMap<>();

    /**
     * Call to load the languages
     */
    public static void initialize() {
        JSONObject response;
        try {
            response = HTTPCommon.executeGETRequest("http://" + GetCommon.getIpForDB() + ":" + PORT + "/GetLanguages/" + API_KEY);
        } catch (HTTPRequestFailException e) {
            System.out.println("Error getting languages with " + e.getResponseCode());
            return;
        }

        if (response.containsKey("languages")) {
            for (String language : (List<String>) response.get("languages")) {
                try {
                    String languageFile = HTTPCommon.executeTextGETRequest("http://" + GetCommon.getIpForDB() + ":" + PORT + "/GetLanguage/" + language + "/" + API_KEY);
                    Translator.languages.put(language, new Language(language, languageFile));
                } catch (HTTPRequestFailException e) {
                    System.out.println("Error getting language (" + language + ") with " + e.getResponseCode());
                }
            }
        }
    }

    public static String translate(String lang, String key) {
        Language language = Translator.languages.get(lang);

        if (language == null) {
            return key;
        }

        return language.getTranslation(key);
    }

    public static class Language {

        private String language;
        private File file;
        private Map<String, String> translations = new LinkedHashMap<>();

        public Language(String language) {
            this.language = language;

            this.file = new File(language);
            this.loadFile();
        }

        public Language(String language, String toParse) {
            this.language = language;

            for (String line : toParse.split("\n")) {
                String[] parts = line.split("=");

                this.translations.put(parts[0], parts[1]);
            }
        }

        public void loadFile() {
            try {
                BufferedReader br = new BufferedReader(new FileReader(this.file));
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split("=");

                    this.translations.put(parts[0], parts[1]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void saveFile() {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(this.file));
                for (Map.Entry<String, String> entry : this.translations.entrySet()) {
                    bw.write(entry.getKey());
                    bw.write("=");
                    bw.write(entry.getValue());
                    bw.newLine();
                }

                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public String getLanguage() {
            return this.language;
        }

        public String getTranslation(final String key) {
            String val = this.translations.get(key);
            if (val == null) {
                // Submit a new translation
                final JSONObject jsonObject = new JSONObject();
                jsonObject.put("key", key);

                new Thread(new Runnable() {
                    public void run() {
                        try {
                            HTTPCommon.executePOSTRequest("http://" + GetCommon.getIpForDB() + ":" + PORT + "/SubmitMissingKey/" + API_KEY, jsonObject);
                        } catch (HTTPRequestFailException e) {
                            System.out.println("Error submitting key for (" + key + ") with response code " + e.getResponseCode());
                        }
                    }
                }).start();
                return key;
            }

            return val;
        }

    }

}
