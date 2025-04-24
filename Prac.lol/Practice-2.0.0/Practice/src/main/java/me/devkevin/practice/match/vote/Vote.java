package me.devkevin.practice.match.vote;

import club.inverted.chatcolor.CC;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 24/01/2023 @ 16:35
 * vote / me.devkevin.practice.match / Practice
 */
@Getter
@RequiredArgsConstructor
public enum Vote {
    ONE_STAR(CC.RED, 1),
    TWO_STARS(CC.YELLOW, 2),
    THREE_STARS(CC.YELLOW, 3),
    FOUR_STARS(CC.YELLOW, 4),
    FIVE_STARS(CC.GREEN, 5);

    private final String color;
    private final int vote;

    public String getDisplayName() {
        return this.color + "[" + this.vote + "\u272e]";
    }
}
