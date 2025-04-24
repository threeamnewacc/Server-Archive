package club.minemen.classes.playerclass;

import org.bukkit.entity.Player;

/**
 * @since 12/31/2017
 */
public interface IPlayerClass {

	String getName();

	boolean hasClassEquipped(Player player);

	void onEquip(Player player);

	void onDequip(Player player);

	void onTick(Player player);

}
