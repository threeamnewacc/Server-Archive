package me.devkevin.landcore.server;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public enum WhitelistMode {
    NONE {
        @Override
        public boolean isProfileIneligible(CoreProfile profile) {
            return false;
        }
    },
    RANKS {
        @Override
        public boolean isProfileIneligible(CoreProfile profile) {
            return !profile.hasDonor();
        }
    },
    STAFF {
        @Override
        public boolean isProfileIneligible(CoreProfile profile) {
            return !profile.hasStaff();
        }
    };

    private static final String WHITELIST_MESSAGE = CC.RED + "The server has been whitelisted. Come back later!";

    public void activate() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            CoreProfile profile = LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId());

            if (isProfileIneligible(profile)) {
                player.kickPlayer(WHITELIST_MESSAGE);
            }
        }
    }

    public abstract boolean isProfileIneligible(CoreProfile profile);
}
