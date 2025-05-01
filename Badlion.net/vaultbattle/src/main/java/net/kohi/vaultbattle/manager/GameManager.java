package net.kohi.vaultbattle.manager;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.cosmetics.Cosmetics;
import net.badlion.gberry.Gberry;
import net.badlion.smellychat.managers.ChatSettingsManager;
import net.kohi.sidebar.SidebarAPI;
import net.kohi.sidebar.item.SidebarItem;
import net.kohi.vaultbattle.VaultBattlePlugin;
import net.kohi.vaultbattle.listener.PingListener;
import net.kohi.vaultbattle.menu.SpectatePlayersMenu;
import net.kohi.vaultbattle.menu.SpectateWarpsMenu;
import net.kohi.vaultbattle.type.*;
import net.kohi.vaultbattle.util.Soulbound;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Random;

public class GameManager {

    public static World gameMapWorld;
    private final VaultBattlePlugin plugin;
    private final SpectatePlayersMenu spectatePlayersMenu;
    private final SpectateWarpsMenu spectateWarpsMenu;
    private final SidebarItem ipSidebar = new SidebarItem(0) {
        private final String[] ips = new String[]{"na", "eu", "naw", "au"};
        private final String text = ChatColor.GOLD.toString() + ChatColor.BOLD + "IP: " + ChatColor.WHITE;

        @Override
        public String getText() {
            return text + ips[(int) Math.abs(System.currentTimeMillis() / 3000 % ips.length)] + ".badlion.net";
        }
    };
    private final SidebarItem serverSidebar = new SidebarItem(1) {
        private final String text = ChatColor.GOLD.toString() + ChatColor.BOLD + "Server: " + ChatColor.WHITE + Gberry.serverName.toUpperCase();

        @Override
        public String getText() {
            return text;
        }
    };
    private final SidebarItem spaceSidebar = new SidebarItem(7) {
        private final String text = ChatColor.GOLD.toString() + ChatColor.STRIKETHROUGH + "--------------";

        @Override
        public String getText() {
            return text;
        }
    };
    public int breakAmount = 1;
    private GameMap map;
    private BukkitTask countdown;
    private BukkitTask spawner;
    private Phase phase = Phase.PRE_START;
    private boolean wallsDropped = false;
    private String phaseText = ChatColor.GREEN.toString() + ChatColor.BOLD + "Pre-game";
    private final SidebarItem phaseSidebar = new SidebarItem(5) {
        @Override
        public String getText() {
            return phaseText;
        }
    };

    public GameManager(VaultBattlePlugin plugin) {
        this.plugin = plugin;
        spectatePlayersMenu = new SpectatePlayersMenu(plugin);
        spectateWarpsMenu = new SpectateWarpsMenu(plugin);
        startWaitingAlerts();
    }

    public Location getSpawn() {
        Random random = new Random();
        World world = plugin.getServer().getWorld("world");
        return new Location(world, random.nextDouble() * 10 - 5, 65, random.nextDouble() * 10 - 5);
    }


    public void checkForFullTeams() {
        for (Team team : plugin.getTeamManager().getTeams()) {
            if (team.size() < plugin.getStartingPlayers()) {
                return;
            }
        }
        preStart();
    }

