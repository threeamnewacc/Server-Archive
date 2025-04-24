package me.devkevin.controller;

import me.devkevin.model.Inventory;
import me.devkevin.model.Match;
import me.devkevin.repo.InventoryRepository;
import me.devkevin.repo.MatchesRepository;
import me.devkevin.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by DevKevin
 * Project: WebAPI
 * Date: 23/02/2022 @ 4:10
 */
@RestController
@RequestMapping("/api/{key}/matches")
@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
public class MatchController {

    @Autowired private InventoryRepository inventoryRepository;
    @Autowired private MatchesRepository matchesRepository;

    @RequestMapping("/insert/inventory")
    public ResponseEntity<String> insertInventoryData(HttpServletRequest request,
                                                      @PathVariable("key") String key) {
        if (!Constants.validServerKey(key)) {
            return null;
        }

        String inventoryA = request.getParameter("inventorty-a");
        String inventoryB = request.getParameter("inventorty-b");
        String matchId = request.getParameter("match-id");

        Inventory inventory = new Inventory();
        inventory.setMatchId(matchId);
        inventory.setWinnerInventory(inventoryA);
        inventory.setLoserInventory(inventoryB);

        this.inventoryRepository.save(inventory);

        return Constants.SUCCESS_STRING;
    }

    @RequestMapping("/insert/match")
    public ResponseEntity<String> insertMatchData(HttpServletRequest request,
                                                  @PathVariable("key") String key) {
        if (!Constants.validServerKey(key)) {
            return null;
        }

        String id = request.getParameter("match-id");

        int winner = Integer.parseInt(request.getParameter("winners"));
        int loser = Integer.parseInt(request.getParameter("losers"));

        int winnerEloBefore = Integer.parseInt(request.getParameter("winner-elo-before"));
        int loserEloBefore = Integer.parseInt(request.getParameter("loser-elo-before"));

        int winnerEloAfter = Integer.parseInt(request.getParameter("winner-elo-after"));
        int loserEloAfter = Integer.parseInt(request.getParameter("loser-elo-after"));

        int inventory = Integer.parseInt(request.getParameter("inventory"));

        Match match = new Match(id, inventory, winner, loser,
                winnerEloBefore, loserEloBefore,
                winnerEloAfter, loserEloAfter);

        this.matchesRepository.save(match);

        return Constants.SUCCESS_STRING;
    }

}

