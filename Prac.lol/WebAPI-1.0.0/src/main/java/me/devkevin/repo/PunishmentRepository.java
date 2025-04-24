package me.devkevin.repo;

import me.devkevin.model.Punishment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by DevKevin
 * Project: WebAPI
 * Date: 23/02/2022 @ 3:39
 */
@Repository
public interface PunishmentRepository extends CrudRepository<Punishment, Integer> {

    Punishment findFirstByPlayerIdAndTypeContainingOrderByTimestampDesc(int id, String reason);

    List<Punishment> findByPlayerId(int id);

    Long countByPlayerId(int id);

}
