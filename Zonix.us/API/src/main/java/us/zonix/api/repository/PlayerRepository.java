package us.zonix.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import us.zonix.api.model.Player;

import java.util.List;

@Repository
public interface PlayerRepository extends CrudRepository<Player, Integer> {

    Integer countBy();

    Player findFirstByUuid(String uuid);

    List<Player> findByNameLike(String name);

    List<Player> findByName(String name);

    Player findFirstByIp(String ip);

    List<Player> findByIp(String ip);

    List<Player> findByRank(String rank);

    Player findFirstByEmailAddress(String email);

    Player findFirstByConfirmationId(String confirmation);

}
