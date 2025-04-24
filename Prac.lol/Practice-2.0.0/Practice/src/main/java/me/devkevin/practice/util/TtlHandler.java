package me.devkevin.practice.util;

/**
 * Copyright 29/04/2020 Kevin Acaymo
 * Use and or redistribution of compiled JAR file and or source code is permitted only if given
 * explicit permission from original author: Kevin Acaymo
 */
public interface TtlHandler<E> {

    void onExpire(E p0);
    long getTimestamp(E p0);
}
