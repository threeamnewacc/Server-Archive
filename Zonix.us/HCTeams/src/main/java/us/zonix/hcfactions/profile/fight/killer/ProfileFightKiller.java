package us.zonix.hcfactions.profile.fight.killer;

import lombok.Getter;
import org.apache.commons.lang.WordUtils;
import org.bukkit.entity.EntityType;

public class ProfileFightKiller {

    @Getter private final EntityType entityType;
    @Getter private final String name;

    public ProfileFightKiller(EntityType entityType, String name) {
        this.entityType = entityType;
        this.name = name;
    }

    public ProfileFightKiller(EntityType entityType) {
        this(entityType, WordUtils.capitalize(entityType.name()));
    }
}
