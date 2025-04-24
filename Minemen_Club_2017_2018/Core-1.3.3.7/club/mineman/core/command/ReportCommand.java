// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.core.command;

import java.beans.ConstructorProperties;
import club.mineman.core.util.finalutil.PlayerUtil;
import org.bukkit.event.inventory.InventoryClickEvent;
import club.mineman.core.gui.GuiClickable;
import club.mineman.core.mineman.Mineman;
import club.mineman.core.gui.GuiItem;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Arrays;
import club.mineman.core.util.finalutil.ItemUtil;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import club.mineman.core.gui.GuiPage;
import club.mineman.core.gui.GuiFolder;
import club.mineman.core.rank.Rank;
import club.mineman.core.CorePlugin;
import org.bukkit.Bukkit;
import club.mineman.core.util.finalutil.CC;
import club.mineman.core.util.finalutil.StringUtil;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import club.mineman.core.util.ttl.TtlArrayList;
import java.util.concurrent.TimeUnit;
import java.util.UUID;
import java.util.List;
import org.bukkit.command.Command;

public class ReportCommand extends Command
{
    private List<UUID> cooldown;
    
    public ReportCommand() {
        super("report");
        this.setDescription("Report a player.");
        this.cooldown = new TtlArrayList<UUID>(TimeUnit.SECONDS, 30L);
    }
    
    public boolean execute(final CommandSender sender, final String s, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(StringUtil.PLAYER_ONLY);
            return true;
        }
        final Player player = (Player)sender;
        if (args.length < 1) {
            player.sendMessage(CC.RED + "Please use /report <player>");
            return true;
        }
        if (Bukkit.getPlayer(args[0]) == null) {
            player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[0]));
            return true;
        }
        if (player.getName().equalsIgnoreCase(args[0])) {
            player.sendMessage(CC.RED + "You cannot report yourself.");
            return true;
        }
        if (this.cooldown.contains(player.getUniqueId())) {
            player.sendMessage(CC.RED + "Please wait to do this again.");
            return true;
        }
        final Player target = Bukkit.getPlayer(args[0]);
        final Mineman minemanTarget = CorePlugin.getInstance().getPlayerManager().getPlayer(target.getUniqueId());
        if (minemanTarget.hasRank(Rank.TRAINEE)) {
            player.sendMessage(CC.RED + "If you wish to report this person, please personally message an admin.");
            return true;
        }
        final GuiFolder folder = new GuiFolder(CC.GREEN + "Report Menu", 9);
        final GuiPage page = new GuiPage(folder);
        final List<ItemStack> items = new ArrayList<ItemStack>(Arrays.asList(ItemUtil.createItem(Material.DIAMOND_SWORD, CC.GOLD + "Combat Hacks"), ItemUtil.createItem(Material.GOLD_SWORD, CC.GOLD + "Movement Hacks"), ItemUtil.createItem(Material.BOOK_AND_QUILL, CC.GOLD + "Chat Violation"), ItemUtil.createItem(Material.ENDER_PEARL, CC.GOLD + "Other")));
        for (int i = 1; i <= 7; i += 2) {
            page.addItem(i, new ReportClickable(items, this, player, args[0]));
        }
        page.fill();
        folder.setCurrentPage(page);
        folder.openGui(player);
        return true;
    }
    
    private class ReportClickable implements GuiClickable
    {
        private final List<ItemStack> items;
        private final ReportCommand command;
        private final Player player;
        private final String target;
        
        @Override
        public void onClick(final InventoryClickEvent event) {
            this.player.closeInventory();
            this.command.cooldown.add(this.player.getUniqueId());
            final String reportPrefix = CC.GRAY + "[" + CC.RED + "Report" + CC.GRAY + "] ";
            this.player.sendMessage(CC.GREEN + "Your report has been submitted.");
            PlayerUtil.messageStaff(reportPrefix + CC.RED + this.player.getName() + " has reported " + this.target + " for " + event.getCurrentItem().getItemMeta().getDisplayName() + CC.RED + ".");
        }
        
        @Override
        public ItemStack getItemStack() {
            return this.items.remove(0);
        }
        
        @ConstructorProperties({ "items", "command", "player", "target" })
        public ReportClickable(final List<ItemStack> items, final ReportCommand command, final Player player, final String target) {
            this.items = items;
            this.command = command;
            this.player = player;
            this.target = target;
        }
    }
}
