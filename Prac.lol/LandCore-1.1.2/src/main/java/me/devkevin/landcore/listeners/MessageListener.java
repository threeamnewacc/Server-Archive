package me.devkevin.landcore.listeners;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.event.player.PlayerMessageEvent;
import me.devkevin.landcore.player.CoreProfile;
import me.devkevin.landcore.utils.message.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class MessageListener implements Listener {
    private final LandCore plugin;

    private static void sendMessage(CoreProfile sender, CoreProfile receiver, Player player, String msg) {
        receiver.setConverser(sender.getId());
        player.sendMessage(msg);
    }

    @EventHandler
    public void onMessage(PlayerMessageEvent event) {
        Player sender = event.getPlayer();
        CoreProfile senderProfile = plugin.getProfileManager().getProfile(sender.getUniqueId());

        if (!senderProfile.isMessaging() && !senderProfile.hasStaff()) {
            sender.sendMessage(CC.RED + "You have messaging disabled.");
            return;
        }

        Player receiver = event.getReceiver();
        CoreProfile receiverProfile = plugin.getProfileManager().getProfile(receiver.getUniqueId());

        if (senderProfile.hasStaff()) {
            // NO-OP
        } else if (!receiverProfile.isMessaging()) {
            sender.sendMessage(CC.RED + receiver.getName() + " has messaging disabled.");
            return;
        }

        String toMsg = CC.GRAY + "(To " + receiverProfile.getChatFormat() + CC.GRAY + ") " + event.getMessage();
        String fromMsg = CC.GRAY + "(From " + senderProfile.getChatFormat() + CC.GRAY + ") " + event.getMessage();
//
//        if (plugin.getFilter().isFiltered(event.getMessage())) {
//            if (senderProfile.hasStaff()) {
//                sender.sendMessage(CC.RED + "That would have been filtered.");
//            } else {
//                String filteredMessage = CC.GRAY + "(" + sender.getDisplayName()
//                        + CC.GRAY + " -> " + receiver.getDisplayName() + CC.GRAY + ") " + event.getMessage();
//
//                plugin.getStaffManager().messageStaff(CC.RED + "(Filtered) " + filteredMessage);
//                sender.sendMessage(toMsg);
//                return;
//            }
//        }
        // TODO: message-specific filter

        sendMessage(senderProfile, receiverProfile, receiver, fromMsg);
        sendMessage(receiverProfile, senderProfile, sender, toMsg);

        if (receiverProfile.isPlayingSounds()) {
            receiver.playSound(receiver.getLocation(), Sound.NOTE_PLING, 1.0F, 2.0F);
        }
    }
}
