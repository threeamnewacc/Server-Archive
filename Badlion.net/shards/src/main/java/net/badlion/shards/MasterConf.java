package net.badlion.shards;

import net.badlion.shards.type.Border;

import java.util.HashMap;
import java.util.Map;

public class MasterConf {


    /**
     * Key is server name, border is the border for that server.
     */
    private Map<String, Border> shardBorderMap = new HashMap<>();

    public Map<String, Border> getShardBorderMap() {
        return shardBorderMap;
    }

    public void generateDefaults(int midRadius, int worldRadius) {
        // MIDDLE
        this.shardBorderMap.put("test1", new Border(midRadius, midRadius, -midRadius, -midRadius));

        /*

        This is how the default shards are setup, 1 shard in the middle, and 4 surrounding it.

        # = test2 region
        @ = test5 region
        M = test1 region (middle)
        $ = test4 region
        & = test3 region

        ###@@@@@@
        ###@@@@@@
        ###MMM$$$
        ###MMM$$$
        ###MMM$$$
        &&&&&&$$$
        &&&&&&$$$

         */

        this.shardBorderMap.put("test2", new Border(-midRadius, -midRadius, -worldRadius, worldRadius));

        this.shardBorderMap.put("test3", new Border(midRadius, -midRadius, -worldRadius, -worldRadius));

        this.shardBorderMap.put("test4", new Border(midRadius, midRadius, worldRadius, -worldRadius));

        this.shardBorderMap.put("test5", new Border(-midRadius, midRadius, worldRadius, worldRadius));
    }
}
