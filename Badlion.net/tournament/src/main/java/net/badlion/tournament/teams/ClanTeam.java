package net.badlion.tournament.teams;

import net.badlion.clans.managers.ClanManager.Clan;
import net.badlion.clans.managers.ClanManager.ClanMember;

import java.util.*;

public class ClanTeam extends DefaultTeam {

    private Clan clan;

    public ClanTeam(Clan clan) {
        super(clan.getName(), clan.getLeader());
        this.setClan(clan);
        Set<UUID> uuids = new HashSet<>();
        for (ClanMember member : clan.getClanMembers()) {
            uuids.add(member.getUuid());
        }
        this.setUUIDs(uuids);
        this.addMember(this.getLeader());
    }

    @Override
    public String getType() {
        return "clan";
    }

    public Clan getClan() {
        return clan;
    }

    public void setClan(Clan clan) {
        this.clan = clan;
    }

    @Override
    public ClanTeam clone() {
        return new ClanTeam(this.getClan());
    }
}
