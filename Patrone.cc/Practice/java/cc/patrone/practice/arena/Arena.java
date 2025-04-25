package cc.patrone.practice.arena;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@AllArgsConstructor
public class Arena {

    private String name;

    @Setter
    private Location spawn1;

    @Setter
    private Location spawn2;

    @Setter
    private boolean sumo;

}
