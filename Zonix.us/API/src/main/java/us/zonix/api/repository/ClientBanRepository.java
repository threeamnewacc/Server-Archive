package us.zonix.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import us.zonix.api.model.ClientBan;

@Repository
public interface ClientBanRepository extends CrudRepository<ClientBan, Integer> {

    ClientBan findByIp(String ip);

    ClientBan findByHwid(String hwid);

}
