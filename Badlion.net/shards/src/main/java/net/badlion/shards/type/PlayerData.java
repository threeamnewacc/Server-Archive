package net.badlion.shards.type;

import com.google.gson.reflect.TypeToken;
import net.badlion.shards.ShardPlugin;
import net.badlion.shards.grpc.PlayerSyncRequest;
import net.badlion.shards.grpc.PlayerTransferRequest;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.com.google.gson.TypeAdapter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.lang.reflect.Type;
import java.util.List;
import java.util.UUID;

public class PlayerData {

    private UUID uuid;
    private int entityId;

    private Location location;

    private boolean isSprinting;
    private boolean isFlying;

    private ItemStack[] playerInventory;
    private ItemStack[] playerArmor;

    private int handslot;

    private GameMode gameMode;
    private double health;
    private int food;
    private float saturation;
    private float exhaustion;

    private List<PotionEffect> potionEffects;

    private float exp;
    private int totalExp;
    private int level;

    private int fireticks;

    public PlayerData(UUID uuid, Location location, boolean isSprinting){
        this.uuid = uuid;
        this.location = location;
        this.isSprinting = isSprinting;
    }

    public PlayerData(PlayerTransferRequest playerTransferRequest){
        this.uuid = UUID.fromString(playerTransferRequest.getUuid());
        this.entityId = playerTransferRequest.getEntityid();
        this.location = new Location(Bukkit.getWorld(playerTransferRequest.getWorld()), playerTransferRequest.getLocx(), playerTransferRequest.getLocy(), playerTransferRequest.getLocz(), playerTransferRequest.getYaw(), playerTransferRequest.getPitch());
        this.isSprinting = playerTransferRequest.getSprinting();
        this.isFlying = playerTransferRequest.getFlying();
        Type items = new TypeToken<ItemStack[]>() {
        }.getType();
        this.playerInventory = ShardPlugin.getPlugin().getGsonSmall().fromJson(playerTransferRequest.getInventory(), items);
        this.playerArmor = ShardPlugin.getPlugin().getGsonSmall().fromJson(playerTransferRequest.getArmor(), items);
        this.handslot = playerTransferRequest.getHandslot();

        this.gameMode = GameMode.getByValue(playerTransferRequest.getGamemode());
        this.health = playerTransferRequest.getHealth();
        this.food = playerTransferRequest.getFood();
        this.saturation = playerTransferRequest.getSaturation();
        this.exhaustion = playerTransferRequest.getExhaustion();
        Type potions = new TypeToken<List<PotionEffect>>() {
        }.getType();
        this.potionEffects = ShardPlugin.getPlugin().getGsonSmall().fromJson(playerTransferRequest.getPotions(), potions);

        this.exp = playerTransferRequest.getExp();
        this.totalExp = playerTransferRequest.getTotalexp();
        this.level = playerTransferRequest.getLevel();

        this.fireticks = playerTransferRequest.getFireticks();
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getEntityId() {
        return entityId;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isSprinting() {
        return isSprinting;
    }

    public ItemStack[] getPlayerArmor() {
        return playerArmor;
    }

    public ItemStack[] getPlayerInventory() {
        return playerInventory;
    }

    public int getHandslot() {
        return handslot;
    }

    public boolean isFlying() {
        return isFlying;
    }

    public double getHealth() {
        return health;
    }

    public float getExhaustion() {
        return exhaustion;
    }

    public float getSaturation() {
        return saturation;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public int getFood() {
        return food;
    }

    public List<PotionEffect> getPotionEffects() {
        return potionEffects;
    }

    public float getExp() {
        return exp;
    }

    public int getLevel() {
        return level;
    }

    public int getTotalExp() {
        return totalExp;
    }

    public int getFireticks() {
        return fireticks;
    }
}
