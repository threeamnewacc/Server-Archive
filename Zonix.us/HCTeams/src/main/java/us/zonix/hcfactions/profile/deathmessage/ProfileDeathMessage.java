package us.zonix.hcfactions.profile.deathmessage;

import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.zonix.hcfactions.util.player.PlayerUtility;
import us.zonix.hcfactions.factions.type.PlayerFaction;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.util.player.PlayerUtility;

public class ProfileDeathMessage {

    private final ProfileDeathMessageTemplate template;
    private final Profile profile, killer;
    private final String mob, weapon;
    private final boolean combatLogger;

    public ProfileDeathMessage(ProfileDeathMessageTemplate template, Profile profile, Profile killer, String weapon) {
        this(template, profile, killer, null, weapon, false);
    }

    public ProfileDeathMessage(ProfileDeathMessageTemplate template, Profile profile, Profile killer, String weapon, boolean combatLogger) {
        this(template, profile, killer, null, weapon, combatLogger);
    }

    public ProfileDeathMessage(ProfileDeathMessageTemplate template, Profile profile, String mob) {
        this(template, profile, null, mob);
    }

    public ProfileDeathMessage(ProfileDeathMessageTemplate template, Profile profile) {
        this(template, profile, null, null);
    }

    public ProfileDeathMessage(ProfileDeathMessageTemplate template, Profile profile, boolean combatLogger) {
        this(template, profile, null, null, combatLogger);
    }

    public ProfileDeathMessage(ProfileDeathMessageTemplate template, Profile profile, Profile killer, String mob, String weapon, boolean combatLogger) {
        this.template = template;
        this.profile = profile;
        this.killer = killer;
        this.mob = mob;
        this.weapon = weapon;
        this.combatLogger = combatLogger;

        TextComponent component = new TextComponent(template.getMessage());
        String message = ComponentSerializer.toString(component);

        if (combatLogger) {
            message = message.replace("%PLAYER%", "(Combat Logger) " + profile.getName());
        }
        else {
            message = message.replace("%PLAYER%", profile.getName());
        }

        message = message.replace("%PLAYER_KILLS%", profile.getKillCount() + "");

        if (killer != null) {
            message = message.replace("%KILLER%", killer.getName());
            message = message.replace("%KILLER_KILLS%", killer.getKillCount() + "");
        }

        if (mob != null) {
            message = message.replace("%MOB%", mob);
        }

        if (weapon != null) {
            message = message.replace("%WEAPON%", weapon);
        } else {
            message = message.replace("%WEAPON%", "their fist");
        }

        BaseComponent[] baseComponents = ComponentSerializer.parse(message);

        for (Player online : PlayerUtility.getOnlinePlayers() ) {
            Profile onlineProfile = Profile.getByPlayer(online);

            if (profile.getUuid().equals(online.getUniqueId())) {
                online.spigot().sendMessage(baseComponents);
                continue;
            }

            if (killer != null && killer.getUuid().equals(online.getUniqueId())) {
                online.spigot().sendMessage(baseComponents);
                continue;
            }

            if (!onlineProfile.getOptions().isViewDeathMessages()) {
                PlayerFaction faction = PlayerFaction.getByPlayer(online);

                if (faction != null) {
                    if (killer != null && faction.getAllPlayerUuids().contains(killer.getUuid())) {
                        online.spigot().sendMessage(baseComponents);
                        continue;
                    }

                    if (faction.getAllPlayerUuids().contains(profile.getUuid())) {
                        online.spigot().sendMessage(baseComponents);
                        continue;
                    }
                }
            }
            else {
                online.spigot().sendMessage(baseComponents);
            }
        }
    }

}
