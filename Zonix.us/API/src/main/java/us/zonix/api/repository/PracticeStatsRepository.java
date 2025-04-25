package us.zonix.api.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import us.zonix.api.model.PracticeStats;

@Repository
public interface PracticeStatsRepository extends PagingAndSortingRepository<PracticeStats, Integer> {

    PracticeStats findFirstByUuid(String uuid);

}
