package me.devkevin.repo;

import me.devkevin.model.Player;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by DevKevin
 * Project: WebAPI
 * Date: 22/02/2022 @ 20:28
 */
@Repository
public interface PlayerRepository extends CrudRepository<Player, Integer> {

    List<Player> findByIpAddress(String ipAddress);

    List<Player> findByName(String name);

    Player findFirstByUniqueId(String uniqueId);

    List<Player> findAllByPlayerIdIn(int[] ids);

    Player findFirstByPlayerId(int id);

}
