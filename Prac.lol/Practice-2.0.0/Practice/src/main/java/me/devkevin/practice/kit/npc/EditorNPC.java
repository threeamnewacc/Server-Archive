package me.devkevin.practice.kit.npc;

import lombok.Setter;
import me.devkevin.npc.events.NPCInteractEvent;
import me.devkevin.npc.npcs.NPC;
import me.devkevin.practice.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

/**
 * Copyright 11/02/2022 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public class EditorNPC implements Listener {

    // helmet
    private final ItemStack helmetDiamond = new ItemBuilder(Material.DIAMOND_HELMET).build();
    private final ItemStack helmetIron = new ItemBuilder(Material.IRON_HELMET).build();
    private final ItemStack helmetGold = new ItemBuilder(Material.GOLD_HELMET).build();
    private final ItemStack helmetChainMail = new ItemBuilder(Material.CHAINMAIL_HELMET).build();
    private final ItemStack helmetLeather = new ItemBuilder(Material.LEATHER_HELMET).build();

    // chest
    private final ItemStack chestDiamond = new ItemBuilder(Material.DIAMOND_CHESTPLATE).build();
    private final ItemStack chestIron = new ItemBuilder(Material.IRON_CHESTPLATE).build();
    private final ItemStack chestGold = new ItemBuilder(Material.GOLD_CHESTPLATE).build();
    private final ItemStack chestChainMail = new ItemBuilder(Material.CHAINMAIL_CHESTPLATE).build();
    private final ItemStack chestLeather = new ItemBuilder(Material.LEATHER_CHESTPLATE).build();

    // legs
    private final ItemStack legsDiamond = new ItemBuilder(Material.DIAMOND_LEGGINGS).build();
    private final ItemStack legsIron = new ItemBuilder(Material.IRON_LEGGINGS).build();
    private final ItemStack legsGold = new ItemBuilder(Material.GOLD_LEGGINGS).build();
    private final ItemStack legsChainMail = new ItemBuilder(Material.CHAINMAIL_LEGGINGS).build();
    private final ItemStack legsLeather = new ItemBuilder(Material.LEATHER_LEGGINGS).build();

    // boots
    private final ItemStack bootsDiamond = new ItemBuilder(Material.DIAMOND_BOOTS).build();
    private final ItemStack bootsIron = new ItemBuilder(Material.IRON_BOOTS).build();
    private final ItemStack bootsGold = new ItemBuilder(Material.GOLD_BOOTS).build();
    private final ItemStack bootsChainMail = new ItemBuilder(Material.CHAINMAIL_BOOTS).build();
    private final ItemStack bootsLeather = new ItemBuilder(Material.LEATHER_BOOTS).build();

    // for npc hand
    private final ItemStack diamondSword = new ItemBuilder(Material.DIAMOND_SWORD).build();
    private final ItemStack ironSword = new ItemBuilder(Material.IRON_SWORD).build();
    private final ItemStack goldSword = new ItemBuilder(Material.GOLD_SWORD).build();
    private final ItemStack stoneSword = new ItemBuilder(Material.STONE_SWORD).build();
    private final ItemStack woodSword = new ItemBuilder(Material.WOOD_SWORD).build();


    @Setter private int index = 0;

    @EventHandler
    public void onPlayerInteractWithNPC(NPCInteractEvent event) {
        Player player = event.getPlayer();
        NPC npc = event.getNpc();

        switch (npc.getName()) {
            case "Profile":
                break;
            case "Editor":
                if (event.getAction() == NPCInteractEvent.Action.RIGHT_CLICK) {
                    if (index == 1) {
                        //-------------------------------------------------------------\\
                        npc.destroy(player);
                        npc.setHelmet(helmetDiamond);
                        npc.setChest(chestDiamond);
                        npc.setLegs(legsDiamond);
                        npc.setBoots(bootsDiamond);
                        npc.setHand(diamondSword);
                        npc.spawn(player);
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
                        //-------------------------------------------------------------\\
                    }
                    if (index == 2) {
                        //-------------------------------------------------------------\\
                        npc.destroy(player);
                        npc.setHelmet(helmetIron);
                        npc.setChest(chestIron);
                        npc.setLegs(legsIron);
                        npc.setBoots(bootsIron);
                        npc.setHand(ironSword);
                        npc.spawn(player);
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
                        //-------------------------------------------------------------\\
                    }
                    if (index == 3) {
                        //-------------------------------------------------------------\\
                        npc.destroy(player);
                        npc.setHelmet(helmetGold);
                        npc.setChest(chestGold);
                        npc.setLegs(legsGold);
                        npc.setBoots(bootsGold);
                        npc.setHand(goldSword);
                        npc.spawn(player);
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
                        //-------------------------------------------------------------\\
                    }
                    if (index == 4) {
                        //-------------------------------------------------------------\\
                        npc.destroy(player);
                        npc.setHelmet(helmetChainMail);
                        npc.setChest(chestChainMail);
                        npc.setLegs(legsChainMail);
                        npc.setBoots(bootsChainMail);
                        npc.setHand(stoneSword);
                        npc.spawn(player);
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
                        //-------------------------------------------------------------\\
                    }
                    if (index == 5) {
                        //-------------------------------------------------------------\\
                        npc.destroy(player);
                        npc.setHelmet(helmetLeather);
                        npc.setChest(chestLeather);
                        npc.setLegs(legsLeather);
                        npc.setBoots(bootsLeather);
                        npc.setHand(woodSword);
                        npc.spawn(player);
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1F, 1F);
                        //-------------------------------------------------------------\\
                    }
                }
                // when the index is equals to 6 return to 1 number by number
                if (index == 5) {
                    setIndex(0);
                } else {
                    index++;
                }
                break;
        }
    }
}
