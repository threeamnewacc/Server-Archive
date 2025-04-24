package me.devkevin.landcore.faction.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 21/03/2023 @ 0:39
 * FactionMember / me.devkevin.landcore.faction.member / LandCore
 */
@Getter
@Setter
@AllArgsConstructor
public class FactionMember {
    private final UUID uuid;
    private final FactionMemberType type;
}
