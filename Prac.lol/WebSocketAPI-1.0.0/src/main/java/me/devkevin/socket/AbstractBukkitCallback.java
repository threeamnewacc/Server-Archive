package me.devkevin.socket;

import me.devkevin.api.Callback;
import me.devkevin.api.ErrorCallback;
import org.bukkit.Bukkit;

/**
 * Created by DevKevin
 * Project: WebSocketAPI
 * Date: 23/02/2022 @ 4:56
 */
public abstract class AbstractBukkitCallback implements Callback, ErrorCallback {
    @Override
    public void onError(String message) {
        Bukkit.getLogger().severe("[WebSocketAPI]: " + message);
    }
}
