package us.zonix.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import us.zonix.api.model.Punishment;

import java.util.List;
import java.util.UUID;

@Repository
public interface PunishmentRepository extends CrudRepository<Punishment, Integer> {

    Integer countByType(String type);

    Punishment findFirstById(int id);

    List<Punishment> findByUuid(String uuid);

    List<Punishment> findByAddedBy(String addedBy);

}
