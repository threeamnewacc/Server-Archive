package me.devkevin.landcore.nametag;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.nametag.impl.InternalNametag;
import me.devkevin.landcore.nametag.impl.NametagProvider;
import me.devkevin.landcore.player.CoreProfile;
import org.bukkit.entity.Player;
import xyz.haoshoku.nick.api.NickAPI;

import static net.minecraft.server.v1_8_R3.ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 17/01/2023 @ 11:37
 * NameTagAdapter / land.pvp.core.nametag / LandCore
 */
public class NameTagAdapter extends NametagProvider {
    public NameTagAdapter() {
        super("NameTagAdapter", 5);
    }

    @Override
    public InternalNametag.NametagInfo fetchNametag(Player toRefresh, Player refreshFor) {
        CoreProfile coreProfile = LandCore.getInstance().getProfileManager().getProfile(toRefresh.getUniqueId());

        if (NickAPI.isNicked(toRefresh)) {
            return createNametag(coreProfile.getDisguiseRank().getColor(), "", ALWAYS);
        }

        return createNametag(coreProfile.getGrant().getRank().getColor(), "", ALWAYS);
    }
}
