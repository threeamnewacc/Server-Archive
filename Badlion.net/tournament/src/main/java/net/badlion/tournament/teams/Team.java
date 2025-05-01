package net.badlion.tournament.teams;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface Team extends Cloneable {

    /**
     * Get the ID of the team
     */
    UUID getID();

    /**
     * Get name of the team
     */
    String getName();

    /**
     * et nameS
     */
    void setName(String name);

    /**
     * Get the type of team
     */
    String getType();

    /**
     * Get Leader
     */
    UUID getLeader();

    /**
     * Set Leader
     */
    void setLeader(UUID uuid);

    /**
     * Check if a team has a certain member
     */
    boolean hasUUID(UUID uuid);

    /**
     * Set uuids
     */
    void setUUIDs(Set<UUID> uuids);

    /**
     * Get all uuids of team
     */
    Set<UUID> getUUIDs();

    /**
     * Get the role of a specific team member
     */
    String getRole(UUID uuid);

    /**
     * Get the role of each team member
     */
    Map<UUID, String> getRoles();

    /**
     * Set the role for the team members
     */
    void setRoles(Map<UUID, String> roles);

    /**
     * Add member to team
     */
    boolean addMember(UUID uuid);

    /**
     * Remove member from team
     */
    boolean removeMember(UUID uuid);

    /**
     * Clone object
     */
    Team clone();

    /**
     * Send a message to the entire team
     */
    void sendMessage(String message);

}
