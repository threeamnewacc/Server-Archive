package me.devkevin.landcore.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 23/01/2023 @ 17:55
 * SkinUtil / me.devkevin.landcore.utils / LandCore
 */
public class SkinUtil {

    public static void changeName(final Player p, final String newName) {
        for (final Player pl : Bukkit.getOnlinePlayers()) {
            if (p == pl) {
                continue;
            }
            final GameProfile gp = ((CraftPlayer) p).getProfile();
            try {
                final Field nameField = GameProfile.class.getDeclaredField("name");
                nameField.setAccessible(true);
                final Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(nameField, nameField.getModifiers() & 0xFFFFFFEF);
                nameField.set(gp, newName);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void setSkin(GameProfile profile) {
        profile.getProperties().put("textures", new Property("textures", "eyJ0aW1lc3RhbXAiOjE1NjQ0MzI5ODkyMDMsInByb2ZpbGVJZCI6IjY5OTEzMzU5NDVjZDRhNWJiNTYxYmM3OWYzNWU5NzQ0IiwicHJvZmlsZU5hbWUiOiJWZXJ6aWRlIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iY2NjMjk2Y2QxNzFjMTU4ZWVjOTNlYzAzYzVhNjVlYWRjNTM4MDdlMzQ3ZWRhMmEzMzRiNjcwMzQ1ODk3YTU4In0sIkNBUEUiOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9lN2RmZWExNmRjODNjOTdkZjAxYTEyZmFiYmQxMjE2MzU5YzBjZDBlYTQyZjk5OTliNmU5N2M1ODQ5NjNlOTgwIn19fQ==", "R8fsaMkLfOxWwza/f0tuO5+uOKQcZvty1S2UHO9m+rvRFkP8Q3X90peQyLOPU4oJF6KDeZWp+xQrTJxtuCPT7cQ1I9zlatLmY5w9titm0DqA3LF2YGEz3IX86iRn7jyDUJjJNNZDvH6725BMLnK8h+nBwUdhIvwVx9f3uePxPhk0dzw+m7f9eg1Y8lLxwDm4cb+7y+8wlqslE1xvEFjlaR7+kBsIq4J4HUfvXRxBkVnheTRAKsXT6D/d9oPy3+RXpOrQB8CxOExtlM3KKYZrMHRYi3HWRrs4Fq87fRM3QnOl5/E7bAbI50XI5rc4HLWka0AfcYSzEhT8sBlDujVzY3PwRDnGlhDiFxpgY4yfijjP2lNoD4muH7yJFA9K/VBgEc8lEwhWt+AXBTRRntN6VdHsJ7xCIYsbQ6wjjyE0DeVZ4HdyNV/B7bk4wVtq03GQK+l6KDppp3VAbMzV5nnkOJSj4jHxA8t8GuUY2ecSTD2u1o7+55puFSSXKlK2DiEAPpinRyGhVT6+I/6Ac7cUOVGn5omspTgQEk2i+NCGFnsmZ5JGadvbHPEpx5NGKNzwElQF2i9UnCRUSEqCgRQqqvvkJb8DXPZaeOYPsIA+zffBmdOx3EX9tAW4W6yeplBLWErFEW0D4DcgL3gEXrCQRFB9OUakUQXmxXh5/r9rI7E="));
    }
}
