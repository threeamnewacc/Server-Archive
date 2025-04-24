package me.devkevin.landcore.utils;

import me.devkevin.landcore.utils.message.CC;
import net.md_5.bungee.api.chat.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 20/01/2023 @ 12:20
 * Clickable / land.pvp.core.utils / LandCore
 */
public class Clickable {

    private List<TextComponent> components = new ArrayList<>();

    public TextComponent add(String msg, String hoverMsg, String clickString) {
        TextComponent message = new TextComponent(CC.translate(msg));
        if (hoverMsg != null) {
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(CC.translate(hoverMsg)).create()));
        }
        if (clickString != null) {
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickString));
        }
        components.add(message);
        return message;
    }

    public void add(String message) {
        components.add(new TextComponent(message));
    }

    public void sendToPlayer(Player player) {
        player.spigot().sendMessage((BaseComponent[])asComponents());
    }

    public TextComponent[] asComponents() {
        return components.toArray(new TextComponent[0]);
    }
}
