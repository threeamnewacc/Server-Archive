package us.zonix.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import us.zonix.api.model.MemeLog;

import java.util.List;

@Repository
public interface MemeRepository extends CrudRepository<MemeLog, Integer> {

    List<MemeLog> findByUuid(String uuid);

}
