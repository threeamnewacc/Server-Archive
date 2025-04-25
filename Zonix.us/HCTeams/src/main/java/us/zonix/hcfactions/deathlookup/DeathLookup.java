package us.zonix.hcfactions.deathlookup;

import org.apache.commons.lang.WordUtils;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.fight.killer.type.ProfileFightPlayerKiller;
import us.zonix.hcfactions.util.InventorySerialisation;
import us.zonix.hcfactions.util.NumberUtil;
import us.zonix.hcfactions.util.ItemBuilder;
import us.zonix.hcfactions.util.LocationSerialization;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import us.zonix.hcfactions.profile.fight.ProfileFight;
import us.zonix.hcfactions.profile.fight.ProfileFightEffect;
import us.zonix.hcfactions.profile.fight.ProfileFightEnvironment;
import us.zonix.hcfactions.profile.fight.killer.ProfileFightKiller;
import us.zonix.hcfactions.profile.fight.killer.type.ProfileFightEnvironmentKiller;
import com.mongodb.client.MongoCursor;
import lombok.Getter;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import us.zonix.hcfactions.FactionsPlugin;
import us.zonix.hcfactions.profile.Profile;
import us.zonix.hcfactions.profile.fight.ProfileFight;
import us.zonix.hcfactions.profile.fight.ProfileFightEnvironment;
import us.zonix.hcfactions.profile.fight.killer.type.ProfileFightPlayerKiller;
import us.zonix.hcfactions.util.ItemBuilder;
import us.zonix.hcfactions.util.LocationSerialization;
import us.zonix.hcfactions.util.NumberUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.descending;

/*
    This is in beta, not going to be making it configurable for quite some time.
 */
public class DeathLookup {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
    private static final int PAGE_SIZE = 9;
    private static final String INVENTORY_TITLE = ChatColor.RED + "Deaths - %PAGE%/%TOTAL%";
    private static FactionsPlugin main = FactionsPlugin.getInstance();

    @Getter private final Profile profile;
    @Getter private final DeathLookupData data;

    public DeathLookup(Profile profile) {
        this.profile = profile;
        this.data = new DeathLookupData();
    }

    public Inventory getDeathInventory(int page) {

        int total = (int) Math.ceil(getDeathsCount() / 9.0);
        if (total == 0) {
            total = 1;
        }

        Inventory toReturn = Bukkit.createInventory(null, 18, INVENTORY_TITLE.replace("%PLAYER%", profile.getName()).replace("%PAGE%", page + "").replace("%TOTAL%", total + ""));

        toReturn.setItem(0, new ItemBuilder(Material.CARPET).durability(7).name(ChatColor.RED + "Previous Page").build());
        toReturn.setItem(8, new ItemBuilder(Material.CARPET).durability(7).name(ChatColor.RED + "Next Page").build());
        toReturn.setItem(4, new ItemBuilder(Material.PAPER).name(ChatColor.RED + "Page " + page + "/" + total).lore(Arrays.asList(ChatColor.YELLOW + "Player: " + ChatColor.RED + profile.getName())).build());

        int count = 0;
        for (ProfileFight death : getDeaths(page)) {
            ItemBuilder builder = new ItemBuilder(Material.SKULL_ITEM).name(ChatColor.YELLOW + DATE_FORMAT.format(new Date(death.getOccurredAt())));

            List<String> lore = new ArrayList<>();
            lore.addAll(Arrays.asList(
                    "&7&m------------------------------",
                    "&eKilled by: &c" + (death.getKiller().getName() == null ? "Environment (" + WordUtils.capitalize(((ProfileFightEnvironmentKiller) death.getKiller()).getType().name().toLowerCase()) + ")" : death.getKiller().getName())
            ));

            if (!(death.getEffects().isEmpty())) {
                lore.add("&7&m------------------------------");
                lore.add("&cEffects:");
                for (ProfileFightEffect effect : death.getEffects()) {
                    String name = WordUtils.capitalize(effect.getType().getName().replace("_", " ").toLowerCase());
                    lore.add(" &e" + name + " " + NumberUtil.toRomanNumeral(effect.getLevel()) + "&c for &e" + DurationFormatUtils.formatDuration(effect.getDuration(), "mm:ss") + "m");
                }
            }

            lore.add("&7&m------------------------------");

            builder.lore(lore);

            toReturn.setItem(PAGE_SIZE + count, builder.build());
            count++;
        }

        return toReturn;
    }

    public int getTotalPages() {
        int total = (int) Math.ceil(getDeathsCount() / 9.0);
        if (total == 0) {
            total = 1;
        }
        return total;
    }

    public long getDeathsCount() {
        return main.getFactionsDatabase().getFights().count(eq("killed", profile.getUuid().toString())) + profile.getFights().size();
    }

