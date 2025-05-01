package net.badlion.gberry.listeners;

import net.badlion.common.libraries.IPCommon;
import net.badlion.gberry.events.AsyncDelayedPlayerJoinEvent;
import net.badlion.gberry.events.AsyncPlayerJoinEvent;
import net.badlion.gberry.events.AsyncPlayerQuitEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.badlion.gberry.Gberry;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.SQLException;

public class PlayerJoinLeaveListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage("");
	}

	@EventHandler(priority= EventPriority.LAST, ignoreCancelled=true)
	public void onPlayerJoinLast(final PlayerJoinEvent event) {
		if (Gberry.enableAsyncLoginEvent) {
			if (event.getPlayer().isOnline()) {
				new BukkitRunnable() {

					@Override
					public void run() {
						Connection connection = null;

						try {
							connection = Gberry.getConnection();
							final AsyncPlayerJoinEvent ev = new AsyncPlayerJoinEvent(connection, event.getPlayer().getUniqueId(), event.getPlayer().getName(), IPCommon.toLongIP(event.getPlayer().getAddress().getAddress().getAddress()));
							Gberry.plugin.getServer().getPluginManager().callEvent(ev);

							// Only attach back to the main thread if we have runnables to work with
							if (ev.getRunnables().size() > 0) {
								Gberry.plugin.getServer().getScheduler().runTask(Gberry.plugin, new Runnable() {
									@Override
									public void run() {
										// Call our runnables
										for (Runnable r : ev.getRunnables()) {
											try {
												r.run();
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								});
							}
						} catch (SQLException e) {
							e.printStackTrace();
						} finally {
							if (connection != null) {
								try {
									connection.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						}
					}

				}.runTaskAsynchronously(Gberry.plugin);
			}
		}

		if (Gberry.enableAsyncDelayedLoginEvent) {
			if (event.getPlayer().isOnline()) {
				new BukkitRunnable() {

					@Override
					public void run() {
						Connection connection = null;

						try {
							connection = Gberry.getConnection();
							final AsyncDelayedPlayerJoinEvent ev = new AsyncDelayedPlayerJoinEvent(connection, event.getPlayer().getUniqueId(), event.getPlayer().getName());
							Gberry.plugin.getServer().getPluginManager().callEvent(ev);

							// Only attach back to the main thread if we have runnables to work with
							if (ev.getRunnables().size() > 0) {
								Gberry.plugin.getServer().getScheduler().runTask(Gberry.plugin, new Runnable() {
									@Override
									public void run() {
										// Call our runnables
										for (Runnable r : ev.getRunnables()) {
											try {
												r.run();
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								});
							}
						} catch (SQLException e) {
							e.printStackTrace();
						} finally {
							if (connection != null) {
								try {
									connection.close();
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						}
					}

				}.runTaskLaterAsynchronously(Gberry.plugin, 60);
			}
		}
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		event.setQuitMessage("");
	}

	@EventHandler(priority=EventPriority.FIRST)
	public void onPlayerQuit(final PlayerQuitEvent event) {
		if (Gberry.enableAsyncQuitEvent) {
			new BukkitRunnable() {

				@Override
				public void run() {
					Connection connection = null;

					try {
						connection = Gberry.getConnection();
						final AsyncPlayerQuitEvent ev = new AsyncPlayerQuitEvent(connection, event.getPlayer().getUniqueId(), event.getPlayer().getName(), IPCommon.toLongIP(event.getPlayer().getAddress().getAddress().getAddress()));
						Gberry.plugin.getServer().getPluginManager().callEvent(ev);

						// Only attach back to the main thread if we have runnables to work with
						if (ev.getRunnables().size() > 0) {
							Gberry.plugin.getServer().getScheduler().runTask(Gberry.plugin, new Runnable() {
								@Override
								public void run() {
									// Call our runnables
									for (Runnable r : ev.getRunnables()) {
										try {
											r.run();
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
							});
						}
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						if (connection != null) {
							try {
								connection.close();
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
					}
				}

			}.runTaskAsynchronously(Gberry.plugin);
		}
	}

}
