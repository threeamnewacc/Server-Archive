package me.devkevin.repo;

import me.devkevin.model.Match;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by DevKevin
 * Project: WebAPI
 * Date: 23/02/2022 @ 3:36
 */
@Repository
public interface MatchesRepository extends CrudRepository<Match, String> {

    List<Match> findByWinner(int winner);

    List<Match> findByLoser(int winner);

    Match findById(int id);

}
