package net.badlion.uhc.listeners.gamemodes;

import net.badlion.gberry.utils.blocks.CraftMassBlockUpdate;
import net.badlion.gberry.utils.blocks.MassBlockUpdate;
import net.badlion.uhc.BadlionUHC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class WaterWorldGameMode implements GameMode {

    private Map<Location, Integer> materials = new HashMap<>();
    private Map<Location, Byte> datas = new HashMap<>();

    public WaterWorldGameMode() {
        World world = BadlionUHC.getInstance().getUHCWorld();
        MassBlockUpdate massBlockUpdate = new CraftMassBlockUpdate(world);
        massBlockUpdate.setRelightingStrategy(MassBlockUpdate.RelightingStrategy.IMMEDIATE);
        massBlockUpdate.setMaxRelightTimePerTick(2, TimeUnit.MILLISECONDS);
        for (int x = -100; x < 100; x++) {
            for (int z = -100; z < 100; z++) {
                Block highestBlock = world.getHighestBlockAt(x, z);
                for (int y = 3; y < 256 && y < highestBlock.getY(); y++) {
                    Location location = new Location(world, x, y, z);
                    Block block = location.getBlock();
                    this.materials.put(location, block.getTypeId());
                    this.datas.put(location, block.getData());

                    if (y <= 63) {
                        massBlockUpdate.setBlock(x, y, z, 8);
                    } else {
                        massBlockUpdate.setBlock(x, y, z, 0);
                    }
                }
            }
        }

        massBlockUpdate.notifyClients();
    }

    public ItemStack getExplanationItem() {
        ItemStack item = new ItemStack(Material.WATER);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Water World");

        List<String> lore = new ArrayList<>();

        lore.add(ChatColor.AQUA + "- 100x100 is an ocean");
        lore.add(ChatColor.AQUA + "- Bring your boats m8");

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public String getAuthor() {
        return "Badlion";
    }

    @Override
    public void unregister() {
        World world = BadlionUHC.getInstance().getUHCWorld();
        MassBlockUpdate massBlockUpdate = new CraftMassBlockUpdate(world);
        massBlockUpdate.setRelightingStrategy(MassBlockUpdate.RelightingStrategy.IMMEDIATE);
        massBlockUpdate.setMaxRelightTimePerTick(2, TimeUnit.MILLISECONDS);
        for (int x = -100; x < 100; x++) {
            for (int z = -100; z < 100; z++) {
                for (int y = 3; y < 256; y++) {
                    Location location = new Location(world, x, y, z);
                    Integer material = this.materials.get(location);
                    if (material == null) {
                        break;
                    }

                    byte data = this.datas.get(location);

                    massBlockUpdate.setBlock(x, y, z, material, data);
                }
            }
        }

        massBlockUpdate.notifyClients();
    }

}
