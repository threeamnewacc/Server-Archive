package me.devkevin.practice.staff;

import lombok.Getter;
import club.inverted.chatcolor.CC;
import me.devkevin.practice.Practice;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.profile.state.ProfileState;
import org.bukkit.entity.Player;

/**
 * Copyright 05/05/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class StaffMode {

    @Getter private final Practice plugin = Practice.getInstance();

    private void giveStaffModeItems(Player player) {
        player.closeInventory();
        player.getInventory().setContents(this.plugin.getHotbarItem().getStaffModeItems());
        player.updateInventory();
    }

    public void staffMode(Player player) {
        Profile profile = this.plugin.getProfileManager().getProfileData(player.getUniqueId());

        profile.setState(ProfileState.STAFF);

        giveStaffModeItems(player);

        player.sendMessage(CC.GREEN + "You have joined to Staff Mode.");
    }
}
