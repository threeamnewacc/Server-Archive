package me.devkevin.landcore.listeners;

import me.devkevin.landcore.LandCore;
import me.devkevin.landcore.utils.message.CC;
import lombok.RequiredArgsConstructor;
import org.bukkit.SkullType;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 18/01/2023 @ 13:59
 * PlayerInteractListener / land.pvp.core.listeners / LandCore
 */
@RequiredArgsConstructor
public class PlayerInteractListener implements Listener {
    private final LandCore plugin;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block.getState() instanceof Skull) {
                Skull skull = (Skull) block.getState();
                if (skull.getSkullType() != SkullType.PLAYER)
                    return;

                if (skull.getOwner() == null) {
                    player.sendMessage(CC.GOLD + "This is the head of: " + CC.WHITE + "Steve");
                    return;
                }

                player.sendMessage(CC.GOLD + "This is the head of: " + CC.WHITE + skull.getOwner());
            }
        }
    }
}
