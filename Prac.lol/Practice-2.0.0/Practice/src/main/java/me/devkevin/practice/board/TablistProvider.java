package me.devkevin.practice.board;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 09/03/2023 @ 15:55
 * TablistProvider / me.devkevin.practice.board / Practice
 */
/*public class TablistProvider implements TabAdapter {
    @Override
    public String getHeader(Player player) {
        return CC.GOLD + "Prac LOL";
    }

    @Override
    public String getFooter(Player player) {
        return CC.GRAY + "prac.lol";
    }

    @Override
    public List<TabEntry> getLines(Player player) {
        List<TabEntry> lines = Lists.newArrayList();

        int column = 1;
        int row = 0;

        for (Rank rank : ImmutableList.copyOf(Rank.values()).reverse()) {
            CoreProfile coreProfile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

            if (coreProfile != null) {
                if (coreProfile.getRank() == rank) {

                    for (Player online : Bukkit.getOnlinePlayers()) {
                        try {
                            lines.add(new TabEntry(column, row,  LandCore.getInstance().getProfileManager().getProfile(online.getUniqueId()).getGrant().getRank().getColor() + online.getName()).setPing(((CraftPlayer) online).getHandle().ping));
                            if (column++ < 3) {
                                continue;
                            }
                            column = 0;

                            if (row++ < 19) {
                                continue;
                            }
                            row = 0;

                        } catch (Exception ignored) {
                            break;
                        }
                    }

                    lines.add(new TabEntry(19, 0, CC.GRAY + "prac.lol"));
                    lines.add(new TabEntry(19, 0, CC.GRAY + "prac.lol"));
                    lines.add(new TabEntry(19, 0, CC.GRAY + "prac.lol"));
                }
            }
        }

        return lines;
    }
}*/
