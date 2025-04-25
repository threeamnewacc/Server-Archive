package us.zonix.practice.arena;

import us.zonix.practice.CustomLocation;

public class StandaloneArena {
    private CustomLocation a;
    private CustomLocation b;
    private CustomLocation min;
    private CustomLocation max;

    public CustomLocation getA() {
        return this.a;
    }

    public CustomLocation getB() {
        return this.b;
    }

    public CustomLocation getMin() {
        return this.min;
    }

    public CustomLocation getMax() {
        return this.max;
    }

    public void setA(CustomLocation a) {
        this.a = a;
    }

    public void setB(CustomLocation b) {
        this.b = b;
    }

    public void setMin(CustomLocation min) {
        this.min = min;
    }

    public void setMax(CustomLocation max) {
        this.max = max;
    }

    public StandaloneArena(CustomLocation a, CustomLocation b, CustomLocation min, CustomLocation max) {
        this.a = a;
        this.b = b;
        this.min = min;
        this.max = max;
    }

    public StandaloneArena() {
    }
}
