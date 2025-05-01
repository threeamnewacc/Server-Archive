package net.badlion.clans.managers;

import net.badlion.gberry.Gberry;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.sql.*;
import java.util.*;

public class ClanManager {

    public enum CLAN_RANK {
        LEADER(1), OFFICER(2), MEMBER(3), RECRUIT(4);

        private static final Map<Integer, CLAN_RANK> ranks = new HashMap<>();
        private int pos;

        CLAN_RANK(int pos) {
            this.pos = pos;
        }

        public int getPos() {
            return pos;
        }

        public static CLAN_RANK get(int pos) {
            return CLAN_RANK.ranks.get(pos);
        }

        static {
            for (CLAN_RANK clanRank : CLAN_RANK.values()) {
                CLAN_RANK.ranks.put(clanRank.getPos(), clanRank);
            }
        }

    }

    /**
     * ASYNC
     */
    public static boolean hasDonatorPlusPerm(UUID uuid) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM potion_gperms_users WHERE uuid = ?;";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid.toString());
            rs = ps.executeQuery();

            if (rs.next()) {
                String group = rs.getString("group");

                rs.close();
                ps.close();

                query = "SELECT * FROM potion_gperms_group_permissions WHERE group_name = ? AND permission_name = 'badlion.donatorplus';";

                ps = connection.prepareStatement(query);
                ps.setString(1, group);
                rs = ps.executeQuery();

                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return false;
    }

    /**
     * ASYNC
     */
    public static Clan getClanByTag(String tag) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM clans WHERE lower_tag = ?;";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, tag.toLowerCase());
            rs = ps.executeQuery();

