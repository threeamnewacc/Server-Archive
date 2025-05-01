package net.badlion.arenacommon.kits;

public class KitType {

    private String kitName;
    private String tag;

    public KitType(String kitName, String tag) {
        this.kitName = kitName;
        this.tag = tag;
    }

    public String getKitName() {
        return kitName;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof KitType) {
            return ((KitType) obj).getKitName().equals(this.kitName) && ((KitType) obj).getTag().equals(this.tag);
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 17;
        hash = hash * 31 + this.kitName.hashCode();
        hash = hash * 31 + this.tag.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return this.kitName + " " + this.tag;
    }

}
