package net.kohi.vaultbattle.manager;

import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.smellychat.managers.ChatSettingsManager;
import net.kohi.sidebar.SidebarAPI;
import net.kohi.vaultbattle.VaultBattlePlugin;
import net.kohi.vaultbattle.type.Phase;
import net.kohi.vaultbattle.type.PlayerData;
import net.kohi.vaultbattle.type.Team;
import net.kohi.vaultbattle.type.TeamColor;
import net.kohi.vaultbattle.util.Soulbound;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class TeamManager {

    private final VaultBattlePlugin plugin;

    private ItemStack leaveItem = ItemStackUtil.createItem(
            Material.CLAY_BRICK,
            ChatColor.RED + "Leave Team");

    private ItemStack randomTeamItem = ItemStackUtil.createItem(
            Material.DIAMOND,
            ChatColor.GOLD + "Join a Random Team");

    private List<Team> teams = new ArrayList<>();

    private Map<Material, Double> convertValues = new HashMap<>();

    public TeamManager(VaultBattlePlugin plugin) {
        this.plugin = plugin;
    }


    public Team getTeam(TeamColor color) {
        for (Team team : teams) {
            if (team.getColor().equals(color)) {
                return team;
            }
        }
        return null;
    }

    public int averageTeamSize() {
        int average = 0;
        for (Team team : teams) {
            average += team.size();
        }
        return average / teams.size();
    }

    private int smallestTeamSize() {
        Team small = null;
        for (Team team : teams) {
            if (small == null) {
                small = team;
            } else if (small.size() > team.size()) {
                small = team;
            }
        }
        return small.size();
    }

    public void joinRandom(Player player) {
        Random random = new Random();
        Team team = teams.get(random.nextInt(teams.size()));
        joinTeam(team, player);
    }

    public void joinSmallest(Player player) {
        Team small = null;
        for (Team team : teams) {
            if (small == null) {
                small = team;
            } else if (small.size() > team.size()) {
                small = team;
            }
        }
        joinTeam(small, player);
    }

    public void joinTeam(Team team, Player player) {
        if (team.getPlayers().contains(player.getUniqueId())) {
            player.sendFormattedMessage("{0}You are already are in team {1}", ChatColor.RED, team.getName());
            return;
        }
        // only allow teams to have 3 more than the lowest team
        // TODO: configurable if we need
        if (team.size() >= smallestTeamSize() + 3) {
            player.sendFormattedMessage("{0}The {1} team is too full. Join another team or wait for teams to balance out.", ChatColor.RED, team.getName() + ChatColor.RED);
            return;
        }
        removeFromAllTeams(player);
        PlayerData playerData = plugin.getPlayerDataManager().get(player);
        team.broadcastMessage(player.getDisplayName() + ChatColor.GREEN + " joined your team.");
        team.getPlayers().add(player.getUniqueId());
        team.getScoreBoardTeam().addEntry(player.getName());
        playerData.setTeam(team);
        player.sendMessage("");
        player.sendFormattedMessage("{0}You joined the {1} team!", ChatColor.GREEN, team.getName() + ChatColor.GREEN);
        player.sendMessage("");
        if (plugin.getGameManager().getPhase().equals(Phase.PRE_START)) {
            player.getInventory().clear();
            player.getInventory().addItem(leaveItem);
            player.updateInventory();
            plugin.getGameManager().checkForFullTeams();
        } else if (!plugin.getGameManager().isWallsDropped() && plugin.getGameManager().getPhase().equals(Phase.STARTED)) {
            for (Team sideBarTeam : teams) {
                SidebarAPI.removeSidebarItem(player, sideBarTeam.getPlayerCountSidebar());
                SidebarAPI.addSidebarItem(player, sideBarTeam.getBankSidebar());
            }
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }
            ChatSettingsManager.getChatSettings(player).setActiveChannel("T");
            playerData.setPickingTeam(false);
            Location spawnPoint = plugin.getGameManager().getMap().getTeamSpawns().get(team.getColor()).toLocation(GameManager.gameMapWorld);
            player.setGameMode(GameMode.SURVIVAL);
            player.teleport(new Location(GameManager.gameMapWorld, spawnPoint.getBlockX(), spawnPoint.getBlockY(), spawnPoint.getBlockZ(), spawnPoint.getYaw(), spawnPoint.getPitch()));
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
        }
    }

    public void giveKit(Player player) {
        PlayerData playerData = plugin.getPlayerDataManager().get(player);
        Team team = playerData.getTeam();
        if (team != null) {
            player.getInventory().addItem(Soulbound.makeSoulbound(new ItemStack(Material.IRON_PICKAXE)));
            player.getInventory().addItem(Soulbound.makeSoulbound(new ItemStack(Material.COOKED_BEEF, 3)));
            ItemStack heathPotion = Soulbound.makeSoulbound(new ItemStack(Material.POTION));
            heathPotion.setDurability((short) 16421);
            player.getInventory().addItem(heathPotion);
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
        }
    }

    public boolean removeFromAllTeams(Player player) {
        boolean removed = false;
        plugin.getPlayerDataManager().get(player).setTeam(null);
        for (Team team : teams) {
            team.getPlayers().remove(player.getUniqueId());
            if (team.getScoreBoardTeam().hasEntry(player.getName())) {
                team.getScoreBoardTeam().removeEntry(player.getName());
                removed = true;
            }
        }
        return removed;
    }

    public void load() {
        convertValues.put(Material.EMERALD, 1.0 / 9.0);
        convertValues.put(Material.EMERALD_BLOCK, 1.0);
        convertValues.put(Material.DIAMOND, 1.0 / 5.0);
        convertValues.put(Material.DIAMOND_BLOCK, 9.0 / 5.0);
        convertValues.put(Material.IRON_INGOT, 1.0 / 12.0);
        convertValues.put(Material.IRON_BLOCK, 9.0 / 12.0);
        convertValues.put(Material.REDSTONE, 1.0 / 14.0);
        convertValues.put(Material.REDSTONE_BLOCK, 9.0 / 14.0);
        convertValues.put(Material.GOLD_INGOT, 1.0 / 9.0);
        convertValues.put(Material.GOLD_BLOCK, 1.0);

        Team red = new Team();
        red.setColor(TeamColor.RED);
        red.setName(ChatColor.RED + "Red");
        ItemStack redItem = ItemStackUtil.createItem(
                Material.INK_SACK,
                (short) 1,
                ChatColor.RED + "Join Red Team");
        red.setJoinItem(redItem);
        teams.add(red);
        org.bukkit.scoreboard.Team redScoreTeam = plugin.getScoreboard().registerNewTeam("redTeam");
        redScoreTeam.setPrefix(ChatColor.RED.toString());
        red.setScoreBoardTeam(redScoreTeam);

        Team blue = new Team();
        blue.setColor(TeamColor.BLUE);
        blue.setName(ChatColor.BLUE + "Blue");
        ItemStack blueItem = ItemStackUtil.createItem(
                Material.INK_SACK,
                (short) 4,
                ChatColor.BLUE + "Join Blue Team");
        blue.setJoinItem(blueItem);
        teams.add(blue);
        org.bukkit.scoreboard.Team blueScoreTeam = plugin.getScoreboard().registerNewTeam("blueTeam");
        blueScoreTeam.setPrefix(ChatColor.BLUE.toString());
        blue.setScoreBoardTeam(blueScoreTeam);

        Team yellow = new Team();
        yellow.setColor(TeamColor.YELLOW);
        yellow.setName(ChatColor.YELLOW + "Yellow");
        ItemStack yellowItem = ItemStackUtil.createItem(
                Material.INK_SACK,
                (short) 11,
                ChatColor.YELLOW + "Join Yellow Team");
        yellow.setJoinItem(yellowItem);
        teams.add(yellow);
        org.bukkit.scoreboard.Team yellowScoreTeam = plugin.getScoreboard().registerNewTeam("yellowTeam");
        yellowScoreTeam.setPrefix(ChatColor.YELLOW.toString());
        yellow.setScoreBoardTeam(yellowScoreTeam);

        Team green = new Team();
        green.setColor(TeamColor.GREEN);
        green.setName(ChatColor.GREEN + "Green");
        ItemStack greenItem = ItemStackUtil.createItem(
                Material.INK_SACK,
                (short) 10,
                ChatColor.GREEN + "Join Green Team");
        green.setJoinItem(greenItem);
        teams.add(green);
        org.bukkit.scoreboard.Team greenScoreTeam = plugin.getScoreboard().registerNewTeam("greenTeam");
        greenScoreTeam.setPrefix(ChatColor.GREEN.toString());
        green.setScoreBoardTeam(greenScoreTeam);
    }

    public ItemStack getLeaveItem() {
        return this.leaveItem;
    }

    public ItemStack getRandomTeamItem() {
        return this.randomTeamItem;
    }

    public List<Team> getTeams() {
        return this.teams;
    }

    public Map<Material, Double> getConvertValues() {
        return this.convertValues;
    }
}
