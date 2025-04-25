package us.zonix.practice.kit;

import java.util.ArrayList;
import java.util.List;
import net.edater.spigot.EdaterSpigot;
import net.edater.spigot.knockback.KnockbackProfile;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import us.zonix.practice.bots.ZonixBot;

public class Kit {
    private final String name;
    private ItemStack[] contents = new ItemStack[36];
    private ItemStack[] armor = new ItemStack[4];
    private ItemStack[] kitEditContents = new ItemStack[36];
    private ItemStack icon;
    private List<String> excludedArenas = new ArrayList<>();
    private List<String> arenaWhiteList = new ArrayList<>();
    private boolean enabled = false;
    private boolean ranked = false;
    private boolean combo = false;
    private boolean sumo = false;
    private boolean build = false;
    private boolean spleef = false;
    private boolean parkour = false;
    private boolean hcteams = false;
    private boolean premium = false;
    private boolean bestOfThree = false;
    private int priority = 0;
    private KnockbackProfile knockbackProfile = EdaterSpigot.INSTANCE.getKnockbackHandler().getActiveProfile();

    public void applyToPlayer(Player player) {
        player.getInventory().setContents(this.contents);
        player.getInventory().setArmorContents(this.armor);
        player.updateInventory();
        player.setKnockbackProfile(this.knockbackProfile);
    }

    public void applyToNPC(ZonixBot bot) {
        bot.getBukkitEntity().getInventory().setContents(this.contents);
        bot.getBukkitEntity().getInventory().setArmorContents(this.armor);
        bot.getBukkitEntity().updateInventory();
        bot.getBukkitEntity().setKnockbackProfile(this.knockbackProfile);
    }

    public void whitelistArena(String arena) {
        if (!this.arenaWhiteList.remove(arena)) {
            this.arenaWhiteList.add(arena);
        }
    }

    public void excludeArena(String arena) {
        if (!this.excludedArenas.remove(arena)) {
            this.excludedArenas.add(arena);
        }
    }

    public String getName() {
        return this.name;
    }

    public ItemStack[] getContents() {
        return this.contents;
    }

    public ItemStack[] getArmor() {
        return this.armor;
    }

    public ItemStack[] getKitEditContents() {
        return this.kitEditContents;
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    public List<String> getExcludedArenas() {
        return this.excludedArenas;
    }

    public List<String> getArenaWhiteList() {
        return this.arenaWhiteList;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isRanked() {
        return this.ranked;
    }

    public boolean isCombo() {
        return this.combo;
    }

    public boolean isSumo() {
        return this.sumo;
    }

    public boolean isBuild() {
        return this.build;
    }

    public boolean isSpleef() {
        return this.spleef;
    }

    public boolean isParkour() {
        return this.parkour;
    }

    public boolean isHcteams() {
        return this.hcteams;
    }

    public boolean isPremium() {
        return this.premium;
    }

    public boolean isBestOfThree() {
        return this.bestOfThree;
    }

    public int getPriority() {
        return this.priority;
    }

    public KnockbackProfile getKnockbackProfile() {
        return this.knockbackProfile;
    }

    public void setContents(ItemStack[] contents) {
        this.contents = contents;
    }

    public void setArmor(ItemStack[] armor) {
        this.armor = armor;
    }

    public void setKitEditContents(ItemStack[] kitEditContents) {
        this.kitEditContents = kitEditContents;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public void setExcludedArenas(List<String> excludedArenas) {
        this.excludedArenas = excludedArenas;
    }

    public void setArenaWhiteList(List<String> arenaWhiteList) {
        this.arenaWhiteList = arenaWhiteList;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setRanked(boolean ranked) {
        this.ranked = ranked;
    }

    public void setCombo(boolean combo) {
        this.combo = combo;
    }

    public void setSumo(boolean sumo) {
        this.sumo = sumo;
    }

    public void setBuild(boolean build) {
        this.build = build;
    }

    public void setSpleef(boolean spleef) {
        this.spleef = spleef;
    }

    public void setParkour(boolean parkour) {
        this.parkour = parkour;
    }

    public void setHcteams(boolean hcteams) {
        this.hcteams = hcteams;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public void setBestOfThree(boolean bestOfThree) {
        this.bestOfThree = bestOfThree;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setKnockbackProfile(KnockbackProfile knockbackProfile) {
        this.knockbackProfile = knockbackProfile;
    }

    public Kit(
        String name,
        ItemStack[] contents,
        ItemStack[] armor,
        ItemStack[] kitEditContents,
        ItemStack icon,
        List<String> excludedArenas,
        List<String> arenaWhiteList,
        boolean enabled,
        boolean ranked,
        boolean combo,
        boolean sumo,
        boolean build,
        boolean spleef,
        boolean parkour,
        boolean hcteams,
        boolean premium,
        boolean bestOfThree,
        int priority,
        KnockbackProfile knockbackProfile
    ) {
        this.name = name;
        this.contents = contents;
        this.armor = armor;
        this.kitEditContents = kitEditContents;
        this.icon = icon;
        this.excludedArenas = excludedArenas;
        this.arenaWhiteList = arenaWhiteList;
        this.enabled = enabled;
        this.ranked = ranked;
        this.combo = combo;
        this.sumo = sumo;
        this.build = build;
        this.spleef = spleef;
        this.parkour = parkour;
        this.hcteams = hcteams;
        this.premium = premium;
        this.bestOfThree = bestOfThree;
        this.priority = priority;
        this.knockbackProfile = knockbackProfile;
    }

    public Kit(String name) {
        this.name = name;
    }
}
