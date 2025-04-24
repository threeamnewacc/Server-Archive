package club.minemen.classes.manager;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@RequiredArgsConstructor
public final class EffectManager {

	private static final int SECONDARY_DURATION = 400;
	private static final int TERTIARY_DURATION = 20;
	private static final int DURATION = 600;

	private final Cache<Long, Boolean> infiniteCache
			= CacheBuilder.newBuilder().expireAfterWrite(500, TimeUnit.MILLISECONDS).build();

	private final SetMultimap<Integer, PotionEffect> effects = HashMultimap.create();
	private final List<PotionEffect> buffer = new ArrayList<>();

	public void tick(Player player) {
		this.buffer.clear();
		this.buffer.addAll(this.effects.get(player.getEntityId()));
		this.buffer.forEach(effect -> tickEffect(player, effect));
	}

	private void tickEffect(Player player, PotionEffect effect) {
		PotionEffect current = player.getActivePotionEffects().stream()
		                             .filter(e -> e.getType().equals(effect.getType())).findAny().orElse(null);
		refillEffect(player, current, effect);
	}

	private void refillEffect(Player player, PotionEffect current, PotionEffect effect) {
		if (current == null || current.getAmplifier() < effect.getAmplifier()) {
			// no effect or worse effect, give them the full effect
			flagInfinite(player, effect.getType());
			player.addPotionEffect(new PotionEffect(effect.getType(), DURATION, effect.getAmplifier(), true), true);
		} else if (current.getAmplifier() == effect.getAmplifier()) {
			if (current.getDuration() < SECONDARY_DURATION) {
				// adding an amount instead of setting should keep effect ticking (eg. regen) regular
				flagInfinite(player, effect.getType());
				player.addPotionEffect(
						new PotionEffect(effect.getType(), current.getDuration() + 100, effect.getAmplifier(), true),
						true);
			} else if (current.getDuration() > DURATION) {
				// somehow we got a longer time than what a refill should be, possibly we drank a potion
				// remove infinite effect and refresh potion time
				this.effects.get(player.getEntityId()).remove(effect);
				player.addPotionEffect(
						new PotionEffect(current.getType(), current.getDuration() + 1, current.getAmplifier(),
								current.isAmbient()), true);
			}
		} else if (current.getDuration() < TERTIARY_DURATION) {
			// better effect, only override if less than 1 second remaining
			flagInfinite(player, effect.getType());
			player.addPotionEffect(new PotionEffect(effect.getType(), DURATION, effect.getAmplifier(), true), true);
		}
	}

	public boolean isInfiniteEffect(Player player, PotionEffectType type, int amplifier) {
		return this.effects.get(player.getEntityId()).stream()
		                   .anyMatch(e -> e.getType().equals(type) && e.getAmplifier() == amplifier);
	}

	public void removePlayer(Player player) {
		this.effects.get(player.getEntityId()).forEach(e -> player.removePotionEffect(e.getType()));
		this.effects.removeAll(player.getEntityId());
	}

	public boolean addEffect(Player player, PotionEffect effect) {
		if (this.effects.put(player.getEntityId(), effect)) {
			tickEffect(player, effect);
			return true;
		}
		return false;
	}

	public boolean removeEffect(Player player, PotionEffect effect) {
		if (effects.remove(player.getEntityId(), effect)) {
			player.getActivePotionEffects().stream()
			      .filter(e -> e.getType().equals(effect.getType()) && e.getAmplifier() == effect.getAmplifier())
			      .forEach(e -> player.removePotionEffect(effect.getType()));
			tick(player);
			return true;
		}
		return false;
	}

	public void flagInfinite(Player player, PotionEffectType type) {
		this.infiniteCache.put(player.getEntityId() | (long) type.getId() << 32, Boolean.TRUE);
	}

	public boolean isInfiniteFlagged(Player player, int type) {
		return this.infiniteCache.getIfPresent(player.getEntityId() | (long) type << 32) != null;
	}
}
