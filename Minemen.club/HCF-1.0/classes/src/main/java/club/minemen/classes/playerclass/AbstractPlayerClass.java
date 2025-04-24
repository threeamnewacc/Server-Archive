package club.minemen.classes.playerclass;

import club.minemen.classes.ClassesPlugin;
import club.minemen.core.util.finalutil.CC;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

/**
 * @since 12/31/2017
 */
@Getter
@RequiredArgsConstructor
public abstract class AbstractPlayerClass implements IPlayerClass {

	private final String armour;
	private final String name;

	@Getter
	private final Map<PotionEffectType, PotionEffect> classPassiveEffects = new HashMap<>();

	@Override
	public void onEquip(Player player) {
		this.getClassPassiveEffects().values().forEach(potionEffect -> ClassesPlugin.getInstance().getEffectManager().addEffect(player, potionEffect));

		player.sendFormattedMessage("{0}You''ve equipped the {1}{2}{0} class.", CC.PRIMARY, CC.SECONDARY, this.getName());
	}

	@Override
	public void onDequip(Player player) {
		this.getClassPassiveEffects().values().forEach(potionEffect -> ClassesPlugin.getInstance().getEffectManager().removeEffect(player, potionEffect));

		player.sendFormattedMessage("{0}You''ve unequipped the {1}{2}{0} class.", CC.PRIMARY, CC.SECONDARY, this.getName());
	}

	@Override
	public boolean hasClassEquipped(Player player) {
		for (ItemStack itemStack : player.getInventory().getArmorContents()) {
			if (itemStack == null || !itemStack.getType().name().startsWith(this.armour + "_")) {
				return false;
			}
		}
		return true;
	}

}
