package net.badlion.cosmetics;

import net.badlion.cosmetics.commands.CosmeticsInventoryCommand;
import net.badlion.cosmetics.commands.EquipCosmeticCommand;
import net.badlion.cosmetics.commands.GiveCosmeticCommand;
import net.badlion.cosmetics.commands.ToggleCosmetics;
import net.badlion.cosmetics.customparticles.BadlionLogo;
import net.badlion.cosmetics.inventories.ArrowTrailInventory;
import net.badlion.cosmetics.inventories.CosmeticsInventory;
import net.badlion.cosmetics.inventories.GadgetInventory;
import net.badlion.cosmetics.inventories.MorphInventory;
import net.badlion.cosmetics.inventories.ParticleInventory;
import net.badlion.cosmetics.inventories.PetInventory;
import net.badlion.cosmetics.inventories.RodTrailInventory;
import net.badlion.cosmetics.listeners.GlobalListener;
import net.badlion.cosmetics.listeners.PetListener;
import net.badlion.cosmetics.managers.ArrowTrailManager;
import net.badlion.cosmetics.managers.CosmeticsManager;
import net.badlion.cosmetics.managers.GadgetManager;
import net.badlion.cosmetics.managers.MorphManager;
import net.badlion.cosmetics.managers.PetManager;
import net.badlion.cosmetics.managers.RodTrailManager;
import net.badlion.cosmetics.tasks.CosmeticsTask;
import net.badlion.cosmetics.tasks.PetAITask;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Cosmetics extends JavaPlugin {

    private static Cosmetics plugin;

    private boolean arrowTrailsEnabled = true;
    private boolean gadgetsEnabled = true;
    private boolean morphsEnabled = true;
    private boolean particlesEnabled = true;
    private boolean petsEnabled = true;
    private boolean rodTrailsEnabled = true;

    private ArrowTrailManager arrowTrailManager;
    private RodTrailManager rodTrailManager;
    private GadgetManager gadgetManager;
    private MorphManager morphManager;
    //private ParticleManager particleManager;
    private Listener petListener;
    private PetManager petManager;

    public Cosmetics() {
        Gberry.enableProtocol = true;
    }

    public static Cosmetics getInstance() {
        return Cosmetics.plugin;
    }

    @Override
    public void onEnable() {
        Cosmetics.plugin = this;

        BadlionLogo.initialize();

        this.saveDefaultConfig();

        Gberry.enableAsyncDelayedLoginEvent = true;

        // Try to write the twitch.png file into the plugins directory
        Gberry.writeJarFile(this, "badlion.png");
        Gberry.writeJarFile(this, "badlion_logo_25.png");
        Gberry.writeJarFile(this, "badlion_logo_50.png");

        // Initialize inventory stuff
        SmellyInventory.initialize(this, false);
        CosmeticsInventory.initialize();
        ArrowTrailInventory.initialize();
        GadgetInventory.initialize();
        MorphInventory.initialize();
        ParticleInventory.initialize();
        PetInventory.initialize();
        RodTrailInventory.initialize();

        // Commands
        this.getCommand("cosmetics").setExecutor(new CosmeticsInventoryCommand());
        this.getCommand("equipcosmetic").setExecutor(new EquipCosmeticCommand());
        this.getCommand("givecosmetic").setExecutor(new GiveCosmeticCommand());
        this.getCommand("togglecosmetics").setExecutor(new ToggleCosmetics());

        // Listeners
        this.getServer().getPluginManager().registerEvents(new CosmeticsInventory(), this);
        this.getServer().getPluginManager().registerEvents(new CosmeticsManager(), this);
        this.getServer().getPluginManager().registerEvents(new GlobalListener(), this);

        this.arrowTrailManager = new ArrowTrailManager();
        this.getServer().getPluginManager().registerEvents(this.arrowTrailManager, this);

        this.rodTrailManager = new RodTrailManager();
        this.getServer().getPluginManager().registerEvents(this.rodTrailManager, this);

        this.gadgetManager = new GadgetManager();
        this.getServer().getPluginManager().registerEvents(this.gadgetManager, this);

        this.morphManager = new MorphManager();
        this.getServer().getPluginManager().registerEvents(this.morphManager, this);

        //this.particleManager = new ParticleManager();
        //this.getServer().getPluginManager().registerEvents(this.particleManager, this);

        this.petListener = new PetListener();
        this.getServer().getPluginManager().registerEvents(this.petListener, this);

        this.petManager = new PetManager();
        this.getServer().getPluginManager().registerEvents(this.petManager, this);

        // Tasks
        BukkitUtil.runTaskTimer(new CosmeticsTask(), 20L, 1L);
        BukkitUtil.runTaskTimer(new PetAITask(), 20L, 5L);
    }

    @Override
    public void onDisable() {
        // Pets
        for (UUID uuid : PetManager.getSpawnedPets().keySet()) {
            PetManager.despawnPet(this.getServer().getPlayer(uuid), null, true);
        }
    }

    private void safeTeleportEntity(LivingEntity entity, Player player) {
        // Get this before because teleportation breaks leash
        boolean leash = entity.isLeashed();

        entity.teleport(player.getLocation().add(0D, 2D, 0D));

        if (leash) {
            entity.setLeashHolder(player);
        }
    }

    public boolean isPetsEnabled() {
        return petsEnabled;
    }

    public boolean isGadgetsEnabled() {
        return gadgetsEnabled;
    }

    public boolean isArrowTrailsEnabled() {
        return arrowTrailsEnabled;
    }

    public boolean isMorphsEnabled() {
        return morphsEnabled;
    }

    public boolean isParticlesEnabled() {
        return particlesEnabled;
    }

    public boolean isRodTrailsEnabled() {
        return rodTrailsEnabled;
    }

    public boolean isAllDisabled() {
        return !petsEnabled && !gadgetsEnabled && !arrowTrailsEnabled && !morphsEnabled && !particlesEnabled;
    }

    // Mightn't be the nicest way, but i saw it as a way to keep it small, less copy-paste
    public void setCosmeticEnabled(CosmeticType type, boolean enabled) {
        switch (type) {
            case ARROW_TRAIL:
                this.arrowTrailsEnabled = enabled;

                if (enabled) {
                    BukkitUtil.registerListener(this.arrowTrailManager);
                } else {
                    BukkitUtil.unregisterListener(this.arrowTrailManager);
                }
                break;
            case ROD_TRAIL:
                this.rodTrailsEnabled = enabled;

                if (enabled) {
                    BukkitUtil.registerListener(this.rodTrailManager);
                } else {
                    BukkitUtil.unregisterListener(this.rodTrailManager);
                }
                break;
            case GADGET:
                this.gadgetsEnabled = enabled;

                if (enabled) {
                    BukkitUtil.registerListener(this.gadgetManager);
                } else {
                    BukkitUtil.unregisterListener(this.gadgetManager);
                }

                // TODO: Declan remove their gadget (e.g. enderpearls)
                break;
            case MORPH:
                this.morphsEnabled = enabled;

                if (enabled) {
                    BukkitUtil.registerListener(this.morphManager);
                    this.morphManager.activateListeners();
                } else {
                    BukkitUtil.unregisterListener(this.morphManager);
                    this.morphManager.deactivateListeners();

                    // Remove morphs
                    for (CosmeticsManager.CosmeticsSettings cosmeticsSettings : CosmeticsManager.getCosmeticsSettings().values()) {
                        if (cosmeticsSettings.getActiveMorph() != null) {
                            Player player = this.getServer().getPlayer(cosmeticsSettings.getUuid());
                            // TODO: Is this everything?
	                        // TODO: PLAYER CAN BE OFFLINE HERE, IDK WHY
	                        if (player != null) {
		                        cosmeticsSettings.getActiveMorph().removeMorph(player);
	                        }
                            cosmeticsSettings.setActiveMorph(null, false);
                        }
                    }
                }
                break;
            case PET:
                this.petsEnabled = enabled;

                if (enabled) {
                    BukkitUtil.registerListener(this.petManager);
                    BukkitUtil.registerListener(this.petListener);
                    this.petManager.activateListeners();
                } else {
                    BukkitUtil.unregisterListener(this.petManager);
                    BukkitUtil.unregisterListener(this.petListener);
                    this.petManager.deactivateListeners();

                    // Despawn pets
                    List<UUID> uuids = new ArrayList<>();
                    uuids.addAll(PetManager.getSpawnedPets().keySet());
                    for (UUID uuid : uuids) {
                        // TODO: Is this everything?
                        PetManager.despawnPet(this.getServer().getPlayer(uuid), null);
                    }
                }
                break;
            case PARTICLE:
                this.particlesEnabled = enabled;

                //if (enabled) {
                //	BukkitUtil.registerListener(this.particleManager);
                //} else {
                //	BukkitUtil.unregisterListener(this.particleManager);
                //}
                break;
            default:
                throw new IndexOutOfBoundsException("Invalid Cosmetic Type");
        }
    }

    public void allowCosmetics() {
        // Enable all cosmetics
        for (CosmeticType type : CosmeticType.values()) {
            this.setCosmeticEnabled(type, true);
        }
    }

    public void disallowCosmetics() {
        // Disable all cosmetics
        for (CosmeticType type : CosmeticType.values()) {
            this.setCosmeticEnabled(type, false);
        }

        // Despawn pets
        List<UUID> uuids = new ArrayList<>();
        uuids.addAll(PetManager.getSpawnedPets().keySet());
        for (UUID uuid : uuids) {
            PetManager.despawnPet(this.getServer().getPlayer(uuid), null);
        }

        // Remove morphs
        for (CosmeticsManager.CosmeticsSettings cosmeticsSettings : CosmeticsManager.getCosmeticsSettings().values()) {
            if (cosmeticsSettings.getActiveMorph() != null) {
                Player player = this.getServer().getPlayer(cosmeticsSettings.getUuid());
                cosmeticsSettings.getActiveMorph().removeMorph(player);
            }
        }
    }

    public enum CosmeticType {ARROW_TRAIL, GADGET, MORPH, PET, PARTICLE, ROD_TRAIL}

}
