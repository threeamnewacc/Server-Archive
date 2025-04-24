package me.devkevin.landcore.server.filter;

import lombok.Getter;

@Getter
class NegativeWordPair {
    private final String word;
    private final String[] matches;

    NegativeWordPair(String word, String... matches) {
        this.word = word;
        this.matches = matches;
    }
}
