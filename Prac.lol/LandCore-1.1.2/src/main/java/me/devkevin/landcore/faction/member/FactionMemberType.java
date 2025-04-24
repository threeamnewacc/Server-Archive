package me.devkevin.landcore.faction.member;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 21/03/2023 @ 0:40
 * FactionMemberType / me.devkevin.landcore.faction.member / LandCore
 */
@Getter
@AllArgsConstructor
public enum FactionMemberType {
    LEADER("Leader", 0),
    CAPTAIN("Captain", 1),
    MEMBER("Member", 2);

    private final String name;
    private final int weight;
}
