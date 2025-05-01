package net.badlion.gberry.utils.tinyprotocol;

import java.util.ArrayList;
import java.util.List;

public class GameProfileScheduler {

    private static List<GameProfileHook> hooks = new ArrayList<>();

    public static void addHook(GameProfileHook hook) {
        GameProfileScheduler.hooks.add(hook);
    }

    public static List<GameProfileHook> getHooks() {
        return hooks;
    }

}
