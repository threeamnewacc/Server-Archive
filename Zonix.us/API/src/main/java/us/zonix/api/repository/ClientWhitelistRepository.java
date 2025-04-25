package us.zonix.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import us.zonix.api.model.ClientWhitelist;

@Repository
public interface ClientWhitelistRepository extends CrudRepository<ClientWhitelist, Integer> {

    ClientWhitelist findByIp(String ip);

    ClientWhitelist findByUuid(String uuid);

}
