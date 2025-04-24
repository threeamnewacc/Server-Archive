package com.massivecraft.factions.menu.player;

import club.minemen.hcfactions.HCFactions;
import club.minemen.hcfactions.utils.ItemStackUtil;
import com.massivecraft.factions.FactionPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.menu.AbstractMenu;
import com.massivecraft.factions.struct.Role;
import com.massivecraft.factions.type.SubclaimOwner;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;

public class SubclaimAuthMenu extends AbstractMenu {

	private Faction faction;
	private SubclaimOwner subclaimOwner;
	private FactionPlayer factionPlayer;
	private Map<Integer, FactionPlayer> members = new HashMap<>();


	public SubclaimAuthMenu(HCFactions plugin, SubclaimOwner subclaimOwner, FactionPlayer factionPlayer) {
		super(plugin, 54, "Subclaim Menu");
		this.factionPlayer = factionPlayer;
		this.faction = factionPlayer.getFaction();
		this.subclaimOwner = subclaimOwner;
		updateSkulls();
	}

	public void updateSkulls() {
		ItemStack skull;
		int size = 54;
		int top = 0;
		int bottom = size - subclaimOwner.getAddedMembers().size();
		ItemStack spacer;
		for (int p = 0; p < size; p++) {
			short data = 14;
			if (p * 2 > bottom) {
				data = 5;
			}
			spacer = ItemStackUtil.createItem(Material.STAINED_GLASS_PANE, data, data == 5 ? ChatColor.GREEN + "Added players below" : ChatColor.RED + "Not added players above", "Click a head to give or take away access");
			inventory.setItem(p, spacer);
		}
		for (FactionPlayer member : faction.getFPlayersWhereRole(Role.NORMAL)) {
			if (member.equals(factionPlayer)) {
				continue;
			}
			if (subclaimOwner.getAddedMembers().contains(member.getUuid())) {
				inventory.setItem(bottom, getSkull(member.getName(), ChatColor.RED + "Remove " + member.getName()));
				members.put(bottom, member);
				bottom++;
			} else {
				inventory.setItem(top, getSkull(member.getName(), ChatColor.GREEN + "Add " + member.getName()));
				members.put(top, member);
				top++;
			}
		}
	}


	private ItemStack getSkull(String playerName, String text) {
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwner(playerName);
		meta.setDisplayName(text);
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event) {
		//TODO: Add or take away players perms to use subclaim
		if (event.getClick() == null || event.getClickedInventory() == null) {
			return;
		}
		if (event.getClickedInventory().equals(inventory)) {
			event.setCancelled(true);
			int slot = event.getSlot();
			if (members.containsKey(slot)) {
				FactionPlayer member = members.get(slot);
				if (subclaimOwner.getAddedMembers().contains(member.getUuid())) {
					subclaimOwner.getAddedMembers().remove(member.getUuid());
				} else {
					subclaimOwner.getAddedMembers().add(member.getUuid());
				}
				updateSkulls();
			}
		}
	}

	@Override
	public void onInventoryDrag(InventoryDragEvent event) {
		event.setCancelled(true);
	}

	@Override
	public void onInventoryClose(InventoryCloseEvent event) {
	}
}
