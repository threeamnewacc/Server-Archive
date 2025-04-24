package me.devkevin.util;

import me.devkevin.model.Player;
import me.devkevin.repo.PlayerRepository;

import java.util.List;
import java.util.UUID;

/**
 * Util class containing wrapper methods to fetch Player Data. <p></p>
 *
 * Created by DevKevin
 * Project: WebAPI
 * Date: 22/02/2022 @ 20:27
 */
public class PlayerUtil {

    public static Player getByName(String name, PlayerRepository playerRepository) {
        if (name.length() > 16) {
            UUID uuid = UUID.fromString(name);

            return playerRepository.findFirstByUniqueId(uuid.toString());
        }

        List<Player> players = playerRepository.findByName(name);
        if (players.size() == 1) {
            return players.get(0);
        }

        try {
            UUID uuid = HttpUtil.getUniqueIdFromName(name);
            if (uuid == null) {
                return null;
            }

            return playerRepository.findFirstByUniqueId(uuid.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
