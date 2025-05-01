package net.badlion.shards.type;

public class EnchantType {
    private final String type;
    private final int tier;

    public EnchantType(String type, int tier){
        this.type = type;
        this.tier = tier;
    }

    public String getType() {
        return type;
    }

    public int getTier() {
        return tier;
    }
}
