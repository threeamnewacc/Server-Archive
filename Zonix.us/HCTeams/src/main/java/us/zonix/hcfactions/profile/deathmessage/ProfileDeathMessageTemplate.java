package us.zonix.hcfactions.profile.deathmessage;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public enum ProfileDeathMessageTemplate {
    BLOCK_EXPLOSION(new ComponentBuilder("").append("%PLAYER%").color(ChatColor.RED).append("[%PLAYER_KILLS%]").color(ChatColor.DARK_RED).append(" was blown up.").color(ChatColor.YELLOW).create()),
    CONTACT(new ComponentBuilder("").append("%PLAYER%").color(ChatColor.RED).append("[%PLAYER_KILLS%]").color(ChatColor.DARK_RED).append(" ran into a cactus.").color(ChatColor.YELLOW).create()),

    CUSTOM(new ComponentBuilder("").append("%PLAYER%").color(ChatColor.RED).append("[%PLAYER_KILLS%]").color(ChatColor.DARK_RED).append(" died.").color(ChatColor.YELLOW).create()),
    DROWNING(new ComponentBuilder("").append("%PLAYER%").color(ChatColor.RED).append("[%PLAYER_KILLS%]").color(ChatColor.DARK_RED).append(" drowned.").color(ChatColor.YELLOW).create()),
    LOGGER(new ComponentBuilder("").append("%PLAYER%").color(ChatColor.RED).append("[%PLAYER_KILLS%]").color(ChatColor.DARK_RED).append(ChatColor.GRAY + " (Combat-Logger)" + ChatColor.DARK_RED + " was killed by ").color(ChatColor.YELLOW).append("%KILLER%").color(ChatColor.RED).append("[%KILLER_KILLS%]").color(ChatColor.DARK_RED).append(" using ").color(ChatColor.YELLOW).append("%WEAPON%").color(ChatColor.RED).create()),
    PLAYER(new ComponentBuilder("").append("%PLAYER%").color(ChatColor.RED).append("[%PLAYER_KILLS%]").color(ChatColor.DARK_RED).append(" was killed by ").color(ChatColor.YELLOW).append("%KILLER%").color(ChatColor.RED).append("[%KILLER_KILLS%]").color(ChatColor.DARK_RED).append(" using ").color(ChatColor.YELLOW).append("%WEAPON%").color(ChatColor.RED).create()),
    MOB(new ComponentBuilder("").append("%PLAYER%").color(ChatColor.RED).append("[%PLAYER_KILLS%]").color(ChatColor.DARK_RED).append(" was killed by a ").color(ChatColor.YELLOW).append("%MOB%").color(ChatColor.RED).create()),
    ENTITY_EXPLOSION(new ComponentBuilder("").append("%PLAYER%").color(ChatColor.RED).append("[%PLAYER_KILLS%]").color(ChatColor.DARK_RED).append(" was blown up by a ").color(ChatColor.YELLOW).append("%MOB%").color(ChatColor.RED).create()),
    FALL(new ComponentBuilder("").append("%PLAYER%").color(ChatColor.RED).append("[%PLAYER_KILLS%]").color(ChatColor.DARK_RED).append(" hit the ground too hard.").color(ChatColor.YELLOW).create()),
    FALLING_BLOCK(new ComponentBuilder("").append("%PLAYER%").color(ChatColor.RED).append("[%PLAYER_KILLS%]").color(ChatColor.DARK_RED).append(" was hit on the head by an anvil.").color(ChatColor.YELLOW).create()),
    FIRE(new ComponentBuilder("").append("%PLAYER%").color(ChatColor.RED).append("[%PLAYER_KILLS%]").color(ChatColor.DARK_RED).append(" burnt to a crisp.").color(ChatColor.YELLOW).create()),
    FIRE_TICK(new ComponentBuilder("").append("%PLAYER%").color(ChatColor.RED).append("[%PLAYER_KILLS%]").color(ChatColor.DARK_RED).append(" burnt to a crisp.").color(ChatColor.YELLOW).create()),
    LAVA(new ComponentBuilder("").append("%PLAYER%").color(ChatColor.RED).append("[%PLAYER_KILLS%]").color(ChatColor.DARK_RED).append(" tried to swim in lava.").color(ChatColor.YELLOW).create()),
    LIGHTNING(new ComponentBuilder("").append("%PLAYER%").color(ChatColor.RED).append("[%PLAYER_KILLS%]").color(ChatColor.DARK_RED).append(" was struck by lightning.").color(ChatColor.YELLOW).create()),
    MAGIC(new ComponentBuilder("").append("%PLAYER%").color(ChatColor.RED).append("[%PLAYER_KILLS%]").color(ChatColor.DARK_RED).append(" died to magic.").color(ChatColor.YELLOW).create()),
    MELTING(new ComponentBuilder("").append("%PLAYER%").color(ChatColor.RED).append("[%PLAYER_KILLS%]").color(ChatColor.DARK_RED).append(" died to a snowman melting?").color(ChatColor.YELLOW).create()),
    POSION(new ComponentBuilder("").append("%PLAYER%").color(ChatColor.RED).append("[%PLAYER_KILLS%]").color(ChatColor.DARK_RED).append(" was poisoned to death.").color(ChatColor.YELLOW).create()),
    PROJECTILE(new ComponentBuilder("").append("%PLAYER%").color(ChatColor.RED).append("[%PLAYER_KILLS%]").color(ChatColor.DARK_RED).append(" died from a projectile.").color(ChatColor.YELLOW).create()),
    STARVATION(new ComponentBuilder("").append("%PLAYER%").color(ChatColor.RED).append("[%PLAYER_KILLS%]").color(ChatColor.DARK_RED).append(" starved to death.").color(ChatColor.YELLOW).create()),
    SUFFOCATION(new ComponentBuilder("").append("%PLAYER%").color(ChatColor.RED).append("[%PLAYER_KILLS%]").color(ChatColor.DARK_RED).append(" suffocated to death.").color(ChatColor.YELLOW).create()),
    SUICIDE(new ComponentBuilder("").append("%PLAYER%").color(ChatColor.RED).append("[%PLAYER_KILLS%]").color(ChatColor.DARK_RED).append(" killed themselves.").color(ChatColor.YELLOW).create()),
    THORNS(new ComponentBuilder("").append("%PLAYER%").color(ChatColor.RED).append("[%PLAYER_KILLS%]").color(ChatColor.DARK_RED).append(" died from thorns.").color(ChatColor.YELLOW).create()),
    VOID(new ComponentBuilder("").append("%PLAYER%").color(ChatColor.RED).append("[%PLAYER_KILLS%]").color(ChatColor.DARK_RED).append(" felt into the void.").color(ChatColor.YELLOW).create()),
    WITHER(new ComponentBuilder("").append("%PLAYER%").color(ChatColor.RED).append("[%PLAYER_KILLS%]").color(ChatColor.DARK_RED).append(" withered to death.").color(ChatColor.YELLOW).create());

    @Getter private BaseComponent[] message;

    ProfileDeathMessageTemplate(BaseComponent[] message) {
        this.message = message;
    }

}
