package us.zonix.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import us.zonix.api.model.ClientCosmetics;

@Repository
public interface ClientCosmeticsRepository extends CrudRepository<ClientCosmetics, Integer> {

    ClientCosmetics findFirstByUuid(String uuid);

}
