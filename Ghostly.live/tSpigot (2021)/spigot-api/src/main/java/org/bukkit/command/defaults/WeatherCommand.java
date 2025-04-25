package org.bukkit.command.defaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

import com.google.common.collect.ImmutableList;

@Deprecated
public class WeatherCommand extends VanillaCommand {
    private static final List<String> WEATHER_TYPES = ImmutableList.of("clear", "rain", "thunder");

    public WeatherCommand() {
        super("weather");
        
        this.description = "Changes the weather";
        this.usageMessage = "/weather <clear/rain/thunder> [duration in seconds]";
        
        this.setPermission("bukkit.command.weather");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) return true;
        
        if (args.length == 0) {
            sender.sendMessage("&cUsage: " + usageMessage);
            return false;
        }

        int duration = (300 + new Random().nextInt(600)) * 20;
        if (args.length >= 2) {
            duration = getInteger(sender, args[1], 1, 1000000) * 20;
        }

        World world = Bukkit.getWorlds().get(0);

        world.setWeatherDuration(duration);
        world.setThunderDuration(duration);

        if ("clear".equalsIgnoreCase(args[0])) {
            world.setStorm(false);
            world.setThundering(false);
            Command.broadcastCommandMessage(sender, "&aChanged weather to clear for &f" + (duration / 20) + "&a seconds.");
        } else if ("rain".equalsIgnoreCase(args[0])) {
            world.setStorm(true);
            world.setThundering(false);
            Command.broadcastCommandMessage(sender, "&aChanged weather to rainy for &f" + (duration / 20) + "&a seconds.");
        } else if ("thunder".equalsIgnoreCase(args[0])) {
            world.setStorm(true);
            world.setThundering(true);
            Command.broadcastCommandMessage(sender, "&aChanged weather to thundering &f" + (duration / 20) + "&a seconds.");
        }

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], WEATHER_TYPES, new ArrayList<String>(WEATHER_TYPES.size()));
        }

        return ImmutableList.of();
    }
}
