package me.devkevin.landcore.punishment;

import lombok.Getter;
import lombok.Setter;
import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.message.CC;
import me.devkevin.landcore.utils.time.TimeUtil;
import org.apache.logging.log4j.core.Core;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 09/02/2023 @ 3:31
 * Punishment / me.devkevin.landcore.punishment / LandCore
 */
public class Punishment {

    public static PunishmentJsonSerializer SERIALIZER = new PunishmentJsonSerializer();
    public static PunishmentJsonDeserializer DESERIALIZER = new PunishmentJsonDeserializer();

    @Getter private final UUID uuid;
    @Getter private final PunishmentType type;
    @Getter @Setter private UUID addedBy;
    @Getter final private long addedAt;
    @Getter private final String addedReason;
    @Getter final private long duration;
    @Getter @Setter private UUID pardonedBy;
    @Getter @Setter private long pardonedAt;
    @Getter @Setter private String pardonedReason;
    @Getter @Setter private boolean pardoned;

    public Punishment(UUID uuid, PunishmentType type, long addedAt, String addedReason, long duration) {
        this.uuid = uuid;
        this.type = type;
        this.addedAt = addedAt;
        this.addedReason = addedReason;
        this.duration = duration;
    }

    public boolean isPermanent() {
        return type == PunishmentType.BLACKLIST || duration == Integer.MAX_VALUE;
    }

    public boolean isActive() {
        return !pardoned && (isPermanent() || getMillisRemaining() < 0);
    }

    public long getMillisRemaining() {
        return System.currentTimeMillis() - (addedAt + duration);
    }

    public String getTimeRemaining() {
        if (pardoned) {
            return "Pardoned";
        }

        if (isPermanent()) {
            return "Permanent";
        }

        if (!isActive()) {
            return "Expired";
        }

        return TimeUtil.millisToRoundedTime((addedAt + duration) - System.currentTimeMillis());
    }

    public String getContext() {
        if (!(type == PunishmentType.BAN || type == PunishmentType.MUTE)) {
            return pardoned ? type.getUndoContext() : type.getContext();
        }

        if (isPermanent()) {
            return (pardoned ? type.getUndoContext() : "permanently " + type.getContext());
        } else {
            return (pardoned ? type.getUndoContext() : "temporarily " + type.getContext());
        }
    }

    public void broadcast(String sender, Player target, boolean silent) {
        CoreProfile profile = LandCore.getInstance().getProfileManager().getProfile(target.getUniqueId());

        if (silent) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId()).hasRank(Rank.TRIAL_MOD)) {
                    player.sendMessage(CC.GRAY + "[Silent] " +
                            profile.getGrant().getRank().getColor() + target.getName() + CC.GREEN + " has been " + getContext() + " by " + sender);
                }
            });
        } else {
            Bukkit.broadcastMessage(profile.getGrant().getRank().getColor() + target.getName() + CC.GREEN + " has been " + getContext() + " by " + sender);
        }
    }

    public String getKickMessage() {
        String kickMessage;

        if (type == PunishmentType.BAN) {
            kickMessage = "&cYour account is {context} from PracLOL.{temporary}\n\n&cIf you feel this punishment is unjust, you may appeal at:\n&ehttps://www.prac.lol";
            String temporary = "";

            if (!isPermanent()) {
                temporary = "\n&cThis punishment expires in &e{time-remaining}&c.";
                temporary = temporary.replace("{time-remaining}", getTimeRemaining());
            }

            kickMessage = kickMessage.replace("{context}", getContext())
                    .replace("{temporary}", temporary);
        } else if (type == PunishmentType.KICK) {
            kickMessage = "&cYou were kicked by a staff member.\nReason: &e" + addedReason;
        } else {
            kickMessage = null;
        }

        return CC.translate(kickMessage);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Punishment && ((Punishment) object).uuid.equals(uuid);
    }

}
