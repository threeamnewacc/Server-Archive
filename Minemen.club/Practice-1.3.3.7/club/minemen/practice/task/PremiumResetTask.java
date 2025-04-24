// 
// Decompiled by Procyon v0.6.0
// 

package club.minemen.practice.task;

import club.minemen.core.api.callback.Callback;
import club.minemen.core.api.request.Request;
import com.google.gson.JsonElement;
import club.minemen.core.api.abstr.AbstractBukkitCallback;
import club.minemen.practice.request.PremiumRequest;
import club.minemen.core.CorePlugin;
import club.minemen.practice.Practice;
import java.util.TimerTask;

public class PremiumResetTask extends TimerTask
{
    private final Practice plugin;
    
    @Override
    public void run() {
        CorePlugin.getInstance().getRequestProcessor().sendRequestAsync((Request)new PremiumRequest("reset", "", 0), (Callback)new AbstractBukkitCallback() {
            public void callback(final JsonElement jsonElement) {
                PremiumResetTask.this.plugin.getLogger().info("Successfully ran Practice Reset");
            }
        });
    }
    
    public PremiumResetTask() {
        this.plugin = Practice.getInstance();
    }
}
