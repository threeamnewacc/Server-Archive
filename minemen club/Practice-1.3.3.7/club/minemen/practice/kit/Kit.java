// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.kit;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import club.minemen.core.util.finalutil.CC;
import org.bukkit.entity.Player;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class Kit
{
    private final String name;
    private ItemStack[] contents;
    private ItemStack[] armor;
    private ItemStack[] kitEditContents;
    private ItemStack icon;
    private List<String> excludedArenas;
    private List<String> arenaWhiteList;
    private boolean enabled;
    private boolean ranked;
    private boolean combo;
    private boolean sumo;
    private boolean build;
    private boolean spleef;
    
    public void applyToPlayer(final Player player) {
        player.getInventory().setContents(this.contents);
        player.getInventory().setArmorContents(this.armor);
        player.updateInventory();
        player.sendMessage(CC.PRIMARY + "Giving you the default kit.");
    }
    
    public void whitelistArena(final String arena) {
        if (!this.arenaWhiteList.remove(arena)) {
            this.arenaWhiteList.add(arena);
        }
    }
    
    public void excludeArena(final String arena) {
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
    
    public void setContents(final ItemStack[] contents) {
        this.contents = contents;
    }
    
    public void setArmor(final ItemStack[] armor) {
        this.armor = armor;
    }
    
    public void setKitEditContents(final ItemStack[] kitEditContents) {
        this.kitEditContents = kitEditContents;
    }
    
    public void setIcon(final ItemStack icon) {
        this.icon = icon;
    }
    
    public void setExcludedArenas(final List<String> excludedArenas) {
        this.excludedArenas = excludedArenas;
    }
    
    public void setArenaWhiteList(final List<String> arenaWhiteList) {
        this.arenaWhiteList = arenaWhiteList;
    }
    
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
    
    public void setRanked(final boolean ranked) {
        this.ranked = ranked;
    }
    
    public void setCombo(final boolean combo) {
        this.combo = combo;
    }
    
    public void setSumo(final boolean sumo) {
        this.sumo = sumo;
    }
    
    public void setBuild(final boolean build) {
        this.build = build;
    }
    
    public void setSpleef(final boolean spleef) {
        this.spleef = spleef;
    }
    
    @ConstructorProperties({ "name", "contents", "armor", "kitEditContents", "icon", "excludedArenas", "arenaWhiteList", "enabled", "ranked", "combo", "sumo", "build", "spleef" })
    public Kit(final String name, final ItemStack[] contents, final ItemStack[] armor, final ItemStack[] kitEditContents, final ItemStack icon, final List<String> excludedArenas, final List<String> arenaWhiteList, final boolean enabled, final boolean ranked, final boolean combo, final boolean sumo, final boolean build, final boolean spleef) {
        this.contents = new ItemStack[36];
        this.armor = new ItemStack[4];
        this.kitEditContents = new ItemStack[36];
        this.excludedArenas = new ArrayList<String>();
        this.arenaWhiteList = new ArrayList<String>();
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
    }
    
    @ConstructorProperties({ "name" })
    public Kit(final String name) {
        this.contents = new ItemStack[36];
        this.armor = new ItemStack[4];
        this.kitEditContents = new ItemStack[36];
        this.excludedArenas = new ArrayList<String>();
        this.arenaWhiteList = new ArrayList<String>();
        this.name = name;
    }
}
