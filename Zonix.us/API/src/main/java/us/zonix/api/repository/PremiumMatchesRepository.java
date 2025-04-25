package us.zonix.api.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import us.zonix.api.model.PremiumMatches;

@Repository
public interface PremiumMatchesRepository extends PagingAndSortingRepository<PremiumMatches, Integer> {

    PremiumMatches findFirstByUuid(String uuid);

}
