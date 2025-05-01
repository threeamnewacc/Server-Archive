package net.badlion.tournament.teams;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class DefaultTeam implements Team {

    private UUID id;
    private String name = "default";
    private UUID leader;
    private Set<UUID> uuids = new HashSet<>();
    private Map<UUID, String> roles = new HashMap<>();

    public DefaultTeam() {
        this(UUID.randomUUID(), "null", null);
    }

    public DefaultTeam(UUID leader) {
        this(UUID.randomUUID(), Bukkit.getPlayer(leader) != null ? Bukkit.getPlayer(leader).getName() : leader.toString(), leader);
    }

    public DefaultTeam(String name, UUID leader) {
        this(UUID.randomUUID(), name, leader);
    }

    public DefaultTeam(UUID id, String name, UUID leader) {
        this.id = id;
        this.setName(name);
        this.setLeader(leader);
        this.addMember(leader);
    }

    @Override
    public UUID getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public UUID getLeader() {
        return leader;
    }

    @Override
    public void setLeader(UUID uuid) {
        this.leader = uuid;
    }

    @Override
    public String getType() {
        return "default";
    }

    @Override
    public boolean hasUUID(UUID uuid) {
        return this.getUUIDs().contains(uuid) || this.getLeader().equals(uuid);
    }

    @Override
    public Set<UUID> getUUIDs() {
        return uuids;
    }

    @Override
    public void setUUIDs(Set<UUID> uuids) {
        this.uuids = uuids;
    }

    @Override
    public String getRole(UUID uuid) {
        if (!this.hasUUID(uuid)) {
            return "Not on Team";
        }

        if (this.getRoles().get(uuid) != null) {
            return this.getRoles().get(uuid);
        }

        if (this.getLeader().equals(uuid)) {
            return "Leader";
        }

        return "None";
    }

    @Override
    public Map<UUID, String> getRoles() {
        return roles;
    }

    @Override
    public void setRoles(Map<UUID, String> roles) {
        this.roles = roles;
    }

    @Override
    public boolean addMember(UUID uuid) {
        return this.getUUIDs().add(uuid);
    }

    @Override
    public boolean removeMember(UUID uuid) {
        return this.getUUIDs().remove(uuid);
    }

    @Override
    public DefaultTeam clone() {
        return new DefaultTeam(this.getName(), this.getLeader());
    }

    @Override
    public void sendMessage(String message) {
        for (UUID uuid : this.getUUIDs()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.sendMessage(message);
            }
        }
    }
}
