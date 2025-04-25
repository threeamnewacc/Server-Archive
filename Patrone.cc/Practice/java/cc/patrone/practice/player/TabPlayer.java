package cc.patrone.practice.player;

import cc.patrone.core.group.Group;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TabPlayer {

    private String name;
    private Group group;
    private int weight;
}
