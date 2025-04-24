// 
// Decompiled by Procyon v0.6.0
// 

package club.mineman.antigamingchair.commands.subcommands;

import java.beans.ConstructorProperties;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.core.api.request.RequestCallback;
import club.mineman.core.api.APIMessage;
import java.util.Iterator;
import org.bukkit.plugin.Plugin;
import club.mineman.core.util.finalutil.HttpUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import club.mineman.core.api.request.AbstractCallback;
import club.mineman.antigamingchair.request.banwave.AGCGetBanWaveRequest;
import club.mineman.core.CorePlugin;
import club.mineman.core.util.finalutil.CC;
import club.mineman.antigamingchair.event.player.PlayerBanWaveEvent;
import club.mineman.core.util.finalutil.StringUtil;
import org.bukkit.event.Event;
import club.mineman.antigamingchair.event.BanWaveEvent;
import org.bukkit.entity.Player;
import club.mineman.antigamingchair.AntiGamingChair;

public class BanWaveCommand implements SubCommand
{
    private final AntiGamingChair plugin;
    
    @Override
    public void execute(final Player player, final Player target, final String[] args) {
        if (args.length < 2) {
            return;
        }
        final String lowerCase = args[1].toLowerCase();
        switch (lowerCase) {
            case "start": {
                final BanWaveEvent banWaveEvent = new BanWaveEvent(player.getName());
                this.plugin.getServer().getPluginManager().callEvent((Event)banWaveEvent);
                break;
            }
            case "stop": {
                this.plugin.getBanWaveManager().setBanWaveStarted(false);
                break;
            }
            case "add": {
                if (args.length < 3) {
                    return;
                }
                final Player player2 = this.plugin.getServer().getPlayer(args[2]);
                if (player2 == null) {
                    player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, args[2]));
                    return;
                }
                final PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player2);
                if (!playerData.isBanWave()) {
                    playerData.setBanWave(true);
                    final PlayerBanWaveEvent banEvent = new PlayerBanWaveEvent(player2, "Manual");
                    this.plugin.getServer().getPluginManager().callEvent((Event)banEvent);
                    player.sendMessage(CC.L_PURPLE + "Added " + CC.D_PURPLE + player2.getName() + CC.L_PURPLE + " to the ban wave.");
                    break;
                }
                break;
            }
            case "list": {
                CorePlugin.getInstance().getRequestManager().sendRequest((APIMessage)new AGCGetBanWaveRequest(), (RequestCallback)new AbstractCallback("Error fetching the ban wave list.") {
                    public void callback(final JSONObject data) {
                        final JSONArray array = (JSONArray)data.get((Object)"data");
                        final StringBuilder list = new StringBuilder();
                        for (final Object object : array) {
                            final JSONObject jsonObject = (JSONObject)object;
                            final String name = (String)jsonObject.get((Object)"name");
                            list.append(name).append("\n");
                        }
                        BanWaveCommand.this.plugin.getServer().getScheduler().runTaskAsynchronously((Plugin)BanWaveCommand.this.plugin, () -> {
                            final Object val$player = player;
                            final String bin = HttpUtil.getHastebin(list.toString());
                            if (bin == null) {
                                player.sendMessage(CC.RED + "There was a problem uploading the ban wave list, check console for details.");
                            }
                            else {
                                player.sendMessage(CC.L_PURPLE + "Banwave list: http://www.hastebin.com/" + bin);
                            }
                        });
                    }
                });
                break;
            }
        }
    }
    
    @ConstructorProperties({ "plugin" })
    public BanWaveCommand(final AntiGamingChair plugin) {
        this.plugin = plugin;
    }
}
