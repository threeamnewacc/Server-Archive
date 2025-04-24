package me.devkevin.landcore.utils.menu;

import lombok.Getter;
import org.bukkit.Sound;

public enum ButtonSound {

    CLICK(Sound.CLICK),//Sound.CLICK stari
    SUCCESS(Sound.SUCCESSFUL_HIT),
    FAIL(Sound.DIG_GRASS);

    @Getter private Sound sound;

    ButtonSound(Sound sound) {
        this.sound = sound;
    }
}