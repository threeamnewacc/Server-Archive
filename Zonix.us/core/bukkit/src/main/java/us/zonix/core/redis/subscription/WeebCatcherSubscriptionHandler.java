package us.zonix.core.redis.subscription;

import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import us.zonix.core.CorePlugin;
import us.zonix.core.punishment.PunishmentType;
import us.zonix.core.punishment.helpers.PunishmentHelper;
import us.zonix.core.shared.redis.subscription.JedisSubscriptionHandler;

public class WeebCatcherSubscriptionHandler implements JedisSubscriptionHandler<JsonObject> {

    @Override
    public void handleMessage(JsonObject object) {

        String message = object.get("message").getAsString();

        switch (message) {
            case "ban":
                String name = object.get("name").getAsString();
                Player target = Bukkit.getPlayer(name);

                if(target != null) {
                    new BukkitRunnable() {
                        public void run() {
                            new PunishmentHelper(Bukkit.getConsoleSender(), name, PunishmentType.BAN, "[ZAC] Unfair Advantage", (long) -1, false, false);
                        }
                    }.runTaskAsynchronously(CorePlugin.getInstance());
                }
                break;
        }
    }

}