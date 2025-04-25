package us.zonix.api.util;

import lombok.Getter;
import us.zonix.api.Application;
import us.zonix.api.model.Player;
import us.zonix.api.repository.PlayerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Search {

    @Getter private List<String> usernames = new ArrayList<>();

    private PlayerRepository repository;

    public Search() {
        this.repository = Application.getInstance().getPlayerRepository();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                List<String> newUsernames = new ArrayList<>();

                for (Player player : repository.findAll()) {
                    if (player.getName() != null) {
                        newUsernames.add(player.getName());
                    }
                }

                usernames = newUsernames;
            }
        };

        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(task, 0, 1000*60*10);
    }

}
