package me.devkevin.repo;

import me.devkevin.model.Inventory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by DevKevin
 * Project: WebAPI
 * Date: 23/02/2022 @ 4:11
 */
@Repository
public interface InventoryRepository extends CrudRepository<Inventory, Integer> {

    Inventory findByMatchId(String matchId);

    Inventory findById(int id);

}
