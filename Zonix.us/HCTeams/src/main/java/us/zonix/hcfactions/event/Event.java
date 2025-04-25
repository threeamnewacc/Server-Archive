package us.zonix.hcfactions.event;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface Event {

    String getName();
    List<String> getScoreboardText();
    boolean isActive();

}
