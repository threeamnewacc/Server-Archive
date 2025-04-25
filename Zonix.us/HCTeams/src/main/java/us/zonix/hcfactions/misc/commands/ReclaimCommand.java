package us.zonix.hcfactions.misc.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.hcfactions.util.PluginCommand;
import us.zonix.hcfactions.util.command.Command;
import us.zonix.hcfactions.util.command.CommandArgs;
import us.zonix.core.profile.Profile;
import us.zonix.hcfactions.util.command.CommandArgs;

public class ReclaimCommand extends PluginCommand {

    @Command(name = "reclaim", aliases = {"rc", "claim"}, inGameOnly = true)
    public void onCommand(CommandArgs command) {
        this.runCommands(command.getPlayer());
    }

    private void runCommands(Player player) {

        us.zonix.hcfactions.profile.Profile profile = us.zonix.hcfactions.profile.Profile.getByUuid(player.getUniqueId());

        if(profile == null) {
            return;
        }

        if(profile.isReclaim()) {
            player.sendMessage(this.main.getLanguageConfig().getString("RECLAIM.NONE"));
            return;
        }

        String rankName = Profile.getByUuid(player.getUniqueId()) == null ? "DEFAULT" : Profile.getByUuid(player.getUniqueId()).getRank().getName().toUpperCase();

        if(!this.main.getConfig().contains("RECLAIM." + rankName)) {
            player.sendMessage(this.main.getLanguageConfig().getString("RECLAIM.NONE"));
            return;
        }

        for(String key : this.main.getConfig().getStringList("RECLAIM." + rankName)) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), key.replace("%PLAYER%", player.getName()));
        }

        Bukkit.broadcastMessage(this.main.getLanguageConfig().getString("RECLAIM.SUCCESS").replace("%PLAYER%", player.getName()));

        profile.setReclaim(true);

        new BukkitRunnable() {

            @Override
            public void run() {
                profile.save();
            }
        }.runTaskAsynchronously(this.main);

    }

}
