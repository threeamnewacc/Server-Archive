package us.zonix.hcfactions.kits;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.util.InventorySerialisation;
import us.zonix.hcfactions.util.ItemBuilder;
import us.zonix.hcfactions.FactionsPlugin;

import java.io.IOException;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;

public class Kit {

    private static FactionsPlugin main = FactionsPlugin.getInstance();
    private static Set<Kit> kits = new HashSet<>();

    @Getter private final String name;
    @Getter private final List<ItemStack> items;

    public Kit(String name) {
        this.name = name;
        this.items = new ArrayList<>();

        kits.add(this);
    }


    public void save() {
        MongoCollection collection = main.getFactionsDatabase().getKits();

        Document document = new Document();
        document.put("name", name);
        document.put("items", InventorySerialisation.itemStackArrayToJson(items.toArray(new ItemStack[items.size()])));

        collection.replaceOne(eq("name", name), document, new UpdateOptions().upsert(true));
    }

    public static void load() {
        MongoCollection collection = main.getFactionsDatabase().getKits();
        MongoCursor cursor = collection.find().iterator();
        while (cursor.hasNext()) {
            Document document = (Document) cursor.next();
            String name = document.getString("name");
            List<ItemStack> items;

            try {
                items = new ArrayList<>(Arrays.asList(InventorySerialisation.itemStackArrayFromJson(document.getString("items"))));
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            Kit kit = new Kit(name);
            kit.getItems().addAll(items);
        }
    }

    public void loadKit(Player player) {

        for(ItemStack item : this.getItems()) {

            if(item == null) {
                continue;
            }

            if(player.getInventory().firstEmpty() == -1) {
                player.getWorld().dropItem(player.getLocation(), item);
            } else {
                player.getInventory().addItem(item);
            }
        }


        player.updateInventory();
    }

    public void loadFullKit(Player player) {

        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[4]);

        for(ItemStack item : this.getItems()) {

            if(item == null) {
                continue;
            }

            if(InventorySerialisation.isHelmet(item)) {
                player.getInventory().setHelmet(item);
            }

            else if(InventorySerialisation.isChestplate(item)) {
                player.getInventory().setChestplate(item);
            }

            else if(InventorySerialisation.isLeggings(item)) {
                player.getInventory().setLeggings(item);
            }

            else if(InventorySerialisation.isBoots(item)) {
                player.getInventory().setBoots(item);
            }

           else {

                if(player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(item);
                }
            }
        }


        player.updateInventory();
    }

    public static Kit getByName(String name) {
        for (Kit kit : kits) {
            if (kit.getName().toLowerCase().equalsIgnoreCase(name.toLowerCase())) {
                return kit;
            }
        }
        return null;
    }

    public static Set<Kit> getKits() {
        return kits;
    }
}
