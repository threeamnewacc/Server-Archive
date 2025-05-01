package net.badlion.shards;

import java.util.List;

public class ShardRequest {

    private String type;
    private List<String> args;

    public ShardRequest(String type, List<String> args) {
        this.type = type;
        this.args = args;
    }

    public String getType() {
        return type;
    }

    public List<String> getArgs() {
        return args;
    }
}