            if (rs.next()) {
                return new Clan(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return null;
    }

    /**
     * ASYNC
     */
    public static Clan getClanByName(String name) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM clans WHERE lower_name = ?;";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, name.toLowerCase());
            rs = ps.executeQuery();

            if (rs.next()) {
                return new Clan(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return null;
    }

    /**
     * ASYNC
     */
    public static Clan getClanByClanId(int id) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM clans WHERE clan_id = ?;";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                return new Clan(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return null;
    }

    /**
     * ASYNC
     */
    public static List<ClanMember> getClanMembers(Clan clan) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<ClanMember> members = new ArrayList<>();

        String query = "SELECT * FROM clan_members WHERE clan_id = ? ORDER BY rank ASC;";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setInt(1, clan.getClanId());
            rs = ps.executeQuery();

            while (rs.next()) {
                members.add(new ClanMember(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return members;
    }

    /**
     * ASYNC
     */
    public static ClanMember getClanMember(UUID uuid) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM clan_members WHERE member = ?;";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid.toString());
            rs = ps.executeQuery();

            if (rs.next()) {
                return new ClanMember(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return null;
    }

    /**
     * ASYNC
     */
    public static List<ClanInvite> getClanInvitesByUUID(UUID uuid) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<ClanInvite> invites = new ArrayList<>();

        String query = "SELECT * FROM clan_invites WHERE invitee_uuid = ?;";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid.toString());
            rs = ps.executeQuery();

            while (rs.next()) {
                invites.add(new ClanInvite(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return invites;
    }

    private static List<ClanInvite> getClanInvitesByClanId(int clanId) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<ClanInvite> invites = new ArrayList<>();

        String query = "SELECT * FROM clan_invites WHERE clan_id = ?;";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setInt(1, clanId);
            rs = ps.executeQuery();

            while (rs.next()) {
                invites.add(new ClanInvite(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return invites;
    }

    /**
     * ASYNC
     */
    public static ClanInvite getClanInvite(UUID invited, UUID inviter) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM clan_invites WHERE invitee_uuid = ? AND inviter_uuid = ?;";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, invited.toString());
            ps.setString(2, inviter.toString());
            rs = ps.executeQuery();

            if (rs.next()) {
                return new ClanInvite(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return null;
    }

    public static void insertClanHistory(Clan clan, UUID taker, UUID receiver, String action) {
        Connection connection = null;
        PreparedStatement ps = null;

        String query = "INSERT INTO clan_history (clan_id, member_taking_action, member_receiving_action, clan_action, action_date) VALUES (?, ?, ?, ?, ?);";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);

            ps.setInt(1, clan.getClanId());
            ps.setString(2, taker.toString());
            ps.setString(3, receiver.toString());
            ps.setString(4, action);
            ps.setTimestamp(5, new Timestamp(new DateTime(DateTimeZone.UTC).getMillis()));

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(ps, connection);
        }
    }

    public static void insertClanInvite(Clan clan, UUID inviterUUID, UUID invitedUUID) {
        Connection connection = null;
        PreparedStatement ps = null;

        String query = "INSERT INTO clan_invites (clan_id, invitee_uuid, inviter_uuid, invite_date) VALUES (?, ?, ?, ?);";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);

            ps.setInt(1, clan.getClanId());
            ps.setString(2, invitedUUID.toString());
            ps.setString(3, inviterUUID.toString());
            ps.setTimestamp(4, new Timestamp(new DateTime(DateTimeZone.UTC).getMillis()));

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(ps, connection);
        }
    }

    private static boolean updateInsertClan(Clan clan) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;


        String query = "";
        if (clan.getClanId() != -1) {
            query = "UPDATE clans SET tag = ?, lower_tag = ?, name = ?, lower_name = ?, description = ?, leader = ? WHERE clan_id = ?;\n";
        } else {
            query = "INSERT INTO clans (tag, name, leader, description, creation_date, lower_name, lower_tag) VALUES (?, ?, ?, '', ?, ?, ?);";
        }

        try {
            connection = Gberry.getConnection();
            if (clan.getClanId() != -1) {
                ps = connection.prepareStatement(query);
                ps.setString(1, clan.getTag());
                ps.setString(2, clan.getTag().toLowerCase());
                ps.setString(3, clan.getName());
                ps.setString(4, clan.getName().toLowerCase());
                ps.setString(5, clan.getDescription());
                ps.setString(6, clan.getLeader().toString());
                ps.setInt(7, clan.getClanId());
            } else {
                ps = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setString(1, clan.getTag());
                ps.setString(2, clan.getName());
                ps.setString(3, clan.getLeader().toString());
                ps.setTimestamp(4, new Timestamp(new DateTime(DateTimeZone.UTC).getMillis()));
                ps.setString(5, clan.getName().toLowerCase());
                ps.setString(6, clan.getTag().toLowerCase());
            }

            ps.executeUpdate();

            if (clan.getClanId() == -1) {
                rs = ps.getGeneratedKeys();

                if (rs.next()) {
                    clan.setClanId(rs.getInt(1));
                }
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return false;
    }

    private static boolean updateInsertClanMember(ClanMember clanMember) {
        Connection connection = null;
        PreparedStatement ps = null;

        String query = "UPDATE clan_members SET rank = ? WHERE member = ?;\n";
        query += "INSERT INTO clan_members (clan_id, member, rank, join_date) SELECT ?, ?, ?, ? WHERE NOT EXISTS " +
                         "(SELECT 1 FROM clan_members WHERE member = ?);";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);

            ps.setInt(1, clanMember.getRank().getPos());
            ps.setString(2, clanMember.getUuid().toString());
            ps.setInt(3, clanMember.getClanId());
            ps.setString(4, clanMember.getUuid().toString());
            ps.setInt(5, clanMember.getRank().getPos());
            ps.setTimestamp(6, new Timestamp(new DateTime(DateTimeZone.UTC).getMillis()));
            ps.setString(7, clanMember.getUuid().toString());

            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(ps, connection);
        }

        return false;
    }

    public static boolean checkBannedTag(String tag) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM clan_banned_tags WHERE lower(tag) = ?;";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, tag.toLowerCase());

            rs = ps.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return false;
    }

    public static void updateInsertClanBoard(Clan clan) {
        Connection connection = null;
        PreparedStatement ps = null;

        String query = "UPDATE forum_boards SET title = ? WHERE board_id = ?;\n";
        query += "INSERT INTO forum_boards (board_id, title, access) SELECT ?, ?, ? WHERE NOT EXISTS " +
                         "(SELECT 1 FROM forum_boards WHERE board_id = ?);";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);

            ps.setString(1, clan.getName() + "'s Forum");
            ps.setInt(2, clan.getClanId());
            ps.setInt(3, clan.getClanId());
            ps.setString(4, clan.getName() + "'s Forum");
            ps.setString(5, clan.getTag());
            ps.setInt(6, clan.getClanId());

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(ps, connection);
        }
    }

    public static void updateInsertClanCategory(Clan clan, boolean newClan) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "";
        if (!newClan) {
            query = "UPDATE forum_categories SET category_title = ? WHERE board_id = ? AND parent_id = 0;";
        } else {
            query = "INSERT INTO forum_categories (board_id, category_title, parent_id, posts, threads, ordering, description, " +
                            "last_post_username, last_post_uid, last_post_time, last_post_tid, last_post_thread_name, " +
                            "raccess, waccess, caccess) VALUES (?, ?, ?, 0, 0, ?, ?, 'None', 0, NULL, 0, 'None', 'view', 'user', 'user');";
        }

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

            // Top category first
            if (!newClan) {
                ps.setString(1, clan.getName());
                ps.setInt(2, clan.getClanId());
            } else {
                ps.setInt(1, clan.getClanId());
                ps.setString(2, clan.getName());
                ps.setInt(3, 0);
                ps.setInt(4, 1);
                ps.setString(5, ""); // Empty description
            }

            ps.executeUpdate();
            int categoryId = -1;

