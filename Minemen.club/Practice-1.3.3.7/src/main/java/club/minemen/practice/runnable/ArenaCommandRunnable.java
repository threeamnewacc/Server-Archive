package club.minemen.practice.runnable;

import club.minemen.core.util.CustomLocation;
import club.minemen.core.util.finalutil.CC;
import club.minemen.practice.Practice;
import club.minemen.practice.arena.Arena;
import club.minemen.practice.arena.StandaloneArena;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @since 11/25/2017
 */
@Getter
@AllArgsConstructor
public class ArenaCommandRunnable implements Runnable {

	private final Practice plugin;
	private final Arena copiedArena;

	private int times;

	@Override
	public void run() {
		this.duplicateArena(this.copiedArena, 10000, 10000);
	}

	private void duplicateArena(Arena arena, int offsetX, int offsetZ) {
		new DuplicateArenaRunnable(this.plugin, arena, offsetX, offsetZ, 500, 500) {
			@Override
			public void onComplete() {
				double minX = arena.getMin().getX() + this.getOffsetX();
				double minZ = arena.getMin().getZ() + this.getOffsetZ();
				double maxX = arena.getMax().getX() + this.getOffsetX();
				double maxZ = arena.getMax().getZ() + this.getOffsetZ();
				double aX = arena.getA().getX() + this.getOffsetX();
				double aZ = arena.getA().getZ() + this.getOffsetZ();
				double bX = arena.getB().getX() + this.getOffsetX();
				double bZ = arena.getB().getZ() + this.getOffsetZ();

				CustomLocation min = new CustomLocation(minX, arena.getMin().getY(), minZ);
				CustomLocation max = new CustomLocation(maxX, arena.getMax().getY(), maxZ);
				CustomLocation a = new CustomLocation(aX, arena.getA().getY(), aZ);
				CustomLocation b = new CustomLocation(bX, arena.getB().getY(), bZ);

				StandaloneArena standaloneArena = new StandaloneArena(a, b, min, max);

				arena.addStandaloneArena(standaloneArena);
				arena.addAvailableArena(standaloneArena);

				if (--ArenaCommandRunnable.this.times > 0) {
					ArenaCommandRunnable.this.plugin.getServer().broadcastMessage(
							CC.PRIMARY + "Placed a standalone arena of " + CC.SECONDARY + arena.getName() + CC.PRIMARY
									+ " at " + CC.SECONDARY + minX + CC.PRIMARY + ", " + CC.SECONDARY + minZ
									+ CC.PRIMARY + ". " + CC.SECONDARY + ArenaCommandRunnable.this.times + CC.PRIMARY + " arenas remaining.");
					ArenaCommandRunnable.this.duplicateArena(arena, (int) Math.round(maxX), (int) Math.round(maxZ));
				} else {
					ArenaCommandRunnable.this.plugin.getServer().broadcastMessage(CC.PRIMARY + "Finished pasting " + CC.SECONDARY
							+ ArenaCommandRunnable.this.copiedArena.getName() + CC.PRIMARY + "'s standalone arenas.");
					ArenaCommandRunnable.this.plugin.getArenaManager().setGeneratingArenaRunnables(
							ArenaCommandRunnable.this.plugin.getArenaManager().getGeneratingArenaRunnables() - 1);
				}
			}
		}.run();
	}
}
