package us.zonix.practice.events.tnttag;

import java.util.UUID;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.zonix.practice.events.EventPlayer;
import us.zonix.practice.events.PracticeEvent;
import us.zonix.practice.util.ItemBuilder;
import us.zonix.practice.util.PlayerUtil;

public class TNTTagPlayer extends EventPlayer {
    private boolean tagged;
    private boolean eliminated = false;

    public TNTTagPlayer(UUID uuid, PracticeEvent event) {
        super(uuid, event);
    }

    public void update() {
        Player player = this.getPlayer();
        if (player != null) {
            PlayerUtil.clearPlayer(player);
            if (this.tagged) {
                player.getInventory().setHelmet(new ItemBuilder(Material.TNT).name("&cYou're tagged!").lore("&7Hit a player to remove", "&7this TNT!").build());
                player.getInventory().setChestplate(new ItemBuilder(Material.LEATHER_CHESTPLATE).name("&c&lYOU'RE TAGGED").color(Color.RED).build());
                player.getInventory().setLeggings(new ItemBuilder(Material.LEATHER_LEGGINGS).name("&c&lYOU'RE TAGGED").color(Color.RED).build());
                player.getInventory().setBoots(new ItemBuilder(Material.LEATHER_BOOTS).name("&c&lYOU'RE TAGGED").color(Color.RED).build());
                this.getEvent().sendMessage("&c" + player.getName() + " &eis it!");
                this.sendMessage(new String[]{"&cYou have been TNT Tagged!", "&7Hit a player to transfer the TNT!"});
            }

            if (player.hasPotionEffect(PotionEffectType.SPEED)) {
                player.removePotionEffect(PotionEffectType.SPEED);
            }

            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, this.tagged ? 2 : 1));
        }
    }

    public boolean isTagged() {
        return this.tagged;
    }

    public boolean isEliminated() {
        return this.eliminated;
    }

    public void setTagged(boolean tagged) {
        this.tagged = tagged;
    }

    public void setEliminated(boolean eliminated) {
        this.eliminated = eliminated;
    }
}
