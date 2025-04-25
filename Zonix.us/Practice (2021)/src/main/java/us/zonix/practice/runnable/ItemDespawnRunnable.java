package us.zonix.practice.runnable;

import java.util.Iterator;
import org.bukkit.entity.Item;
import us.zonix.practice.Practice;

public class ItemDespawnRunnable implements Runnable {
    private final Practice plugin = Practice.getInstance();

    @Override
    public void run() {
        Iterator<Item> it = this.plugin.getFfaManager().getItemTracker().keySet().iterator();

        while (it.hasNext()) {
            Item item = it.next();
            long l = this.plugin.getFfaManager().getItemTracker().get(item);
            if (l + 15000L < System.currentTimeMillis()) {
                item.remove();
                it.remove();
            }
        }
    }
}
