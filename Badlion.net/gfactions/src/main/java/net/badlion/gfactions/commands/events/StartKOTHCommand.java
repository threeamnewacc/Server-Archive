package net.badlion.gfactions.commands.events;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import net.badlion.gberry.Gberry;
import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.events.koth.KOTH;
import net.badlion.gfactions.managers.TicketManager;
import net.badlion.gfactions.events.koth.StartKOTHTask;
import net.badlion.gfactions.tasks.tower.TowerBuilderTask;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StartKOTHCommand implements CommandExecutor {
	
    private GFactions plugin;
    private int x = -38;
    private int z = -38;
	
	public StartKOTHCommand(GFactions plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
			// Debug stuff
			if (args.length == 1 && args[0].equals("dragon") && player.isOp()) {
				//this.plugin.getServer().getWorld("world").spawnEntity(new Location(this.plugin.getServer().getWorld("world"), 0, 100, 0), EntityType.ENDER_DRAGON);
				//EntityManager manager = RemoteEntities.createManager(this.plugin);
				//RemoteEntity entity = manager.createEntity(RemoteEntityType.EnderDragon, new Location(this.plugin.getServer().getWorld("world"), 0, 200, 0), true);
				//entity.setStationary(false);
				//RemoteEnderDragon dragon = (RemoteEnderDragon) entity;
				//dragon.shouldDestroyBlocks(false);
				//dragon.shouldNormallyFly(true);
				//dragon.move(new Location(this.plugin.getServer().getWorld("world"), 50, 200, 50));
				return true;
			} else if (args.length == 1 && args[0].equals("kill") && player.isOp()) {
				List<Entity> entities = this.plugin.getServer().getWorld("world").getEntities();
				for (Entity entity : entities) {
					if (entity instanceof EnderDragon) {
						entity.remove();
					}
				}
                return true;
			} else if (args.length == 1 && args[0].equals("note") && player.isOp()) {
				//player.getInventory().addItem(TicketManager.createNoteFromItemStack(player.getItemInHand()));
                return true;
			} else if (args.length == 1 && args[0].equals("item") && player.isOp()) {
				player.getInventory().addItem(TicketManager.toItemStackFromNote(player.getItemInHand()));
                return true;
			} else if (args.length == 1 && args[0].equals("dungeon") && player.isOp()) {
				this.plugin.getDungeonManager().createNewDungeon();
                return true;
			} else if (args.length == 1 && args[0].equals("unload") && player.isOp()) {
				this.plugin.getServer().unloadWorld("dungeon1", false);
                return true;
			} else if (args.length == 1 && args[0].equals("dungeontp") && player.isOp()) {
				player.teleport(this.plugin.getDungeonManager().getSpawnLocation());
                return true;
			} else if (args.length == 1 && args[0].equals("tower") && player.isOp()) {
				this.plugin.getCheckTimeTask().setxTowerLocation(107);
				this.plugin.getCheckTimeTask().setyTowerLocation(71);
				this.plugin.getCheckTimeTask().setzTowerLocation(69);
				new TowerBuilderTask(this.plugin).runTask(this.plugin);
			} else if (args.length == 1 && args[0].equals("despawntower") && player.isOp()) {
				this.plugin.getTower().despawn();
			} else if (args.length == 1 && args[0].equals("sword") && player.isOp()) {
                for (ItemStack item : this.plugin.getItemGenerator().generateGodWeapon(2)) {
                    player.getInventory().addItem(item);
                }
                for (ItemStack item : this.plugin.getItemGenerator().generateGodArmor(2)) {
                    player.getInventory().addItem(item);
                }
                return true;
            } else if (args.length == 1 && args[0].equals("where") && player.isOp()) {
				player.sendMessage(this.plugin.getgGuardPlugin().getProtectedRegionName(player.getLocation(), this.plugin.getgGuardPlugin().getProtectedRegions()));
                return true;
            } else if (args.length == 1 && args[0].equals("regen") && player.isOp()) {
                //this.plugin.regenerateAreaNearSpawn();
                player.sendMessage("Done");
                return true;
            } else if (args.length == 1 && args[0].equals("end") && player.isOp()) {
                player.teleport(new Location(this.plugin.getServer().getWorld("world_the_end"), 0, 200, 0));
                return true;
            } else if (args.length == 1 && args[0].equals("west") && player.isOp()) {
                this.createRoads();
                return true;
            /*} else if (args.length == 1 && args[0].equals("lotto") && player.isOp()) {
                new LottoDrawingTask(this.plugin).runTask(this.plugin);
                return true;*/
            } else if (args.length == 1 && args[0].equals("fkqazzy") && player.isOp()) {
                /*for (int x = 27; x <= 38; x++) {
                    for (int z = -33; z <= -22; z++) {
                        this.plugin.getServer().getWorld("world").regenerateChunk(x, z);
                    }
                } */
                return true;
            } else if (args.length == 1 && args[0].equals("regenchunk") && player.isOp()) {
                player.getWorld().regenerateChunk(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ());
            } else if (args.length == 1 && args[0].equals("fakevote")) {
                Vote v = new Vote();
                v.setUsername(player.getName());
                this.plugin.getServer().getPluginManager().callEvent(new VotifierEvent(v));
                return true;
            } else if (args.length == 1 && args[0].equals("spawner")) {
				ItemStack item = new ItemStack(Material.MOB_SPAWNER, 1);

				List<String> lore = new ArrayList<String>();

				String loreString = EntityType.SKELETON.toString();
				loreString = loreString.substring(0, 1).toUpperCase() + loreString.substring(1).toLowerCase();
				loreString = loreString + " Spawner";
				lore.add(loreString);

				ItemMeta meta = item.getItemMeta();
				meta.setLore(lore);
				item.setItemMeta(meta);
				player.getInventory().addItem(item);
                return true;
			} else if (args.length == 1 && args[0].equals("bloodbowl")) {
				this.plugin.getBloodBowlManager().startBloodBowl();
				return true;
			} else if (args.length == 1 && args[0].equals("unclaim")) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // Stop
                        if (StartKOTHCommand.this.x >= 38) {
                            this.cancel();
                            return;
                        }

                        // End of a row
                        if (StartKOTHCommand.this.z > 37) {
                            StartKOTHCommand.this.z = -38;
                            StartKOTHCommand.this.x++;
                            return;
                        }

                        if (StartKOTHCommand.this.z >= -5 && StartKOTHCommand.this.z <= 4 && StartKOTHCommand.this.x >= -5 && StartKOTHCommand.this.x <= 4) {
                            // Do nothing
                        } else {
                            Player p = Bukkit.getPlayer("MasterGberry");
                            if (p == null) {
                                return;
                            } else {
                                p.sendMessage(ChatColor.GREEN + "CLAIMED CHUNK " + StartKOTHCommand.this.x + " " + StartKOTHCommand.this.z);
                                p.teleport(new Location(Bukkit.getWorld("world"), (StartKOTHCommand.this.x * 16) + 8, 100, (StartKOTHCommand.this.z * 16) + 8));
                            }
                        }

                        // Move along the Z Axis
                        StartKOTHCommand.this.z++;
                    }
                }.runTaskTimer(this.plugin, 0, 2);
                return true;
            } else if (args.length == 2 && args[0].equals("loot")) {
                final ArrayList<ItemStack> items = new ArrayList<>();
                items.addAll(plugin.getItemGenerator().generateRandomSuperRareItem(3));
                items.addAll(plugin.getItemGenerator().generateRandomRareItem(4));
                items.addAll(plugin.getItemGenerator().generateRandomCommonItem(3));

                this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                    @Override
                    public void run() {
                        UUID uuid = Gberry.getOfflineUUID(args[1]);
                        if (uuid == null) {
                            return;
                        }

                        //plugin.getAuction().insertHeldAuctionItems(uuid.toString(), items); TODO
                    }
                });

                return true;
            }

            // startkoth <length> <hill_switches>
			if (args.length != 1) {
				return false;
			} else {
				//List<String> koths = (List<String>) this.plugin.getConfig().getList("gfactions.koth.koth_names");
				//String kothName = koths.get(this.plugin.generateRandomInt(0, koths.size() - 1));
                String kothName = args[0];

				int x = this.plugin.getConfig().getInt("gfactions.koth." + kothName + ".capzone.x_min");
				int y = this.plugin.getConfig().getInt("gfactions.koth." + kothName + ".capzone.y_min");
				int z = this.plugin.getConfig().getInt("gfactions.koth." + kothName + ".capzone.z_min");

				this.plugin.setKoth(new KOTH(this.plugin, 1800, kothName)); // Length: 1800 seconds (30 minutes)

				Gberry.broadcastMessage(ChatColor.GOLD + "New KOTH starting around " + ChatColor.GREEN + x + ", " + y + ", " + z + ChatColor.GOLD + "!!!");
                new StartKOTHTask(this.plugin, this.plugin.getKoth()).runTask(this.plugin);
            }
		}
		return true;
	}

    private void createRoads() {
        World world = Bukkit.getWorld("world");
        int i = 0;
        int hardy = 68;
        for (int x = -68; x > -10050; x -= 1) { // TODO: SHRINK THIS DOWN FOR TESTING
            for (int y = hardy; y <= hardy + 9; y++) {
                for (int z = -3; z <= 2; z++) {
                    // Wood edges
                    if (y == hardy && (z == -3 || z == 2)) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.LOG);
                        block.setData((byte)9);
                        continue;
                    } else if (y == hardy && z == -2) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.SPRUCE_WOOD_STAIRS);
                        block.setData((byte)3);
                        continue;
                    } else if (y == hardy && z == 1) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.SPRUCE_WOOD_STAIRS);
                        block.setData((byte)2);
                        continue;
                    } else if (y == hardy) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.WOOD_STEP);
                        block.setData((byte)0);
                        continue;
                    }

                    // 2nd level
                    if (y == hardy + 1 && (z == -3 || z == 2) && (i < 3 || i > 5)) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.WOOD_STEP);
                        block.setData((byte)0);
                        continue;
                    } else if (y == hardy + 1 && (z == -3 || z == 2) && i == 3) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.SPRUCE_WOOD_STAIRS);
                        block.setData((byte)1);
                        continue;
                    } else if (y == hardy + 1 && (z == -3 || z == 2) && i == 4) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.WOOD_STEP);
                        block.setData((byte)8);
                        continue;
                    } else if (y == hardy + 1 && (z == -3 || z == 2) && i == 5) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.SPRUCE_WOOD_STAIRS);
                        block.setData((byte)0);
                        continue;
                    } else if (y == hardy + 1) {
                        world.getBlockAt(x, y, z).setType(Material.AIR);
                        continue;
                    }

                    // 3rd lvl
                    if (y == hardy + 2 && (z == -3 || z == 2) && i == 4) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.WOOD_STEP);
                        block.setData((byte)8);
                        continue;
                    } else if (y == hardy + 2) {
                        world.getBlockAt(x, y, z).setType(Material.AIR);
                        continue;
                    }

                    // 4th level
                    if (y == hardy + 3 && (z == -3 || z == 2) && i == 4) {
                        world.getBlockAt(x, y, z).setType(Material.GLOWSTONE);
                        continue;
                    }  else if (y == hardy + 3) {
                        world.getBlockAt(x, y, z).setType(Material.AIR);
                        continue;
                    }

                    // 5th level
                    if (y == hardy + 4 && (z == -3 || z == 2) && i == 4) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.WOOD_STEP);
                        block.setData((byte)0);
                        continue;
                    } else if (y == hardy + 4) {
                        world.getBlockAt(x, y, z).setType(Material.AIR);
                        continue;
                    }

                    // Everything else is AIR
                    world.getBlockAt(x, y, z).setType(Material.AIR);
                }
            }

            ++i;
            if (i == 9) {
                i = 0;
            }
        }

        for (int x = 67; x < 10050; x += 1) { // TODO: SHRINK THIS DOWN FOR TESTING
            for (int y = hardy; y <= hardy + 9; y++) {
                for (int z = -3; z <= 2; z++) {
                    // Wood edges
                    if (y == hardy && (z == -3 || z == 2)) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.LOG);
                        block.setData((byte)9);
                        continue;
                    } else if (y == hardy && z == -2) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.SPRUCE_WOOD_STAIRS);
                        block.setData((byte)3);
                        continue;
                    } else if (y == hardy && z == 1) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.SPRUCE_WOOD_STAIRS);
                        block.setData((byte)2);
                        continue;
                    } else if (y == hardy) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.WOOD_STEP);
                        block.setData((byte)0);
                        continue;
                    }

                    // 2nd level
                    if (y == hardy + 1 && (z == -3 || z == 2) && (i < 3 || i > 5)) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.WOOD_STEP);
                        block.setData((byte)0);
                        continue;
                    } else if (y == hardy + 1 && (z == -3 || z == 2) && i == 3) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.SPRUCE_WOOD_STAIRS);
                        block.setData((byte)0);
                        continue;
                    } else if (y == hardy + 1 && (z == -3 || z == 2) && i == 4) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.WOOD_STEP);
                        block.setData((byte)8);
                        continue;
                    } else if (y == hardy + 1 && (z == -3 || z == 2) && i == 5) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.SPRUCE_WOOD_STAIRS);
                        block.setData((byte)1);
                        continue;
                    } else if (y == hardy + 1) {
                        world.getBlockAt(x, y, z).setType(Material.AIR);
                        continue;
                    }

                    // 3rd lvl
                    if (y == hardy + 2 && (z == -3 || z == 2) && i == 4) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.WOOD_STEP);
                        block.setData((byte)8);
                        continue;
                    } else if (y == hardy + 2) {
                        world.getBlockAt(x, y, z).setType(Material.AIR);
                        continue;
                    }

                    // 4th level
                    if (y == hardy + 3 && (z == -3 || z == 2) && i == 4) {
                        world.getBlockAt(x, y, z).setType(Material.GLOWSTONE);
                        continue;
                    }  else if (y == hardy + 3) {
                        world.getBlockAt(x, y, z).setType(Material.AIR);
                        continue;
                    }

                    // 5th level
                    if (y == hardy + 4 && (z == -3 || z == 2) && i == 4) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.WOOD_STEP);
                        block.setData((byte)0);
                        continue;
                    } else if (y == hardy + 4) {
                        world.getBlockAt(x, y, z).setType(Material.AIR);
                        continue;
                    }

                    // Everything else is AIR
                    world.getBlockAt(x, y, z).setType(Material.AIR);
                }
            }

            ++i;
            if (i == 9) {
                i = 0;
            }
        }

        for (int z = -68; z > -10050; z -= 1) { // TODO: SHRINK THIS DOWN FOR TESTING
            for (int y = hardy; y <= hardy + 9; y++) {
                for (int x = -3; x <= 2; x++) {
                    // Wood edges
                    if (y == hardy && (x == -3 || x == 2)) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.LOG);
                        block.setData((byte)9);
                        continue;
                    } else if (y == hardy && x == -2) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.SPRUCE_WOOD_STAIRS);
                        block.setData((byte)1);
                        continue;
                    } else if (y == hardy && x == 1) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.SPRUCE_WOOD_STAIRS);
                        block.setData((byte)0);
                        continue;
                    } else if (y == hardy) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.WOOD_STEP);
                        block.setData((byte)0);
                        continue;
                    }

                    // 2nd level
                    if (y == hardy + 1 && (x == -3 || x == 2) && (i < 3 || i > 5)) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.WOOD_STEP);
                        block.setData((byte)0);
                        continue;
                    } else if (y == hardy + 1 && (x == -3 || x == 2) && i == 3) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.SPRUCE_WOOD_STAIRS);
                        block.setData((byte)3);
                        continue;
                    } else if (y == hardy + 1 && (x == -3 || x == 2) && i == 4) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.WOOD_STEP);
                        block.setData((byte)8);
                        continue;
                    } else if (y == hardy + 1 && (x == -3 || x == 2) && i == 5) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.SPRUCE_WOOD_STAIRS);
                        block.setData((byte)2);
                        continue;
                    } else if (y == hardy + 1) {
                        world.getBlockAt(x, y, z).setType(Material.AIR);
                        continue;
                    }

                    // 3rd lvl
                    if (y == hardy + 2 && (x == -3 || x == 2) && i == 4) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.WOOD_STEP);
                        block.setData((byte)8);
                        continue;
                    } else if (y == hardy + 2) {
                        world.getBlockAt(x, y, z).setType(Material.AIR);
                        continue;
                    }

                    // 4th level
                    if (y == hardy + 3 && (x == -3 || x == 2) && i == 4) {
                        world.getBlockAt(x, y, z).setType(Material.GLOWSTONE);
                        continue;
                    }  else if (y == hardy + 3) {
                        world.getBlockAt(x, y, z).setType(Material.AIR);
                        continue;
                    }

                    // 5th level
                    if (y == hardy + 4 && (x == -3 || x == 2) && i == 4) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.WOOD_STEP);
                        block.setData((byte)0);
                        continue;
                    } else if (y == hardy + 4) {
                        world.getBlockAt(x, y, z).setType(Material.AIR);
                        continue;
                    }

                    // Everything else is AIR
                    world.getBlockAt(x, y, z).setType(Material.AIR);
                }
            }

            ++i;
            if (i == 9) {
                i = 0;
            }
        }

        for (int z = 67; z < 10050; z += 1) { // TODO: SHRINK THIS DOWN FOR TESTING
            for (int y = hardy; y <= hardy + 9; y++) {
                for (int x = -3; x <= 2; x++) {
                    // Wood edges
                    if (y == hardy && (x == -3 || x == 2)) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.LOG);
                        block.setData((byte)9);
                        continue;
                    } else if (y == hardy && x == -2) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.SPRUCE_WOOD_STAIRS);
                        block.setData((byte)1);
                        continue;
                    } else if (y == hardy && x == 1) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.SPRUCE_WOOD_STAIRS);
                        block.setData((byte)0);
                        continue;
                    } else if (y == hardy) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.WOOD_STEP);
                        block.setData((byte)0);
                        continue;
                    }

                    // 2nd level
                    if (y == hardy + 1 && (x == -3 || x == 2) && (i < 3 || i > 5)) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.WOOD_STEP);
                        block.setData((byte)0);
                        continue;
                    } else if (y == hardy + 1 && (x == -3 || x == 2) && i == 3) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.SPRUCE_WOOD_STAIRS);
                        block.setData((byte)2);
                        continue;
                    } else if (y == hardy + 1 && (x == -3 || x == 2) && i == 4) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.WOOD_STEP);
                        block.setData((byte)8);
                        continue;
                    } else if (y == hardy + 1 && (x == -3 || x == 2) && i == 5) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.SPRUCE_WOOD_STAIRS);
                        block.setData((byte)3);
                        continue;
                    } else if (y == hardy + 1) {
                        world.getBlockAt(x, y, z).setType(Material.AIR);
                        continue;
                    }

                    // 3rd lvl
                    if (y == hardy + 2 && (x == -3 || x == 2) && i == 4) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.WOOD_STEP);
                        block.setData((byte)8);
                        continue;
                    } else if (y == hardy + 2) {
                        world.getBlockAt(x, y, z).setType(Material.AIR);
                        continue;
                    }

                    // 4th level
                    if (y == hardy + 3 && (x == -3 || x == 2) && i == 4) {
                        world.getBlockAt(x, y, z).setType(Material.GLOWSTONE);
                        continue;
                    }  else if (y == hardy + 3) {
                        world.getBlockAt(x, y, z).setType(Material.AIR);
                        continue;
                    }

                    // 5th level
                    if (y == hardy + 4 && (x == -3 || x == 2) && i == 4) {
                        Block block = world.getBlockAt(x, y, z);
                        block.setType(Material.WOOD_STEP);
                        block.setData((byte)0);
                        continue;
                    } else if (y == hardy + 4) {
                        world.getBlockAt(x, y, z).setType(Material.AIR);
                        continue;
                    }

                    // Everything else is AIR
                    world.getBlockAt(x, y, z).setType(Material.AIR);
                }
            }

            ++i;
            if (i == 9) {
                i = 0;
            }
        }
    }

}
