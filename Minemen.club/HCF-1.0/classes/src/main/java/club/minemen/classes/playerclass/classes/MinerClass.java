package club.minemen.classes.playerclass.classes;

import club.minemen.classes.ClassesPlugin;
import club.minemen.classes.manager.EffectManager;
import club.minemen.classes.playerclass.AbstractPlayerClass;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @since 12/31/2017
 */
public final class MinerClass extends AbstractPlayerClass {

	private final PotionEffect fireResistance = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0);

	private final PotionEffect resistance2 = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1);
	private final PotionEffect resistance1 = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0);

	private final PotionEffect invisibility = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0);
	private final PotionEffect regeneration = new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0);

	private final PotionEffect haste3 = new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 2);
	private final PotionEffect haste4 = new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 3);
	private final PotionEffect haste5 = new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 4);

	private final PotionEffect speed1 = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0);
	private final PotionEffect speed2 = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1);

	public MinerClass() {
		super("Miner", "IRON");

		this.getClassPassiveEffects().put(PotionEffectType.NIGHT_VISION,
				new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));

		this.getClassPassiveEffects().put(PotionEffectType.FAST_DIGGING,
				new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1));
	}

	@Override
	public void onTick(Player player) {
		if (player.getLocation().getY() <= 20) {
			ClassesPlugin.getInstance().getEffectManager().addEffect(player, this.invisibility);
		} else {
			ClassesPlugin.getInstance().getEffectManager().removeEffect(player, this.invisibility);
		}

		int diamondsMined = player.getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE);

		// give regen shit
		if (diamondsMined >= 250) {
			ClassesPlugin.getInstance().getEffectManager().addEffect(player, this.regeneration);
		}

		// give f res shit
		if (diamondsMined >= 750) {
			ClassesPlugin.getInstance().getEffectManager().addEffect(player, this.fireResistance);
		}

		// give speed shit
		if (diamondsMined >= 900) {
			ClassesPlugin.getInstance().getEffectManager().addEffect(player, this.speed2);
		} else if (diamondsMined >= 100) {
			ClassesPlugin.getInstance().getEffectManager().addEffect(player, this.speed1);
		}

		// give resistance1 shit
		if (diamondsMined >= 1200) {
			ClassesPlugin.getInstance().getEffectManager().addEffect(player, this.resistance2);
		} else if (diamondsMined >= 500) {
			ClassesPlugin.getInstance().getEffectManager().addEffect(player, this.resistance1);
		}

		// give haste shit
		if (diamondsMined >= 1000) {
			ClassesPlugin.getInstance().getEffectManager().addEffect(player, this.haste5);
		} else if (diamondsMined >= 600) {
			ClassesPlugin.getInstance().getEffectManager().addEffect(player, this.haste4);
		} else if (diamondsMined >= 50) {
			ClassesPlugin.getInstance().getEffectManager().addEffect(player, this.haste3);
		}
	}

	@Override
	public void onDequip(Player player) {
		super.onDequip(player);

		EffectManager em = ClassesPlugin.getInstance().getEffectManager();
		em.removeEffect(player, this.invisibility);
		em.removeEffect(player, this.haste3);
		em.removeEffect(player, this.haste4);
		em.removeEffect(player, this.haste5);
		em.removeEffect(player, this.speed1);
		em.removeEffect(player, this.speed2);
		em.removeEffect(player, this.resistance1);
		em.removeEffect(player, this.resistance2);
		em.removeEffect(player, this.fireResistance);
		em.removeEffect(player, this.regeneration);
	}
}
