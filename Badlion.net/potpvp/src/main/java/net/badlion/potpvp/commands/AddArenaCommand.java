package net.badlion.potpvp.commands;

import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.managers.ArenaManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class AddArenaCommand extends GCommandExecutor {

    public AddArenaCommand() {
        super(2); // 2 args minimum
    }

    @Override
    public void onGroupCommand(Command command, String label, final String[] args) {
	    if (args.length == 2 && args[0].equalsIgnoreCase("asd")) {
		    try {
			    ArenaManager.ArenaType arenaType = ArenaManager.ArenaType.valueOf(args[1]);
			    this.player.sendMessage(ChatColor.BLUE + "Available " + args[1] + " arenas: " + ArenaManager.getArenasAvailable(arenaType));
		    } catch (IllegalArgumentException e) {
			    this.player.sendMessage(ChatColor.RED + "Bad arena type specified");
		    }
		    return;
	    }

        String warp1 = "";
        String warp2 = "";
        if (args.length >= 3) {
            warp1 = args[2];
            if (args.length == 4) {
                warp2 = args[3];
            }
        }

        final String warp1Final = warp1;
        final String warp2Final = warp2;
        PotPvP.getInstance().getServer().getScheduler().runTaskAsynchronously(PotPvP.getInstance(), new Runnable() {
            @Override
            public void run() {
                ArenaManager.addArena(AddArenaCommand.this.player, args[0], args[1], warp1Final, warp2Final);
            }
        });
    }

    @Override
    public void usage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Command usage: /addarena [name] [types] [warp1] [warp2]");
    }


}
