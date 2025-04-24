package me.devkevin.landcore.punishment.procedure;

import lombok.Getter;
import lombok.Setter;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.punishment.Punishment;
import org.bukkit.entity.Player;
import java.util.HashSet;
import java.util.Set;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 09/02/2023 @ 3:51
 * PunishmentProcedure / me.devkevin.landcore.punishment.procedure / LandCore
 */
@Getter
public class PunishmentProcedure {
    @Getter private static final Set<PunishmentProcedure> procedures = new HashSet<>();

    private final Player issuer;
    private final CoreProfile recipient;
    private final PunishmentProcedureType type;
    private PunishmentProcedureStage stage;
    @Setter private Punishment punishment;

    public PunishmentProcedure(Player issuer, CoreProfile recipient, PunishmentProcedureType type, PunishmentProcedureStage stage) {
        this.issuer = issuer;
        this.recipient = recipient;
        this.type = type;
        this.stage = stage;

        procedures.add(this);
    }

    public void finish() {
        this.recipient.save(true);
        procedures.remove(this);
    }

    public static PunishmentProcedure getByPlayer(Player player) {
        for (PunishmentProcedure procedure : procedures) {
            if (procedure.issuer.equals(player)) {
                return procedure;
            }
        }

        return null;
    }
}
