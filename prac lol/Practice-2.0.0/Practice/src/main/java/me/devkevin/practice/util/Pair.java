package me.devkevin.practice.util;

/**
 * Copyright 23/05/2021 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public final class Pair<FIRST, SECOND> {
    public final FIRST first;


    public final SECOND second;


    public Pair(FIRST first, SECOND second) {
        this.first = first;
        this.second = second;
    }

    public int hashCode() {
        return 17 * (first != null ? first.hashCode() : 0) + 17 * (second != null ? second.hashCode() : 0);
    }


    public boolean equals(Object o) {
        if (!(o instanceof Pair)) {
            return false;
        }

        Pair<?, ?> that = (Pair) o;
        return (equal(first, first)) && (equal(second, second));
    }

    private static boolean equal(Object a, Object b) {
        return (a == b) || ((a != null) && (a.equals(b)));
    }

    public String toString() {
        return String.format("{%s,%s}", new Object[]{first, second});
    }
}

