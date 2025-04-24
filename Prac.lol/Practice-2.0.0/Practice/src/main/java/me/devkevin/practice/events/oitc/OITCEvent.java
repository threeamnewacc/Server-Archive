package me.devkevin.practice.events.oitc;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.devkevin.practice.Practice;
import me.devkevin.practice.events.EventCountdownTask;
import me.devkevin.practice.events.PracticeEvent;
import me.devkevin.practice.location.CustomLocation;
import me.devkevin.practice.profile.Profile;
import me.devkevin.practice.util.ItemUtil;
import me.devkevin.practice.util.PlayerUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class OITCEvent extends PracticeEvent<OITCPlayer> {

	private final Map<UUID, OITCPlayer> players = new HashMap<>();

	@Getter private OITCGameTask gameTask = null;
	private final OITCCountdownTask countdownTask = new OITCCountdownTask(this);
	private List<CustomLocation> respawnLocations;

	public OITCEvent() {
		super("OITC");
	}

	@Override
	public Map<UUID, OITCPlayer> getPlayers() {
		return players;
	}

	@Override
	public EventCountdownTask getCountdownTask() {
		return countdownTask;
	}

	@Override
	public List<CustomLocation> getSpawnLocations() {
		return Collections.singletonList(this.getPlugin().getCustomLocationManager().getOitcLocation());
	}

	@Override
	public void onStart() {
		this.respawnLocations = new ArrayList<>();
		this.gameTask = new OITCGameTask();
		this.gameTask.runTaskTimerAsynchronously(getPlugin(), 0, 20);
	}

	@Override
	public Consumer<Player> onJoin() {
		return player -> players.put(player.getUniqueId(), new OITCPlayer(player.getUniqueId(), this));
	}

	@Override
	public Consumer<Player> onDeath() {

		return player -> {

			OITCPlayer data = getPlayer(player);

			if (data == null) {
				return;
			}

			if(data.getState() == OITCPlayer.OITCState.WAITING) {
				return;
			}

			if(data.getState() == OITCPlayer.OITCState.ELIMINATED) {
				if (this.getByState(OITCPlayer.OITCState.FIGHTING).size() == 1 || this.getPlayers().size() == 1) {
					Player winner = Bukkit.getPlayer(this.getByState(OITCPlayer.OITCState.FIGHTING).get(0));

					Profile winnerData = Practice.getInstance().getProfileManager().getProfileData(winner.getUniqueId());
					winnerData.setOitcWins(winnerData.getOitcWins() + 1);

					for (int i = 0; i <= 2; i++) {
						String announce = ChatColor.YELLOW + "(Event) " + ChatColor.GREEN.toString() + "Winner: " + winner.getName();
						Bukkit.broadcastMessage(announce);
					}

					this.gameTask.cancel();
					end();
				}
				return;
			}

			if(data.getState() == OITCPlayer.OITCState.FIGHTING || data.getState() == OITCPlayer.OITCState.PREPARING || data.getState() == OITCPlayer.OITCState.RESPAWNING) {

				String deathMessage = ChatColor.YELLOW + "(Event) " + ChatColor.RED + player.getName() + "(" + data.getScore() + ")" + ChatColor.GRAY + " has been eliminated from the game.";

				if(data.getLastKiller() != null) {

					OITCPlayer killerData = data.getLastKiller();
					Player killer = getPlugin().getServer().getPlayer(killerData.getUuid());

					int count = killerData.getScore() + 1;
					killerData.setScore(count);

					killer.getInventory().setItem(6, ItemUtil.createItem(Material.GLOWSTONE_DUST, ChatColor.YELLOW.toString() + ChatColor.BOLD + "Kills", killerData.getScore() == 0 ? 1 : killerData.getScore()));
					if(killer.getInventory().contains(Material.ARROW)) {
						killer.getInventory().getItem(8).setAmount(killer.getInventory().getItem(8).getAmount() + 2);
					} else {
						killer.getInventory().setItem(8, new ItemStack(Material.ARROW, 2));
					}
					killer.updateInventory();

					killer.playSound(killer.getLocation(), Sound.NOTE_PLING, 1F, 1F);

					FireworkEffect fireworkEffect = FireworkEffect.builder().withColor(Color.fromRGB(127, 56, 56)).withFade(Color.fromRGB(127, 56, 56)).with(FireworkEffect.Type.BALL).build();
					PlayerUtil.sendFirework(fireworkEffect, player.getLocation().add(0, 1.5, 0));

					data.setLastKiller(null);

					deathMessage = ChatColor.YELLOW + "(Event) " + ChatColor.RED + player.getName() + "(" + data.getScore() + ")" + ChatColor.GRAY + " has been killed" + (killer == null ? "." : " by " + ChatColor.GREEN + killer.getName() + "(" + count + ")");

					if (count == 25) {

						Profile winnerData = Practice.getInstance().getProfileManager().getProfileData(killer.getUniqueId());
						winnerData.setOitcWins(winnerData.getOitcWins() + 1);

						for (int i = 0; i <= 2; i++) {
							String announce = ChatColor.YELLOW + "(Event) " + ChatColor.GREEN.toString() + "Winner: " + killer.getName();
							Bukkit.broadcastMessage(announce);
						}

						this.gameTask.cancel();
						end();
					}
				}

				if (data.getLastKiller() == null) {
					// Respawn the player
					data.setLives((data.getLives() - 1));

					Profile playerData = this.getPlugin().getProfileManager().getProfileData(player.getUniqueId());
					playerData.setOitcKills(playerData.getOitcKills() + data.getScore());
					playerData.setOitcDeaths(playerData.getOitcDeaths() + 1);

					if(data.getLives() == 0) {

						playerData.setOitcLosses(playerData.getOitcLosses() + 1);
                        getPlayers().remove(player.getUniqueId());

                        player.sendMessage(ChatColor.YELLOW + "(Event) " + ChatColor.GRAY + "You have been eliminated from the game.");

                        getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> {
                            getPlugin().getProfileManager().sendToSpawn(player);
                            if(getPlayers().size() >= 2) {
                                getPlugin().getEventManager().addSpectatorOITC(player, getPlugin().getProfileManager().getProfileData(player.getUniqueId()), OITCEvent.this);
                            }
                        });
                    } else {
                        BukkitTask respawnTask = new RespawnTask(player, data).runTaskTimerAsynchronously(getPlugin(), 0, 20);
                        data.setRespawnTask(respawnTask);
                    }
				}

				sendMessage(deathMessage);

				if (this.getByState(OITCPlayer.OITCState.FIGHTING).size() == 1 || this.getPlayers().size() == 1) {
					Player winner = Bukkit.getPlayer(this.getByState(OITCPlayer.OITCState.FIGHTING).get(0));

					Profile winnerData = Practice.getInstance().getProfileManager().getProfileData(winner.getUniqueId());
					winnerData.setOitcWins(winnerData.getOitcWins() + 1);

					for (int i = 0; i <= 2; i++) {
						String announce = ChatColor.YELLOW + "(Event) " + ChatColor.GREEN.toString() + "Winner: " + winner.getName();
						Bukkit.broadcastMessage(announce);
					}

					this.gameTask.cancel();
					end();
				}
			}
		};
	}

	public void teleportNextLocation(Player player) {
	    player.teleport(getGameLocations().remove(ThreadLocalRandom.current().nextInt(getGameLocations().size())).toBukkitLocation());
	}

	private List<CustomLocation> getGameLocations() {

		if(this.respawnLocations != null && this.respawnLocations.size() == 0) {
			this.respawnLocations.addAll(this.getPlugin().getCustomLocationManager().getOitcSpawnpoints());
		}

		return this.respawnLocations;
	}

	private void giveRespawnItems(Player player, OITCPlayer oitcPlayer) {

		this.getPlugin().getServer().getScheduler().runTask(this.getPlugin(), () -> {
            PlayerUtil.reset(player);
            player.getInventory().setItem(0, ItemUtil.createItem(Material.WOOD_SWORD, ChatColor.GREEN + "Wood Sword"));
            player.getInventory().setItem(1, ItemUtil.createItem(Material.BOW, ChatColor.GREEN + "Bow"));
            player.getInventory().setItem(6, ItemUtil.createItem(Material.GLOWSTONE_DUST, ChatColor.YELLOW.toString() + ChatColor.BOLD + "Kills", oitcPlayer.getScore() == 0 ? 1 : oitcPlayer.getScore()));
            player.getInventory().setItem(7, ItemUtil.createItem(Material.REDSTONE, ChatColor.RED.toString() + ChatColor.BOLD + "Lives", oitcPlayer.getLives()));
            player.getInventory().setItem(8, new ItemStack(Material.ARROW));
			player.updateInventory();
        });
	}

	private Player getRandomPlayer() {

		if(getByState(OITCPlayer.OITCState.FIGHTING).size() == 0) {
			return null;
		}

		List<UUID> fighting = getByState(OITCPlayer.OITCState.FIGHTING);

		Collections.shuffle(fighting);

		UUID uuid = fighting.get(ThreadLocalRandom.current().nextInt(fighting.size()));

		return getPlugin().getServer().getPlayer(uuid);
	}

	public List<UUID> getByState(OITCPlayer.OITCState state) {
		return players.values().stream().filter(player -> player.getState() == state).map(OITCPlayer::getUuid).collect(Collectors.toList());
	}

    @Getter
    @RequiredArgsConstructor
    public class RespawnTask extends BukkitRunnable {

	    private final Player player;
	    private final OITCPlayer oitcPlayer;
        private int time = 5;

        @Override
        public void run() {

            if(oitcPlayer.getLives() == 0) {
                cancel();
                return;
            }

            if(time > 0) {
				player.sendMessage(ChatColor.YELLOW + "(Event) " + ChatColor.GRAY + "Respawning in " + time + "...");
			}

            if(time == 5) {
                getPlugin().getServer().getScheduler().runTask(getPlugin(), () -> {
                    PlayerUtil.reset(player);
                    getBukkitPlayers().forEach(member -> member.hidePlayer(player));
                });

                oitcPlayer.setState(OITCPlayer.OITCState.RESPAWNING);

            } else if(time <= 0) {
                player.sendMessage(ChatColor.YELLOW + "(Event) " + ChatColor.GRAY + "Respawning...");
                player.sendMessage(ChatColor.YELLOW + "(Event) " + ChatColor.RED.toString() + ChatColor.BOLD + oitcPlayer.getLives() + " " + (oitcPlayer.getLives() == 1 ? "LIFE" : "LIVES") + " REMAINING");


                getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), () -> {
                    giveRespawnItems(player, oitcPlayer);
                    player.teleport(getGameLocations().remove(ThreadLocalRandom.current().nextInt(getGameLocations().size())).toBukkitLocation());
                    getBukkitPlayers().forEach(member -> member.showPlayer(player));
                }, 2L);

                oitcPlayer.setState(OITCPlayer.OITCState.FIGHTING);

                cancel();
            }

            time--;

        }
    }

	/**
	 * To ensure that the fight doesn't go on forever and to
	 * let the players know how much time they have left.
	 */
	@Getter
	@RequiredArgsConstructor
	public class OITCGameTask extends BukkitRunnable {

		private int time = 303;

		@Override
		public void run() {
			// Make sure we don't get a fuck ton of NPEs

			if (time == 303) {
				PlayerUtil.sendMessage(ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + 3 + ChatColor.YELLOW + "...", getBukkitPlayers());
			} else if (time == 302) {
				PlayerUtil.sendMessage(ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + 2 + ChatColor.YELLOW + "...", getBukkitPlayers());
			} else if (time == 301) {
				PlayerUtil.sendMessage(ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + 1 + ChatColor.YELLOW + "...", getBukkitPlayers());
			} else if (time == 300) {
				PlayerUtil.sendMessage(ChatColor.GREEN + "The game has started, good luck!", getBukkitPlayers());

				for(OITCPlayer player : getPlayers().values()) {
					player.setScore(0);
					player.setLives(5);
					player.setState(OITCPlayer.OITCState.FIGHTING);
				}

				for(Player player : getBukkitPlayers()) {

					OITCPlayer oitcPlayer = getPlayer(player.getUniqueId());

					if(oitcPlayer != null) {
						teleportNextLocation(player);
						giveRespawnItems(player, oitcPlayer);
					}
				}

			} else if (time <= 0) {

				Player winner = getRandomPlayer();

				if(winner != null) {

					Profile winnerData = Practice.getInstance().getProfileManager().getProfileData(winner.getUniqueId());
					winnerData.setOitcWins(winnerData.getOitcWins() + 1);

					for (int i = 0; i <= 2; i++) {
						String announce = ChatColor.YELLOW + "(Event) " + ChatColor.GREEN.toString() + "Winner: " + winner.getName();
						Bukkit.broadcastMessage(announce);
					}
				}

                gameTask.cancel();
                end();
				cancel();
				return;
			}

			if (Arrays.asList(60, 50, 40, 30, 25, 20, 15, 10).contains(time)) {
				PlayerUtil.sendMessage(ChatColor.YELLOW + "The game ends in " + ChatColor.GREEN + time + ChatColor.YELLOW + "...", getBukkitPlayers());
			} else if (Arrays.asList(5, 4, 3, 2, 1).contains(time)) {
				PlayerUtil.sendMessage(ChatColor.YELLOW + "The game is ending in " + ChatColor.GREEN + time + ChatColor.YELLOW + "...", getBukkitPlayers());
			}

			time--;
		}
	}

	public List<OITCPlayer> sortedScores() {
		List<OITCPlayer> list = new ArrayList<>(this.players.values());
		list.sort(new SortComparator().reversed());
		return list;
	}

	private class SortComparator implements Comparator<OITCPlayer> {

		@Override public int compare(OITCPlayer p1, OITCPlayer p2) {
			return Integer.compare(p1.getScore(), p2.getScore());
		}
	}
}
