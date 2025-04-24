package me.devkevin.repo;

import me.devkevin.model.PracticeProfile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by DevKevin
 * Project: WebAPI
 * Date: 23/02/2022 @ 3:38
 */
@Repository
public interface PracticeRepository extends CrudRepository<PracticeProfile, Integer> {

    PracticeProfile findFirstByPlayerId(int id);

}