            if (newClan) {
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    categoryId = rs.getInt(1);
                }
            }

            // General discussion now
            if (!newClan) {
                query = "UPDATE forum_categories SET description = ? WHERE board_id = ? AND parent_id != 0 AND category_title = 'General Discussion';";
            } else {
                query = "INSERT INTO forum_categories (board_id, category_title, parent_id, posts, threads, ordering, description, " +
                                "last_post_username, last_post_uid, last_post_time, last_post_tid, last_post_thread_name, " +
                                "raccess, waccess, caccess) VALUES (?, ?, ?, 0, 0, ?, ?, 'None', 0, NULL, 0, 'None', 'view', 'user', 'user');";
            }

            ps.close();
            ps = connection.prepareStatement(query);

            if (!newClan) {
                ps.setString(1, "Private forum to discuss " + clan.getName() + " related stuff.");
                ps.setInt(2, clan.getClanId());
            } else {
                ps.setInt(1, clan.getClanId());
                ps.setString(2, "General Discussion");
                ps.setInt(3, categoryId);
                ps.setInt(4, 2);
                ps.setString(5, "Private forum to discuss " + clan.getName() + " related stuff.");
            }

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }
    }

    public static class Clan {

        private int clanId = -1;
        private String tag;
        private String name;
        private String description;
        private UUID leader;

        public Clan(String tag, String name, UUID leader) {
            this.tag = tag;
            this.name = name;
            this.description = "";
            this.leader = leader;

            this.update();
        }

        public Clan(ResultSet rs) throws SQLException {
            this.clanId = rs.getInt("clan_id");
            this.name = rs.getString("name");
            this.tag = rs.getString("tag");
            this.description = rs.getString("description");
            this.leader = UUID.fromString(rs.getString("leader"));
        }

        public int getClanId() {
            return clanId;
        }

        public void setClanId(int clanId) {
            this.clanId = clanId;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public UUID getLeader() {
            return leader;
        }

        public void setLeader(UUID leader) {
            this.leader = leader;
        }

        public List<ClanMember> getClanMembers() {
            return ClanManager.getClanMembers(this);
        }

        public List<ClanInvite> getClanInvites() {
            return ClanManager.getClanInvitesByClanId(this.clanId);
        }

        public boolean update() {
            return ClanManager.updateInsertClan(this);
        }

        public void delete() {
            for (ClanMember clanMember : ClanManager.getClanMembers(this)) {
                clanMember.delete();
            }

            for (ClanInvite clanInvite : ClanManager.getClanInvitesByClanId(this.clanId)) {
                clanInvite.delete();
            }

            Connection connection = null;
            PreparedStatement ps = null;

            String query = "DELETE FROM clans WHERE clan_id = ?;";

            try {
                connection = Gberry.getConnection();
                ps = connection.prepareStatement(query);

                ps.setInt(1, this.clanId);
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                Gberry.closeComponents(ps, connection);
            }
        }

    }

    public static class ClanMember {

        private int clanId;
        private UUID uuid;
        private CLAN_RANK rank;

        public ClanMember(ResultSet rs) throws SQLException {
            this.clanId = rs.getInt("clan_id");
            this.uuid = UUID.fromString(rs.getString("member"));
            this.rank = CLAN_RANK.get(rs.getInt("rank"));
        }

        public ClanMember(int clanId, UUID uuid, CLAN_RANK rank) {
            this.clanId = clanId;
            this.uuid = uuid;
            this.rank = rank;

            // Sync to DB
            this.update();
        }

        public int getClanId() {
            return clanId;
        }

        public UUID getUuid() {
            return uuid;
        }

        public CLAN_RANK getRank() {
            return rank;
        }

        public void setRank(CLAN_RANK rank) {
            this.rank = rank;
        }

        public Clan getClan() {
            return ClanManager.getClanByClanId(this.clanId);
        }

        public boolean update() {
            return ClanManager.updateInsertClanMember(this);
        }

        public void delete() {
            Connection connection = null;
            PreparedStatement ps = null;

            String query = "DELETE FROM clan_members WHERE member = ?;";

            try {
                connection = Gberry.getConnection();
                ps = connection.prepareStatement(query);

                ps.setString(1, this.uuid.toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                Gberry.closeComponents(ps, connection);
            }
        }

    }

    public static class ClanInvite {

        private int clanId;
        private UUID invited;
        private UUID inviter;

        public ClanInvite(ResultSet rs) throws SQLException {
            this.clanId = rs.getInt("clan_id");
            this.invited = UUID.fromString(rs.getString("invitee_uuid"));
            this.inviter = UUID.fromString(rs.getString("inviter_uuid"));
        }

        public int getClanId() {
            return clanId;
        }

        public UUID getInvited() {
            return invited;
        }

        public UUID getInviter() {
            return inviter;
        }

        public Clan getClan() {
            return ClanManager.getClanByClanId(this.clanId);
        }

        public void delete() {
            Connection connection = null;
            PreparedStatement ps = null;

            String query = "DELETE FROM clan_invites WHERE clan_id = ? AND invitee_uuid = ? AND inviter_uuid = ?;";

            try {
                connection = Gberry.getConnection();
                ps = connection.prepareStatement(query);

                ps.setInt(1, this.clanId);
                ps.setString(2, this.invited.toString());
                ps.setString(3, this.inviter.toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                Gberry.closeComponents(ps, connection);
            }
        }

    }

}