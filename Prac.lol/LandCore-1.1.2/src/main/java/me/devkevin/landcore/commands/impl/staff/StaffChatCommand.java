package me.devkevin.landcore.commands.impl.staff;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.commands.PlayerCommand;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.player.rank.Rank;
import me.devkevin.landcore.utils.StringUtil;
import me.devkevin.landcore.utils.message.CC;
import org.bukkit.entity.Player;

public class StaffChatCommand extends PlayerCommand {
    private final LandCore plugin;

    public StaffChatCommand(LandCore plugin) {
        super("staffchat", Rank.TRIAL_MOD);
        this.plugin = plugin;
        setAliases("sc");
    }

    @Override
    public void execute(Player player, String[] args) {
        CoreProfile profile = plugin.getProfileManager().getProfile(player.getUniqueId());

        if (args.length == 0) {
            boolean inStaffChat = !profile.isInStaffChat();

            profile.setInStaffChat(inStaffChat);

            player.sendMessage(inStaffChat ? CC.GREEN + "You are now in staff chat." : CC.RED + "You are no longer in staff chat.");
        } else {
            String message = StringUtil.buildString(args, 0);

            plugin.getStaffManager().messageStaff(profile.getChatFormat(), message);
        }
    }
}
