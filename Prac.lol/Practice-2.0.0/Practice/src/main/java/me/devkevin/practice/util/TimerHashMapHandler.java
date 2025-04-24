package me.devkevin.practice.util;

/**
 * @author DevKevin (devkevinggg@gmail.com)
 * 12/03/2023 @ 1:05
 * TimerHashMapHandler / me.devkevin.practice.util / Practice
 */
public interface TimerHashMapHandler<E> {

    public void onExpire(E var1);

    public long getTimestamp(E var1);
}
