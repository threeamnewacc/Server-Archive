package net.badlion.combattag;

import net.badlion.combattag.listeners.CombatTagListener;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatTagPlugin extends JavaPlugin {

    private static CombatTagPlugin plugin;

    private Map<UUID, Long> combatTaggedPlayers = new HashMap<>();
    private Map<UUID, LoggerNPC> combatLogNPC = new HashMap<>();

    private boolean tagDamager;
    private int combatTagLengthInMilliseconds;

    public CombatTagPlugin() {
        CombatTagPlugin.plugin = this;
    }

    @Override
    public void onEnable() {
        this.tagDamager = this.getConfig().getBoolean("tag-damager");
        this.combatTagLengthInMilliseconds = this.getConfig().getInt("combat-tag-length-in-millis", 30000);

        this.getServer().getPluginManager().registerEvents(new CombatTagListener(), this);
    }

    @Override
    public void onDisable() {

    }

	public boolean isCombatLogger(Entity entity) {
		return entity instanceof Zombie && entity.hasMetadata("CombatLoggerNPC");
	}

	public LoggerNPC getCombatLoggerFromEntity(Entity entity) {
		if (this.isCombatLogger(entity)) {
			return CombatTagPlugin.getInstance().getLogger((UUID) entity.getMetadata("CombatLoggerNPC").get(0).value());
		}

		return null;
	}

    public void addCombatTagged(Player player) {
        this.addCombatTagged(player.getUniqueId());
    }

    public void addCombatTagged(UUID uuid) {
        this.combatTaggedPlayers.put(uuid, System.currentTimeMillis() + 30000);
    }

    public void removeCombatTagged(Player player) {
        this.removeCombatTagged(player.getUniqueId());
    }

    public void removeCombatTagged(UUID uuid) {
        this.combatTaggedPlayers.remove(uuid);
        this.combatLogNPC.remove(uuid);
    }

    public boolean isInCombat(Player player) {
        return this.isInCombat(player.getUniqueId());
    }

    public boolean isInCombat(UUID uuid) {
        if (this.getRemainingCombatTagTime(uuid) < 0) {
            this.removeCombatTagged(uuid);
            return false;
        }

        return true;
    }

    public int getRemainingCombatTagTime(Player player) {
        return this.getRemainingCombatTagTime(player.getUniqueId());
    }

    public int getRemainingCombatTagTime(UUID uuid) {
        Long time = this.combatTaggedPlayers.get(uuid);

        if (time == null) {
            return -1;
        }

        return (int) (time - System.currentTimeMillis());
    }

	public static CombatTagPlugin getInstance() {
		return CombatTagPlugin.plugin;
	}

    public LoggerNPC getLogger(UUID uuid) {
        return this.combatLogNPC.get(uuid);
    }

    public Collection<LoggerNPC> getAllLoggers() {
        return this.combatLogNPC.values();
    }

    public boolean addCombatTagLogger(UUID uuid, LoggerNPC npc) {
        return this.combatLogNPC.put(uuid, npc) == npc;
    }

    public LoggerNPC removeCombatTagLogger(UUID uuid) {
        return this.combatLogNPC.remove(uuid);
    }

    public boolean isTagDamager() {
        return tagDamager;
    }

    public int getCombatTagLengthInMilliseconds() {
        return combatTagLengthInMilliseconds;
    }

}
