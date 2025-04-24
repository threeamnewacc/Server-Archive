package me.devkevin.landcore.player.grant.procedure;

import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.rank.Rank;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 20/01/2023 @ 0:36
 * GrantProcedure / land.pvp.core.rank.procedure / LandCore
 */
@Getter
@Setter
public class GrantProcedure {

    @Getter
    @Setter
    private static Set<GrantProcedure> procedures = new HashSet<>();
    private GrantProcedureStage stage;
    private Rank rank;
    private long duration;
    private String reason;
    private CoreProfile addedTo;
    private String addedBy;

    public GrantProcedure(Rank rank, CoreProfile addedTo, String addedBy, GrantProcedureStage stage) {
        this.rank = rank;
        this.addedTo = addedTo;
        this.addedBy = addedBy;
        this.stage = stage;
        procedures.add(this);
    }

    public static GrantProcedure getByPlayer(String name) {
        for (GrantProcedure procedure : GrantProcedure.getProcedures()) {
            if (procedure.getAddedBy().equalsIgnoreCase(name)) {
                return procedure;
            }
        }
        return null;
    }
}
