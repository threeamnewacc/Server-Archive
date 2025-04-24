package com.massivecraft.factions.zcore.util;

import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class MaterialMatcher {

	private final Material material;
	private final int damage;

	public static MaterialMatcher parse(String s) throws Exception {
		String[] params = s.split(":");
		if ((params.length < 1) || (params.length > 2)) {
			throw new Exception("Expected one or two column delimited parameters");
		}

		Material material = Material.matchMaterial(params[0]);
		if (material == null) {
			throw new Exception("Unknown material \"" + params[0] + "\"");
		}

		int damage = -1;
		if (params.length > 1) {
			try {
				damage = Integer.parseInt(params[1]);
			} catch (NumberFormatException ex) {
				throw new Exception("Integer expected for second parameter");
			}
		}
		return new MaterialMatcher(material, damage);
	}

	public boolean matches(ItemStack itemStack) {
		return this.material == itemStack.getType() && (this.damage == -1 || this.damage == itemStack.getDurability());
	}

	public String toString() {
		if (this.damage == -1) {
			return this.material.toString();
		}
		return this.material.toString() + ":" + this.damage;
	}
}
