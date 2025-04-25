package us.zonix.api.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import us.zonix.api.model.PracticeMatch;

import java.util.List;

@Repository
public interface PracticeMatchRepository extends CrudRepository<PracticeMatch, Integer> {

    List<PracticeMatch> findTop10ByWinnerUuidOrLoserUuidOrderByTimestampDesc(String uuid1, String uuid2);

}
