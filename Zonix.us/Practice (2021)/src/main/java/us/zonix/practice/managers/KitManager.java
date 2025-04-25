package us.zonix.practice.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.edater.spigot.EdaterSpigot;
import net.edater.spigot.knockback.KnockbackProfile;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import us.zonix.practice.Practice;
import us.zonix.practice.file.ConfigFile;
import us.zonix.practice.kit.Kit;

public class KitManager {
    private final Practice plugin = Practice.getInstance();
    private final Map<String, Kit> kits = new HashMap<>();
    private final List<String> rankedKits = new ArrayList<>();
    private final ConfigFile kitsFile = new ConfigFile(this.plugin, "kits");

    public KitManager() {
        this.loadKits();
        this.kits
            .entrySet()
            .stream()
            .filter(kit -> kit.getValue().isEnabled())
            .filter(kit -> kit.getValue().isRanked())
            .forEach(kit -> this.rankedKits.add(kit.getKey()));
    }

    private void loadKits() {
        ConfigurationSection kitSection = this.kitsFile.getConfiguration().getConfigurationSection("kits");
        if (kitSection != null) {
            kitSection.getKeys(false)
                .forEach(
                    name -> {
                        ItemStack[] contents = (kitSection.getList(name + ".contents")).toArray(new ItemStack[0]);
                        ItemStack[] armor = (kitSection.getList(name + ".armor")).toArray(new ItemStack[0]);
                        ItemStack[] kitEditContents = (kitSection.getList(name + ".kitEditContents")).toArray(new ItemStack[0]);
                        List<String> excludedArenas = kitSection.getStringList(name + ".excludedArenas");
                        List<String> arenaWhiteList = kitSection.getStringList(name + ".arenaWhitelist");
                        ItemStack icon = (ItemStack)kitSection.get(name + ".icon");
                        boolean enabled = kitSection.getBoolean(name + ".enabled");
                        boolean ranked = kitSection.getBoolean(name + ".ranked");
                        boolean combo = kitSection.getBoolean(name + ".combo");
                        boolean sumo = kitSection.getBoolean(name + ".sumo");
                        boolean build = kitSection.getBoolean(name + ".build");
                        boolean spleef = kitSection.getBoolean(name + ".spleef");
                        boolean parkour = kitSection.getBoolean(name + ".parkour");
                        boolean hcteams = kitSection.getBoolean(name + ".hcteams");
                        boolean bestOfThree = kitSection.getBoolean(name + ".bestOfThree");
                        boolean premium = kitSection.getBoolean(name + ".premium");
                        int priority = kitSection.getInt(name + ".priority");
                        Optional<KnockbackProfile> knockback = EdaterSpigot.INSTANCE
                            .getKnockbackHandler()
                            .getProfileByName(kitSection.getString(name + ".knockback"));
                        Kit kit = new Kit(
                            name,
                            contents,
                            armor,
                            kitEditContents,
                            icon,
                            excludedArenas,
                            arenaWhiteList,
                            enabled,
                            ranked,
                            combo,
                            sumo,
                            build,
                            spleef,
                            parkour,
                            hcteams,
                            premium,
                            bestOfThree,
                            priority,
                            knockback.orElseGet(() -> EdaterSpigot.INSTANCE.getKnockbackHandler().getActiveProfile())
                        );
                        this.kits.put(name, kit);
                    }
                );
        }
    }

    public void saveKits() {
        FileConfiguration fileConfig = this.kitsFile.getConfiguration();
        fileConfig.set("kits", null);
        this.kits.forEach((kitName, kit) -> {
            if (kit.getIcon() != null && kit.getContents() != null && kit.getArmor() != null) {
                fileConfig.set("kits." + kitName + ".contents", kit.getContents());
                fileConfig.set("kits." + kitName + ".armor", kit.getArmor());
                fileConfig.set("kits." + kitName + ".kitEditContents", kit.getKitEditContents());
                fileConfig.set("kits." + kitName + ".icon", kit.getIcon());
                fileConfig.set("kits." + kitName + ".excludedArenas", kit.getExcludedArenas());
                fileConfig.set("kits." + kitName + ".arenaWhitelist", kit.getArenaWhiteList());
                fileConfig.set("kits." + kitName + ".enabled", kit.isEnabled());
                fileConfig.set("kits." + kitName + ".ranked", kit.isRanked());
                fileConfig.set("kits." + kitName + ".combo", kit.isCombo());
                fileConfig.set("kits." + kitName + ".sumo", kit.isSumo());
                fileConfig.set("kits." + kitName + ".build", kit.isBuild());
                fileConfig.set("kits." + kitName + ".spleef", kit.isSpleef());
                fileConfig.set("kits." + kitName + ".parkour", kit.isParkour());
                fileConfig.set("kits." + kitName + ".hcteams", kit.isHcteams());
                fileConfig.set("kits." + kitName + ".bestOfThree", kit.isBestOfThree());
                fileConfig.set("kits." + kitName + ".premium", kit.isPremium());
                fileConfig.set("kits." + kitName + ".priority", kit.getPriority());
                fileConfig.set("kits." + kitName + ".knockback", kit.getKnockbackProfile().getName());
            }
        });
        this.kitsFile.save();
    }

    public void deleteKit(String name) {
        this.kits.remove(name);
    }

    public void createKit(String name) {
        this.kits.put(name, new Kit(name));
    }

    public Collection<Kit> getKits() {
        return this.kits.values();
    }

    public Kit getKit(String name) {
        return this.kits.get(name);
    }

    public List<String> getRankedKits() {
        return this.rankedKits;
    }
}
