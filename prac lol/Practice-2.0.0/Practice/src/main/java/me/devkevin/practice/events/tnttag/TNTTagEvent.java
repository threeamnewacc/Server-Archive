package me.devkevin.practice.events.tnttag;

import club.inverted.chatcolor.CC;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.devkevin.landcore.LandCore;
import me.devkevin.practice.Practice;
import me.devkevin.practice.events.EventCountdownTask;
import me.devkevin.practice.events.PracticeEvent;
import me.devkevin.practice.location.CustomLocation;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.util.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Copyright 05/09/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class TNTTagEvent extends PracticeEvent<TNTTagPlayer> {

    private final Map<UUID, TNTTagPlayer> players = new HashMap<>();
    private final TNTTagCountdownTask countdownTask = new TNTTagCountdownTask(this);
    @Getter @Setter
    private TNTTagTask tntTagTask;

    public TNTTagEvent() {
        super("TNTTag");
    }

    @Override
    public  Map<UUID, TNTTagPlayer> getPlayers() {
        return players;
    }

    @Override
    public EventCountdownTask getCountdownTask() {
        return countdownTask;
    }

    @Override
    public List<CustomLocation> getSpawnLocations() {
        return Collections.singletonList(this.getPlugin().getCustomLocationManager().getTnttagLocation());
    }

    @Override
    public void onStart() {
        new TNTTagGameTask().runTaskTimer(Practice.getInstance(), 0,20L);
    }

    @Override
    public Consumer<Player> onJoin() {
        return player -> players.put(player.getUniqueId(), new TNTTagPlayer(player.getUniqueId(), this));
    }

    @Override
    public Consumer<Player> onDeath() {
        return player -> {

            TNTTagPlayer data = getPlayer(player);

            if(!player.isOnline()){
                return;
            }


            if(data.getState() == TNTTagPlayer.TNTTagState.TAGGED){
                data.setState(TNTTagPlayer.TNTTagState.ELIMINATED);
                PlayerUtil.reset(player);
                this.getPlugin().getProfileManager().giveSpawnItems(player);
                Practice.getInstance().getEventManager().addDeathSpectatorTntTag(player,
                        Practice.getInstance().getProfileManager().getProfileData(player.getUniqueId()), this);


                sendMessage(CC.GOLD + CC.BOLD + "(TnTTag) " + LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId()).getGrant().getRank().getColor() + player.getName() + CC.GREEN + " has been eliminated");
                player.sendMessage("");
                player.sendMessage(CC.RED + "You have been eliminated from the event. Better luck next time!");
                if (!player.hasPermission("practice.donors.gold")) {
                    player.sendMessage(CC.GRAY + "Purchase a rank at https://udrop.buycraft.net/ to host events of your own.");
                }
                player.sendMessage("");

                if (this.getByState(TNTTagPlayer.TNTTagState.IN_GAME).size() <= 5){
                    this.getByState(TNTTagPlayer.TNTTagState.IN_GAME).forEach(tntplayer -> {

                        Player other = Bukkit.getPlayer(tntplayer);

                        other.teleport(Practice.getInstance().getCustomLocationManager().getTnttagGameLocation().toBukkitLocation());
                    });

                    this.getByState(TNTTagPlayer.TNTTagState.TAGGED).forEach(tntplayer -> {

                        Player other = Bukkit.getPlayer(tntplayer);

                        other.teleport(Practice.getInstance().getCustomLocationManager().getTnttagGameLocation().toBukkitLocation());

                    });
                }
            }

            if (this.getByState(TNTTagPlayer.TNTTagState.IN_GAME).size() == 1 && this.getByState(TNTTagPlayer.TNTTagState.TAGGED).size() == 0) {
                Player winner = Bukkit.getPlayer(this.getByState(TNTTagPlayer.TNTTagState.IN_GAME).get(0));

                Profile winnerData = Practice.getInstance().getProfileManager().getProfileData(winner.getUniqueId());
                //winnerData.settn(winnerData.getSumoEventWins() + 1);

                handleWin(winner);
                end();
            }

        };
    }

    public List<UUID> getByState(TNTTagPlayer.TNTTagState state) {
        return players.values().stream().filter(player -> player.getState() == state).map(TNTTagPlayer::getUuid).collect(Collectors.toList());
    }

    public void explodePlayer(){

        getByState(TNTTagPlayer.TNTTagState.TAGGED).forEach(tntplayer -> {
            Player player = Bukkit.getPlayer(tntplayer);

            player.getWorld().createExplosion(player.getLocation(), 0.0F);

            onDeath().accept(player);
        });

        if (getByState(TNTTagPlayer.TNTTagState.IN_GAME).size()  >= 30){
            pickRandomPlayer();
            pickRandomPlayer();
            pickRandomPlayer();
            pickRandomPlayer();
            pickRandomPlayer();
        } else if(getByState(TNTTagPlayer.TNTTagState.IN_GAME).size() >= 20){
            pickRandomPlayer();
            pickRandomPlayer();
            pickRandomPlayer();
            pickRandomPlayer();
        } else if(getByState(TNTTagPlayer.TNTTagState.IN_GAME).size() >= 10){
            pickRandomPlayer();
            pickRandomPlayer();
            pickRandomPlayer();
        } else if(getByState(TNTTagPlayer.TNTTagState.IN_GAME).size() >= 5){
            pickRandomPlayer();
            pickRandomPlayer();
        } else {
            pickRandomPlayer();
        }
    }

    public void tagPlayer(Player player, Player attacker){

        if(getPlayer(player.getUniqueId()).getState() == TNTTagPlayer.TNTTagState.TAGGED){
            return;
        }

        getPlayer(player.getUniqueId()).setState(TNTTagPlayer.TNTTagState.TAGGED);
        getPlayer(attacker.getUniqueId()).setState(TNTTagPlayer.TNTTagState.IN_GAME);

        player.getInventory().setHelmet(new ItemStack(Material.TNT, 1));
        player.getInventory().setItem(0, new ItemStack(Material.TNT, 1));

        for (PotionEffect effect : attacker.getActivePotionEffects()) {
            attacker.removePotionEffect(effect.getType());
        }
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3));
        attacker.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));


        player.getInventory().setHelmet(new ItemStack(Material.TNT, 1));
        attacker.getInventory().setHelmet(new ItemStack(Material.AIR, 1));

        attacker.getInventory().setItem(0, new ItemStack(Material.AIR, 1));
        player.getInventory().setItem(0, new ItemStack(Material.TNT, 1));

        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 20f, 0.1f);

        sendMessage(CC.GOLD + CC.BOLD + "(TnTTag) " + LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId()).getGrant().getRank().getColor() + player.getName() + CC.GRAY + " is IT!");


        //FireworkEffect effect = FireworkEffect.builder().withColor(Color.RED).with(FireworkEffect.Type.CREEPER).build();
    }

    public Player pickRandomPlayer(){
        if (getByState(TNTTagPlayer.TNTTagState.IN_GAME).size() == 0) {
            return null;
        }

        List<UUID> waiting = getByState(TNTTagPlayer.TNTTagState.IN_GAME);

        Collections.shuffle(waiting);

        UUID uuid = waiting.get(ThreadLocalRandom.current().nextInt(waiting.size()));

        getPlayer(uuid).setState(TNTTagPlayer.TNTTagState.TAGGED);

        Player player = getPlugin().getServer().getPlayer(uuid);

        player.getInventory().setHelmet(new ItemStack(Material.TNT, 1));
        player.getInventory().setItem(0, new ItemStack(Material.TNT, 1));
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3));
        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 20f, 0.1f);
        sendMessage(CC.GOLD + CC.BOLD + "(TnTTag) " + LandCore.getInstance().getProfileManager().getProfile(player.getUniqueId()).getGrant().getRank().getColor() + player.getName() + CC.GRAY + " is IT!");

        return player;
    }

    /**
     * To ensure that the fight doesn't go on forever and to
     * let the players know how much time they have left.
     */
    @Getter
    @RequiredArgsConstructor
    public class TNTTagGameTask extends BukkitRunnable {

        private int time = 3;

        @Override
        public void run() {

            if (time == 3) {
                PlayerUtil.sendMessage(CC.D_RED + "3...", getBukkitPlayers());
            } else if (time == 2) {
                PlayerUtil.sendMessage(CC.RED + "2...", getBukkitPlayers());
            } else if (time == 1) {
                PlayerUtil.sendMessage(CC.YELLOW + "1...", getBukkitPlayers());
            } else if (time == 0) {
                PlayerUtil.sendMessage(CC.GREEN + "Go!", getBukkitPlayers());

                for (TNTTagPlayer player : getPlayers().values()) {
                    player.setState(TNTTagPlayer.TNTTagState.IN_GAME);
                    this.cancel();
                }

                getBukkitPlayers().forEach(player -> {
                    player.teleport(Practice.getInstance().getCustomLocationManager().getTnttagGameLocation().toBukkitLocation());
                    for (PotionEffect effect : player.getActivePotionEffects()) {
                        player.removePotionEffect(effect.getType());
                    }
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
                });

                if (getPlayers().size()  >= 30){
                    pickRandomPlayer();
                    pickRandomPlayer();
                    pickRandomPlayer();
                    pickRandomPlayer();
                    pickRandomPlayer();
                } else if(getPlayers().size() >= 20){
                    pickRandomPlayer();
                    pickRandomPlayer();
                    pickRandomPlayer();
                    pickRandomPlayer();
                } else if(getPlayers().size() >= 10){
                    pickRandomPlayer();
                    pickRandomPlayer();
                    pickRandomPlayer();
                } else if(getPlayers().size() >= 5){
                    pickRandomPlayer();
                    pickRandomPlayer();
                } else {
                    pickRandomPlayer();
                }
                tntTagTask = new TNTTagTask();
                tntTagTask.runTaskTimer(Practice.getInstance(), 0,20L);
                return;
            }
            time--;
        }
    }


    @Getter
    @RequiredArgsConstructor
    public class TNTTagTask extends BukkitRunnable {

        private int time = 30;

        @Override
        public void run() {

            if (time == 0){
                explodePlayer();
                if (getByState(TNTTagPlayer.TNTTagState.IN_GAME).size() == 1 && getByState(TNTTagPlayer.TNTTagState.TAGGED).size() == 0){
                    return;
                }
                time = 30;
            }
            time--;
        }
    }
}
