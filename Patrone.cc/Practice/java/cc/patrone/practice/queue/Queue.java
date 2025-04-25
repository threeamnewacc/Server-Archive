package cc.patrone.practice.queue;

import cc.patrone.practice.kit.Kit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class Queue {

    private Kit kit;
    private boolean ranked;

    @Setter
    private int minElo;

    @Setter
    private int maxElo;

}
