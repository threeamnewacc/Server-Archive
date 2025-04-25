package us.zonix.practice.bots;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.util.PlayerAnimation;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.zonix.practice.arena.Arena;
import us.zonix.practice.arena.StandaloneArena;
import us.zonix.practice.kit.Kit;
import us.zonix.practice.util.ItemUtil;

public class ZonixBot {
    private NPC npc;
    private Kit kit;
    private Arena arena;
    private StandaloneArena standaloneArena;
    private boolean destroyed;
    private Random random = new Random();
    private ZonixBot.BotDifficulty botDifficulty;
    public BotMechanics botMechanics;

    public boolean isSpawned() {
        return this.npc.isSpawned();
    }

    public Player getBukkitEntity() {
        return (Player)this.npc.getEntity();
    }

    public void swing() {
        if (this.getBukkitEntity() != null) {
            PlayerAnimation.ARM_SWING.play(this.getBukkitEntity());
        }
    }

    public void destroy() {
        if (this.getBukkitEntity() != null) {
            this.getBukkitEntity().setHealth(20.0);
        }

        this.npc.despawn();
        this.npc.destroy();
        this.destroyed = true;
    }

    public void hurt(boolean burn, boolean critical, boolean sharp) {
        this.getBukkitEntity().playEffect(EntityEffect.HURT);

        for (Entity ent : this.getBukkitEntity().getNearbyEntities(100.0, 100.0, 100.0)) {
            if (ent instanceof Player) {
                this.getBukkitEntity().getWorld().playSound(this.getBukkitEntity().getLocation(), Sound.HURT_FLESH, 0.7F, 1.0F);
            }
        }

        if (burn) {
            this.getBukkitEntity().setFireTicks(20);
        } else {
            Location l = this.getBukkitEntity().getLocation().add(0.0, 1.0, 0.0);
            if (critical) {
                for (int i = 0; i < this.random.nextInt(5) + 10; i++) {
                    l.getWorld().playEffect(l, Effect.CRIT, 1);
                }
            }

            if (sharp) {
                for (int i = 0; i < this.random.nextInt(5) + 10; i++) {
                    l.getWorld().playEffect(l, Effect.MAGIC_CRIT, 1);
                }
            }
        }
    }

    public void startMechanics(List<UUID> players, ZonixBot.BotDifficulty difficulty) {
        this.botMechanics = new BotMechanics(this, players, difficulty);
    }

    public NPC getNpc() {
        return this.npc;
    }

    public Kit getKit() {
        return this.kit;
    }

    public Arena getArena() {
        return this.arena;
    }

    public StandaloneArena getStandaloneArena() {
        return this.standaloneArena;
    }

    public boolean isDestroyed() {
        return this.destroyed;
    }

    public Random getRandom() {
        return this.random;
    }

    public ZonixBot.BotDifficulty getBotDifficulty() {
        return this.botDifficulty;
    }

    public BotMechanics getBotMechanics() {
        return this.botMechanics;
    }

    public void setNpc(NPC npc) {
        this.npc = npc;
    }

    public void setKit(Kit kit) {
        this.kit = kit;
    }

    public void setArena(Arena arena) {
        this.arena = arena;
    }

    public void setStandaloneArena(StandaloneArena standaloneArena) {
        this.standaloneArena = standaloneArena;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public void setBotDifficulty(ZonixBot.BotDifficulty botDifficulty) {
        this.botDifficulty = botDifficulty;
    }

    public void setBotMechanics(BotMechanics botMechanics) {
        this.botMechanics = botMechanics;
    }

    public static enum BotDifficulty {
        EASY(2.5, ItemUtil.createItem(Material.STAINED_GLASS_PANE, ChatColor.WHITE.toString() + ChatColor.BOLD + "Easy")),
        MEDIUM(2.8, ItemUtil.createItem(Material.STAINED_GLASS_PANE, ChatColor.YELLOW.toString() + ChatColor.BOLD + "Medium", 1, (short)4)),
        HARD(3.0, ItemUtil.createItem(Material.STAINED_GLASS_PANE, ChatColor.GOLD.toString() + ChatColor.BOLD + "Hard", 1, (short)1)),
        EXPERT(3.2, ItemUtil.createItem(Material.STAINED_GLASS_PANE, ChatColor.RED.toString() + ChatColor.BOLD + "Expert", 1, (short)14));

        private double reach;
        private ItemStack item;

        private BotDifficulty(double reach, ItemStack item) {
            this.item = item;
            this.reach = reach;
        }

        public ItemStack getItem() {
            return this.item;
        }

        public double getReach() {
            return this.reach;
        }
    }
}
