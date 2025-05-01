package net.badlion.cosmetics.managers;

import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.arrowtrails.ArrowTrail;
import net.badlion.cosmetics.events.ActivateCosmeticEvent;
import net.badlion.cosmetics.gadgets.Gadget;
import net.badlion.cosmetics.morphs.Morph;
import net.badlion.cosmetics.particles.Particle;
import net.badlion.cosmetics.pets.Pet;
import net.badlion.cosmetics.rodtrails.RodTrail;
import net.badlion.cosmetics.utils.MorphUtil;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.FinishedUserDataEvent;
import net.badlion.gberry.events.GSyncEvent;
import net.badlion.gberry.managers.UserDataManager;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.gpermissions.GPermissions;
import net.badlion.smellycases.SmellyCases;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CosmeticsManager implements Listener {

    private static Map<UUID, Set<Cosmetics.CosmeticType>> disabledCosmeticTypes = new HashMap<>();
    private static Map<UUID, CosmeticsSettings> cosmeticSettings = new ConcurrentHashMap<>();
    private Set<UUID> loaded = new HashSet<>();

    public static CosmeticsSettings createCosmeticsSettings(UUID uuid) {
        return CosmeticsManager.cosmeticSettings.put(uuid, new CosmeticsSettings(uuid));
    }

    public static CosmeticsSettings getCosmeticsSettings(UUID uuid) {
        return CosmeticsManager.cosmeticSettings.get(uuid);
    }

    public static Map<UUID, CosmeticsSettings> getCosmeticsSettings() {
        return CosmeticsManager.cosmeticSettings;
    }

    public static CosmeticsSettings removeCosmeticsSettings(UUID uuid) {
        return CosmeticsManager.cosmeticSettings.remove(uuid);
    }

    // Disables use of a cosmetic type for a player
    // TODO: DECLAN FIX THIS FOR HORSE PETS
    public static void disableCosmetic(UUID uuid, Cosmetics.CosmeticType type) {
        Set<Cosmetics.CosmeticType> disabledCosmeticTypes = CosmeticsManager.disabledCosmeticTypes.get(uuid);
        if (disabledCosmeticTypes == null) {
            disabledCosmeticTypes = new HashSet<>();
        }

        disabledCosmeticTypes.add(type);
        CosmeticsManager.disabledCosmeticTypes.put(uuid, disabledCosmeticTypes);

        // Remove active cosmetic
        CosmeticsSettings cosmeticsSettings = getCosmeticsSettings(uuid);
        if (cosmeticsSettings == null) {
            return;
        }

        switch (type) {
            case MORPH:
                cosmeticsSettings.setActiveMorph(null, true);
                break;
            case ARROW_TRAIL:
                cosmeticsSettings.setActiveArrowTrail(null, true);
                break;
            case PARTICLE:
                cosmeticsSettings.setActiveParticle(null, true);
                break;
            case PET:
                cosmeticsSettings.setActivePet(null, true);
                break;
            case GADGET:
                cosmeticsSettings.setActiveGadget(null);
                break;
            case ROD_TRAIL:
                cosmeticsSettings.setActiveGadget(null);
                break;
        }
    }

    // Re-allow the player to use a cosmetic type
    public static void enableCosmetic(UUID uuid, Cosmetics.CosmeticType type) {
        Set<Cosmetics.CosmeticType> disabledCosmeticTypes = CosmeticsManager.disabledCosmeticTypes.get(uuid);
        if (disabledCosmeticTypes == null) {
            return;
        }

        disabledCosmeticTypes.remove(type);
        CosmeticsManager.disabledCosmeticTypes.put(uuid, disabledCosmeticTypes);
    }

    // Check if a type is disabled for someone
    public static boolean isCosmeticDisabled(UUID uuid, Cosmetics.CosmeticType type) {
        Set<Cosmetics.CosmeticType> cosmeticTypes = disabledCosmeticTypes.get(uuid);
        return cosmeticTypes != null && cosmeticTypes.contains(type);
    }

    private void loadCosmetics(final UUID uuid) {
        if (this.loaded.contains(uuid)) {
            return;
        }

        // Load cosmetics
        CosmeticsManager.createCosmeticsSettings(uuid);

        final CosmeticsSettings cosmeticsSettings = CosmeticsManager.getCosmeticsSettings(uuid);

        Cosmetics.getInstance().getServer().getScheduler().runTask(Cosmetics.getInstance(), new Runnable() {
            @Override
            public void run() {
                final Player player = Bukkit.getPlayer(uuid);

                if (player == null) return;

                // Pets
                if (Cosmetics.getInstance().isPetsEnabled() || CosmeticsManager.isCosmeticDisabled(player.getUniqueId(), Cosmetics.CosmeticType.PET)) {
                    if (cosmeticsSettings.getActivePet() != null) {
                        ActivateCosmeticEvent activateCosmeticEvent = new ActivateCosmeticEvent(player, cosmeticsSettings.getActivePet());
                        Cosmetics.getInstance().getServer().getPluginManager().callEvent(activateCosmeticEvent);

                        if (!activateCosmeticEvent.isCancelled()) {
                            PetManager.spawnPet(player, cosmeticsSettings, cosmeticsSettings.getActivePet());
                            player.getInventory().setItem(3, ItemStackUtil.createItem(Material.BLAZE_POWDER, ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Despawn Pet"));
                        }
                    }
                }

                // Arrow Trails
                if (Cosmetics.getInstance().isArrowTrailsEnabled() || CosmeticsManager.isCosmeticDisabled(player.getUniqueId(), Cosmetics.CosmeticType.ARROW_TRAIL)) {
                    if (cosmeticsSettings.getActiveArrowTrail() != null && Gberry.serverType == Gberry.ServerType.LOBBY) {
                        ActivateCosmeticEvent activateCosmeticEvent = new ActivateCosmeticEvent(player, cosmeticsSettings.getActiveArrowTrail());
                        Cosmetics.getInstance().getServer().getPluginManager().callEvent(activateCosmeticEvent);

                        if (!activateCosmeticEvent.isCancelled()) {
                            ItemStack bow = ItemStackUtil.createItem(Material.BOW, ChatColor.GOLD + ChatColor.BOLD.toString() + "Particle Bow");
                            ItemMeta bowM = bow.getItemMeta();
                            bowM.spigot().setUnbreakable(true);
                            bow.setItemMeta(bowM);

                            player.getInventory().setItem(7, Gberry.getGlowItem(bow));
                            player.getInventory().setItem(9, new ItemStack(Material.ARROW));
                        }
                    }
                }

                // Morphs
                if (Cosmetics.getInstance().isMorphsEnabled() || CosmeticsManager.isCosmeticDisabled(player.getUniqueId(), Cosmetics.CosmeticType.MORPH)) {
                    // Set Active Morph
                    if (cosmeticsSettings.getActiveMorph() != null) {
                        ActivateCosmeticEvent activateCosmeticEvent = new ActivateCosmeticEvent(player, cosmeticsSettings.getActiveMorph());
                        Cosmetics.getInstance().getServer().getPluginManager().callEvent(activateCosmeticEvent);

                        if (!activateCosmeticEvent.isCancelled()) {
                            if (cosmeticsSettings.getActiveMorph().getMorphType() == MorphUtil.MorphType.WITHER_SKELETON) {
                                player.setWalkSpeed(0.6f);
                            } else {
                                player.setWalkSpeed(0.2f);
                            }
                            new MorphUtil(cosmeticsSettings.getActiveMorph().getMorphType(), player);
                            player.getInventory().setItem(4,
                                    ItemStackUtil.createItem(
                                            Material.MONSTER_EGG, ChatColor.RED + ChatColor.BOLD.toString() + "Morph Ability",
                                            ChatColor.GREEN + "Right/left click with this",
                                            ChatColor.GREEN + "item in hand to use your",
                                            ChatColor.GREEN + "active morph abilities."
                                    )
                            );
                            player.getInventory().setItem(2, ItemStackUtil.createItem(Material.ANVIL, ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Remove Morph"));
                        }
                    }

                    // Show the player all the currently morphed players
                    for (Map.Entry<UUID, CosmeticsManager.CosmeticsSettings> entry : CosmeticsManager.getCosmeticsSettings().entrySet()) {
                        if (entry.getValue().getActiveMorph() != null && entry.getKey() != uuid) {
                            new MorphUtil(entry.getValue().getActiveMorph().getMorphType(), Bukkit.getPlayer(entry.getKey())).sendPlayerSetMorph(player, false);
                        }
                    }
                }

                CosmeticsManager.this.loaded.add(uuid);
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onUserDataLoad(FinishedUserDataEvent event) {
        this.loadCosmetics(event.getUuid());
    }

    @EventHandler
    public void onGSync(GSyncEvent event) {
        if (event.getArgs().size() < 4) {
            return;
        }

        String subChannel = event.getArgs().get(0);
        if (subChannel.equals("Cosmetics")) {
            String msg = event.getArgs().get(1);
            UUID uuid = UUID.fromString(event.getArgs().get(2));

            if (!Gberry.isPlayerOnline(SmellyCases.getInstance().getServer().getPlayer(uuid))) {
                return; // Only handle if they are online
            }

            CosmeticsSettings cosmeticsSettings = getCosmeticsSettings(uuid);
            if (cosmeticsSettings == null) {
                return;
            }

            List<String> cosmeticsToChange = event.getArgs().subList(3, event.getArgs().size());

            if (msg.equals("remove")) {
                for (String cosmetic : cosmeticsToChange) {
                    cosmeticsSettings.removeCosmetic(Cosmetics.CosmeticType.valueOf(cosmetic.split("-")[0].toUpperCase()), cosmetic.split("-")[1], false);
                }
            } else if (msg.equals("add")) {
                for (String cosmetic : cosmeticsToChange) {
                    cosmeticsSettings.addCosmetic(Cosmetics.CosmeticType.valueOf(cosmetic.split("-")[0].toUpperCase()), cosmetic.split("-")[1]);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        // Reset walk speed in case it was set
        event.getPlayer().setWalkSpeed(0.2f);

        // Remove CosmeticsSettings object
        CosmeticsManager.removeCosmeticsSettings(event.getPlayer().getUniqueId());
        this.loaded.remove(event.getPlayer().getUniqueId());
    }

    public static class CosmeticsSettings {

        private boolean loaded;

        private UUID uuid;

        private RodTrail activeRodTrail;
        private Set<RodTrail> ownedRodTrails = new LinkedHashSet<>();

        private Gadget activeGadget;
        private Set<Gadget> ownedGadgets = new LinkedHashSet<>();

        private Morph activeMorph;
        private Set<Morph> ownedMorphs = new LinkedHashSet<>();

        private Particle activeParticle;
        private Set<Particle> ownedParticles = new LinkedHashSet<>();

        private ArrowTrail activeArrowTrail;
        private Set<ArrowTrail> ownedArrowTrails = new LinkedHashSet<>();

        private Pet activePet;
        private Map<String, String> ownedPets = new LinkedHashMap<>();

        public CosmeticsSettings(UUID uuid) {
            this.uuid = uuid;

            // Try to fix race condition? IDK
            if (this.uuid == null) {
                return;
            }

            this.loadCosmeticsData();

            CosmeticsManager.cosmeticSettings.put(uuid, this);
        }

        public void loadCosmeticsData() {
            UserDataManager.UserData userData = UserDataManager.getUserData(this.uuid);
            if (userData == null) {
                return;
            }
            JSONObject data = userData.getCosmetics();

            for (Object key : data.keySet()) {
                String keyString = (String) key;
                String[] s = keyString.split("-");
                // pet-lucky_sheep-name

                switch (s[0]) {
                    case "pet":
                        if (s[1].equals("active")) {
                            Object petInternalName = data.get(key);
                            if (petInternalName == null) {
                                // They don't have an active pet
                                this.activePet = null;
                            } else {
                                this.activePet = PetManager.getPet(petInternalName.toString());
                            }
                        } else {
                            String petInternalName = s[1];

                            if (data.get(key).equals(true)) {
                                // Check if this is a custom name
                                if (s.length == 3) {
                                    // Put this name in our mapping
                                    this.ownedPets.put(petInternalName, s[2]);
                                } else {
                                    // Was a custom name for this pet already loaded?
                                    String customName = this.ownedPets.get(petInternalName);
                                    this.ownedPets.put(petInternalName, customName);
                                }
                            }
                        }
                        break;

                    case "particle":
                        if (s[1].equals("active")) {
                            Object particleInternalName = data.get(key);
                            if (particleInternalName == null) {
                                // They don't have an active particle
                                this.activeParticle = null;
                            } else {
                                this.activeParticle = ParticleManager.getParticle(particleInternalName.toString());
                            }
                        } else {
                            if (data.get(key).equals(true)) {
                                this.ownedParticles.add(ParticleManager.getParticle(s[1]));
                            }
                        }
                        break;

                    case "morph":
                        if (s[1].equals("active")) {
                            Object morphInternalName = data.get(key);
                            if (morphInternalName == null) {
                                // They don't have an active morph
                                this.activeMorph = null;
                            } else {
                                this.activeMorph = MorphManager.getMorph(morphInternalName.toString());
                            }
                        } else {
                            if (data.get(key).equals(true)) {
                                this.ownedMorphs.add(MorphManager.getMorph(s[1]));
                            }
                        }
                        break;

                    case "arrow_trail":
                        if (s[1].equals("active")) {
                            Object arrowTrailInternalName = data.get(key);
                            if (arrowTrailInternalName == null) {
                                // They don't have an active arrow trail
                                this.activeArrowTrail = null;
                            } else {
                                this.activeArrowTrail = ArrowTrailManager.getArrowTrail(arrowTrailInternalName.toString());
                            }
                        } else {
                            if (data.get(key).equals(true)) {
                                this.ownedArrowTrails.add(ArrowTrailManager.getArrowTrail(s[1]));
                            }
                        }
                        break;

                    case "gadget":
                        if (s[1].equals("active")) {
                            Object gadgetInternalName = data.get(key);
                            if (gadgetInternalName == null) {
                                // They don't have an active gadget
                                this.activeGadget = null;
                            } else {
                                this.activeGadget = GadgetManager.getGadget(gadgetInternalName.toString());
                            }
                        } else {
                            if (data.get(key).equals(true)) {
                                this.ownedGadgets.add(GadgetManager.getGadget(s[1]));
                            }
                        }
                        break;

                    case "rod_trail":
                        if (s[1].equals("active")) {
                            Object rodTrailInternalName = data.get(key);
                            if (rodTrailInternalName == null) {
                                // They don't have an active rod trail
                                this.activeRodTrail = null;
                            } else {
                                this.activeRodTrail = RodTrailManager.getRodTrail(rodTrailInternalName.toString());
                            }
                        } else {
                            if (data.get(key).equals(true)) {
                                this.ownedRodTrails.add(RodTrailManager.getRodTrail(s[1]));
                            }
                        }
                        break;

                    default:
                        throw new IllegalArgumentException("\"" + s[0] + "\" is an invalid type when loading cosmetics data.");
                }
            }

            this.setLoaded();
        }

        private void savePetName(final String smellyPetName, final String customName) {
            UserDataManager.UserData userData = UserDataManager.getUserData(this.uuid);
            JSONObject data = userData.getCosmetics();

            data.put("pet-" + smellyPetName + "-name", customName);

            userData.setCosmetics(data, true);
        }

        // Less copy-paste stuff with these two methods below
        private void setActiveCosmetic(Cosmetics.CosmeticType cosmeticType, String cosmeticName, boolean update) {
            UserDataManager.UserData userData = UserDataManager.getUserData(this.uuid);
            JSONObject data = userData.getCosmetics();

            data.put(cosmeticType.name().toLowerCase() + "-active", cosmeticName != null ? cosmeticName.toLowerCase() : null);

            userData.setCosmetics(data, update);
        }

        private void addCosmetic(Cosmetics.CosmeticType cosmeticType, String cosmeticName) {
            // Can't add null cosmetic ayy lmao
            if (cosmeticName != null) {
                // Check if they have the cosmetic already
                switch (cosmeticType) {
                    case MORPH:
                        Morph morph = MorphManager.getMorph(cosmeticName);
                        if (hasMorph(morph)) {
                            return;
                        }
                        this.ownedMorphs.add(morph);
                        break;
                    case PET:
                        if (hasPet(cosmeticName)) {
                            return;
                        }
                        this.ownedPets.put(cosmeticName, null);
                        break;
                    case ARROW_TRAIL:
                        ArrowTrail arrowTrail = ArrowTrailManager.getArrowTrail(cosmeticName);
                        if (hasArrowTrail(arrowTrail)) {
                            return;
                        }
                        this.ownedArrowTrails.add(arrowTrail);
                        break;
                    case PARTICLE:
                        Particle particle = ParticleManager.getParticle(cosmeticName);
                        if (hasParticle(particle)) {
                            return;
                        }
                        this.ownedParticles.add(particle);
                        break;
                    case GADGET:
                        Gadget gadget = GadgetManager.getGadget(cosmeticName);
                        if (hasGadget(gadget)) {
                            return;
                        }
                        this.ownedGadgets.add(gadget);
                        break;
                    case ROD_TRAIL:
                        RodTrail rodTrail = RodTrailManager.getRodTrail(cosmeticName);
                        if (hasRodTrail(rodTrail)) {
                            return;
                        }
                        this.ownedRodTrails.add(rodTrail);
                        break;
                }

                UserDataManager.UserData userData = UserDataManager.getUserData(this.uuid);
                JSONObject data = userData.getCosmetics();

                data.put(cosmeticType.name().toLowerCase() + "-" + cosmeticName, true);

                userData.setCosmetics(data, true);
            }
        }

        private void removeCosmetic(Cosmetics.CosmeticType cosmeticType, String cosmeticName, boolean update) {
            // Can't remove null cosmetic ayy lmao
            if (cosmeticName != null) {
                // Check if they don't have the cosmetic already
                switch (cosmeticType) {
                    case MORPH:
                        Morph morph = MorphManager.getMorph(cosmeticName);
                        if (!hasMorph(morph)) {
                            return;
                        }
                        if (this.activeMorph != null && this.activeMorph.equals(morph)) {
                            setActiveMorph(null, update);
                        }
                        // Local update
                        this.ownedMorphs.remove(morph);
                        break;
                    case PET:
                        // NOTE: Don't use hasPet() because of special Donator temp pets
                        if (!this.ownedPets.containsKey(cosmeticName)) {
                            return;
                        }
                        if (this.activePet != null && this.activePet.equals(PetManager.getPet(cosmeticName))) {
                            setActivePet(null, update);
                        }
                        this.ownedPets.remove(cosmeticName);
                        break;
                    case ARROW_TRAIL:
                        ArrowTrail arrowTrail = ArrowTrailManager.getArrowTrail(cosmeticName);
                        if (!hasArrowTrail(arrowTrail)) {
                            return;
                        }
                        if (this.activeArrowTrail != null && this.activeArrowTrail.equals(arrowTrail)) {
                            setActiveArrowTrail(null, update);
                        }
                        this.ownedArrowTrails.remove(arrowTrail);
                        break;
                    case PARTICLE:
                        Particle particle = ParticleManager.getParticle(cosmeticName);
                        if (!hasParticle(particle)) {
                            return;
                        }
                        if (this.activeParticle != null && this.activeParticle.equals(particle)) {
                            setActiveParticle(null, update);
                        }
                        this.ownedParticles.remove(particle);
                        break;
                    case GADGET:
                        Gadget gadget = GadgetManager.getGadget(cosmeticName);
                        if (!hasGadget(gadget)) {
                            return;
                        }
                        if (this.activeGadget != null && this.activeGadget.equals(gadget)) {
                            setActiveGadget(null);
                        }
                        this.ownedGadgets.remove(gadget);
                        break;
                    case ROD_TRAIL:
                        RodTrail rodTrail = RodTrailManager.getRodTrail(cosmeticName);
                        if (!hasRodTrail(rodTrail)) {
                            return;
                        }
                        if (this.activeRodTrail != null && this.activeRodTrail.equals(rodTrail)) {
                            setActiveRodTrail(null, update);
                        }
                        this.ownedRodTrails.remove(rodTrail);
                        break;
                }

                UserDataManager.UserData userData = UserDataManager.getUserData(this.uuid);
                JSONObject data = userData.getCosmetics();

                data.put(cosmeticType.name().toLowerCase() + "-" + cosmeticName.toLowerCase(), false);

                userData.setCosmetics(data, update);
            }
        }

        public Map<String, String> getOwnedPets() {
            return ownedPets;
        }

        public Set<Morph> getOwnedMorphs() {
            return ownedMorphs;
        }

        public ArrowTrail getActiveArrowTrail() {
            return activeArrowTrail;
        }

        public void setActiveArrowTrail(ArrowTrail activeArrowTrail, boolean update) {
            if (this.activeArrowTrail == null && activeArrowTrail == null) {
                return;
            }

            Player player = Bukkit.getPlayer(this.uuid);
            if (player != null && activeArrowTrail == null) {
                if (Gberry.serverType == Gberry.ServerType.LOBBY) {
                    player.getInventory().setItem(7, ItemStackUtil.EMPTY_ITEM);
                }
            }

            if (activeArrowTrail != null) {
                ActivateCosmeticEvent activateCosmeticEvent = new ActivateCosmeticEvent(Cosmetics.getInstance().getServer().getPlayer(this.uuid),
                        activeMorph);
                Cosmetics.getInstance().getServer().getPluginManager().callEvent(activateCosmeticEvent);

                if (activateCosmeticEvent.isCancelled() || CosmeticsManager.isCosmeticDisabled(this.uuid, Cosmetics.CosmeticType.ARROW_TRAIL)) {
                    return;
                }

                this.setActiveCosmetic(Cosmetics.CosmeticType.ARROW_TRAIL, activeArrowTrail.getName(), update);
                this.activeArrowTrail = activeArrowTrail;
            } else {
                this.setActiveCosmetic(Cosmetics.CosmeticType.ARROW_TRAIL, null, update);
                this.activeArrowTrail = null;
            }
        }

        public Set<ArrowTrail> getOwnedArrowTrails() {
            return ownedArrowTrails;
        }

        public Set<RodTrail> getOwnedRodTrails() {
            return ownedRodTrails;
        }

        public Set<Gadget> getOwnedGadgets() {
            return ownedGadgets;
        }

        public Set<Particle> getOwnedParticles() {
            return ownedParticles;
        }

        public Particle getActiveParticle() {
            return activeParticle;
        }

        public void setActiveParticle(Particle activeParticle, boolean update) {
            if (this.activeParticle == null && activeParticle == null) {
                return;
            }

            if (activeParticle != null) {
                ActivateCosmeticEvent activateCosmeticEvent = new ActivateCosmeticEvent(Cosmetics.getInstance().getServer().getPlayer(this.uuid),
                        activeParticle);
                Cosmetics.getInstance().getServer().getPluginManager().callEvent(activateCosmeticEvent);

                if (activateCosmeticEvent.isCancelled() || CosmeticsManager.isCosmeticDisabled(this.uuid, Cosmetics.CosmeticType.PARTICLE)) {
                    return;
                }
                this.setActiveCosmetic(Cosmetics.CosmeticType.PARTICLE, activeParticle.getName(), update);
                this.activeParticle = activeParticle;
            } else {
                this.setActiveCosmetic(Cosmetics.CosmeticType.PARTICLE, null, update);
                this.activeParticle = null;
            }
        }

        public Pet getActivePet() {
            return activePet;
        }

        public void setActivePet(Pet activePet, boolean update) {
            if (this.activePet == null && activePet == null) {
                return;
            }

            // Donator perks
            if (activePet != null) {
                String smellyPetName = activePet.getName();
                if (!this.ownedPets.containsKey(smellyPetName)) {
                    if (GPermissions.plugin.getUserGroup(this.uuid).getName().equals("lion")) {
                        if (smellyPetName.equals("lion") || smellyPetName.equals("lucky_sheep") || smellyPetName.equals("slime")) {
                            update = false;
                        }
                    } else if (GPermissions.plugin.getUserGroup(this.uuid).getName().equals("donatorplus")) {
                        if (smellyPetName.equals("lucky_sheep") || smellyPetName.equals("slime")) {
                            update = false;
                        }
                    } else if (GPermissions.plugin.getUserGroup(this.uuid).getName().equals("donator")) {
                        if (smellyPetName.equals("slime")) {
                            update = false;
                        }
                    }
                }
            }

            Player player = Bukkit.getPlayer(this.uuid);
            if (player != null && activePet == null) {
                player.getInventory().setItem(3, ItemStackUtil.EMPTY_ITEM);
                player.updateInventory();
            }

            if (activePet != null) {
                ActivateCosmeticEvent activateCosmeticEvent = new ActivateCosmeticEvent(Cosmetics.getInstance().getServer().getPlayer(this.uuid), activePet);
                Cosmetics.getInstance().getServer().getPluginManager().callEvent(activateCosmeticEvent);

                if (activateCosmeticEvent.isCancelled() || CosmeticsManager.isCosmeticDisabled(this.uuid, Cosmetics.CosmeticType.PET)) {
                    return;
                }
                this.setActiveCosmetic(Cosmetics.CosmeticType.PET, activePet.getName(), update);
                this.activePet = activePet;
            } else {
                this.setActiveCosmetic(Cosmetics.CosmeticType.PET, null, update);
                this.activePet = null;
            }

        }

        public RodTrail getActiveRodTrail() {
            return activeRodTrail;
        }

        public void setActiveRodTrail(RodTrail activeRodTrail, boolean update) {
            if (this.activeRodTrail == null && activeRodTrail == null) {
                return;
            }

            if (activeRodTrail != null) {
                ActivateCosmeticEvent activateCosmeticEvent = new ActivateCosmeticEvent(Cosmetics.getInstance().getServer().getPlayer(this.uuid), activeRodTrail);
                Cosmetics.getInstance().getServer().getPluginManager().callEvent(activateCosmeticEvent);

                if (activateCosmeticEvent.isCancelled() || CosmeticsManager.isCosmeticDisabled(this.uuid, Cosmetics.CosmeticType.ROD_TRAIL)) {
                    return;
                }

                this.setActiveCosmetic(Cosmetics.CosmeticType.ROD_TRAIL, activeRodTrail.getName(), update);
                this.activeRodTrail = activeRodTrail;
            } else {
                this.setActiveCosmetic(Cosmetics.CosmeticType.ROD_TRAIL, null, update);
                this.activeRodTrail = null;
            }
        }

        public Morph getActiveMorph() {
            return activeMorph;
        }

        public void setActiveMorph(Morph activeMorph, boolean update) {
            // Anti-Cheat prevention
            Player player = Bukkit.getPlayer(this.uuid);
            if (player != null) {
                player.setBypassGCheat(false);
                if (activeMorph == null) {
                    player.setWalkSpeed(0.2f);
                    player.getInventory().setItem(4, ItemStackUtil.EMPTY_ITEM);
                    player.getInventory().setItem(2, ItemStackUtil.EMPTY_ITEM);
                    player.updateInventory();
                }
            }

            if (this.activeMorph == null && activeMorph == null) {
                return;
            }

            if (activeMorph != null) {
                ActivateCosmeticEvent activateCosmeticEvent = new ActivateCosmeticEvent(Cosmetics.getInstance().getServer().getPlayer(this.uuid), activeMorph);
                Cosmetics.getInstance().getServer().getPluginManager().callEvent(activateCosmeticEvent);

                if (activateCosmeticEvent.isCancelled() || CosmeticsManager.isCosmeticDisabled(this.uuid, Cosmetics.CosmeticType.MORPH)) {
                    return;
                }
                this.setActiveCosmetic(Cosmetics.CosmeticType.MORPH, activeMorph.getName(), update);
                this.activeMorph = activeMorph;
            } else {
                this.setActiveCosmetic(Cosmetics.CosmeticType.MORPH, null, update);
                this.activeMorph = null;
            }

        }

        public Gadget getActiveGadget() {
            return activeGadget;
        }

        public void setActiveGadget(Gadget activeGadget) {
            if (this.activeGadget == null && activeGadget == null) {
                return;
            }

            Player player = Cosmetics.getInstance().getServer().getPlayer(this.uuid);
            if (player != null && activeGadget == null) {
                player.getInventory().setItem(5, ItemStackUtil.EMPTY_ITEM); // TODO: We need to un-hardcode the inventory slots and make it so diff servers can put it in diff spots
                player.updateInventory();
            }

            if (activeGadget != null) {
                ActivateCosmeticEvent activateCosmeticEvent = new ActivateCosmeticEvent(Cosmetics.getInstance().getServer().getPlayer(this.uuid), activeGadget);
                Cosmetics.getInstance().getServer().getPluginManager().callEvent(activateCosmeticEvent);

                if (activateCosmeticEvent.isCancelled() || CosmeticsManager.isCosmeticDisabled(this.uuid, Cosmetics.CosmeticType.GADGET)) {
                    return;
                }
            }

            this.activeGadget = activeGadget;
        }

        public String getPetDisplayName(String smellyPetName) {
            return this.ownedPets.get(smellyPetName);
        }

        public UUID getUuid() {
            return uuid;
        }

        public boolean isLoaded() {
            return loaded;
        }

        public boolean hasGadget(Gadget gadget) {
            return this.ownedGadgets.contains(gadget);
        }

        public boolean hasRodTrail(RodTrail rodTrail) {
            return this.ownedRodTrails.contains(rodTrail);
        }

        public boolean hasParticle(Particle particle) {
            return this.ownedParticles.contains(particle);
        }

        public boolean hasPet(String smellyPetName) {
            // Donator perks
            if (GPermissions.plugin.getUserGroup(this.uuid).getName().equals("lion")) {
                if (smellyPetName.equals("lion") || smellyPetName.equals("lucky_sheep") || smellyPetName.equals("slime")) {
                    return true;
                }
            }

            if (GPermissions.plugin.getUserGroup(this.uuid).getName().equals("donatorplus")) {
                if (smellyPetName.equals("lucky_sheep") || smellyPetName.equals("slime")) {
                    return true;
                }
            }

            if (GPermissions.plugin.getUserGroup(this.uuid).getName().equals("donator")) {
                if (smellyPetName.equals("slime")) {
                    return true;
                }
            }

            return this.ownedPets.containsKey(smellyPetName);
        }

        public boolean hasArrowTrail(ArrowTrail arrowtrail) {
            return this.ownedArrowTrails.contains(arrowtrail);
        }

        public boolean hasMorph(Morph morph) {
            return this.ownedMorphs.contains(morph);
        }

        public void setLoaded() {
            this.loaded = true;
        }

        public void renamePet(String smellyPetName, String name) {
            this.savePetName(smellyPetName, name);
            this.ownedPets.put(smellyPetName, name);
        }

        public void addParticle(Particle particle) {
            this.addCosmetic(Cosmetics.CosmeticType.PARTICLE, particle.getName());
        }

        public void addPet(String smellyPetName) {
            this.addCosmetic(Cosmetics.CosmeticType.PET, smellyPetName);
        }

        public void addGadget(Gadget gadget) {
            this.addCosmetic(Cosmetics.CosmeticType.GADGET, gadget.getName());
        }

        public void addRodTrail(RodTrail rodTrail) {
            this.addCosmetic(Cosmetics.CosmeticType.ROD_TRAIL, rodTrail.getName());
        }

        public void addArrowTrail(ArrowTrail arrowtrail) {
            this.addCosmetic(Cosmetics.CosmeticType.ARROW_TRAIL, arrowtrail.getName());
        }

        public void addMorph(Morph morph) {
            this.addCosmetic(Cosmetics.CosmeticType.MORPH, morph.getName());
        }

        public void removeParticle(Particle particle) {
            this.removeCosmetic(Cosmetics.CosmeticType.PARTICLE, particle.getName(), true);
        }

        public void removePet(String smellyPetName) {
            this.removeCosmetic(Cosmetics.CosmeticType.PET, smellyPetName, true);
        }

        public void removeGadget(Gadget gadget) {
            this.removeCosmetic(Cosmetics.CosmeticType.GADGET, gadget.getName(), true);
        }

        public void removeRodTrail(RodTrail rodTrail) {
            this.removeCosmetic(Cosmetics.CosmeticType.ROD_TRAIL, rodTrail.getName(), true);
        }

        public void removeArrowTrail(ArrowTrail arrowtrail) {
            this.removeCosmetic(Cosmetics.CosmeticType.ARROW_TRAIL, arrowtrail.getName(), true);
        }

        public void removeMorph(Morph morph) {
            this.removeCosmetic(Cosmetics.CosmeticType.MORPH, morph.getName(), true);
        }
    }
}
