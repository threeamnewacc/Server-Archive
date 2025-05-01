package net.badlion.potpvp.ffaworlds;

import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.bukkitevents.FollowedPlayerTeleportEvent;
import net.badlion.potpvp.helpers.PlayerHelper;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class KitFFAWorld extends FFAWorld {

    // Use this to load kits instead
    protected SmellyInventory kitInventory;

    public KitFFAWorld(ItemStack ffaItem, KitRuleSet kitRuleSet) {
        super(ffaItem, kitRuleSet);
    }

    @Override
    public Location handleRespawn(Player player) {
        PotPvP.getInstance().getServer().getPluginManager().callEvent(new FollowedPlayerTeleportEvent(player));
        PlayerHelper.healAndPrepPlayerForBattle(player);

        // Default kit has inventory item stuff for them to pick a kit
        player.getInventory().setContents(this.kitRuleSet.defaultInventoryKit);
        player.getInventory().setArmorContents(this.kitRuleSet.defaultArmorKit);

        return this.spawn;
    }


}
