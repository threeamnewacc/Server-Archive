package me.devkevin.landcore.nametag.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.ScoreboardTeamBase;
import org.bukkit.entity.Player;

/**
 * A class that can 'provide' nametags for players.
 */
@Getter
@AllArgsConstructor
public abstract class NametagProvider {

    private String name;
    private int weight;

    /**
     * Takes in the player to refresh and who to refresh that player for,
     * and returns the prefix and suffix they should be given.
     *
     * @param toRefresh  The player whose nametag is getting refreshed.
     * @param refreshFor The player who this nametag change is going to be visible to.
     * @return The nametag that refreshFor should see above toRefresh's head.
     */
    public abstract InternalNametag.NametagInfo fetchNametag(Player toRefresh, Player refreshFor);

    /**
     * Wrapper method to create a NametagInfo object.
     *
     * @param prefix The prefix the nametag has.
     * @param suffix The suffix the nametag has.
     * @param tagVisibility The visibility the nametag has.
     * @return The created NametagInfo object.
     */
    public static InternalNametag.NametagInfo createNametag(String prefix, String suffix, ScoreboardTeamBase.EnumNameTagVisibility tagVisibility) {
        return (InternalNametag.getOrCreate(prefix, suffix, tagVisibility));
    }

    protected static final class DefaultNametagProvider extends NametagProvider {

        public DefaultNametagProvider() {
            super("Default Provider", 0);
        }

        @Override
        public InternalNametag.NametagInfo fetchNametag(Player toRefresh, Player refreshFor) {
            return (createNametag("", "", ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS));
        }

    }
}

