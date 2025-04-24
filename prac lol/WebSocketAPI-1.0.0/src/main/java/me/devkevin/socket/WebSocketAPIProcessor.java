package me.devkevin.socket;

import me.devkevin.WebSocketAPI;
import me.devkevin.api.AbstractRequestProcessor;

/**
 * Created by DevKevin
 * Project: WebSocketAPI
 * Date: 23/02/2022 @ 4:48
 */
public class WebSocketAPIProcessor extends AbstractRequestProcessor {
    private final WebSocketAPI plugin;

    public WebSocketAPIProcessor(WebSocketAPI plugin, String apiUrl, String apiKey) {
        super(apiUrl, apiKey);

        this.plugin = plugin;
    }

    @Override
    public boolean shouldSend() {
        return !this.plugin.getServer().isPrimaryThread();
    }

    @Override
    public void runTaskAsynchronously(Runnable runnable) {
        if (this.plugin.getServer().isPrimaryThread()) {
            runnable.run();
        } else {
            this.plugin.getServer().getScheduler().runTask(this.plugin, runnable);
        }
    }

    @Override
    public void runTask(Runnable runnable) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, runnable);
    }
}
