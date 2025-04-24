package me.devkevin.landcore.event;

import me.devkevin.landcore.player.CoreProfile;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 16/01/2023 @ 12:34
 * CoreProfileEvent / land.pvp.core.event / LandCore
 */
@Getter
@RequiredArgsConstructor
public class CoreProfileEvent extends BaseEvent {
    private final CoreProfile profile;

    public UUID getUniqueId() {
        return this.profile.getId();
    }
}
