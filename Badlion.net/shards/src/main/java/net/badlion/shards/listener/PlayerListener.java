package net.badlion.shards.listener;

import net.badlion.shards.ShardPlugin;
import net.badlion.shards.grpc.ShardInstanceClient;
import net.badlion.shards.type.Border;
import net.badlion.shards.type.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPlayerIdEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPreJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class PlayerListener implements Listener {

    private final ShardPlugin plugin;


    public PlayerListener(ShardPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        // TODO : Listen for requests from the shards nearby and unload/load as needed
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        String shardTo = this.plugin.getShardAt(event.getTo());

        if (shardTo != null && !this.plugin.getConf().getServerName().equals(shardTo)) {
            if (this.plugin.getPlayerSyncManager().getPlayersBeingSent().contains(player.getUniqueId())) {
                return;
            }
            this.plugin.getLogger().info("Sending " + player.getName() + " to " + shardTo + ": " + System.currentTimeMillis());
            this.plugin.getPlayerSyncManager().getPlayersBeingSent().add(player.getUniqueId());

            ShardInstanceClient client = this.plugin.getShardClient(shardTo);

            // Try and sync the player if they don't get synced try again next move
            if (client.transferPlayer(player, event.getTo())) {
                this.plugin.getLogger().info("Sending " + player.getName() + " to " + shardTo + " SYNC DONE: " + System.currentTimeMillis());
                this.plugin.sendToServer(event.getPlayer(), shardTo);
            } else {
                this.plugin.getPlayerSyncManager().getPlayersBeingSent().remove(player.getUniqueId());
            }
            return;
        }
        Border shardBorder = this.plugin.getMasterConf().getShardBorderMap().get(this.plugin.getConf().getServerName());

        for (String shard : this.plugin.getNearbyShards(event.getTo(), 64)) {

            if (shard.equals(this.plugin.getConf().getServerName())) continue;

            ShardInstanceClient client = this.plugin.getShardClient(shard);
            if (!shardBorder.isInsideNonRenderZone(event.getTo())) {
                client.syncPlayer(player, event.getTo());
            } else if (!shardBorder.isInsideNonRenderZone(event.getFrom()) && shardBorder.isInsideNonRenderZone(event.getTo())) {
                client.despawnPlayer(player.getEntityId(), player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();

        String shardTo = this.plugin.getShardAt(event.getTo());

        if (shardTo != null && !this.plugin.getConf().getServerName().equals(shardTo)) {
            if (this.plugin.getPlayerSyncManager().getPlayersBeingSent().contains(player.getUniqueId())) {
                return;
            }
            this.plugin.getLogger().info("Sending " + player.getName() + " to " + shardTo + ": " + System.currentTimeMillis());
            this.plugin.getPlayerSyncManager().getPlayersBeingSent().add(player.getUniqueId());

            ShardInstanceClient client = this.plugin.getShardClient(shardTo);

            // Try and sync the player if they don't get synced try again next move
            if (client.transferPlayer(player, event.getTo())) {
                this.plugin.getLogger().info("Sending " + player.getName() + " to " + shardTo + " SYNC DONE: " + System.currentTimeMillis());
                this.plugin.sendToServer(event.getPlayer(), shardTo);
            } else {
                this.plugin.getPlayerSyncManager().getPlayersBeingSent().remove(player.getUniqueId());
            }
            return;
        }

        Border shardBorder = this.plugin.getMasterConf().getShardBorderMap().get(this.plugin.getConf().getServerName());

        for (String shard : this.plugin.getNearbyShards(event.getTo(), 64)) {

            if (shard.equals(this.plugin.getConf().getServerName())) continue;

            ShardInstanceClient client = this.plugin.getShardClient(shard);
            if (!shardBorder.isInsideNonRenderZone(event.getTo())) {
                client.syncPlayer(player, event.getTo());
            } else if (!shardBorder.isInsideNonRenderZone(event.getFrom()) && shardBorder.isInsideNonRenderZone(event.getTo())) {
                client.despawnPlayer(player.getEntityId(), player.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(ChatColor.GOLD + "You are now ON SERVER: " + ChatColor.GREEN + this.plugin.getConf().getServerName());
    }

    // Custom event to keep the players id the same, this avoids glitchy syncing with players clients
    @EventHandler
    public void onEntityPlayerId(EntityPlayerIdEvent event) {
        if (this.plugin.getPlayerSyncManager().getPlayerDataMap().containsKey(event.getPlayerUUID())) {
            PlayerData playerData = this.plugin.getPlayerSyncManager().getPlayerDataMap().get(event.getPlayerUUID());
            event.setId(playerData.getEntityId());
        }
    }

    @EventHandler
    public void onJoin(PlayerPreJoinEvent event) {
        Player player = event.getPlayer();

        this.plugin.getPlayerSyncManager().removeTrackingPlayer(player.getUniqueId());

        if (this.plugin.getPlayerSyncManager().getPlayerDataMap().containsKey(player.getUniqueId())) {
            // Don't send the player a location update, this prevents being pushed back as you join
            event.setSendLocationOnJoin(false);

            PlayerData playerData = this.plugin.getPlayerSyncManager().getPlayerDataMap().get(player.getUniqueId());
            player.setInternalLocation(playerData.getLocation());

            player.getInventory().clear();
            player.getInventory().setArmorContents(new ItemStack[4]);

            player.setGameMode(playerData.getGameMode());

            player.setHealth(playerData.getHealth());
            player.setFoodLevel(playerData.getFood());
            player.setSaturation(playerData.getSaturation());
            player.setExhaustion(playerData.getExhaustion());

            player.setSprinting(playerData.isSprinting());
            player.setFlying(playerData.isFlying());


            player.getInventory().setContents(playerData.getPlayerInventory());
            player.getInventory().setArmorContents(playerData.getPlayerArmor());
            player.getInventory().setHeldItemSlot(playerData.getHandslot());
            player.updateInventory();

            for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                player.removePotionEffect(potionEffect.getType());
            }

            for (PotionEffect potionEffect : playerData.getPotionEffects()) {
                player.addPotionEffect(potionEffect);
            }

            player.setExp(playerData.getExp());
            player.setTotalExperience(playerData.getTotalExp());
            player.setLevel(playerData.getLevel());

            player.setFireTicks(playerData.getFireticks());

            if (this.plugin.getEntitySyncManager().getEntitiesWaitingForPassengerPlayer().containsKey(player.getUniqueId())) {
                Vehicle vehicle = (Vehicle) this.plugin.getEntitySyncManager().getEntitiesWaitingForPassengerPlayer().remove(player.getUniqueId());
                vehicle.setPassenger(player);
            }
        } else {
            event.getPlayer().teleport(new Location(Bukkit.getWorld("world"), 0, 5, 0));
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!this.plugin.getPlayerSyncManager().getPlayersBeingSent().remove(player.getUniqueId())) {
            for (String shard : this.plugin.getNearbyShards(player.getLocation(), 64)) {

                if (shard.equals(this.plugin.getConf().getServerName())) continue;

                ShardInstanceClient client = this.plugin.getShardClient(shard);
                client.despawnPlayer(player.getEntityId(), player.getUniqueId());
            }
        }
        this.plugin.getPlayerSyncManager().getPlayerDataMap().remove(player.getUniqueId());
    }

}