    public void checkForWinner() {
        Team winner = null;
        for (Team team : plugin.getTeamManager().getTeams()) {
            if (!team.isEliminated()) {
                if (winner != null) {
                    return;
                } else {
                    winner = team;
                }
            }
        }
        if (winner != null) {
            for (int i = 0; i < 10; i++) {
                Bukkit.broadcastMessage(winner.getName() + " wins the game!!");
            }
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                ChatSettingsManager.getChatSettings(player).setActiveChannel("G");
                player.sendFormattedMessage("{0}You are now in global chat.", ChatColor.GREEN);
            }
            Bukkit.broadcastMessage(ChatColor.GOLD + "Server rebooting in 30 seconds.");
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player player : plugin.getServer().getOnlinePlayers()) {
                        player.kickPlayer("The game has ended.");
                    }
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            plugin.getServer().shutdown();
                        }
                    }.runTaskLater(plugin, 10);
                }
            }.runTaskLater(plugin, 20 * 30);
        }
    }

    public void startWaitingAlerts() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (phase != Phase.PRE_START || countdown != null) {
                    cancel();
                    return;
                }
                if (!Bukkit.getOnlinePlayers().isEmpty()) {
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "The game will start when there are at least " +
                            ChatColor.GOLD + plugin.getStartingPlayers() + " players" + ChatColor.YELLOW + " on each team.");
                }
                checkForFullTeams();
            }
        }.runTaskTimer(plugin, 20 * 20, 20 * 20);
    }

    public void preStart() {
        GameMap map = plugin.getGameMapManager().pickMap();
        if (map != null) {
            if (!map.isValid()) {
                Bukkit.broadcastMessage(ChatColor.RED + "There are no maps loaded on this server. Please contact a dev.");
                return;
            }
            World world = plugin.getGameMapManager().loadWorld(map);
            gameMapWorld = world;
            this.map = map;
            startCountdown(60);
        } else {
            Bukkit.broadcastMessage(ChatColor.RED + "There are no maps loaded on this server. Please contact a dev.");
        }
    }

    public void start() {
        // Disallow all cosmetics except arrow trail during actual play
        Cosmetics.getInstance().disallowCosmetics();
        Cosmetics.getInstance().setCosmeticEnabled(Cosmetics.CosmeticType.ARROW_TRAIL, true);

        for (Team team : plugin.getTeamManager().getTeams()) {
            for (int i = 0; i < 5; i++) {
                team.setBank(team.getBank() + 1);
                Region region = plugin.getGameManager().getMap().getBanks().get(team.getColor());
                Location location = region.getBlock(team.getBankRounded());
                location.setWorld(GameManager.gameMapWorld);
                location.getBlock().setType(Material.EMERALD_BLOCK);
            }
        }
        setPhase(Phase.STARTED);
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerData data = plugin.getPlayerDataManager().get(player);
            Team team = data.getTeam();
            if (team != null) {
                for (Team allTeams : plugin.getTeamManager().getTeams()) {
                    SidebarAPI.removeSidebarItem(player, allTeams.getPlayerCountSidebar());
                    SidebarAPI.addSidebarItem(player, allTeams.getBankSidebar());
                }
                for (PotionEffect effect : player.getActivePotionEffects()) {
                    player.removePotionEffect(effect.getType());
                }
                ChatSettingsManager.getChatSettings(player).setActiveChannel("T");
                Location spawnPoint = getMap().getTeamSpawns().get(team.getColor()).toLocation(gameMapWorld);
                player.setGameMode(GameMode.SURVIVAL);
                player.teleport(new Location(gameMapWorld, spawnPoint.getBlockX(), spawnPoint.getBlockY(), spawnPoint.getBlockZ(), spawnPoint.getYaw(), spawnPoint.getPitch()));
                player.setHealth(20.0);
                player.setFoodLevel(20);
                player.setSaturation(10);
                player.getInventory().clear();
                player.setLevel(0);
                player.getInventory().addItem(Soulbound.makeSoulbound(new ItemStack(Material.IRON_PICKAXE)));
                player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 15));
                ItemStack heathPotion = Soulbound.makeSoulbound(new ItemStack(Material.POTION));
                heathPotion.setDurability((short) 16421);
                for (int i = 0; i < 3; i++) {
                    player.getInventory().addItem(heathPotion);
                }
                player.getInventory().addItem(new ItemStack(Material.WORKBENCH));
                player.getInventory().addItem(new ItemStack(Material.FURNACE));
                player.getInventory().addItem(new ItemStack(Material.NETHER_STALK, 10));
                player.getInventory().addItem(new ItemStack(Material.SOUL_SAND, 5));

                ItemStack helm = Soulbound.makeSoulbound(new ItemStack(Material.LEATHER_HELMET));
                LeatherArmorMeta armorHelmMeta = (LeatherArmorMeta) helm.getItemMeta();
                armorHelmMeta.setColor(team.getColor().toColor());
                helm.setItemMeta(armorHelmMeta);
                player.getInventory().setHelmet(helm);

                ItemStack chest = Soulbound.makeSoulbound(new ItemStack(Material.LEATHER_CHESTPLATE));
                LeatherArmorMeta armorChestMeta = (LeatherArmorMeta) chest.getItemMeta();
                armorChestMeta.setColor(team.getColor().toColor());
                chest.setItemMeta(armorChestMeta);
                player.getInventory().setChestplate(chest);

                ItemStack legs = Soulbound.makeSoulbound(new ItemStack(Material.LEATHER_LEGGINGS));
                LeatherArmorMeta armorLegsMeta = (LeatherArmorMeta) legs.getItemMeta();
                armorLegsMeta.setColor(team.getColor().toColor());
                legs.setItemMeta(armorLegsMeta);
                player.getInventory().setLeggings(legs);

                ItemStack boots = Soulbound.makeSoulbound(new ItemStack(Material.LEATHER_BOOTS));
                LeatherArmorMeta armorBootMeta = (LeatherArmorMeta) boots.getItemMeta();
                armorBootMeta.setColor(team.getColor().toColor());
                boots.setItemMeta(armorBootMeta);
                player.getInventory().setBoots(boots);

                player.updateInventory();
                player.setExhaustion(0);
                player.setFireTicks(0);
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 60 * 5, 1, true)); // ambient haste 1 for 4 min
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 3, true)); // resistance to avoid any nasty tp damage
            } else {
                data.setPickingTeam(true);
            }
        }
        spawner = new SpawnerSpawn().runTaskTimer(plugin, 20, 20 * 10);
        countdown = new BukkitRunnable() {
            int time = map.getWallsBreakTime(); // 3 minutes

            @Override
            public void run() {
                time--;
                phaseText = ChatColor.AQUA.toString() + ChatColor.BOLD + "Walls Drop: " + ChatColor.RESET + String.format("%d:%02d", time / 60, time % 60);
                if (time % 60 == 0 && time != 0) {
                    int minutes = time / 60;
                    plugin.getServer().broadcastMessage(ChatColor.RED + "You have " + ChatColor.GOLD + minutes + ChatColor.RED + (minutes == 1 ? " minute" : " minutes") + " until the walls drop!");
                    for (Player player : plugin.getServer().getOnlinePlayers()) {
                        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "WOOD_CLICK", "BLOCK_WOOD_BUTTON_CLICK_ON"), 1.0F, 1.0F);
                    }
                }
                if (time <= 20 && time != 0) {
                    plugin.getServer().broadcastMessage(ChatColor.RED + "Walls drop in " + ChatColor.GOLD + time + ChatColor.RED + (time == 1 ? " second!" : " seconds!"));
                    for (Player player : plugin.getServer().getOnlinePlayers()) {
                        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "WOOD_CLICK", "BLOCK_WOOD_BUTTON_CLICK_ON"), 1.0F, 1.0F);
                    }
                }
                if (time == 0) {
                    countdown.cancel();
                    plugin.getServer().broadcastMessage(ChatColor.RED + "The walls have broken!");
                    for (Player player : plugin.getServer().getOnlinePlayers()) {
                        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "NOTE_PLING", "BLOCK_NOTE_PLING"), 1.0F, 1.0F);
                    }
                    dropWalls();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public void dropWalls() {
        wallsDropped = true;
        for (Region region : map.getWalls()) {
            region.getContainedBlocks().stream().filter(location -> location.getBlock().getType().equals(Material.BEDROCK)).forEach(location -> {
                location.getBlock().setType(Material.AIR);
            });
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "BLAZE_DEATH", "ENTITY_BLAZE_DEATH"), 1.0F, 1.0F);
            PlayerData playerData = plugin.getPlayerDataManager().get(player);
            if (playerData.isPickingTeam()) {
                player.getInventory().clear();
                player.getInventory().setArmorContents(null);
                plugin.getPlayerDataManager().teleportToSpawn(player);
                plugin.getPlayerDataManager().makeSpectator(player);
                for (Team team : plugin.getTeamManager().getTeams()) {
                    SidebarAPI.removeSidebarItem(player, team.getPlayerCountSidebar());
                    SidebarAPI.addSidebarItem(player, team.getBankSidebar());
                }
            }
        }
        countdown = new BukkitRunnable() {
            int time = map.getWallsBreakTime() / 2;

            @Override
            public void run() {
                time--;
                phaseText = ChatColor.AQUA.toString() + ChatColor.BOLD + "2x Damage: " + ChatColor.RESET + String.format("%d:%02d", time / 60, time % 60);
                if (time % 60 == 0 && time != 0) {
                    int minutes = time / 60;
                    plugin.getServer().broadcastMessage(ChatColor.RED + "You have " + ChatColor.GOLD + minutes + ChatColor.RED + (minutes == 1 ? " minute" : " minutes") + " until 2x Bank Damage!");
                    for (Player player : plugin.getServer().getOnlinePlayers()) {
                        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "WOOD_CLICK", "BLOCK_WOOD_BUTTON_CLICK_ON"), 1.0F, 1.0F);
                    }
                }
                if (time <= 20 && time != 0) {
                    plugin.getServer().broadcastMessage(ChatColor.RED + "2x Bank Damage in " + ChatColor.GOLD + time + ChatColor.RED + (time == 1 ? " second!" : " seconds!"));
                    for (Player player : plugin.getServer().getOnlinePlayers()) {
                        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "WOOD_CLICK", "BLOCK_WOOD_BUTTON_CLICK_ON"), 1.0F, 1.0F);
                    }
                }
                if (time == 0) {
                    countdown.cancel();
                    startDoubleBreaks();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public void startDoubleBreaks() {
        breakAmount = 2;
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Banks now take 2x damage per hit!");
        Bukkit.broadcastMessage("");
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "BLAZE_DEATH", "ENTITY_BLAZE_DEATH"), 1.0F, 1.0F);
        }
        countdown = new BukkitRunnable() {
            int time = map.getWallsBreakTime() / 2;

            @Override
            public void run() {
                time--;
                phaseText = ChatColor.AQUA.toString() + ChatColor.BOLD + "3x Damage: " + ChatColor.RESET + String.format("%d:%02d", time / 60, time % 60);
                if (time % 60 == 0 && time != 0) {
                    int minutes = time / 60;
                    plugin.getServer().broadcastMessage(ChatColor.RED + "You have " + ChatColor.GOLD + minutes + ChatColor.RED + (minutes == 1 ? " minute" : " minutes") + " until 3x Bank Damage!");
                    for (Player player : plugin.getServer().getOnlinePlayers()) {
                        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "WOOD_CLICK", "BLOCK_WOOD_BUTTON_CLICK_ON"), 1.0F, 1.0F);
                    }
                }
                if (time <= 20 && time != 0) {
                    plugin.getServer().broadcastMessage(ChatColor.RED + "3x Bank Damage in " + ChatColor.GOLD + time + ChatColor.RED + (time == 1 ? " second!" : " seconds!"));
                    for (Player player : plugin.getServer().getOnlinePlayers()) {
                        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "WOOD_CLICK", "BLOCK_WOOD_BUTTON_CLICK_ON"), 1.0F, 1.0F);
                    }
                }
                if (time == 0) {
                    countdown.cancel();
                    startTripleBreaks();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public void startTripleBreaks() {
        breakAmount = 3;
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Banks now take 3x damage per hit!");
        Bukkit.broadcastMessage("");
        for (Player player : Bukkit.getOnlinePlayers()) {
            SidebarAPI.removeSidebarItem(player, phaseSidebar);
            player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "BLAZE_DEATH", "ENTITY_BLAZE_DEATH"), 1.0F, 1.0F);
        }
    }


    public void startCountdown(final int seconds) {
        if (countdown != null || phase != Phase.PRE_START) {
            return;
        }
        countdown = new StartGameTask(seconds).runTaskTimer(plugin, 20, 20);
    }

    public void launchRandomFirework(Location loc) {
        Firework firework = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta fireworkMeta = (FireworkMeta) Bukkit.getItemFactory().getItemMeta(Material.FIREWORK);
        fireworkMeta.setPower(2);
        Random random = new Random();
        FireworkEffect.Builder builder = FireworkEffect.builder();
        FireworkEffect.Type type = FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)];
        builder.with(type);
        for (int i = 0; i < 4; i++) {
            builder.withColor(Color.fromRGB(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        }
        builder.withTrail().withFlicker();
        fireworkMeta.addEffect(builder.build());
        firework.setFireworkMeta(fireworkMeta);
    }

    public VaultBattlePlugin getPlugin() {
        return this.plugin;
    }

    public SpectatePlayersMenu getSpectatePlayersMenu() {
        return this.spectatePlayersMenu;
    }

    public SidebarItem getServerSidebar() {
        return this.serverSidebar;
    }

    public SidebarItem getSpaceSidebar() {
        return this.spaceSidebar;
    }

    public int getBreakAmount() {
        return this.breakAmount;
    }

    public GameMap getMap() {
        return this.map;
    }

    public BukkitTask getCountdown() {
        return this.countdown;
    }

    public BukkitTask getSpawner() {
        return this.spawner;
    }

    public Phase getPhase() {
        return this.phase;
    }

    public boolean isWallsDropped() {
        return this.wallsDropped;
    }

    public String getPhaseText() {
        return this.phaseText;
    }

    public SidebarItem getPhaseSidebar() {
        return this.phaseSidebar;
    }

    public void setBreakAmount(int breakAmount) {
        this.breakAmount = breakAmount;
    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    public void setCountdown(BukkitTask countdown) {
        this.countdown = countdown;
    }

    public void setSpawner(BukkitTask spawner) {
        this.spawner = spawner;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public void setWallsDropped(boolean wallsDropped) {
        this.wallsDropped = wallsDropped;
    }

    public void setPhaseText(String phaseText) {
        this.phaseText = phaseText;
    }

    public SidebarItem getIpSidebar() {
        return ipSidebar;
    }

    public SpectateWarpsMenu getSpectateWarpsMenu() {
        return spectateWarpsMenu;
    }

    public class StartGameTask extends BukkitRunnable {

        int count;

        public StartGameTask(int count) {
            this.count = count;
        }

        @Override
        public void run() {
            if (count == 0) {
                start();
                for (Player player : plugin.getServer().getOnlinePlayers()) {
                    player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "FIREWORK_LARGE_BLAST", "ENTITY_FIREWORK_LARGE_BLAST"), 1.0F, 1.0F);
                }
                cancel();
            } else {
                PingListener.setCountdown(count);
                if (count < 20 || count % 5 == 0) {
                    plugin.getServer().broadcastMessage(ChatColor.GREEN + "Game starting in " + ChatColor.YELLOW + count + ChatColor.GREEN + " seconds");
                    plugin.getServer().getWorlds().get(0).setTime(0);
                    for (Player player : plugin.getServer().getOnlinePlayers()) {
                        player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "CLICK", "UI_BUTTON_CLICK"), 1.0F, 1.0F);
                    }
                }
                count--;
            }
        }

        public int getCount() {
            return this.count;
        }
    }


    private class SpawnerSpawn extends BukkitRunnable {

        @Override
        public void run() {
            spectatePlayersMenu.update();
            if (gameMapWorld == null) {
                return;
            }
            for (Chunk chunk : gameMapWorld.getLoadedChunks()) {
                for (BlockState blockState : chunk.getTileEntities()) {
                    if (blockState instanceof CreatureSpawner) {
                        CreatureSpawner spawner = (CreatureSpawner) blockState;
                        spawner.setDelay(1);
                    }
                }
            }
        }
    }
}