    public List<ProfileFight> getDeaths(int page) {
        List<ProfileFight> toReturn = new ArrayList<>();

        int toLimit = 0;
        for (ProfileFight fight : profile.getFights()) {
            if (fight.getKiller() instanceof ProfileFightPlayerKiller && ((ProfileFightPlayerKiller) fight.getKiller()).getUuid().equals(profile.getUuid())) {
                continue;
            }
            toReturn.add(fight);
            toLimit++;
        }

        MongoCursor cursor = main.getFactionsDatabase().getFights().find(eq("killed", profile.getUuid().toString())).skip((page - 1) * PAGE_SIZE).limit(PAGE_SIZE - toLimit).sort(descending("occurred_at")).iterator();
        while (cursor.hasNext()) {
            Document document = (Document) cursor.next();
            List<ProfileFightEffect> effects = new ArrayList<>();

            for (JsonElement effectElement : (JsonArray) new JsonParser().parse(document.getString("effects"))) {
                JsonObject effectObject = (JsonObject) effectElement;
                effects.add(new ProfileFightEffect(new PotionEffect(PotionEffectType.getByName(effectObject.get("type").getAsString()), effectObject.get("duration").getAsInt() / 1000 * 20, effectObject.get("level").getAsInt() - 1)));
            }

            try {
                Document killerDocument = (Document) document.get("killer");

                ProfileFightKiller killer;
                if (killerDocument.get("type").equals("PLAYER")) {
                    List<ProfileFightEffect> killerEffects = new ArrayList<>();

                    for (JsonElement effectElement : (JsonArray) new JsonParser().parse(killerDocument.getString("effects"))) {
                        JsonObject effectObject = (JsonObject) effectElement;
                        killerEffects.add(new ProfileFightEffect(new PotionEffect(PotionEffectType.getByName(effectObject.get("type").getAsString()), effectObject.get("duration").getAsInt() / 1000 * 20, effectObject.get("level").getAsInt() - 1)));
                    }

                    killer = new ProfileFightPlayerKiller(killerDocument.getString("name"), UUID.fromString(killerDocument.getString("uuid")), killerDocument.getInteger("ping"), InventorySerialisation.itemStackArrayFromJson(killerDocument.getString("contents")), InventorySerialisation.itemStackArrayFromJson(killerDocument.getString("armor")), killerDocument.getDouble("health"), killerDocument.getDouble("hunger"), killerEffects);
                } else if (killerDocument.get("type").equals("MOB")){
                    killer = new ProfileFightKiller(EntityType.valueOf(killerDocument.getString("mob_type")), killerDocument.getString("name"));
                } else {
                    killer = new ProfileFightEnvironmentKiller(ProfileFightEnvironment.valueOf(killerDocument.getString("type")));
                }

                toReturn.add(new ProfileFight(UUID.fromString(document.getString("uuid")), document.getInteger("ping"), document.getLong("occurred_at"), InventorySerialisation.itemStackArrayFromJson(document.getString("contents")), InventorySerialisation.itemStackArrayFromJson(document.getString("armor")), document.getDouble("hunger"), effects, killer, LocationSerialization.deserializeLocation(document.getString("location"))));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return toReturn;
    }

    public Inventory getFightItemInventory(ProfileFight fight) {
        int deathNumber = (int) (getDeathsCount() - (((getData().getPage() - 1) * 9) + getData().getIndex()));
        Inventory toReturn = Bukkit.createInventory(null, 9 * 6, ChatColor.RED + "Inventory #" + deathNumber);

        toReturn.setItem(0, new ItemBuilder(Material.CARPET).durability(7).name(ChatColor.RED + "Return").build());
        toReturn.setItem(8, new ItemBuilder(Material.CARPET).durability(7).name(ChatColor.RED + "Return").build());
        toReturn.setItem(4, new ItemBuilder(Material.PAPER).name(ChatColor.RED + "Inventory Contents").lore(Arrays.asList(ChatColor.YELLOW + "Player: " + ChatColor.RED + profile.getName())).build());

        List<ItemStack> contents = new ArrayList<>(Arrays.asList(fight.getContents()));
        List<ItemStack> armor = new ArrayList<>(Arrays.asList(fight.getArmor()));

        for (int i = 0; i < contents.size(); i++) {
            if (i <= 8) {
                ItemStack item = contents.get(i);
                if (item != null) {
                    toReturn.setItem(i + 9, item);
                }
            }
        }

        for (int i = 0; i < contents.size(); i++) {
            if (i > 8) {
                ItemStack item = contents.get(i);
                if (item != null) {
                    int position = i;

                    if (position <= 17) {
                        position += 27;
                    } else if (position > 17 && position < 27) {
                        position += 9;
                    } else if (position >= 27) {
                        position -= 18;
                    }

                    while (toReturn.getItem(position) != null) {
                        position++;
                        if (position == toReturn.getSize()) break;
                    }

                    if (position != toReturn.getSize()) {
                        toReturn.setItem(position, item);
                    }
                }
            }
        }

        for (int i = 0; i < 2; i++) {
            toReturn.setItem(49 + i, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(14).name(" ").build());
        }

        for (int i = 0; i < armor.size(); i++) {
            ItemStack item = armor.get(i);
            System.out.println(item);
            if (item != null && item.getType() != Material.AIR) {
                toReturn.setItem(45 + i, item);
            } else {
                toReturn.setItem(45 + i, new ItemBuilder(Material.STAINED_GLASS_PANE).durability(14).name(" ").build());
            }
        }

        List<String> lore = new ArrayList<>();
        if (!fight.getEffects().isEmpty()) {
            lore.add("&7&m------------------------------");
            for (ProfileFightEffect effect : fight.getEffects()) {
                String name = WordUtils.capitalize(effect.getType().getName().replace("_", " ").toLowerCase());
                lore.add("&e" + name + " " + NumberUtil.toRomanNumeral(effect.getLevel()) + "&c for &e" + DurationFormatUtils.formatDuration(effect.getDuration(), "mm:ss") + "m");
            }
            lore.add("&7&m------------------------------");
        }

        ItemStack effects = new ItemBuilder(Material.POTION).name(ChatColor.RED + (fight.getEffects().isEmpty() ? "No Potion Effects" : fight.getEffects().size() + " Effect" + (fight.getEffects().size() == 1 ? "" : "s"))).lore(lore).build();


        toReturn.setItem(51, new ItemBuilder(Material.CHEST).name(ChatColor.RED + "Copy Inventory").build());
        toReturn.setItem(52, new ItemBuilder(Material.PUMPKIN_PIE).name(ChatColor.RED + "Food Level of " + ((int)fight.getHunger() / 2)).build());
        toReturn.setItem(53, effects);

        return toReturn;
    }

    public Inventory getFightInventory(ProfileFight fight) {
        int deathNumber = (int) (getDeathsCount() - (((getData().getPage() - 1) * 9) + getData().getIndex()));
        Inventory toReturn = Bukkit.createInventory(null, 18, ChatColor.RED + "Death #" + deathNumber);

        toReturn.setItem(0, new ItemBuilder(Material.CARPET).durability(7).name(ChatColor.RED + "Previous Death").build());
        toReturn.setItem(8, new ItemBuilder(Material.CARPET).durability(7).name(ChatColor.RED + "Next Death").build());
        toReturn.setItem(4, new ItemBuilder(Material.PAPER).name(ChatColor.RED + "Death Information").lore(Arrays.asList(ChatColor.YELLOW + "Player: " + ChatColor.RED + profile.getName())).build());

        ItemStack inventoryContents = new ItemBuilder(Material.CHEST).name(ChatColor.RED + "Inventory Contents").lore(Arrays.asList(
                "&7&m------------------------------",
                (fight.getInventorySize() == 0 ? "&e" + profile.getName() + "&c had an empty inventory" : "&e" + profile.getName() + "&c had " + fight.getInventorySize() + " items"),
                (fight.hasArmor() ? "&e" + profile.getName() + "&c was wearing armor" : "&e" + profile.getName() + "&c was not wearing armor"),
                "&7&m------------------------------",
                "&eClick to view expanded details",
                "&7&m------------------------------"
        )).build();

        ItemStack deathLocation = new ItemBuilder(Material.EMPTY_MAP).name(ChatColor.RED + "Death Location").lore(Arrays.asList(
                "&7&m------------------------------",
                "&eWorld: &c" + WordUtils.capitalize(fight.getLocation().getWorld().getName().replace("_", " ").toLowerCase()),
                "&eCoordinates: &cX: " + fight.getLocation().getBlockX() + ", Y: " + fight.getLocation().getBlockY() + ", Z: " + fight.getLocation().getBlockZ(),
                "&7&m------------------------------",
                "&eClick to teleport to death location",
                "&7&m------------------------------"
        )).build();

        if (profile.getDeathban() != null) {
            toReturn.setItem(10, inventoryContents);
            toReturn.setItem(13, deathLocation);
            toReturn.setItem(16, new ItemBuilder(Material.SKULL_ITEM).durability(2).name(ChatColor.RED + "Revive " + profile.getName()).build());
        } else {
            toReturn.setItem(11, inventoryContents);
            toReturn.setItem(15, deathLocation);
        }

        return toReturn;
    }

}
